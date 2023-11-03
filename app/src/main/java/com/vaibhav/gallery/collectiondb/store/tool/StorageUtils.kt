package com.vaibhav.gallery.collectiondb.store.tool

import android.content.Context
import android.net.Uri
import com.vaibhav.gallery.model.ImageModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

import java.io.File

class StorageUtils {

    fun moveTo(context: Context,name:String):Boolean{
        return false
    }

    suspend fun delete(context: Context,model: ImageModel):Boolean{
     return false
    }

    private fun getTrashFilesDir(context: Context): File {
        val root = context.filesDir
        val trash = File(root, "trash")
        if (!trash.exists()) trash.mkdirs() else if (!trash.isDirectory && trash.canWrite()) {
            trash.delete()
            trash.mkdirs()
        }
        return trash
    }
}