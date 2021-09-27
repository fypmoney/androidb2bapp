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
    var onBottomSheetDismissListener: ManageChannelsBottomSheet.OnBottomSheetDismissListener
) :
    BottomSheetDialogFragment(),
    DialogUtils.OnAlertDialogClickListener, WebApiCaller.OnWebApiResponse {
    lateinit var binding: BottomSheetBlockUnblockCardBinding
    var cardType = ObservableField<Int>()
    var action = ObservableField<String>()
    override fun getTheme(): Int = R.style.BottomSheetDialogTheme
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        BottomSheetDialog(requireContext(), theme)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.bottom_sheet_block_unblock_card,
            container,
            false
        )
        setUpViews()

        return binding.root
    }

    fun setUpViews() {
        cardInfo?.forEach {
            when (it.cardType) {
                AppConstants.CARD_TYPE_VIRTUAL -> {
                    if (it.isCardBlocked == AppConstants.YES) {
                        binding.virtualCardSwitch.isChecked = false

                    } else if (it.isCardBlocked == AppConstants.NO) {
                        binding.virtualCardSwitch.isChecked = true

                    }
                }
                AppConstants.CARD_TYPE_PHYSICAL -> {
                    if(it.status=="INACTIVE"){
                        binding.physicalCardSwitch.visibility = View.GONE
                        binding.physicalCardTv.visibility = View.GONE
                    }else{
                        binding.physicalCardSwitch.visibility = View.VISIBLE
                        binding.physicalCardTv.visibility = View.VISIBLE
                    }
                    if (it.isCardBlocked == AppConstants.YES) {
                        binding.physicalCardSwitch.isChecked = false

                    } else if (it.isCardBlocked == AppConstants.NO) {
                        binding.physicalCardSwitch.isChecked = true

                    }
                }

            }
        }


        binding.virtualCardSwitch.setOnClickListener {
            cardType.set(AppConstants.CARD_TYPE_VIRTUAL_CARD)
            val message: String = if (binding.virtualCardSwitch.isChecked) {
                binding.virtualCardSwitch.isChecked = !binding.virtualCardSwitch.isChecked
                action.set(AppConstants.UNBLOCK_CARD_ACTION)
                PockketApplication.instance.getString(R.string.card_block_confirm) + PockketApplication.instance.getString(
                    R.string.unblock_card_text
                )

            } else {
                binding.virtualCardSwitch.isChecked = !binding.virtualCardSwitch.isChecked
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

        binding.physicalCardSwitch.setOnClickListener {
            cardType.set(AppConstants.CARD_TYPE_PHYSICAL_CARD)
            val message: String = if (binding.physicalCardSwitch.isChecked) {
                binding.physicalCardSwitch.isChecked = !binding.physicalCardSwitch.isChecked
                action.set(AppConstants.UNBLOCK_CARD_ACTION)
                PockketApplication.instance.getString(R.string.card_block_confirm) + PockketApplication.instance.getString(
                    R.string.unblock_card_text
                )

            } else {
                binding.physicalCardSwitch.isChecked = !binding.physicalCardSwitch.isChecked
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
    }

    override fun onPositiveButtonClick(uniqueIdentifier: String) {
        binding.progress.visibility = View.VISIBLE
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
                binding.progress.visibility = View.GONE
                if (responseData is UpdateCardSettingsResponse) {
                    setCardToggle(responseData.updateCardSettingsResponseDetails)
                }

            }
        }
    }

    override fun onError(purpose: String, errorResponseInfo: ErrorResponseInfo) {
        Utility.showToast(errorResponseInfo.msg)
        binding.progress.visibility = View.GONE

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
                        binding.virtualCardSwitch.isChecked = false
                    }
                    AppConstants.NO -> {
                        binding.virtualCardSwitch.isChecked = true
                    }
                }
            }
            AppConstants.CARD_TYPE_PHYSICAL -> {
                when (updateCardSettingsResponseDetails.isCardBlocked) {
                    AppConstants.YES -> {
                        binding.physicalCardSwitch.isChecked = false
                    }
                    AppConstants.NO -> {
                        binding.physicalCardSwitch.isChecked = true
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