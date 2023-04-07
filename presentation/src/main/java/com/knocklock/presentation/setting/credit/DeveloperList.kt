package com.knocklock.presentation.setting.credit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun DeveloperList(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(30.dp)
    ) {
        Developer.values().forEachIndexed { index, developer ->
            val delay = index * 500
            DeveloperItem(developer = developer, delay = delay)
        }
    }
}