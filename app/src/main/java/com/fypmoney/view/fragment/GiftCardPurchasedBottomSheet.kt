package com.fypmoney.view.fragment


import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.fypmoney.R
import com.fypmoney.databinding.GiftCardPurchasedBottomSheetBinding
import com.fypmoney.model.UpdateTaskGetResponse
import com.fypmoney.util.Utility
import com.fypmoney.view.giftCardModule.model.PurchaseGiftCardResponse
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.lang.Exception


class GiftCardPurchasedBottomSheet(
    var giftCrad: PurchaseGiftCardResponse?

) : BottomSheetDialogFragment() {
    private lateinit var binding: GiftCardPurchasedBottomSheetBinding
    override fun getTheme(): Int = R.style.BottomSheetDialogTheme

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        BottomSheetDialog(requireContext(), theme)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.gift_card_purchased_bottom_sheet,
            container,
            false
        )

        binding.msgPurchase.text = giftCrad?.msg
        Utility.setImageUsingGlide(requireContext(), giftCrad?.detailImage, binding.messageImage)
        binding.continuebtn.setOnClickListener(View.OnClickListener {
            try {
                dismiss()
            } catch (e: Exception) {

            }
        })
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }


}