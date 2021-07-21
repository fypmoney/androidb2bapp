package com.fypmoney.view.fragment


import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import com.fypmoney.R
import com.fypmoney.databinding.BottomSheetDidUKnowBinding
import com.fypmoney.util.AppConstants
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener


/*
* This is used to show did u know data
* */
class DidUKnowBottomSheet :
    BottomSheetDialogFragment() {

    override fun getTheme(): Int = R.style.BottomSheetDialogTheme
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        BottomSheetDialog(requireContext(), theme)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(
            R.layout.bottom_sheet_did_u_know,
            container,
            false
        )
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val bottomSheet = BottomSheetDialog(requireContext())
        val bindingSheet = DataBindingUtil.inflate<BottomSheetDidUKnowBinding>(
            layoutInflater,
            R.layout.bottom_sheet_did_u_know,
            null,
            false
        )

        val tabLayout = view.findViewById<TabLayout>(R.id.tablayout)
        val viewPager = view.findViewById<ViewPager>(R.id.viewPager)


        for (i in 0 until 7) {
            tabLayout.addTab(tabLayout.newTab().setText("" + (i + 1).toString()))
        }


        tabLayout.setupWithViewPager(viewPager)
        val adapter = ViewPagerAdapter(
            childFragmentManager,
            7, resourceId = "https://cdn.pixabay.com/photo/2015/04/23/22/00/tree-736885_960_720.jpg"
        )
        viewPager.adapter = adapter

        tabLayout.setOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                viewPager.currentItem = tab.position
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })


        /*  val lineRecyclerview = view.findViewById<RecyclerView>(R.id.my_recycler_view1)!!
          var benefitsAdapter = BenefitsAdapter()
          lineRecyclerview.setNestedScrollingEnabled(false);


          val imageList = ArrayList<Int>()
          imageList.add(R.drawable.card)
          imageList.add(R.drawable.card)
          imageList.add(R.drawable.card)
          imageList.add(R.drawable.card)
          imageList.add(R.drawable.card)
          imageList.add(R.drawable.card)
          imageList.add(R.drawable.card)

          benefitsAdapter.setList(imageList)
          lineRecyclerview.adapter=benefitsAdapter

  */

        bottomSheet.setContentView(bindingSheet.root)

        return view
    }


    interface OnCardSettingsClickListener {
        fun onCardSettingsClick(position: Int)
    }


    class ViewPagerAdapter(fm: FragmentManager?, var mNumOfTabs: Int, var resourceId: String) :
        FragmentStatePagerAdapter(fm!!) {
        var fragment: Fragment? = null
        override fun getItem(position: Int): Fragment {
            for (i in 0 until mNumOfTabs) {
                if (i == position) {
                    fragment = DidUKnowFragment()
                    val arg = Bundle()
                    arg.putString(AppConstants.FEED_TYPE_DID_YOU_KNOW, resourceId)
                    fragment!!.arguments = arg
                    break
                }
            }
            return fragment!!
        }

        override fun getCount(): Int {
            return mNumOfTabs
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return super.getPageTitle(position)
        }
    }
}