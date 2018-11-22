package com.snijsure.omdbsearch.ui.view
import android.app.Application
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v7.widget.*

import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.view.MenuInflater

import android.view.inputmethod.EditorInfo
import android.widget.RelativeLayout
import android.widget.Toast
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.Optional
import com.snijsure.dbrepository.repo.room.DataRepository
import com.snijsure.omdbsearch.R
import com.snijsure.omdbsearch.data.Movie
import com.snijsure.omdbsearch.ui.viewmodel.MovieViewModel
import com.snijsure.omdbsearch.ui.viewmodel.MovieViewModelFactory
import com.snijsure.omdbsearch.util.InfiniteScrollListener
import com.snijsure.omdbsearch.util.NetworkUtil
import com.snijsure.utility.CoroutinesContextProvider
import dagger.android.support.AndroidSupportInjection
import dagger.android.support.DaggerFragment
import org.jetbrains.annotations.Nullable
import timber.log.Timber
import javax.inject.Inject

class MovieListFragment : DaggerFragment() {

    @Nullable
    @BindView(R.id.movie_list_view)
    lateinit var recyclerView: RecyclerView
    @BindView(R.id.no_connection)
    lateinit var noConnection: AppCompatTextView
    @BindView(R.id.busy_indicator)
    @Nullable
    lateinit var busyIndicator: RelativeLayout
    @BindView(R.id.welcome_text)
    lateinit var welcomeText: AppCompatTextView
    @BindView(R.id.rootView)
    lateinit var rootView: ConstraintLayout

    @Inject
    lateinit var app: Application

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
    private var endOfResultsShown = false
    lateinit var searchView: SearchView

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.movie_list_fragment, container, false)
        ButterKnife.bind(this, view)
        setHasOptionsMenu(true)
        movieViewModel = ViewModelProviders.of(this, movieModelViewFactory).get(MovieViewModel::class.java)
        setupRecyclerView()
        setupViewModelObservers()
        return view
    }

    override fun onCreateOptionsMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.main_menu, menu)

        val searchMenuItem = menu.findItem(R.id.action_search)

        searchView = searchMenuItem.actionView as SearchView
        searchView.isSubmitButtonEnabled = false
        searchView.imeOptions = EditorInfo.IME_ACTION_DONE
        searchView.queryHint = resources.getString(R.string.search_hint)
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
    }

    private fun setupRecyclerView() {

        recyclerView.setHasFixedSize(true)
        (recyclerView.itemAnimator as DefaultItemAnimator).supportsChangeAnimations = false

        val decoration = DividerItemDecoration(this.context, DividerItemDecoration.VERTICAL)
        recyclerView.addItemDecoration(decoration)
        val layoutManager = LinearLayoutManager(this.context)
        adapter = MovieAdapter(this.activity!!, movieViewModel, dataRepo, contextProvider)

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
                            app,
                            resources.getString(R.string.no_more_search_results),
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
                Toast.makeText(app, statusMessage, Toast.LENGTH_LONG).show()
            }
        })
    }

    override fun onResume() {
        super.onResume()
        checkConnectivity()
        rootView.requestFocus()
    }

    override fun onDestroy() {
        super.onDestroy()
        Timber.d("SUBODH frament is destroyed")
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