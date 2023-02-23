package com.knocklock.presentation.util

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore


@Suppress("DEPRECATION", "NewApi")
fun Uri.parseBitmap(context: Context): Bitmap {
    context.grantUriPermission(context.packageName, this, Intent.FLAG_GRANT_READ_URI_PERMISSION)
    val resolver = context.contentResolver.apply {
        takePersistableUriPermission(this@parseBitmap, Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    return when (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) { // 28
        true -> {
            val source = ImageDecoder.createSource(resolver, this)
            ImageDecoder.decodeBitmap(source)
        }
        else -> {
            MediaStore.Images.Media.getBitmap(resolver, this)
        }
    }
}
