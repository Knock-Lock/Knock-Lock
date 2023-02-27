package com.knocklock.presentation.lockscreen.util

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.appcompat.content.res.AppCompatResources
import com.knocklock.presentation.R

/**
 * @Created by 김현국 2023/02/27
 */

fun Uri.toBitmap(context: Context): Bitmap {
    context.grantUriPermission(context.packageName, this, Intent.FLAG_GRANT_READ_URI_PERMISSION)
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        ImageDecoder.decodeBitmap(
            ImageDecoder.createSource(context.contentResolver, this)
        ) { decoder: ImageDecoder, _: ImageDecoder.ImageInfo?, _: ImageDecoder.Source? ->
            decoder.isMutableRequired = true
            decoder.allocator = ImageDecoder.ALLOCATOR_SOFTWARE
        }
    } else {
        BitmapDrawable(
            context.resources,
            MediaStore.Images.Media.getBitmap(context.contentResolver, this)
        ).bitmap
    }
}

fun Int.toBitmap(context: Context): Bitmap {
    val drawable = AppCompatResources.getDrawable(context, this)
    val bitmapDrawable = drawable as BitmapDrawable
    return bitmapDrawable.bitmap
}
