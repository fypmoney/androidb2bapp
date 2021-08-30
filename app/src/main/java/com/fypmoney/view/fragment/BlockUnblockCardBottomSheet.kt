package com.fypmoney.view.fragment


import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.appcompat.widget.SwitchCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableField
import com.fypmoney.R
import com.fypmoney.application.PockketApplication
import com.fypmoney.connectivity.ApiConstant
import com.fypmoney.connectivity.ApiUrl
import com.fypmoney.connectivity.ErrorResponseInfo
import com.fypmoney.connectivity.network.NetworkUtil
import com.fypmoney.connectivity.retrofit.ApiRequest
import com.fypmoney.connectivity.retrofit.WebApiCaller
import com.fypmoney.databinding.BottomSheetBlockUnblockCardBinding
import com.fypmoney.model.CardInfoDetails
import com.fypmoney.model.UpDateCardSettingsRequest
import com.fypmoney.model.UpdateCardSettingsResponse
import com.fypmoney.model.UpdateCardSettingsResponseDetails
import com.fypmoney.util.AppConstants
import com.fypmoney.util.DialogUtils
import com.fypmoney.util.Utility
import com.fypmoney.view.CardSettingClickListener
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


/*
* This is used to block or unblock card
* */
class BlockUnblockCardBottomSheet(
    var cardInfo: List<CardInfoDetails>?,
    var cardSettingClickListener: CardSettingClickListener,
    var onBottomSheetDismissListener: ManageChannelsBottomSheet.OnBottomSheetDismissListener
) :
    BottomSheetDialogFragment(),
    DialogUtils.OnAlertDialogClickListener, WebApiCaller.OnWebApiResponse {
    var cardType = ObservableField<Int>()
    var action = ObservableField<String>()
    lateinit var pSwitch: SwitchCompat
    lateinit var vSwitch: SwitchCompat
    lateinit var progressBar: ProgressBar
    override fun getTheme(): Int = R.style.BottomSheetDialogTheme
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        BottomSheetDialog(requireContext(), theme)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(
            R.layout.bottom_sheet_block_unblock_card,
            container,
            false
        )
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val bottomSheet = BottomSheetDialog(requireContext())
        val bindingSheet = DataBindingUtil.inflate<BottomSheetBlockUnblockCardBinding>(
            layoutInflater,
            R.layout.bottom_sheet_block_unblock_card,
            null,
            false
        )
        bottomSheet.setContentView(bindingSheet.root)

        vSwitch = view.findViewById(R.id.virtual_card_switch)!!
        progressBar = view.findViewById(R.id.progress)!!


        cardInfo?.forEach {
            when (it.cardType) {
                AppConstants.CARD_TYPE_VIRTUAL -> {
                    if (it.isCardBlocked == AppConstants.YES) {
                        vSwitch.isChecked = false

                    } else if (it.isCardBlocked == AppConstants.NO) {
                        vSwitch.isChecked = true

                    }
                }

            }
        }


        vSwitch.setOnClickListener {
            cardType.set(AppConstants.CARD_TYPE_VIRTUAL_CARD)
            val message: String = if (vSwitch.isChecked) {
                vSwitch.isChecked = !vSwitch.isChecked
                action.set(AppConstants.UNBLOCK_CARD_ACTION)
                PockketApplication.instance.getString(R.string.card_block_confirm) + PockketApplication.instance.getString(
                    R.string.unblock_card_text
                )

            } else {
                vSwitch.isChecked = !vSwitch.isChecked
                action.set(AppConstants.BLOCK_CARD_ACTION)
                PockketApplication.instance.getString(R.string.card_block_confirm) + PockketApplication.instance.getString(
                    R.string.block_card_text
                )


            }
            DialogUtils.showConfirmationDialog(
                context = requireContext(),
                title = "",
                message = message,
                positiveButtonText = getString(R.string.yes_btn_text),
                negativeButtonText = getString(R.string.no_btn_text),
                uniqueIdentifier = "",
                onAlertDialogClickListener = this, isNegativeButtonRequired = true
            )

        }

        return view
    }

    override fun onPositiveButtonClick(uniqueIdentifier: String) {
        progressBar.visibility = View.VISIBLE
        callCardSettingsUpdateApi(
            UpDateCardSettingsRequest(
                action = action.get(),
                cardType = cardType.get()
            )
        )
    }

    /*
          * This method is used to call update card settings
          * */
    private fun callCardSettingsUpdateApi(upDateCardSettingsRequest: UpDateCardSettingsRequest) {
        WebApiCaller.getInstance().request(
            ApiRequest(
                ApiConstant.API_UPDATE_CARD_SETTINGS,
                NetworkUtil.endURL(ApiConstant.API_UPDATE_CARD_SETTINGS),
                ApiUrl.PUT,
                upDateCardSettingsRequest,
                this, isProgressBar = true
            )
        )
    }

    override fun progress(isStart: Boolean, message: String) {


    }

    override fun onSuccess(purpose: String, responseData: Any) {
        when (purpose) {
            ApiConstant.API_UPDATE_CARD_SETTINGS -> {
                progressBar.visibility = View.GONE
                if (responseData is UpdateCardSettingsResponse) {
                    setCardToggle(responseData.updateCardSettingsResponseDetails)
                }

            }
        }
    }

    override fun onError(purpose: String, errorResponseInfo: ErrorResponseInfo) {
        Utility.showToast(errorResponseInfo.msg)
        progressBar.visibility = View.GONE

    }

    override fun offLine() {
    }

    /*
    * This method is used to set the card toggle
    * */
    fun setCardToggle(updateCardSettingsResponseDetails: UpdateCardSettingsResponseDetails?) {
        when (updateCardSettingsResponseDetails?.cardType) {

            AppConstants.CARD_TYPE_VIRTUAL -> {
                when (updateCardSettingsResponseDetails.isCardBlocked) {
                    AppConstants.YES -> {
                        vSwitch.isChecked = false
                    }
                    AppConstants.NO -> {
                        vSwitch.isChecked = true
                    }
                }
            }
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        onBottomSheetDismissListener.onBottomSheetDismiss()

    }

}