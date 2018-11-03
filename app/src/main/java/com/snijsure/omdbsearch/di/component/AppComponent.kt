package com.snijsure.omdbsearch.di.component

import com.snijsure.dbrepository.di.FavoriteDatabaseModule
import com.snijsure.omdbsearch.OmdbApplication
import com.snijsure.omdbsearch.di.modules.*
import com.snijsure.utility.di.CoroutinesContextProviderModule
import dagger.Component
import dagger.android.AndroidInjectionModule
import javax.inject.Singleton


@Singleton
@Component(
    modules = [AndroidInjectionModule::class, UiModule::class, AppModule::class,
        NetworkModule::class, CoroutinesContextProviderModule::class,
        FavoriteDatabaseModule::class]
)

interface AppComponent {
    fun inject(app: OmdbApplication)
}