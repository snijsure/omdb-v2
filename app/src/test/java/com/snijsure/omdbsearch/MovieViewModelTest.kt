package com.snijsure.omdbsearch

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.arch.lifecycle.Observer
import com.nhaarman.mockitokotlin2.verify
import com.snijsure.dbrepository.repo.room.DataRepository
import com.snijsure.omdbsearch.data.Movie
import com.snijsure.omdbsearch.data.MovieSearchResponse
import com.snijsure.omdbsearch.data.search.OmdbSearchService
import com.snijsure.omdbsearch.ui.viewmodel.MovieViewModel
import com.snijsure.omdbsearch.util.Constants
import com.snijsure.omdbsearch.util.NetworkUtil
import com.snijsure.utility.CoroutinesContextProvider
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.mockito.ArgumentMatchers
import org.mockito.Mock
import org.mockito.Mockito.times
import org.mockito.MockitoAnnotations
import retrofit2.Response
import retrofit2.mock.Calls
import org.mockito.Mockito.`when` as whenever

class MovieViewModelTest {
    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    @Mock
    lateinit var mockOmdbService: OmdbSearchService
    @Mock
    lateinit var mockNetworkUtil: NetworkUtil
    @Mock
    lateinit var mockContextProvider: CoroutinesContextProvider
    @Mock
    lateinit var mockResponse: Response<MovieSearchResponse>
    @Mock
    lateinit var movieListObserver: Observer<List<Movie>>
    @Mock
    lateinit var dataLoadingObserver: Observer<Boolean>
    @Mock
    lateinit var mockDataRepo: DataRepository
    private lateinit var movieViewModel: MovieViewModel

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        whenever(mockContextProvider.io).thenReturn(Dispatchers.Unconfined)
        whenever(mockContextProvider.main).thenReturn(Dispatchers.Unconfined)
        whenever(mockContextProvider.computation).thenReturn(Dispatchers.Unconfined)

        movieViewModel = MovieViewModel(
            mockOmdbService,
            mockNetworkUtil,
            mockContextProvider,
            mockDataRepo
        )
    }

    @Test
    fun handleNoNetworkConnection() {
        whenever(mockNetworkUtil.isNetworkConnected()).thenReturn(false)

        movieViewModel.loadMovieData("star wars")

        assertEquals(Constants.NO_NETWORK_CONNECTION, movieViewModel.dataLoadStatus.value)
        assertEquals(false, movieViewModel.isDataLoading.value)
    }

    // Simulate condition where API doesn't respond
    @Test
    fun apiRequestErrors() {
        whenever(mockNetworkUtil.isNetworkConnected()).thenReturn(true)

        movieViewModel.loadMovieData("star wars")

        assertEquals(Constants.API_RESPONSE_ERROR, movieViewModel.dataLoadStatus.value)
    }

    // Simulate condition where API responds with zero results
    @Test
    fun emptyResponse() {

        whenever(mockResponse.isSuccessful).thenReturn(true)
        val searchResponse = Calls.response(mockResponse).execute()
        val deferred = CompletableDeferred<Response<MovieSearchResponse>>(searchResponse)
        whenever(mockNetworkUtil.isNetworkConnected()).thenReturn(true)
        whenever(
            mockOmdbService.searchDeferred(
                ArgumentMatchers.anyString(),
                ArgumentMatchers.anyInt()
            )
        ).thenReturn(deferred)

        movieViewModel.loadMovieData("star wars")

        assertEquals(Constants.API_RESPONSE_ERROR, movieViewModel.dataLoadStatus.value)
    }

    @Test
    fun movieEntriesFound() {
        val movieList = setupMovieList()

        movieViewModel.movieData.observeForever(movieListObserver)
        movieViewModel.isDataLoading.observeForever(dataLoadingObserver)
        whenever(mockResponse.body()).thenReturn(MovieSearchResponse(movieList, movieList.size * 100, "1"))
        whenever(mockNetworkUtil.isNetworkConnected()).thenReturn(true)
        whenever(mockResponse.isSuccessful).thenReturn(true)
        val searchResponse = Calls.response(mockResponse).execute()
        val deferred = CompletableDeferred<Response<MovieSearchResponse>>(searchResponse)
        whenever(
            mockOmdbService.searchDeferred(
                ArgumentMatchers.anyString(),
                ArgumentMatchers.anyInt()
            )
        ).thenReturn(deferred)

        movieViewModel.loadMovieData("star wars")

        assertEquals(2, movieViewModel.pageNumber) // We advance to next page
        assertEquals(2 * 100, movieViewModel.totalSearchResults)
        assertEquals(
            movieList,
            movieViewModel.movieData.value
        )

        verify(movieListObserver, times(1)).onChanged(movieList)
        verify(dataLoadingObserver, times(2)).onChanged(false)
        verify(dataLoadingObserver, times(1)).onChanged(true)
    }

    @Test
    fun loadMoreData() {
        val movieList = setupMovieList()

        movieViewModel.movieData.observeForever(movieListObserver)
        movieViewModel.isDataLoading.observeForever(dataLoadingObserver)

        whenever(mockResponse.body()).thenReturn(MovieSearchResponse(movieList, movieList.size * 100, "1"))
        whenever(mockNetworkUtil.isNetworkConnected()).thenReturn(true)
        whenever(mockResponse.isSuccessful).thenReturn(true)
        val searchResponse = Calls.response(mockResponse).execute()
        val deferred = CompletableDeferred<Response<MovieSearchResponse>>(searchResponse)
        whenever(
            mockOmdbService.searchDeferred(
                ArgumentMatchers.anyString(),
                ArgumentMatchers.anyInt()
            )
        ).thenReturn(deferred)

        movieViewModel.loadMovieData("star wars")
        movieViewModel.loadMovieData("star wars")

        // Verify after second search request next request will be for page 3
        assertEquals(3, movieViewModel.pageNumber)
        assertEquals(2 * 100, movieViewModel.totalSearchResults)
        assertEquals(
            movieList,
            movieViewModel.movieData.value
        )
        verify(movieListObserver, times(2)).onChanged(movieList)
        verify(dataLoadingObserver, times(2)).onChanged(true)
        verify(dataLoadingObserver, times(3)).onChanged(false)
    }

    @Test
    fun zeroEntriesFromSearch() {
        whenever(mockResponse.body()).thenReturn(MovieSearchResponse(emptyList(), 0, "1"))
        whenever(mockNetworkUtil.isNetworkConnected()).thenReturn(true)
        whenever(mockResponse.isSuccessful).thenReturn(true)
        val searchResponse = Calls.response(mockResponse).execute()
        val deferred = CompletableDeferred<Response<MovieSearchResponse>>(searchResponse)
        whenever(
            mockOmdbService.searchDeferred(
                ArgumentMatchers.anyString(),
                ArgumentMatchers.anyInt()
            )
        ).thenReturn(deferred)

        movieViewModel.loadMovieData("star wars")

        assertEquals(Constants.NO_SEARCH_RESULTS, movieViewModel.dataLoadStatus.value)
    }

    private fun setupMovieList(): List<Movie> {

        val movie1 = Movie("Star Wars", "1234", "http://www.imdb.com/poster1.png")
        val movie2 = Movie("Star Wars", "1235", "http://www.imdb.com/poster2.png")
        return listOf(movie1, movie2)
    }
}