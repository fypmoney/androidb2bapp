package com.fypmoney.view.storeoffers


import android.app.Dialog
import android.content.ClipboardManager
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableField
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fypmoney.R
import com.fypmoney.databinding.BottomSheetOfferDetailBinding
import com.fypmoney.extension.toGone
import com.fypmoney.util.AppConstants
import com.fypmoney.util.SharedPrefUtils
import com.fypmoney.util.Utility
import com.fypmoney.view.StoreWebpageOpener2
import com.fypmoney.view.adapter.offerpointsAdapter
import com.fypmoney.view.interfaces.ListContactClickListener
import com.fypmoney.view.storeoffers.model.offerDetailResponse
import com.fypmoney.view.webview.ARG_WEB_URL_TO_OPEN
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.bottom_sheet_offer_detail.view.*
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
        Utility.setImageUsingGlideWithShimmerPlaceholder(
            imageView = view.logo,
            url = offerDetails.brandLogo
        )

        view.brandName.text = offerDetails.brandName


        view.copy_code_tv.setOnClickListener(View.OnClickListener {
            Utility.copyTextToClipBoard(
                requireActivity().getSystemService(AppCompatActivity.CLIPBOARD_SERVICE) as ClipboardManager,
                view.referal_code_value_tv.text.trim().toString()
            )
            view.copy_code_tv.text = "Copied"
        })
        offerDetails.couponCode?.let { it ->
            view.referal_code_value_tv.text = it

        } ?: run {
            view.couponcodelayout.toGone()
        }
        view.offer_title.text = offerDetails.offerShortTitle
        view.shop_now_btn.setOnClickListener { it1 ->
            offerDetails.shopUrl?.let { it ->
                when (offerDetails.shopRedirectionType) {
                    AppConstants.OFFER_REDIRECTION_EXTERNAL_WEB_VIEW -> {
                        if (it.isNotEmpty()) {
                            startActivity(
                                Intent.createChooser(
                                    Intent(
                                        Intent.ACTION_VIEW,
                                        Uri.parse(it)
                                    ), getString(R.string.browse_with)
                                )
                            )
                            dismiss()
                        }

                    }
                    AppConstants.OFFER_REDIRECTION_EXTERNAL_WEB_VIEW_SUB -> {
                        if (it.isNotEmpty()) {
                            val url = getSubstutesUrl(offerDetails, it)
                            startActivity(
                                Intent.createChooser(
                                    Intent(
                                        Intent.ACTION_VIEW,
                                        Uri.parse(url)
                                    ), getString(R.string.browse_with)
                                )
                            )
                            dismiss()
                        }
                    }
                    AppConstants.OFFER_REDIRECTION_IN_APP_VIEW_WITH_CARD_OPTION_SUBs -> {
                        if (it.isNotEmpty()) {
                            val url = getSubstutesUrl(offerDetails, it)
                            val intent = Intent(requireContext(), StoreWebpageOpener2::class.java)
                            intent.putExtra(ARG_WEB_URL_TO_OPEN, url)
                            startActivity(intent)
                            dismiss()
                        }

                    }
                    AppConstants.OFFER_REDIRECTION_IN_APP_VIEW_WITH_CARD_OPTIONS -> {
                        if (it.isNotEmpty()) {
                            val intent = Intent(requireContext(), StoreWebpageOpener2::class.java)
                            intent.putExtra(ARG_WEB_URL_TO_OPEN, it)
                            startActivity(intent)
                            dismiss()
                        }

                    }
                }
            } ?: run {
                view.shop_now_btn.toGone()
            }

        }

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

    private fun getSubstutesUrl(offerDetailresponse: offerDetailResponse, it: String): String {
        val offerId = offerDetailresponse.id.toString()
        val const = "$$" + "userId" + "$$"
        val const2 = "$$" + "offerId" + "$$"
        val userId = SharedPrefUtils.getLong(
            requireContext(), key = SharedPrefUtils.SF_KEY_USER_ID
        ).toString()
        val contentWithCode =
            userId.let { userid -> it.replace(const, userid) }

        val url =
            offerId.let { userid -> contentWithCode.replace(const2, userid) }
        return url
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
            offerpointsAdapter(itemsArrayList, requireContext(), itemClickListener2, Color.BLACK)

        recyclerView.adapter = typeAdapter
    }


}