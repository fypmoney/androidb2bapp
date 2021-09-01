package com.fypmoney.view.fragment


import android.app.Dialog
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableField
import com.bumptech.glide.Glide
import com.fypmoney.R
import com.fypmoney.databinding.BottomSheetRedeemCoinsBinding
import com.fypmoney.model.RedeemDetailsResponse
import com.fypmoney.view.interfaces.ListItemClickListener
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.bottom_sheet_redeem_coins.*
import kotlinx.android.synthetic.main.bottom_sheet_redeem_coins.view.*


class RedeemMyntsBottomSheet(
    val itemClickListener2: ListItemClickListener,
    var spinWheelResponseDetails: RedeemDetailsResponse
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
            R.layout.bottom_sheet_redeem_coins,
            container,
            false
        )
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val bottomSheet = BottomSheetDialog(requireContext())
        val bindingSheet = DataBindingUtil.inflate<BottomSheetRedeemCoinsBinding>(
            layoutInflater,
            R.layout.bottom_sheet_redeem_coins,
            null,
            false
        )


        bottomSheet.setContentView(bindingSheet.root)

        if (spinWheelResponseDetails.pointsToRedeem != null && spinWheelResponseDetails.pointsToRedeem!! > 0) {
            view.lin_coins.visibility = View.VISIBLE
            var amount = spinWheelResponseDetails.pointsToRedeem!! / 100
            view.redeem_mynts.text = amount.toString()
            view.message_image.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.ic_redeem_mynts_illustration
                )
            )


            view.continuebtn.text = "Redeem ${amount} Mynt(s)"
            view.dialog_details.text = "will be added to your wallet in form of cash"
            view.continuebtn.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.white
                )
            )
            view.continuebtn.setBackgroundTintList(ColorStateList.valueOf(resources.getColor(R.color.text_color_dark)));

        } else if (spinWheelResponseDetails.pointsToRedeem != null && spinWheelResponseDetails.pointsToRedeem!! == 0) {

            view.message_image.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.ic_redeem_mynts_illustration
                )
            )

            view.continuebtn.setBackgroundTintList(ColorStateList.valueOf(resources.getColor(R.color.cb_grey)));
            view.continuebtn.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.grey_heading
                )
            )
            view.redeem_mynts.text = "0"
            var amount = spinWheelResponseDetails.pointsToRedeem!! / 100
            view.continuebtn.text = "Redeem ${amount} Mynt(s)"
            view.dialog_details.text =
                "Oops! No Mynts available right now. \nTry your luck tomorrow!"

            view.continuebtn.isEnabled = false

        } else if (spinWheelResponseDetails.pointsToRedeem == null) {
            view.lin_coins.visibility = View.GONE
            view.gif_redeem.visibility = View.VISIBLE
            view.message_image.visibility = View.GONE
            Glide.with(requireContext()).asGif().load(R.raw.redeemmynts_2).into(view.gif_redeem)
            view.continuebtn.text = "Continue"
            view.continuebtn.setBackgroundTintList(ColorStateList.valueOf(resources.getColor(R.color.text_color_dark)));
            view.continuebtn.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.white
                )
            )

            view.dialog_details.text = "It's raining money today. Your wallet\n has been updated."
        }

        view.continuebtn.setOnClickListener(View.OnClickListener {
            if (view.continuebtn.text == "Continue") {
                itemClickListener2.onItemClicked(0)

            } else {
                itemClickListener2.onCallClicked(0)

            }

        })



        return view
    }


}