package com.fypmoney.view.activity

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseActivity
import com.fypmoney.databinding.ViewStayTunedBinding
import com.fypmoney.util.AppConstants
import com.fypmoney.viewmodel.StayTunedViewModel
import kotlinx.android.synthetic.main.toolbar.*

/*
* This class is used to display successful request sent to the member
* */
class StayTunedView : BaseActivity<ViewStayTunedBinding, StayTunedViewModel>() {
    private lateinit var mViewModel: StayTunedViewModel

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.view_stay_tuned
    }

    override fun getViewModel(): StayTunedViewModel {
        mViewModel = ViewModelProvider(this).get(StayTunedViewModel::class.java)
        return mViewModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setToolbarAndTitle(
            context = this@StayTunedView,
            toolbar = toolbar,
            isBackArrowVisible = true
        )
        mViewModel.relation.set(intent.getStringExtra(AppConstants.RELATION) + resources.getString(R.string.stay_tuned_screen_sub_title1))
    }

}
