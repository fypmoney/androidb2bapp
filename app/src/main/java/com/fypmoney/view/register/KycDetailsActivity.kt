package com.fypmoney.view.register

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import com.fypmoney.BR

import com.fypmoney.base.BaseActivity

import com.fypmoney.util.Utility

import com.fypmoney.view.register.viewModel.KycdetailViewModel
import com.fypmoney.view.storeoffers.model.offerDetailResponse

import android.widget.ArrayAdapter

import android.widget.AdapterView
import com.fyp.trackr.models.TrackrEvent
import com.fyp.trackr.models.trackr
import com.fyp.trackr.services.TrackrServices
import com.fypmoney.R
import com.fypmoney.databinding.ActivityKycDetailsBinding
import com.fypmoney.util.AppConstants
import com.fypmoney.view.activity.*
import java.util.*
import kotlin.collections.ArrayList


class KycDetailsActivity : BaseActivity<ActivityKycDetailsBinding, KycdetailViewModel>(),
    Utility.OnDateSelectedWithDateFormat, AdapterView.OnItemSelectedListener {
    private var kyc_type: String? = null
    private lateinit var binding: ActivityKycDetailsBinding
    var genders = arrayOf(
        "Male", "Female"
    )

    private val kycDetailsVM by viewModels<KycdetailViewModel> { defaultViewModelProviderFactory }
    private var itemsArrayList: ArrayList<offerDetailResponse> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = getViewDataBinding()

        setObserver()

        binding.spinnerGender.onItemSelectedListener = this

        kyc_type = intent.getStringExtra(AppConstants.KYC_type)

        if (kyc_type == getString(R.string.pan)) {
            binding.panNumberTitle.text = "PAN*"


        } else {
            binding.panNumberTitle.text = "Aadhaar no*"
        }
        kycDetailsVM.kycType.value = kyc_type


        val ad: ArrayAdapter<*> = ArrayAdapter<Any?>(
            this,
            android.R.layout.simple_spinner_item,
            genders
        )
        ad.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )
        binding.spinnerGender.adapter = ad
    }

    private fun setObserver() {
        kycDetailsVM.onActivateAccountSuccess.observe(this) {
            if (it.showMobileOtpVerificationScreen == AppConstants.YES)
                intentToActivity(it.token)
            else {
                if (kycDetailsVM.postKycScreenCode.value != null && kycDetailsVM.postKycScreenCode.value == "1") {
                    trackr {
                        it.services = arrayListOf(
                            TrackrServices.FIREBASE,
                            TrackrServices.MOENGAGE,
                            TrackrServices.FB, TrackrServices.ADJUST
                        )
                        it.name = TrackrEvent.kyc_verification_other
                    }
                    if (hasPermissions(this, Manifest.permission.READ_CONTACTS)) {
                        intentToActivity(HomeView::class.java)
                    } else {
                        intentToActivity(PermissionsActivity::class.java)
                    }
                } else if (kycDetailsVM.postKycScreenCode.value != null && kycDetailsVM.postKycScreenCode.value == "0") {
                    when (Utility.getCustomerDataFromPreference()?.isReferralAllowed) {
                        AppConstants.YES -> {
                            intentToActivity(ReferralCodeView::class.java)
                        }
                        else -> {
                            if (hasPermissions(this, Manifest.permission.READ_CONTACTS)) {
                                intentToActivity(HomeView::class.java)
                            } else {
                                intentToActivity(PermissionsActivity::class.java)
                            }

                        }
                    }
                    trackr {
                        it.services = arrayListOf(
                            TrackrServices.FIREBASE,
                            TrackrServices.MOENGAGE,
                            TrackrServices.FB, TrackrServices.ADJUST
                        )
                        it.name = TrackrEvent.kyc_verification_teen
                    }
                    intentToActivity(ReferralCodeView::class.java, true)
                } else if (kycDetailsVM.postKycScreenCode.value != null && kycDetailsVM.postKycScreenCode.value == "90") {
                    trackr {
                        it.services = arrayListOf(
                            TrackrServices.FIREBASE,
                            TrackrServices.MOENGAGE,
                            TrackrServices.FB, TrackrServices.ADJUST
                        )
                        it.name = TrackrEvent.kyc_verification_adult
                    }
                    intentToActivity(AgeAllowedActivationView::class.java, true)
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