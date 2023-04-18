package com.fypmoney.view.register

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.InputFilter
import android.text.InputFilter.AllCaps
import android.text.InputType
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import com.fyp.trackr.models.TrackrEvent
import com.fyp.trackr.models.trackr
import com.fyp.trackr.services.TrackrServices
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseActivity
import com.fypmoney.databinding.ActivityKycDetailsBinding
import com.fypmoney.util.AppConstants
import com.fypmoney.util.Utility
import com.fypmoney.view.activity.ChooseInterestRegisterView
import com.fypmoney.view.activity.EnterOtpView
import com.fypmoney.view.activity.ReferralCodeView
import com.fypmoney.view.fragment.kycTanCBottomSheet
import com.fypmoney.view.register.fragments.AadharCardInfoDialogFragment
import com.fypmoney.view.register.viewModel.KycdetailViewModel
import kotlinx.android.synthetic.main.toolbar_animation.*


class KycDetailsActivity : BaseActivity<ActivityKycDetailsBinding, KycdetailViewModel>(),
    Utility.OnDateSelectedWithDateFormat, AdapterView.OnItemSelectedListener {
    private var kyc_type: String? = null
    private lateinit var binding: ActivityKycDetailsBinding
    var genders = arrayOf(
        "Male", "Female"
    )

    private val kycDetailsVM by viewModels<KycdetailViewModel> { defaultViewModelProviderFactory }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = getViewDataBinding()

        setObserver()

        binding.spinnerGender.onItemSelectedListener = this

        kyc_type = intent.getStringExtra(AppConstants.KYC_type)
        kycDetailsVM.kycType.value = kyc_type
        if (kyc_type == getString(R.string.pan)) {
            binding.panNumberTitle.text = "PAN*"
            binding.verificationTitle.text = "Full  Name as mentioned in PAN"

            binding.panNumber.filters = arrayOf<InputFilter>(
                AllCaps(),
                InputFilter.LengthFilter(10)
            )


        } else {
            binding.verificationTitle.text = "Full  Name as mentioned in Aadhaar"
            binding.panNumber.filters = arrayOf<InputFilter>(
                InputFilter.LengthFilter(12)
            )
            binding.panNumber.setInputType(InputType.TYPE_CLASS_TEXT or InputType.TYPE_CLASS_NUMBER)
            binding.panNumberTitle.text = "Aadhaar no*"
        }
        binding.hereby.setOnClickListener {
            callKycDetailsSheeet()
        }


        val ad: ArrayAdapter<*> = ArrayAdapter<Any?>(
            this,
            android.R.layout.simple_spinner_item,
            genders
        )
        ad.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )
        binding.spinnerGender.adapter = ad
        setLottieAnimationToolBar(
            isBackArrowVisible = true,//back arrow visibility
            isLottieAnimation = true,// lottie animation visibility
            imageView = ivToolBarBack,//back image view
            lottieAnimationView = ivAnimationGift,
            screenName = KycDetailsActivity::class.java.simpleName

        )// lottie anima


    }

    private fun callKycDetailsSheeet() {

        var bottomSheetMessage = kycTanCBottomSheet()
        bottomSheetMessage.dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.RED))
        bottomSheetMessage.show(supportFragmentManager, "TASKMESSAGE")
    }

    private fun setObserver() {
        kycDetailsVM.customerProfileSuccess.observe(this) { kycActivateAccountResponseDetails->
            if (kycDetailsVM.onActivateAccountSuccess.value?.showMobileOtpVerificationScreen == AppConstants.YES)
                intentToActivity(kycDetailsVM.onActivateAccountSuccess.value!!.token)
            else {
                if (kycDetailsVM.postKycScreenCode.value != null && kycDetailsVM.postKycScreenCode.value == "1") {
                    trackr {
                        it.services = arrayListOf(
                            TrackrServices.FIREBASE,
                            TrackrServices.MOENGAGE,
                            TrackrServices.FB
                        )
                        it.name = TrackrEvent.kyc_verification_other
                    }
                    val intent = Intent(this, InviteParentSiblingActivity::class.java)
                    intent.putExtra(AppConstants.USER_TYPE, "1")
                    startActivity(intent)
                    finish()

                }
                else if (kycDetailsVM.postKycScreenCode.value != null && kycDetailsVM.postKycScreenCode.value == "0") {
                    trackr {
                        it.services = arrayListOf(
                            TrackrServices.FIREBASE,
                            TrackrServices.MOENGAGE,
                            TrackrServices.FB
                        )
                        it.name = TrackrEvent.kyc_verification_teen
                    }
                    when (Utility.getCustomerDataFromPreference()?.isReferralAllowed) {
                        AppConstants.YES -> {
                            intentToActivity(ReferralCodeView::class.java)
                        }
                        else -> {
                            startActivity(Intent(this, ChooseInterestRegisterView::class.java))

                        }
                    }


                } else if (kycDetailsVM.postKycScreenCode.value != null && kycDetailsVM.postKycScreenCode.value == "90") {
                    trackr {
                        it.services = arrayListOf(
                            TrackrServices.FIREBASE,
                            TrackrServices.MOENGAGE,
                            TrackrServices.FB
                        )
                        it.name = TrackrEvent.kyc_verification_adult
                    }
                    val intent = Intent(this, InviteParentSiblingActivity::class.java)
                    intent.putExtra(AppConstants.USER_TYPE, "90")
                    startActivity(intent)
                    finish()
                }

            }
        }
        kycDetailsVM.onDobClicked.observe(this) {
            if (it) {
                try {
                    Utility.showDatePickerInDateFormatDialog(
                        context = this,
                        onDateSelected = this,
                        isDateOfBirth = false
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                kycDetailsVM.onDobClicked.value = false
            }
        }
        kycDetailsVM.onAadharCardInfoClicked.observe(this) {
            if (it) {
                val showAdharCardInfo = AadharCardInfoDialogFragment()
                showAdharCardInfo.show(supportFragmentManager, "show aadharinfo")
                kycDetailsVM.onAadharCardInfoClicked.value = false
            }
        }
    }

    private fun intentToActivity(token: String) {
        val intent = Intent(this, EnterOtpView::class.java)
        intent.putExtra(
            AppConstants.MOBILE_TYPE,
            ""
        )
        intent.putExtra(
            AppConstants.FROM_WHICH_SCREEN, AppConstants.KYC_MOBILE_VERIFICATION
        )

        intent.putExtra(
            AppConstants.MOBILE_WITHOUT_COUNTRY_CODE,
            ""
        )

        intent.putExtra(
            AppConstants.KYC_ACTIVATION_TOKEN,
            token
        )
        intent.putExtra(
            AppConstants.KIT_FOUR_DIGIT, ""

        )
        startActivity(intent)
    }

    private fun intentToActivity(aClass: Class<*>, isFinishAll: Boolean? = false) {
        val intent = Intent(this, aClass)
        startActivity(intent)
        if (isFinishAll == true) {
            finishAffinity()
        }
    }

    override fun onDateSelectedWithDateFormat(dateonserver: String?, format: String) {


        kycDetailsVM.dob.value = format
        kycDetailsVM.dobDate.value = dateonserver
    }

    /**
     * Override for set binding variable
     *
     * @return variable id
     */
    override fun getBindingVariable(): Int = BR.viewModel

    /**
     * @return layout resource id
     */
    override fun getLayoutId(): Int = R.layout.activity_kyc_details

    /**
     * Override for set view model
     *
     * @return view model instance
     */
    override fun getViewModel(): KycdetailViewModel = kycDetailsVM
    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        kycDetailsVM.gender.value = genders[position]
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {

    }



}