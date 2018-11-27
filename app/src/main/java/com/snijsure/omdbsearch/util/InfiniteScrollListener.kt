package com.snijsure.omdbsearch.util

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import com.snijsure.omdbsearch.ui.viewmodel.MovieViewModel

/**
 * A scroll listener for RecyclerView to load more items as you approach the end.
 *
 * Adapted from https://gist.github.com/ssinss/e06f12ef66c51252563e
 */
abstract class InfiniteScrollListener(
    private val layoutManager: LinearLayoutManager,
    private val dataLoading: MovieViewModel
) : RecyclerView.OnScrollListener() {
    private val loadMoreRunnable = Runnable { onLoadMore() }

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        // bail out if scrolling upward or already loading data
        if (dy < 0 || dataLoading.isDataLoading.value == true) return

        val visibleItemCount = recyclerView.childCount
        val totalItemCount = layoutManager.itemCount
        val firstVisibleItem = layoutManager.findFirstVisibleItemPosition()

        if (totalItemCount - visibleItemCount < firstVisibleItem + VISIBLE_THRESHOLD) {
            recyclerView.post(loadMoreRunnable)
        }
    }

    abstract fun onLoadMore()

    companion object {
        // The minimum number of items remaining before we should loading more.
        private const val VISIBLE_THRESHOLD = 5
    }
}
