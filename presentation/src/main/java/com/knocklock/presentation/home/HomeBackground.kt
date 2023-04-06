package com.knocklock.presentation.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.core.graphics.drawable.toBitmap
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.knocklock.domain.model.LockScreenBackground
import com.knocklock.presentation.R
import com.knocklock.presentation.util.defaultGradientColors
import com.knocklock.presentation.util.getGradientColors
import com.knocklock.presentation.util.getPalette

@Composable
fun HomeBackground(
    modifier: Modifier = Modifier,
    lockScreenBackground: LockScreenBackground
) {
    val imagePainter = when (lockScreenBackground) {
        is LockScreenBackground.DefaultWallPaper -> {
            rememberAsyncImagePainter(
                ImageRequest
                    .Builder(LocalContext.current)
                    .data(R.drawable.default_wallpaper)
                    .allowHardware(false)
                    .build()
            )
        }
        is LockScreenBackground.LocalImage -> {
            rememberAsyncImagePainter(
                ImageRequest
                    .Builder(LocalContext.current)
                    .data(lockScreenBackground.imageUri)
                    .allowHardware(false)
                    .build()
            )
        }
    }

    val palette = (imagePainter.state as? AsyncImagePainter.State.Success)?.let {
        getPalette(it.result.drawable.toBitmap())
    }

    val backgroundGradientBrush = Brush.linearGradient(
        colors = palette?.let { getGradientColors(palette) } ?: defaultGradientColors,
        start = Offset.Zero,
        end = Offset.Infinite,
    )

    Box(
        modifier = modifier
    ) {
        Box(
            modifier = modifier.background(
                brush = backgroundGradientBrush,
                alpha = 0.6f
            )
        )
        Image(
            modifier = Modifier
                .fillMaxSize(0.75f)
                .align(Alignment.Center),
            painter = imagePainter,
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
        )
    }
}