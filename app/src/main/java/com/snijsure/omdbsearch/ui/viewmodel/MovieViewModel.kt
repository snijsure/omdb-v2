package com.snijsure.omdbsearch.ui.viewmodel

import android.app.Application
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.snijsure.dbrepository.repo.room.DataRepository
import com.snijsure.dbrepository.repo.room.FavoriteEntry
import com.snijsure.omdbsearch.data.*
import com.snijsure.omdbsearch.data.search.OmdbSearchService
import com.snijsure.omdbsearch.util.*
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
    private val appContext: Application,
    private val dataRepo: DataRepository
) : ViewModel(),
    LoadSourceCallback {

    val isDataLoading = MutableLiveData<Boolean>().apply {
        this.value = false
    }
    var totalSearchResults = 0
    var pendingSearchFetcherJob: Job? = null
    var movieData: MutableLiveData<List<Movie>> = MutableLiveData()

    var dataLoadStatus = MutableLiveData<String>()
    var pageNumber = 1

    @Suppress("UNCHECKED_CAST")
    override fun sourceLoaded(result: Any?) {
        isDataLoading.postValue(false)
        if (result != null && (result as List<Movie>).isNotEmpty()) {
            pageNumber++
            movieData.postValue(result as List<Movie>?)
        } else {
            dataLoadStatus.postValue(Constants.NO_SEARCH_RESULTS)
        }
    }

    override fun loadFailed(reason: String) {
        isDataLoading.postValue(false)
        dataLoadStatus.postValue(reason)
    }

    override fun onCleared() {
        super.onCleared()
        try {
            pendingSearchFetcherJob?.cancel()
        } catch (e: Exception) {
            Timber.e(e, "Error while cancelling job")
        }
    }

    fun loadMovieData(searchTerm: String) {
        if (networkUtil.isNetworkConnected()) {
            pendingSearchFetcherJob = GlobalScope.launch(contextProvider.io,
                CoroutineStart.DEFAULT
            ) {
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
        val count = GlobalScope.async {
            dataRepo.isFavorite(movie.imdbId)
        }.await()
        return count > 0
    }

    fun addToFavorite(movie: Movie) {
        val entry = FavoriteEntry(title = movie.title,imdbid = movie.imdbId,
                poster = movie.poster)
        dataRepo.addMovieToFavorites(entry)
    }

}
