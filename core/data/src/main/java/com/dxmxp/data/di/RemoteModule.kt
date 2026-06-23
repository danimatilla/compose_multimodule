package com.dxmxp.data.di

import com.dxmxp.data.BuildConfig
import com.dxmxp.data.remote.api.PunkapiApi
import com.dxmxp.data.remote.interceptor.AuthInterceptor
import com.dxmxp.data.remote.interceptor.ErrorInterceptor
import com.dxmxp.domain.Constants.BASE_URL
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
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        authInterceptor: AuthInterceptor,
        errorInterceptor: ErrorInterceptor
    ): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .addInterceptor(errorInterceptor)
        .addInterceptor(
            HttpLoggingInterceptor()
                .apply {
                    level = if (BuildConfig.DEBUG) {
                        HttpLoggingInterceptor.Level.HEADERS
                    } else {
                        HttpLoggingInterceptor.Level.NONE
                    }
                }
        )
        .build()

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class PunkapiRetrofit
}
