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
import com.fypmoney.databinding.ActivitySelectOperatorBinding
import com.fypmoney.util.AppConstants
import com.fypmoney.view.adapter.TopTenUsersAdapter
import com.fypmoney.view.home.main.home.adapter.CallToActionAdapter
import com.fypmoney.view.recharge.adapter.OperatorSelectionAdapter
import com.fypmoney.view.recharge.model.OperatorResponse
import com.fypmoney.view.recharge.viewmodel.SelectOperatorViewModel
import kotlinx.android.synthetic.main.toolbar.*
import kotlin.collections.ArrayList


/*
* This class is used as Home Screen
* */
class SelectOperatorActivity :
    BaseActivity<ActivitySelectOperatorBinding, SelectOperatorViewModel>() {
    private lateinit var mViewModel: SelectOperatorViewModel
    private lateinit var mViewBinding: ActivitySelectOperatorBinding

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_select_operator
    }

    override fun getViewModel(): SelectOperatorViewModel {
        mViewModel = ViewModelProvider(this).get(SelectOperatorViewModel::class.java)
        return mViewModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewBinding = getViewDataBinding()
        setToolbarAndTitle(
            context = this@SelectOperatorActivity,
            toolbar = toolbar, backArrowTint = Color.WHITE,
            titleColor = Color.WHITE,
            isBackArrowVisible = true,
            toolbarTitle = "Mobile Recharge"
        )

        setObserver()


    }

    private fun setUpRecyclerView(arrayList: ArrayList<OperatorResponse>) {
        val topTenUsersAdapter = OperatorSelectionAdapter(
            this, onRecentUserClick = {

                var intent = Intent(this, PlanSelectionActivity::class.java)

                startActivity(intent)
            }
        )


        with(mViewBinding.rvOperator) {
            adapter = topTenUsersAdapter
            layoutManager =
                LinearLayoutManager(this@SelectOperatorActivity, RecyclerView.HORIZONTAL, false)
        }
    }

    private fun setObserver() {
        mViewModel.opertaorList.observe(this) {
//            (mViewBinding.rvOperator.adapter as OperatorSelectionAdapter).submitList(it)

        }
    }


}
