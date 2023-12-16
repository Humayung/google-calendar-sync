package android.test.googlecalendarsync

import android.app.Application
import android.app.UiModeManager.MODE_NIGHT_NO
import android.test.googlecalendarsync.di.dependencies
import androidx.appcompat.app.AppCompatDelegate
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.component.KoinComponent
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class MainApp: Application(), KoinComponent {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger(Level.ERROR)
            androidContext(this@MainApp)
            modules(
                listOf(dependencies)
            )
        }

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }
}