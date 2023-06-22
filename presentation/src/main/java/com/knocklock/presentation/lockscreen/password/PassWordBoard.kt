package com.knocklock.presentation.lockscreen.password

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.knocklock.presentation.extenstions.wiggle
import kotlinx.collections.immutable.ImmutableList

/**
 * @Created by 김현국 2023/05/17
 */

@Composable
fun CirclePassWordBoard(
    passWordList: ImmutableList<PassWord>,
    onPassWordClick: (String) -> Unit,
    removePassWord: () -> Unit,
    isPlaying: Boolean,
    inputPassWordState: ImmutableList<PassWord>,
    eventState: Event,
    onWiggleAnimationEnded: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ConstraintLayout(
        modifier = modifier,
    ) {
        val screenWidthDp = LocalConfiguration.current.screenWidthDp.dp
        val circlePassWordNumberSize = screenWidthDp / 3
        val fixedSizeModifier = Modifier.size(circlePassWordNumberSize).padding(horizontal = circlePassWordNumberSize / 10).aspectRatio(1f).background(
            color = Color.LightGray.copy(alpha = 0.3f),
            shape = CircleShape,
        ).clip(CircleShape)

        val removeBackgroundModifier = Modifier.size(circlePassWordNumberSize).padding(horizontal = circlePassWordNumberSize / 10).aspectRatio(1f)
        val (
            topUnlockLayout, button1, button2, button3, button4, button5, button6, button7, button8, button9, buttonEmpty, button0, backbutton,
        ) = createRefs()

        Column(
            modifier = Modifier.constrainAs(topUnlockLayout) {
                start.linkTo(parent.start)
                top.linkTo(parent.top)
                end.linkTo(parent.end)
                bottom.linkTo(button2.top)
            },
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(50.dp))
            Locker(
                modifier = Modifier.size(50.dp),
                isPlaying = isPlaying,
            )
            InsertPassWordText()
            Spacer(modifier = Modifier.height(40.dp))
            InsertPassWordRow(
                modifier = Modifier
                    .wiggle(isWiggle = eventState == Event.VIBRATE, onWiggleAnimationEnded = onWiggleAnimationEnded)
                    .padding(horizontal = 50.dp)
                    .fillMaxWidth(),
                inputPassWordState = inputPassWordState,
            )
        }

        CirclePassWordNumber(
            modifier = fixedSizeModifier.constrainAs(button1) {
                start.linkTo(parent.start)
                top.linkTo(topUnlockLayout.bottom)
                end.linkTo(button2.start)
                bottom.linkTo(button4.top)
            },
            passWord = passWordList[0],
            onPassWordClick = onPassWordClick,
        )
        CirclePassWordNumber(
            modifier = fixedSizeModifier.constrainAs(button2) {
                start.linkTo(button1.end)
                top.linkTo(topUnlockLayout.bottom)
                end.linkTo(button3.start)
                bottom.linkTo(button5.top)
            },
            passWord = passWordList[1],
            onPassWordClick = onPassWordClick,
        )
        CirclePassWordNumber(
            modifier = fixedSizeModifier.constrainAs(button3) {
                start.linkTo(button2.end)
                end.linkTo(parent.end)
                top.linkTo(topUnlockLayout.bottom)
                bottom.linkTo(button6.top)
            },
            passWord = passWordList[2],
            onPassWordClick = onPassWordClick,
        )
        CirclePassWordNumber(
            modifier = fixedSizeModifier.constrainAs(button4) {
                start.linkTo(parent.start)
                top.linkTo(button1.bottom)
                end.linkTo(button5.start)
                bottom.linkTo(button7.top)
            },
            passWord = passWordList[3],
            onPassWordClick = onPassWordClick,
        )
        CirclePassWordNumber(
            modifier = fixedSizeModifier.constrainAs(button5) {
                start.linkTo(button4.end)
                top.linkTo(button2.bottom)
                end.linkTo(button6.start)
                bottom.linkTo(button8.top)
            },
            passWord = passWordList[4],
            onPassWordClick = onPassWordClick,
        )
        CirclePassWordNumber(
            modifier = fixedSizeModifier.constrainAs(button6) {
                start.linkTo(button5.end)
                top.linkTo(button3.bottom)
                end.linkTo(parent.end)
                bottom.linkTo(button9.top)
            },
            passWord = passWordList[5],
            onPassWordClick = onPassWordClick,
        )
        CirclePassWordNumber(
            modifier = fixedSizeModifier.constrainAs(button7) {
                start.linkTo(parent.start)
                end.linkTo(button8.start)
                top.linkTo(button4.bottom)
                bottom.linkTo(buttonEmpty.top)
            },
            passWord = passWordList[6],
            onPassWordClick = onPassWordClick,
        )
        CirclePassWordNumber(
            modifier = fixedSizeModifier.constrainAs(button8) {
                start.linkTo(button7.end)
                end.linkTo(button9.start)
                top.linkTo(button5.bottom)
                bottom.linkTo(button0.top)
            },
            passWord = passWordList[7],
            onPassWordClick = onPassWordClick,
        )
        CirclePassWordNumber(
            modifier = fixedSizeModifier.constrainAs(button9) {
                start.linkTo(button8.end)
                top.linkTo(button6.bottom)
                end.linkTo(parent.end)
                bottom.linkTo(backbutton.top)
            },
            passWord = passWordList[8],
            onPassWordClick = onPassWordClick,
        )
        CirclePassWordNumber(
            modifier = removeBackgroundModifier.constrainAs(buttonEmpty) {
                start.linkTo(parent.start)
                top.linkTo(button7.bottom)
                end.linkTo(button0.start)
                bottom.linkTo(parent.bottom)
            },
            passWord = passWordList[9],
            onPassWordClick = { },
        )
        CirclePassWordNumber(
            modifier = fixedSizeModifier.constrainAs(button0) {
                start.linkTo(buttonEmpty.end)
                top.linkTo(button8.bottom)
                end.linkTo(backbutton.start)
                bottom.linkTo(parent.bottom)
            },
            passWord = passWordList[10],
            onPassWordClick = onPassWordClick,
        )
        BackButton(
            modifier = removeBackgroundModifier.constrainAs(backbutton) {
                start.linkTo(button0.end)
                top.linkTo(button9.bottom)
                end.linkTo(parent.end)
                bottom.linkTo(parent.bottom)
            }.clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = {
                    removePassWord()
                },
            ),
        )
    }
}
