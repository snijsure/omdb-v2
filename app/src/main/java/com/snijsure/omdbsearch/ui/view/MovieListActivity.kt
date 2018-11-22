package com.snijsure.omdbsearch.ui.view

import android.os.Bundle
import android.view.MenuItem
import butterknife.ButterKnife
import com.snijsure.omdbsearch.R
import dagger.android.AndroidInjection
import dagger.android.support.DaggerAppCompatActivity

/**
 * This activity displayes list of movies retrieved using OMDB API
 */
class MovieListActivity : DaggerAppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_list)
        ButterKnife.bind(this)
        supportActionBar?.title = resources.getString(R.string.movie_search)
        val fragment = MovieListFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.my_nav_host_fragment, fragment)
            .commit()
    }

    // Reference: https://antonioleiva.com/kotlin-awesome-tricks-for-android/
    private inline fun consume(f: () -> Unit): Boolean {
        f()
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        android.R.id.home -> consume { onBackPressed() }
        else -> super.onOptionsItemSelected(item)
    }
}
