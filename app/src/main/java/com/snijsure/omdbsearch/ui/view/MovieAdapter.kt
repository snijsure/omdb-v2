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
import com.snijsure.omdbsearch.R
import com.snijsure.omdbsearch.data.Movie
import com.snijsure.omdbsearch.databinding.MovieListBinding
import com.snijsure.omdbsearch.ui.viewmodel.MovieViewModel
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import timber.log.Timber

class MovieAdapter(private val activity: Activity, private val viewModel: MovieViewModel) : RecyclerView.Adapter<MovieAdapter.MovieInfoHolder>() {


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
            GlobalScope.launch(Main){
                if (it.imdbId.isNotEmpty() && viewModel.isFavorite(it)) {
                    holder.binding.movieFav.visibility = View.VISIBLE
                }
                else {
                    holder.binding.movieFav.visibility = View.GONE
                }
            }
        }

        holder.binding.movieHolder.setOnClickListener {
            //val pair = arrayOfNulls<Pair>(2)
            val pair1 = Pair.create<View,String>(holder.binding.moviePoster,
                activity.resources.getString(R.string.sharedImageView))
            val pair2 = Pair.create<View,String>(holder.binding.movieTitle,
                activity.resources.getString(R.string.sharedText))

            val options = ActivityOptions.makeSceneTransitionAnimation(activity, pair1,pair2)
            val intent = Intent(it.context, MovieDetailActivity::class.java).apply {
                putExtra(MovieDetailActivity.IMDB_ID, movieList[holder.adapterPosition].imdbId)
            }
            it.context.startActivity(intent,options.toBundle())
        }

        holder.binding.movieHolder.setOnLongClickListener {
            //The Action you want to perform
            viewModel.addToFavorite(movieList[holder.adapterPosition])
            notifyItemChanged(holder.adapterPosition)
            true
        }
    }

    inner class MovieInfoHolder(val binding: MovieListBinding) : RecyclerView.ViewHolder(binding.root)
}


