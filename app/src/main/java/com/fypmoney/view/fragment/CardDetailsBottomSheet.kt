package com.fypmoney.view.fragment


import android.app.Dialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.fypmoney.R
import com.fypmoney.databinding.BottomSheetCardDetailsBinding
import com.fypmoney.model.*
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.bottom_sheet_card_details.*
import kotlinx.android.synthetic.main.bottom_sheet_card_details.view.*


/*
* This is Card Details
* */
class CardDetailsBottomSheet(var cardInfoDetails: CardInfoDetailsBottomSheet?) :
    BottomSheetDialogFragment() {
    lateinit var mViewBinding: BottomSheetCardDetailsBinding

    override fun getTheme(): Int = R.style.BottomSheetDialogTheme

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        BottomSheetDialog(requireContext(), theme)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mViewBinding = DataBindingUtil.inflate(
            layoutInflater,
            R.layout.bottom_sheet_card_details,
            container,
            false
        )
        return mViewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mViewBinding.copy.setOnClickListener {
            val text = card_details.text.toString()
            val clipboard: ClipboardManager? =
                requireActivity().getSystemService(Context.CLIPBOARD_SERVICE)
                        as ClipboardManager?
            val clip = ClipData.newPlainText(getString(R.string.card), text)

            clipboard?.setPrimaryClip(clip)
            Toast.makeText(requireContext(), getString(R.string.copy_to_clipboard), Toast.LENGTH_SHORT).show()
        }


        mViewBinding.image.setOnCheckedChangeListener { _, isChecked -> // checkbox status is changed from uncheck to checked.
            if (!isChecked) {
                changePasswordVisibilityShow(view.cvvValue)
            } else {
                changePasswordVisibility(view.cvvValue)

            }
        }
        if (!cardInfoDetails?.cardNo.isNullOrEmpty()) {
            mViewBinding.cardDetails.text = cardInfoDetails?.cardNo
        } else {
            mViewBinding.copy.visibility = View.GONE
        }
        if (!cardInfoDetails?.expiry_month.isNullOrEmpty()) {
            mViewBinding.ExpiryValue.text =
                cardInfoDetails?.expiry_month + "/" + cardInfoDetails?.expiry_year

        } else {
            mViewBinding.expiry.visibility = View.INVISIBLE
            mViewBinding.ExpiryValue.visibility = View.INVISIBLE
        }
        if (!cardInfoDetails?.CVV.isNullOrEmpty()) {
            mViewBinding.cvvValue.text = cardInfoDetails?.CVV
        } else {
            mViewBinding.cvv.visibility = View.INVISIBLE
            mViewBinding.cvvValue.visibility = View.INVISIBLE
            mViewBinding.image.visibility = View.INVISIBLE
        }
    }



    private fun changePasswordVisibilityShow(text: TextView) {
        text.transformationMethod = PasswordTransformationMethod.getInstance()
    }


    private fun changePasswordVisibility(text: TextView) {
        text.transformationMethod = HideReturnsTransformationMethod.getInstance()
    }



}