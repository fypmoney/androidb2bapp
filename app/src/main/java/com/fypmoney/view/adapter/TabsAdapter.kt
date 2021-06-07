package com.fypmoney.view.adapter

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.fypmoney.view.fragment.AssignedTaskFragment
import com.fypmoney.view.fragment.YourTaskFragment

@Suppress("DEPRECATION")
internal class TabsAdapter(
    var context: Context,
    fm: FragmentManager,
    var totalTabs: Int
) :
    FragmentPagerAdapter(fm) {
    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> {
                YourTaskFragment()
            }
            1 -> {
                //AssignedTaskFragment()
                YourTaskFragment()
            }
            else -> getItem(position)
        }
    }
    override fun getCount(): Int {
        return totalTabs
    }
}