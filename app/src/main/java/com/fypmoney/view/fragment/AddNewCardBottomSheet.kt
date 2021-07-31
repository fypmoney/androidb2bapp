package com.fypmoney.view.fragment


import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.ProgressBar
import androidx.appcompat.widget.AppCompatEditText
import androidx.databinding.DataBindingUtil
import com.fypmoney.R
import com.fypmoney.application.PockketApplication
import com.fypmoney.connectivity.ApiConstant
import com.fypmoney.connectivity.ApiUrl
import com.fypmoney.connectivity.ErrorResponseInfo
import com.fypmoney.connectivity.network.NetworkUtil
import com.fypmoney.connectivity.retrofit.ApiRequest
import com.fypmoney.connectivity.retrofit.WebApiCaller
import com.fypmoney.databinding.BottomSheetAddNewCardBinding
import com.fypmoney.model.*
import com.fypmoney.util.AppConstants
import com.fypmoney.util.Utility
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.payu.india.Payu.PayuConstants
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


/*
* This is used to show new card added
* */
class AddNewCardBottomSheet(
    private val amount: String?,
    private val merchantKey: String?,
    private var onBottomSheetClickListener: OnAddNewCardClickListener
) : BottomSheetDialogFragment(), WebApiCaller.OnWebApiResponse {
    lateinit var cardNumber: AppCompatEditText
    lateinit var cardName: AppCompatEditText
    lateinit var expiry: AppCompatEditText
    lateinit var cvv: AppCompatEditText
    lateinit var saveCardCheckbox: CheckBox
    lateinit var progressBar: ProgressBar
    var expiryList = mutableListOf<String>()
    override fun getTheme(): Int = R.style.BottomSheetDialogTheme
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        BottomSheetDialog(requireContext(), theme)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(
            R.layout.bottom_sheet_add_new_card,
            container,
            false
        )
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val bottomSheet = BottomSheetDialog(requireContext())
        val bindingSheet = DataBindingUtil.inflate<BottomSheetAddNewCardBinding>(
            layoutInflater,
            R.layout.bottom_sheet_add_new_card,
            null,
            false
        )
        bottomSheet.setContentView(bindingSheet.root)

        cardNumber = view.findViewById(R.id.enter_card)!!
        cardName = view.findViewById(R.id.name_card)!!
        expiry = view.findViewById(R.id.expiryValue)!!
        cvv = view.findViewById(R.id.cvv)!!
        saveCardCheckbox = view.findViewById(R.id.saveCardCheckbox)!!
        val btnAdd = view.findViewById<Button>(R.id.btnContinue)!!
        progressBar = view.findViewById(R.id.progress)!!

        btnAdd.text = getString(R.string.add_btn_text) + " " + getString(R.string.Rs) + amount

        expiry.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, start: Int, removed: Int, added: Int) {
                if (start == 1 && start + added == 2 && p0?.contains('/') == false) {
                    expiry.setText("$p0/")
                    expiry.setSelection(expiry.length())
                } else if (start == 3 && start - removed == 2 && p0?.contains('/') == true) {
                    expiry.setText(p0.toString().replace("/", ""))
                    expiry.setSelection(expiry.length())

                }
            }
        })
        btnAdd.setOnClickListener {
            expiryList = expiry.text.toString().split("/") as MutableList<String>
            val df: DateFormat =
                SimpleDateFormat("yy", Locale.getDefault()) // Just the year, with 2 digits
            val formattedDate: String = df.format(Calendar.getInstance().time)
            when {
                cardNumber.length() == 0 -> {
                    Utility.showToast(getString(R.string.card_number_empty_error))

                }
                cardName.length() == 0 -> {
                    Utility.showToast(getString(R.string.card_name_empty_error))

                }
                expiry.length() == 0 -> {
                    Utility.showToast(getString(R.string.card_expiry_empty_error))

                }
                expiry.length() < 5 -> {
                    Utility.showToast(getString(R.string.card_expiry_valid_empty_error))

                }

                expiryList[0] == "00" || expiryList[0] > "12" -> {
                    Utility.showToast(getString(R.string.card_expiry_month_valid_empty_error))

                }

                expiryList[1] < formattedDate -> {
                    Utility.showToast(getString(R.string.card_expiry_year_valid_empty_error))

                }

                cvv.length() < 3 -> {
                    Utility.showToast(getString(R.string.card_cvv_valid_error))

                }
                else -> {
                    progressBar.visibility = View.VISIBLE
                    callGetHashApi(
                        command = PayuConstants.CHECK_IS_DOMESTIC,
                        var1 = cardNumber.text.toString().take(6)
                    )

                }
            }


        }

        return view
    }

    interface OnAddNewCardClickListener {
        fun onAddNewCardButtonClick(addNewCardDetails: AddNewCardDetails)

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
                    command = PayuConstants.CHECK_IS_DOMESTIC,
                    key = merchantKey,
                    var1 = var1,
                    hash = hash
                )
            ), whichServer = AppConstants.PAYU_SERVER
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
                        cardNumber.text.toString().take(6),
                        responseData.getHashResponseDetails.hashData?.get(0)?.hashValue!!
                    )

                }

            }
            ApiConstant.PAYU_PRODUCTION_URL -> {
                if (responseData is CheckIsDomesticResponse) {
                    progressBar.visibility = View.GONE
                    if (responseData.isDomestic == "Y" && responseData.cardCategory == "DC") {
                        onBottomSheetClickListener.onAddNewCardButtonClick(
                            AddNewCardDetails(
                                cardNumber = cardNumber.text.toString(),
                                nameOnCard = cardName.text.toString(),
                                expiryMonth = expiryList[0],
                                expiryYear = "20" + expiryList[1],
                                isCardSaved = saveCardCheckbox.isChecked,
                                cvv = cvv.text.toString()
                            )
                        )
                        dismiss()
                    } else {
                        Utility.showToast(PockketApplication.instance.getString(R.string.card_number_validation_error))
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