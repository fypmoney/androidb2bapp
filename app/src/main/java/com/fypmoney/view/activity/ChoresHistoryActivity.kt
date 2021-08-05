package com.fypmoney.view.activity

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModelProvider
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseActivity
import com.fypmoney.database.entity.TaskEntity
import com.fypmoney.databinding.ActivityChoresHistoryBinding
import com.fypmoney.databinding.ViewChoresBinding
import com.fypmoney.model.HistoryModelResponse
import com.fypmoney.util.AppConstants
import com.fypmoney.view.adapter.CompletedTasksAdapter
import com.fypmoney.view.fragment.*
import com.fypmoney.view.interfaces.ListItemClickListener
import com.fypmoney.viewmodel.ChoresHistoryViewModel
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.android.synthetic.main.view_add_task.*

class ChoresHistoryActivity : BaseActivity<ViewChoresBinding, ChoresHistoryViewModel>(),
    ProcessCompleteBSFragment.OnPBottomSheetClickListener,
    GoodJobBSFragment.OnGjBottomSheetClickListener,
    WellDoneBSFragment.OnWDBottomSheetClickListener,
    WhoopsBSFragment.OnWPBottomSheetClickListener,
    YayBSFragment.OnYyBottomSheetClickListener {
    private var itemsArrayList: ArrayList<HistoryModelResponse> = ArrayList()


    private var typeAdapter: CompletedTasksAdapter? = null
    private lateinit var mViewModel: ChoresHistoryViewModel
    var mbindign: ActivityChoresHistoryBinding? = null
    var taskDetailsData = ObservableField<TaskEntity>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mbindign = ActivityChoresHistoryBinding.inflate(layoutInflater)
        setContentView(mbindign!!.root)

        setToolbarAndTitle(
            context = this@ChoresHistoryActivity,
            toolbar = toolbar,
            isBackArrowVisible = true, toolbarTitle = getString(R.string.chore_history_title)
        )






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

        mViewModel!!.TaskHistory.observe(this, androidx.lifecycle.Observer { list ->
            itemsArrayList.clear()
            itemsArrayList.addAll(list)
            typeAdapter!!.notifyDataSetChanged()
        })
        setRecyclerView()

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

    private fun setRecyclerView() {


        var itemClickListener2 = object : ListItemClickListener {


            override fun onItemClicked(pos: Int) {


            }

            override fun onCallClicked(pos: Int) {

            }
        }


        typeAdapter =
            CompletedTasksAdapter(itemsArrayList, this, itemClickListener2!!)
        mbindign!!.recyclerView.adapter = typeAdapter


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


    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_chores_history
    }

    override fun getViewModel(): ChoresHistoryViewModel {
        mViewModel = ViewModelProvider(this).get(ChoresHistoryViewModel::class.java)
        return mViewModel
    }


}