package android.test.googlecalendarsync.persentation.homePage


import android.Manifest
import android.accounts.AccountManager
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.test.googlecalendarsync.R
import android.test.googlecalendarsync.core.util.AppUtils
import android.test.googlecalendarsync.core.util.Constants
import android.test.googlecalendarsync.core.util.DatePickerDialog
import android.test.googlecalendarsync.core.util.DateTimeFormatter
import android.test.googlecalendarsync.core.util.LinearHorizontalSpacingItemDecoration
import android.test.googlecalendarsync.core.util.LinearVerticalSpacingItemDecoration
import android.test.googlecalendarsync.core.util.ScrollDetectorListener
import android.test.googlecalendarsync.core.util.dpToPx
import android.test.googlecalendarsync.core.util.log
import android.test.googlecalendarsync.core.util.parseMonth
import android.test.googlecalendarsync.data.model.ColorModel
import android.test.googlecalendarsync.data.model.EventModel
import android.test.googlecalendarsync.data.model.EventModelSync
import android.test.googlecalendarsync.databinding.FragmentMainBinding
import android.test.googlecalendarsync.domain.ItemDate
import android.test.googlecalendarsync.domain.Resource
import android.test.googlecalendarsync.persentation.common.BindingFragment
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException
import com.google.api.client.util.DateTime
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Date


class MainFragment : BindingFragment<FragmentMainBinding>(), KoinComponent {
    val accountCredential: GoogleAccountCredential by inject()
    private val mainViewModel by inject<MainViewModel>()
    lateinit var calendarAdapter: CalendarListAdapter
    private val requestAuthLauncher: ActivityResultLauncher<Intent> =
        createActivityLauncher(::onRequestAuthResult)
    val requestAccountPicker: ActivityResultLauncher<Intent> =
        createActivityLauncher(::onRequestAccountPicker)
    private var eventListAdapter: EventListAdapter = EventListAdapter()
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentMainBinding
        get() = FragmentMainBinding::inflate

    override fun setupView(binding: FragmentMainBinding, savedInstanceState: Bundle?) {
        mainViewModel.eventResponse.onEach { res ->
            consumeGetEventsResource(res)
            setTaskStatus(res)
        }.launchIn(lifecycleScope)
        mainViewModel.currentSelectedDate.onEach {
            binding.collapsingToolbar.title = it.format(DateTimeFormatter.EdMMMyyyy)
            syncCalendar()
        }.launchIn(lifecycleScope)
        prepareCalendarApi()
        eventListAdapter.setNavigator(object : EventListAdapter.Navigator {
            override fun onItemClicked(item: EventModel) = Unit
            override fun getColor(key: String?, item: EventModel): ColorModel? {

                return mainViewModel.getColorsResponse.value?.data?.firstOrNull { it.key == key }
            }
        })
        binding.toolbar.setOnClickListener {
            it.log("sdfkjdshfkdshfkjdshfhdsjf")
            binding.drawer.open()
        }
        binding.navigationView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.menu_email -> {
                    Intent(Intent.ACTION_SENDTO).apply {
                        data = Uri.parse("mailto:humayungmirza@gmail.com")
                    }.apply {
                        activity.startActivity(this)
                    }
                }

                R.id.menu_github -> {
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://github.com/Humayung")
                    ).apply {
                        activity.startActivity(this)
                    }
                }
            }

            false
        }
        binding.rvEvent.adapter = eventListAdapter
        binding.rvDate.addItemDecoration(
            LinearHorizontalSpacingItemDecoration(
                right = 8.dpToPx(),
                leftMost = 16.dpToPx(),
                rightMost = 16.dpToPx()
            )
        )
        binding.rvEvent.addItemDecoration(
            LinearVerticalSpacingItemDecoration(
                bottom = 16.dpToPx(),
                topMost = 16.dpToPx(),
                bottomMost = 80.dpToPx()
            )
        )

        binding.swipeRefresh.setOnRefreshListener {
            syncCalendar(fromCache = false)
        }
        binding.tvTitle.setOnClickListener {
            DatePickerDialog.showPicker(
                childFragmentManager,
                calendarAdapter.selectedItem?.date,
                onSelected = { dialog, selectedDate ->
                    setupCalendar(selectedDate)
                    dialog.dismiss()
                })
        }
        binding.btnAddEvent.setOnClickListener {
            it.log("dsfkjdshfkjdshfkdsfkdsf")
            navController.navigate(MainFragmentDirections.actionGetEventFragmentToAddEventFragment())
        }
        binding.rvEvent.addOnScrollListener(object : ScrollDetectorListener(lifecycleScope) {
            override fun onScrollStopped(): Unit = binding.btnAddEvent.extend()
            override fun onScrollStart(): Unit = binding.btnAddEvent.shrink()
        })
        setupCalendar(mainViewModel.currentSelectedDate.value)
    }


    fun syncCalendar(fromCache: Boolean = true) {
        mainViewModel.getEventsForDate(
            mainViewModel.currentSelectedDate.value,
            "primary",
            fromCache
        )
    }

    private fun updateEventsListColors(colors: List<ColorModel>) {
        eventListAdapter.data.onEachIndexed { index, eventModel ->
            eventModel.colorId?.let {
                eventListAdapter.notifyItemChanged(index)
            }
        }
    }


    private fun consumeGetEventsResource(res: Resource<EventModelSync>) {
        when (res) {
            is Resource.Error -> {
                binding.swipeRefresh.isRefreshing = false
                handleResponseError(res.exception)
            }

            is Resource.Loading -> {
                populateEventList(res.data)
            }

            is Resource.Success -> {
                binding.swipeRefresh.isRefreshing = false
                populateEventList(res.data)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun populateEventList(events: EventModelSync?) {
        events?.lastSync?.let {
            val instant = Instant.ofEpochMilli(it.lastSynchronizedMillis)
            val lastSync = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
            binding.tvLastSync.text = "Last sync ${lastSync.format(DateTimeFormatter.EdMMMyyyyHHmm)}"
        } ?: run {
            binding.tvLastSync.text = "Last sync: Never"
        }
        eventListAdapter.apply {
            clear()
            addAll(events?.eventModel.orEmpty())
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setupCalendar(date: LocalDate) {
        val currentYear = date.year
        val currentMonthIndex = date.monthValue - 1
        val currentDateIndex = date.dayOfMonth - 1

        calendarAdapter = CalendarListAdapter()
        val days = date.lengthOfMonth()
        repeat(days) { index ->
            val dateIndex = date.withDayOfMonth(index + 1)
            calendarAdapter.add(ItemDate(dateIndex))
        }
        mainViewModel.currentSelectedDate.value = date
        val monthName = parseMonth(currentMonthIndex)
        binding.tvTitle.text = "$monthName, $currentYear"

        calendarAdapter.setSelected(currentDateIndex)
        calendarAdapter.setNavigator(object : CalendarListAdapter.CalendarListNavigator {
            override fun onItemClicked(index: Int, itemDate: ItemDate) {
                calendarAdapter.setSelected(index)
                mainViewModel.currentSelectedDate.value = itemDate.date
            }
        })
        binding.rvDate.adapter = calendarAdapter
        val layoutManager = (binding.rvDate.layoutManager as LinearLayoutManager)
        layoutManager.scrollToPositionWithOffset(currentDateIndex, 16.dpToPx())
    }

    private fun setTaskStatus(res: Resource<EventModelSync>) {
        binding.layoutNoEvent.isVisible = binding.rvEvent.adapter?.itemCount == 0
        when (res) {
            is Resource.Error -> {
                binding.swipeRefresh.isRefreshing = false
                binding.layoutErrorLoadingEvent.isVisible = true
            }

            is Resource.Loading -> {
                binding.swipeRefresh.isRefreshing = true
                binding.layoutErrorLoadingEvent.isVisible = false
            }

            is Resource.Success -> {
                binding.swipeRefresh.isRefreshing = false
                binding.layoutErrorLoadingEvent.isVisible = false
            }
        }
    }

    private fun handleResponseError(ex: Exception?) {
        ex ?: return
        when (ex) {
            is GooglePlayServicesAvailabilityIOException -> {
                showGooglePlayServicesAvailabilityErrorDialog(ex.connectionStatusCode)
            }

            is UserRecoverableAuthIOException -> {
                ex.intent?.let { intent -> requestAuthLauncher.launch(intent) }
            }
        }

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            Constants.REQUEST_GOOGLE_PLAY_SERVICES -> {
                if (resultCode != Activity.RESULT_OK) return
                prepareCalendarApi()
            }
        }
    }

    private fun onRequestAccountPicker(result: ActivityResult) {
        if (result.resultCode != Activity.RESULT_OK) return
        result.data?.let { data ->
            data.extras ?: return
            val accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME)
            if (accountName != null) {
                val settings = this.activity.getPreferences(Context.MODE_PRIVATE)
                val editor = settings?.edit()
                editor?.putString(Constants.PREF_ACCOUNT_NAME, accountName)
                editor?.apply()
                accountCredential.selectedAccountName = accountName
                prepareCalendarApi()
            }
        }
    }

    private fun onRequestAuthResult(result: ActivityResult) {
        if (result.resultCode == Activity.RESULT_OK) {
            prepareCalendarApi()
        }
    }

    private fun prepareCalendarApi() {
        if (!isGooglePlayServicesAvailable()) return acquireGooglePlayServices()
        if (accountCredential.selectedAccountName == null) return chooseAccount()
        mainViewModel.getColors()
        syncCalendar()
    }

    private fun chooseAccount() {
        Dexter
            .withContext(activity)
            .withPermission(Manifest.permission.GET_ACCOUNTS)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(p0: PermissionGrantedResponse?) {
                    val accountName = activity.getPreferences(Context.MODE_PRIVATE)
                        ?.getString(Constants.PREF_ACCOUNT_NAME, null)
                    if (accountName != null) {
                        accountCredential.selectedAccountName = accountName
                        prepareCalendarApi()
                    } else {
                        requestAccountPicker.launch(accountCredential.newChooseAccountIntent())
                    }
                }

                override fun onPermissionDenied(p0: PermissionDeniedResponse?) {
                    AppUtils.toast(
                        activity,
                        "Permission required! Enable permission in the settings",
                    )
                }

                override fun onPermissionRationaleShouldBeShown(
                    p0: PermissionRequest?,
                    p1: PermissionToken?
                ) {
                    p1?.continuePermissionRequest()
                }

            }).check()
    }

    private fun isGooglePlayServicesAvailable(): Boolean {
        val apiAvailability = GoogleApiAvailability.getInstance()
        val connectionStatusCode = apiAvailability.isGooglePlayServicesAvailable(requireContext())
        return connectionStatusCode == ConnectionResult.SUCCESS
    }

    private fun acquireGooglePlayServices() {
        val apiAvailability = GoogleApiAvailability.getInstance()
        val connectionStatusCode = apiAvailability.isGooglePlayServicesAvailable(requireContext())
        if (apiAvailability.isUserResolvableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode)
        }
    }

    private fun showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode: Int) {
        val apiAvailability = GoogleApiAvailability.getInstance()
        val dialog = apiAvailability.getErrorDialog(
            this,
            connectionStatusCode,
            Constants.REQUEST_GOOGLE_PLAY_SERVICES
        )
        dialog?.show()
    }


}