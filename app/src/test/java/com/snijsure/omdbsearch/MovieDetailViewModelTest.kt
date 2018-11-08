package com.snijsure.omdbsearch

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.arch.lifecycle.Observer
import com.nhaarman.mockitokotlin2.verify
import com.snijsure.omdbsearch.data.MovieDetail
import com.snijsure.omdbsearch.data.search.OmdbSearchService
import com.snijsure.omdbsearch.ui.viewmodel.MovieDetailViewModel
import com.snijsure.omdbsearch.util.Constants
import com.snijsure.omdbsearch.util.NetworkUtil
import com.snijsure.utility.CoroutinesContextProvider
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import retrofit2.Response
import retrofit2.mock.Calls


class MovieDetailViewModelTest {
    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    @Mock
    lateinit var mockOmdbService: OmdbSearchService
    @Mock
    lateinit var mockNetworkUtil: NetworkUtil
    @Mock
    lateinit var mockContextProvider: CoroutinesContextProvider
    @Mock
    lateinit var mockResponse: Response<MovieDetail>
    @Mock
    lateinit var mockJob: Job
    @Mock
    lateinit var movieDetailObserver: Observer<MovieDetail>
    @Mock
    lateinit var dataLoadingObserver: Observer<Boolean>

    private lateinit var movieDetailViewModel: MovieDetailViewModel

    @ExperimentalCoroutinesApi
    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        Mockito.`when`(mockContextProvider.io).thenReturn(Dispatchers.Unconfined)
        Mockito.`when`(mockContextProvider.main).thenReturn(Dispatchers.Unconfined)

        movieDetailViewModel = MovieDetailViewModel(
            mockOmdbService,
            mockNetworkUtil,
            mockContextProvider)
        movieDetailViewModel.pendingSearchFetcherJob = mockJob

    }

    @Test
    fun handleNoNetworkConnection() {
        Mockito.`when`(mockNetworkUtil.isNetworkConnected()).thenReturn(false)

        movieDetailViewModel.loadMovieDetail("1234")

        Assert.assertEquals(Constants.NO_NETWORK_CONNECTION, movieDetailViewModel.dataLoadStatus.value)
        Assert.assertEquals(false, movieDetailViewModel.isDataLoading.value)

    }

    // Simulate condition where API doesn't respond
    @Test
    fun apiRequestErrors() {
        Mockito.`when`(mockNetworkUtil.isNetworkConnected()).thenReturn(true)

        movieDetailViewModel.loadMovieDetail("1234")

        Assert.assertEquals(Constants.API_RESPONSE_ERROR, movieDetailViewModel.dataLoadStatus.value)
    }

    // Simulate condition where API responds with zero results
    @Test
    fun emptyResponse() {

        Mockito.`when`(mockResponse.isSuccessful).thenReturn(true)
        val searchResponse = Calls.response(mockResponse).execute()
        val deferred = CompletableDeferred<Response<MovieDetail>>(searchResponse)
        Mockito.`when`(mockNetworkUtil.isNetworkConnected()).thenReturn(true)
        Mockito.`when`(
            mockOmdbService.movieDetailDeferred(
                "1234"
            )
        ).thenReturn(deferred)

        movieDetailViewModel.loadMovieDetail("1234")

        Assert.assertEquals(Constants.API_RESPONSE_ERROR, movieDetailViewModel.dataLoadStatus.value)
    }


    @Test
    fun loadMovieDetails() {
        val movieDetails = setupMovieDetails()

        movieDetailViewModel.movieDetail.observeForever(movieDetailObserver)
        movieDetailViewModel.isDataLoading.observeForever(dataLoadingObserver)
        Mockito.`when`(mockResponse.body()).thenReturn(movieDetails)
        Mockito.`when`(mockNetworkUtil.isNetworkConnected()).thenReturn(true)
        Mockito.`when`(mockResponse.isSuccessful).thenReturn(true)
        val searchResponse = Calls.response(mockResponse).execute()
        val deferred = CompletableDeferred<Response<MovieDetail>>(searchResponse)
        Mockito.`when`(
            mockOmdbService.movieDetailDeferred(
                "1234"
            )
        ).thenReturn(deferred)

        movieDetailViewModel.loadMovieDetail("1234")



        verify(movieDetailObserver, Mockito.times(1)).onChanged(movieDetails)
        verify(dataLoadingObserver, Mockito.times(2)).onChanged(false)
        verify(dataLoadingObserver, Mockito.times(1)).onChanged(true)

    }

    private fun setupMovieDetails(): MovieDetail {
        return MovieDetail("Movie1","Director1","2001","Plot1","http://poster")

    }

}