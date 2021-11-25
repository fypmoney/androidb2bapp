package com.fypmoney.view.storeoffers

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseFragment
import com.fypmoney.databinding.OffersStoreBinding
import com.fypmoney.view.fragment.OfferDetailsBottomSheet
import com.fypmoney.view.storeoffers.adapter.SliderAdapter
import com.fypmoney.view.storeoffers.model.offerDetailResponse
import com.fypmoney.viewmodel.OffersViewModel
import kotlin.collections.ArrayList


/**
 * This fragment is used for loyalty
 */
class OffersScreen : BaseFragment<OffersStoreBinding, OffersViewModel>() {

    private var moved: kotlin.Int? = 1
    private lateinit var mViewModel: OffersViewModel
    private lateinit var mViewBinding: OffersStoreBinding


    override fun getBindingVariable(): kotlin.Int {
        return BR.viewModel
    }

    override fun getLayoutId(): kotlin.Int {
        return R.layout.offers_store
    }


    override fun getViewModel(): OffersViewModel {
        mViewModel = ViewModelProvider(this).get(OffersViewModel::class.java)
        return mViewModel
    }

    private var viewPager2: ViewPager2? = null
    private val sliderHandler = Handler()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mViewBinding = getViewDataBinding()
        mViewBinding.viewModel = mViewModel


        viewPager2 = mViewBinding.imageviewpager






        setObservers(requireContext())

    }


    private val sliderRunnable =
        Runnable { viewPager2!!.currentItem = viewPager2!!.currentItem + 1 }

    private fun setObservers(requireContext: Context) {

        mViewModel.offerList.observe(requireActivity()) {


            setListSlider(it)

        }


    }

    private fun setListSlider(arrayList: ArrayList<offerDetailResponse>) {
        moved = 1


        var itemClickListener2 = object : ListOfferClickListener {


            override fun onItemClicked(pos: offerDetailResponse) {

                callOfferDetailsSheeet(pos)

            }


        }

        viewPager2!!.adapter =
            SliderAdapter(arrayList, viewPager2!!, itemClickListener2, requireContext())

        viewPager2!!.clipToPadding = false
        viewPager2!!.clipChildren = false
        viewPager2!!.offscreenPageLimit = 3
        viewPager2!!.getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_NEVER

        val compositePageTransformer = CompositePageTransformer()
        compositePageTransformer.addTransformer(MarginPageTransformer(20))
        compositePageTransformer.addTransformer { page, position ->
            val r = 1 - Math.abs(position)
            page.scaleY = 0.85f + r * 0.05f
        }

        viewPager2!!.setPageTransformer(compositePageTransformer)

        viewPager2!!.registerOnPageChangeCallback(object : OnPageChangeCallback() {
            override fun onPageSelected(position: kotlin.Int) {
                super.onPageSelected(position)
                if (position == 0 && moved == 1) {
                    moved = 0
                    sliderHandler.removeCallbacks(sliderRunnable)
                    sliderHandler.postDelayed(sliderRunnable, 10) // slide duration 2 seconds
                }

            }
        })
    }

    private fun callOfferDetailsSheeet(redeemDetails: offerDetailResponse) {

        var bottomSheetMessage = OfferDetailsBottomSheet(redeemDetails)
        bottomSheetMessage?.dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.RED))
        bottomSheetMessage?.show(childFragmentManager, "TASKMESSAGE")
    }


    override fun onTryAgainClicked() {

    }

    override fun onPause() {
        super.onPause()
        sliderHandler.removeCallbacks(sliderRunnable)
    }

    override fun onResume() {
        super.onResume()
        sliderHandler.postDelayed(sliderRunnable, 2000)
    }

}
