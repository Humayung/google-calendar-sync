package android.test.googlecalendarsync.data.local.entity

import android.test.googlecalendarsync.data.model.EventDateTimeModel
import android.test.googlecalendarsync.data.model.EventModel
import android.test.googlecalendarsync.data.remote.util.JsonParser
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.google.gson.reflect.TypeToken
import java.time.Instant
import java.time.ZoneId

@Entity
data class EventEntity(
    @PrimaryKey val id: String,
    val description: String?,
    val start: Long,
    val end: Long,
    val title: String?,
    val color: String?
) {
    fun toEventModel(): EventModel {
        return EventModel(
            description = this.description,
            start = Instant.ofEpochMilli(this.start).atZone(ZoneId.systemDefault())
                .toLocalDateTime(),
            end = Instant.ofEpochMilli(this.end).atZone(ZoneId.systemDefault()).toLocalDateTime(),
            title = this.title,
            colorId = this.color
        )
    }
}


@ProvidedTypeConverter
class Converters(private val jsonParser: JsonParser) {
    @TypeConverter
    fun fromEventDateTime(json: String): EventDateTimeModel? {
        return jsonParser.fromJson<EventDateTimeModel>(
            json,
            object : TypeToken<EventDateTimeModel>() {}.type
        )
    }

    @TypeConverter
    fun toEventDateTimeJson(eventDateTime: EventDateTimeModel): String? {
        return jsonParser.toJson(eventDateTime, object : TypeToken<EventDateTimeModel>() {}.type)
    }
}

