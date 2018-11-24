package com.snijsure.omdbsearch.ui.view

import android.app.Application
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v7.widget.*
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.Toast
import butterknife.BindView
import butterknife.ButterKnife
import com.snijsure.dbrepository.repo.room.DataRepository
import com.snijsure.omdbsearch.R
import com.snijsure.omdbsearch.data.Movie
import com.snijsure.omdbsearch.ui.viewmodel.MovieViewModel
import com.snijsure.omdbsearch.ui.viewmodel.MovieViewModelFactory
import com.snijsure.utility.CoroutinesContextProvider
import dagger.android.support.AndroidSupportInjection
import dagger.android.support.DaggerFragment
import org.jetbrains.annotations.Nullable
import timber.log.Timber
import javax.inject.Inject

class FavoritesFragment : DaggerFragment() {

    @Nullable
    @BindView(R.id.movie_list_view)
    lateinit var recyclerView: RecyclerView
    @BindView(R.id.noFavorites)
    lateinit var noFavorites: AppCompatTextView
    @BindView(R.id.busy_indicator)
    @Nullable
    lateinit var busyIndicator: RelativeLayout
    @BindView(R.id.rootView)
    lateinit var rootView: ConstraintLayout

    @Inject
    lateinit var app: Application
    @Inject
    lateinit var dataRepo: DataRepository
    @Inject
    lateinit var contextProvider: CoroutinesContextProvider

    private lateinit var movieViewModel: MovieViewModel
    private lateinit var adapter: MovieAdapter

    @Inject
    lateinit var movieModelViewFactory: MovieViewModelFactory

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
        Timber.d("On Attach")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.movie_favorites_fragment, container, false)
        ButterKnife.bind(this, rootView)
        movieViewModel = ViewModelProviders.of(this, movieModelViewFactory).get(MovieViewModel::class.java)
        Timber.d("On CreateView")
        setupRecyclerView()
        setupViewModelObservers()
        movieViewModel.pageNumber = 1
        movieViewModel.loadFavorites()
        return rootView
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
        setViewVisibilty(hasItems = false)
    }

    private fun setupViewModelObservers() {
        movieViewModel.movieData.observe(this, Observer<List<Movie>> {
            if (it != null) {
                if (it.isEmpty()) {
                    setViewVisibilty(hasItems = false)
                } else {
                    setViewVisibilty(hasItems = true)
                    adapter.movieList.addAll(it)
                    adapter.notifyDataSetChanged()
                }
            } else {
                setViewVisibilty(hasItems = true)
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

    private fun setViewVisibilty(hasItems: Boolean) {
        if (hasItems) {
            recyclerView.visibility = View.VISIBLE
            noFavorites.visibility = View.GONE
        } else {
            recyclerView.visibility = View.GONE
            noFavorites.visibility = View.VISIBLE
        }
    }
}