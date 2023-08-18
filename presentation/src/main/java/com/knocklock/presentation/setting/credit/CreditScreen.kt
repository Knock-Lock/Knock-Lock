package com.knocklock.presentation.setting.credit

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.knocklock.presentation.R
import com.knocklock.presentation.ui.component.KnockLockTopAppbar

@Composable
fun CreditRoute(
    onIconClick: () -> Boolean,
    onTextClick: (TextMenu) -> Unit,
    modifier: Modifier = Modifier,
) {
    CreditScreen(
        onIconClick = onIconClick,
        onTextClick = onTextClick,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreditScreen(
    onIconClick: () -> Boolean,
    onTextClick: (TextMenu) -> Unit,
    modifier: Modifier = Modifier,
) {
    val scrollState = rememberScrollState(0)

    Scaffold(
        modifier = modifier,
        topBar = {
            KnockLockTopAppbar(
                stringResource(id = R.string.knock_lock_credit),
                onBackButtonClick = { onIconClick() },
                modifier = Modifier,
            )
        },
    ) { padding ->
        CreditBody(scrollState, padding, onTextClick, modifier)
    }
}

@Composable
private fun CreditBody(
    scrollState: ScrollState,
    padding: PaddingValues,
    onTextClick: (TextMenu) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .verticalScroll(scrollState)
            .padding(
                top = padding.calculateTopPadding(),
                bottom = padding.calculateBottomPadding(),
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        DeveloperList()
        Spacer(modifier.padding(20.dp))
        CreditTextMenuList(onTextClick = onTextClick)
    }
}

@Composable
fun CreditTextMenuList(
    onTextClick: (TextMenu) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(bottom = 20.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        TextMenu.values().forEach { menu ->
            CreditTextMenu(onTextClick = onTextClick, menu = menu)
        }
    }
}

@Composable
private fun CreditTextMenu(
    onTextClick: (TextMenu) -> Unit,
    menu: TextMenu,
    modifier: Modifier = Modifier,
) {
    Text(
        modifier = modifier.clickable { onTextClick(menu) },
        text = stringResource(id = menu.textRes),
        fontSize = 12.sp,
        color = Color.Blue,
        textDecoration = TextDecoration.Underline,
    )
}
