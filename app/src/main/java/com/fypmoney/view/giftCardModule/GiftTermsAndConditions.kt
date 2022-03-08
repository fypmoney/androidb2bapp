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

        mViewBinding.brandName.text = giftBrand?.displayName
        mViewBinding.offerTitle.text = giftBrand?.giftMessage

        Glide.with(this).load(giftBrand?.brandLogo).placeholder(shimmerDrawable())
            .into(mViewBinding.logo)
        try {
            val jsonArr = JSONArray(giftBrand?.howToRedeem)
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
            offerpointsAdapter(itemsArrayList, this, itemClickListener2, Color.WHITE)
        recyclerView.adapter = typeAdapter
    }

    /*
    * This method is used to observe the observers
    * */


}
