package com.snijsure.utility.di

import com.snijsure.utility.CoroutinesContextProvider
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.Dispatchers

/**
 * Provide [CoroutinesContextProvider] to this app's components.
 */
@Module
class CoroutinesContextProviderModule {

    @Provides
    fun provideCoroutinesContextProvider() = CoroutinesContextProvider(Dispatchers.Main, Dispatchers.IO)
}
