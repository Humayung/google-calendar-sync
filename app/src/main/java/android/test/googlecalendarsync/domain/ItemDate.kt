package android.test.googlecalendarsync.domain

import java.time.LocalDate

data class ItemDate(
    val date: LocalDate,
    var isSelected: Boolean = false
)
