package com.snijsure.dbrepository.repo.room

import javax.inject.Inject


class DataRepository @Inject constructor(private val favoriteDBRepo : FavoriteDBRepo)
    : FavoriteDBRepo by favoriteDBRepo