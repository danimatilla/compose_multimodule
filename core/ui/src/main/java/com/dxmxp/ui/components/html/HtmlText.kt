package com.dxmxp.ui.components.html

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.dxmxp.ui.theme.SeedTheme

/**
 * A Jetpack Compose component for displaying HTML formatted text.
 *
 * Supports:
 * - HTML Tags (`<b>`, `<i>`, `<u>`, `<s>`, `<h1>`-`<h6>`, `<p>`, `<br>`, `<font>`, `<code>`, `<sub>`, `<sup>`, `<small>`, `<big>`, `<ul>`, `<ol>`, `<li>`, etc.) with default HTML styling out-of-the-box
 * - External tag-to-design-token configuration via [LocalHtmlTagStyles] or explicit [tagStyles]
 * - Dynamic tag resolution via [HtmlTagStyles.tagResolver] for arbitrary HTML tags at runtime
 * - Link rendering (`<a href="...">`) with configurable click handling via [onLinkClick]
 * - Inline CSS styles (`style="color: red; font-weight: bold; background-color: yellow; font-size: 16sp;"`)
 * - Debug mode inspection via [debug] flag or [LocalHtmlDebug] to visualize paragraph endings (`¶`) and element bounding boxes
 *
 * @param html The HTML formatted string to render
 * @param modifier Composable modifier
 * @param tagStyles Optional mapping of HTML tags to design system typography tokens. Defaults to [LocalHtmlTagStyles.current].
 * @param debug Debug flag for visualizing paragraph endings (`¶`) and element bounding boxes. Defaults to [LocalHtmlDebug.current].
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
    tagStyles: HtmlTagStyles? = LocalHtmlTagStyles.current,
    debug: Boolean = LocalHtmlDebug.current,
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

    val annotatedString = remember(html, tagStyles, defaultLinkStyle, debug) {
        HtmlTextHelper.parseHtml(
            html = html,
            tagStyles = tagStyles,
            defaultLinkStyle = defaultLinkStyle,
            onLinkClick = effectiveOnLinkClick,
            debug = debug
        )
    }

    val effectiveModifier = if (debug) {
        modifier.border(
            width = 1.dp,
            color = Color(0xFFE91E63), // Debug magenta/pink border
            shape = RoundedCornerShape(2.dp)
        )
    } else {
        modifier
    }

    Text(
        text = annotatedString,
        modifier = effectiveModifier,
        style = style,
        color = color,
        softWrap = softWrap,
        overflow = overflow,
        maxLines = maxLines,
        onTextLayout = onTextLayout ?: {}
    )
}

@PreviewLightDark
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

@PreviewLightDark
@Composable
private fun HtmlTextDebugPreview() {
    SeedTheme {
        Surface {
            Column(modifier = Modifier.padding(16.dp)) {
                // Vista en modo Debug simple mediante el flag `debug = true`
                HtmlText(
                    html = """
                        <h1>Encabezado H1 en Debug</h1>
                        <p>Párrafo con <b>negrita</b>, <i>cursiva</i> y <a href="#">enlace</a>.</p>
                        <code>val x = 42</code>
                    """.trimIndent(),
                    debug = true
                )
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun HtmlTextExternalConfigPreview() {
    SeedTheme {
        Surface {
            Column(modifier = Modifier.padding(16.dp)) {
                val headlineMedium = MaterialTheme.typography.headlineMedium
                val headlineLarge = MaterialTheme.typography.headlineLarge
                val bodyMedium = MaterialTheme.typography.bodyMedium

                // Configuración externa mediante CompositionLocal + Dynamic Tag Resolver
                val externalHtmlConfig = HtmlTagStyles(
                    tagStyles = mapOf(
                        "h1" to headlineLarge, // Token TitleXL
                        "p" to bodyMedium
                    ),
                    tagResolver = { tagName ->
                        // Resolver dinámico para cualquier tag desconocido que llegue en tiempo de ejecución
                        if (tagName.contains("title")) headlineMedium else null
                    }
                )

                CompositionLocalProvider(LocalHtmlTagStyles provides externalHtmlConfig) {
                    HtmlText(
                        html = """
                            <h1>Encabezado H1 configurado externamente (TitleXL)</h1>
                            <p>Párrafo configurado externamente.</p>
                            <custom-title>Tag dinámico resuelto por tagResolver</custom-title>
                        """.trimIndent()
                    )
                }
            }
        }
    }
}
