package com.example.data.di

import com.example.data.BuildConfig
import com.example.data.remote.api.SpaceXApi
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

    @Retention(AnnotationRetention.RUNTIME)
    @Qualifier
    annotation class ShipApi

    @Retention(AnnotationRetention.RUNTIME)
    @Qualifier
    annotation class RocketApi

    @RocketApi
    @Provides
    fun provideRocketApi(
        retrofit: Retrofit
    ): SpaceXApi = retrofit.retrofitBuilder()
        .create(SpaceXApi.Rockets::class.java)

    @ShipApi
    @Provides
    fun provideShipApi(
        retrofit: Retrofit
    ): SpaceXApi = retrofit.retrofitBuilder()
        .create(SpaceXApi.Ships::class.java)

    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient
    ): Retrofit {
        val json = Json {
            ignoreUnknownKeys = true // Ignore unknown JSON fields.
        }
        val contentType = "application/json".toMediaType()

        return Retrofit.Builder()
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(
                HttpLoggingInterceptor()
                    .apply {
                        if (BuildConfig.DEBUG) {
                            level = HttpLoggingInterceptor.Level.BODY
                        }
                    }
            )
            .build()

    private fun Retrofit.retrofitBuilder() =
        newBuilder()
            .baseUrl("https://api.spacexdata.com")
            .build()
}
