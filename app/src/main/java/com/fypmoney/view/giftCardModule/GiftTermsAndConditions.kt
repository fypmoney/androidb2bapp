package com.fypmoney.view.giftCardModule

import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseActivity

import com.fypmoney.view.giftCardModule.viewModel.PurchaseGiftViewModel
import kotlinx.android.synthetic.main.toolbar_gift_card.*
import com.fypmoney.databinding.GiftTAndCBinding
import com.fypmoney.util.AppConstants
import com.fypmoney.view.giftCardModule.model.GiftProductResponse


class GiftTermsAndConditions : BaseActivity<GiftTAndCBinding, PurchaseGiftViewModel>() {

    private var giftBrand: GiftProductResponse? = null
    private lateinit var mViewModel: PurchaseGiftViewModel
    private lateinit var mViewBinding: GiftTAndCBinding

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.gift_t_and_c
    }

    override fun getViewModel(): PurchaseGiftViewModel {
        mViewModel = ViewModelProvider(this).get(PurchaseGiftViewModel::class.java)
        return mViewModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewBinding = getViewDataBinding()

        setToolbarAndTitle(
            context = this,
            toolbar = toolbar,
            isBackArrowVisible = true, toolbarTitle = "Terms and conditions",
            titleColor = Color.WHITE,
            backArrowTint = Color.WHITE
        )
        giftBrand = intent?.getParcelableExtra(AppConstants.GIFT_BRAND_SELECTED)

        mViewBinding.brandName.text = giftBrand?.displayName
        mViewBinding.offerTitle.text = giftBrand?.giftMessage

        Glide.with(this).load(giftBrand?.detailImage).into(mViewBinding.logo)

    }


    /*
    * This method is used to observe the observers
    * */


}
