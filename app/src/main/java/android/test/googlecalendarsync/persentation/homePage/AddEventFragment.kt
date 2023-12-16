package android.test.googlecalendarsync.persentation.homePage

import android.os.Bundle
import android.test.googlecalendarsync.data.model.ColorModel
import android.test.googlecalendarsync.data.model.EventModel
import android.test.googlecalendarsync.databinding.FragmentAddEventBinding
import android.test.googlecalendarsync.domain.ItemColor
import android.test.googlecalendarsync.domain.Resource
import android.test.googlecalendarsync.persentation.common.BindingFragment
import android.test.googlecalendarsync.core.util.AppUtils
import android.test.googlecalendarsync.core.util.DatePickerDialog
import android.test.googlecalendarsync.core.util.DateTimeFormatter
import android.test.googlecalendarsync.core.util.InputValidation
import android.test.googlecalendarsync.core.util.InputValidationType
import android.test.googlecalendarsync.core.util.LinearHorizontalSpacingItemDecoration
import android.test.googlecalendarsync.core.util.TimePickerDialog
import android.test.googlecalendarsync.core.util.dpToPx
import android.test.googlecalendarsync.core.util.setDisabled
import android.test.googlecalendarsync.core.util.setEnabled
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.time.LocalDate
import java.time.LocalTime

class AddEventFragment : BindingFragment<FragmentAddEventBinding>(), KoinComponent {
    val mainViewModel by inject<MainViewModel>()
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentAddEventBinding
        get() = FragmentAddEventBinding::inflate

    private lateinit var colorAdapter: ColorAdapter

    override fun setupView(binding: FragmentAddEventBinding, savedInstanceState: Bundle?) {
        binding.toolbar.setOnClickListener {
            navController.navigateUp()
        }
        mainViewModel.getColorsResponse
            .filterNotNull()
            .onEach { res ->
                consumeGetColorsResponse(res)
            }.launchIn(lifecycleScope)
        binding.btnBack.setOnClickListener {
            navController.navigateUp()
        }

        binding.rvColor.addItemDecoration(
            LinearHorizontalSpacingItemDecoration(
                right = 8.dpToPx(),
                leftMost = 16.dpToPx(),
                rightMost = 16.dpToPx()
            )
        )
        binding.btnAddEvent.setOnClickListener {
            validateFields()
        }
        binding.tvDate.setOnClickListener {
            pickADate(
                setter = { binding.tvDate.text = it },
                getter = { binding.tvDate.text.toString() }
            )
        }
        binding.tvStartTime.setOnClickListener {
            pickATime(
                setter = { binding.tvStartTime.text = it },
                getter = { binding.tvStartTime.text.toString() },
                default = LocalTime.now()
            )
        }

        binding.tvEndTime.setOnClickListener {
            pickATime(
                setter = { binding.tvEndTime.text = it },
                getter = { binding.tvEndTime.text.toString() },
                default = LocalTime.now().plusHours(1)
            )
        }
        binding.tvDate.text = mainViewModel.currentSelectedDate.value.format(DateTimeFormatter.yMd)
        mainViewModel.getColors()
    }

    private fun consumeInsertEventResource(res: Resource<EventModel>) {
        when (res) {
            is Resource.Error -> {
                AppUtils.toast(activity, "Failed adding event, there is something wrong")
                AppUtils.dismissLoading()
            }

            is Resource.Loading -> AppUtils.showLoading(activity)
            is Resource.Success -> {
                AppUtils.dismissLoading()
                navController.navigateUp()
            }
        }
    }

    private fun validateFields() {
        val validator = InputValidation.Builder()
            .validateEmpty()
            .build()
        listOf(
            binding.edtTaskName to "Task name is required",
            binding.tvStartTime to "Start time is required",
            binding.tvEndTime to "End time is required",
            binding.tvDate to "Date is required",
            binding.edtDescription to "Description is required"
        ).map { (field, message) ->
            validator.validate(field.text.toString(), onNotValid = {
                field.error = message
            })
        }.also {
            val allIsValid = it.none { it != InputValidationType.FieldIsValid }
            if (!allIsValid) return
        }
        val startTime = LocalTime.parse(binding.tvStartTime.text, DateTimeFormatter.Hmm)
        val endTime = LocalTime.parse(binding.tvEndTime.text, DateTimeFormatter.Hmm)
        if (endTime.isBefore(startTime)) {
            binding.tvEndTime.error = "End time is behind start time"
            AppUtils.toast(activity, binding.tvEndTime.error.toString())
            return
        }
        val date = LocalDate.parse(binding.tvDate.text, DateTimeFormatter.yMd)
        val eventModel = EventModel(
            description = binding.edtDescription.text.toString(),
            start = LocalTime.parse(binding.tvStartTime.text, DateTimeFormatter.Hmm)
                .atDate(date),
            end = LocalTime.parse(binding.tvEndTime.text, DateTimeFormatter.Hmm)
                .atDate(date),
            title = binding.edtTaskName.text.toString(),
            colorId = colorAdapter.getSelected().color.key

        )
        lifecycleScope.launch {
            mainViewModel.insertEvent(eventModel, "primary").onEach { res ->
                consumeInsertEventResource(res)
            }.launchIn(this)
        }
    }

    private fun toggleBtnAddEventAvailability(res: Resource<List<ColorModel>>) {
        when (res) {
            is Resource.Error -> {
                binding.btnAddEvent.setDisabled()
            }

            is Resource.Loading -> {
                binding.btnAddEvent.setDisabled()
            }

            is Resource.Success -> {
                if (!res.data.isNullOrEmpty()) binding.btnAddEvent.setEnabled()
            }
        }
    }

    private fun consumeGetColorsResponse(res: Resource<List<ColorModel>>) {
        when (res) {
            is Resource.Error -> Unit
            is Resource.Loading -> populateColorsAdapter(res.data.orEmpty())
            is Resource.Success -> populateColorsAdapter(res.data.orEmpty())

        }
    }

    private fun populateColorsAdapter(colors: List<ColorModel>) {
        colorAdapter = ColorAdapter().apply {
            colors.map {
                ItemColor(isSelected = false, color = it)
            }.also {
                addAll(it)
            }
            setNavigator(ColorAdapter.Navigator { selected ->
                data.onEach { it.isSelected = selected == it }
                notifyDataSetChanged()
            })
            binding.rvColor.adapter = this
        }
    }

    private fun pickADate(setter: (String) -> Unit, getter: () -> String) {
        val currentDate = getter()
        val initialDate = if (currentDate.isNullOrBlank()) {
            null
        } else {
            LocalDate.parse(currentDate, DateTimeFormatter.yMd)
        }
        DatePickerDialog.showPicker(childFragmentManager, initialDate) { _, date ->
            setter(date.format(DateTimeFormatter.yMd))
        }
    }

    private fun pickATime(setter: (String) -> Unit, getter: () -> String, default: LocalTime) {
        val currentTime = getter()
        val initialTime = if (currentTime.isBlank()) {
            default
        } else {
            LocalTime.parse(currentTime, DateTimeFormatter.Hmm)
        }
        TimePickerDialog.showDialog(childFragmentManager, initialTime) { _, date ->
            setter(date.format(DateTimeFormatter.Hmm))
        }
    }
}