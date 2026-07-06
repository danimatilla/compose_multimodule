package com.dxmxp.ui.navigation.utils

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.navigation3.runtime.MetadataScope
import androidx.navigation3.ui.NavDisplay

object NavigationUtils {

    fun MetadataScope.modalAnimation() {
        put(NavDisplay.TransitionKey) {
            slideInVertically(
                initialOffsetY = { it },
                animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing)
            ).plus(fadeIn(animationSpec = tween(durationMillis = 500)))
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
                            durationMillis = 400,
                            easing = FastOutSlowInEasing
                        )
                    ).plus(fadeOut(animationSpec = tween(durationMillis = 400)))
                ).apply {
                    targetContentZIndex = -1f
                }
        }
    }
}