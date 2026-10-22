package com.dxmxp.ui.components.html

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dxmxp.ui.theme.SeedTheme

/**
 * A Jetpack Compose component for displaying HTML formatted text.
 *
 * Supports:
 * - HTML Tags (`<b>`, `<i>`, `<u>`, `<s>`, `<h1>`-`<h6>`, `<p>`, `<br>`, `<font>`, `<code>`, `<sub>`, `<sup>`, `<small>`, `<big>`, `<ul>`, `<ol>`, `<li>`, etc.) with default HTML styling out-of-the-box
 * - Optionally mapping HTML tags to design system tokens via [tagStyles] (e.g. `h1` -> `MaterialTheme.typography.headlineLarge` / `TitleXL`)
 * - Link rendering (`<a href="...">`) with configurable click handling via [onLinkClick]
 * - Inline CSS styles (`style="color: red; font-weight: bold; background-color: yellow; font-size: 16sp;"`)
 *
 * @param html The HTML formatted string to render
 * @param modifier Composable modifier
 * @param tagStyles Optional mapping of HTML tags to design system typography tokens. If null, standard default HTML tag styles are applied.
 * @param style Base text style
 * @param color Text color
 * @param linkColor Color applied to HTML links
 * @param linkTextDecoration Text decoration applied to HTML links
 * @param onLinkClick Callback invoked when an HTML link is clicked. If null, opens URI via [LocalUriHandler].
 * @param softWrap Whether text should break at soft line breaks
 * @param overflow How visual overflow should be handled
 * @param maxLines Maximum number of lines to display
 * @param onTextLayout Callback invoked upon new text layout
 */
@Composable
fun HtmlText(
    html: String,
    modifier: Modifier = Modifier,
    tagStyles: HtmlTagStyles? = null,
    style: TextStyle = LocalTextStyle.current,
    color: Color = Color.Unspecified,
    linkColor: Color = MaterialTheme.colorScheme.primary,
    linkTextDecoration: TextDecoration = TextDecoration.Underline,
    onLinkClick: ((String) -> Unit)? = null,
    softWrap: Boolean = true,
    overflow: TextOverflow = TextOverflow.Clip,
    maxLines: Int = Int.MAX_VALUE,
    onTextLayout: ((TextLayoutResult) -> Unit)? = null
) {
    val uriHandler = LocalUriHandler.current
    val effectiveOnLinkClick: (String) -> Unit = onLinkClick ?: { url ->
        runCatching { uriHandler.openUri(url) }
    }

    val defaultLinkStyle = remember(linkColor, linkTextDecoration) {
        SpanStyle(color = linkColor, textDecoration = linkTextDecoration)
    }

    val annotatedString = remember(html, tagStyles, defaultLinkStyle) {
        HtmlTextHelper.parseHtml(
            html = html,
            tagStyles = tagStyles,
            defaultLinkStyle = defaultLinkStyle,
            onLinkClick = effectiveOnLinkClick
        )
    }

    Text(
        text = annotatedString,
        modifier = modifier,
        style = style,
        color = color,
        softWrap = softWrap,
        overflow = overflow,
        maxLines = maxLines,
        onTextLayout = onTextLayout ?: {}
    )
}

@Preview(showBackground = true)
@Composable
private fun HtmlTextDefaultPreview() {
    SeedTheme {
        Surface {
            Column(modifier = Modifier.padding(16.dp)) {
                // Uso por defecto sin vincular tokens: aplica los estilos por defecto a cada etiqueta HTML
                HtmlText(
                    html = """
                        <h1>Encabezado H1 por defecto</h1>
                        <p>Párrafo con <b>negrita</b>, <i>cursiva</i> y <u>subrayado</u>.</p>
                        <p style="color: #E91E63; font-weight: bold;">Estilo inline CSS personalizado.</p>
                        <p><a href="https://kotlinlang.org">Enlace por defecto</a></p>
                    """.trimIndent()
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun HtmlTextDesignTokensPreview() {
    SeedTheme {
        Surface {
            Column(modifier = Modifier.padding(16.dp)) {
                // Uso opcional vinculando etiquetas a tokens de diseño (p. ej. h1 -> TitleXL / headlineLarge)
                HtmlText(
                    html = """
                        <h1>Encabezado H1 vinculado a Token TitleXL</h1>
                        <p>Párrafo vinculado a Token BodyMedium.</p>
                    """.trimIndent(),
                    tagStyles = HtmlTagStyles(
                        h1 = MaterialTheme.typography.headlineLarge, // Token de diseño
                        p = MaterialTheme.typography.bodyMedium
                    )
                )
            }
        }
    }
}
