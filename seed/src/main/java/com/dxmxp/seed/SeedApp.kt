package com.dxmxp.seed

import android.app.Application
import android.content.Context
import coil3.ImageLoader
import coil3.SingletonImageLoader
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class SeedApp : Application(), SingletonImageLoader.Factory {

    @Inject
    lateinit var imageLoader: ImageLoader

    override fun newImageLoader(context: Context): ImageLoader {
        return if (::imageLoader.isInitialized) {
            imageLoader
        } else {
            ImageLoader.Builder(context).build()
        }
    }
}
