package com.snijsure.dbrepository.repo.room

import android.arch.lifecycle.LiveData
import com.snijsure.utility.CoroutinesContextProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class FavoriteDBRepoImpl @Inject constructor(
    private val favDao: FavoriteDao,
    private val contextProvider: CoroutinesContextProvider
) :
    FavoriteDBRepo {
    private val pendingJob = Job()
    private val coroutineScope = CoroutineScope(contextProvider.io + pendingJob)

    override fun removeEntryFromFavorites(movieId: String) {
        coroutineScope.launch {
            favDao.removeEntryFromFavorites(movieId)
        }
    }

    override suspend fun getFavorites(): LiveData<List<FavoriteEntry>> {
        return coroutineScope.async {
            favDao.getFavorites()
        }.await()
    }

    override suspend fun isFavorite(movieId: String): Int {
        return coroutineScope.async {
            favDao.isFavorite(movieId)
        }.await()
    }

    override fun addMovieToFavorites(movie: FavoriteEntry) {
        coroutineScope.launch {
            try {
                favDao.addMovieToFavorites(movie)
            } catch (e: Exception) {
                Timber.e(e, "Exception while adding movie to favorites")
            }
        }
    }
}