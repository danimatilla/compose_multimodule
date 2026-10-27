# Core UI Module (`:core:ui`)

> **Jetpack Compose Design System, HTML Text Helper, Common UI Components & Screen Templates.**

The **`:core:ui`** module provides reusable Jetpack Compose components, themes, design system typography, image loading utilities, and a native HTML text parser and renderer with support for design system tokens and inline CSS.

---

## 📋 Table of Contents

- [Overview](#overview)
- [HtmlText Component](#htmltext-component)
  - [Key Features](#key-features)
  - [Usage Examples](#usage-examples)
  - [External Design Tokens (`HtmlTagStyles`)](#external-design-tokens-htmltagstyles)
  - [Inline CSS Styling](#inline-css-styling)
  - [Debug & Inspection Mode](#debug--inspection-mode)
- [UI Components](#ui-components)
  - [SeedTopAppBar](#seedtopappbar)
  - [BottomBar](#bottombar)
  - [SeedImage](#seedimage)
  - [WebViewScreen](#webviewscreen)
- [Theme & Design Tokens](#theme--design-tokens)

---

## Overview

The `:core:ui` module serves as the central Design System and Component Library for the multi-module Android project.

---

## HtmlText Component

`HtmlText` is a native Jetpack Compose component (`com.dxmxp.ui.components.html.HtmlText`) designed to parse and render HTML strings directly into Compose `AnnotatedString` without `AndroidView` overhead.

### Key Features
- 🚀 **100% Native Compose Rendering**: Uses `AnnotatedString` and `Text` (zero `AndroidView` / `TextView` inflation overhead).
- 🎨 **External Design Tokens (`HtmlTagStyles`)**: Map HTML tags (`h1`, `p`, `code`, etc.) to `MaterialTheme.typography` tokens globally via `LocalHtmlTagStyles`.
- 🎨 **Inline CSS Style Parsing**: Supports `style="color: #ff0000; font-weight: bold; background-color: yellow; font-size: 16sp;"`.
- 🔗 **Interactive Links (`<a href="...">`)**: Native Compose `LinkAnnotation.Url` with configurable `onLinkClick` and high-contrast, WCAG AAA compliant link colors in Light and Dark themes.
- 🐛 **Visual Inspector / Debug Mode**: Toggle `debug = true` or `LocalHtmlDebug` to visualize paragraph endings (`¶`), element bounding boxes, and container bounds.

---

### Usage Examples

#### 1. Standard Default Usage
Applies default HTML tag styling out-of-the-box (`h1` = bold 2.0em, `p` = body text, `b` = bold, `i` = italic, `u` = underline, etc.):

```kotlin
import com.dxmxp.ui.components.html.HtmlText

@Composable
fun ArticleScreen() {
    HtmlText(
        html = """
            <h1>Welcome to Compose</h1>
            <p>This is a paragraph with <b>bold</b>, <i>italic</i>, and <u>underline</u> text.</p>
            <p style="color: #E91E63; font-weight: bold;">Styled with inline CSS.</p>
            <p><a href="https://kotlinlang.org">Kotlin Language</a></p>
        """.trimIndent()
    )
}
```

#### 2. External Design Token Mapping (`LocalHtmlTagStyles`)
Provide global tag-to-typography mappings at the Theme or Screen level without passing parameters to every `HtmlText`:

```kotlin
val appHtmlTagStyles = HtmlTagStyles(
    tagStyles = mapOf(
        "h1" to MaterialTheme.typography.headlineLarge, // TitleXL token
        "p" to MaterialTheme.typography.bodyMedium
    ),
    tagResolver = { tagName ->
        // Dynamic resolver for arbitrary tags coming in at runtime
        if (tagName.contains("title")) MaterialTheme.typography.headlineMedium else null
    }
)

CompositionLocalProvider(LocalHtmlTagStyles provides appHtmlTagStyles) {
    // Automatically applies the external tag styles
    HtmlText(html = "<h1>Title with TitleXL</h1><p>Body paragraph</p>")
}
```

#### 3. Custom Link Interception (`onLinkClick`)
```kotlin
HtmlText(
    html = "<p>Visit <a href=\"app://stories/detail?id=123\">Story #123</a></p>",
    onLinkClick = { url ->
        // Intercept link click
        navigator.push(DeepLinkRouter.resolve(Uri.parse(url)))
    }
)
```

#### 4. Debug Mode Inspection
```kotlin
HtmlText(
    html = "<h1>Debug Title</h1><p>Paragraph text.</p>",
    debug = true // Enables pilcrow symbol ¶ at paragraph ends and element box tints
)
```

---

## UI Components

### `SeedTopAppBar`
Standardized top app bar with navigation back button, title, and action icons.

### `BottomBar`
Main navigation bar supporting badge counts and active tab highlighting.

### `SeedImage`
Coil 3 image composable with built-in shimmer loading state and error placeholders.

### `WebViewScreen`
Full-featured WebView screen supporting progress bar, title sync, and back button handling.

---

## Module Structure

```
core/ui/
├── src/main/java/com/dxmxp/ui/
│   ├── components/
│   │   ├── html/
│   │   │   ├── HtmlText.kt            # Main Composable HtmlText
│   │   │   ├── HtmlTagStyles.kt       # Design tokens & LocalHtmlTagStyles
│   │   │   ├── HtmlConfig.kt          # Unified HtmlConfig object
│   │   │   ├── HtmlTextHelper.kt      # Extension functions & String.parseHtml()
│   │   │   ├── HtmlParser.kt          # Native HTML & Entity parser engine
│   │   │   ├── CssStyleParser.kt      # Inline CSS style="..." parser
│   │   │   └── LocalHtmlDebug.kt      # LocalHtmlDebug CompositionLocal
│   │   ├── BottomBar.kt
│   │   └── SeedTopAppBar.kt
│   ├── common/
│   │   ├── SeedImage.kt
│   │   └── NavigationExtensions.kt
│   ├── screens/
│   │   └── WebView.kt
│   └── theme/
│       ├── Color.kt
│       ├── Theme.kt
│       └── Type.kt
└── src/test/java/com/dxmxp/ui/
    └── components/html/
        └── HtmlTextHelperTest.kt      # Unit test suite
```

---

**Maintained By:** Core UI Team
