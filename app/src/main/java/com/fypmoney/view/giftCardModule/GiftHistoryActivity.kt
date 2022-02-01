package com.fypmoney.view.giftCardModule

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
import com.fypmoney.view.giftCardModule.adapters.GiftListBaseAdapter
import com.fypmoney.view.giftCardModule.model.GiftSearchResponse
import com.fypmoney.view.giftCardModule.model.VoucherBrandItem
import com.fypmoney.view.giftCardModule.viewModel.GiftCardViewModel
import com.fypmoney.view.home.main.explore.ViewDetails.ExploreInAppWebview
import com.fypmoney.view.home.main.explore.`interface`.ExploreItemClickListener
import com.fypmoney.view.home.main.explore.model.ExploreContentResponse
import com.fypmoney.view.home.main.explore.model.SectionContentItem

import kotlinx.android.synthetic.main.toolbar_gift_card.*
import java.util.ArrayList


class GiftHistoryActivity : BaseActivity<ActivityAllGiftCardsBinding, GiftCardViewModel>() {


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

        setObservers()

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
                val intent = Intent(this, PurchaseGiftCardScreen2::class.java)

                startActivity(intent)
            },
            this
        )
        mViewBinding?.giftcardRv?.adapter = typeAdapter
    }

    /*
    * This method is used to observe the observers
    * */
    private fun setObservers() {
        mviewModel?.allGiftList?.observe(this, {

            it.forEach {

            }
            setRecyclerView(it)

        })
    }


}
