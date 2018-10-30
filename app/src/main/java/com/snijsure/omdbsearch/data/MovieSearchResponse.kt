package com.snijsure.omdbsearch.data

import com.google.gson.annotations.SerializedName

data class MovieSearchResponse(
    @SerializedName("Search") val movieSearchResults: List<Movie>,
    @SerializedName("totalResults") val totalResults: Int,
    @SerializedName("Response") val responseStatus: String
)