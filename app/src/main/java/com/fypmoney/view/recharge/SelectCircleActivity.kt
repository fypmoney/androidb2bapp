package com.fypmoney.view.recharge


import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseActivity
import com.fypmoney.databinding.ActivitySelectCircleBinding
import com.fypmoney.view.home.main.home.adapter.CallToActionAdapter
import com.fypmoney.view.recharge.adapter.CircleSelectionAdapter
import com.fypmoney.view.recharge.adapter.OperatorSelectionAdapter
import com.fypmoney.view.recharge.model.CircleResponse
import com.fypmoney.view.recharge.viewmodel.SelectCircleViewModel
import kotlinx.android.synthetic.main.toolbar.*
import kotlin.collections.ArrayList


/*
* This class is used as Home Screen
* */
class SelectCircleActivity : BaseActivity<ActivitySelectCircleBinding, SelectCircleViewModel>() {
    private lateinit var mViewModel: SelectCircleViewModel
    private lateinit var mViewBinding: ActivitySelectCircleBinding

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_select_circle
    }

    override fun getViewModel(): SelectCircleViewModel {
        mViewModel = ViewModelProvider(this).get(SelectCircleViewModel::class.java)
        return mViewModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewBinding = getViewDataBinding()
        setToolbarAndTitle(
            context = this@SelectCircleActivity,
            toolbar = toolbar, backArrowTint = Color.WHITE,
            titleColor = Color.WHITE,
            isBackArrowVisible = true,
            toolbarTitle = "Mobile Recharge"
        )


        setObserver()


    }

    private fun setUpRecyclerView(arrayList: ArrayList<CircleResponse>) {
        val topTenUsersAdapter = CircleSelectionAdapter(
            this, onRecentUserClick = {

                var intent = Intent(this, PlanSelectionActivity::class.java)

                startActivity(intent)
            }
        )


        with(mViewBinding.rvCircles) {
            adapter = topTenUsersAdapter
            layoutManager =
                LinearLayoutManager(this@SelectCircleActivity, RecyclerView.HORIZONTAL, false)
        }
        (mViewBinding.rvCircles.adapter as CircleSelectionAdapter).submitList(arrayList)
    }

    private fun setObserver() {
        mViewModel.opertaorList.observe(this) {
            setUpRecyclerView(it)
        }
    }


}
