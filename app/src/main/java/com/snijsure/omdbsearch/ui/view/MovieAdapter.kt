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
import com.snijsure.omdbsearch.util.SharedPreferencesUtil


class MovieAdapter(val activity: Activity) : RecyclerView.Adapter<MovieAdapter.MovieInfoHolder>() {


    private var layoutInflater: LayoutInflater? = null

    // Recommend making the movieList private and then creating a method that updates the list.
    // If you expose the movieList, it breaks encapsulation and external classes then have complete access to the list.
    // Recommend having an updateList(list: List<Movies>) method and then within that method you call notifydatasetchanged
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

    // Recommend trying to simplify the bind logic simpler and try to restrict it to only just setting up the view.
    // Instead of placing the logic in the onBindViewHolder to check if the favorite icon is visible or not,
    // I recommend calculating this ahead of time and creating a new Data Class that has
    // a a field isFavoriteVisible.
    // So that helps to simplify the onBindViewHolder logic
    override fun onBindViewHolder(holder: MovieInfoHolder, position: Int) {
        holder.binding.movie = movieList[position]
        holder.binding.movie?.let { it ->

            // Recommend refactoring out the logic to determine  if the movieFav icon is visibile or not
            // because this is inefficient.
            // The call to SharedPreferencesUtil.getArrayList  has read from Preferences, parse it and then look up by id.
            // This is done each bind
            val favList = SharedPreferencesUtil.getArrayList(activity,
                SharedPreferencesUtil.FAV_LIST)
            if (it.imdbId.isNotEmpty() && favList.contains(it.imdbId)) {
                holder.binding.movieFav.visibility = View.VISIBLE
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


        // Also recommend, refactoring this out and exposing a click listener.
        // The actual implementation to update the favorites list is done in the ViewModel
        holder.binding.movieHolder.setOnLongClickListener {
            //The Action you want to perform
            val id = movieList[holder.adapterPosition].imdbId
            val favList = SharedPreferencesUtil.getArrayList(it.context,
                SharedPreferencesUtil.FAV_LIST)
            if (!favList.contains(id)) {
                favList.add(id)
                SharedPreferencesUtil.saveArrayList(it.context,favList, SharedPreferencesUtil.FAV_LIST)
            }
            notifyItemChanged(holder.adapterPosition)
            true
        }
    }

    inner class MovieInfoHolder(val binding: MovieListBinding) : RecyclerView.ViewHolder(binding.root)
}


