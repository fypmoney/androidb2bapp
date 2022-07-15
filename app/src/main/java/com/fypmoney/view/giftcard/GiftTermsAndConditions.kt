package com.fypmoney.view.giftcard

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
import com.fypmoney.databinding.GiftTAndCBinding
import com.fypmoney.util.AppConstants
import com.fypmoney.view.adapter.offerpointsAdapter
import com.fypmoney.view.giftcard.model.VoucherBrandItem
import com.fypmoney.view.giftcard.viewModel.CreateEGiftCardFragmentVM
import com.fypmoney.view.interfaces.ListContactClickListener
import kotlinx.android.synthetic.main.toolbar_gift_card.*
import org.json.JSONArray


class GiftTermsAndConditions : BaseActivity<GiftTAndCBinding, CreateEGiftCardFragmentVM>() {


    private var giftBrand: VoucherBrandItem? = null
    private lateinit var mViewModel: CreateEGiftCardFragmentVM
    private lateinit var mViewBinding: GiftTAndCBinding

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.gift_t_and_c
    }

    override fun getViewModel(): CreateEGiftCardFragmentVM {
        mViewModel = ViewModelProvider(this).get(CreateEGiftCardFragmentVM::class.java)
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

        Glide.with(this).load(giftBrand?.brandLogo).placeholder(shimmerDrawable())
            .into(mViewBinding.logo)
        try {
            val jsonArr = JSONArray(giftBrand?.howToRedeem)
            var itemsArrayList: ArrayList<String> = ArrayList()
//            itemsArrayList.add("Google Play Gift Code purchases are non-refundable. Google Play Gift Codes can only be used on the Google Play Store to purchase apps, games, digital content and in-app items available on the Store.")
//            itemsArrayList.add("* Only use this gift card's code on Google Play. Any other request for the code may be a scam. Visit play.google.com/giftcardscam")
//            itemsArrayList.add("See play.google.com/in-card-terms for full terms. Issued by Google Payment Singapore Pte. Ltd. for purchases of eligible items on Google Play only. Users must be Indian residents aged 18+. Between the ages of 13 and 17, the consent of a parent or guardian is required prior to making a purchase. Requires a Google Payments account and Internet access to redeem. Not usable for hardware and certain subscriptions. Other limits may apply. No fees or expiration. Card not redeemable for cash or other cards, not reloadable or refundable and cannot be combined with non-Google Play balances, resold, exchanged or transferred for value. User is responsible for loss of card. For help, Visit support.google.com/googleplay/go/cardhelp")
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
