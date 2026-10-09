package com.dxmxp.navigation.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay

@Composable
fun NavGraphDisplay(
    modifier: Modifier = Modifier,
    backStack: NavBackStack<NavKey>,
    registerScreens: EntryProviderScope<NavKey>.() -> Unit
) {
    val entryProvider = remember {
        entryProvider(builder = registerScreens)
    }

    NavDisplay(
        backStack = backStack,
        entryProvider = entryProvider,
        modifier = modifier.fillMaxSize()
    )
}
