package com.dxmxp.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import coil3.ImageLoader
import coil3.SingletonImageLoader
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade

/**
 * A reusable image component that handles local and remote images using Coil 3.
 * It uses the singleton ImageLoader configured in the Application class,
 * which is integrated with the project's OkHttpClient and AuthInterceptor.
 *
 * @param model The data source for the image (URL, resource ID, File, etc.).
 * @param contentDescription Text used by accessibility services.
 * @param modifier Modifier for the image.
 * @param placeholder A [Painter] to be used while the image is loading.
 * @param error A [Painter] to be used if the image fails to load.
 * @param contentScale How to scale the image.
 * @param filterQuality Sampling algorithm used when drawing the image.
 */
@Composable
fun SeedImage(
    model: Any?,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    placeholder: Painter? = rememberVectorPainter(Icons.Default.Refresh),
    error: Painter? = rememberVectorPainter(Icons.Default.Info),
    contentScale: ContentScale = ContentScale.Fit,
    filterQuality: FilterQuality = FilterQuality.Low,
) {
    val context = LocalContext.current
    val isPreview = LocalInspectionMode.current

    val imageLoader = if (isPreview) {
        ImageLoader.Builder(context).build()
    } else {
        SingletonImageLoader.get(context)
    }

    val imageRequest = ImageRequest.Builder(context)
        .data(model)
        .crossfade(true)
        .build()

    AsyncImage(
        model = imageRequest,
        imageLoader = imageLoader,
        contentDescription = contentDescription,
        modifier = modifier,
        placeholder = placeholder,
        error = error,
        contentScale = contentScale,
        filterQuality = filterQuality
    )
}

/**
 * A basic placeholder to be used while images are loading or failed.
 */
@Composable
fun ImagePlaceholder(
    modifier: Modifier = Modifier,
    iconColor: Color = MaterialTheme.colorScheme.outline,
    backgroundColor: Color = MaterialTheme.colorScheme.surfaceVariant
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Refresh,
            contentDescription = null,
            tint = iconColor
        )
    }
}
