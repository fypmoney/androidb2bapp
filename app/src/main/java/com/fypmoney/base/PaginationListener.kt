package com.fypmoney.base

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

abstract class PaginationListener(val layoutManager: LinearLayoutManager) :
    RecyclerView.OnScrollListener() {

    private val TAG = PaginationListener::class.java.simpleName

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)

        val visibleItemCount = layoutManager.childCount
        val totalItemCount = layoutManager.itemCount
        val firstVisibleItemCount = layoutManager.findFirstVisibleItemPosition()

        if (!isLoading()) {
            if (dy > 0) {
                if ((visibleItemCount + firstVisibleItemCount) >= totalItemCount) {
                    try {
                        loadMoreItems()
                    } catch (e: Exception) {
                    }
                }
            } else {
                if (firstVisibleItemCount == 0) {
                    loadMoreTopItems()
                }
            }
        }
    }

    protected abstract fun loadMoreItems()
    protected abstract fun loadMoreTopItems()
    protected abstract fun isLoading(): Boolean
}