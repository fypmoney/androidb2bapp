package com.fypmoney.view.giftCardModule

import android.graphics.Color
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseActivity
import com.fypmoney.bindingAdapters.shimmerDrawable

import com.fypmoney.view.giftCardModule.viewModel.PurchaseGiftViewModel
import kotlinx.android.synthetic.main.toolbar_gift_card.*
import com.fypmoney.databinding.GiftTAndCBinding
import com.fypmoney.util.AppConstants
import com.fypmoney.view.adapter.offerpointsAdapter
import com.fypmoney.view.giftCardModule.model.VoucherBrandItem
import com.fypmoney.view.interfaces.ListContactClickListener
import org.json.JSONArray


class GiftTermsAndConditions : BaseActivity<GiftTAndCBinding, PurchaseGiftViewModel>() {


    private var giftBrand: VoucherBrandItem? = null
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

        mViewBinding.brandName.text = giftBrand?.description
        mViewBinding.offerTitle.text = giftBrand?.brandTagLine2

        Glide.with(this)
            .load("https://images.livemint.com/img/2021/12/06/1600x900/WhatsApp_Image_2020-05-22_at_11.41.40_1590127957394_1590127957628_1638785122012.jpeg")
            .placeholder(shimmerDrawable())
            .into(mViewBinding.logo)
        try {
            val jsonArr = JSONArray(giftBrand?.howToRedeem)
            var itemsArrayList: ArrayList<String> = ArrayList()
            itemsArrayList.add("Qwikcilver Solutions Private Limited (Qwikcilver) has filed an application with regulatory authorities to consolidate into its parent Pine Labs Private Limited (Pine Labs). On receipt of the approval, the business will get consolidated into a single entity, i.e. Pine Labs and Pine Labs will thereafter honour all valid prepaid gift cards and undertake any other card issuer obligations previously being undertaken by Qwikcilver. There will be no change to your Myntra E- Gift Card, issued by Qwikcilver (Co-branded with Myntra Designs Private Limited), in any manner, such as its use, acceptance, validity, redemption and user experience. Status quo will prevail over the existing valid prepaid gift card balances and no deduction or changes will be made thereto in any manner whatsoever.")
            itemsArrayList.add("Myntra E- Gift Card is issued by Qwikcilver Solutions Pvt. Ltd which is a private limited company incorporated under the laws of India, and is authorised by the Reserve Bank of India (\"RBI\") to issue such Gift Cards.")
            itemsArrayList.add("This E-Gift Card is redeemable only on Myntra marketplace platform.")
            itemsArrayList.add("This E-Gift Card shall have a validity period of 1 year.")
            itemsArrayList.add("This E - Gift Card cannot be used to purchase other gift cards.")
            itemsArrayList.add("Multiple E- Gift Cards can be clubbed in a single order.")
            itemsArrayList.add("E -Gift Cards cannot be redeemed for Cash or Credit and cannot be reloaded.")
            itemsArrayList.add("This E-Gift Card can be redeemed online against sellers listed on www.myntra.com or Myntra Mobile App only.")
            itemsArrayList.add("In cases where the order is cancelled, the E- Gift Card amount shall be refunded to the source E-Gift Card. The E - Gift Card amount shall not be refunded to the user's cashback account.")
            itemsArrayList.add("The balance amount, if any, of the cost of the item being purchased by the Customer, after the discount has been availed, will have to be paid by the Customer at the time of purchase. If the order value exceeds the E -Gift Card amount, the balance must be paid by Credit Card/Debit Card/Internet Banking/Cash on Delivery.")
            for (i in 0 until jsonArr.length()) {

                itemsArrayList.add(jsonArr[i] as String)

            }
            setRecyclerView(itemsArrayList, mViewBinding.recyclerView)
        } catch (e: Exception) {

        }
    }

    private fun setRecyclerView(

        list: List<String?>?,
        recyclerView: RecyclerView
    ) {
        val layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerView.layoutManager = layoutManager

        var itemsArrayList: ArrayList<String> = ArrayList()
        list?.forEach { it ->
            if (it != null) {
                itemsArrayList.add(it)
            }
        }
        var itemClickListener2 = object : ListContactClickListener {


            override fun onItemClicked(pos: kotlin.Int) {

            }


        }
        var typeAdapter =
            offerpointsAdapter(
                itemsArrayList,
                this,
                itemClickListener2,
                Color.WHITE,
                textcolor = Color.WHITE
            )
        recyclerView.adapter = typeAdapter
    }

    /*
    * This method is used to observe the observers
    * */


}
