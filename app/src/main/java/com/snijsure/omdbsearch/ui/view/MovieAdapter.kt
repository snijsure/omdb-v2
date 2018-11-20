package com.snijsure.omdbsearch.ui.view

import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.util.Pair
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.snijsure.dbrepository.repo.room.DataRepository
import com.snijsure.dbrepository.repo.room.FavoriteEntry
import com.snijsure.omdbsearch.R
import com.snijsure.omdbsearch.data.Movie
import com.snijsure.omdbsearch.databinding.MovieListBinding
import com.snijsure.omdbsearch.ui.viewmodel.MovieViewModel
import com.snijsure.utility.CoroutinesContextProvider
import kotlinx.coroutines.*
import timber.log.Timber

class MovieAdapter(
    private val activity: Activity,
    private val viewModel: MovieViewModel,
    private val dataRepo: DataRepository,
    private val contextProvider: CoroutinesContextProvider
) : RecyclerView.Adapter<MovieAdapter.MovieInfoHolder>() {

    private val pendingJobs = Job()
    private val coroutineScope = CoroutineScope(contextProvider.io + pendingJobs)

    private var layoutInflater: LayoutInflater? = null
    var movieList = mutableListOf<Movie>()

    override fun getItemCount(): Int {
        return movieList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieInfoHolder {
        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(parent.context)
        }
        val binding = DataBindingUtil.inflate<MovieListBinding>(
            layoutInflater!!,
            R.layout.movie_list, parent,
            false
        )
        return MovieInfoHolder(binding)
    }

    override fun onBindViewHolder(holder: MovieInfoHolder, position: Int) {
        holder.binding.movie = movieList[position]
        holder.binding.movie?.let { it ->
            coroutineScope.launch {
                var visibility = View.GONE
                if (it.imdbId.isNotEmpty())
                    visibility = if (viewModel.isFavorite(it))
                        View.VISIBLE
                    else
                        View.GONE
                withContext(Dispatchers.Main) {
                    holder.binding.movieFav.visibility = visibility
                }
            }
        }
    }

    /**
     * Note we register callback handler in InfoHolder and not onBindViewHolder, a common mistake
     */

    inner class MovieInfoHolder(val binding: MovieListBinding) : RecyclerView.ViewHolder(binding.root),
        View.OnClickListener, View.OnLongClickListener {

        /**
         * Toggles favorite status of the movie, not the job is launched on GlobalScope
         */
        override fun onLongClick(v: View?): Boolean {
            var visibility: Int
            coroutineScope.launch {
                try {
                    // Question: Should UI be calling ViewModel and let ViewModel tell
                    // it item is favored or directly use datarepo??
                    val movie = movieList[adapterPosition]
                    if (dataRepo.isFavorite(movie.imdbId) != 0) {
                        visibility = View.GONE
                        dataRepo.removeEntryFromFavorites(movie.imdbId)
                    } else {
                        val entry = FavoriteEntry(title = movie.title, imdbid = movie.imdbId,
                            poster = movie.poster)
                        dataRepo.addMovieToFavorites(entry)
                        visibility = View.VISIBLE
                    }
                    withContext(contextProvider.main) {
                        binding.movieFav.visibility = visibility
                    }
                } catch (e: Exception) {
                    Timber.e("Unable to add item to favorites list")
                }
            }
            return true
        }

        /**
         * Launches detail activity, the intent is launched with sharedImage and sharedText as
         * intent parameters so shared element transition will cause image and move title to
         * animate correctly.
         */
        override fun onClick(v: View?) {
            v?.let { view ->
                val pair1 = Pair.create<View, String>(
                    binding.moviePoster,
                    activity.resources.getString(R.string.sharedImageView)
                )
                val pair2 = Pair.create<View, String>(
                    binding.movieTitle,
                    activity.resources.getString(R.string.sharedText)
                )
                val options = ActivityOptions.makeSceneTransitionAnimation(activity, pair1, pair2)
                val intent = Intent(view.context, MovieDetailActivity::class.java).apply {
                    putExtra(MovieDetailActivity.IMDB_ID, movieList[adapterPosition].imdbId)
                }
                view.context.startActivity(intent, options.toBundle())
            }
        }

        init {
            binding.movieHolder.setOnClickListener(this)
            binding.movieHolder.setOnLongClickListener(this)
        }
    }
}

