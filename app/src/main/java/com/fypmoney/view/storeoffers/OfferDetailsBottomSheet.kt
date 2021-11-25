package com.fypmoney.view.fragment


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

        Glide.with(requireContext()).load(offerDetails.brandLogo).placeholder(shimmerDrawable())
            .into(view.logo)
        view.brandName.text = offerDetails.brandName




        setRecyclerView(view, offerDetails.offerContent, view.rv_details)
        setRecyclerView(view, offerDetails.tnc, view.recycler_view)

//        view.continuebtn.setOnClickListener(View.OnClickListener {
//            if (view.continuebtn.text == "Continue") {
//                itemClickListener2.onItemClicked(0)
//
//            } else {
//                itemClickListener2.onCallClicked(0)
//
//            }
//
//        })


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