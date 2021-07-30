package com.fypmoney.view.activity

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseActivity
import com.fypmoney.databinding.ActivityAddTaskBinding
import com.fypmoney.model.AssignedTaskResponse
import com.fypmoney.util.DialogUtils
import com.fypmoney.view.interfaces.ListItemClickListener
import com.fypmoney.viewmodel.AddTaskViewModel
import kotlinx.android.synthetic.main.activity_add_task.*
import kotlinx.android.synthetic.main.fragment_assigned_task.view.*

import kotlinx.android.synthetic.main.toolbar.*
import nearby.matchinteractmeet.groupalike.Profile.Trips.adapter.FamilyAdapter

class AddTaskActivity : BaseActivity<ActivityAddTaskBinding, AddTaskViewModel>() , DialogUtils.OnAlertDialogClickListener{

    private lateinit var mViewModel: AddTaskViewModel
    private lateinit var mViewBinding: ActivityAddTaskBinding
    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_add_task
    }
    private var itemsArrayList: ArrayList<AssignedTaskResponse> = ArrayList()

    override fun getViewModel(): AddTaskViewModel {
        mViewModel = ViewModelProvider(this).get(AddTaskViewModel::class.java)
        return mViewModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_task)


        mViewBinding = getViewDataBinding()
        setIntentValues(intent)



  itemsArrayList.add(AssignedTaskResponse())
        itemsArrayList.add(AssignedTaskResponse())
        itemsArrayList.add(AssignedTaskResponse())
        itemsArrayList.add(AssignedTaskResponse())
        itemsArrayList.add(AssignedTaskResponse())

        setRecyclerView()
        setToolbarAndTitle(
            context = this@AddTaskActivity,
            toolbar = toolbar,
            isBackArrowVisible = true, toolbarTitle = getString(R.string.chore_history_title)
        )

        btnContinue.setOnClickListener {
            if(validate()){
                mViewModel.callSampleTask(add_money_editext.text.toString(),et_title.text.toString())
            }
        }

    }

    private fun setIntentValues(intent: Intent?) {
        val sampleTitle = intent?.getStringExtra("sample_title")
        var sampleDescription = intent?.getStringExtra("sample_desc")
        var sample_amount = intent?.getStringExtra("sample_amount")

        et_title.setText(sampleTitle)
        et_desc.setText(sampleDescription)
        add_money_editext.setText(sample_amount)
    }

    private fun setRecyclerView() {


        var itemClickListener2 = object : ListItemClickListener {


            override fun onItemClicked(pos: Int) {
                TODO("Not yet implemented")
            }

            override fun onCallClicked(pos: Int) {

            }


        }

        var typeAdapter =
            FamilyAdapter(itemsArrayList, this, itemClickListener2!!)
        recycler_view.adapter = typeAdapter


    }
     fun validate(): Boolean {
        var success: Boolean = true

        if (add_money_editext.text.toString().trim().isNullOrEmpty()) {
            success = false
            add_money_editext.error = "Enter Amount"
        }
        if (et_title.text.toString().trim().isNullOrEmpty()) {
            success = false
            et_title.error = "Enter Task title"
        }






        return success
    }



    override fun onPositiveButtonClick(uniqueIdentifier: String) {
        TODO("Not yet implemented")
    }


}