package com.knocklock.presentation.extenstions

import android.os.Build
import android.view.HapticFeedbackConstants
import android.view.View
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.keyframes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import com.knocklock.presentation.setting.password.PasswordInputState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

fun Modifier.noRippleClickable(
    enabled: Boolean = true,
    onClick: () -> Unit
) = composed {
    clickable(
        interactionSource = remember { MutableInteractionSource() },
        indication = null,
        enabled = enabled,
        onClick = onClick
    )
}

fun Modifier.wiggle(
    isWiggle: Boolean = false,
    onWiggleAnimationEnded: () -> Unit = {}
) = composed {
    val offsetX = remember { Animatable(0F) }
    val view = LocalView.current
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(isWiggle) {
        if (isWiggle) {
            coroutineScope.launch {
                offsetX.animateTo(
                    targetValue = 0f,
                    animationSpec =  keyframes {
                        durationMillis = 800
                        val easing = FastOutLinearInEasing
                        for (i in 1..8) {
                            val x = when (i % 3) {
                                0 -> 4f
                                1 -> -4f
                                else -> 0f
                            }
                            x at durationMillis / 10 * i with easing
                        }
                    },
                )
            }.invokeOnCompletion {
                onWiggleAnimationEnded()
            }
            view.let {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    view.performHapticFeedback(HapticFeedbackConstants.REJECT)
                } else {
                    view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
                }
            }
        }
    }

    Modifier.offset(offsetX.value.dp, 0.dp)
}