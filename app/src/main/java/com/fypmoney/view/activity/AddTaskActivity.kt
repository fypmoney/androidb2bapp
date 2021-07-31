package com.fypmoney.view.activity

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseActivity
import com.fypmoney.database.entity.ContactEntity
import com.fypmoney.databinding.ActivityAddTaskBinding
import com.fypmoney.util.AppConstants
import com.fypmoney.util.DialogUtils
import com.fypmoney.view.interfaces.ListItemClickListener
import com.fypmoney.viewmodel.AddTaskViewModel
import kotlinx.android.synthetic.main.activity_add_task.*
import kotlinx.android.synthetic.main.fragment_assigned_task.view.*
import kotlinx.android.synthetic.main.toolbar.*
import nearby.matchinteractmeet.groupalike.Profile.Trips.adapter.FamilyAdapter
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class AddTaskActivity : BaseActivity<ActivityAddTaskBinding, AddTaskViewModel>() , DialogUtils.OnAlertDialogClickListener{

    private var myCalendar: Calendar?=null
    private var myCalendar2: Calendar?=null
    private var typeAdapter: FamilyAdapter?=null
    private lateinit var mViewModel: AddTaskViewModel
    private lateinit var mViewBinding: ActivityAddTaskBinding
    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_add_task
    }
    private var itemsArrayList: ArrayList<ContactEntity> = ArrayList()

    override fun getViewModel(): AddTaskViewModel {
        mViewModel = ViewModelProvider(this).get(AddTaskViewModel::class.java)
        return mViewModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_task)


        mViewBinding = getViewDataBinding()
        setIntentValues(intent)

        myCalendar = Calendar.getInstance()
        myCalendar2 = Calendar.getInstance()


        val date =
            OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                myCalendar?.set(Calendar.YEAR, year)
                myCalendar?.set(Calendar.MONTH, monthOfYear)
                myCalendar?.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                updateLabel()
            }

        etMobileNo.setOnClickListener {
            DatePickerDialog(
                this@AddTaskActivity, date, myCalendar!!
                    .get(Calendar.YEAR), myCalendar!!.get(Calendar.MONTH),
                myCalendar!!.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
        val date2 =
            OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                myCalendar2?.set(Calendar.YEAR, year)
                myCalendar2?.set(Calendar.MONTH, monthOfYear)
                myCalendar2?.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                updateLabel2()
            }

        endTime.setOnClickListener {
            DatePickerDialog(
                this@AddTaskActivity, date2, myCalendar2!!
                    .get(Calendar.YEAR), myCalendar2!!.get(Calendar.MONTH),
                myCalendar2!!.get(Calendar.DAY_OF_MONTH)
            ).show()
        }


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
    private fun updateLabel() {
        val myFormat = "dd/MM/yyyy" //In which you need put here
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        etMobileNo.setText(sdf.format(myCalendar?.getTime()))
    }
    private fun updateLabel2() {
        val myFormat = "dd/MM/yyyy" //In which you need put here
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        endTime.setText(sdf.format(myCalendar2?.getTime()))
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

            }

            override fun onCallClicked(pos: Int) {
                intentToActivity(ContactViewAddMember::class.java)
            }


        }

         typeAdapter =
            FamilyAdapter(itemsArrayList, this, itemClickListener2!!)
        recycler_view.adapter = typeAdapter


    }

    private fun intentToActivity(aClass: Class<*>) {
        startActivityForResult(Intent(this, aClass),13)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)


Log.d("chackresults", data?.dataString.toString())
                val returnValue: ContactEntity? = data?.getParcelableExtra(AppConstants.CONTACT_SELECTED_RESPONSE)
                if (returnValue != null) {
                    itemsArrayList.add(returnValue)
                    typeAdapter?.notifyDataSetChanged()
                }


    }

    override fun onPositiveButtonClick(uniqueIdentifier: String) {

    }


}