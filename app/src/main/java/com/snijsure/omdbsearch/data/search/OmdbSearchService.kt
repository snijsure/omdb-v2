package com.snijsure.omdbsearch.data.search

import com.snijsure.omdbsearch.data.MovieDetail
import com.snijsure.omdbsearch.data.MovieSearchResponse
import kotlinx.coroutines.Deferred
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface OmdbSearchService {

    @GET("/")
    fun searchDeferred(
        @Query("s") searchTerm: String,
        @Query("page") pageNumber: Int
    ): Deferred<Response<MovieSearchResponse>>

    @GET("/")
    fun movieDetailDeferred(
        @Query("i") movieId: String,
        @Query("plot") plotType: String = "short"
    ): Deferred<Response<MovieDetail>>

    companion object {

        const val ENDPOINT = "https://www.omdbapi.com/"
    }
}