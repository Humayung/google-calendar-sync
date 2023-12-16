package android.test.googlecalendarsync.di

import android.app.Application
import android.content.Context
import android.test.googlecalendarsync.data.local.EventDatabase
import android.test.googlecalendarsync.data.remote.GoogleCalendarRepositoryImpl
import android.test.googlecalendarsync.domain.CalendarRepository
import androidx.room.Room
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.client.util.ExponentialBackOff
import com.google.api.services.calendar.Calendar
import com.google.api.services.calendar.CalendarScopes


fun provideAccountCredential(context: Context): GoogleAccountCredential? {
    return GoogleAccountCredential.usingOAuth2(
        context,
        arrayListOf(CalendarScopes.CALENDAR)
    ).setBackOff(ExponentialBackOff())
}

fun provideCalendar(accountCredential: GoogleAccountCredential): Calendar {
    val transport = AndroidHttp.newCompatibleTransport()
    val jsonFactory = JacksonFactory.getDefaultInstance()
    return Calendar
        .Builder(transport, jsonFactory, accountCredential)
        .setApplicationName("GetEventCalendar")
        .build()
}

fun provideCalendarRepository(
    db: EventDatabase,
    calendar: Calendar
): CalendarRepository {
    return GoogleCalendarRepositoryImpl(calendar, db.dao)
}

fun provideEventDatabase(app: Application): EventDatabase {
    return Room.databaseBuilder(
        app,
        EventDatabase::class.java,
        "events_db"
    ).build()
}