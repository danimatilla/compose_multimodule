package com.dxmxp.ui.components.html

import androidx.compose.runtime.compositionLocalOf

/**
 * CompositionLocal providing external debug mode flag across the Compose hierarchy.
 *
 * When true, [HtmlText] visualizes:
 * - Paragraph endings with the pilcrow symbol (`¶`).
 * - Element bounding boxes with subtle background color tints.
 * - Container outer borders.
 */
val LocalHtmlDebug = compositionLocalOf { false }
