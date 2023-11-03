package com.vaibhav.gallery.collectiondb.store.tool

import android.content.Context
import android.util.AttributeSet
import com.google.android.material.checkbox.MaterialCheckBox

class ShowCaseCheckBox : MaterialCheckBox {
    constructor(context: Context?) : super(context) {
        isClickable=false
    }
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        isClickable=false
    }
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        isClickable=false
    }
}