package com.knocklock.presentation.util

import android.content.Intent
import android.provider.MediaStore

fun getGalleryIntent() = Intent(
    Intent.ACTION_GET_CONTENT,
    MediaStore.Images.Media.EXTERNAL_CONTENT_URI
).apply {
    type = "image/*"
    action = Intent.ACTION_OPEN_DOCUMENT
    putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false)
}