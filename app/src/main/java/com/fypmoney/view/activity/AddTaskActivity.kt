package com.fypmoney.view.activity

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseActivity
import com.fypmoney.connectivity.ApiConstant
import com.fypmoney.connectivity.retrofit.Allapi
import com.fypmoney.connectivity.retrofit.ApiClient1
import com.fypmoney.databinding.ActivityAddTaskBinding
import com.fypmoney.databinding.ViewAddMemberBinding
import com.fypmoney.databinding.ViewChoresBinding
import com.fypmoney.model.addTaskModal.AddTaskRequest
import com.fypmoney.model.addTaskModal.AddTaskResponce
import com.fypmoney.model.yourTaskModal.TaskRequest
import com.fypmoney.model.yourTaskModal.YourTaskResponse
import com.fypmoney.util.AppConstants
import com.fypmoney.util.DialogUtils
import com.fypmoney.util.SharedPrefUtils
import com.fypmoney.view.adapter.YourTaskStaggeredAdapter
import com.fypmoney.view.fragment.AcceptRejectTaskFragment
import com.fypmoney.viewmodel.AddTaskViewModel
import com.fypmoney.viewmodel.ChoresViewModel
import kotlinx.android.synthetic.main.toolbar.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddTaskActivity : BaseActivity<ActivityAddTaskBinding, AddTaskViewModel>() , DialogUtils.OnAlertDialogClickListener{

    private lateinit var mViewModel: AddTaskViewModel
    private lateinit var mViewBinding: ActivityAddTaskBinding
    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_add_task
    }

    override fun getViewModel(): AddTaskViewModel {
        mViewModel = ViewModelProvider(this).get(AddTaskViewModel::class.java)
        return mViewModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_task)


        mViewBinding = getViewDataBinding()


        setToolbarAndTitle(
            context = this@AddTaskActivity,
            toolbar = toolbar,
            isBackArrowVisible = true, toolbarTitle = getString(R.string.chore_history_title)
        )

        mViewBinding.btnContinue.setOnClickListener {
            addTask()
        }

        setObserver()

        /* findViewById<FloatingActionButton>(R.id.fab).setOnClickListener { view ->
             Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                 .setAction("Action", null).show()
         }*/
    }

    private fun setObserver() {
        /*mViewModel.onFromContactClicked.observe(this) {
            if (it) {
                //intentToActivity(ContactView::class.java)
                mViewModel.onFromContactClicked.value = false
            }
        }*/
    }



    override fun onPositiveButtonClick(uniqueIdentifier: String) {
        TODO("Not yet implemented")
    }

    fun addTask() {
        try {
            val apiInterface = ApiClient1.getClient().create(Allapi::class.java)
            val task= AddTaskRequest(10000,"mm","2021-06-15T19:12:35Z",0,"INR",28397,"2021-06-04T19:12:35Z","Gardening")
            val responseBodyCall = apiInterface.AddTask("web_app", SharedPrefUtils.SF_KEY_ACCESS_TOKEN, task )
            responseBodyCall.enqueue(object : Callback<AddTaskResponce> {
                override fun onResponse(call: Call<AddTaskResponce>, response: Response<AddTaskResponce>) {
                    val id: Int = response.body()?.data!!.id
                    if (id!=null)
                    {
                        Toast.makeText(this@AddTaskActivity,"Task Added",Toast.LENGTH_LONG).show()
                    }
                    Log.d("Statusdata", id!!.toString())
                    //  test!!.text= status.toString()
                }
                override fun onFailure(call: Call<AddTaskResponce>, t: Throwable) {

                }
            })
        } catch (e: Exception) {
        }
    }
}