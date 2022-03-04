package com.fypmoney.view.giftCardModule

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseActivity
import com.fypmoney.databinding.ActivityViewAllGiftCardsBinding
import com.fypmoney.util.AppConstants
import com.fypmoney.view.giftCardModule.model.GiftSearchResponse
import com.fypmoney.view.giftCardModule.model.VoucherBrandItem
import com.fypmoney.view.giftCardModule.viewModel.GiftViewAllViewModel

import kotlinx.android.synthetic.main.toolbar_gift_card.*


class GiftViewAllScreen : BaseActivity<ActivityViewAllGiftCardsBinding, GiftViewAllViewModel>() {


    private var mViewBinding: ActivityViewAllGiftCardsBinding? = null
    private var mviewModel: GiftViewAllViewModel? = null
    private var gotBrandDetails: GiftSearchResponse? = null
    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_view_all_gift_cards
    }

    override fun getViewModel(): GiftViewAllViewModel {
        mviewModel = ViewModelProvider(this).get(GiftViewAllViewModel::class.java)
        return mviewModel!!
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewBinding = getViewDataBinding()




        gotBrandDetails = intent?.getParcelableExtra(AppConstants.GIFT_BRAND_SELECTED)
        gotBrandDetails?.voucherBrand.let {
            setRecyclerView(it)

        }


        setToolbarAndTitle(
            context = this,
            toolbar = toolbar,
            isBackArrowVisible = true, toolbarTitle = gotBrandDetails?.name,
            titleColor = Color.WHITE,
            backArrowTint = Color.WHITE
        )

    }

    private fun setRecyclerView(

        list: List<VoucherBrandItem?>?
    ) {
        val giftCardAdapter = GiftCardAdapter(
            this, onRecentUserClick = {
                intentToActivity(it, PurchaseGiftCardScreen2::class.java)
            }
        )


        mViewBinding?.let {
            with(it.rvBase) {
                adapter = giftCardAdapter
                layoutManager = GridLayoutManager(
                    context,
                    2
                )
            }
        }

        (mViewBinding?.rvBase?.adapter as GiftCardAdapter).run {
            list.let {
                submitList(it)
            }

        }
    }

    private fun intentToActivity(voucherBrandItem: VoucherBrandItem, aClass: Class<*>) {
        val intent = Intent(this, aClass)
        intent.putExtra(AppConstants.GIFT_BRAND_SELECTED, voucherBrandItem)
        startActivity(intent)
    }

    /*
    * This method is used to observe the observers
    * */
    private fun setObservers() {

    }


}
