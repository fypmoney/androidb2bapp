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


import com.fypmoney.view.giftCardModule.viewModel.GiftDetailsModel
import kotlinx.android.synthetic.main.toolbar_gift_card.*
import com.fypmoney.databinding.GiftDetailsActivityBinding
import com.fypmoney.util.AppConstants
import com.fypmoney.util.Utility
import com.fypmoney.view.adapter.offerpointsAdapter
import com.fypmoney.view.interfaces.ListContactClickListener
import org.json.JSONArray


class GiftDetailsActivity : BaseActivity<GiftDetailsActivityBinding, GiftDetailsModel>() {

    private var giftBrand: Int? = null
    private lateinit var mViewModel: GiftDetailsModel
    private lateinit var mViewBinding: GiftDetailsActivityBinding

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.gift_details_activity
    }

    override fun getViewModel(): GiftDetailsModel {
        mViewModel = ViewModelProvider(this).get(GiftDetailsModel::class.java)
        return mViewModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewBinding = getViewDataBinding()


        giftBrand = intent?.getIntExtra(AppConstants.GIFT_ID, -1)

        mViewModel.callVoucherStatus(giftBrand)

        setObserver()
        setToolbarAndTitle(
            context = this,
            toolbar = toolbar,
            isBackArrowVisible = true, toolbarTitle = "Gift card",
            titleColor = Color.WHITE,
            backArrowTint = Color.WHITE
        )


    }

    private fun setObserver() {
        mViewModel.giftDetailsResponse.observe(this) {
            mViewBinding.brandName.text = it?.brandName
            mViewBinding.offerTitle.text = it?.message
            mViewBinding.couponAmount.text = "₹" + Utility.convertToRs(it.amount.toString()) + "/-"
            Glide.with(this).load(it?.detailImage).into(mViewBinding.logo)
            var issuedate = ""
            it.issueDate.let { it1 ->
                issuedate = Utility.parseDateTime(
                    it1,
                    inputFormat = AppConstants.SERVER_DATE_TIME_FORMAT1,
                    outputFormat = AppConstants.CHANGED_DATE_TIME_FORMAT4
                )
            }
            setToolbarAndTitle(
                context = this,
                toolbar = toolbar,
                isBackArrowVisible = true, toolbarTitle = it.productName,
                titleColor = Color.WHITE,
                backArrowTint = Color.WHITE
            )
            var validitydate = ""
            it.endDate.let { it1 ->
                validitydate = Utility.parseDateTime(
                    it1,
                    inputFormat = AppConstants.SERVER_DATE_TIME_FORMAT1,
                    outputFormat = AppConstants.CHANGED_DATE_TIME_FORMAT4
                )
            }

            mViewBinding.giftDetails.text =
                "Voucher Number - ${it.voucherNo}\nVoucher PIN - ${it.voucherGcCode}\n Purchased on - $issuedate\nValidity -$validitydate\nFyp Order Number - ${it.giftVoucherOrderNo}"
            try {
                val jsonArr = JSONArray(it?.tnc)
                var itemsArrayList: ArrayList<String> = ArrayList()
                for (i in 0 until jsonArr.length()) {
                    itemsArrayList.add(jsonArr[i] as String)
                }

//                itemsArrayList.add("Google Play Gift Code purchases are non-refundable. Google Play Gift Codes can only be used on the Google Play Store to purchase apps, games, digital content and in-app items available on the Store.")
//                itemsArrayList.add("* Only use this gift card's code on Google Play. Any other request for the code may be a scam. Visit play.google.com/giftcardscam")
//                itemsArrayList.add("See play.google.com/in-card-terms for full terms. Issued by Google Payment Singapore Pte. Ltd. for purchases of eligible items on Google Play only. Users must be Indian residents aged 18+. Between the ages of 13 and 17, the consent of a parent or guardian is required prior to making a purchase. Requires a Google Payments account and Internet access to redeem. Not usable for hardware and certain subscriptions. Other limits may apply. No fees or expiration. Card not redeemable for cash or other cards, not reloadable or refundable and cannot be combined with non-Google Play balances, resold, exchanged or transferred for value. User is responsible for loss of card. For help, Visit support.google.com/googleplay/go/cardhelp")

                setRecyclerView(itemsArrayList, mViewBinding.recyclerView)
            } catch (e: Exception) {
                e
            }
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
