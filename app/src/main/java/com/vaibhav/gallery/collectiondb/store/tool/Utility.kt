package com.vaibhav.gallery.collectiondb.store.tool

import android.graphics.ColorFilter
import android.view.View
import android.view.animation.AnimationUtils
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieProperty
import com.airbnb.lottie.SimpleColorFilter
import com.airbnb.lottie.model.KeyPath
import com.airbnb.lottie.value.LottieValueCallback
import com.vaibhav.gallery.R

fun View.makeAnimation(){
    val anim=AnimationUtils.loadAnimation(context, R.anim.bounce)
    startAnimation(anim)
}
fun LottieAnimationView.changeLayersColor(
    @ColorRes colorRes: Int
) {
    val color = ContextCompat.getColor(context, colorRes)
    val filter = SimpleColorFilter(color)
    val keyPath = KeyPath("**")
    val callback: LottieValueCallback<ColorFilter> = LottieValueCallback(filter)

    addValueCallback(keyPath, LottieProperty.COLOR_FILTER, callback)
}