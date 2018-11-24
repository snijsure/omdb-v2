package com.snijsure.dbrepository.repo.room

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query

@Dao
interface FavoriteDao {

    @Query("SELECT * FROM favorites")
    fun getFavorites(): List<FavoriteEntry>

    @Query("SELECT COUNT(*) FROM favorites WHERE imdbid = :movieId")
    fun isFavorite(movieId: String): Int

    @Query("DELETE FROM favorites WHERE imdbid = :movieId")
    fun removeEntryFromFavorites(movieId: String)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun addMovieToFavorites(movie: FavoriteEntry)
}
