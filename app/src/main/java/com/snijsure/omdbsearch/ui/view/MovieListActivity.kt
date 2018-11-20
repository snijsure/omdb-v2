package com.snijsure.omdbsearch.ui.view

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.AppCompatTextView
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SearchView
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.RelativeLayout
import android.widget.Toast
import butterknife.BindView
import butterknife.ButterKnife
import com.snijsure.dbrepository.repo.room.DataRepository
import com.snijsure.omdbsearch.R
import com.snijsure.omdbsearch.data.Movie
import com.snijsure.omdbsearch.ui.viewmodel.MovieViewModel
import com.snijsure.omdbsearch.ui.viewmodel.MovieViewModelFactory
import com.snijsure.omdbsearch.util.InfiniteScrollListener
import com.snijsure.omdbsearch.util.NetworkUtil
import com.snijsure.utility.CoroutinesContextProvider
import dagger.android.AndroidInjection
import javax.inject.Inject

/**
 * This activity displayes list of movies retrieved using OMDB API
 */
class MovieListActivity : AppCompatActivity() {
    @Inject
    lateinit var movieModelViewFactory: MovieViewModelFactory
    @Inject
    lateinit var networkUtil: NetworkUtil
    @Inject
    lateinit var dataRepo: DataRepository
    @Inject
    lateinit var contextProvider: CoroutinesContextProvider

    lateinit var movieViewModel: MovieViewModel
    lateinit var adapter: MovieAdapter
    lateinit var searchView: SearchView

    @BindView(R.id.movie_list_view)
    lateinit var recyclerView: RecyclerView
    @BindView(R.id.no_connection)
    lateinit var noConnection: AppCompatTextView
    @BindView(R.id.busy_indicator)
    lateinit var busyIndicator: RelativeLayout
    @BindView(R.id.welcome_text)
    lateinit var welcomeText: AppCompatTextView
    @BindView(R.id.rootView)
    lateinit var rootView: ConstraintLayout

    private var endOfResultsShown = false

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_list)
        ButterKnife.bind(this)
        movieViewModel = ViewModelProviders.of(this, movieModelViewFactory).get(MovieViewModel::class.java)
        setupRecyclerView()
        setupViewModelObservers()
    }

    override fun onResume() {
        super.onResume()
        checkConnectivity()
        rootView.requestFocus()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)

        val searchMenuItem = menu.findItem(R.id.action_search)

        searchView = searchMenuItem.actionView as SearchView
        searchView.imeOptions = EditorInfo.IME_ACTION_DONE
        searchView.queryHint = resources.getString(R.string.search_hint)
        searchView.setIconifiedByDefault(false)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                // Reset page number and do search also clear previous search results
                recyclerView.visibility = View.VISIBLE
                welcomeText.visibility = View.GONE
                adapter.movieList.clear()
                adapter.notifyDataSetChanged()
                movieViewModel.pageNumber = 1
                movieViewModel.loadMovieData(query)
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                return true
            }
        })
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        android.R.id.home -> consume { onBackPressed() }
        else -> super.onOptionsItemSelected(item)
    }

    private fun setupRecyclerView() {

        recyclerView.setHasFixedSize(true)
        val decoration = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        recyclerView.addItemDecoration(decoration)
        val layoutManager = LinearLayoutManager(this)
        adapter = MovieAdapter(this, movieViewModel, dataRepo, contextProvider)

        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter

        recyclerView.addOnScrollListener(object : InfiniteScrollListener(layoutManager, movieViewModel) {
            override fun onLoadMore() {
                // Load more data only if totalSearchResults are > than currently loaded
                // search results.
                if (movieViewModel.totalSearchResults > adapter.movieList.size) {
                    val str = searchView.query.toString()
                    if (str.isNotEmpty()) {
                        movieViewModel.loadMovieData(str)
                    }
                } else {
                    if (!endOfResultsShown) {
                        Toast.makeText(
                            applicationContext,
                            this@MovieListActivity.resources.getString(R.string.no_more_search_results),
                            Toast.LENGTH_LONG
                        ).show()
                        endOfResultsShown = true
                    }
                }
            }
        })
    }

    private fun setupViewModelObservers() {
        movieViewModel.movieData.observe(this, Observer<List<Movie>> {
            if (it != null) {
                adapter.movieList.addAll(it)
                adapter.notifyDataSetChanged()
            }
        })

        movieViewModel.isDataLoading.observe(this, Observer<Boolean> {
            it?.let { status ->
                if (status) {
                    busyIndicator.visibility = View.VISIBLE
                } else {
                    busyIndicator.visibility = View.GONE
                }
            }
        })

        movieViewModel.dataLoadStatus.observe(this, Observer<String> {
            it?.let { statusMessage ->
                Toast.makeText(applicationContext, statusMessage, Toast.LENGTH_LONG).show()
            }
        })
    }

    // Reference: https://antonioleiva.com/kotlin-awesome-tricks-for-android/
    private inline fun consume(f: () -> Unit): Boolean {
        f()
        return true
    }

    private fun checkConnectivity() {
        if (networkUtil.isNetworkConnected()) {
            noConnection.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
        } else {
            noConnection.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
            welcomeText.visibility = View.GONE
        }
    }
}
