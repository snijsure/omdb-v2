package com.snijsure.omdbsearch.ui.main

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.AppCompatTextView
import android.widget.ImageView
import android.widget.ProgressBar
import butterknife.BindView
import butterknife.ButterKnife
import com.bumptech.glide.Glide
import com.snijsure.omdbsearch.R
import com.snijsure.omdbsearch.data.MovieDetail
import dagger.android.AndroidInjection
import javax.inject.Inject

class MovieDetailActivity : AppCompatActivity() {
    @Inject
    lateinit var movieModelViewFactory: MovieViewModelFactory
    lateinit var movieViewModel: MovieViewModel

    @BindView(R.id.movie_poster)
    lateinit var poster: ImageView
    @BindView(R.id.movie_title)
    lateinit var title: AppCompatTextView
    @BindView(R.id.movie_year)
    lateinit var year: AppCompatTextView
    @BindView(R.id.movie_director)
    lateinit var director: AppCompatTextView
    @BindView(R.id.movie_plot)
    lateinit var plot: AppCompatTextView

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_detail)
        ButterKnife.bind(this)

        movieViewModel = ViewModelProviders.of(this, movieModelViewFactory).get(MovieViewModel::class.java)

        if (intent.hasExtra(IMDB_ID)) {
            val movieId = intent.getStringExtra(IMDB_ID)
            movieViewModel.loadMovieDetail(movieId)
        }

        movieViewModel.movieDetail.observe(this, Observer<MovieDetail> {
            if (it != null) {
                plot.text = it.plot
                director.text = this.resources.getString(R.string.director) + it.director
                year.text = this.resources.getString(R.string.year) + it.year
                title.text = it.title
                if (it.poster.isNotEmpty()) {
                    Glide.with(this)
                        .load(it.poster)
                        .into(poster)
                }
            }
        })
    }

    companion object {
        const val IMDB_ID = "item_id"
    }
}
