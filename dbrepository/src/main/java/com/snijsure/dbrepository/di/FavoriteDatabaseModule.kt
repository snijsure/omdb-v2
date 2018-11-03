package com.snijsure.dbrepository.di

import android.app.Application
import com.snijsure.dbrepository.repo.room.FavoriteDao
import com.snijsure.dbrepository.repo.room.FavoriteRoomDb
import com.snijsure.utility.di.CoroutinesContextProviderModule
import dagger.Module
import dagger.Provides
import javax.inject.Singleton


@Module(includes = [
    (DatabaseBindingModule::class), (CoroutinesContextProviderModule::class)
])
class FavoriteDatabaseModule {

    @Provides
    @Singleton
    fun provideFavoriteDatabase(application: Application): FavoriteRoomDb {
        return FavoriteRoomDb.buildDatabase(application)
    }

    @Provides
    fun provideBookmarkEntryDao(db: FavoriteRoomDb): FavoriteDao {
        return db.favoriteDao()
    }

}
