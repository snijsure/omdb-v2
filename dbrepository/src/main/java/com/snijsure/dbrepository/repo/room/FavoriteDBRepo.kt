package com.snijsure.dbrepository.repo.room

import android.arch.lifecycle.LiveData

interface FavoriteDBRepo {
    fun getFavorites(): LiveData<List<FavoriteEntry>>
    fun addMovieToFavorites(movie: FavoriteEntry)
}