package com.fypmoney.view.referandearn.view

import android.content.ClipboardManager
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.fyp.trackr.models.TrackrEvent
import com.fyp.trackr.models.trackr
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseActivity
import com.fypmoney.databinding.ActivityReferAndEarnBinding
import com.fypmoney.util.Utility
import com.fypmoney.view.referandearn.viewmodel.ReferAndEarnActivityVM
import kotlinx.android.synthetic.main.toolbar.*

class ReferAndEarnActivity : BaseActivity<ActivityReferAndEarnBinding,ReferAndEarnActivityVM>() {
    private var referMessage: String? = null
    private lateinit var mViewModel: ReferAndEarnActivityVM
    private lateinit var mViewBinding: ActivityReferAndEarnBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewBinding = getViewDataBinding()
        mViewBinding.viewmodel = mViewModel
        setToolbarAndTitle(
            context = this@ReferAndEarnActivity,
            toolbar = toolbar,
            isBackArrowVisible = true, toolbarTitle = getString(R.string.refer_and_earn)
        )
        trackr {
            it.name = TrackrEvent.ref_tab
        }
        setUpViews()
        setUpObserver()
    }

    private fun setUpViews() {
        mViewBinding.referalCodeValueTv.text = Utility.getCustomerDataFromPreference()?.referralCode

    }

    private fun setUpObserver() {

        mViewModel.referResponse.observe(this, {

            mViewBinding.referAndEarnTitleTv.text = it.referLine1

            mViewBinding.referAndEarnSubTitleTv.text = it.referLine2

            referMessage = it.referMessage

            mViewBinding.thirdMessage.text = it.rfu1


        })
        mViewModel.event.observe(this, {
            handelEvents(it)
        })

    }

    private fun handelEvents(it: ReferAndEarnActivityVM.ReferAndEarnEvent?) {
        when(it){
            ReferAndEarnActivityVM.ReferAndEarnEvent.CopyReferCode -> {
                Utility.copyTextToClipBoard(
                    getSystemService(CLIPBOARD_SERVICE) as ClipboardManager,
                    Utility.getCustomerDataFromPreference()?.referralCode
                )
            }
            ReferAndEarnActivityVM.ReferAndEarnEvent.ShareReferCode -> {

                if (referMessage != null) {
                    shareInviteCodeFromReferal(referMessage!!)
                } else {
                    Utility.showToast("Loading")
                }

            }
        }
    }

    override fun getBindingVariable(): Int = BR.viewModel

    override fun getLayoutId(): Int  = R.layout.activity_refer_and_earn

    override fun getViewModel(): ReferAndEarnActivityVM {
        mViewModel = ViewModelProvider(this).get(ReferAndEarnActivityVM::class.java)
        return mViewModel
    }

}