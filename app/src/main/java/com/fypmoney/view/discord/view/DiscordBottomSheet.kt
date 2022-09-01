package com.fypmoney.view.discord.view


import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.fypmoney.R
import com.fypmoney.base.BaseBottomSheetFragment
import com.fypmoney.databinding.BottomSheetDicordFailBinding
import com.fypmoney.view.discord.viewmodel.DiscordFailBottomSheetVM


/*
* This is used to show transaction fail in case of add money
* */
class DiscordBottomSheet(
    val finishDiscord: () -> Unit
) : BaseBottomSheetFragment<BottomSheetDicordFailBinding>() {


    private val discordBottomsheetVm by viewModels<DiscordFailBottomSheetVM> {
        defaultViewModelProviderFactory
    }
    override val baseFragmentVM: DiscordFailBottomSheetVM
        get() = discordBottomsheetVm
    override val customTag: String
        get() = DiscordBottomSheet::class.java.simpleName
    override val layoutId: Int
        get() = R.layout.bottom_sheet_dicord_fail


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpBinding()
        setUpListener()

    }

    private fun setUpBinding() {
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
        }
    }

    private fun setUpListener() {
        binding.changeMethod.setOnClickListener {
            dismiss()
            finishDiscord()

        }
    }


}