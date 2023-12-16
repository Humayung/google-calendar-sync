package android.test.googlecalendarsync.data.model

import android.test.googlecalendarsync.core.util.DateTimeFormatter
import android.test.googlecalendarsync.core.util.log
import android.test.googlecalendarsync.data.local.entity.EventEntity
import com.google.api.client.util.DateTime
import com.google.api.services.calendar.model.Event
import com.google.api.services.calendar.model.EventDateTime
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime

data class EventModel(
    val description: String?,
    val start: LocalDateTime,
    val end: LocalDateTime,
    val title: String?,
    val colorId: String?
) {
    fun toEvent(): Event {
        return Event().apply {
            description = this@EventModel.description
            start = this@EventModel.start.toEventDateTime()
            end = this@EventModel.end.toEventDateTime()
            summary = this@EventModel.title
            colorId = this@EventModel.colorId
        }
    }
}

fun LocalDateTime.toEventDateTime(): EventDateTime {
    return EventDateTime().also { it.dateTime = this.toDateTime() }
}

fun DateTime.dateToLocalDateTime(): LocalDateTime {
    return LocalDate.parse(toStringRfc3339(), DateTimeFormatter.yMd).atStartOfDay()
}


fun Event.toEventEntity(): EventEntity {
    this.log("sdfsdfkjdslkfds")
    val dateStart = this.start.dateTime?.toZonedDateTime()?.toLocalDateTime()
        ?: this.start.date.dateToLocalDateTime()
    val dateEnd = this.end.dateTime?.toZonedDateTime()?.toLocalDateTime()
        ?: this.end.date.dateToLocalDateTime()
    log("sdfsdfsfdsdf", dateStart, dateEnd)
    return EventEntity(
        description = this.description,
        start = dateStart.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(),
        end = dateEnd.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(),
        title = this.summary,
        color = this.colorId,
        id = this.id
    )
}

fun DateTime.toZonedDateTime(): ZonedDateTime {
    return ZonedDateTime.parse(toStringRfc3339(), DateTimeFormatter.rfc3339)
}

fun LocalDateTime.toDateTime(): DateTime {
    val zoned = ZonedDateTime.of(this, ZoneId.systemDefault())
    return DateTime(zoned.toInstant().toEpochMilli())
}

data class EventDateTimeModel(
    val dateTime: LocalDateTime,
    val timeZone: String?
) {
    fun toEventDateTime(): EventDateTime {
        return EventDateTime().apply {
            dateTime = this@EventDateTimeModel.dateTime.toDateTime()
            timeZone = this@EventDateTimeModel.timeZone
        }
    }
}
