package com.vaibhav.gallery.collectiondb.store.tool

import android.content.Context

import android.util.AttributeSet




class DynamicImageView : androidx.appcompat.widget.AppCompatImageView {
    private var whRatio = 0f

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {}
    constructor(context: Context) : super(context) {}

    fun setRatio(ratio: Float) {
        whRatio = ratio
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        if (whRatio != 0f) {
            val width = measuredWidth
            val height = (whRatio * width).toInt()
            setMeasuredDimension(width, height)
        }
    }
}