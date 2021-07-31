package com.fypmoney.view.fragment


import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ProgressBar
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
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
import com.fypmoney.databinding.BottomSheetAddUpiBinding
import com.fypmoney.model.*
import com.fypmoney.util.AppConstants
import com.fypmoney.util.Utility
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.payu.india.Payu.PayuConstants
import kotlinx.android.synthetic.main.bottom_sheet_add_upi.*


/*
* This is used to show new upi added
* */
class AddUpiBottomSheet(
    private val amount: String?,
    private val merchantKey: String?,
    private var onBottomSheetClickListener: OnAddUpiClickListener
) : BottomSheetDialogFragment(), WebApiCaller.OnWebApiResponse {
    lateinit var name: AppCompatTextView
    lateinit var progressBar: ProgressBar
    var isUpiVerified = ObservableField(false)
    var vpaInResponse = ObservableField<String>()
    override fun getTheme(): Int = R.style.BottomSheetDialogTheme
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        BottomSheetDialog(requireContext(), theme)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(
            R.layout.bottom_sheet_add_upi,
            container,
            false
        )
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val bottomSheet = BottomSheetDialog(requireContext())
        val bindingSheet = DataBindingUtil.inflate<BottomSheetAddUpiBinding>(
            layoutInflater,
            R.layout.bottom_sheet_add_upi,
            null,
            false
        )
        bottomSheet.setContentView(bindingSheet.root)

        val upiId = view.findViewById<EditText>(R.id.upiId)!!
        val saveCardCheckbox = view.findViewById<CheckBox>(R.id.saveCardCheckbox)!!
        val btnAdd = view.findViewById<Button>(R.id.btnAdd)!!
        val verify = view.findViewById<AppCompatTextView>(R.id.verifyButton)!!
        name = view.findViewById(R.id.name)!!
        progressBar = view.findViewById(R.id.progress)!!

        btnAdd.text = getString(R.string.add_btn_text) + " " + getString(R.string.Rs) + amount

        verify.setOnClickListener {
            when {
                upiId.length() == 0 -> {
                    Utility.showToast(getString(R.string.add_upi_empty_error))

                }
                upiId.length() > PayuConstants.MAX_VPA_SIZE -> {
                    Utility.showToast(getString(R.string.invalid_upi_error))

                }
                !upiId.text.toString().trim().contains("@") -> {
                    Utility.showToast(getString(R.string.invalid_upi_error))

                }
                else -> {
                    progressBar.visibility = View.VISIBLE
                    callGetHashApi(PayuConstants.VALIDATE_VPA, var1 = upiId.text.toString())
                }
            }
        }

        btnAdd.setOnClickListener {

            when {
                upiId.length() == 0 -> {
                    Utility.showToast(getString(R.string.add_upi_empty_error))

                }
                upiId.length() > PayuConstants.MAX_VPA_SIZE -> {
                    Utility.showToast(getString(R.string.invalid_upi_error))

                }
                !upiId.text.toString().trim().contains("@") -> {
                    Utility.showToast(getString(R.string.invalid_upi_error))

                }
                else -> {
                    progressBar.visibility = View.VISIBLE
                    callGetHashApi(PayuConstants.VALIDATE_VPA, var1 = upiId.text.toString())

                }


            }


        }

        return view
    }

    interface OnAddUpiClickListener {
        fun onAddUpiClickListener(upiId: String, isUpiSaved: Boolean)
    }

    /*
          *This method is used to call payment parameters while receiving the payment
          * */
    private fun callPayUApi(var1: String, hash: String) {
        WebApiCaller.getInstance().request(
            ApiRequest(
                purpose = ApiConstant.PAYU_PRODUCTION_URL,
                endpoint = NetworkUtil.endURL(ApiConstant.PAYU_PRODUCTION_URL),
                request_type = ApiUrl.POST,
                onResponse = this, isProgressBar = false,
                param = PayUServerRequest(
                    command = PayuConstants.VALIDATE_VPA,
                    key = merchantKey,
                    var1 = var1,
                    hash = hash
                )
            ), whichServer = AppConstants.PAYU_SERVER, command = PayuConstants.VALIDATE_VPA
        )
    }

    /*
   *This method is used to call payment parameters while receiving the payment
   * */
    private fun callGetHashApi(command: String, var1: String) {
        WebApiCaller.getInstance().request(
            ApiRequest(
                purpose = ApiConstant.API_GET_HASH,
                endpoint = NetworkUtil.endURL(ApiConstant.API_GET_HASH),
                request_type = ApiUrl.POST,
                onResponse = this, isProgressBar = false,
                param = makeGetHashRequest(command, var1)
            )
        )
    }

    /*
      * This method is used to make request of hash
      * */
    fun makeGetHashRequest(command: String, var1: String): GetHashRequest {
        val getHashRequest = GetHashRequest()
        getHashRequest.merchantKey = merchantKey
        val list = ArrayList<HashData>()
        val hashData = HashData()
        hashData.command = command
        hashData.var1 = var1
        list.add(hashData)
        getHashRequest.hashData = list
        return getHashRequest
    }

    override fun onSuccess(purpose: String, responseData: Any) {

        when (purpose) {
            ApiConstant.API_GET_HASH -> {
                if (responseData is GetHashResponse) {
                    callPayUApi(
                        upiId.text.toString(),
                        responseData.getHashResponseDetails.hashData?.get(0)?.hashValue!!
                    )

                }

            }
            ApiConstant.PAYU_PRODUCTION_URL -> {
                if (responseData is ValidateVpaResponse) {
                    progressBar.visibility = View.GONE
                    when (responseData.isVPAValid) {
                        1 -> {
                            name.text = responseData.payerAccountName
                            onBottomSheetClickListener.onAddUpiClickListener(
                                upiId.text.toString(), saveCardCheckbox.isChecked
                            )
                            dismiss()

                        }


                    else -> {
                        name.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.text_color_red
                            )
                        )
                        name.text =
                            PockketApplication.instance.getString(R.string.invalid_upi_error)
                    }
                }

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

override fun progress(isStart: Boolean, message: String) {

}

}