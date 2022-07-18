package com.fypmoney.view.giftcard

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseActivity
import com.fypmoney.databinding.ActivityHistoryGiftCardsBinding
import com.fypmoney.util.AppConstants
import com.fypmoney.util.SharedPrefUtils
import com.fypmoney.view.giftcard.model.GiftHistoryResponseModel
import com.fypmoney.view.giftcard.viewModel.GiftHistoryModel


import kotlinx.android.synthetic.main.toolbar_gift_card.*


class GiftHistoryActivity : BaseActivity<ActivityHistoryGiftCardsBinding, GiftHistoryModel>() {


    private var mViewBinding: ActivityHistoryGiftCardsBinding? = null
    private var mviewModel: GiftHistoryModel? = null

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_history_gift_cards
    }

    override fun getViewModel(): GiftHistoryModel {
        mviewModel = ViewModelProvider(this).get(GiftHistoryModel::class.java)
        return mviewModel!!
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewBinding = getViewDataBinding()

        setToolbarAndTitle(
            context = this,
            toolbar = toolbar,
            isBackArrowVisible = true, toolbarTitle = "Gift Cards History",
            titleColor = Color.WHITE,
            backArrowTint = Color.WHITE
        )
        mViewBinding?.history?.setOnClickListener {
            val intent = Intent(this, GiftHistoryUnusedActivity::class.java)
            startActivity(intent)
        }
        setObservers()

    }

    private fun setRecyclerView(

        list: List<GiftHistoryResponseModel>
    ) {
//        if (list.size > 0) {
//            mViewBinding.shimmerLayout.visibility = View.GONE
//        }
        val layoutManager =
            GridLayoutManager(this, 2)
        mViewBinding?.giftcardRv?.layoutManager = layoutManager

        var mobile = SharedPrefUtils.getString(
            application,
            SharedPrefUtils.SF_KEY_USER_MOBILE
        )


        val typeAdapter = GiftCardHistoryAdapter(

            this,
            onItemClicked = {

                intentToActivity(it.id, GiftDetailsActivity::class.java)
            },
            mobile,
            onReloadClicked = {

                mviewModel?.callVoucherStatus(it.externalOrderId)
            }
        )
        typeAdapter.submitList(list)
        mViewBinding?.giftcardRv?.adapter = typeAdapter

    }

    private fun intentToActivity(contactEntity: Int?, aClass: Class<*>) {
        contactEntity.let {
            val intent = Intent(this, aClass)
            intent.putExtra(AppConstants.GIFT_ID, it)
            startActivity(intent)
        }

    }

    /*
    * This method is used to observe the observers
    * */
    private fun setObservers() {
        mviewModel?.allGiftList?.observe(this) {

            setRecyclerView(it)

        }
        mviewModel?.clicked?.observe(this) {

            if (it?.isVoucherPurchased == AppConstants.YES) {
                intentToActivity(it.id, GiftDetailsActivity::class.java)
            }

        }

    }


}
