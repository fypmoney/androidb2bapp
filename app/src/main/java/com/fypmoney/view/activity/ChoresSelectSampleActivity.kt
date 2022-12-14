package com.fypmoney.view.activity


import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseActivity
import com.fypmoney.databinding.ViewAddTaskBinding
import com.fypmoney.model.SampleTaskModel
import com.fypmoney.view.adapter.SampleTaskAdapter
import com.fypmoney.view.interfaces.ListItemClickListener
import kotlinx.android.synthetic.main.toolbar.*

import com.fypmoney.viewmodel.CreateTaskViewModel
import kotlinx.android.synthetic.main.activity_add_task.*
import kotlinx.android.synthetic.main.toolbar.*


import kotlinx.android.synthetic.main.view_add_task.*


/*
* This is used to show list of notification
* */
class ChoresSelectSampleActivity : BaseActivity<ViewAddTaskBinding, CreateTaskViewModel>(){
    private lateinit var mViewModel: CreateTaskViewModel
    private lateinit var mViewBinding: ViewAddTaskBinding
    private var typeAdapter: SampleTaskAdapter? = null
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

    private var itemsArrayList: ArrayList<SampleTaskModel.SampleTaskDetails> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewBinding = getViewDataBinding()

        setToolbarAndTitle(
            context = this@ChoresSelectSampleActivity,
            toolbar = toolbar,
            isBackArrowVisible = true, toolbarTitle = getString(R.string.chore_title)
        )
        setObserver()
//        Glide.with(this).load(R.drawable.rocket_loader).into(loader!!);

        setRecyclerView()
    }

    private fun setRecyclerView() {


        var itemClickListener2 = object : ListItemClickListener {


            override fun onItemClicked(pos: Int) {

                var task = itemsArrayList[pos]
                val intent = Intent(this@ChoresSelectSampleActivity, AddTaskActivity::class.java)

                intent.putExtra("sample_title", task.name.toString())
                intent.putExtra("sample_desc", task.description.toString())
                intent.putExtra("sample_amount", task.amount.toString())
                intent.putExtra("numberofdays", task.numberOfDays)
                intent.putExtra("emoji", task.emojis)
                val bndlAnimation = ActivityOptions.makeCustomAnimation(
                    applicationContext,
                    com.fypmoney.R.anim.slideinleft,
                    com.fypmoney.R.anim.slideinright
                ).toBundle()


                startActivityForResult(intent, 23, bndlAnimation)
                mViewModel.onNotificationClicked.value = false
            }

            override fun onCallClicked(pos: Int) {
                val intent = Intent(this@ChoresSelectSampleActivity, AddTaskActivity::class.java)


                val bndlAnimation = ActivityOptions.makeCustomAnimation(
                    applicationContext,
                    com.fypmoney.R.anim.slideinleft,
                    com.fypmoney.R.anim.slideinright
                ).toBundle()


                startActivityForResult(intent, 23, bndlAnimation)

            }
        }

        typeAdapter =
            SampleTaskAdapter(itemsArrayList, this, itemClickListener2!!)
        recyclerview.adapter = typeAdapter


    }

    private fun setObserver() {
        mViewModel!!.sampleTaskList.observe(this, androidx.lifecycle.Observer { list ->
            itemsArrayList.clear()
            itemsArrayList.addAll(list)
            typeAdapter!!.notifyDataSetChanged()

        })
        mViewModel?.loading?.observe(this) {
            if (it) {
                loader?.visibility = View.VISIBLE
            } else {
                loader?.visibility = View.GONE
            }
        }


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == 88) {

            setResult(99)
            finish()
        }
    }

    private fun intentToActivity(aClass: Class<*>) {
        startActivity(Intent(this@ChoresSelectSampleActivity, aClass))

    }
}
