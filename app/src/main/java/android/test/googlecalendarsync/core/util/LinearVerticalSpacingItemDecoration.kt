package android.test.googlecalendarsync.core.util

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class LinearVerticalSpacingItemDecoration(
    var top: Int = 0,
    var bottom: Int = 0,
    var left: Int = 0,
    var right: Int = 0,
    var topMost: Int? = null,
    var bottomMost: Int? = null
) : RecyclerView.ItemDecoration() {

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

            bottomMost.log("sdfdsfsdf ${adapter.itemCount} $position" )
            if (position == adapter.itemCount - 1) {
                bottomMost?.let { outRect.bottom = it }
            }
            if (position == 0) {
                topMost?.let{
                    outRect.top = topMost as Int
                    topMost?.let { outRect.top = it }
                }
            }
        }
    }
}
