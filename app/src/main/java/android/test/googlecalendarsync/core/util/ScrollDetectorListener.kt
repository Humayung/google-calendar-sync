package android.test.googlecalendarsync.core.util

import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * An abstract RecyclerView.OnScrollListener that provides functionality to hide and show a button view
 * based on the scroll state of a RecyclerView.
 */
abstract class ScrollDetectorListener(
    private val scope: CoroutineScope,
    private val delayMillis: Long = 1000,
) : RecyclerView.OnScrollListener() {
    private var job: Job? = null

    /**
     * Called when the scroll state of the RecyclerView changes.
     *
     * @param recyclerView The RecyclerView that is being scrolled.
     * @param newState The new scroll state, which can be one of [RecyclerView.SCROLL_STATE_IDLE],
     * [RecyclerView.SCROLL_STATE_DRAGGING], or [RecyclerView.SCROLL_STATE_SETTLING].
     */
    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
        super.onScrollStateChanged(recyclerView, newState)
        when (newState) {
            RecyclerView.SCROLL_STATE_IDLE -> {
                job?.cancel()
                job = scope.launch {
                    delay(delayMillis)
                    onScrollStopped()
                }

            }

            RecyclerView.SCROLL_STATE_DRAGGING -> {
                job?.cancel()
                onScrollStart()
            }

            RecyclerView.SCROLL_STATE_SETTLING -> Unit
        }
    }


    abstract fun onScrollStopped(): Unit?
    abstract fun onScrollStart(): Unit?
}
