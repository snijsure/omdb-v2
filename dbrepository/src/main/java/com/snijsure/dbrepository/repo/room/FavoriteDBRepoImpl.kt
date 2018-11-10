package com.snijsure.dbrepository.repo.room

import android.arch.lifecycle.LiveData
import com.snijsure.utility.CoroutinesContextProvider
import kotlinx.coroutines.*
import timber.log.Timber
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class FavoriteDBRepoImpl @Inject constructor(private val favDao: FavoriteDao,
                                             private val contextProvider: CoroutinesContextProvider) :
    FavoriteDBRepo , CoroutineScope {
    override val coroutineContext: CoroutineContext
        get() = contextProvider.io

    override fun removeEntryFromFavorites(movieId: String) {
        launch(coroutineContext) {
            favDao.removeEntryFromFavorites(movieId)
        }
    }

    override fun getFavorites(): LiveData<List<FavoriteEntry>> {
        return runBlocking(coroutineContext) {
            favDao.getFavorites()
        }
    }

    override suspend fun isFavorite(movieId: String): Int {
        return async(coroutineContext) {
            favDao.isFavorite(movieId)
        }.await()
    }

    override fun addMovieToFavorites(movie: FavoriteEntry) {
        launch(coroutineContext) {
            try {
                favDao.addMovieToFavorites(movie)
            }
            catch(e: Exception) {
                Timber.e(e,"Exception while adding movie to favorites")
            }
        }
    }
}