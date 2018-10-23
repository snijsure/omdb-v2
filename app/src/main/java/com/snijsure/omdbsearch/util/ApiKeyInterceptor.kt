package com.snijsure.omdbsearch.util

import com.snijsure.omdbsearch.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response

import java.io.IOException

// Adds apikey and client id to request.
class ApiKeyInterceptor : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val originalHttpUrl = original.url()

        val url = originalHttpUrl.newBuilder()
            .addQueryParameter("i", BuildConfig.OMDB_API_APP_ID)
            .addQueryParameter("apikey", BuildConfig.OMDB_API_KEY)
            .addQueryParameter("plot","full")
            .build()

        val requestBuilder = original.newBuilder()
            .url(url)

        val request = requestBuilder.build()
        return chain.proceed(request)
    }
}
