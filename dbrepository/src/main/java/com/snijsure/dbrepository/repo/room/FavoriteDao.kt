package com.snijsure.dbrepository.repo.room

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import com.snijsure.dbrepository.repo.room.FavoriteEntry


@Dao
interface FavoriteDao {

    @Query("SELECT * FROM FavoriteEntry")
    fun getFavorites(): LiveData<List<FavoriteEntry>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addMovieToFavorites(movie: FavoriteEntry)
}
