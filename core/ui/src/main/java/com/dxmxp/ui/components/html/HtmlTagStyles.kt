package com.dxmxp.ui.components.html

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextDecoration

/**
 * Maps HTML tags to design system tokens (Compose [TextStyle] / [SpanStyle]).
 *
 * Example:
 * ```
 * val tagStyles = HtmlTagStyles.default().copy(
 *     h1 = MaterialTheme.typography.headlineLarge, // e.g. TitleXL design token
 *     p = MaterialTheme.typography.bodyMedium
 * )
 * ```
 */
data class HtmlTagStyles(
    val h1: TextStyle? = null,
    val h2: TextStyle? = null,
    val h3: TextStyle? = null,
    val h4: TextStyle? = null,
    val h5: TextStyle? = null,
    val h6: TextStyle? = null,
    val p: TextStyle? = null,
    val code: TextStyle? = null,
    val blockquote: TextStyle? = null,
    val a: SpanStyle? = null,
    val customTagStyles: Map<String, TextStyle> = emptyMap()
) {
    /**
     * Resolves the [TextStyle] design token for a given HTML tag name (case-insensitive).
     */
    fun resolveTextStyle(tagName: String): TextStyle? {
        val tag = tagName.lowercase()
        return when (tag) {
            "h1" -> h1
            "h2" -> h2
            "h3" -> h3
            "h4" -> h4
            "h5" -> h5
            "h6" -> h6
            "p" -> p
            "code" -> code
            "blockquote" -> blockquote
            else -> customTagStyles[tag] ?: customTagStyles[tagName]
        }
    }

    companion object {
        /**
         * Creates a default [HtmlTagStyles] mapping tags to current [MaterialTheme.typography] design tokens.
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
         * Creates a default [HtmlTagStyles] mapping tags from a given [Typography] instance.
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
