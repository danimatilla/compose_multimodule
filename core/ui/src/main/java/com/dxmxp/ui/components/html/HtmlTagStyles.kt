package com.dxmxp.ui.components.html

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextDecoration

/**
 * External configuration for mapping HTML tags to design system typography tokens.
 *
 * Can be provided externally via [CompositionLocalProvider] with [LocalHtmlTagStyles].
 *
 * Example external configuration:
 * ```
 * val myHtmlConfig = HtmlTagStyles(
 *     tagStyles = mapOf(
 *         "h1" to MaterialTheme.typography.headlineLarge, // TitleXL token
 *         "p" to MaterialTheme.typography.bodyMedium
 *     ),
 *     tagResolver = { tagName ->
 *         // Dynamic resolver for any dynamic or unknown tag at runtime
 *         if (tagName.startsWith("heading")) MaterialTheme.typography.titleLarge else null
 *     }
 * )
 *
 * CompositionLocalProvider(LocalHtmlTagStyles provides myHtmlConfig) {
 *     // Any HtmlText inside this hierarchy reads LocalHtmlTagStyles automatically!
 *     HtmlText(html = "<h1>Header</h1>")
 * }
 * ```
 */
data class HtmlTagStyles(
    val tagStyles: Map<String, TextStyle> = emptyMap(),
    val a: SpanStyle? = null,
    val tagResolver: ((tagName: String) -> TextStyle?)? = null
) {
    constructor(
        h1: TextStyle? = null,
        h2: TextStyle? = null,
        h3: TextStyle? = null,
        h4: TextStyle? = null,
        h5: TextStyle? = null,
        h6: TextStyle? = null,
        p: TextStyle? = null,
        code: TextStyle? = null,
        blockquote: TextStyle? = null,
        a: SpanStyle? = null,
        customTagStyles: Map<String, TextStyle> = emptyMap(),
        tagResolver: ((tagName: String) -> TextStyle?)? = null
    ) : this(
        tagStyles = buildMap {
            h1?.let { put("h1", it) }
            h2?.let { put("h2", it) }
            h3?.let { put("h3", it) }
            h4?.let { put("h4", it) }
            h5?.let { put("h5", it) }
            h6?.let { put("h6", it) }
            p?.let { put("p", it) }
            code?.let { put("code", it) }
            blockquote?.let { put("blockquote", it) }
            putAll(customTagStyles)
        },
        a = a,
        tagResolver = tagResolver
    )

    val h1: TextStyle? get() = tagStyles["h1"]
    val h2: TextStyle? get() = tagStyles["h2"]
    val h3: TextStyle? get() = tagStyles["h3"]
    val h4: TextStyle? get() = tagStyles["h4"]
    val h5: TextStyle? get() = tagStyles["h5"]
    val h6: TextStyle? get() = tagStyles["h6"]
    val p: TextStyle? get() = tagStyles["p"]
    val code: TextStyle? get() = tagStyles["code"]
    val blockquote: TextStyle? get() = tagStyles["blockquote"]

    /**
     * Resolves the [TextStyle] design token for a given HTML tag name (case-insensitive).
     */
    fun resolveTextStyle(tagName: String): TextStyle? {
        val tag = tagName.lowercase()
        return tagStyles[tag] ?: tagResolver?.invoke(tag)
    }

    companion object {
        /**
         * Creates a default [HtmlTagStyles] mapping standard tags from current [MaterialTheme.typography].
         */
        @Composable
        @ReadOnlyComposable
        fun default(): HtmlTagStyles {
            val typography = MaterialTheme.typography
            val colorScheme = MaterialTheme.colorScheme
            return HtmlTagStyles(
                h1 = typography.headlineLarge,
                h2 = typography.headlineMedium,
                h3 = typography.headlineSmall,
                h4 = typography.titleLarge,
                h5 = typography.titleMedium,
                h6 = typography.titleSmall,
                p = typography.bodyMedium,
                code = typography.bodyMedium.copy(fontFamily = FontFamily.Monospace),
                blockquote = typography.bodyMedium.copy(fontStyle = FontStyle.Italic),
                a = SpanStyle(
                    color = colorScheme.primary,
                    textDecoration = TextDecoration.Underline
                )
            )
        }

        /**
         * Creates a default [HtmlTagStyles] mapping tags from an explicit [Typography] instance.
         */
        fun fromTypography(typography: Typography, linkStyle: SpanStyle? = null): HtmlTagStyles {
            return HtmlTagStyles(
                h1 = typography.headlineLarge,
                h2 = typography.headlineMedium,
                h3 = typography.headlineSmall,
                h4 = typography.titleLarge,
                h5 = typography.titleMedium,
                h6 = typography.titleSmall,
                p = typography.bodyMedium,
                code = typography.bodyMedium.copy(fontFamily = FontFamily.Monospace),
                blockquote = typography.bodyMedium.copy(fontStyle = FontStyle.Italic),
                a = linkStyle
            )
        }
    }
}

/**
 * CompositionLocal providing external HTML tag styling configuration across the Compose view hierarchy.
 */
val LocalHtmlTagStyles = compositionLocalOf<HtmlTagStyles?> { null }
