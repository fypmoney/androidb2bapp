package com.fypmoney.view.home.main.homescreen.view

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import com.fypmoney.R
import com.fypmoney.base.BaseBottomSheetFragment
import com.fypmoney.base.BaseViewModel
import com.fypmoney.databinding.BottomsheetLoadMoneyBinding
import com.fypmoney.util.AppConstants
import com.fypmoney.util.Utility
import com.fypmoney.view.activity.AddMoneyUpiDebitView
import com.fypmoney.viewmodel.AddMoneyViewModel
import com.google.firebase.crashlytics.FirebaseCrashlytics
import kotlinx.android.synthetic.main.view_add_money.*

class LoadMoneyBottomSheet:BaseBottomSheetFragment<BottomsheetLoadMoneyBinding>() {

    private val loadMoneyBottomSheetVM by viewModels<AddMoneyViewModel> {
        defaultViewModelProviderFactory
    }

    override val baseFragmentVM: BaseViewModel
        get() = loadMoneyBottomSheetVM
    override val customTag: String
        get() = LoadMoneyBottomSheet::class.java.simpleName
    override val layoutId: Int
        get() = R.layout.bottomsheet_load_money


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpBinding()
        binding.lottieLayerName.gifResource = R.raw.load_money_gif
        setObserver()
    }

    private fun setUpBinding(){
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = loadMoneyBottomSheetVM
        }
    }
    private fun setObserver() {
        binding.addMoneyEditext.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                try{
                    if (s.toString().startsWith("0")) {
                        s.clear()
                    } else {
                        if (s.toString().isNotEmpty()) {
                            binding.btnSendOtp.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.text_color_dark))
                            binding.btnSendOtp.setTextColor(
                                ContextCompat.getColor(
                                    requireContext(),
                                    R.color.white
                                )
                            )
                            if (!loadMoneyBottomSheetVM.remainingLoadLimitAmount.get()
                                    .isNullOrEmpty() && s.toString()
                                    .toInt() > loadMoneyBottomSheetVM.remainingLoadLimitAmount.get()!!.toInt() / 100
                            ) {
                                binding.addMoneyEditext.setText(
                                    (loadMoneyBottomSheetVM.remainingLoadLimitAmount.get()!!
                                        .toInt() / 100).toString()
                                )
                                binding.addMoneyEditext.setSelection(binding.addMoneyEditext.text.length)
                            }

                        } else {
                            binding.btnSendOtp.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.cb_grey))
                            binding.btnSendOtp.setTextColor(
                                ContextCompat.getColor(
                                    requireContext(),
                                    R.color.grey_heading
                                )
                            )

                        }
                    }
                }catch (e: NumberFormatException){
                    loadMoneyBottomSheetVM.remainingLoadLimit.get()?.let {
                        Utility.showToast(loadMoneyBottomSheetVM.remainingLoadLimit.get())
                        FirebaseCrashlytics.getInstance()
                            .setCustomKey("load_amount", it)
                        FirebaseCrashlytics.getInstance().recordException(e)
                        add_money_editext.setText("")
                    }

                }

            }
        })
        loadMoneyBottomSheetVM.setEdittextLength.observe(this) {
            if (it) {
                add_money_editext.setSelection(add_money_editext.text.length)
                loadMoneyBottomSheetVM.setEdittextLength.value = false
            }
        }

        loadMoneyBottomSheetVM.onAddClicked.observe(this) {
            if (it) {
                intentToActivity(AddMoneyUpiDebitView::class.java)
                // callBottomSheet(mViewModel.amountSelected.get()!!, getString(R.string.dummy_amount))
                loadMoneyBottomSheetVM.onAddClicked.value = false
            }
        }
    }

    private fun intentToActivity(aClass: Class<*>) {
        val intent = Intent(requireContext(), aClass)
        intent.putExtra(AppConstants.AMOUNT, loadMoneyBottomSheetVM.amountSelected.get())
        startActivity(intent)
    }


}