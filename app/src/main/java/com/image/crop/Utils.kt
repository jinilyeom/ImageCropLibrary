package com.image.crop

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import java.io.IOException

object Utils {
    private const val TAG = "Utils"

    fun getBitmapFromUri(context: Context, uri: Uri?): Bitmap? {
        if (uri == null) {
            return null
        }

        try {
            val rawFileDescriptor = context.contentResolver.openFileDescriptor(uri, "r")
            val bitmap = BitmapFactory.decodeFileDescriptor(rawFileDescriptor?.fileDescriptor)
            rawFileDescriptor?.close()

            return bitmap
        } catch (ex: IOException) {
            Log.e(TAG, "Bitmap IOException")
        }

        return null
    }
}