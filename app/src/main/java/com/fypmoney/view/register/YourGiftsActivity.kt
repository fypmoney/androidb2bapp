package com.fypmoney.view.register

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import com.fyp.trackr.models.TrackrEvent
import com.fyp.trackr.models.trackr
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseActivity
import com.fypmoney.databinding.ActivityYourGiftsBinding
import com.fypmoney.util.Utility
import com.fypmoney.view.activity.PermissionsActivity
import com.fypmoney.view.home.main.homescreen.view.HomeActivity
import com.fypmoney.view.kycagent.ui.KycAgentActivity
import com.fypmoney.view.register.viewModel.YourgiftVM

class YourGiftsActivity : BaseActivity<ActivityYourGiftsBinding, YourgiftVM>() {

    private lateinit var binding: ActivityYourGiftsBinding
    private val yourGiftVM by viewModels<YourgiftVM> { defaultViewModelProviderFactory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = getViewDataBinding()
        binding.continueBtn.setOnClickListener {
            trackr {
                it.name = TrackrEvent.onboard_offer_screen_continue
            }
            if (hasPermissions(this, Manifest.permission.READ_CONTACTS)) {
                intentToActivity(KycAgentActivity::class.java, true)
            } else {
                intentToActivity(PermissionsActivity::class.java, true)
            }
        }

        setObserver()
    }

    private fun intentToActivity(aClass: Class<*>, isFinishAll: Boolean? = false) {
        val intent = Intent(this, aClass)
        startActivity(intent)
        if (isFinishAll == true) {
            finishAffinity()
        }
    }

    private fun setObserver() {
        yourGiftVM?.giftsList?.observe(
            this
        ) { list ->

            list.forEach {
                if (it.giftType == getString(R.string.mynts_caps)) {

                    binding.myntsLayout.visibility = View.VISIBLE
                    binding.myntsWon.text = it.message

                } else if (it.giftType == getString(R.string.card_caps)) {


                    binding.cardOfferPriceValueTv.text =
                        "₹" + Utility.convertToRs(it.value.toString())
                    binding.cardLayout.visibility = View.VISIBLE
                }
            }
        }
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
    override fun getLayoutId(): Int = R.layout.activity_your_gifts

    /**
     * Override for set view model
     *
     * @return view model instance
     */
    override fun getViewModel(): YourgiftVM = yourGiftVM


}