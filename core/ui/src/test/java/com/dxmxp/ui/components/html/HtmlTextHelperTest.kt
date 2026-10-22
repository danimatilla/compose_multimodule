package com.dxmxp.ui.components.html

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.sp
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class HtmlTextHelperTest {

    @Test
    fun `parseCssColor parses named colors correctly`() {
        assertEquals(Color(0xFFFF0000), HtmlTextHelper.parseCssColor("red"))
        assertEquals(Color(0xFF00FF00), HtmlTextHelper.parseCssColor("lime"))
        assertEquals(Color(0xFF0000FF), HtmlTextHelper.parseCssColor("blue"))
        assertEquals(Color(0xFF000000), HtmlTextHelper.parseCssColor("black"))
        assertEquals(Color(0xFFFFFFFF), HtmlTextHelper.parseCssColor("white"))
        assertEquals(Color.Transparent, HtmlTextHelper.parseCssColor("transparent"))
    }

    @Test
    fun `parseCssColor parses hex colors correctly`() {
        assertEquals(Color(0xFFFF0000), HtmlTextHelper.parseCssColor("#f00"))
        assertEquals(Color(0xFFFF0000), HtmlTextHelper.parseCssColor("#ff0000"))
        assertEquals(Color(0x80FF0000), HtmlTextHelper.parseCssColor("#ff000080"))
    }

    @Test
    fun `parseCssColor parses rgb and rgba colors correctly`() {
        assertEquals(Color(red = 255, green = 0, blue = 0, alpha = 255), HtmlTextHelper.parseCssColor("rgb(255, 0, 0)"))
        assertEquals(Color(red = 0, green = 128, blue = 255, alpha = 127), HtmlTextHelper.parseCssColor("rgba(0, 128, 255, 0.5)"))
    }

    @Test
    fun `parseCssStyle parses font weight, style and decoration correctly`() {
        val css = "font-weight: bold; font-style: italic; text-decoration: underline;"
        val spanStyle = HtmlTextHelper.parseCssStyle(css)

        assertEquals(FontWeight.Bold, spanStyle.fontWeight)
        assertEquals(FontStyle.Italic, spanStyle.fontStyle)
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
        assertEquals(FontStyle.Italic, styles[1].item.fontStyle)
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
        assertEquals(Color.Red, styles[0].item.color)
    }

    @Test
    fun `parseHtml merges inline CSS over tag default style`() {
        val html = "<p style=\"color: #00FF00;\">Styled Paragraph</p>"
        val annotatedString = HtmlTextHelper.parseHtml(html)

        val styles = annotatedString.spanStyles
        assertNotNull(styles.find { it.item.color == Color(0xFF00FF00) })
    }
}
