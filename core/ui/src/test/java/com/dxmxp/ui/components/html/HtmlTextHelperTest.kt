package com.dxmxp.ui.components.html

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class HtmlTextHelperTest {

    @Test
    fun `parseCssColor parses named colors correctly`() {
        assertEquals(Color(0xFFFF0000), HtmlTextHelper.parseCssColor("red") as Any)
        assertEquals(Color(0xFF00FF00), HtmlTextHelper.parseCssColor("lime") as Any)
        assertEquals(Color(0xFF0000FF), HtmlTextHelper.parseCssColor("blue") as Any)
        assertEquals(Color(0xFF000000), HtmlTextHelper.parseCssColor("black") as Any)
        assertEquals(Color(0xFFFFFFFF), HtmlTextHelper.parseCssColor("white") as Any)
        assertEquals(Color.Transparent, HtmlTextHelper.parseCssColor("transparent") as Any)
    }

    @Test
    fun `parseCssColor parses hex colors correctly`() {
        assertEquals(Color(0xFFFF0000), HtmlTextHelper.parseCssColor("#f00") as Any)
        assertEquals(Color(0xFFFF0000), HtmlTextHelper.parseCssColor("#ff0000") as Any)
        assertEquals(Color(0x80FF0000), HtmlTextHelper.parseCssColor("#ff000080") as Any)
    }

    @Test
    fun `parseCssColor parses rgb and rgba colors correctly`() {
        assertEquals(Color(red = 255, green = 0, blue = 0, alpha = 255), HtmlTextHelper.parseCssColor("rgb(255, 0, 0)") as Any)
        assertEquals(Color(red = 0, green = 128, blue = 255, alpha = 127), HtmlTextHelper.parseCssColor("rgba(0, 128, 255, 0.5)") as Any)
    }

    @Test
    fun `parseCssStyle parses font weight, style and decoration correctly`() {
        val css = "font-weight: bold; font-style: italic; text-decoration: underline;"
        val spanStyle = HtmlTextHelper.parseCssStyle(css)

        assertEquals(FontWeight.Bold, spanStyle.fontWeight)
        assertEquals(FontStyle.Italic, spanStyle.fontStyle as Any)
        assertEquals(TextDecoration.Underline, spanStyle.textDecoration)
    }

    @Test
    fun `parseCssStyle parses font size and family correctly`() {
        val css = "font-size: 18sp; font-family: monospace;"
        val spanStyle = HtmlTextHelper.parseCssStyle(css)

        assertEquals(18.sp, spanStyle.fontSize)
        assertEquals(FontFamily.Monospace, spanStyle.fontFamily)
    }

    @Test
    fun `parseHtml converts basic tags into annotated string`() {
        val html = "Hello <b>World</b> and <i>Compose</i>"
        val annotatedString = HtmlTextHelper.parseHtml(html)

        assertEquals("Hello World and Compose", annotatedString.text)

        val styles = annotatedString.spanStyles
        assertEquals(2, styles.size)

        // "World" -> bold
        assertEquals(FontWeight.Bold, styles[0].item.fontWeight)
        assertEquals(6, styles[0].start)
        assertEquals(11, styles[0].end)

        // "Compose" -> italic
        assertEquals(FontStyle.Italic, styles[1].item.fontStyle as Any)
        assertEquals(16, styles[1].start)
        assertEquals(23, styles[1].end)
    }

    @Test
    fun `parseHtml handles html entities correctly`() {
        val html = "A &amp; B &lt; C &gt; D &quot;E&quot; &#39;F&#39;"
        val annotatedString = HtmlTextHelper.parseHtml(html)

        assertEquals("A & B < C > D \"E\" 'F'", annotatedString.text)
    }

    @Test
    fun `parseHtml parses links and attaches URL annotations`() {
        val html = "Click <a href=\"https://kotlinlang.org\">here</a> for Kotlin"
        val annotatedString = HtmlTextHelper.parseHtml(html)

        assertEquals("Click here for Kotlin", annotatedString.text)

        val urlAnnotations = annotatedString.getStringAnnotations(tag = "URL", start = 0, end = annotatedString.length)
        assertEquals(1, urlAnnotations.size)
        assertEquals("https://kotlinlang.org", urlAnnotations[0].item)
        assertEquals(6, urlAnnotations[0].start)
        assertEquals(10, urlAnnotations[0].end)
    }

    @Test
    fun `parseHtml applies design tokens from HtmlTagStyles`() {
        val h1Token = TextStyle(fontSize = 28.sp, fontWeight = FontWeight.ExtraBold)
        val pToken = TextStyle(fontSize = 14.sp)

        val config = HtmlTagStyles(
            tagStyles = mapOf(
                "h1" to h1Token,
                "p" to pToken
            )
        )

        val html = "<h1>Title</h1><p>Paragraph</p>"
        val annotatedString = HtmlTextHelper.parseHtml(html, tagStyles = config)

        val styles = annotatedString.spanStyles
        assertNotNull(styles.find { it.item.fontSize == 28.sp && it.item.fontWeight == FontWeight.ExtraBold })
        assertNotNull(styles.find { it.item.fontSize == 14.sp })
    }

    @Test
    fun `parseHtml applies tagResolver for dynamic tags`() {
        val dynamicToken = TextStyle(fontSize = 20.sp, color = Color.Red)
        val config = HtmlTagStyles(
            tagResolver = { tagName ->
                if (tagName == "custom-heading") dynamicToken else null
            }
        )

        val html = "<custom-heading>Dynamic Text</custom-heading>"
        val annotatedString = HtmlTextHelper.parseHtml(html, tagStyles = config)

        val styles = annotatedString.spanStyles
        assertEquals(1, styles.size)
        assertEquals(20.sp, styles[0].item.fontSize)
        assertEquals(Color.Red, styles[0].item.color as Any)
    }

    @Test
    fun `parseHtml merges inline CSS over tag default style`() {
        val html = "<p style=\"color: #00FF00;\">Styled Paragraph</p>"
        val annotatedString = HtmlTextHelper.parseHtml(html)

        val styles = annotatedString.spanStyles
        assertNotNull(styles.find { it.item.color == Color(0xFF00FF00) })
    }

    @Test
    fun `parseHtml applies correct heading line height in ParagraphStyle`() {
        val html = "<h1>Heading 1</h1><h2>Heading 2</h2>"
        val annotatedString = HtmlTextHelper.parseHtml(html)

        val paragraphs = annotatedString.paragraphStyles
        assertEquals(2, paragraphs.size)

        // h1 -> 2.5.em line height
        assertEquals(2.5.em, paragraphs[0].item.lineHeight)

        // h2 -> 1.9.em line height
        assertEquals(1.9.em, paragraphs[1].item.lineHeight)
    }

    @Test
    fun `parseHtml applies proportional line height for inline CSS font size`() {
        val html = "<p style=\"font-size: 30sp;\">Large Paragraph</p>"
        val annotatedString = HtmlTextHelper.parseHtml(html)

        val paragraphs = annotatedString.paragraphStyles
        assertEquals(1, paragraphs.size)
        assertEquals(39.sp, paragraphs[0].item.lineHeight)
    }

    @Test
    fun `parseHtml renders debug paragraph ends and element box tints when debug is enabled`() {
        val html = "<p>Line 1</p><p>Line 2</p>"
        val annotatedString = HtmlTextHelper.parseHtml(html, debug = true)

        // Verify pilcrow symbol '¶' is rendered in the text
        assertTrue(annotatedString.text.contains("¶"))

        // Verify background tint is applied for element box
        val styles = annotatedString.spanStyles
        assertNotNull(styles.find { it.item.background == Color(0x264CAF50) }) // Green debug box for paragraph
    }
}
