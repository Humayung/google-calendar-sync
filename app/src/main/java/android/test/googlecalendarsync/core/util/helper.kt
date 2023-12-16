package android.test.googlecalendarsync.core.util

import android.content.res.Resources
import android.util.Log
import android.util.TypedValue
import android.view.View
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt

fun <R> CoroutineScope.executeAsyncTask(
    onStart: () -> Unit,
    doInBackground: () -> R,
    onPostExecute: (R) -> Unit,
    onCancelled: () -> Unit
) = launch {
    onStart()
    val result = withContext(Dispatchers.IO) {
        doInBackground() // runs in background thread without blocking the Main Thread
    }
    onPostExecute(result) // runs in Main Thread
    onCancelled()
}

fun <T> T.log(title: String = "Log"): T {
    Log.d("LOGGGGG", "$title: $this")
    return this
}

fun <T, V> T.log(title: String = "Log", block: (T) -> V): T {
    Log.d("LOGGGGG", "$title: ${block.invoke(this)}")
    return this
}

fun log(title: String = "Log", vararg values: Any) {
    Log.d("LOGGGGG", "$title: ${values.joinToString()}")
}

fun Number.dpToPx(): Int {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this.toFloat(),
        Resources.getSystem().displayMetrics
    ).roundToInt()
}


object DateTimeFormatter {
    val rfc3339 = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    val yMd: DateTimeFormatter = DateTimeFormatter.ofPattern("y-M-d")
    val EdMMMyyyy: DateTimeFormatter = DateTimeFormatter.ofPattern("E, d MMMM yyyy")
    val EdMMMyyyyHHmm: DateTimeFormatter = DateTimeFormatter.ofPattern("E, d MMMM yyyy 'at' HH:mm:s")
    val HHmm = DateTimeFormatter.ofPattern("HH:mm")
    val Hmm: DateTimeFormatter = DateTimeFormatter.ofPattern("H:mm")
}

val <T>T.list: List<T>
    get() = listOf(this)

fun View.setDisabled() {
    this.alpha = 0.5f
    this.isEnabled = false
}

fun View.setEnabled() {
    this.alpha = 1f
    this.isEnabled = true
}

fun parseMonth(monthIndex: Int): String {
    return when (monthIndex) {
        0 -> "January"
        1 -> "February"
        2 -> "March"
        3 -> "April"
        4 -> "May"
        5 -> "June"
        6 -> "July"
        7 -> "August"
        8 -> "September"
        9 -> "October"
        10 -> "November"
        11 -> "December"
        else -> "Invalid Month: $monthIndex"
    }
}