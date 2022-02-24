package com.fypmoney.view.fragment


import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fypmoney.R
import com.fypmoney.application.PockketApplication
import com.fypmoney.connectivity.ApiConstant
import com.fypmoney.connectivity.ApiUrl
import com.fypmoney.connectivity.ErrorResponseInfo
import com.fypmoney.connectivity.network.NetworkUtil
import com.fypmoney.connectivity.retrofit.ApiRequest
import com.fypmoney.connectivity.retrofit.WebApiCaller
import com.fypmoney.databinding.BottomSheetAddUpiBinding
import com.fypmoney.extension.toGone
import com.fypmoney.extension.toVisible
import com.fypmoney.model.*
import com.fypmoney.util.AppConstants
import com.fypmoney.util.SharedPrefUtils
import com.fypmoney.util.Utility
import com.fypmoney.view.adapter.SavedUpiAdapter
import com.fypmoney.view.adapter.SavedUpiUiModel
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
    private lateinit var binding: BottomSheetAddUpiBinding
    private val savedUpiUiModelList = mutableListOf<SavedUpiUiModel>()

    companion object{
        fun newInstance(){

        }
    }
    override fun getTheme(): Int = R.style.BottomSheetDialogTheme

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        BottomSheetDialog(requireContext(), theme)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater,
            R.layout.bottom_sheet_add_upi,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUp()
    }

    private fun setUpRecyclerView(binding: BottomSheetAddUpiBinding) {

        with(binding.savedRv) {
            adapter = SavedUpiAdapter(
                viewLifecycleOwner, onUpiClicked = {
                    binding.upiId.setText(it.upiId)
                    savedUpiUiModelList.forEach { it1->
                        it1.isSelected = it1.upiId==it.upiId
                    }
                    val newList = mutableListOf<SavedUpiUiModel>()
                    newList.addAll(savedUpiUiModelList)
                    (binding.savedRv.adapter as SavedUpiAdapter).run {
                        submitList(newList)
                        notifyDataSetChanged()
                    }
                }
            )
            addItemDecoration(DividerItemDecoration(requireContext(), RecyclerView.VERTICAL))
            layoutManager =
                LinearLayoutManager(binding.savedRv.context, RecyclerView.VERTICAL, false)
        }
    }

    private fun setUp() {
        setUpRecyclerView(binding)
        binding.btnAdd.text =
            getString(R.string.add_btn_text) + " " + getString(R.string.Rs) + amount

        binding.verifyButton.setOnClickListener {
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
                    binding.progress.visibility = View.VISIBLE
                    callGetHashApi(PayuConstants.VALIDATE_VPA, var1 = upiId.text.toString())
                }
            }
        }

        binding.btnAdd.setOnClickListener {

            when {
                binding.upiId.length() == 0 -> {
                    Utility.showToast(getString(R.string.add_upi_empty_error))

                }
                binding.upiId.length() > PayuConstants.MAX_VPA_SIZE -> {
                    Utility.showToast(getString(R.string.invalid_upi_error))

                }
                !binding.upiId.text.toString().trim().contains("@") -> {
                    Utility.showToast(getString(R.string.invalid_upi_error))

                }
                else -> {
                    binding.progress.visibility = View.VISIBLE
                    callGetHashApi(PayuConstants.VALIDATE_VPA, var1 = upiId.text.toString())

                }


            }


        }
        val savedupiList =
            SharedPrefUtils.getArrayList(binding.savedRv.context, SharedPrefUtils.SF_UPI_LIST)
        savedupiList?.forEach {
            val upiMOdel = SavedUpiUiModel(it)
            savedUpiUiModelList.add(upiMOdel)
        }
        if(savedupiList.isNullOrEmpty()){
            binding.prefferedUpiTv.toGone()
            binding.upiListCl.toGone()
        }else{
            binding.prefferedUpiTv.toVisible()
            binding.upiListCl.toVisible()
            (binding.savedRv.adapter as SavedUpiAdapter).run {
                submitList(savedUpiUiModelList)
            }
        }

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
        if(this@AddUpiBottomSheet.isAdded){
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
                        binding.progress.visibility = View.GONE


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


}

override fun onError(purpose: String, errorResponseInfo: ErrorResponseInfo) {
    if(this@AddUpiBottomSheet.isAdded) {
        Utility.showToast(errorResponseInfo.msg)
        binding.progress.visibility = View.GONE
    }


}


override fun offLine() {

}

override fun progress(isStart: Boolean, message: String) {

}

}