package android.test.googlecalendarsync.persentation

import android.test.googlecalendarsync.R
import android.test.googlecalendarsync.persentation.common.BindingActivity
import android.test.googlecalendarsync.databinding.ActivityMainBinding
import android.view.LayoutInflater
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ReportFragment.Companion.reportFragment
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController

class MainActivity : BindingActivity<ActivityMainBinding>() {
    lateinit var navController: NavController
    lateinit var appBarConfiguration: AppBarConfiguration
    override val bindingInflater: (LayoutInflater) -> ActivityMainBinding
        get() = ActivityMainBinding::inflate

    override fun setupView(binding: ActivityMainBinding) {

    }

}