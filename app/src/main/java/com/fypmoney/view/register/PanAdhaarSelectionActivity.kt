package com.fypmoney.view.register


import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.fyp.trackr.models.TrackrEvent
import com.fyp.trackr.models.TrackrField
import com.fyp.trackr.models.trackr
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseActivity
import com.fypmoney.bindingAdapters.setBackgroundDrawable
import com.fypmoney.databinding.ActivityKycTypeBinding
import com.fypmoney.util.AppConstants
import com.fypmoney.util.Utility
import com.fypmoney.view.register.fragments.kycDetailsBottomSheet
import com.fypmoney.view.register.viewModel.KycTypeVM
import kotlinx.android.synthetic.main.toolbar_animation.*
import java.util.*


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
        setLottieAnimationToolBar(
            isBackArrowVisible = false,//back arrow visibility
            isLottieAnimation = true,// lottie animation visibility
            imageView = ivToolBarBack,//back image view
            lottieAnimationView = ivAnimationGift,
            screenName = PanAdhaarSelectionActivity::class.java.simpleName

        )
        setObserver()
        setBackgrounds()
    }

    private fun setBackgrounds() {
        setBackgroundDrawable(
            mViewBinding.cvAadhaar,
            ContextCompat.getColor(this, R.color.screenBackground),
            38f,
            ContextCompat.getColor(this, R.color.screenBackground),
            0f,
            false
        )
        setBackgroundDrawable(
            mViewBinding.cvPan,
            ContextCompat.getColor(this, R.color.screenBackground),
            38f,
            ContextCompat.getColor(this, R.color.screenBackground),
            0f,
            false
        )
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
                trackr {
                    it.name = TrackrEvent.onboard_doc_sel
                    it.add(
                        TrackrField.document_type, userTypeSelected.toLowerCase(Locale.ENGLISH))
                }
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
                ContextCompat.getColor(this, R.color.screenBackground),
                38f,
                ContextCompat.getColor(this, R.color.screenBackground),
                0f,
                false
            )

            setBackgroundDrawable(
                mViewBinding.cvPan,
                ContextCompat.getColor(this, R.color.green_selected_light),
                38f,
                ContextCompat.getColor(this, R.color.green_selected),
                4f,
                false
            )


        } else {
            setBackgroundDrawable(
                mViewBinding.cvAadhaar,
                ContextCompat.getColor(this, R.color.green_selected_light),
                38f,
                ContextCompat.getColor(this, R.color.green_selected),
                4f,
                false
            )
            setBackgroundDrawable(
                mViewBinding.cvPan,
                ContextCompat.getColor(this, R.color.screenBackground),
                38f,
                ContextCompat.getColor(this, R.color.screenBackground),
                0f,
                false
            )


        }

    }

}
