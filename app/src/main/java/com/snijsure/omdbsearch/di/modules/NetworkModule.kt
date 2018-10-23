package com.snijsure.omdbsearch.di.modules

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.snijsure.omdbsearch.BuildConfig
import com.snijsure.omdbsearch.data.search.OmdbSearchService
import com.snijsure.omdbsearch.util.ApiKeyInterceptor
import com.squareup.moshi.KotlinJsonAdapterFactory
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.Exception
import javax.inject.Singleton


@Module
class NetworkModule() {

    @Provides
    fun provideImdbSearchService(): OmdbSearchService =
        provideRetrofit(OmdbSearchService.ENDPOINT).create(OmdbSearchService::class.java)

    @Provides
    @Singleton
    fun providesMoshi(): Moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()


    @Provides
    fun provideRetrofitBuilder() = Retrofit.Builder()

    @Provides
    fun provideRetrofit(
        baseUrl: String
    ): Retrofit {
        return provideRetrofitBuilder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .client(provideOkHttpClient(provideLoggingInterceptor(), provideApiKeyInterceptor()))
            .build()
    }


    @Provides
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }
    }

    @Provides
    fun provideApiKeyInterceptor(): ApiKeyInterceptor {
        return ApiKeyInterceptor()
    }

    @Provides
    fun provideOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor,
        apiKeyInterceptor: ApiKeyInterceptor
    ): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(apiKeyInterceptor)
            .build()

    @Provides
    fun provideConnectivityManager(application : Application) : ConnectivityManager? {
        return try {
            application.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        } catch(e: Exception) {
            null
        }
    }

    @SuppressLint("MissingPermission")
    @Provides
    fun provideNetworkCapabilities(connectivityManager: ConnectivityManager?) : NetworkCapabilities? {
        return try {
            connectivityManager?.getNetworkCapabilities(connectivityManager.activeNetwork)
        } catch(e: Exception) {
            null
        }
    }
}
