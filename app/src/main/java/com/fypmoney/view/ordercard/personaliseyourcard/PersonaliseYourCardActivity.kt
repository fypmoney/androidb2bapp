package com.fypmoney.view.ordercard.personaliseyourcard

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.core.widget.doAfterTextChanged
import androidx.core.widget.doOnTextChanged
import androidx.databinding.library.baseAdapters.BR
import androidx.lifecycle.ViewModelProvider
import com.fyp.trackr.models.TrackrEvent
import com.fyp.trackr.models.TrackrField
import com.fyp.trackr.models.trackr
import com.fyp.trackr.services.TrackrServices
import com.fypmoney.R
import com.fypmoney.base.BaseActivity
import com.fypmoney.databinding.ActivityPersonaliseYourCardBinding
import com.fypmoney.util.AppConstants
import com.fypmoney.util.SharedPrefUtils
import com.fypmoney.view.ordercard.placeorder.PlaceOrderCardView
import com.google.firebase.analytics.FirebaseAnalytics
import kotlinx.android.synthetic.main.toolbar.*

class PersonaliseYourCardActivity : BaseActivity<ActivityPersonaliseYourCardBinding,PersonaliseYourCardVM>() {
    private lateinit var mViewModel:PersonaliseYourCardVM
    private lateinit var binding: ActivityPersonaliseYourCardBinding

    override fun getBindingVariable(): Int  = BR.viewModel

    override fun getLayoutId(): Int  = R.layout.activity_personalise_your_card

    override fun getViewModel(): PersonaliseYourCardVM {
        mViewModel = ViewModelProvider(this).get(PersonaliseYourCardVM::class.java)
        return mViewModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = getViewDataBinding()
        setToolbarAndTitle(
            context = this@PersonaliseYourCardActivity,
            toolbar = toolbar,
            isBackArrowVisible = true,
            backArrowTint = Color.WHITE,
        )
        mViewModel.userOfferCard = intent.getParcelableExtra(AppConstants.ORDER_CARD_INFO)

        setUpViews()
        setUpObserver()
    }

    private fun setUpObserver() {
        mViewModel.event.observe(this,{
            handelEvent(it)
        })
    }

    private fun handelEvent(it: PersonaliseYourCardVM.PersonaliseCardEvents?) {
        when(it){
            PersonaliseYourCardVM.PersonaliseCardEvents.PersonaliseComplete -> {
                SharedPrefUtils.putString(this,
                    SharedPrefUtils.SF_KEY_NAME_ON_CARD,
                    binding.nameOnCardEt.text.toString()
                )
                trackr {
                    it.name = TrackrEvent.personalised_card_complete
                    it.add(
                        TrackrField.user_id,SharedPrefUtils.getLong(
                        applicationContext,
                        SharedPrefUtils.SF_KEY_USER_ID
                    ).toString())
                }
                val intent = Intent(this@PersonaliseYourCardActivity, PlaceOrderCardView::class.java)
                intent.putExtra(AppConstants.ORDER_CARD_INFO,mViewModel.userOfferCard)
                startActivity(intent)
            }
        }
    }

    override fun onBackPressed() {
        SharedPrefUtils.putString(this,
            SharedPrefUtils.SF_KEY_NAME_ON_CARD,
            binding.nameOnCardEt.text.toString()
        )
        super.onBackPressed()

    }

    private fun setUpViews() {
        binding.nameOnCardEt.doOnTextChanged { text, start, before, count ->
            binding.nameOnCardTv.text = text
        }
        binding.nameOnCardEt.doAfterTextChanged {
            binding.getThisCardBtn.isEnabled = binding.nameOnCardEt.text!!.isNotEmpty()
        }
        SharedPrefUtils.getString(this,
            SharedPrefUtils.SF_KEY_NAME_ON_CARD
        )?.let {
            binding.nameOnCardEt.setText(it)

        }
    }
}