package com.dxmxp.ui.components.html

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp

/**
 * Utility for parsing CSS inline `style="..."` attributes into Compose [SpanStyle], [ParagraphStyle], and [Color].
 */
internal object CssStyleParser {

    private val NAMED_COLORS = mapOf(
        "black" to Color(0xFF000000),
        "white" to Color(0xFFFFFFFF),
        "red" to Color(0xFFFF0000),
        "green" to Color(0xFF008000),
        "lime" to Color(0xFF00FF00),
        "blue" to Color(0xFF0000FF),
        "yellow" to Color(0xFFFFFF00),
        "cyan" to Color(0xFF00FFFF),
        "aqua" to Color(0xFF00FFFF),
        "magenta" to Color(0xFFFF00FF),
        "fuchsia" to Color(0xFFFF00FF),
        "gray" to Color(0xFF808080),
        "grey" to Color(0xFF808080),
        "lightgray" to Color(0xFFD3D3D3),
        "lightgrey" to Color(0xFFD3D3D3),
        "darkgray" to Color(0xFFA9A9A9),
        "darkgrey" to Color(0xFFA9A9A9),
        "silver" to Color(0xFFC0C0C0),
        "orange" to Color(0xFFFFA500),
        "purple" to Color(0xFF800080),
        "pink" to Color(0xFFFFC0CB),
        "brown" to Color(0xFFA52A2A),
        "maroon" to Color(0xFF800000),
        "navy" to Color(0xFF000080),
        "teal" to Color(0xFF008080),
        "olive" to Color(0xFF808000),
        "transparent" to Color.Transparent
    )

    /**
     * Parses an inline CSS string e.g. "color: red; font-weight: bold; font-size: 16sp;" into a [SpanStyle].
     */
    fun parseSpanStyle(cssString: String): SpanStyle {
        if (cssString.isBlank()) return SpanStyle()

        var color: Color = Color.Unspecified
        var background: Color = Color.Unspecified
        var fontWeight: FontWeight? = null
        var fontStyle: FontStyle? = null
        var textDecoration: TextDecoration? = null
        var fontSize: TextUnit = TextUnit.Unspecified
        var fontFamily: FontFamily? = null
        var baselineShift: BaselineShift? = null

        val declarations = cssString.split(';')
        for (declaration in declarations) {
            val parts = declaration.split(':', limit = 2)
            if (parts.size != 2) continue

            val key = parts[0].trim().lowercase()
            val value = parts[1].trim()

            when (key) {
                "color" -> {
                    parseColor(value)?.let { color = it }
                }
                "background", "background-color" -> {
                    parseColor(value)?.let { background = it }
                }
                "font-weight" -> {
                    fontWeight = parseFontWeight(value)
                }
                "font-style" -> {
                    fontStyle = parseFontStyle(value)
                }
                "text-decoration", "text-decoration-line" -> {
                    textDecoration = parseTextDecoration(value)
                }
                "font-size" -> {
                    fontSize = parseFontSize(value)
                }
                "font-family" -> {
                    fontFamily = parseFontFamily(value)
                }
                "vertical-align", "baseline-shift" -> {
                    baselineShift = parseBaselineShift(value)
                }
            }
        }

        return SpanStyle(
            color = color,
            fontSize = fontSize,
            fontWeight = fontWeight,
            fontStyle = fontStyle,
            fontFamily = fontFamily,
            background = background,
            baselineShift = baselineShift,
            textDecoration = textDecoration
        )
    }

    /**
     * Parses paragraph alignment styles from CSS inline style.
     */
    fun parseParagraphStyle(cssString: String): ParagraphStyle? {
        if (cssString.isBlank()) return null

        val declarations = cssString.split(';')
        for (declaration in declarations) {
            val parts = declaration.split(':', limit = 2)
            if (parts.size != 2) continue

            val key = parts[0].trim().lowercase()
            val value = parts[1].trim().lowercase()

            if (key == "text-align") {
                val textAlign = when (value) {
                    "center" -> TextAlign.Center
                    "right", "end" -> TextAlign.Right
                    "left", "start" -> TextAlign.Left
                    "justify" -> TextAlign.Justify
                    else -> null
                }
                if (textAlign != null) {
                    return ParagraphStyle(textAlign = textAlign)
                }
            }
        }
        return null
    }

    /**
     * Parses CSS color string into Compose [Color].
     */
    fun parseColor(colorStr: String): Color? {
        val trimmed = colorStr.trim().lowercase()
        if (trimmed.isEmpty()) return null

        NAMED_COLORS[trimmed]?.let { return it }

        if (trimmed.startsWith("#")) {
            val hex = trimmed.substring(1)
            return when (hex.length) {
                3 -> { // #RGB -> #RRGGBB
                    val r = hex[0].toString().repeat(2)
                    val g = hex[1].toString().repeat(2)
                    val b = hex[2].toString().repeat(2)
                    parseHexColor("FF$r$g$b")
                }
                4 -> { // #RGBA -> #AARRGGBB
                    val r = hex[0].toString().repeat(2)
                    val g = hex[1].toString().repeat(2)
                    val b = hex[2].toString().repeat(2)
                    val a = hex[3].toString().repeat(2)
                    parseHexColor("$a$r$g$b")
                }
                6 -> parseHexColor("FF$hex") // #RRGGBB
                8 -> { // #RRGGBBAA -> #AARRGGBB
                    val rrggbb = hex.substring(0, 6)
                    val aa = hex.substring(6, 8)
                    parseHexColor("$aa$rrggbb")
                }
                else -> null
            }
        }

        if (trimmed.startsWith("rgb")) {
            val inner = trimmed.substringAfter("(").substringBefore(")").trim()
            val components = inner.split(',').map { it.trim() }
            if (components.size >= 3) {
                val r = components[0].toIntOrNull()?.coerceIn(0, 255) ?: return null
                val g = components[1].toIntOrNull()?.coerceIn(0, 255) ?: return null
                val b = components[2].toIntOrNull()?.coerceIn(0, 255) ?: return null
                val a = if (components.size >= 4) {
                    val alphaStr = components[3]
                    if (alphaStr.endsWith("%")) {
                        (alphaStr.dropLast(1).toFloatOrNull() ?: 100f) / 100f
                    } else {
                        alphaStr.toFloatOrNull() ?: 1.0f
                    }
                } else 1.0f

                return Color(red = r, green = g, blue = b, alpha = (a * 255).toInt().coerceIn(0, 255))
            }
        }

        return null
    }

    private fun parseHexColor(hexString: String): Color? {
        return try {
            val colorInt = hexString.toLong(16).toInt()
            Color(colorInt)
        } catch (_: Exception) {
            null
        }
    }

    private fun parseFontWeight(value: String): FontWeight? {
        val trimmed = value.lowercase()
        return when (trimmed) {
            "bold", "bolder" -> FontWeight.Bold
            "normal", "lighter" -> FontWeight.Normal
            else -> {
                val weightNum = trimmed.toIntOrNull()
                if (weightNum != null && weightNum in 100..900) {
                    FontWeight(weightNum)
                } else null
            }
        }
    }

    private fun parseFontStyle(value: String): FontStyle? {
        return when (value.lowercase()) {
            "italic", "oblique" -> FontStyle.Italic
            "normal" -> FontStyle.Normal
            else -> null
        }
    }

    private fun parseTextDecoration(value: String): TextDecoration? {
        val lower = value.lowercase()
        val hasUnderline = lower.contains("underline")
        val hasLineThrough = lower.contains("line-through") || lower.contains("strikethrough")

        return when {
            hasUnderline && hasLineThrough -> TextDecoration.combine(
                listOf(TextDecoration.Underline, TextDecoration.LineThrough)
            )
            hasUnderline -> TextDecoration.Underline
            hasLineThrough -> TextDecoration.LineThrough
            lower.contains("none") -> TextDecoration.None
            else -> null
        }
    }

    private fun parseFontSize(value: String): TextUnit {
        val lower = value.lowercase().trim()
        return when {
            lower.endsWith("sp") -> (lower.dropLast(2).toFloatOrNull() ?: return TextUnit.Unspecified).sp
            lower.endsWith("px") -> (lower.dropLast(2).toFloatOrNull() ?: return TextUnit.Unspecified).sp
            lower.endsWith("pt") -> ((lower.dropLast(2).toFloatOrNull() ?: return TextUnit.Unspecified) * 1.33f).sp
            lower.endsWith("em") -> (lower.dropLast(2).toFloatOrNull() ?: return TextUnit.Unspecified).em
            lower.endsWith("%") -> ((lower.dropLast(1).toFloatOrNull() ?: return TextUnit.Unspecified) / 100f).em
            lower == "xx-small" -> 0.6.em
            lower == "x-small" -> 0.75.em
            lower == "small" -> 0.875.em
            lower == "medium" -> 1.0.em
            lower == "large" -> 1.2.em
            lower == "x-large" -> 1.5.em
            lower == "xx-large" -> 2.0.em
            else -> {
                val num = lower.toFloatOrNull()
                if (num != null) num.sp else TextUnit.Unspecified
            }
        }
    }

    private fun parseFontFamily(value: String): FontFamily? {
        val lower = value.lowercase().trim('\'', '"', ' ')
        return when {
            lower.contains("monospace") -> FontFamily.Monospace
            lower.contains("serif") && !lower.contains("sans-serif") -> FontFamily.Serif
            lower.contains("sans-serif") -> FontFamily.SansSerif
            lower.contains("cursive") -> FontFamily.Cursive
            else -> null
        }
    }

    private fun parseBaselineShift(value: String): BaselineShift? {
        val lower = value.lowercase()
        return when {
            lower.contains("sub") -> BaselineShift.Subscript
            lower.contains("super") -> BaselineShift.Superscript
            else -> null
        }
    }
}
