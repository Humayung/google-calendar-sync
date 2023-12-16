package android.test.googlecalendarsync.domain

enum class Status {
    SUCCESS,
    ERROR,
    LOADING
}

sealed class Resource<T>(
    val data: T?,
    val exception: Exception? = null,
    val status: Status
) {
    class Success<T>(data: T?) : Resource<T>(data, null, Status.SUCCESS)
    class Error<T>(data: T?, exception: Exception?) : Resource<T>(data, exception, Status.ERROR)
    class Loading<T>(data: T? = null) : Resource<T>(data, null, Status.LOADING)
}
