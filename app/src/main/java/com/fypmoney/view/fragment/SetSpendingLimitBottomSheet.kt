package com.fypmoney.view.fragment


import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.Toast
import androidx.appcompat.widget.AppCompatTextView
import androidx.databinding.DataBindingUtil
import com.fypmoney.R
import com.fypmoney.application.PockketApplication
import com.fypmoney.databinding.BottomSheetSetSpendingLimitBinding
import com.fypmoney.model.BankProfileResponseDetails
import com.fypmoney.model.CardInfoDetails
import com.fypmoney.model.UpdateCardLimitRequest
import com.fypmoney.util.AppConstants
import com.fypmoney.util.Utility
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.lang.Exception


/*
* This is used to show spending limit details of card
* */
class SetSpendingLimitBottomSheet(
    var bankProfileResponseDetails: BankProfileResponseDetails?,
    var onSetSpendingLimitClickListener: OnSetSpendingLimitClickListener,
    var onBottomSheetDismissListener: ManageChannelsBottomSheet.OnBottomSheetDismissListener
) :
    BottomSheetDialogFragment() {

    override fun getTheme(): Int = R.style.BottomSheetDialogTheme
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        BottomSheetDialog(requireContext(), theme)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(
            R.layout.bottom_sheet_set_spending_limit,
            container,
            false
        )

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews(view)

    }

    private fun setupViews(view: View) {
        val save = view.findViewById<Button>(R.id.button_save)!!
        val cancel = view.findViewById<AppCompatTextView>(R.id.cancel)!!
        val amountEcom = view.findViewById<AppCompatTextView>(R.id.amount_ecom)!!
        val amountPos = view.findViewById<AppCompatTextView>(R.id.amount_pos)!!
        val amountAtm = view.findViewById<AppCompatTextView>(R.id.amount_atm)!!
        val seekbarEcom = view.findViewById<SeekBar>(R.id.seekBar_ecom)!!
        val seekbarPos = view.findViewById<SeekBar>(R.id.seekBar_point_sale)!!
        val seekbarAtm = view.findViewById<SeekBar>(R.id.seekBar_atm)!!
        val offLineStores = view.findViewById<RelativeLayout>(R.id.rl_offline_store)!!
        val rlAtm = view.findViewById<RelativeLayout>(R.id.rl_atm)!!

        //Offline status
        bankProfileResponseDetails?.cardInfos?.forEach {
            when (it.cardType) {
                AppConstants.CARD_TYPE_PHYSICAL -> {
                    if (it.status == AppConstants.ENABLE) {
                        offLineStores.visibility = View.VISIBLE
                    }
                    if (it.atmEnabled == AppConstants.YES) {
                        rlAtm.visibility = View.VISIBLE
                    }
                }
            }
        }


        try {
            amountEcom.text =
                PockketApplication.instance.getString(R.string.Rs) + Utility.convertToRs(
                    bankProfileResponseDetails?.ecomLimit
                )
            amountPos.text =
                PockketApplication.instance.getString(R.string.Rs) + Utility.convertToRs(
                    bankProfileResponseDetails?.posLimit
                )
            amountAtm.text =
                PockketApplication.instance.getString(R.string.Rs) + Utility.convertToRs(
                    bankProfileResponseDetails?.atmLimit
                )
            seekbarEcom.progress =
                Utility.convertToRs(bankProfileResponseDetails?.ecomLimit)!!.toInt()
            seekbarPos.progress =
                Utility.convertToRs(bankProfileResponseDetails?.posLimit)!!.toInt()
            seekbarAtm.progress =
                Utility.convertToRs(bankProfileResponseDetails?.atmLimit)!!.toInt()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        save.setOnClickListener {
            onSetSpendingLimitClickListener.onSetSpendingLimitButtonClick(
                UpdateCardLimitRequest(
                    action = AppConstants.MOD_ONLINE_LIMIT,
                    ecomLimit = Utility.convertToPaise(seekbarEcom.progress.toString()),
                    posLimit = Utility.convertToPaise(seekbarPos.progress.toString()),
                    atmLimit = Utility.convertToPaise(seekbarAtm.progress.toString())
                )
            )
            dismiss()

        }
        cancel.setOnClickListener {
            dismiss()
        }


        seekbarPos.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                amountPos.text = PockketApplication.instance.getString(R.string.Rs) + progress
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {

            }
        })
        seekbarAtm.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                amountAtm.text = PockketApplication.instance.getString(R.string.Rs) + progress
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {

            }
        })

        seekbarEcom.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                amountEcom.text = PockketApplication.instance.getString(R.string.Rs) + progress
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {

            }
        })
    }

    interface OnSetSpendingLimitClickListener {
        fun onSetSpendingLimitButtonClick(updateCardLimitRequest: UpdateCardLimitRequest)
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        onBottomSheetDismissListener.onBottomSheetDismiss()

    }
}