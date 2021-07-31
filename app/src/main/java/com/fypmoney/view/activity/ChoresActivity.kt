package com.fypmoney.view.activity

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.ViewPager
import com.fypmoney.R
import com.fypmoney.database.entity.TaskEntity
import com.fypmoney.view.fragment.AcceptRejectTaskFragment
import com.fypmoney.view.fragment.YourTasksFragment
import com.fypmoney.view.fragment.AssignedTaskFragment
import com.fypmoney.viewmodel.ChoresViewModel
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.view_chores.*
import java.util.ArrayList

/*
* This is used to handle chores
* */



class ChoresActivity :AppCompatActivity(),
    AcceptRejectTaskFragment.OnBottomSheetClickListener {

    lateinit var tabLayout: TabLayout
    lateinit var viewPager: ViewPager
    lateinit var ll_show_history: LinearLayout







companion object{
    var mViewModel:ChoresViewModel?=null
}
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.view_chores)

        setObserver()
        mViewModel = ViewModelProvider(this).get(ChoresViewModel::class.java)



        tabLayout = findViewById(R.id.tabLayout)
        viewPager = findViewById(R.id.viewPager)
        initializeTabs(viewPager,tabLayout)
        ll_show_history = findViewById(R.id.ll_show_history)





        ll_show_history.setOnClickListener {
            val intent = Intent(this, ChoresHistoryActivity::class.java)
// To pass any data to next activity
            //intent.putExtra("keyIdentifier", value)
// start your next activity
            startActivity(intent)
        }
        btnSendOtp.setOnClickListener {
            val intent = Intent(this, ChoresSelectSampleActivity::class.java)

            startActivity(intent)
        }

    }
    internal class ViewPagerAdapter(manager: FragmentManager?) :
        FragmentPagerAdapter(manager!!) {
        private val mFragmentList: MutableList<Fragment> = ArrayList()
        private val mFragmentTitleList: MutableList<String> = ArrayList()
        override fun getItem(position: Int): Fragment {
            return mFragmentList[position]
        }

        override fun getCount(): Int {
            return mFragmentList.size
        }

        fun addFragment(fragment: Fragment, title: String) {
            mFragmentList.add(fragment)
            mFragmentTitleList.add(title)
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return mFragmentTitleList[position]
        }
    }

    private fun initializeTabs(viewPager: ViewPager, tabLayout: TabLayout) {




        val adapter = ViewPagerAdapter(supportFragmentManager)

        adapter.addFragment(YourTasksFragment(), "Your Task")
        adapter.addFragment(AssignedTaskFragment(), "Assigned tasks")


        viewPager.adapter = adapter

        tabLayout.setupWithViewPager(viewPager);


    }

    /**
     * Create this method for observe the viewModel fields
     */
    private fun setObserver() {
        mViewModel?.onAddMoneyClicked?.observe(this) {
            //intentToActivity()
        }

    }



    override fun onBottomSheetButtonClick() {
        /*when (type) {
            AppConstants.LEAVE_MEMBER -> {
                //mViewModel.callLeaveFamilyApi()
            }
            AppConstants.REMOVE_MEMBER -> {
                //mViewModel.callRemoveMemberApi()

            }
        }*/
    }


    private fun callAcceptRjectSheet(taskEntity: TaskEntity?) {
        val bottomSheet =
            AcceptRejectTaskFragment(
                taskEntity, this
            )
        bottomSheet.dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.RED))
        bottomSheet.show(supportFragmentManager, "CeeptRejectBottomSheet")
    }



}
