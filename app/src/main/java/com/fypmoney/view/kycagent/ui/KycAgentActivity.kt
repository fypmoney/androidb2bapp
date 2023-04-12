package com.fypmoney.view.kycagent.ui

import android.Manifest
import android.os.Bundle
import androidx.activity.viewModels
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseActivity
import com.fypmoney.databinding.ActivityKycAgentBinding
import com.fypmoney.listener.LocationListenerClass
import com.fypmoney.util.SharedPrefUtils
import com.fypmoney.view.kycagent.viewmodel.KycAgentActivityVM

class KycAgentActivity : BaseActivity<ActivityKycAgentBinding, KycAgentActivityVM>(),
    LocationListenerClass.GetCurrentLocationListener {

    private lateinit var binding: ActivityKycAgentBinding
    private val kycAgentActivityVM by viewModels<KycAgentActivityVM> { defaultViewModelProviderFactory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = getViewDataBinding()

        LocationListenerClass(
            this, this
        ).permissions()
        checkAndAskPermission()

    }

    override fun getBindingVariable(): Int = BR.viewModel

    override fun getLayoutId(): Int = R.layout.activity_kyc_agent

    override fun getViewModel(): KycAgentActivityVM = kycAgentActivityVM

    private fun checkAndAskPermission() {
        when (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION)) {
            false -> {
                kycAgentActivityVM.postLatlong(
                    "", "", SharedPrefUtils.getLong(
                        application, key = SharedPrefUtils.SF_KEY_USER_ID
                    )
                )
            }
            else -> {
                requestPermission(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }

    }

    override fun getCurrentLocation(
        isInternetConnected: Boolean?,
        latitude: Double,
        Longitude: Double
    ) {
        SharedPrefUtils.getLong(
            application, key = SharedPrefUtils.SF_KEY_USER_ID
        ).let {
            kycAgentActivityVM.postLatlong("$latitude", "$Longitude", it)
        }
    }

}