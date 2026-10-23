package com.dxmxp.ui.components.html

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle

/**
 * Public helper for parsing and converting HTML text to Compose [AnnotatedString] with
 * support for HTML tags, links, design system tokens, inline CSS styles, and debug visualization.
 */
object HtmlTextHelper {

    /**
     * Converts an HTML string containing HTML tags, links, optional design tokens, and inline CSS styles into an [AnnotatedString].
     *
     * @param html Raw HTML string
     * @param tagStyles Optional mapping of HTML tags to design system tokens (e.g., h1 -> TitleXL)
     * @param defaultLinkStyle Optional default style applied to <a> tags if not overridden by tagStyles
     * @param onLinkClick Callback invoked when a link is clicked
     * @param debugConfig Optional debug configuration for visualizing carriage returns and element boxes
     */
    fun parseHtml(
        html: String,
        tagStyles: HtmlTagStyles? = null,
        defaultLinkStyle: SpanStyle? = null,
        onLinkClick: ((String) -> Unit)? = null,
        debugConfig: HtmlDebugConfig? = null
    ): AnnotatedString {
        return HtmlParser.parse(html, tagStyles, defaultLinkStyle, onLinkClick, debugConfig)
    }

    /**
     * Parses an inline CSS style string (e.g. "color: #ff0000; font-weight: bold;") into a [SpanStyle].
     */
    fun parseCssStyle(cssString: String): SpanStyle {
        return CssStyleParser.parseSpanStyle(cssString)
    }

    /**
     * Parses a CSS color string (hex, rgb, rgba, named color) into a Compose [Color].
     */
    fun parseCssColor(colorString: String): Color? {
        return CssStyleParser.parseColor(colorString)
    }
}

/**
 * Extension function to parse an HTML string directly into an [AnnotatedString].
 */
fun String.parseHtml(
    tagStyles: HtmlTagStyles? = null,
    defaultLinkStyle: SpanStyle? = null,
    onLinkClick: ((String) -> Unit)? = null,
    debugConfig: HtmlDebugConfig? = null
): AnnotatedString {
    return HtmlTextHelper.parseHtml(this, tagStyles, defaultLinkStyle, onLinkClick, debugConfig)
}
