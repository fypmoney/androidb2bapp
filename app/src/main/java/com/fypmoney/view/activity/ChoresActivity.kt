package com.fypmoney.view.activity

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.ViewPager
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseActivity
import com.fypmoney.databinding.ViewChoresBinding
import com.fypmoney.model.TaskDetailResponse
import com.fypmoney.model.UpdateTaskGetResponse
import com.fypmoney.util.AppConstants
import com.fypmoney.view.fragment.*
import com.fypmoney.view.interfaces.AcceptRejectClickListener
import com.fypmoney.view.interfaces.MessageSubmitClickListener
import com.fypmoney.viewmodel.ChoresViewModel
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.view_chores.*
import java.util.ArrayList


import android.app.ActivityOptions
import com.fyp.trackr.models.TrackrEvent
import com.fyp.trackr.models.trackr
import com.fypmoney.util.Utility
import kotlinx.android.synthetic.main.toolbar.*


/*
* This is used to handle chores
* */



class ChoresActivity : BaseActivity<ViewChoresBinding, ChoresViewModel>() {

    private var commentstr: String? = null
    private var choresModel: TaskDetailResponse? = null
    private var bottomsheetInsufficient: TaskMessageInsuficientFuntBottomSheet? = null
    private var loader_icon: ImageView? = null

    private var bottomSheetMessage: TaskMessageBottomSheet2? = null
    private var bottomSheetTaskAction: TaskActionBottomSheet? = null
    lateinit var tabLayout: TabLayout
    lateinit var viewPager: ViewPager
    lateinit var ll_show_history: LinearLayout







companion object{
    var mViewModel:ChoresViewModel?=null
}
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)




        setObserver()
        setToolbarAndTitle(
            context = this,
            toolbar = toolbar,
            isBackArrowVisible = true, toolbarTitle = getString(R.string.chore_title)
        )
        tabLayout = findViewById(R.id.tabLayout)
        viewPager = findViewById(R.id.viewPager)
        loader_icon = findViewById(R.id.loader)
        initializeTabs(tabLayout)
        ll_show_history = findViewById(R.id.ll_show_history)

        swipetorefresh.setOnRefreshListener {

            mViewModel?.loading?.postValue(true)
            mViewModel?.onRefresh()

        }
//        Glide.with(this).load(R.drawable.rocket_loader).into(loader_icon!!);




        ll_show_history.setOnClickListener {
            val intent = Intent(this, ChoresHistoryActivity::class.java)
            val bndlAnimation = ActivityOptions.makeCustomAnimation(
                applicationContext,
                com.fypmoney.R.anim.slide_in_up,
                com.fypmoney.R.anim.slide_out_up
            ).toBundle()

            startActivity(intent, bndlAnimation)


        }
        ivNotificationBell.setOnClickListener(View.OnClickListener {
            intentToActivity(NotificationView::class.java)

        })

        myProfile.setOnClickListener(View.OnClickListener {
            intentToActivity(UserProfileView::class.java)
        })

        btnSendOtp.setOnClickListener {
            val intent = Intent(this, ChoresSelectSampleActivity::class.java)
            val bndlAnimation = ActivityOptions.makeCustomAnimation(
                applicationContext,
                com.fypmoney.R.anim.slideinleft,
                com.fypmoney.R.anim.slideinright
            ).toBundle()
            trackr {
                it.name = TrackrEvent.add_misssion
            }
            startActivityForResult(intent, 12, bndlAnimation)

        }

    }

    private fun intentToActivity(aClass: Class<*>) {
        startActivity(Intent(this, aClass))
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

    private fun initializeTabs(tabLayout: TabLayout) {


        val adapter = ViewPagerAdapter(supportFragmentManager)

        adapter.addFragment(YourTasksFragment(), getString(R.string.your_missions))
        adapter.addFragment(AssignedTaskFragment(), getString(R.string.assigned_missions))


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
        mViewModel?.loading?.observe(this) {
            if (it) {

                loader_icon?.visibility = View.VISIBLE
            } else {
                loader_icon?.visibility = View.GONE
                layout2.visibility = View.VISIBLE
                swipetorefresh!!.isRefreshing = it
            }


        }

        mViewModel!!.TaskDetailResponse.observe(this, androidx.lifecycle.Observer { list ->


            if (list.actionAllowed == "REJECT,ACCEPT" || list.actionAllowed == "CANCEL" || list.actionAllowed == "COMPLETE" || list.actionAllowed == "DEPRECIATE,APPRECIATEANDPAY" || list.actionAllowed == "") {
                callTaskActionSheet(list)
            }
        })
        mViewModel!!.bottomSheetStatus.observe(this, androidx.lifecycle.Observer { list ->
            bottomSheetTaskAction?.dismiss()

            bottomSheetMessage?.dismiss()
            mViewModel?.callSampleTask()
            if (list.currentState == "ACCEPT") {

                callTaskMessageSheet(list)
            }
            if (list.currentState == "REJECT") {

                callTaskMessageSheet(list)
            }
            if (list.currentState == "CANCEL") {

                callTaskMessageSheet(list)
            }
            if (list.currentState == "COMPLETE") {

                callTaskMessageSheet(list)
            }
            if (list.currentState == "DEPRECIATE") {

                callTaskMessageSheet(list)
            }
            if (list.currentState == "APPRECIATEANDPAY") {

                callTaskMessageSheet(list)
            }
        })
        mViewModel!!.error.observe(this, { code ->

            if (code == AppConstants.INSUFFICIENT_ERROR_CODE) {
                callInsuficientFundMessageSheet(Utility.convertToRs(mViewModel!!.amountToBeAdded))
            }


        })
    }

    private fun callTaskActionSheet(list: TaskDetailResponse) {
        var itemClickListener2 = object : AcceptRejectClickListener {
            override fun onAcceptClicked(pos: Int, str: String) {
                if (pos == 56) {
                    commentstr = str
                    choresModel = list
                    askForDevicePassword()
                }


            }

            override fun onRejectClicked(pos: Int) {}

            override fun ondimiss() {
                bottomSheetTaskAction?.dismiss()
            }
        }

        bottomSheetTaskAction =
            TaskActionBottomSheet(itemClickListener2, list, mViewModel?.yourtask?.get())
        bottomSheetTaskAction?.dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.RED))
        bottomSheetTaskAction?.show(supportFragmentManager, "TASKACCEPTREJECT")
    }



    private fun callTaskMessageSheet(list: UpdateTaskGetResponse) {
        var itemClickListener2 = object : MessageSubmitClickListener {


            override fun onSubmit() {
                bottomSheetMessage?.dismiss()

            }
        }
        bottomSheetMessage =
            TaskMessageBottomSheet2(itemClickListener2, list, list.id.toString())
        bottomSheetMessage?.dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.RED))
        bottomSheetMessage?.show(supportFragmentManager, "TASKMESSAGE")
    }

    private fun callInsuficientFundMessageSheet(amount: String?) {
        val itemClickListener2 = object : AcceptRejectClickListener {
            override fun onAcceptClicked(pos: Int, str: String) {
                bottomsheetInsufficient?.dismiss()
                intentToPayActivity(ContactListView::class.java, AppConstants.PAY)
            }

            override fun onRejectClicked(pos: Int) {
                bottomsheetInsufficient?.dismiss()
                callActivity(AddMoneyView::class.java,amount)
            }

            override fun ondimiss() {

            }
        }
        bottomsheetInsufficient = TaskMessageInsuficientFuntBottomSheet(itemClickListener2,title = resources.getString(R.string.insufficient_bank_balance),
                subTitle =  resources.getString(R.string.insufficient_bank_body),
                amount = resources.getString(R.string.add_money_title1)+resources.getString(R.string.Rs)+amount)
        bottomsheetInsufficient?.dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.RED))
        bottomsheetInsufficient?.show(supportFragmentManager, "TASKMESSAGE")
    }



    private fun intentToPayActivity(aClass: Class<*>, pay: String? = null) {
        val intent = Intent(this, aClass)
        intent.putExtra(AppConstants.FROM_WHICH_SCREEN, pay)
        startActivity(intent)
    }


    private fun callActivity(aClass: Class<*>,amount:String?) {
        val intent = Intent(this, aClass)
        intent.putExtra("amountshouldbeadded", amount)



        startActivity(intent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == 99) {

            if (mViewModel != null) {

                mViewModel?.callSampleTask()
            }

            viewPager.currentItem = 1


        }

        when (requestCode) {
            AppConstants.DEVICE_SECURITY_REQUEST_CODE -> {
                when (resultCode) {
                    RESULT_OK -> {
                        if (commentstr == null) {
                            commentstr = ""
                        }
                        ChoresActivity.mViewModel!!.callTaskAccept(
                            "APPRECIATEANDPAY", choresModel?.entityId.toString(), commentstr!!

                        )


                    }

                }
            }

        }
    }

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.view_chores
    }

    override fun getViewModel(): ChoresViewModel {
        mViewModel = ViewModelProvider(this).get(ChoresViewModel::class.java)

        return mViewModel!!
    }
}
