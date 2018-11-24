package com.snijsure.dbrepository.repo.room

interface FavoriteDBRepo {
    suspend fun getFavorites(): List<FavoriteEntry>
    suspend fun isFavorite(movieId: String): Int
    fun addMovieToFavorites(movie: FavoriteEntry)
    fun removeEntryFromFavorites(movieId: String)
}