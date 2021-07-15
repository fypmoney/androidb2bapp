package com.fypmoney.view.activity

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseActivity
import com.fypmoney.databinding.ViewTrackOrderBinding
import com.fypmoney.viewmodel.TrackOrderViewModel
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.android.synthetic.main.view_order_card.*

/*
* This class is used to card tracking
* */
class TrackOrderView : BaseActivity<ViewTrackOrderBinding, TrackOrderViewModel>() {
    private lateinit var mViewModel: TrackOrderViewModel

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.view_track_order
    }

    override fun getViewModel(): TrackOrderViewModel {
        mViewModel = ViewModelProvider(this).get(TrackOrderViewModel::class.java)
        return mViewModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setToolbarAndTitle(
            context = this@TrackOrderView,
            toolbar = toolbar,
            isBackArrowVisible = true
        )
        setObservers()

    }

    fun setObservers() {

    }

    /**
     * Method to navigate to the different activity
     */
    private fun intentToActivity(aClass: Class<*>) {
        startActivity(Intent(this@TrackOrderView, aClass))
    }


}
