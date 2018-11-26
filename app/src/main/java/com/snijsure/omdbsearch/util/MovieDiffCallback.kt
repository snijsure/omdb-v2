package com.snijsure.omdbsearch.util

import android.support.annotation.Nullable
import android.support.v7.util.DiffUtil
import com.snijsure.omdbsearch.data.Movie


class MovieDiffCallback(private val oldList: List<Movie>, private val newList: List<Movie>) :
    DiffUtil.Callback() {

    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].imdbId === newList[newItemPosition].imdbId
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].imdbId === newList[newItemPosition].imdbId
    }

    @Nullable
    override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
        return super.getChangePayload(oldItemPosition, newItemPosition)
    }
}