package com.fypmoney.view.giftCardModule

import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.application.PockketApplication
import com.fypmoney.base.BaseActivity
import com.fypmoney.database.entity.ContactEntity
import com.fypmoney.databinding.ViewPurchaseGiftCard2Binding

import com.fypmoney.util.DialogUtils
import com.fypmoney.util.SharedPrefUtils
import com.fypmoney.util.Utility
import com.fypmoney.view.giftCardModule.fragments.GiftCardNotFyperBottomSheet
import com.fypmoney.view.giftCardModule.fragments.GiftCardPurchasedBottomSheet
import com.fypmoney.view.giftCardModule.model.PurchaseGiftCardRequest
import com.fypmoney.view.giftCardModule.model.PurchaseGiftCardResponse
import com.fypmoney.view.giftCardModule.model.VoucherDetailsItem
import com.fypmoney.view.giftCardModule.model.VoucherProductItem

import com.fypmoney.view.giftCardModule.viewModel.PurchaseGiftViewModel
import kotlinx.android.synthetic.main.toolbar_gift_card.*
import java.util.*
import kotlin.collections.ArrayList
import android.widget.RadioButton

import android.widget.RadioGroup
import kotlinx.android.synthetic.main.view_purchase_gift_card2.view.*


class GiftTermsAndConditions : BaseActivity<ViewPurchaseGiftCard2Binding, PurchaseGiftViewModel>() {


    private lateinit var mViewModel: PurchaseGiftViewModel
    private lateinit var mViewBinding: ViewPurchaseGiftCard2Binding

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.view_purchase_gift_card2
    }

    override fun getViewModel(): PurchaseGiftViewModel {
        mViewModel = ViewModelProvider(this).get(PurchaseGiftViewModel::class.java)
        return mViewModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewBinding = getViewDataBinding()
        mViewBinding?.viewModel = mViewModel
        setToolbarAndTitle(
            context = this,
            toolbar = toolbar,
            isBackArrowVisible = true, toolbarTitle = "Terms and conditions",
            titleColor = Color.WHITE,
            backArrowTint = Color.WHITE
        )


    }


    /*
    * This method is used to observe the observers
    * */


}
