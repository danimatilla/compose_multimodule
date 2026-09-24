package com.dxmxp.navigation.utils

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation3.runtime.MetadataScope
import androidx.navigation3.ui.NavDisplay
import kotlinx.coroutines.delay

object NavigationUtils {

    @Composable
    fun rememberModalContentVisible(): State<Boolean> {
        var visible by remember { mutableStateOf(false) }

        LaunchedEffect(Unit) {
            delay(MODAL_ENTER_DURATION_MS.toLong())
            visible = true
        }

        return remember(visible) { mutableStateOf(visible) }
    }

    fun MetadataScope.modalAnimation() {
        put(NavDisplay.TransitionKey) {
            slideInVertically(
                initialOffsetY = { it },
                animationSpec = tween(
                    durationMillis = MODAL_ENTER_DURATION_MS,
                    easing = FastOutSlowInEasing
                )
            ).plus(fadeIn(animationSpec = tween(durationMillis = MODAL_ENTER_DURATION_MS)))
                .togetherWith(
                    ExitTransition.KeepUntilTransitionsFinished
                ).apply {
                    targetContentZIndex = 1f
                }
        }
        put(NavDisplay.PopTransitionKey) {
            EnterTransition.None
                .togetherWith(
                    slideOutVertically(
                        targetOffsetY = { it },
                        animationSpec = tween(
                            durationMillis = MODAL_EXIT_DURATION_MS,
                            easing = FastOutSlowInEasing
                        )
                    ).plus(fadeOut(animationSpec = tween(durationMillis = MODAL_EXIT_DURATION_MS)))
                ).apply {
                    targetContentZIndex = -1f
                }
        }
    }

    private const val MODAL_ENTER_DURATION_MS = 500
    private const val MODAL_EXIT_DURATION_MS = 400
}
