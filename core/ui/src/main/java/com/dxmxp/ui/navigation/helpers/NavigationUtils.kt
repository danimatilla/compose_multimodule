package com.dxmxp.ui.navigation.helpers

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
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
            slideInVertically(initialOffsetY = { it })
                .plus(fadeIn())
                .togetherWith(ExitTransition.None)
                .apply {
                    targetContentZIndex = 1f
                }
        }
        put(NavDisplay.PopTransitionKey) {
            EnterTransition.None
                .togetherWith(
                    slideOutVertically(targetOffsetY = { it })
                        .plus(fadeOut())
                )
        }
    }
}