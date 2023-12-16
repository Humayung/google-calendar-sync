package android.test.googlecalendarsync.data.local

import android.test.googlecalendarsync.data.local.entity.ColorEntity
import android.test.googlecalendarsync.data.local.entity.EventEntity
import android.test.googlecalendarsync.data.local.entity.LastSynchronizedDateEntity
import androidx.room.Database
import androidx.room.RoomDatabase


@Database(
    entities = [EventEntity::class, ColorEntity::class, LastSynchronizedDateEntity::class],
    version = 1
)
abstract class EventDatabase: RoomDatabase() {
    abstract val dao: EventDao
}