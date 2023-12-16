package android.test.googlecalendarsync.domain

import android.test.googlecalendarsync.data.local.entity.ColorEntity
import android.test.googlecalendarsync.data.local.entity.EventEntity
import android.test.googlecalendarsync.data.model.ColorModel
import android.test.googlecalendarsync.data.model.EventModel
import android.test.googlecalendarsync.data.model.EventModelSync
import android.test.googlecalendarsync.data.model.LastSynchronizedDateModel
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface CalendarRepository {
    suspend fun getEventForDate(
        dateTime: LocalDate,
        calendarId: String,
        fromCache: Boolean
    ): Flow<Resource<EventModelSync>>

    suspend fun getColors(): Flow<Resource<List<ColorModel>>>
    suspend fun insertEventForDate(
        calendarId: String,
        eventModel: EventModel
    ): Flow<Resource<EventModel>>
}