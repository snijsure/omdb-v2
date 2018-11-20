package com.snijsure.dbrepository.di

import com.snijsure.dbrepository.repo.room.FavoriteDBRepo
import com.snijsure.dbrepository.repo.room.FavoriteDBRepoImpl
import dagger.Binds
import dagger.Module

@Module
abstract class DatabaseBindingModule {

    @Binds
    abstract fun bindFavRepository(impl: FavoriteDBRepoImpl): FavoriteDBRepo
}