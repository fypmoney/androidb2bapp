package com.fypmoney.view

import android.app.Dialog
import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.fypmoney.R
import com.fypmoney.databinding.BottomsheetAddMoneySuccessViewBinding
import com.fypmoney.util.Utility
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.view_login_success.*
import java.util.*
import kotlin.concurrent.schedule

class AddMoneySuccessBottomSheet(var amountAdded:String,
                                 var balanceAmount:String,
                                 val onViewDetailsClick:()->Unit
):BottomSheetDialogFragment() {
    private lateinit var binding:BottomsheetAddMoneySuccessViewBinding
    override fun getTheme(): Int = R.style.BottomSheetDialogTheme
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        BottomSheetDialog(requireContext(), theme)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.bottomsheet_add_money_success_view, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
    }

    private fun setupViews() {
        binding.amountTv.text = """${resources.getString(R.string.Rs)} ${Utility.convertToRs(amountAdded)}"""
        binding.updatedBalanceTv.text = String.format(resources.getString(R.string.udpated_account_balance),Utility.convertToRs(balanceAmount))

        binding.image.gifResource = R.raw.add_money_transaction_successful
        /*Timer().schedule(1000){
            requireActivity().runOnUiThread {
                image.pause()
            }
        }*/
        val mp: MediaPlayer = MediaPlayer.create(
            requireContext(),
            R.raw.tick
        )
        mp.start()

        binding.viewDetailsBtn.setOnClickListener {
            onViewDetailsClick()
        }
    }


}