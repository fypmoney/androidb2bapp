package com.fypmoney.view.giftcard

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseActivity
import com.fypmoney.databinding.ActivityAllGiftCardsBinding
import com.fypmoney.util.AppConstants
import com.fypmoney.view.giftcard.adapters.GiftListBaseAdapter
import com.fypmoney.view.giftcard.model.GiftSearchResponse
import com.fypmoney.view.giftcard.model.VoucherBrandItem
import com.fypmoney.view.giftcard.view.CreateEGiftCardFragment
import com.fypmoney.view.giftcard.viewModel.GiftCardViewModel
import kotlinx.android.synthetic.main.toolbar_gift_card.*


class GiftCardsListScreen : BaseActivity<ActivityAllGiftCardsBinding, GiftCardViewModel>() {


    private var mViewBinding: ActivityAllGiftCardsBinding? = null
    private var mviewModel: GiftCardViewModel? = null

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_all_gift_cards
    }

    override fun getViewModel(): GiftCardViewModel {
        mviewModel = ViewModelProvider(this).get(GiftCardViewModel::class.java)
        return mviewModel!!
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewBinding = getViewDataBinding()

        setToolbarAndTitle(
            context = this,
            toolbar = toolbar,
            isBackArrowVisible = true, toolbarTitle = "Gift Cards",
            titleColor = Color.WHITE,
            backArrowTint = Color.WHITE
        )
        mViewBinding?.history?.setOnClickListener {
            val intent = Intent(this, GiftHistoryActivity::class.java)
            startActivity(intent)
        }
        setObservers()
//        SharedPrefUtils.putString(
//            getApplication(), key = SharedPrefUtils.SF_KEY_ACCESS_TOKEN,
//            value = "80e7e6f4-4988-4d11-8c01-7425815b1e7b-0f5fb58f238e3b6341b9e8349fb1a87d3a01d781fa6aa1e49bd387e93d0bd4c0"
//        )

    }

    private fun setRecyclerView(

        list: List<GiftSearchResponse>
    ) {
//        if (list.size > 0) {
//            mViewBinding.shimmerLayout.visibility = View.GONE
//        }
        val layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        mViewBinding?.giftcardRv?.layoutManager = layoutManager


        var arrayList: ArrayList<GiftSearchResponse> = ArrayList()
        list.forEach {
            if (it.voucherBrand?.size!! > 0) {
                arrayList.add(it)
            }
        }

        val typeAdapter = GiftListBaseAdapter(
            arrayList,
            this,
            onRecentUserClick = {


                intentToActivity(it, CreateEGiftCardFragment::class.java)

            },
            this, viewAllClicked = {
                intentToActivity(it, GiftViewAllScreen::class.java)
            }
        )
        mViewBinding?.giftcardRv?.adapter = typeAdapter
    }

    private fun intentToActivity(voucherBrandItem: VoucherBrandItem, aClass: Class<*>) {
        val intent = Intent(this, aClass)
        intent.putExtra(AppConstants.GIFT_BRAND_SELECTED, voucherBrandItem)
        startActivity(intent)
    }

    private fun intentToActivity(voucherBrandItem: GiftSearchResponse, aClass: Class<*>) {
        val intent = Intent(this, aClass)
        intent.putExtra(AppConstants.GIFT_BRAND_SELECTED, voucherBrandItem)
        startActivity(intent)
    }

    /*
    * This method is used to observe the observers
    * */
    private fun setObservers() {
        mviewModel?.allGiftList?.observe(this) {

            setRecyclerView(it)

        }
    }


}
