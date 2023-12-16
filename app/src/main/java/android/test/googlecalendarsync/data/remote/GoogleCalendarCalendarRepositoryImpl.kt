package android.test.googlecalendarsync.data.remote

import android.test.googlecalendarsync.core.util.list
import android.test.googlecalendarsync.core.util.log
import android.test.googlecalendarsync.data.model.EventModel
import android.test.googlecalendarsync.data.model.toEventEntity
import android.test.googlecalendarsync.domain.CalendarRepository
import android.test.googlecalendarsync.domain.Resource
import android.test.googlecalendarsync.data.local.EventDao
import android.test.googlecalendarsync.data.local.entity.ColorEntity
import android.test.googlecalendarsync.data.local.entity.LastSynchronizedDateEntity
import android.test.googlecalendarsync.data.model.ColorModel
import android.test.googlecalendarsync.data.model.EventModelSync
import android.test.googlecalendarsync.data.model.LastSynchronizedDateModel
import com.google.api.client.util.DateTime
import com.google.api.services.calendar.Calendar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import java.time.LocalDate
import java.time.ZoneId
import kotlin.time.Duration.Companion.milliseconds

class GoogleCalendarRepositoryImpl(private val calendar: Calendar, private val dao: EventDao) :
    CalendarRepository,
    KoinComponent {
    override suspend fun getEventForDate(
        date: LocalDate,
        calendarId: String,
        fromCache: Boolean
    ): Flow<Resource<EventModelSync>> = flow {
        val startDate =
            date.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val endDate = date.plusDays(1).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()
            .toEpochMilli()
        val localEvents = dao.getEvents(startDate, endDate).map { it.toEventModel() }
        val forDay = startDate.milliseconds.inWholeDays
        val lastSync = dao.getLastSynchronized(forDay)
        val daysDifferent = lastSync?.let {
            System.currentTimeMillis().milliseconds.inWholeDays - lastSync.lastSynchronizedMillis.milliseconds.inWholeDays
        }
        val shouldSyncFromApi = daysDifferent == null || daysDifferent >= 1 || !fromCache
        shouldSyncFromApi.log("sdfdsfdsfdsfsdfdsfdsf")
        val eventModelSync = EventModelSync(localEvents, lastSync?.toLastSynchronisedDateModel())
        if (!shouldSyncFromApi) {
            emit(Resource.Success(eventModelSync))
            return@flow
        }
        emit(Resource.Loading(eventModelSync))

        try {
            val remoteEvents = withContext(Dispatchers.IO) {
                calendar
                    .events()
                    .list(calendarId)
                    .setTimeMax(DateTime(endDate))
                    .setTimeMin(DateTime(startDate))
                    .setOrderBy("startTime")
                    .setSingleEvents(true)
                    .execute()
                    .items.map {
                        it.toEventEntity()
                    }
            }
            dao.insertLastSynchronized(
                LastSynchronizedDateEntity(
                    forDay = forDay,
                    lastSynchronizedMillis = System.currentTimeMillis()
                )
            )
            dao.deleteEventsRange(startDate, endDate)
            dao.insertEvents(remoteEvents)
        } catch (e: Exception) {
            e.printStackTrace()
            emit(Resource.Error(exception = e, data = null))
            return@flow
        }
        val newEvents = dao.getEvents(startDate, endDate)
        val newSync = dao.getLastSynchronized(forDay)

        EventModelSync(
            newEvents.map { it.toEventModel() },
            newSync?.toLastSynchronisedDateModel()
        ).apply {
            emit(Resource.Success(this))
        }
    }

    override suspend fun getColors(): Flow<Resource<List<ColorModel>>> = flow {
        emit(Resource.Loading())
        val localColors = dao.getAllColors()
        emit(Resource.Loading(localColors.map { it.toColorModel() }))
        try {
            val remoteColors = withContext(Dispatchers.IO) {
                calendar
                    .colors()
                    .get()
                    .execute()
                    .let {
                        it.event.keys.zip(it.event.values).map { (key, color) ->
                            ColorEntity(
                                key = key,
                                background = color.background,
                                foreground = color.foreground
                            )
                        }
                    }
            }
            dao.deleteAllColors()
            dao.insertColors(remoteColors)
        } catch (e: Exception) {
            e.printStackTrace()
            emit(Resource.Error(exception = e, data = null))
            return@flow
        }
        val newColors = dao.getAllColors()
        emit(Resource.Success(newColors.map { it.toColorModel() }))
    }

    override suspend fun insertEventForDate(
        calendarId: String,
        eventModel: EventModel
    ): Flow<Resource<EventModel>> = flow {
        emit(Resource.Loading())
        try {
            val result = withContext(Dispatchers.IO) {
                calendar
                    .events()
                    .insert(calendarId, eventModel.toEvent())
                    .execute()
                    .toEventEntity()
            }
            dao.insertEvents(result.list)
            emit(Resource.Success(result.toEventModel()))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(Resource.Error(exception = e, data = null))
        }
    }
}
