package com.knocklock.presentation.ui.setting.credit

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import androidx.compose.ui.unit.sp
import com.knocklock.presentation.R

private val headerHeight = 275.dp
private val toolbarHeight = 56.dp

private val paddingMedium = 16.dp

private val titlePaddingStart = 16.dp
private val titlePaddingEnd = 72.dp

private const val titleFontScaleStart = 1f
private const val titleFontScaleEnd = 0.66f

@Composable
fun CreditRoute(
    modifier: Modifier = Modifier,
    onIconClick: () -> Boolean
) {
    CreditScreen(modifier, onIconClick)
}

@Composable
fun CreditScreen(
    modifier: Modifier = Modifier,
    onIconClick: () -> Boolean
) {
    val scrollState = rememberScrollState(0)
    val headerHeightPx = with(LocalDensity.current) { headerHeight.toPx() }
    val toolbarHeightPx = with(LocalDensity.current) { toolbarHeight.toPx() }

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        Header(scroll = scrollState, headerHeightPx = headerHeightPx)
        Body(scroll = scrollState)
        Toolbar(
            scroll = scrollState,
            headerHeightPx = headerHeightPx,
            toolbarHeightPx = toolbarHeightPx,
            onIconClick = onIconClick
        )
        Title(
            scroll = scrollState,
            headerHeightPx = headerHeightPx,
            toolbarHeightPx = toolbarHeightPx
        )
    }
}


@Composable
private fun Header(
    modifier: Modifier = Modifier,
    scroll: ScrollState,
    headerHeightPx: Float
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(headerHeight)
            .graphicsLayer {
                translationY = -scroll.value.toFloat() / 1.2f
                alpha = (-1f / headerHeightPx) * scroll.value + 1
            }
    ) {
        Image(
            painter = painterResource(id = R.drawable.bg_header),
            contentDescription = null,
            contentScale = ContentScale.FillBounds
        )
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color(0xAA000000)),
                        startY = 3 * headerHeightPx / 4
                    )
                )
        )
    }
}


@Composable
private fun Body(
    modifier: Modifier = Modifier,
    scroll: ScrollState
) {
    Column(
        modifier = modifier
            .verticalScroll(scroll)
            .fillMaxHeight(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier.height(headerHeight))
        DeveloperList()
        Spacer(modifier.padding(20.dp))
        Column(
            modifier = modifier.padding(bottom = 20.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.github),
                fontSize = 12.sp,
                color = Color.Blue,
                textDecoration = TextDecoration.Underline
            )
            Text(
                text = stringResource(R.string.evaluate),
                fontSize = 12.sp,
                color = Color.Blue,
                textDecoration = TextDecoration.Underline
            )
            Text(
                text = stringResource(R.string.inquiry),
                fontSize = 12.sp,
                color = Color.Blue,
                textDecoration = TextDecoration.Underline
            )
            Text(
                text = stringResource(R.string.open_source_license),
                fontSize = 12.sp,
                color = Color.Blue,
                textDecoration = TextDecoration.Underline
            )
            Text(
                text = stringResource(R.string.service),
                fontSize = 12.sp,
                color = Color.Blue,
                textDecoration = TextDecoration.Underline
            )
            Text(
                text = stringResource(R.string.donate),
                fontSize = 12.sp,
                color = Color.Blue,
                textDecoration = TextDecoration.Underline
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Toolbar(
    modifier: Modifier = Modifier,
    scroll: ScrollState,
    headerHeightPx: Float,
    toolbarHeightPx: Float,
    onIconClick: () -> Boolean
) {
    val toolbarBottom = headerHeightPx - toolbarHeightPx
    val showToolbar by remember {
        derivedStateOf {
            scroll.value >= toolbarBottom
        }
    }

    AnimatedVisibility(
        visible = showToolbar,
        enter = fadeIn(animationSpec = tween(300)),
        exit = fadeOut(animationSpec = tween(300))
    ) {
        CenterAlignedTopAppBar(
            modifier = modifier
                .background(
                    brush = Brush.horizontalGradient(
                        listOf(Color(0xff1FAB89), Color(0xff6AFCBD))
                    )
                ),
            navigationIcon = {
                IconButton(
                    modifier = modifier
                        .padding(horizontal = 16.dp)
                        .size(24.dp),
                    onClick = { onIconClick() }
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = null,
                        tint = Color.White
                    )
                }
            },
            title = {},
            colors = TopAppBarDefaults.smallTopAppBarColors(
                containerColor = Color.Transparent
            )
        )
    }
}

@Composable
private fun Title(
    modifier: Modifier = Modifier,
    scroll: ScrollState,
    headerHeightPx: Float,
    toolbarHeightPx: Float
) {
    var titleHeightPx by remember { mutableStateOf(0f) }
    var titleWidthPx by remember { mutableStateOf(0f) }

    Text(
        modifier = modifier
            .graphicsLayer {
                val collapseRange: Float = (headerHeightPx - toolbarHeightPx)
                val collapseFraction: Float = (scroll.value / collapseRange).coerceIn(0f, 1f)

                val scaleXY = lerp(
                    titleFontScaleStart.dp,
                    titleFontScaleEnd.dp,
                    collapseFraction
                )

                val titleExtraStartPadding = titleWidthPx.toDp() * (1 - scaleXY.value) / 2f

                val titleYFirstInterpolatedPoint = lerp(
                    headerHeight - titleHeightPx.toDp() - paddingMedium,
                    headerHeight / 2,
                    collapseFraction
                )

                val titleXFirstInterpolatedPoint = lerp(
                    titlePaddingStart,
                    (titlePaddingEnd - titleExtraStartPadding) * 5 / 4,
                    collapseFraction
                )

                val titleYSecondInterpolatedPoint = lerp(
                    headerHeight / 2,
                    toolbarHeight / 2 - titleHeightPx.toDp() / 2,
                    collapseFraction
                )

                val titleXSecondInterpolatedPoint = lerp(
                    (titlePaddingEnd - titleExtraStartPadding) * 5 / 4,
                    titlePaddingEnd - titleExtraStartPadding,
                    collapseFraction
                )

                val titleY = lerp(
                    titleYFirstInterpolatedPoint,
                    titleYSecondInterpolatedPoint,
                    collapseFraction
                )

                val titleX = lerp(
                    titleXFirstInterpolatedPoint,
                    titleXSecondInterpolatedPoint,
                    collapseFraction
                )

                translationY = titleY.toPx()
                translationX = titleX.toPx()
                scaleX = scaleXY.value
                scaleY = scaleXY.value
            }
            .onGloballyPositioned {
                titleHeightPx = it.size.height.toFloat()
                titleWidthPx = it.size.width.toFloat()
            },
        text = stringResource(R.string.knock_lock_credit),
        fontSize = 30.sp,
        fontWeight = FontWeight.Bold
    )
}
