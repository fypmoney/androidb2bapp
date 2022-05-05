package com.fypmoney.view.upgradetokyc

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.fyp.trackr.models.TrackrEvent
import com.fyp.trackr.models.trackr
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseActivity
import com.fypmoney.databinding.ActivityUpgradeToKycInfoBinding
import com.fypmoney.util.AppConstants
import com.fypmoney.view.activity.AadhaarAccountActivationView
import kotlinx.android.synthetic.main.toolbar.*

class UpgradeToKycInfoActivity : BaseActivity<ActivityUpgradeToKycInfoBinding,UpgradeToKycInfoActivityVM>() {


    private lateinit var _binding: ActivityUpgradeToKycInfoBinding

    private val binding get() = _binding

    private lateinit var viewModel:UpgradeToKycInfoActivityVM



    override fun onTryAgainClicked() {
    }

    /**
     * Override for set binding variable
     *
     * @return variable id
     */
    override fun getBindingVariable(): Int  = BR.viewModel

    /**
     * @return layout resource id
     */
    override fun getLayoutId(): Int = R.layout.activity_upgrade_to_kyc_info

    /**
     * Override for set view model
     *
     * @return view model instance
     */
    override fun getViewModel(): UpgradeToKycInfoActivityVM {
        viewModel = ViewModelProvider(this).get(UpgradeToKycInfoActivityVM::class.java)
        return viewModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = getViewDataBinding()
        setUpViews()
        setUpObserver()
    }



    private fun setUpViews() {
        setToolbarAndTitle(
            context = this,
            toolbar = toolbar,
            isBackArrowVisible = true, toolbarTitle = getString(R.string.upgrade_aadhar_kyc),
            titleColor = Color.BLACK,
            backArrowTint = Color.BLACK
        )
    }
    private fun setUpObserver() {
        viewModel.event.observe(this) {
            handleEvent(it)
        }
    }

    private fun handleEvent(it: UpgradeToKycInfoActivityVM.UpgradeToKYCEvent?) {
        when(it){
            UpgradeToKycInfoActivityVM.UpgradeToKYCEvent.UpgradeKycClick -> {
                trackr {
                    it.name = TrackrEvent.upgrade_your_kyc_clicked
                }
                startActivity(Intent(this, AadhaarAccountActivationView::class.java).apply {
                    putExtra(AppConstants.KYC_UPGRADE_FROM_WHICH_SCREEN,intent.getStringExtra(AppConstants.KYC_UPGRADE_FROM_WHICH_SCREEN))
                })
            }

        }
    }

}