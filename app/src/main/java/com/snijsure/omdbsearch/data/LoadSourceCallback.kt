package com.snijsure.omdbsearch.data

interface LoadSourceCallback {

    fun sourceLoaded(result: Any?)

    fun loadFailed(reason: String)
}