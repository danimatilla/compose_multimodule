package com.dxmxp.ui.screens

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.viewinterop.AndroidView
import com.dxmxp.ui.navigation.core.LocalNavigator
import com.dxmxp.ui.navigation.model.Screen
import kotlinx.serialization.Serializable
import android.webkit.WebView as NativeWebView

@Serializable
data class WebView(val url: String, val title: String? = null) : Screen {
    override val route: String get() = "/webview"
    override val showMainBottomBar: Boolean get() = false
}

/**
 * A full WebView screen with top bar, progress indicator and back handling.
 */
@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("SetJavaScriptEnabled")
@Composable
fun WebViewScreen(
    screen: WebView,
    modifier: Modifier = Modifier,
) {
    val navigator = LocalNavigator.current
    var webViewInstance by remember { mutableStateOf<NativeWebView?>(null) }
    var canGoBack by remember { mutableStateOf(false) }
    val currentTitleState = remember { mutableStateOf(screen.title.orEmpty()) }

    BackHandler {
        if (canGoBack) {
            webViewInstance?.goBack()
        } else {
            navigator.pop()
        }
    }

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        TopAppBar(currentTitleState, canGoBack, webViewInstance)
        AndroidView(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            factory = { context ->
                NativeWebView(context).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT,
                    )
                    settings.apply {
                        javaScriptEnabled = true
                        domStorageEnabled = true
                        loadWithOverviewMode = true
                        useWideViewPort = true
                        builtInZoomControls = true
                        displayZoomControls = false
                    }

                    webViewClient = object : WebViewClient() {
                        override fun onPageStarted(
                            view: NativeWebView?,
                            url: String?,
                            favicon: Bitmap?
                        ) {
                            canGoBack = view?.canGoBack() ?: false
                        }

                        override fun onPageFinished(view: NativeWebView?, url: String?) {
                            canGoBack = view?.canGoBack() ?: false
                            if (screen.title == null) {
                                val newTitle = view?.title.orEmpty()
                                if (newTitle.isNotEmpty()) {
                                    currentTitleState.value = newTitle
                                }
                            }
                        }

                        override fun doUpdateVisitedHistory(
                            view: NativeWebView?,
                            url: String?,
                            isReload: Boolean
                        ) {
                            super.doUpdateVisitedHistory(view, url, isReload)
                            canGoBack = view?.canGoBack() ?: false
                        }
                    }
                    webChromeClient = object : WebChromeClient() {
                        override fun onReceivedTitle(view: NativeWebView?, title: String?) {
                            super.onReceivedTitle(view, title)
                            if (screen.title == null && !title.isNullOrEmpty()) {
                                currentTitleState.value = title
                            }
                        }
                    }
                    loadUrl(screen.url)
                    webViewInstance = this
                }
            },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopAppBar(
    currentTitleState: MutableState<String>,
    canGoBack: Boolean,
    webViewInstance: NativeWebView?,
) {
    val navigator = LocalNavigator.current
    TopAppBar(
        title = {
            Text(
                text = currentTitleState.value.ifEmpty { "Loading..." },
                style = MaterialTheme.typography.titleLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        },
        navigationIcon = {
            IconButton(
                onClick = {
                    if (canGoBack) {
                        webViewInstance?.goBack()
                    } else {
                        navigator.pop()
                    }
                },
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                )
            }
        },
    )
}
