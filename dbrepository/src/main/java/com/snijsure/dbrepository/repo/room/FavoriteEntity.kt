package com.snijsure.dbrepository.repo.room

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.support.annotation.NonNull


@Entity (tableName ="favorites")
data class FavoriteEntry (
    @PrimaryKey(autoGenerate = true) var  id : Int = 0,
    @ColumnInfo(name = "title") var title: String,
    @ColumnInfo(name ="imdbid") var imdbid: String,
    @ColumnInfo(name ="poster") var poster: String?
    )