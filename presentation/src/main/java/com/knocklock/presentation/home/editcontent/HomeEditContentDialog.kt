package com.knocklock.presentation.home.editcontent

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.holix.android.bottomsheetdialog.compose.BottomSheetDialog
import com.knocklock.presentation.extenstions.noRippleClickable
import com.knocklock.presentation.widget.BottomSheetHeaderBar

@Composable
fun HomeEditContentDialog(
    modifier: Modifier = Modifier,
    clickListener: (HomeEditType) -> Unit = {},
    onDismiss: () -> Unit = {}
) {
    BottomSheetDialog(onDismissRequest = onDismiss) {
        Column(
            modifier = modifier
                .background(color = Color.DarkGray)
                .padding(8.dp)
        ) {
            BottomSheetHeaderBar(
                modifier = Modifier.align(CenterHorizontally)
            )

            LazyVerticalGrid(
                modifier = modifier.padding(24.dp),
                columns = GridCells.Fixed(3)
            ) {
                items(HomeEditType.values()) { type ->
                    HomeEditTypeItem(
                        modifier = Modifier.fillMaxSize(),
                        editType = type,
                        clickListener = clickListener,
                    )
                }
            }
        }
    }
}

@Composable
fun HomeEditTypeItem(
    editType: HomeEditType,
    modifier: Modifier = Modifier,
    clickListener: (HomeEditType) -> Unit = {},
) {
    Box(
        modifier = modifier
            .padding(16.dp)
            .height(40.dp)
            .noRippleClickable {
                clickListener(editType)
            }
            .background(
                color = Color.White,
                shape = RoundedCornerShape(12.dp)
            )
    ) {
        Text(
            modifier = Modifier.align(Center),
            text = stringResource(editType.labelRes),
            maxLines = 1
        )
    }
}