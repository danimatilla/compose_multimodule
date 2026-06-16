package com.dxmxp.data.di

import com.dxmxp.data.BuildConfig
import com.dxmxp.data.remote.api.PunkapiApi
import com.dxmxp.data.remote.interceptor.ErrorInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RemoteModule {

    @Provides
    @Singleton
    fun providePunkapiApi(
        @PunkapiRetrofit retrofit: Retrofit
    ): PunkapiApi.Beers = retrofit.create(PunkapiApi.Beers::class.java)

    @Provides
    @Singleton
    @PunkapiRetrofit
    fun provideRetrofit(
        okHttpClient: OkHttpClient
    ): Retrofit {
        val json = Json { ignoreUnknownKeys = true }
        return Retrofit.Builder()
            .baseUrl("https://punkapi-alxiw.amvera.io/v3/")
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        errorInterceptor: ErrorInterceptor
    ): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(errorInterceptor)
        .addInterceptor(
            HttpLoggingInterceptor()
                .apply {
                    if (BuildConfig.DEBUG) {
                        level = HttpLoggingInterceptor.Level.BODY
                    }
                }
        )
        .build()

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class PunkapiRetrofit
}
