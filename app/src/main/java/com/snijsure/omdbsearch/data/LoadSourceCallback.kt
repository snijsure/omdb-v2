package com.snijsure.omdbsearch.data

interface LoadSourceCallback {

    fun sourceLoaded(result: List<Movie>?)

    fun loadFailed(reason: String)
}