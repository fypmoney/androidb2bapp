package com.fypmoney.view.fragment


import android.R.attr
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableField
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fypmoney.R
import com.fypmoney.bindingAdapters.shimmerDrawable
import com.fypmoney.databinding.BottomSheetOfferDetailBinding
import com.fypmoney.view.adapter.offerpointsAdapter
import com.fypmoney.view.interfaces.ListContactClickListener

import com.fypmoney.view.storeoffers.model.offerDetailResponse
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.bottom_sheet_offer_detail.view.*
import kotlinx.android.synthetic.main.bottom_sheet_offer_detail.view.recycler_view
import org.json.JSONObject

import android.R.attr.data
import androidx.core.content.ContextCompat
import com.fypmoney.bindingAdapters.setBackgroundDrawable
import com.fypmoney.model.HistoryItem
import com.fypmoney.util.Utility
import kotlinx.android.synthetic.main.bottom_sheet_offer_detail.view.logo
import kotlinx.android.synthetic.main.reward_offer_detail.*
import kotlinx.android.synthetic.main.slide_item_container.*
import kotlinx.android.synthetic.main.slide_item_container.view.*

import org.json.JSONArray


class OfferDetailsBottomSheet(

    var offerDetails: offerDetailResponse
) :
    BottomSheetDialogFragment() {
    override fun getTheme(): Int = R.style.BottomSheetDialogTheme
    var otp = ObservableField<String>()
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        BottomSheetDialog(requireContext(), theme)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(
            R.layout.bottom_sheet_offer_detail,
            container,
            false
        )
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val bottomSheet = BottomSheetDialog(requireContext())
        val bindingSheet = DataBindingUtil.inflate<BottomSheetOfferDetailBinding>(
            layoutInflater,
            R.layout.bottom_sheet_offer_detail,
            null,
            false
        )


        bottomSheet.setContentView(bindingSheet.root)
        Utility.setImageUsingGlideWithShimmerPlaceholder(imageView = view.logo, url = offerDetails.brandLogo)

        view.brandName.text = offerDetails.brandName

        view.offer_title.text = offerDetails.offerShortTitle

        try {
            val jsonArr = JSONArray(offerDetails?.tnc)
            var itemsArrayList: ArrayList<String> = ArrayList()
            for (i in 0 until jsonArr.length()) {

                itemsArrayList.add(jsonArr[i] as String)

            }
            setRecyclerView(view, itemsArrayList, view.recycler_view)
        } catch (e: Exception) {

        }
        try {
            val jsonArr2 = JSONArray(offerDetails?.offerContent)
            var itemsArrayList2: ArrayList<String> = ArrayList()
            for (i in 0 until jsonArr2.length()) {

                itemsArrayList2.add(jsonArr2[i] as String)

            }



            setRecyclerView(view, itemsArrayList2, view.rv_details)

        } catch (e: Exception) {

        }



        return view
    }

    private fun setRecyclerView(
        root: View,
        list: List<String?>?,
        recyclerView: RecyclerView
    ) {
        val layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
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
            offerpointsAdapter(itemsArrayList, requireContext(), itemClickListener2)
        recyclerView.adapter = typeAdapter
    }


}