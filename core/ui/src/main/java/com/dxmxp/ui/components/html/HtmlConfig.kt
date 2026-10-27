package com.dxmxp.ui.components.html

/**
 * Unified configuration for HTML parsing, link handling, tag styles, and debug inspection.
 *
 * @param tagStyles Mapping of HTML tags to design system typography tokens.
 * @param onLinkClick Callback invoked when an HTML link (`<a href="...">`) is clicked.
 * @param debug Flag to enable visual debug inspection (pilcrow symbol `¶`, element box tints, and borders).
 */
data class HtmlConfig(
    val tagStyles: HtmlTagStyles? = null,
    val onLinkClick: ((String) -> Unit)? = null,
    val debug: Boolean = false
)
