package com.snijsure.omdbsearch.ui.main


import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.snijsure.omdbsearch.R
import com.snijsure.omdbsearch.data.Movie
import com.snijsure.omdbsearch.databinding.MovieListBinding
import com.snijsure.omdbsearch.util.SharedPreferencesUtil


class MovieAdapter(val context: Context) : RecyclerView.Adapter<MovieAdapter.MovieInfoHolder>() {


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
            val favList = SharedPreferencesUtil.getArrayList(context,
                SharedPreferencesUtil.FAV_LIST)
            if (it.imdbId.isNotEmpty() && favList.contains(it.imdbId)) {
                holder.binding.movieFav.visibility = View.VISIBLE
            }
        }

        holder.binding.movieHolder.setOnClickListener {
            val intent = Intent(it.context, MovieDetailActivity::class.java).apply {
                putExtra(MovieDetailActivity.IMDB_ID, movieList[holder.adapterPosition].imdbId)
            }
            it.context.startActivity(intent)
        }

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


