package com.dxmxp.ui.navigation.scaffold

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.dxmxp.ui.navigation.model.Screen
import com.dxmxp.ui.navigation.model.Screen.Companion.screenEntry

/**
 * DSL builder to simplify graph entry registration.
 * Reduces boilerplate when registering multiple screens with different patterns.
 */
class ScaffoldEntryBuilder(
    val scope: EntryProviderScope<NavKey>
) {
    inline fun <reified SCREEN : Screen> registerScreen(
        crossinline content: @Composable (SCREEN) -> Unit
    ) {
        scope.screenEntry<SCREEN> { screen -> content(screen) }
    }

    inline fun <reified SCREEN : Screen, reified VM : ViewModel> registerScreen(
        crossinline viewModelProvide: @Composable () -> VM,
        crossinline content: @Composable (VM) -> Unit
    ) {
        scope.screenEntry<SCREEN, VM>(
            viewModelProvide = viewModelProvide
        ) { vm -> content(vm) }
    }

    fun registerGraph(block: EntryProviderScope<NavKey>.() -> Unit) {
        block(scope)
    }
}

fun EntryProviderScope<NavKey>.scaffoldBuilder(
    block: ScaffoldEntryBuilder.() -> Unit
) {
    ScaffoldEntryBuilder(this).also { builder ->
        builder.block()
    }
}
