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


import com.fypmoney.view.giftCardModule.viewModel.PurchaseGiftViewModel
import kotlinx.android.synthetic.main.toolbar_gift_card.*
import com.fypmoney.databinding.GiftDetailsActivityBinding
import com.fypmoney.util.AppConstants
import com.fypmoney.view.adapter.offerpointsAdapter
import com.fypmoney.view.giftCardModule.model.GiftHistoryResponseModel
import com.fypmoney.view.interfaces.ListContactClickListener
import org.json.JSONArray


class GiftDetailsActivity : BaseActivity<GiftDetailsActivityBinding, PurchaseGiftViewModel>() {

    private var giftBrand: GiftHistoryResponseModel? = null
    private lateinit var mViewModel: PurchaseGiftViewModel
    private lateinit var mViewBinding: GiftDetailsActivityBinding

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.gift_details_activity
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
            isBackArrowVisible = true, toolbarTitle = "Gift card",
            titleColor = Color.WHITE,
            backArrowTint = Color.WHITE
        )
        giftBrand = intent?.getParcelableExtra(AppConstants.GIFT_HISTORY_SELECTED)

        mViewBinding.brandName.text = giftBrand?.productName
        mViewBinding.offerTitle.text = giftBrand?.message

        Glide.with(this).load(giftBrand?.detailImage).into(mViewBinding.logo)

        try {
            val jsonArr = JSONArray(giftBrand?.tnc)
            var itemsArrayList: ArrayList<String> = ArrayList()
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
            offerpointsAdapter(itemsArrayList, this, itemClickListener2)
        recyclerView.adapter = typeAdapter
    }


    /*
    * This method is used to observe the observers
    * */


}
