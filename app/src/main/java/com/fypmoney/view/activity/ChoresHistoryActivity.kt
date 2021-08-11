package com.fypmoney.view.activity

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseActivity
import com.fypmoney.base.PaginationListener
import com.fypmoney.database.entity.TaskEntity
import com.fypmoney.databinding.ActivityChoresHistoryBinding
import com.fypmoney.databinding.ViewChoresBinding
import com.fypmoney.model.HistoryModelResponse
import com.fypmoney.util.AppConstants
import com.fypmoney.view.adapter.CompletedTasksAdapter
import com.fypmoney.view.fragment.*
import com.fypmoney.view.interfaces.ListItemClickListener
import com.fypmoney.viewmodel.ChoresHistoryViewModel
import kotlinx.android.synthetic.main.activity_chores_history.*
import kotlinx.android.synthetic.main.fragment_assigned_task.view.*
import kotlinx.android.synthetic.main.toolbar.*


class ChoresHistoryActivity : BaseActivity<ActivityChoresHistoryBinding, ChoresHistoryViewModel>(),
    ProcessCompleteBSFragment.OnPBottomSheetClickListener,
    GoodJobBSFragment.OnGjBottomSheetClickListener,
    WellDoneBSFragment.OnWDBottomSheetClickListener,
    WhoopsBSFragment.OnWPBottomSheetClickListener,
    YayBSFragment.OnYyBottomSheetClickListener {
    private var itemsArrayList: ArrayList<HistoryModelResponse> = ArrayList()


    private var typeAdapter: CompletedTasksAdapter? = null
    private lateinit var mViewModel: ChoresHistoryViewModel
    private var isLoading = false
    var page = 0

    var mbindign: ActivityChoresHistoryBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mbindign = ActivityChoresHistoryBinding.inflate(layoutInflater)
        setContentView(mbindign!!.root)

        setToolbarAndTitle(
            context = this@ChoresHistoryActivity,
            toolbar = toolbar,
            isBackArrowVisible = true, toolbarTitle = getString(R.string.chore_history_title)
        )








        mViewModel!!.TaskHistory.observe(this, androidx.lifecycle.Observer { list ->
            if (list != null) {
                page = page + 1
                isLoading = false
                mbindign?.LoadProgressBar?.visibility = View.GONE
                itemsArrayList.addAll(list)
                typeAdapter!!.notifyDataSetChanged()
            }

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
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        mbindign!!.recyclerView!!.layoutManager = layoutManager
        mbindign!!.recyclerView.addOnScrollListener(object : PaginationListener(layoutManager) {
            override fun loadMoreItems() {
                Log.d("chackpaginat", "dc")
                loadMore()
            }

            override fun loadMoreTopItems() {

            }

            override fun isLoading(): Boolean {
                return isLoading
            }
        })

        typeAdapter =
            CompletedTasksAdapter(itemsArrayList, this, itemClickListener2!!)
        mbindign!!.recyclerView.adapter = typeAdapter


    }

    private fun loadMore() {
        mbindign?.LoadProgressBar?.visibility = View.VISIBLE
        mViewModel.callSampleTask(page)
        isLoading = true

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