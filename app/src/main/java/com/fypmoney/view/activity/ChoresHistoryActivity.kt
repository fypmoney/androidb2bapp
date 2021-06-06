package com.fypmoney.view.activity

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseActivity
import com.fypmoney.database.entity.TaskEntity
import com.fypmoney.databinding.ViewChoresBinding
import com.fypmoney.model.YourTaskModel
import com.fypmoney.util.AppConstants
import com.fypmoney.view.adapter.TaskHistoryStaggeredAdapter
import com.fypmoney.view.adapter.YourTaskStaggeredAdapter
import com.fypmoney.view.fragment.*
import com.fypmoney.viewhelper.GridItemDecoration
import com.fypmoney.viewmodel.ChoresViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import kotlinx.android.synthetic.main.toolbar.*

class ChoresHistoryActivity : BaseActivity<ViewChoresBinding, ChoresViewModel>() ,
    ProcessCompleteBSFragment.OnPBottomSheetClickListener,
    GoodJobBSFragment.OnGjBottomSheetClickListener,
    WellDoneBSFragment.OnWDBottomSheetClickListener,
    WhoopsBSFragment.OnWPBottomSheetClickListener,
    YayBSFragment.OnYyBottomSheetClickListener{

    private lateinit var mViewModel: ChoresViewModel
    lateinit var card_create: CardView
    lateinit var card_plant_water: CardView
    lateinit var card_plant_water1: CardView
    lateinit var card_clean_bed: CardView
    lateinit var card_make_bed: CardView
    lateinit var btnContinue: MaterialButton
    var taskDetailsData = ObservableField<TaskEntity>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chores_history)
        setToolbarAndTitle(
            context = this@ChoresHistoryActivity,
            toolbar = toolbar,
            isBackArrowVisible = true, toolbarTitle = getString(R.string.chore_history_title)
        )
        card_create = findViewById(R.id.card_create)
        card_plant_water = findViewById(R.id.card_plant_water)
        card_plant_water1 = findViewById(R.id.card_plant_water1)
        card_clean_bed = findViewById(R.id.card_clean_bed)
        card_make_bed = findViewById(R.id.card_make_bed)
        btnContinue = findViewById(R.id.btnContinue)

        btnContinue.setOnClickListener {
            callYaySheet(taskDetailsData.get())
        }

        card_create.setOnClickListener {
            intentToAddMemberActivity(AddTaskActivity::class.java)
        }

        card_plant_water.setOnClickListener{
            callProcessSheet(taskDetailsData.get())
        }

        card_plant_water1.setOnClickListener{
            callGoodJSheet(taskDetailsData.get())
        }

        card_clean_bed.setOnClickListener {
            callWellDSheet(taskDetailsData.get())
        }

        card_make_bed.setOnClickListener {
            callWhoopSheet(taskDetailsData.get())
        }

//        findViewById<FloatingActionButton>(R.id.fab).setOnClickListener { view ->
//            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                .setAction("Action", null).show()
//        }
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
        bottomSheet.show(getSupportFragmentManager(), "AcceptRejectBottomSheet")
    }

    private fun callGoodJSheet(taskEntity: TaskEntity?) {
        val bottomSheet =
            GoodJobBSFragment(
                taskEntity, this
            )
        bottomSheet.dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.RED))
        bottomSheet.show(getSupportFragmentManager(), "AcceptRejectBottomSheet")
    }

    private fun callWellDSheet(taskEntity: TaskEntity?) {
        val bottomSheet =
            WellDoneBSFragment(
                taskEntity, this
            )
        bottomSheet.dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.RED))
        bottomSheet.show(getSupportFragmentManager(), "AcceptRejectBottomSheet")
    }

    private fun callWhoopSheet(taskEntity: TaskEntity?) {
        val bottomSheet =
            WhoopsBSFragment(
                taskEntity, this
            )
        bottomSheet.dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.RED))
        bottomSheet.show(getSupportFragmentManager(), "AcceptRejectBottomSheet")
    }

    private fun callYaySheet(taskEntity: TaskEntity?) {
        val bottomSheet =
            YayBSFragment(
                taskEntity, this
            )
        bottomSheet.dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.RED))
        bottomSheet.show(getSupportFragmentManager(), "AcceptRejectBottomSheet")
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

}