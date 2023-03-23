package com.knocklock.presentation.setting.credit

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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


@Composable
fun CreditRoute(
    modifier: Modifier = Modifier,
    onIconClick: () -> Boolean,
    onTextClicked: (TextMenu) -> Unit
) {
    CreditScreen(modifier, onIconClick, onTextClicked)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreditScreen(
    modifier: Modifier = Modifier,
    onIconClick: () -> Boolean,
    onTextClicked: (TextMenu) -> Unit
) {
    val scrollState = rememberScrollState(0)

    Scaffold(
        modifier = modifier,
        topBar = { CreditHeader(onIconClick) },
    ) { padding ->
        CreditBody(modifier, scrollState, padding, onTextClicked)
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CreditHeader(
    onIconClick: () -> Boolean
) {
    TopAppBar(
        modifier = Modifier
            .padding(5.dp)
            .fillMaxWidth(),
        title = {
            Text(
                text = stringResource(id = R.string.knock_lock_credit),
                modifier = Modifier.padding(start = 10.dp, bottom = 5.dp)
            )
        },
        navigationIcon = {
            IconButton(
                modifier = Modifier.size(20.dp),
                onClick = { onIconClick() }
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

@Composable
private fun CreditBody(
    modifier: Modifier = Modifier,
    scrollState: ScrollState,
    padding: PaddingValues,
    onTextClicked: (TextMenu) -> Unit
) {
    Column(
        modifier = modifier
            .verticalScroll(scrollState)
            .padding(
                top = padding.calculateTopPadding(),
                bottom = padding.calculateBottomPadding() - 5.dp
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        DeveloperList()
        Spacer(modifier.padding(20.dp))
        CreditTextMenuList(onTextClicked = onTextClicked)
    }
}

@Composable
fun CreditTextMenuList(
    modifier: Modifier = Modifier,
    onTextClicked: (TextMenu) -> Unit
) {
    Column(
        modifier = modifier.padding(bottom = 20.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextMenu.values().forEach { menu ->
            CreditTextMenu(onTextClicked = onTextClicked, menu = menu)
        }
    }
}

@Composable
private fun CreditTextMenu(
    modifier: Modifier = Modifier,
    onTextClicked: (TextMenu) -> Unit,
    menu: TextMenu
) {
    Text(
        modifier = modifier.clickable { onTextClicked(menu) },
        text = stringResource(id = menu.textRes),
        fontSize = 12.sp,
        color = Color.Blue,
        textDecoration = TextDecoration.Underline
    )
}