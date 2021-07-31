package com.fypmoney.view.fragment


import android.R.attr.label
import android.app.Dialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import androidx.databinding.DataBindingUtil
import com.fypmoney.R
import com.fypmoney.connectivity.ErrorResponseInfo
import com.fypmoney.connectivity.retrofit.WebApiCaller
import com.fypmoney.databinding.BottomSheetCardDetailsBinding
import com.fypmoney.model.*
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.bottom_sheet_card_details.*
import kotlinx.android.synthetic.main.bottom_sheet_card_details.view.*
import kotlinx.android.synthetic.main.virtual_card_back_layout.*


/*
* This is Card Details
* */
class CardDetailsBottomSheet(var cardInfoDetails: CardInfoDetailsBottomSheet?) :
    BottomSheetDialogFragment(), WebApiCaller.OnWebApiResponse {


    override fun getTheme(): Int = R.style.BottomSheetDialogTheme
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        BottomSheetDialog(requireContext(), theme)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(
            R.layout.bottom_sheet_card_details,
            container,
            false
        )
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val bottomSheet = BottomSheetDialog(requireContext())
        val bindingSheet = DataBindingUtil.inflate<BottomSheetCardDetailsBinding>(
            layoutInflater,
            R.layout.bottom_sheet_card_details,
            null,
            false
        )
        view.copy.setOnClickListener(View.OnClickListener {

            var text =card_details.text.toString()
            val clipboard: ClipboardManager? =
                requireActivity().getSystemService(Context.CLIPBOARD_SERVICE)
              as ClipboardManager?
            val clip = ClipData.newPlainText("Card", text)

            clipboard?.setPrimaryClip(clip)
            Toast.makeText(requireContext(),"Copied to Clipboard",Toast.LENGTH_SHORT).show()
        })


        view.image.setOnCheckedChangeListener { _, isChecked -> // checkbox status is changed from uncheck to checked.
            if (!isChecked) {
                changepasswordvisibilityshow(view.cvvValue)
            } else {
                changepasswordvisibility(view.cvvValue)

            }
        }
        bottomSheet.setContentView(bindingSheet.root)
      view.  card_details.text=cardInfoDetails?.cardNo
        view.    ExpiryValue.text=cardInfoDetails?.expiry_month + "/" + cardInfoDetails?.expiry_year

view.cvvValue.text=cardInfoDetails?.CVV




        return view
    }
    private fun changepasswordvisibilityshow(text: TextView) {
        text.transformationMethod = PasswordTransformationMethod.getInstance()
    }

    private fun changepasswordvisibility(text: TextView) {
        text.transformationMethod = HideReturnsTransformationMethod.getInstance()
    }

    override fun progress(isStart: Boolean, message: String) {


    }

    override fun onSuccess(purpose: String, responseData: Any) {

    }

    override fun onError(purpose: String, errorResponseInfo: ErrorResponseInfo) {


    }

    override fun offLine() {
    }

    /*
    * This method is used to set the card toggle
    * */
    interface OnOpenCardClickListener {
        fun OnOpenCardClickListener()
    }




}