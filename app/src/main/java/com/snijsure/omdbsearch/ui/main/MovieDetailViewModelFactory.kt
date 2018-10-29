package com.snijsure.omdbsearch.ui.main

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import javax.inject.Inject



class MovieDetailViewModelFactory @Inject constructor(private val movieDetailViewModel: MovieDetailViewModel) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MovieDetailViewModel::class.java)) {
            return movieDetailViewModel as T
        }
        throw IllegalArgumentException("Unknown class name")
    }
}