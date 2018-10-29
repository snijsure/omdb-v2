package com.snijsure.omdbsearch.ui.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.snijsure.omdbsearch.data.*
import com.snijsure.omdbsearch.data.search.OmdbSearchService
import com.snijsure.omdbsearch.util.Constants
import com.snijsure.omdbsearch.util.NetworkUtil
import com.snijsure.omdbsearch.util.safeApiCall
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject


class MovieDetailViewModel @Inject constructor(
    private val service: OmdbSearchService,
    private val networkUtil: NetworkUtil,
    private val contextProvider: CoroutinesContextProvider
) : ViewModel(),
    LoadSourceCallback {

    val isDataLoading = MutableLiveData<Boolean>().apply {
        this.value = false
    }
    var pendingSearchFetcherJob: Job? = null
    var movieDetail: MutableLiveData<MovieDetail> = MutableLiveData()

    var dataLoadStatus = MutableLiveData<String>()

    override fun sourceLoaded(result: Any?) {
        isDataLoading.postValue(false)
        if (result != null) {
            movieDetail.postValue(result as MovieDetail?)
        } else {
            dataLoadStatus.postValue(Constants.NO_SEARCH_RESULTS)
        }
    }

    override fun loadFailed(reason: String) {
        isDataLoading.postValue(false)
        dataLoadStatus.postValue(reason)
    }

    fun terminatePendingJob() {
        try {
            pendingSearchFetcherJob?.cancel()
        } catch (e: Exception) {
            Timber.e(e, "Error while cancelling job")
        }
    }


    fun loadMovieDetail(movieId: String) {
        if (networkUtil.isNetworkConnected()) {
            pendingSearchFetcherJob = GlobalScope.launch(contextProvider.io,
                CoroutineStart.DEFAULT, null, {
                    isDataLoading.postValue(true)
                    val result = movieDetail(movieId)
                    if (result is Result.Success) {
                        sourceLoaded(result.data)
                    } else if (result is Result.Error) {
                        loadFailed(result.exception.message.toString())
                    }
                })
        } else {
            isDataLoading.postValue(false)
            dataLoadStatus.postValue(Constants.NO_NETWORK_CONNECTION)
        }
    }

    private suspend fun movieDetail(
        movieId: String
    ) = safeApiCall(
        call = { getMovieDetail(movieId) },
        errorMessage = Constants.API_RESPONSE_ERROR
    )


    private suspend fun getMovieDetail(
        movieId: String
    ): Result<MovieDetail> {
        val deferredResponse = service.movieDetailDeferred(movieId)
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
}
