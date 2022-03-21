package com.fypmoney.view.recharge


import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.lifecycle.ViewModelProvider
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseActivity
import com.fypmoney.databinding.ActivitySelectCircleBinding
import com.fypmoney.util.AppConstants
import com.fypmoney.view.recharge.model.OperatorResponse
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

        mViewBinding.continueBtn.setOnClickListener {

            var intent = Intent(this, SelectOperatorActivity::class.java)

            startActivity(intent)


        }
        setObserver()


    }

    private fun setSpinner(arrayList: ArrayList<OperatorResponse>) {
        val plants: MutableList<String> = ArrayList()



        arrayList.forEachIndexed { pos, item ->

            item.name?.let { plants.add(it) }

        }
        val spinnerArrayAdapter = ArrayAdapter(
            this, R.layout.spinner_item, plants
        )
        spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_item)
        mViewBinding.optionsMenu.adapter = spinnerArrayAdapter
    }

    private fun setObserver() {
        mViewModel.opertaorList.observe(this) {
            setSpinner(it)
        }
    }


}
