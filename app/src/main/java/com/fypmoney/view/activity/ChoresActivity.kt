package com.fypmoney.view.activity

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.widget.LinearLayout
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.ViewPager
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseActivity
import com.fypmoney.database.entity.ContactEntity
import com.fypmoney.database.entity.TaskEntity
import com.fypmoney.databinding.ViewChoresBinding
import com.fypmoney.util.AppConstants
import com.fypmoney.view.adapter.TabsAdapter
import com.fypmoney.view.fragment.AcceptRejectTaskFragment
import com.fypmoney.viewmodel.ChoresViewModel
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.card_your_task.*
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.android.synthetic.main.view_user_feeds.*

/*
* This is used to handle chores
* */

class ChoresActivity : BaseActivity<ViewChoresBinding, ChoresViewModel>(),
    AcceptRejectTaskFragment.OnBottomSheetClickListener {
    private lateinit var mViewModel: ChoresViewModel
    lateinit var tabLayout: TabLayout
    lateinit var viewPager: ViewPager
    lateinit var ll_show_history: LinearLayout

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.view_chores
    }

    override fun getViewModel(): ChoresViewModel {
        mViewModel = ViewModelProvider(this).get(ChoresViewModel::class.java)
        return mViewModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setToolbarAndTitle(
            context = this@ChoresActivity,
            toolbar = toolbar,
            isBackArrowVisible = true, toolbarTitle = getString(R.string.chore_title)
        )
        setObserver()

        tabLayout = findViewById(R.id.tabLayout)
        viewPager = findViewById(R.id.viewPager)
        ll_show_history = findViewById(R.id.ll_show_history)
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.sub_title1)))
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.sub_title2)))
        tabLayout.tabGravity = TabLayout.GRAVITY_FILL
        val adapter = TabsAdapter(
            this, supportFragmentManager,
            tabLayout.tabCount
        )
        viewPager.adapter = adapter
        viewPager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabLayout))
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                viewPager.currentItem = tab.position
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })

        ll_show_history.setOnClickListener {
            val intent = Intent(this, ChoresHistoryActivity::class.java)
// To pass any data to next activity
            //intent.putExtra("keyIdentifier", value)
// start your next activity
            startActivity(intent)
        }

    }


    /**
     * Create this method for observe the viewModel fields
     */
    private fun setObserver() {
        mViewModel.onAddMoneyClicked.observe(this) {
            //intentToActivity()
        }

    }

    /**
     * Method to navigate to the different activity
     */
    private fun intentToActivity(contactEntity: ContactEntity, aClass: Class<*>) {
        val intent = Intent(this@ChoresActivity, aClass)
        intent.putExtra(AppConstants.CONTACT_SELECTED_RESPONSE, contactEntity)
        startActivity(intent)
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
