package com.knocklock.presentation.setting.credit

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.knocklock.presentation.R

@Composable
fun DeveloperList(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(30.dp)
    ) {
        Spacer(modifier.padding(20.dp))
        DeveloperItem(
            developer = Developer(
                R.string.daq,
                R.drawable.hyunkuk,
                R.string.daq_comment
            )
        )
        DeveloperItem(
            developer = Developer(
                R.string.jaeryo,
                R.drawable.minuk,
                R.string.jaeryo_comment
            )
        )
        DeveloperItem(
            developer = Developer(
                R.string.hence,
                R.drawable.hyunsu,
                R.string.hence_comment
            )
        )

    }
}