package com.knocklock.presentation.util

import android.graphics.Bitmap
import androidx.compose.ui.graphics.Color
import androidx.palette.graphics.Palette

val defaultGradientColors = listOf(Color.Transparent, Color.Transparent)

fun getPalette(bitmap: Bitmap): Palette {
    return Palette.from(bitmap)
        .maximumColorCount(10)
        .generate()
}

fun getGradientColors(palette: Palette): List<Color> {
    val colors = mutableListOf<Color>().apply {
        palette.lightVibrantSwatch?.rgb?.let(::addColorWithRGB)
        palette.vibrantSwatch?.rgb?.let(::addColorWithRGB)
        palette.mutedSwatch?.rgb?.let(::addColorWithRGB)
        palette.lightMutedSwatch?.rgb?.let(::addColorWithRGB)
    }

    return if (colors.size >= 2) {
        colors
    } else {
        defaultGradientColors
    }
}

private fun MutableList<Color>.addColorWithRGB(rgb: Int) {
    add(Color(rgb))
}