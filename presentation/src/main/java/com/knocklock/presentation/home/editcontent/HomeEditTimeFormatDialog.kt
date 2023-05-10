package com.knocklock.presentation.home.editcontent

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
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
import com.knocklock.presentation.util.TimeFormat
import com.knocklock.presentation.util.TimeVerticalFormat
import com.knocklock.presentation.util.TimeWithMeridiem
import com.knocklock.presentation.util.TimeWithNoSecondFormat
import com.knocklock.presentation.util.TimeWithSecondFormat

@Composable
fun HomeEditTimeFormatDialog(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = viewModel(),
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
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                TimeFormatItem(
                    modifier = Modifier.fillMaxWidth(),
                    format = TimeWithNoSecondFormat,
                    clickListener = viewModel::setTimeFormat
                )
                TimeFormatItem(
                    modifier = Modifier.fillMaxWidth(),
                    format = TimeWithSecondFormat,
                    clickListener = viewModel::setTimeFormat
                )
                TimeFormatItem(
                    modifier = Modifier.fillMaxWidth(),
                    format = TimeVerticalFormat,
                    clickListener = viewModel::setTimeFormat
                )
                TimeFormatItem(
                    modifier = Modifier.fillMaxWidth(),
                    format = TimeWithMeridiem,
                    clickListener = viewModel::setTimeFormat
                )
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
            .padding(16.dp)
            .noRippleClickable {
                clickListener(format)
            }
            .run {
                if (isSelected) {
                    border(
                        width = 2.dp,
                        color = Color.White,
                        shape = RoundedCornerShape(10.dp)
                    )
                } else {
                    this
                }
            }
    ) {
        ClockWidget(timeFormat = format)
    }
}