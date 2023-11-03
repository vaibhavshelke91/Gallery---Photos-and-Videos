package com.vaibhav.gallery.collectiondb.store.tool

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class MarginItemDecoration(private val spaceSize: Int) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect, view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        with(outRect) {

            top = if (parent.getChildAdapterPosition(view) == 0 || parent.getChildAdapterPosition(view) == 1 ||
                parent.getChildAdapterPosition(view) == 2 || parent.getChildAdapterPosition(view) == 3) {
                spaceSize
            }else{
                0
            }

        }
    }
}