package com.snijsure.omdbsearch.ui.viewmodel

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import javax.inject.Inject


class MovieViewModelFactory @Inject constructor(private val mainViewModel: MovieViewModel) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MovieViewModel::class.java)) {
            return mainViewModel as T
        }
        throw IllegalArgumentException("Unknown class name")
    }
}