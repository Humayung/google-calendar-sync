package android.test.googlecalendarsync.di

import android.test.googlecalendarsync.domain.CalendarRepository
import android.test.googlecalendarsync.data.remote.GoogleCalendarRepositoryImpl
import android.test.googlecalendarsync.persentation.homePage.MainViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val dependencies = module {
    single { provideAccountCredential(androidContext()) }
    single { provideCalendar(get()) }
    single { provideEventDatabase(androidApplication()) }
    single { provideCalendarRepository(get(), get()) }

    single { MainViewModel() }
}