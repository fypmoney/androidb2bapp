package com.fypmoney.view.recharge.fragments

import android.os.Bundle
import android.view.View
import androidx.annotation.DrawableRes
import androidx.fragment.app.viewModels
import com.fypmoney.R
import com.fypmoney.base.BaseBottomSheetFragment
import com.fypmoney.base.BaseViewModel
import com.fypmoney.databinding.BottomsheetDthKonwMoreBinding
import com.fypmoney.view.recharge.viewmodel.DthKnowMoreBottomSheetVM

class DthKnowMoreBottomSheet(val title:String,val subTitle:String,@DrawableRes val res:Int) : BaseBottomSheetFragment<BottomsheetDthKonwMoreBinding>() {
    private val dthKnowMoreBottomSheetVM by viewModels<DthKnowMoreBottomSheetVM> {
        defaultViewModelProviderFactory
    }
    override val baseFragmentVM: BaseViewModel
    get() = dthKnowMoreBottomSheetVM
    override val customTag: String
    get() = DthKnowMoreBottomSheet::class.java.simpleName
    override val layoutId: Int
    get() = R.layout.bottomsheet_dth_konw_more

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpBinding()
        setupObserver()
    }

    private fun setUpBinding(){
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = dthKnowMoreBottomSheetVM
        }
        binding.titleTv.text = title
        binding.subTitleTv.text = subTitle
        binding.operatorIv.setImageResource(res)
    }
    private fun setupObserver() {

    }
}