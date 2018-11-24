package com.snijsure.dbrepository.repo.room

import android.arch.lifecycle.LiveData

interface FavoriteDBRepo {
    suspend fun getFavorites(): List<FavoriteEntry>
    suspend fun isFavorite(movieId: String): Int
    fun addMovieToFavorites(movie: FavoriteEntry)
    fun removeEntryFromFavorites(movieId: String)
}