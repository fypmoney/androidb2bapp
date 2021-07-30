package com.fypmoney.view.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseActivity
import com.fypmoney.databinding.ViewCreateTaskBinding


import com.fypmoney.viewmodel.CreateTaskViewModel

import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.android.synthetic.main.view_user_feeds.*

/*
* This is used to show list of notification
* */
class ChoresCreateTaskActivity : BaseActivity<ViewCreateTaskBinding, CreateTaskViewModel>(){
    private lateinit var mViewModel: CreateTaskViewModel
    private lateinit var mViewBinding: ViewCreateTaskBinding
    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.view_create_task
    }

    override fun getViewModel(): CreateTaskViewModel {
        mViewModel = ViewModelProvider(this).get(CreateTaskViewModel::class.java)
        return mViewModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewBinding = getViewDataBinding()
        toolbar_image.visibility = View.VISIBLE
        setToolbarAndTitle(
            context = this@ChoresCreateTaskActivity,
            toolbar = toolbar,
            isBackArrowVisible = true, toolbarTitle = getString(R.string.chore_title)
        )
        setObserver()
    }


    /**
     * Create this method for observe the viewModel fields
     */
    private fun setObserver() {
        mViewModel.onNotificationClicked.observe(this) {
            if (it) {

                mViewModel.onNotificationClicked.value = false
            }

        }


    }






}
