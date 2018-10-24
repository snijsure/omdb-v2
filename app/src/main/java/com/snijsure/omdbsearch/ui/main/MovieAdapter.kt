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
import timber.log.Timber


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

        holder.binding.movieHolder.setOnClickListener {
            Timber.d("SUBODH you clicked on ${holder.adapterPosition} imdbid ${movieList[holder.adapterPosition].imdbId}")

            val intent = Intent(it.context, MovieDetailActivity::class.java).apply {
                putExtra(MovieDetailActivity.IMDB_ID, movieList[holder.adapterPosition].imdbId)
            }
            it.context.startActivity(intent)
        }
    }

    inner class MovieInfoHolder(val binding: MovieListBinding) : RecyclerView.ViewHolder(binding.root)
}

