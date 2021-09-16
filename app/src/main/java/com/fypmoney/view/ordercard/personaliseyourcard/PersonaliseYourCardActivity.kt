package com.fypmoney.view.ordercard.personaliseyourcard

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.core.widget.doAfterTextChanged
import androidx.core.widget.doOnTextChanged
import androidx.databinding.library.baseAdapters.BR
import androidx.lifecycle.ViewModelProvider
import com.fypmoney.R
import com.fypmoney.base.BaseActivity
import com.fypmoney.databinding.ActivityPersonaliseYourCardBinding
import com.fypmoney.util.AppConstants
import com.fypmoney.util.SharedPrefUtils
import com.fypmoney.view.ordercard.placeorder.PlaceOrderCardView
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
                val intent = Intent(this@PersonaliseYourCardActivity, PlaceOrderCardView::class.java)
                intent.putExtra(AppConstants.ORDER_CARD_INFO,mViewModel.userOfferCard)
                startActivity(intent)
            }
        }
    }

    private fun setUpViews() {
        binding.nameOnCardEt.doOnTextChanged { text, start, before, count ->
            binding.nameOnCardTv.text = text
        }
        binding.nameOnCardEt.doAfterTextChanged {
            binding.getThisCardBtn.isEnabled = true
        }
        SharedPrefUtils.getString(this,
            SharedPrefUtils.SF_KEY_NAME_ON_CARD
        )?.let {
            binding.nameOnCardEt.setText(it)

        }
    }
}