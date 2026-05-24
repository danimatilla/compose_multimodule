package com.dxmxp.ui.navigation.helpers

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.ui.unit.IntOffset
import androidx.navigation3.runtime.MetadataScope
import androidx.navigation3.ui.NavDisplay

object NavigationUtils {

    fun MetadataScope.modalAnimation() {
        // Usamos spring para el movimiento para que se sienta más fluido y "físico"
        val slideSpec = spring<IntOffset>(
            dampingRatio = Spring.DampingRatioNoBouncy, // Sin rebote para un movimiento más directo
            stiffness = Spring.StiffnessMediumLow // Un poco más lento que el valor por defecto
        )
        // Un fundido un poco más largo para acompañar el movimiento
        val fadeSpec = tween<Float>(durationMillis = 1000)

        put(NavDisplay.TransitionKey) {
            (slideInVertically(animationSpec = slideSpec, initialOffsetY = { it }) + fadeIn(fadeSpec))
                .togetherWith(fadeOut(fadeSpec))
                .apply {
                    targetContentZIndex = 1f // Asegura que el contenido entrante esté por encima del saliente
                }
        }
        put(NavDisplay.PopTransitionKey) {
            fadeIn(fadeSpec).togetherWith(
                slideOutVertically(animationSpec = slideSpec, targetOffsetY = { it }) + fadeOut(fadeSpec)
            )
        }
    }
}