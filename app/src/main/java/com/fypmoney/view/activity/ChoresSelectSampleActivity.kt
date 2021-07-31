package com.fypmoney.view.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseActivity
import com.fypmoney.databinding.ViewAddTaskBinding


import com.fypmoney.viewmodel.CreateTaskViewModel

import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.android.synthetic.main.view_user_feeds.*

/*
* This is used to show list of notification
* */
class ChoresSelectSampleActivity : BaseActivity<ViewAddTaskBinding, CreateTaskViewModel>(){
    private lateinit var mViewModel: CreateTaskViewModel
    private lateinit var mViewBinding: ViewAddTaskBinding
    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.view_add_task
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
            context = this@ChoresSelectSampleActivity,
            toolbar = toolbar,
            isBackArrowVisible = true, toolbarTitle = getString(R.string.chore_title)
        )
        setObserver()
        mViewBinding.createTask.setOnClickListener(View.OnClickListener {
            intentToActivity(AddTaskActivity::class.java)
        })
    }


    private fun setObserver() {
        mViewModel.onNotificationClicked.observe(this) {
            if (it) {
               var task= mViewModel.taskSelectedResponse
                val intent = Intent(this, AddTaskActivity::class.java)

                intent.putExtra("sample_title", task.name.toString())
                intent.putExtra("sample_desc",task.description.toString())
                intent.putExtra("sample_amount",task.amount.toString())
                startActivityForResult(intent,23)
                mViewModel.onNotificationClicked.value = false
            }

        }


    }



    private fun intentToActivity(aClass: Class<*>) {
        startActivity(Intent(this@ChoresSelectSampleActivity, aClass))
        finish()
    }
}
