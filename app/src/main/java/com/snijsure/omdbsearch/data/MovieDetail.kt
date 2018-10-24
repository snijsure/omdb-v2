package com.snijsure.omdbsearch.data

import android.databinding.BindingAdapter
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.google.gson.annotations.SerializedName


data class MovieDetail(
    @SerializedName("Title") var title: String,
    @SerializedName("Director") var director: String,
    @SerializedName("Year") var year: String,
    @SerializedName("Plot") var plot: String,
    @SerializedName("Poster") var poster: String
) {

    companion object {
        @JvmStatic
        @BindingAdapter("image")
        fun loadImage(view: ImageView, imageUrl: String) {
            Glide.with(view.context)
                .load(imageUrl)
                .into(view)
        }
    }

}