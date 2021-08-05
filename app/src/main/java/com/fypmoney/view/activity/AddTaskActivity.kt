package com.fypmoney.view.activity

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseActivity
import com.fypmoney.database.entity.ContactEntity
import com.fypmoney.database.entity.MemberEntity
import com.fypmoney.databinding.ActivityAddTaskBinding
import com.fypmoney.model.UpdateTaskGetResponse
import com.fypmoney.util.AppConstants
import com.fypmoney.util.DialogUtils
import com.fypmoney.view.fragment.taskAddedMessageBottomSheet
import com.fypmoney.view.interfaces.ListItemClickListener
import com.fypmoney.view.interfaces.MessageSubmitClickListener
import com.fypmoney.viewmodel.AddTaskViewModel
import kotlinx.android.synthetic.main.activity_add_task.*
import kotlinx.android.synthetic.main.fragment_assigned_task.view.*
import kotlinx.android.synthetic.main.toolbar.*
import nearby.matchinteractmeet.groupalike.Profile.Trips.adapter.addmemberAdapter
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class AddTaskActivity : BaseActivity<ActivityAddTaskBinding, AddTaskViewModel>() , DialogUtils.OnAlertDialogClickListener{

    private var selectedmember: MemberEntity? = null
    private var myCalendar: Calendar? = null
    private var myCalendar2: Calendar? = null
    private var typeAdapter: addmemberAdapter? = null
    private lateinit var mViewModel: AddTaskViewModel
    private lateinit var mViewBinding: ActivityAddTaskBinding
    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_add_task
    }

    private var itemsArrayList: ArrayList<MemberEntity> = ArrayList()

    override fun getViewModel(): AddTaskViewModel {
        mViewModel = ViewModelProvider(this).get(AddTaskViewModel::class.java)
        return mViewModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mViewBinding = getViewDataBinding()
        setIntentValues(intent)
        var numberOfdays = intent?.getIntExtra("numberofdays", -1)
        myCalendar2 = Calendar.getInstance()
        if (numberOfdays != null && numberOfdays > 0) {
            var dt = Date()

            myCalendar2!!.time = dt
            myCalendar2!!.add(Calendar.DATE, numberOfdays)
            dt = myCalendar2!!.time
            updateLabel2()

        }
        myCalendar = Calendar.getInstance()
        updateLabel()
        calendarListners()
        setamount()




        setRecyclerView()
        setToolbarAndTitle(
            context = this@AddTaskActivity,
            toolbar = toolbar,
            isBackArrowVisible = true, toolbarTitle = getString(R.string.chore_history_title)
        )

        btnContinue.setOnClickListener {
            if (validate()) {


                val outputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")

                val startdate = outputFormat.format(myCalendar?.time)
                val enddate = outputFormat.format(myCalendar2?.time)
                if (selectedmember != null) {
                    mViewModel.callAddTask(
                        add_money_editext.text.toString(),
                        et_title.text.toString(),
                        selectedmember?.userId?.toInt().toString(),
                        et_desc.text.toString(),
                        startdate,
                        enddate
                    )
                } else {
                    Toast.makeText(this, "Select any contact", Toast.LENGTH_SHORT).show()
                }

            }
        }
        setObserver()


    }

    private fun setObserver() {
        mViewModel!!.bottomSheetStatus.observe(this, androidx.lifecycle.Observer { list ->

            callTaskMessageSheet(list)

        })

        mViewModel!!.familyMemberList.observe(this, { list ->

            itemsArrayList.addAll(list)
            typeAdapter?.notifyDataSetChanged()
        })
    }

    private fun callTaskMessageSheet(list: UpdateTaskGetResponse) {
        var itemClickListener2 = object : MessageSubmitClickListener {
            override fun onSubmit() {
                setResult(88)
                finish()

            }
        }
        val bottomSheet =
            taskAddedMessageBottomSheet(itemClickListener2, list)
        bottomSheet.dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.RED))
        bottomSheet.show(supportFragmentManager, "TASKMESSAGE")
    }

    private fun setamount() {
        amount4.setOnClickListener(View.OnClickListener {
            add_money_editext.setText("1000")
        })
        amount1.setOnClickListener(View.OnClickListener {
            add_money_editext.setText("50")
        })
        amount2.setOnClickListener(View.OnClickListener {
            add_money_editext.setText("100")
        })
        amount3.setOnClickListener(View.OnClickListener {
            add_money_editext.setText("500")
        })
    }

    private fun calendarListners() {
        val date =
            OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                myCalendar?.set(Calendar.YEAR, year)
                myCalendar?.set(Calendar.MONTH, monthOfYear)
                myCalendar?.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                updateLabel()
            }

        mViewBinding.etMobileNo.setOnClickListener {
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
        mViewBinding.endTime.setOnClickListener {
            DatePickerDialog(
                this@AddTaskActivity, date2, myCalendar2!!
                    .get(Calendar.YEAR), myCalendar2!!.get(Calendar.MONTH),
                myCalendar2!!.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    }

    private fun updateLabel() {
        val myFormat = "dd/MM/yyyy" //In which you need put here
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        mViewBinding.etMobileNo.setText(sdf.format(myCalendar?.time))
    }
    private fun updateLabel2() {
        val myFormat = "dd/MM/yyyy" //In which you need put here
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        mViewBinding.endTime.setText(sdf.format(myCalendar2?.time))
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

        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recycler_view.layoutManager = layoutManager
        var itemClickListener2 = object : ListItemClickListener {


            override fun onItemClicked(pos: Int) {
                selectedmember = itemsArrayList[pos]

            }

            override fun onCallClicked(pos: Int) {

                intentToActivity(ContactViewAddMember::class.java)
            }
        }

         typeAdapter =
             addmemberAdapter(itemsArrayList, this, itemClickListener2!!)
        recycler_view.adapter = typeAdapter


    }

    private fun intentToActivity(aClass: Class<*>) {
        startActivityForResult(Intent(this, aClass),13)
    }
     fun validate(): Boolean {
        var success: Boolean = true

         if (add_money_editext.text.toString().trim().isEmpty()) {
             success = false
             add_money_editext.error = "Enter Amount"
         }
         if (et_title.text.toString().trim().isEmpty()) {
             success = false
             et_title.error = "Enter Task title"
         }


        return success
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

                val returnValue: ContactEntity? = data?.getParcelableExtra(AppConstants.CONTACT_SELECTED_RESPONSE)
                if (returnValue != null) {
                    var member = MemberEntity()
                    Log.d("chackid", returnValue.id.toString())
                    member.id = returnValue.id
                    member.mobileNo = returnValue.contactNumber
                    member.name = returnValue.firstName

                    itemsArrayList.add(member)
                    typeAdapter?.notifyDataSetChanged()
                }


    }

    override fun onPositiveButtonClick(uniqueIdentifier: String) {

    }


}