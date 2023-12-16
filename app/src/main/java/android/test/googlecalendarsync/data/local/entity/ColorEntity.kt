package android.test.googlecalendarsync.data.local.entity

import android.test.googlecalendarsync.data.model.ColorModel
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ColorEntity(
    @PrimaryKey val id: Int? = null,
    val foreground: String,
    val background: String,
    val key: String
) {
    fun toColorModel() = ColorModel(
        foreground = this@ColorEntity.foreground,
        background = this@ColorEntity.background,
        key = this@ColorEntity.key
    )
}