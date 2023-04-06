package com.knocklock.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun HomeBottomSheetContent(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .padding(top = 8.dp)
    ) {
        Spacer(
            modifier = Modifier
                .width(40.dp)
                .height(6.dp)
                .background(color = Color.LightGray, shape = RoundedCornerShape(3.dp))
                .align(CenterHorizontally)
        )
    }
}