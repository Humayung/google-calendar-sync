package android.test.googlecalendarsync.persentation.homePage

import android.test.googlecalendarsync.domain.CalendarRepository
import android.test.googlecalendarsync.data.model.ColorModel
import android.test.googlecalendarsync.data.model.EventModel
import android.test.googlecalendarsync.data.model.EventModelSync
import android.test.googlecalendarsync.domain.Resource
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.time.LocalDate

class MainViewModel : ViewModel(), KoinComponent {
    var job: Job? = null
    private val calendarRepository: CalendarRepository by inject()
    private val _eventsResponse: MutableStateFlow<Resource<EventModelSync>> =
        MutableStateFlow(Resource.Success(null))
    val eventResponse = _eventsResponse.asStateFlow()

    private val _getColorsResponse: MutableStateFlow<Resource<List<ColorModel>>?> =
        MutableStateFlow(null)
    val getColorsResponse = _getColorsResponse.asStateFlow()

    val currentSelectedDate: MutableStateFlow<LocalDate> = MutableStateFlow(LocalDate.now())
    fun getEventsForDate(dateTime: LocalDate, calendarId: String, fromCache: Boolean) {
        cancelJob()
        job = viewModelScope.launch {
            calendarRepository.getEventForDate(dateTime, calendarId, fromCache).also { res ->
                res.onEach {
                    _eventsResponse.emit(it)
                }.launchIn(viewModelScope)
            }
        }
    }

    suspend fun insertEvent(
        eventModel: EventModel,
        calendarId: String
    ): Flow<Resource<EventModel>> {
        return calendarRepository.insertEventForDate(calendarId, eventModel)
    }

    fun cancelJob() {
        job?.cancel()
    }

    fun getColors() {
        viewModelScope.launch {
            calendarRepository.getColors().also { res ->
                res.onEach { _getColorsResponse.emit(it) }.launchIn(this)
            }
        }
    }
}