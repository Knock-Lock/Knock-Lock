package com.knocklock.presentation.ui.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KnockLockTopAppbar(
    title: String,
    onClickBackButton: () -> Unit,
    modifier: Modifier = Modifier
) {
    CenterAlignedTopAppBar(
        modifier = modifier
            .padding(5.dp)
            .fillMaxWidth()
            .testTag("knocklockTopAppBar"),
        title = {
            Text(
                text = title,
                modifier = Modifier.padding(start = 10.dp, bottom = 5.dp)
            )
        },
        navigationIcon = {
            IconButton(
                modifier = Modifier.size(20.dp),
                onClick = onClickBackButton
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = null,
                    tint = Color.Black
                )
            }
        }
    )
}