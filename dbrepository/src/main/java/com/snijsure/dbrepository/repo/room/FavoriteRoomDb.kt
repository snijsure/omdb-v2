package com.snijsure.dbrepository.repo.room

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context
import android.support.annotation.VisibleForTesting

@Database(entities = [(FavoriteEntry::class)], version = 1)
abstract class FavoriteRoomDb : RoomDatabase() {

    abstract fun favoriteDao(): FavoriteDao

    companion object {
        @VisibleForTesting
        private val DATABASE_NAME = "favorites.db"

        fun buildDatabase(appContext: Context): FavoriteRoomDb {
            return Room.databaseBuilder(appContext, FavoriteRoomDb::class.java,
                DATABASE_NAME
            ).build()
        }
    }
}