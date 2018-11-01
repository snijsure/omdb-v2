package com.snijsure.omdbsearch.di.modules

import com.snijsure.omdbsearch.util.CoroutinesContextProvider
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.Dispatchers

/**
 * Provide [CoroutinesContextProvider] to this app's components.
 */
@Module
class CoroutinesContextProviderModule {

    @Provides
    fun provideCoroutinesContextProvider() = CoroutinesContextProvider(
        Dispatchers
            .Main, Dispatchers.Default
    )
}