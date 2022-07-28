package com.fypmoney.view.giftcard

import android.graphics.Color
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseActivity
import com.fypmoney.databinding.ActivityUnsedGiftCardsBinding
import com.fypmoney.util.SharedPrefUtils
import com.fypmoney.view.giftcard.model.GiftHistoryResponseModel
import com.fypmoney.view.giftcard.viewModel.GiftUnsedModel


import kotlinx.android.synthetic.main.toolbar_gift_card.*


class GiftHistoryUnusedActivity : BaseActivity<ActivityUnsedGiftCardsBinding, GiftUnsedModel>() {


    private var mViewBinding: ActivityUnsedGiftCardsBinding? = null
    private var mviewModel: GiftUnsedModel? = null

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_unsed_gift_cards
    }

    override fun getViewModel(): GiftUnsedModel {
        mviewModel = ViewModelProvider(this).get(GiftUnsedModel::class.java)
        return mviewModel!!
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewBinding = getViewDataBinding()

        setToolbarAndTitle(
            context = this,
            toolbar = toolbar,
            isBackArrowVisible = true, toolbarTitle = "Unused Gift Cards",
            titleColor = Color.WHITE,
            backArrowTint = Color.WHITE
        )

        setObservers()

    }

    private fun setRecyclerView(

        list: List<GiftHistoryResponseModel>
    ) {
        val layoutManager =
            GridLayoutManager(this, 2)
        mViewBinding?.giftcardRv?.layoutManager = layoutManager


        var mobile = SharedPrefUtils.getString(
            application,
            SharedPrefUtils.SF_KEY_USER_MOBILE
        )

        val typeAdapter = GiftCardUnsedHistoryAdapter(

            this,
            onRecentUserClick = {

            },
            mobile
        )
        typeAdapter.submitList(list)
        mViewBinding?.giftcardRv?.adapter = typeAdapter

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
