package com.vaibhav.gallery.model

import android.net.Uri
import android.os.Build
import android.provider.MediaStore

object Constant{
    val uri: Uri =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
        } else {
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        }
}