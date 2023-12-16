package android.test.googlecalendarsync.data.local.entity

import android.test.googlecalendarsync.data.model.LastSynchronizedDateModel
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class LastSynchronizedDateEntity(
    @PrimaryKey val forDay: Long,
    val lastSynchronizedMillis: Long
){
    fun toLastSynchronisedDateModel(): LastSynchronizedDateModel {
        return LastSynchronizedDateModel(
            forDay = this.forDay,
            lastSynchronizedMillis = this.lastSynchronizedMillis
        )
    }
}