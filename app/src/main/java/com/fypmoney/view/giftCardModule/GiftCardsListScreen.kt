package com.fypmoney.view.giftCardModule

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseActivity
import com.fypmoney.databinding.ActivityAllGiftCardsBinding
import com.fypmoney.model.FeedDetails
import com.fypmoney.view.giftCardModule.viewModel.GiftCardViewModel

import kotlinx.android.synthetic.main.toolbar_gift_card.*
import java.util.*
import kotlin.collections.ArrayList


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
            isBackArrowVisible = true, toolbarTitle = "Gift Cards"
        )

        setObservers()
        setUpRecyclerView()
    }


    private fun setUpRecyclerView() {
        val giftCardAdapter = GiftCardAdapter(
            this, onRecentUserClick = {
                val intent = Intent(this, PurchaseGiftCardScreen::class.java)
                startActivity(intent)
            }
        )


        with(mViewBinding!!.giftcardRv) {
            adapter = giftCardAdapter
            layoutManager = GridLayoutManager(
                this@GiftCardsListScreen,
                2
            )
        }
        var itemsArrayList: ArrayList<FeedDetails> = ArrayList()

        var item = FeedDetails()
        itemsArrayList.add(item)
        itemsArrayList.add(item)
        itemsArrayList.add(item)
        itemsArrayList.add(item)
        itemsArrayList.add(item)
        (mViewBinding?.giftcardRv?.adapter as GiftCardAdapter).run {
            submitList(itemsArrayList)
        }
    }

    /*
    * This method is used to observe the observers
    * */
    private fun setObservers() {

//        mviewModel?.offerBottomList?.observe(this, {
//            (mViewBinding?.giftcardRv?.adapter as GiftCardAdapter).run {
//                submitList(it)
//            }
//
//        })

    }


}
