package dev.mobile.medicalink.fragments.traitements

import android.content.res.Resources
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class SpacingRecyclerView(private val espacementEnDp: Int) : RecyclerView.ItemDecoration() {
    private val espacementPx: Int = dpToPx(espacementEnDp)

    private fun dpToPx(dp: Int): Int {
        val density = Resources.getSystem().displayMetrics.density
        return (dp * density).toInt()
    }

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        outRect.bottom = espacementPx
    }
}
