package com.fypmoney.view.register

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseActivity
import com.fypmoney.bindingAdapters.setBackgroundDrawable
import com.fypmoney.databinding.ActivityKycTypeBinding

import com.fypmoney.util.AppConstants
import com.fypmoney.util.Utility
import com.fypmoney.view.register.viewModel.KycTypeVM


import com.fypmoney.view.activity.EnterOtpView
import com.fypmoney.view.fragment.OfferDetailsBottomSheet
import com.fypmoney.view.fragment.kycDetailsBottomSheet
import com.fypmoney.view.storeoffers.model.offerDetailResponse


class PanAdhaarSelectionActivity :
    BaseActivity<ActivityKycTypeBinding, KycTypeVM>() {
    private lateinit var mVM: KycTypeVM
    private lateinit var mViewBinding: ActivityKycTypeBinding


    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_kyc_type
    }

    override fun getViewModel(): KycTypeVM {
        mVM = ViewModelProvider(this).get(KycTypeVM::class.java)
        return mVM
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewBinding = getViewDataBinding()
        mViewBinding.moreKycInfo.setOnClickListener {
            callKycDetailsSheeet()
        }

        setObserver()
    }

    private fun setObserver() {
        var userTypeSelected: String = "NOTSELECTED"
        mVM.isPanClick.observe(this) {
            setSelectedUserType(getString(R.string.pan))
            userTypeSelected = getString(R.string.pan)

        }
        mVM.isAadhaarClicked.observe(this) {
            setSelectedUserType(getString(R.string.aadhaar))
            userTypeSelected = getString(R.string.aadhaar)

        }
        mVM.isContinueClick.observe(this) {
            if (userTypeSelected == "NOTSELECTED") {
                Utility.showToast("Select any one")

            } else {
                val intent = Intent(this, KycDetailsActivity::class.java)
                intent.putExtra(AppConstants.KYC_type, userTypeSelected)
                startActivity(intent)

            }
        }
    }

    private fun callKycDetailsSheeet() {

        var bottomSheetMessage = kycDetailsBottomSheet()
        bottomSheetMessage.dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.RED))
        bottomSheetMessage.show(supportFragmentManager, "TASKMESSAGE")
    }

    private fun setSelectedUserType(type: String) {
        if (type == getString(R.string.pan)) {
            setBackgroundDrawable(
                mViewBinding.cvAadhaar,
                resources.getColor(R.color.white),
                38f,
                resources.getColor(R.color.white),
                0f,
                false
            )

            setBackgroundDrawable(
                mViewBinding.cvPan,
                resources.getColor(R.color.green_selected_light),
                38f,
                resources.getColor(R.color.green_selected),
                4f,
                false
            )


        } else {
            setBackgroundDrawable(
                mViewBinding.cvAadhaar,
                resources.getColor(R.color.green_selected_light),
                38f,
                resources.getColor(R.color.green_selected),
                4f,
                false
            )
            setBackgroundDrawable(
                mViewBinding.cvPan,
                resources.getColor(R.color.white),
                38f,
                resources.getColor(R.color.white),
                0f,
                false
            )


        }

    }

}
