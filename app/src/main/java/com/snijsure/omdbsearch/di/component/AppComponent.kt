package com.snijsure.omdbsearch.di.component

import com.snijsure.omdbsearch.OmdbApplication
import com.snijsure.omdbsearch.di.modules.AppModule
import com.snijsure.omdbsearch.di.modules.CoroutinesContextProviderModule
import com.snijsure.omdbsearch.di.modules.NetworkModule
import com.snijsure.omdbsearch.di.modules.UiModule
import dagger.Component
import dagger.android.AndroidInjectionModule
import javax.inject.Singleton


@Singleton
@Component(
    modules = [AndroidInjectionModule::class, UiModule::class, AppModule::class,
        NetworkModule::class, CoroutinesContextProviderModule::class]
)

interface AppComponent {
    fun inject(app: OmdbApplication)
}