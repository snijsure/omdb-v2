package com.snijsure.omdbsearch.data

import android.databinding.BindingAdapter
import android.widget.ImageView
import com.bumptech.glide.Glide

object ImageBinder {
    @JvmStatic
    @BindingAdapter("image")
    fun loadImage(view: ImageView, imageUrl: String?) {
        if (imageUrl != null && imageUrl.isNotEmpty()) {
            Glide.with(view.context)
                .load(imageUrl)
                .into(view)
        }
    }
}