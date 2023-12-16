package android.test.googlecalendarsync.data.local

import android.test.googlecalendarsync.data.local.entity.ColorEntity
import android.test.googlecalendarsync.data.local.entity.EventEntity
import android.test.googlecalendarsync.data.local.entity.LastSynchronizedDateEntity
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface EventDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvents(events: List<EventEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertColors(colors: List<ColorEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLastSynchronized(lastSynchronized: LastSynchronizedDateEntity)

    @Query("DELETE FROM EventEntity WHERE id IN(:ids)")
    suspend fun deleteEvents(ids: List<String>)

    @Query("DELETE FROM ColorEntity WHERE 1")
    suspend fun deleteAllColors()

    @Query("SELECT * FROM ColorEntity WHERE 1")
    suspend fun getAllColors(): List<ColorEntity>

    @Query("SELECT * FROM EventEntity WHERE start >= :dateStartMillis AND `end` < :dateEndMillis")
    suspend fun getEvents(dateStartMillis: Long?, dateEndMillis: Long?): List<EventEntity>

    @Query("DELETE FROM EventEntity WHERE start >= :dateStartMillis AND `end` < :dateEndMillis")
    suspend fun deleteEventsRange(dateStartMillis: Long?, dateEndMillis: Long?)

    @Query("SELECT * FROM LastSynchronizedDateEntity WHERE forDay == :day")
    suspend fun getLastSynchronized(day: Long?): LastSynchronizedDateEntity?


}