package com.fypmoney.view.activity

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.fyp.trackr.models.TrackrEvent
import com.fyp.trackr.models.trackr
import com.fyp.trackr.services.TrackrServices
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseActivity
import com.fypmoney.bindingAdapters.roundedImageView
import com.fypmoney.databinding.RewardOfferDetailBinding
import com.fypmoney.databinding.ViewAgeAllowedAccountBinding
import com.fypmoney.model.FeedDetails
import com.fypmoney.util.AppConstants

import com.fypmoney.util.Utility
import com.fypmoney.view.StoreWebpageOpener2
import com.fypmoney.view.adapter.OffersTopAdapter
import com.fypmoney.view.adapter.offerpointsAdapter
import com.fypmoney.view.interfaces.ListContactClickListener
import com.fypmoney.viewmodel.OffersDetailsViewModel

import kotlinx.android.synthetic.main.reward_offer_detail.*
import kotlinx.android.synthetic.main.toolbar.*

/*
* This class is used to handle school name city
* */
class OfferDetailActivity :
    BaseActivity<RewardOfferDetailBinding, OffersDetailsViewModel>() {
    private var tandCStr: String? = null
    private var typeAdapter: offerpointsAdapter? = null
    private lateinit var mViewModel: OffersDetailsViewModel
    private var itemsArrayList: ArrayList<String> = ArrayList()
    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.reward_offer_detail
    }

    override fun getViewModel(): OffersDetailsViewModel {
        mViewModel = ViewModelProvider(this).get(OffersDetailsViewModel::class.java)
        return mViewModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val id = intent.getStringExtra("feedid")

        back.setOnClickListener {
            super.onBackPressed()
        }

        tandc.setOnClickListener {
            if (tandCStr != null) {
                val intent = Intent(this@OfferDetailActivity, HtmlOpenerView::class.java)
                HtmlOpenerView.tandC = tandCStr as String
                startActivity(intent)
            } else {
                Utility.showToast("Loading")
            }

        }


        mViewModel.callFetchFeedsApi(id)
        setRecyclerView()
        appCompatButton.setOnClickListener {
            val text = couponcode.text.toString()
            val clipboard: ClipboardManager? =
                getSystemService(Context.CLIPBOARD_SERVICE)
                        as ClipboardManager?
            val clip = ClipData.newPlainText(getString(R.string.card), text)

            clipboard?.setPrimaryClip(clip)
            Toast.makeText(this, getString(R.string.copy_to_clipboard), Toast.LENGTH_SHORT).show()
            trackr { it.services = arrayListOf(TrackrServices.FIREBASE, TrackrServices.MOENGAGE)
                it.name = TrackrEvent.OFFERCOPY
            }
        }

        mViewModel!!.offerDetail.observe(
            this,
            { list ->
                tandCStr = list.tnc
                //roundedImageView(image_banner,bottomLeft = 44.0f,bottomRight = 44.0f)
                Utility.setImageUsingGlideWithShimmerPlaceholder(imageView = image_banner, url = list.innerBannerImg)
                offer_title.text = list?.title


                list?.details?.forEach { it ->
                    it?.let { it1 -> itemsArrayList.add(it1) }

                }
                couponcode.text = list?.code
                descrip.text = Utility.parseDateTime(
                    list?.date,
                    inputFormat = AppConstants.SERVER_DATE_TIME_FORMAT1,
                    outputFormat = AppConstants.CHANGED_DATE_TIME_FORMAT4
                )
                Utility.setImageUsingGlideWithShimmerPlaceholder(context = this, imageView = logo, url = list.logoImg)

                typeAdapter?.notifyDataSetChanged()
                nsv_details.visibility = View.VISIBLE

                if(mViewModel.action.value?.url.isNullOrEmpty()){
                    view_details_tv.visibility = View.GONE
                }
            })


        view_details_tv.setOnClickListener {
            mViewModel.action.value?.url?.let {
                val intent2 = Intent(this, StoreWebpageOpener2::class.java)
                StoreWebpageOpener2.url =it
//                    intent2.putExtra("title", mViewModel.offerDetail.value?.title)
                startActivity(intent2)
            }

        }
    }

    private fun setRecyclerView() {
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recycler_view.layoutManager = layoutManager

        var itemClickListener2 = object : ListContactClickListener {


            override fun onItemClicked(pos: Int) {
//
//                val intent = Intent(this@OfferDetailActivity, OfferDetailActivity::class.java)
//
//                intent.putExtra("feedid",itemsArrayList[pos].id)
//
//
//
//
//
//                startActivity(intent)
            }


        }

        typeAdapter = offerpointsAdapter(itemsArrayList, this, itemClickListener2!!)
        recycler_view.adapter = typeAdapter
    }
}
