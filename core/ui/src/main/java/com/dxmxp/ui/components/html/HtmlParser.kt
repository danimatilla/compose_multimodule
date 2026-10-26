package com.dxmxp.ui.components.html

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.LinkInteractionListener
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.isSpecified
import androidx.compose.ui.unit.sp

/**
 * Core engine for parsing HTML tags, links, design tokens, and inline CSS styles into Compose [AnnotatedString].
 */
internal object HtmlParser {

    private data class OpenTag(
        val name: String,
        val startIndex: Int,
        val attributes: Map<String, String>,
        val spanStyle: SpanStyle?,
        val paragraphStyle: ParagraphStyle?,
        val linkUrl: String?
    )

    private val BLOCK_TAGS = setOf("p", "div", "h1", "h2", "h3", "h4", "h5", "h6", "ul", "ol", "li", "blockquote")

    fun parse(
        html: String,
        tagStyles: HtmlTagStyles? = null,
        defaultLinkStyle: SpanStyle? = null,
        onLinkClick: ((String) -> Unit)? = null,
        debug: Boolean = false
    ): AnnotatedString {
        if (html.isEmpty()) return AnnotatedString("")

        val builder = AnnotatedString.Builder()
        val tagStack = mutableListOf<OpenTag>()

        var i = 0
        val len = html.length

        while (i < len) {
            val c = html[i]
            if (c == '<') {
                // Check for HTML comment <!-- ... -->
                if (html.startsWith("<!--", i)) {
                    val commentEnd = html.indexOf("-->", i + 4)
                    if (commentEnd != -1) {
                        i = commentEnd + 3
                        continue
                    }
                }

                val tagEnd = html.indexOf('>', i)
                if (tagEnd != -1) {
                    val tagContent = html.substring(i + 1, tagEnd).trim()
                    i = tagEnd + 1

                    if (tagContent.startsWith("/")) {
                        // Closing tag
                        val tagName = tagContent.substring(1).trim().lowercase()
                        closeTag(tagName, builder, tagStack, tagStyles, defaultLinkStyle, onLinkClick, debug)
                    } else {
                        // Opening or self-closing tag
                        val isSelfClosing = tagContent.endsWith("/")
                        val cleanTag = if (isSelfClosing) tagContent.dropLast(1).trim() else tagContent

                        val (tagName, attributes) = parseTagAndAttributes(cleanTag)

                        if (tagName == "br") {
                            builder.append("\n")
                        } else if (tagName == "hr") {
                            ensureNewline(builder)
                            builder.append("───")
                            ensureNewline(builder)
                        } else if (!isSelfClosing) {
                            if (BLOCK_TAGS.contains(tagName)) {
                                ensureNewline(builder)
                                if (tagName == "li") {
                                    builder.append("• ")
                                }
                            }

                            // Build SpanStyle and ParagraphStyle from design tokens
                            val tokenTextStyle = tagStyles?.resolveTextStyle(tagName)
                            var spanStyle = tokenTextStyle?.toSpanStyle()
                            var paragraphStyle = tokenTextStyle?.toParagraphStyle()

                            // Fallback built-in tag styles if design token is not set
                            if (spanStyle == null) {
                                spanStyle = getBuiltInTagStyle(tagName, attributes)
                            }
                            if (paragraphStyle == null) {
                                paragraphStyle = getBuiltInParagraphStyle(tagName)
                            }

                            // Process debug element bounding box tints if enabled
                            if (debug) {
                                val debugBoxColor = getDebugBoxColor(tagName)
                                if (debugBoxColor != Color.Unspecified) {
                                    val debugSpanStyle = SpanStyle(background = debugBoxColor)
                                    spanStyle = spanStyle?.merge(debugSpanStyle) ?: debugSpanStyle
                                }
                            }

                            // Process inline CSS style="..."
                            val cssStyleAttr = attributes["style"]
                            if (!cssStyleAttr.isNullOrBlank()) {
                                val cssSpanStyle = CssStyleParser.parseSpanStyle(cssStyleAttr)
                                spanStyle = spanStyle?.merge(cssSpanStyle) ?: cssSpanStyle

                                var cssParaStyle = CssStyleParser.parseParagraphStyle(cssStyleAttr)
                                if (cssParaStyle == null && BLOCK_TAGS.contains(tagName) && cssSpanStyle.fontSize.isSpecified) {
                                    val fontSize = cssSpanStyle.fontSize
                                    val computedLineHeight = if (fontSize.isSp) (fontSize.value * 1.3f).sp else if (fontSize.isEm) (fontSize.value * 1.3f).em else TextUnit.Unspecified
                                    if (computedLineHeight.isSpecified) {
                                        cssParaStyle = ParagraphStyle(lineHeight = computedLineHeight)
                                    }
                                }
                                if (cssParaStyle != null) {
                                    paragraphStyle = paragraphStyle?.merge(cssParaStyle) ?: cssParaStyle
                                }
                            }

                            val linkUrl = if (tagName == "a") attributes["href"] else null

                            tagStack.add(
                                OpenTag(
                                    name = tagName,
                                    startIndex = builder.length,
                                    attributes = attributes,
                                    spanStyle = spanStyle,
                                    paragraphStyle = paragraphStyle,
                                    linkUrl = linkUrl
                                )
                            )
                        }
                    }
                    continue
                }
            }

            if (c == '&') {
                val entityEnd = html.indexOf(';', i)
                if (entityEnd != -1 && entityEnd - i <= 10) {
                    val entity = html.substring(i, entityEnd + 1)
                    val decoded = decodeHtmlEntity(entity)
                    if (decoded != null) {
                        builder.append(decoded)
                        i = entityEnd + 1
                        continue
                    }
                }
            }

            builder.append(c)
            i++
        }

        // Close any unclosed open tags
        while (tagStack.isNotEmpty()) {
            val openTag = tagStack.removeAt(tagStack.lastIndex)
            applyOpenTag(openTag, builder.length, builder, tagStyles, defaultLinkStyle, onLinkClick)
        }

        return builder.toAnnotatedString()
    }

    private fun closeTag(
        tagName: String,
        builder: AnnotatedString.Builder,
        tagStack: MutableList<OpenTag>,
        tagStyles: HtmlTagStyles?,
        defaultLinkStyle: SpanStyle?,
        onLinkClick: ((String) -> Unit)?,
        debug: Boolean
    ) {
        val index = tagStack.indexOfLast { it.name == tagName }
        if (index != -1) {
            val openTag = tagStack.removeAt(index)
            if (BLOCK_TAGS.contains(tagName) && debug) {
                appendParagraphEnd(builder)
            }
            val endPos = builder.length
            applyOpenTag(openTag, endPos, builder, tagStyles, defaultLinkStyle, onLinkClick)

            if (BLOCK_TAGS.contains(tagName)) {
                ensureNewline(builder)
            }
        }
    }

    private fun applyOpenTag(
        openTag: OpenTag,
        endPos: Int,
        builder: AnnotatedString.Builder,
        tagStyles: HtmlTagStyles?,
        defaultLinkStyle: SpanStyle?,
        onLinkClick: ((String) -> Unit)?
    ) {
        val startPos = openTag.startIndex
        if (startPos < endPos) {
            openTag.spanStyle?.let { style ->
                builder.addStyle(style, startPos, endPos)
            }

            openTag.paragraphStyle?.let { para ->
                builder.addStyle(para, startPos, endPos)
            }

            if (openTag.linkUrl != null) {
                val url = openTag.linkUrl
                val linkStyle = tagStyles?.a ?: defaultLinkStyle ?: SpanStyle(
                    color = Color(0xFF1B65D4),
                    textDecoration = TextDecoration.Underline
                )
                val linkAnnotation = LinkAnnotation.Url(
                    url = url,
                    styles = TextLinkStyles(style = linkStyle),
                    linkInteractionListener = onLinkClick?.let { listener ->
                        LinkInteractionListener { listener(url) }
                    }
                )
                builder.addLink(linkAnnotation, startPos, endPos)
                builder.addStringAnnotation("URL", url, startPos, endPos)
            }
        }
    }

    private fun appendParagraphEnd(builder: AnnotatedString.Builder) {
        val start = builder.length
        builder.append("¶")
        val end = builder.length
        builder.addStyle(
            SpanStyle(
                color = Color(0xFFE91E63), // Pink/Red debug indicator
                fontWeight = FontWeight.Bold,
                fontSize = 0.75.em
            ),
            start,
            end
        )
    }

    private fun ensureNewline(builder: AnnotatedString.Builder) {
        if (builder.length > 0 && builder.toAnnotatedString().text.last() != '\n') {
            builder.append("\n")
        }
    }

    private fun parseTagAndAttributes(tagContent: String): Pair<String, Map<String, String>> {
        val parts = tagContent.trim().split(Regex("\\s+"), limit = 2)
        val tagName = parts[0].lowercase()
        val attributes = mutableMapOf<String, String>()

        if (parts.size > 1) {
            val attrString = parts[1]
            val regex = Regex("([a-zA-Z0-9_-]+)\\s*=\\s*(?:\"([^\"]*)\"|'([^']*)'|([^\\s>]+))")
            val matches = regex.findAll(attrString)
            for (match in matches) {
                val key = match.groupValues[1].lowercase()
                val value = match.groupValues[2].ifEmpty {
                    match.groupValues[3].ifEmpty { match.groupValues[4] }
                }
                attributes[key] = value
            }
        }

        return Pair(tagName, attributes)
    }

    private fun getDebugBoxColor(tagName: String): Color {
        return when (tagName.lowercase()) {
            "h1", "h2", "h3", "h4", "h5", "h6" -> Color(0x332196F3) // Subtle Blue
            "p", "div", "section", "article" -> Color(0x264CAF50) // Subtle Green
            "a" -> Color(0x33FF9800) // Subtle Orange
            "code", "tt", "pre" -> Color(0x339C27B0) // Subtle Purple
            "b", "strong", "i", "em", "u", "s", "span" -> Color(0x33FFEB3B) // Subtle Yellow
            "ul", "ol", "li" -> Color(0x2600BCD4) // Subtle Cyan
            "blockquote" -> Color(0x26795548) // Subtle Brown
            else -> Color(0x269E9E9E) // Subtle Grey
        }
    }

    private fun getBuiltInParagraphStyle(tagName: String): ParagraphStyle? {
        return when (tagName) {
            "h1" -> ParagraphStyle(lineHeight = 2.6.em)
            "h2" -> ParagraphStyle(lineHeight = 1.95.em)
            "h3" -> ParagraphStyle(lineHeight = 1.52.em)
            "h4" -> ParagraphStyle(lineHeight = 1.3.em)
            "h5" -> ParagraphStyle(lineHeight = 1.1.em)
            "h6" -> ParagraphStyle(lineHeight = 0.9.em)
            else -> null
        }
    }

    private fun getBuiltInTagStyle(tagName: String, attributes: Map<String, String>): SpanStyle? {
        return when (tagName) {
            "b", "strong" -> SpanStyle(fontWeight = FontWeight.Bold)
            "i", "em" -> SpanStyle(fontStyle = FontStyle.Italic)
            "u" -> SpanStyle(textDecoration = TextDecoration.Underline)
            "s", "strike", "del" -> SpanStyle(textDecoration = TextDecoration.LineThrough)
            "code", "tt" -> SpanStyle(fontFamily = FontFamily.Monospace, background = Color(0x1F888888))
            "blockquote" -> SpanStyle(fontStyle = FontStyle.Italic, background = Color(0x0D000000))
            "sub" -> SpanStyle(baselineShift = BaselineShift.Subscript, fontSize = 0.8.em)
            "sup" -> SpanStyle(baselineShift = BaselineShift.Superscript, fontSize = 0.8.em)
            "small" -> SpanStyle(fontSize = 0.8.em)
            "big" -> SpanStyle(fontSize = 1.25.em)
            "h1" -> SpanStyle(fontWeight = FontWeight.Bold, fontSize = 2.0.em)
            "h2" -> SpanStyle(fontWeight = FontWeight.Bold, fontSize = 1.5.em)
            "h3" -> SpanStyle(fontWeight = FontWeight.Bold, fontSize = 1.17.em)
            "h4" -> SpanStyle(fontWeight = FontWeight.Bold, fontSize = 1.0.em)
            "h5" -> SpanStyle(fontWeight = FontWeight.Bold, fontSize = 0.83.em)
            "h6" -> SpanStyle(fontWeight = FontWeight.Bold, fontSize = 0.67.em)
            "font" -> {
                var color: Color = Color.Unspecified
                var fontSize = 0.sp
                attributes["color"]?.let { colorStr ->
                    CssStyleParser.parseColor(colorStr)?.let { color = it }
                }
                attributes["size"]?.let { sizeStr ->
                    val sizeInt = sizeStr.toIntOrNull()
                    if (sizeInt != null) {
                        fontSize = when (sizeInt) {
                            1 -> 10.sp
                            2 -> 12.sp
                            3 -> 14.sp
                            4 -> 18.sp
                            5 -> 24.sp
                            6 -> 32.sp
                            7 -> 48.sp
                            else -> 14.sp
                        }
                    }
                }
                SpanStyle(
                    color = color,
                    fontSize = if (fontSize.value > 0) fontSize else TextUnit.Unspecified
                )
            }
            else -> null
        }
    }

    private fun decodeHtmlEntity(entity: String): String? {
        return when (entity) {
            "&amp;" -> "&"
            "&lt;" -> "<"
            "&gt;" -> ">"
            "&quot;" -> "\""
            "&#39;", "&apos;" -> "'"
            "&nbsp;" -> "\u00A0"
            "&copy;" -> "©"
            "&reg;" -> "®"
            "&euro;" -> "€"
            else -> {
                if (entity.startsWith("&#x") || entity.startsWith("&#X")) {
                    val hex = entity.substring(3, entity.length - 1)
                    val codePoint = hex.toIntOrNull(16)
                    if (codePoint != null) String(Character.toChars(codePoint)) else null
                } else if (entity.startsWith("&#")) {
                    val dec = entity.substring(2, entity.length - 1)
                    val codePoint = dec.toIntOrNull(10)
                    if (codePoint != null) String(Character.toChars(codePoint)) else null
                } else null
            }
        }
    }
}
