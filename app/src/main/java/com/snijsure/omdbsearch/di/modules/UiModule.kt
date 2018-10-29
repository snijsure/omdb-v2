package com.snijsure.omdbsearch.di.modules


import com.snijsure.omdbsearch.ui.view.MovieDetailActivity
import com.snijsure.omdbsearch.ui.view.MovieListActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class UiModule {

    @ContributesAndroidInjector
    abstract fun contributeMovieDetailActivity(): MovieDetailActivity

    @ContributesAndroidInjector
    abstract fun contributeMovieListActivity(): MovieListActivity
}
