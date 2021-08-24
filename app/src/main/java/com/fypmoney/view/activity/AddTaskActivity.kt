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
import com.fypmoney.view.adapter.addmemberAdapter
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

import kotlinx.android.synthetic.main.bottom_sheet_response_task.*
import kotlinx.android.synthetic.main.bottom_sheet_task_added_message.*
import android.text.Editable

import android.text.TextWatcher
import com.fypmoney.util.Utility.removePlusOrNineOneFromNo
import kotlinx.android.synthetic.main.activity_add_task.add_money_editext
import kotlinx.android.synthetic.main.view_add_money.*


class AddTaskActivity : BaseActivity<ActivityAddTaskBinding, AddTaskViewModel>(),
    DialogUtils.OnAlertDialogClickListener {

    private var currentDate: Calendar? = null
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
        currentDate = myCalendar2
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

        add_money_editext.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
//                if (s.toString().length == 1 && s.toString().startsWith("0")) {
//                    s.clear()
//                }
                if (s.toString().startsWith("0")) {
                    s.clear()
                } else if (s.toString().isNotEmpty() && s.toString().toInt() > 9999) {
                    add_money_editext.setText(getString(R.string.amount_limit))
                    add_money_editext.text?.length?.let { add_money_editext.setSelection(it) };
                }
            }
        })
        setRecyclerView()
        setToolbarAndTitle(
            context = this@AddTaskActivity,
            toolbar = toolbar,
            isBackArrowVisible = true, toolbarTitle = getString(R.string.chore_title)
        )


        btnContinue.setOnClickListener {
            if (validate()) {


                val outputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
                if (et_start.text?.trim().toString() == endTime.text?.trim().toString()) {
                    myCalendar?.set(Calendar.HOUR_OF_DAY, 0);

                }
                myCalendar2?.set(Calendar.HOUR_OF_DAY, 23);
                myCalendar2?.set(Calendar.MINUTE, 59);
                myCalendar2?.set(Calendar.SECOND, 59);

                val startdate = outputFormat.format(myCalendar?.time)
                val enddate = outputFormat.format(myCalendar2?.time)
                if (selectedmember != null) {

                    Log.d("chackdate", startdate)
                    Log.d("chackenddate", enddate)
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
        mViewModel.bottomSheetStatus.observe(this, androidx.lifecycle.Observer { list ->

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


                if (year == currentDate?.get(Calendar.YEAR) && monthOfYear == currentDate?.get(
                        Calendar.MONTH
                    ) && dayOfMonth == currentDate?.get(Calendar.DAY_OF_MONTH)
                ) {
                    myCalendar = currentDate

                } else {
                    myCalendar?.set(Calendar.HOUR_OF_DAY, 0);
                    myCalendar?.set(Calendar.MINUTE, 0)
                    myCalendar?.set(Calendar.SECOND, 0)
                }

                myCalendar?.set(Calendar.YEAR, year)
                myCalendar?.set(Calendar.MONTH, monthOfYear)
                myCalendar?.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                updateLabel()
            }

        et_start.setOnClickListener {
            var dialog = DatePickerDialog(
                this@AddTaskActivity, date, myCalendar!!
                    .get(Calendar.YEAR), myCalendar!!.get(Calendar.MONTH),
                myCalendar!!.get(Calendar.DAY_OF_MONTH)
            )

            dialog.datePicker.setMinDate(myCalendar?.timeInMillis!!);
            dialog.show()
        }
        val date2 =
            OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                myCalendar2?.set(Calendar.YEAR, year)
                myCalendar2?.set(Calendar.MONTH, monthOfYear)
                myCalendar2?.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                updateLabel2()
            }
        mViewBinding.endTime.setOnClickListener {
            var dialog = DatePickerDialog(
                this@AddTaskActivity, date2, myCalendar2!!
                    .get(Calendar.YEAR), myCalendar2!!.get(Calendar.MONTH),
                myCalendar2!!.get(Calendar.DAY_OF_MONTH)
            )
            dialog.datePicker.setMinDate(myCalendar?.timeInMillis!!);
            dialog.show()
        }
    }

    private fun updateLabel() {
        val myFormat = "dd/MM/yyyy" //In which you need put here
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        et_start.setText(sdf.format(myCalendar?.time))
    }

    private fun getDateString(time: Date): String? {
        val myFormat = "dd/MM/yyyy" //In which you need put here
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        return sdf.format(time)
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
        if (sample_amount != null) {
            var amount1 = sample_amount?.toInt()?.div(100)
            add_money_editext.setText(amount1.toString())
        }


        et_title.setText(sampleTitle)
        et_desc.setText(sampleDescription)

    }

    private fun setRecyclerView() {

        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recycler_view.layoutManager = layoutManager
        var itemClickListener2 = object : ListItemClickListener {


            override fun onItemClicked(pos: Int) {

                selectedmember = itemsArrayList[pos]

            }

            override fun onCallClicked(pos: Int) {
                if (validate()) {
                    intentToActivity(ContactViewAddMember::class.java)
                }
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
         } else {

         }
         if (et_title.text.toString().trim().isEmpty()) {
             success = false
             et_title.error = "Enter Task title"
         }
         if (et_desc.text.toString().trim().isEmpty()) {
             success = false
             et_desc.error = "Enter Task Details"
         }

         return success
     }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val returnValue: ContactEntity? =
            data?.getParcelableExtra(AppConstants.CONTACT_SELECTED_RESPONSE)
        if (requestCode == 13 && resultCode != RESULT_CANCELED && returnValue != null) {
            var arrayList: ArrayList<MemberEntity> = ArrayList()
            var searched = false
            var searchposition = -1
            arrayList.addAll(itemsArrayList)
            if (arrayList.size > 0) {
                arrayList.forEachIndexed { pos, item ->
                    if (removePlusOrNineOneFromNo(returnValue.contactNumber) == removePlusOrNineOneFromNo(
                            item.mobileNo
                        )
                    ) {
                        searched = true
                        searchposition = pos

                    }
                }
            }
            if (!searched) {
                var member = MemberEntity()

                member.userId = returnValue.userId?.toDouble()
                member.mobileNo = returnValue.contactNumber
                member.name = returnValue.firstName
                member.profilePicResourceId = returnValue.profilePicResourceId

                itemsArrayList.add(member)
                typeAdapter?.notifyDataSetChanged()
                typeAdapter?.selectedPos = itemsArrayList.size - 1
                Log.d("chackid", returnValue.userId.toString())
                typeAdapter?.notifyItemChanged(itemsArrayList.size - 2)

            } else {
                typeAdapter?.selectedPos = searchposition

//                      typeAdapter?.notifyItemChanged(searchposition+1)
                typeAdapter?.notifyDataSetChanged()

            }


        }


    }

    override fun onPositiveButtonClick(uniqueIdentifier: String) {

    }


}