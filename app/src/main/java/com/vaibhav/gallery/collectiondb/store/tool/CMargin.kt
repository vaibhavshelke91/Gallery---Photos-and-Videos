package com.vaibhav.gallery.collectiondb.store.tool

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView


class CMargin(private val spaceSize: Int) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect, view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        with(outRect) {

            top=spaceSize

        }
    }
}