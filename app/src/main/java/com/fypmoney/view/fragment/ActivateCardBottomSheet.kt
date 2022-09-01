package com.fypmoney.view.fragment


import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextUtils
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableField
import com.fypmoney.R
import com.fypmoney.application.PockketApplication
import com.fypmoney.databinding.BottomSheetActivateCardBinding
import com.fypmoney.util.Utility
import com.fypmoney.util.textview.ClickableSpanListener
import com.fypmoney.util.textview.FYPClickableSpan
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class ActivateCardBottomSheet(
    var onActivateCardClickListener: OnActivateCardClickListener,
    var onDismissListner: OnActivateSheetDismissListner? = null
) : BottomSheetDialogFragment() {

    lateinit var binding: BottomSheetActivateCardBinding
    override fun getTheme(): Int = R.style.BottomSheetDialogTheme
    var otp = ObservableField<String>()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        BottomSheetDialog(requireContext(), theme)

    override fun dismiss() {
        super.dismiss()
        onDismissListner?.OnDismiss()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.bottom_sheet_activate_card,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpviews()
    }

    private fun setUpviews() {
        binding.tNCCb.setOnCheckedChangeListener { buttonView, isChecked ->
            binding.buttonOtp.isEnabled = isChecked
        }
        binding.buttonOtp.setOnClickListener {
            when {
                TextUtils.isEmpty(otp.get()) -> {
                    Utility.showToast(PockketApplication.instance.getString(R.string.card_kit_empty_error))
                }
                else -> {
                    onActivateCardClickListener.onActivateCardClick(otp.get())

                }
            }


        }
        binding.otpView.setOtpCompletionListener { otp1 -> // do Stuff
            otp.set(otp1)
        }

        showPrivacyPolicyAndTermsAndConditions()
    }

    interface OnActivateCardClickListener {
        fun onActivateCardClick(kitFourDigit: String?)
        fun onPrivacyPolicyTermsClicked(title: String, url: String)

    }

    interface OnActivateSheetDismissListner {
        fun OnDismiss()


    }


    private fun showPrivacyPolicyAndTermsAndConditions() {
        val text = resources.getString(
            R.string.by_tapping_activate_now_you_agree_to_the_terms_of_service_privacy_policy,
            resources.getString(R.string.terms_and_conditions),
            resources.getString(R.string.privacy_policy)
        )
        val privacyPolicyText = resources.getString(R.string.privacy_policy)
        val tAndCText = resources.getString(R.string.terms_and_conditions)
        val privacyPolicyStarIndex = text.indexOf(privacyPolicyText)
        val privacyPolicyEndIndex = privacyPolicyStarIndex + privacyPolicyText.length
        val tAndCStartIndex = text.indexOf(tAndCText)
        val tAndCEndIndex = tAndCStartIndex + tAndCText.length
        val ss = SpannableString(text);
        ss.setSpan(

            FYPClickableSpan(pos = 1, clickableSpanListener = object : ClickableSpanListener {
                override fun onPositionClicked(pos: Int) {
                    onActivateCardClickListener.onPrivacyPolicyTermsClicked(getString(R.string.privacy_policy),"https://www.fypmoney.in/fyp/privacy-policy/")

                }
            }),
            privacyPolicyStarIndex,
            privacyPolicyEndIndex,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        ss.setSpan(
            FYPClickableSpan(pos = 2, clickableSpanListener = object : ClickableSpanListener {
                override fun onPositionClicked(pos: Int) {
                    onActivateCardClickListener.onPrivacyPolicyTermsClicked(getString(R.string.terms_and_conditions),"https://www.fypmoney.in/fyp/terms-of-use/")
                }
            }),
            tAndCStartIndex,
            tAndCEndIndex,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        binding.tNCTv.text = ss
        binding.tNCTv.highlightColor = Color.TRANSPARENT
        binding.tNCTv.movementMethod = LinkMovementMethod.getInstance()
    }



}