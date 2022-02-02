package com.fypmoney.view.fragment


import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.databinding.ObservableField
import com.fypmoney.R
import com.fypmoney.application.PockketApplication
import com.fypmoney.connectivity.ApiConstant
import com.fypmoney.connectivity.ApiUrl
import com.fypmoney.connectivity.ErrorResponseInfo
import com.fypmoney.connectivity.network.NetworkUtil
import com.fypmoney.connectivity.retrofit.ApiRequest
import com.fypmoney.connectivity.retrofit.WebApiCaller
import com.fypmoney.extension.toGone
import com.fypmoney.model.*
import com.fypmoney.util.AppConstants
import com.fypmoney.util.DialogUtils
import com.fypmoney.util.Utility
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

/*
* This is used to manage channels
* */
class ManageChannelsBottomSheet(var cardInfo: List<CardInfoDetails>?,
                                var onBottomSheetDismissListener:OnBottomSheetDismissListener,
                                var bankProfileResponseDetails: BankProfileResponseDetails? = null
) : BottomSheetDialogFragment(),
    DialogUtils.OnAlertDialogClickListener, WebApiCaller.OnWebApiResponse{
    var cardType = ObservableField<Int>()
    var channelType = ObservableField<String>()
    var isEnable = ObservableField<Int>()
    var isApiHit = ObservableField<Boolean>(true)
    lateinit var pPointSaleSwitch: SwitchCompat
    lateinit var pointSale: TextView
    lateinit var atmTxt: TextView
    lateinit var vEcomSwitch: SwitchCompat
    lateinit var vAtmSwitch: SwitchCompat
    lateinit var progressBar: ProgressBar
    var whichSwitch = ObservableField<String>()
    override fun getTheme(): Int = R.style.BottomSheetDialogTheme
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        BottomSheetDialog(requireContext(), theme)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(
            R.layout.bottom_sheet_manage_channels,
            container,
            false
        )

        pPointSaleSwitch = view.findViewById(R.id.point_sale_switch)!!
        vEcomSwitch = view.findViewById(R.id.ecom_virtual_switch)!!
        vAtmSwitch = view.findViewById(R.id.atm_channel_switch)!!
        pointSale = view.findViewById(R.id.point_sale)!!
        atmTxt = view.findViewById(R.id.atm_channel_tv)!!
        progressBar = view.findViewById(R.id.progress)!!


        cardInfo?.forEach {
            when (it.cardType) {
                AppConstants.CARD_TYPE_VIRTUAL -> {
                    if (it.ecomEnabled == AppConstants.YES) {
                        vEcomSwitch.isChecked = true
                    } else if (it.ecomEnabled == AppConstants.NO) {
                        vEcomSwitch.isChecked = false
                    }
                    //hideOfflineStore()

                }
                AppConstants.CARD_TYPE_PHYSICAL -> {
                    if(it.status == AppConstants.ENABLE){
                        showOfflineStore()

                    }else{
                        hideOfflineStore()
                    }
                    if (it.posEnabled == AppConstants.YES) {
                        pPointSaleSwitch.isChecked = true
                    }
                     if (it.posEnabled == AppConstants.NO) {
                        pPointSaleSwitch.isChecked = false
                    }
                     if (it.atmEnabled == AppConstants.YES) {
                        vAtmSwitch.isChecked = true
                    }
                     if (it.atmEnabled == AppConstants.NO) {
                        vAtmSwitch.isChecked = false
                    }



                }
            }

        }

        bankProfileResponseDetails?.kycType?.let {
            if(it != "MINIMUM"){
                atmTxt.toGone()
                vAtmSwitch.toGone()
            }
        }

        pPointSaleSwitch.setOnClickListener {
            channelType.set(AppConstants.Channel_POS)
            cardType.set(AppConstants.CARD_TYPE_PHYSICAL_CARD)
            val message: String = if (pPointSaleSwitch.isChecked) {
                pPointSaleSwitch.isChecked = !pPointSaleSwitch.isChecked
                isEnable.set(AppConstants.ON)
                PockketApplication.instance.getString(R.string.card_block_confirm) + PockketApplication.instance.getString(
                    R.string.enable_card_text
                )
            } else {
                pPointSaleSwitch.isChecked = !pPointSaleSwitch.isChecked
                isEnable.set(AppConstants.OFF)
                PockketApplication.instance.getString(R.string.card_block_confirm) + PockketApplication.instance.getString(
                    R.string.disable_card_text
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
        vAtmSwitch.setOnClickListener {
            channelType.set(AppConstants.Channel_ATM)
            cardType.set(AppConstants.CARD_TYPE_PHYSICAL_CARD)
            val message: String = if (vAtmSwitch.isChecked) {
                vAtmSwitch.isChecked = !vAtmSwitch.isChecked
                isEnable.set(AppConstants.ON)
                PockketApplication.instance.getString(R.string.card_block_confirm) + PockketApplication.instance.getString(
                    R.string.enable_card_text
                )
            } else {
                vAtmSwitch.isChecked = !vAtmSwitch.isChecked
                isEnable.set(AppConstants.OFF)
                PockketApplication.instance.getString(R.string.card_block_confirm) + PockketApplication.instance.getString(
                    R.string.disable_card_text
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
        vEcomSwitch.setOnClickListener {
            channelType.set(AppConstants.Channel_ECOM)
            cardType.set(AppConstants.CARD_TYPE_VIRTUAL_CARD)
            val message: String = if (vEcomSwitch.isChecked) {
                vEcomSwitch.isChecked = !vEcomSwitch.isChecked
                isEnable.set(AppConstants.ON)
                PockketApplication.instance.getString(R.string.card_block_confirm) + PockketApplication.instance.getString(
                    R.string.enable_card_text
                )
            } else {
                vEcomSwitch.isChecked = !vEcomSwitch.isChecked
                isEnable.set(AppConstants.OFF)
                PockketApplication.instance.getString(R.string.card_block_confirm) + PockketApplication.instance.getString(
                    R.string.disable_card_text
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

    private fun showOfflineStore() {
        pointSale.visibility = View.VISIBLE
        pPointSaleSwitch.visibility = View.VISIBLE
        atmTxt.visibility = View.VISIBLE
        vAtmSwitch.visibility = View.VISIBLE    }

    private fun hideOfflineStore() {
        pointSale.visibility = View.GONE
        pPointSaleSwitch.visibility = View.GONE
        atmTxt.visibility = View.GONE
        vAtmSwitch.visibility = View.GONE
    }

    override fun onPositiveButtonClick(uniqueIdentifier: String) {
        progressBar.visibility = View.VISIBLE
        callCardSettingsUpdateApi(
            UpDateCardSettingsRequest(
                cardType = cardType.get(),
                isEnable = isEnable.get(),
                channelType = channelType.get(),
                action = AppConstants.ENABLE_CHANNEL
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
                    isApiHit.set(false)
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
            AppConstants.CARD_TYPE_PHYSICAL -> {
                when (channelType.get()) {
                    AppConstants.Channel_POS -> {
                        if (updateCardSettingsResponseDetails.posEnabled == AppConstants.YES)
                            pPointSaleSwitch.isChecked = true
                        else if (updateCardSettingsResponseDetails.posEnabled == AppConstants.NO)
                            pPointSaleSwitch.isChecked = false

                    }
                    AppConstants.Channel_ATM -> {
                        if (updateCardSettingsResponseDetails.atmEnabled == AppConstants.YES)
                            vAtmSwitch.isChecked = true
                        else if (updateCardSettingsResponseDetails.atmEnabled == AppConstants.NO)
                            vAtmSwitch.isChecked = false

                    }
                }

            }
            AppConstants.CARD_TYPE_VIRTUAL -> {
                when (channelType.get()) {
                    AppConstants.Channel_ECOM -> {
                        if (updateCardSettingsResponseDetails.ecomEnabled == AppConstants.YES)
                            vEcomSwitch.isChecked = true
                        else if (updateCardSettingsResponseDetails.ecomEnabled == AppConstants.NO)
                            vEcomSwitch.isChecked = false
                    }


                }
            }
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        onBottomSheetDismissListener.onBottomSheetDismiss()

    }


    interface OnBottomSheetDismissListener {
        fun onBottomSheetDismiss()
    }
}