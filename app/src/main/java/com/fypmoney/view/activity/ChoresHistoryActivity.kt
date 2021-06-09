package com.fypmoney.view.activity

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseActivity
import com.fypmoney.connectivity.retrofit.Allapi
import com.fypmoney.connectivity.retrofit.ApiClient1
import com.fypmoney.database.entity.TaskEntity
import com.fypmoney.databinding.ActivityChoresHistoryBinding
import com.fypmoney.databinding.ViewChoresBinding
import com.fypmoney.model.chaoseHistory.ChoresHistoryResponse
import com.fypmoney.util.AppConstants
import com.fypmoney.util.SharedPrefUtils
import com.fypmoney.view.adapter.TaskHistoryStaggeredAdapter
import com.fypmoney.view.fragment.*
import com.fypmoney.viewhelper.GridItemDecoration
import com.fypmoney.viewmodel.ChoresViewModel
import kotlinx.android.synthetic.main.toolbar.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChoresHistoryActivity : BaseActivity<ViewChoresBinding, ChoresViewModel>() ,
    ProcessCompleteBSFragment.OnPBottomSheetClickListener,
    GoodJobBSFragment.OnGjBottomSheetClickListener,
    WellDoneBSFragment.OnWDBottomSheetClickListener,
    WhoopsBSFragment.OnWPBottomSheetClickListener,
    YayBSFragment.OnYyBottomSheetClickListener{
    private lateinit var mViewModel: ChoresViewModel
    var mbindign: ActivityChoresHistoryBinding?=null
    var taskDetailsData = ObservableField<TaskEntity>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mbindign= ActivityChoresHistoryBinding.inflate(layoutInflater)
        setContentView(mbindign!!.root)

        setToolbarAndTitle(
            context = this@ChoresHistoryActivity,
            toolbar = toolbar,
            isBackArrowVisible = true, toolbarTitle = getString(R.string.chore_history_title)
        )




        mbindign!!.btnContinue.setOnClickListener {
            callYaySheet(taskDetailsData.get())
        }

        mbindign!!.cardCreate.setOnClickListener {
            intentToAddMemberActivity(AddTaskActivity::class.java)
        }

        mbindign!!.cardPlantWater.setOnClickListener{
            callProcessSheet(taskDetailsData.get())
        }

        mbindign!!.cardPlantWater1.setOnClickListener{
            callGoodJSheet(taskDetailsData.get())
        }

        mbindign!!.cardCleanBed.setOnClickListener {
            callWellDSheet(taskDetailsData.get())
        }

        mbindign!!.cardMakeBed.setOnClickListener {
            callWhoopSheet(taskDetailsData.get())
        }

        mbindign!!.recyclerView.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        mbindign!!.recyclerView.addItemDecoration(GridItemDecoration(10, 2))

        getHistory()

    }

    private fun intentToAddMemberActivity(aClass: Class<*>) {
        val intent = Intent(this, aClass)
        intent.putExtra(AppConstants.FROM_WHICH_SCREEN, "")
        startActivity(intent)
    }


    override fun OnPBottomSheetClickListener() {

    }

    override fun OnGjBottomSheetClickListener() {

    }

    override fun OnWDBottomSheetClickListener() {

    }

    override fun OnWPBottomSheetClickListener() {

    }

    override fun OnYyBottomSheetClickListener() {

    }


    private fun callProcessSheet(taskEntity: TaskEntity?) {
        val bottomSheet =
            ProcessCompleteBSFragment(
                taskEntity, this
            )
        bottomSheet.dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.RED))
        bottomSheet.show(supportFragmentManager, "AcceptRejectBottomSheet")
    }

    private fun callGoodJSheet(taskEntity: TaskEntity?) {
        val bottomSheet =
            GoodJobBSFragment(
                taskEntity, this
            )
        bottomSheet.dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.RED))
        bottomSheet.show(supportFragmentManager, "AcceptRejectBottomSheet")
    }

    private fun callWellDSheet(taskEntity: TaskEntity?) {
        val bottomSheet =
            WellDoneBSFragment(
                taskEntity, this
            )
        bottomSheet.dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.RED))
        bottomSheet.show(supportFragmentManager, "AcceptRejectBottomSheet")
    }

    private fun callWhoopSheet(taskEntity: TaskEntity?) {
        val bottomSheet =
            WhoopsBSFragment(
                taskEntity, this
            )
        bottomSheet.dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.RED))
        bottomSheet.show(supportFragmentManager, "AcceptRejectBottomSheet")
    }

    private fun callYaySheet(taskEntity: TaskEntity?) {
        val bottomSheet =
            YayBSFragment(
                taskEntity, this
            )
        bottomSheet.dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.RED))
        bottomSheet.show(supportFragmentManager, "AcceptRejectBottomSheet")
    }

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_chores_history
    }

    override fun getViewModel(): ChoresViewModel {
        mViewModel = ViewModelProvider(this).get(ChoresViewModel::class.java)
        return mViewModel
    }

    private fun getHistory() {
        try {
            val apiInterface = ApiClient1.getClient().create(Allapi::class.java)
            val responseBodyCall = apiInterface.getChoresHistory("web_app", SharedPrefUtils.SF_KEY_ACCESS_TOKEN)
            responseBodyCall.enqueue(object : Callback<ChoresHistoryResponse> {
                override fun onResponse(call: Call<ChoresHistoryResponse>, response: Response<ChoresHistoryResponse>) {
                    val status = response.body()?.data
                    val adapter = status?.let {
                        TaskHistoryStaggeredAdapter(
                            this@ChoresHistoryActivity,
                            it
                        )
                    }
                    mbindign!!.recyclerView.adapter = adapter
                }
                override fun onFailure(call: Call<ChoresHistoryResponse>, t: Throwable) {

                }
            })
        } catch (e: Exception) {
        }
    }


}