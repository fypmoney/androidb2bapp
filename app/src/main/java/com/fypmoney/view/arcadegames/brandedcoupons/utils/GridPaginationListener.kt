package com.fypmoney.view.arcadegames.brandedcoupons.utils

import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

abstract class GridPaginationListener(val layoutManager: GridLayoutManager) :
    RecyclerView.OnScrollListener() {

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