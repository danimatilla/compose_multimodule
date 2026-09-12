package com.dxmxp.ui.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.dxmxp.navigation.model.Screen.Companion.screenEntry
import com.dxmxp.ui.screens.WebView
import com.dxmxp.ui.screens.WebViewScreen

/**
 * Extension to register common UI screens from the core:ui module.
 * This keeps the core:navigation module pure and free from UI implementations.
 */
fun EntryProviderScope<NavKey>.registerCoreUiEntries() {
    screenEntry<WebView> { webView ->
        WebViewScreen(screen = webView)
    }
}
