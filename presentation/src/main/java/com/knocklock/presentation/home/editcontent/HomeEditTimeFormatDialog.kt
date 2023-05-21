package com.knocklock.presentation.home.editcontent

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.holix.android.bottomsheetdialog.compose.BottomSheetDialog
import com.knocklock.presentation.extenstions.noRippleClickable
import com.knocklock.presentation.home.HomeViewModel
import com.knocklock.presentation.widget.BottomSheetHeaderBar
import com.knocklock.presentation.widget.ClockWidget
import androidx.lifecycle.viewmodel.compose.viewModel
import com.knocklock.domain.model.TimeFormat

@Composable
fun HomeEditTimeFormatDialog(
    modifier: Modifier = Modifier,
    selectedTimeFormat: TimeFormat? = null,
    clickListener: (format: TimeFormat) -> Unit = {},
    onDismiss: () -> Unit = {}
) {
    BottomSheetDialog(onDismissRequest = onDismiss) {
        Column(
            modifier = modifier
                .background(color = Color.DarkGray)
                .padding(8.dp)
        ) {
            BottomSheetHeaderBar(
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Column(
                modifier = Modifier.fillMaxWidth().padding(vertical = 20.dp, horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                TimeFormat.values().forEach { timeFormat ->
                    key(timeFormat) {
                        TimeFormatItem(
                            modifier = Modifier.fillMaxWidth(),
                            format = timeFormat,
                            isSelected = timeFormat == selectedTimeFormat,
                            clickListener = { timeFormat ->
                                clickListener(timeFormat)
                                onDismiss()
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TimeFormatItem(
    format: TimeFormat,
    modifier: Modifier = Modifier,
    isSelected: Boolean = false,
    clickListener: (format: TimeFormat) -> Unit = {},
) {
    Box(
        modifier = modifier
            .run {
                border(
                    width =  if (isSelected) 4.dp else 1.dp,
                    color = if (isSelected) Color.White else Color.LightGray.copy(alpha = 0.3f),
                    shape = RoundedCornerShape(10.dp)
                )
            }
            .padding(16.dp)
            .noRippleClickable {
                clickListener(format)
            }
    ) {
        ClockWidget(
            modifier = Modifier.fillMaxWidth().align(Alignment.Center),
            timeFormat = format
        )
    }
}