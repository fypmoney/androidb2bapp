package com.fypmoney.view.arcadegames.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

class CircularPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle,var listOfFragments:List<Fragment>) : FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun getItemCount() = Integer.MAX_VALUE

    /**
     * Create the fragment based on the position
     */
    override fun createFragment(position: Int) = listOfFragments[position % listOfFragments.size]

    /**
     * Returns the same id for the same Fragment.
     */
    override fun getItemId(position: Int): Long = (position % listOfFragments.size).toLong()

    /**
     * Default implementation works for collections that don't add, move, remove items.
     *
     *
     *
     * When overriding, also override [.getItemId]
     */
    override fun containsItem(itemId: Long): Boolean {
        return super.containsItem(itemId)
    }

    fun getCenterPage(position: Int = 0) = Integer.MAX_VALUE / 2 + position
}