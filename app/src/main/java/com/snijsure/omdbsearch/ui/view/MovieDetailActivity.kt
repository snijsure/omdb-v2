package com.snijsure.omdbsearch.ui.view

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.snijsure.omdbsearch.R
import com.snijsure.omdbsearch.data.MovieDetail
import com.snijsure.omdbsearch.databinding.ActivityMovieDetailBinding
import com.snijsure.omdbsearch.ui.viewmodel.MovieDetailViewModel
import com.snijsure.omdbsearch.ui.viewmodel.MovieDetailViewModelFactory
import dagger.android.AndroidInjection
import javax.inject.Inject

class MovieDetailActivity : AppCompatActivity() {
    @Inject
    lateinit var movieDetailViewModelFactory: MovieDetailViewModelFactory

    private lateinit var movieDetailViewModel: MovieDetailViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        val dataBinding = DataBindingUtil.setContentView<ActivityMovieDetailBinding>(this, R.layout.activity_movie_detail)

        movieDetailViewModel = ViewModelProviders.of(this, movieDetailViewModelFactory).get(MovieDetailViewModel::class.java)

        if (intent.hasExtra(IMDB_ID)) {
            val movieId = intent.getStringExtra(IMDB_ID)
            movieDetailViewModel.loadMovieDetail(movieId)
        }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        movieDetailViewModel.movieDetail.observe(this, Observer<MovieDetail> {
            if (it != null) {
                dataBinding.executePendingBindings()
                dataBinding.detail = it
            }
        })
    }

    override fun onSupportNavigateUp(): Boolean {
        finishAfterTransition()
        return true
    }

    companion object {
        const val IMDB_ID = "item_id"
    }
}
