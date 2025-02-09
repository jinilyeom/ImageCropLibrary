package com.image.crop

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log

object Utils {
    private const val TAG = "Utils"

    fun getBitmapFromUri(context: Context, uri: Uri?): Bitmap? {
        if (uri == null) {
            return null
        }

        try {
            val rawFileDescriptor = context.contentResolver.openFileDescriptor(uri, "r")

            return rawFileDescriptor.use { pfd ->
                BitmapFactory.decodeFileDescriptor(pfd?.fileDescriptor)
            }
        } catch (ex: Exception) {
            Log.e(TAG, "Get bitmap Exception")
        }

        return null
    }
}