package com.snijsure.omdbsearch.data

import com.google.gson.annotations.SerializedName


data class MovieDetail(
    @SerializedName("Title") var title: String,
    @SerializedName("Director") var director: String,
    @SerializedName("Year") var year: String,
    @SerializedName("Plot") var plot: String,
    @SerializedName("Poster") var poster: String
)