package com.snijsure.dbrepository.repo.room

import android.arch.lifecycle.LiveData
import com.snijsure.utility.CoroutinesContextProvider
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class FavoriteDBRepoImpl @Inject constructor(private val favDao: FavoriteDao,
                                             private val contextProvider: CoroutinesContextProvider) : FavoriteDBRepo {

    override fun getFavorites(): LiveData<List<FavoriteEntry>> {
        return favDao.getFavorites()
    }


    override suspend fun isFavorite(movieId: String): Int {
        return GlobalScope.async(contextProvider.io,
            CoroutineStart.DEFAULT) {
            favDao.isFavorite(movieId)
        }.await()
    }

    override fun addMovieToFavorites(movie: FavoriteEntry) {

        GlobalScope.launch(contextProvider.io,
            CoroutineStart.DEFAULT) {
            try {
                favDao.addMovieToFavorites(movie)
            }
            catch(e: Exception) {
                Timber.e(e,"exception")
            }
        }
    }

}