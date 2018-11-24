package com.snijsure.omdbsearch.ui.view

import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.view.MenuItem
import butterknife.BindView
import butterknife.ButterKnife
import com.snijsure.omdbsearch.R
import dagger.android.AndroidInjection
import dagger.android.support.DaggerAppCompatActivity
import timber.log.Timber

/**
 * This activity displays list of movies retrieved using OMDB API
 */
class MovieListActivity : DaggerAppCompatActivity() {
    @BindView(R.id.bottom_navigation)
    lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        var fragment: Fragment?
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_list)
        ButterKnife.bind(this)
        supportActionBar?.title = resources.getString(R.string.movie_search)

        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.action_clear -> {
                    fragment = MovieListFragment()
                    switchFragment(fragment as MovieListFragment)
                    true
                }
                R.id.action_favorites -> {
                    fragment = FavoritesFragment()
                    switchFragment(fragment as FavoritesFragment)
                    true
                }
                R.id.action_about -> {
                    fragment = AboutAppFragment()
                    switchFragment(fragment as AboutAppFragment)
                    true
                }
                else -> {
                    true
                }
            }
        }
        bottomNavigationView.selectedItemId = R.id.action_clear
    }

    private fun switchFragment(fragment: Fragment) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.main_fragment_holder, fragment)
        fragmentTransaction.commit()
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
