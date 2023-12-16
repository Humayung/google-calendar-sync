package android.test.googlecalendarsync.domain

import android.test.googlecalendarsync.data.model.ColorModel
import android.test.googlecalendarsync.data.model.EventModel

data class ItemEvent(
    val eventModel: EventModel,
    var color: ColorModel?
)