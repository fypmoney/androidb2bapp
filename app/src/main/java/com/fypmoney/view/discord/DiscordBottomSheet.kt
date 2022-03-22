package com.fypmoney.view.discord


import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import com.fypmoney.R
import com.fypmoney.base.BaseBottomSheetFragment
import com.fypmoney.base.BaseViewModel
import com.fypmoney.databinding.BottomSheetDicordFailBinding
import com.fypmoney.databinding.BottomsheetUpiComingSoonBinding
import com.fypmoney.view.discord.viewmodel.DiscordFailBottomSheetVM
import com.fypmoney.view.home.main.home.view.UpiComingSoonBottomSheet
import com.fypmoney.view.home.main.home.viewmodel.UpiComingSoonVM
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.bottom_sheet_dicord_fail.*


/*
* This is used to show transaction fail in case of add money
* */
class DiscordBottomSheet(val finishDiscord:()->Unit
) : BaseBottomSheetFragment<BottomSheetDicordFailBinding>(){




    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpBinding()

    }
    private fun setUpBinding(){
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            changeMethod.setOnClickListener {
                dismiss()
                finishDiscord()

            }
        }
    }
    private val discordBottomsheetVm by viewModels<DiscordFailBottomSheetVM> {
        defaultViewModelProviderFactory
    }
    override val baseFragmentVM: DiscordFailBottomSheetVM
        get() = discordBottomsheetVm
    override val customTag: String
        get() = DiscordBottomSheet::class.java.simpleName
    override val layoutId: Int
        get() = R.layout.bottom_sheet_dicord_fail


}