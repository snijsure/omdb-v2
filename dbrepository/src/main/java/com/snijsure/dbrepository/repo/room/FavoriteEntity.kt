package com.snijsure.dbrepository.repo.room

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.support.annotation.NonNull


@Entity
data class FavoriteEntry (
    @PrimaryKey @NonNull var id: String,
    @ColumnInfo(name = "Title") var title: String,
    @ColumnInfo(name ="imdbid") var imdbid: String,
    @ColumnInfo(name ="poster") var poster: String?
    )