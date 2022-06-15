package com.fypmoney.view.giftcard.fragments


import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.fypmoney.R
import com.fypmoney.databinding.BottomSheetPurschaseNotFyperBinding
import com.fypmoney.view.giftcard.model.PurchaseGiftCardRequest
import com.fypmoney.view.giftcard.model.VoucherDetailsItem
import com.fypmoney.view.giftcard.viewModel.CreateEGiftCardFragmentVM
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.util.*


class GiftCardNotFyperBottomSheet(val mViewModel: CreateEGiftCardFragmentVM) :
    BottomSheetDialogFragment() {
    private lateinit var binding: BottomSheetPurschaseNotFyperBinding
    override fun getTheme(): Int = R.style.BottomSheetDialogTheme

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        BottomSheetDialog(requireContext(), theme)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.bottom_sheet_purschase_not_fyper,
            container,
            false
        )

        binding.purchaseGift.setOnClickListener(View.OnClickListener {

            var purchase = PurchaseGiftCardRequest()
            purchase.destinationMobileNo = binding.etMobile.text.toString()
//                      purchase.destinationName =
//                          mViewModel.selectedContactFromList.get()?.firstName + " " +  mViewModel.selectedContactFromList.get()?.lastName
//                      if ( mViewModel.selectedContactFromList.get()?.isAppUser == true)
//                          purchase.giftedPerson = "FYPUSER"
//                      else

            purchase.giftedPerson = "NOTFYPUSER"

            purchase.destinationEmail = binding.etEmail.text.toString()

            var voucher = VoucherDetailsItem()
            voucher.voucherProductId = mViewModel.selectedGiftCard.get()?.id
            val supplierNames: List<VoucherDetailsItem> = Arrays.asList(voucher)
            purchase.voucherDetails = supplierNames
            mViewModel.purchaseGiftCardRequest(purchase)

        })
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }


}