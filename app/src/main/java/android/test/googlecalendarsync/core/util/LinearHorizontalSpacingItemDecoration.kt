package android.test.googlecalendarsync.core.util

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

/**
 * A custom RecyclerView.ItemDecoration for adding horizontal spacing to items within a linear layout.
 *
 * @param top The top spacing applied to all items.
 * @param bottom The bottom spacing applied to all items.
 * @param left The left spacing applied to all items.
 * @param right The right spacing applied to all items.
 * @param leftMost The additional left spacing applied to the leftmost item. (Optional)
 * @param rightMost The additional right spacing applied to the rightmost item. (Optional)
 */
class LinearHorizontalSpacingItemDecoration(
    val top: Int = 0,
    val bottom: Int = 0,
    val left: Int = 0,
    val right: Int = 0,
    val leftMost: Int? = null,
    val rightMost: Int? = null,
) : RecyclerView.ItemDecoration() {
    /**
     * Applies spacing to the given item view based on its position within the RecyclerView.
     *
     * @param outRect The Rect object to define the spacing for the item view.
     * @param view The current item view being decorated.
     * @param parent The parent RecyclerView.
     * @param state The current RecyclerView.State.
     */
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        val adapter = parent.adapter
        adapter?.let {
            val position = parent.getChildLayoutPosition(view)
            outRect.left = left
            outRect.right = right
            outRect.top = top
            outRect.bottom = bottom
            if (position == adapter.itemCount - 1) {
                rightMost?.let { outRect.right = it }
            }
            if (position == 0) {
                leftMost?.let { outRect.left = it }
            }
        }
    }
}
