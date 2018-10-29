package com.snijsure.omdbsearch.data

import android.databinding.BindingAdapter
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.google.gson.annotations.SerializedName


data class Movie(
    @SerializedName("Title") var title: String,
    @SerializedName("imdbID") var imdbId: String,
    @SerializedName("Poster") var poster: String
)