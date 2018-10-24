package com.snijsure.omdbsearch.ui.main

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.snijsure.omdbsearch.R
import dagger.android.AndroidInjection
import timber.log.Timber
import javax.inject.Inject

class MovieDetailActivity : AppCompatActivity() {
    @Inject
    lateinit var movieModelViewFactory: MovieViewModelFactory
    lateinit var movieViewModel: MovieViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_detail)
        movieViewModel = ViewModelProviders.of(this, movieModelViewFactory).get(MovieViewModel::class.java)

        var intent = getIntent()
        if (intent.hasExtra(IMDB_ID)) {
            var imdbId = intent.getStringExtra(IMDB_ID)
            Timber.d("SUBODH imdb id is ${imdbId}")
            movieViewModel.loadMovieDetail(imdbId)
        }
        // Show the Up button in the action bar.
        //supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    /* override fun onOptionsItemSelected(item: MenuItem) =
        when (item.itemId) {
            android.R.id.home -> {
                navigateUpTo(Intent(this, MovieListActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
        */
    companion object {
        const val IMDB_ID = "item_id"
    }
}
