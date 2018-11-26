package com.snijsure.omdbsearch.ui.viewmodel

import android.arch.lifecycle.MediatorLiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModel
import com.snijsure.dbrepository.repo.room.DataRepository
import com.snijsure.dbrepository.repo.room.FavoriteEntry
import com.snijsure.omdbsearch.data.LoadSourceCallback
import com.snijsure.omdbsearch.data.Movie
import com.snijsure.omdbsearch.data.MovieSearchResponse
import com.snijsure.omdbsearch.data.search.OmdbSearchService
import com.snijsure.omdbsearch.util.Constants
import com.snijsure.omdbsearch.util.NetworkUtil
import com.snijsure.utility.CoroutinesContextProvider
import com.snijsure.utility.Result
import com.snijsure.utility.safeApiCall
import kotlinx.coroutines.*
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject

class MovieViewModel @Inject constructor(
    private val service: OmdbSearchService,
    private val networkUtil: NetworkUtil,
    private val contextProvider: CoroutinesContextProvider,
    private val dataRepo: DataRepository
) : ViewModel(), LoadSourceCallback {

    val isDataLoading = MutableLiveData<Boolean>().apply {
        this.value = false
    }
    var totalSearchResults = 0
    private val pendingJobs = Job()
    val movieData: MutableLiveData<List<Movie>> = MutableLiveData()
    val favoriteMediator: MediatorLiveData<List<FavoriteEntry>> = MediatorLiveData()

    val dataLoadStatus = MutableLiveData<String>()
    private val coroutineScope = CoroutineScope(contextProvider.io + pendingJobs)

    var pageNumber = 1

    init {
        favoriteMediator.value = null
        favoriteMediator.addSource(dataRepo.getFavoritesLiveData()) { favoriteMediator.setValue(it) }
    }

    @Suppress("UNCHECKED_CAST")
    override fun sourceLoaded(result: Any?) {
        isDataLoading.postValue(false)
        try {
            if (result != null && (result as List<Movie>).isNotEmpty()) {
                pageNumber++
                movieData.postValue(result as List<Movie>?)
            } else {
                dataLoadStatus.postValue(Constants.NO_SEARCH_RESULTS)
            }
        } catch (e: Exception) {
            Timber.e(e)
        }
    }

    override fun loadFailed(reason: String) {
        isDataLoading.postValue(false)
        dataLoadStatus.postValue(reason)
    }

    override fun onCleared() {
        super.onCleared()
        try {
            pendingJobs.cancel()
        } catch (e: Exception) {
            Timber.e(e, "Error while cancelling job")
        }
    }

    fun loadMovieData(searchTerm: String) {
        if (networkUtil.isNetworkConnected()) {
            coroutineScope.launch {
                Timber.d("Current thread loadMovieData ${Thread.currentThread().name}")
                isDataLoading.postValue(true)
                val result = search(searchTerm, pageNumber)
                if (result is Result.Success) {
                    totalSearchResults = result.data.totalResults
                    sourceLoaded(result.data.movieSearchResults)
                } else if (result is Result.Error) {
                    loadFailed(result.exception.message.toString())
                }
            }
        } else {
            isDataLoading.postValue(false)
            dataLoadStatus.postValue(Constants.NO_NETWORK_CONNECTION)
        }
    }

    private suspend fun search(
        query: String,
        page: Int
    ) = safeApiCall(
        call = { requestSearch(query, page) },
        errorMessage = Constants.API_RESPONSE_ERROR
    )

    private suspend fun requestSearch(
        query: String,
        page: Int
    ): Result<MovieSearchResponse> {
        val deferredResponse = service.searchDeferred(query, page)
        val response = deferredResponse.await()
        if (response.isSuccessful) {
            val body = response.body()
            return if (body != null) {
                Result.Success(body)
            } else {
                Result.Error(IOException(Constants.API_RESPONSE_ERROR))
            }
        }
        return Result.Error(
            IOException("Error ${response.message()}")
        )
    }

    suspend fun isFavorite(movie: Movie): Boolean {
        val count = coroutineScope.async {
            dataRepo.isFavorite(movie.imdbId)
        }.await()
        return count > 0
    }

    fun loadFavorites() {
        try {
            coroutineScope.launch {
                Timber.d("Current thread loadMovieData ${Thread.currentThread().name}")
                isDataLoading.postValue(true)
                val fav = runBlocking { dataRepo.getFavorites() }
                if (fav.isNotEmpty()) {
                    sourceLoaded(fav.toMovieList())
                } else {
                    loadFailed(Constants.NO_FAVORITES)
                }
            }
        } catch (e: java.lang.Exception) {
            isDataLoading.postValue(false)
            loadFailed(Constants.NO_FAVORITES)
        }
    }
}

fun <E> Collection<E>?.toMovieList(): MutableList<Movie> {
    val result = mutableListOf<Movie>()
    this?.let { favList ->
        for (e in favList) {
            val entry = e as FavoriteEntry
            val m = Movie(title = entry.title, imdbId = entry.imdbid, poster = entry.poster ?: "")
            result.add(m)
        }
    }
    return result
}
