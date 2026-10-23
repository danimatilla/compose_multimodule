package com.dxmxp.ui.components.html

import androidx.compose.runtime.compositionLocalOf

/**
 * Debug configuration for inspecting HTML text rendering in Jetpack Compose.
 *
 * Visualizes:
 * - **Paragraph endings**: Renders the pilcrow symbol (`¶`) exclusively at paragraph/block endings (`p`, `h1`-`h6`, `div`, etc.).
 * - **Element boxes / bounding boxes**: Tints background colors for HTML block elements and inline spans to visually distinguish tags (`h1`, `p`, `a`, `code`, `b`, etc.).
 * - **Outer bounds**: Draws a debug border around the [HtmlText] composable container.
 *
 * @param enabled Whether debug visualization is enabled.
 * @param showParagraphEnds Renders pilcrow symbol (`¶`) at paragraph/block endings (`p`, `h1`-`h6`, `div`, etc.).
 * @param showElementBoxes Renders subtle tinted bounding boxes for HTML tags (`h1`, `p`, `a`, `code`, etc.).
 * @param showContainerBorder Draws a thin border around the outer [HtmlText] composable bounds.
 * @param paragraphEndSymbol The symbol rendered for paragraph endings (default: `"¶"`).
 */
data class HtmlDebugConfig(
    val enabled: Boolean = false,
    val showParagraphEnds: Boolean = true,
    val showElementBoxes: Boolean = true,
    val showContainerBorder: Boolean = true,
    val paragraphEndSymbol: String = "¶"
)

/**
 * CompositionLocal providing external [HtmlDebugConfig] across the Compose hierarchy.
 */
val LocalHtmlDebugConfig = compositionLocalOf { HtmlDebugConfig(enabled = false) }
