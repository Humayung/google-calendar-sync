package android.test.googlecalendarsync.data.model

data class EventModelSync(
    val eventModel: List<EventModel>,
    val lastSync: LastSynchronizedDateModel?
)