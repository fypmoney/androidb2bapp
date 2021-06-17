package com.fypmoney.view.activity

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.View
import android.webkit.WebView
import android.widget.ProgressBar
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseActivity
import com.fypmoney.databinding.ViewAddMoneyUpiDebitBinding
import com.fypmoney.model.AddNewCardDetails
import com.fypmoney.util.AppConstants
import com.fypmoney.util.PayUGpayResponse
import com.fypmoney.util.SharedPrefUtils
import com.fypmoney.util.Utility
import com.fypmoney.view.fragment.AddNewCardBottomSheet
import com.fypmoney.view.fragment.AddUpiBottomSheet
import com.fypmoney.view.fragment.TransactionFailBottomSheet
import com.fypmoney.viewmodel.AddMoneyUpiDebitViewModel
import com.payu.custombrowser.*
import com.payu.custombrowser.bean.CustomBrowserConfig
import com.payu.custombrowser.bean.CustomBrowserResultData
import com.payu.gpay.GPay
import com.payu.india.Interfaces.PaymentRelatedDetailsListener
import com.payu.india.Interfaces.ValueAddedServiceApiListener
import com.payu.india.Model.PayuConfig
import com.payu.india.Model.PayuResponse
import com.payu.india.Payu.PayuConstants
import com.payu.india.Payu.PayuErrors
import com.payu.india.PostParams.PaymentPostParams
import com.payu.paymentparamhelper.PaymentParams
import com.payu.paymentparamhelper.PostData
import com.payu.phonepe.PhonePe
import com.payu.phonepe.callbacks.PayUPhonePeCallback
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.android.synthetic.main.view_aadhaar_account_activation.*
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import kotlin.experimental.and


/*
* This class is used to handle school name city
* */
class AddMoneyUpiDebitView :
    BaseActivity<ViewAddMoneyUpiDebitBinding, AddMoneyUpiDebitViewModel>(),
    PayUGpayResponse.OnGpayResponseListener, AddNewCardBottomSheet.OnAddNewCardClickListener,
    PaymentRelatedDetailsListener, ValueAddedServiceApiListener,
    TransactionFailBottomSheet.OnBottomSheetClickListener, AddUpiBottomSheet.OnAddUpiClickListener {
    private lateinit var mViewModel: AddMoneyUpiDebitViewModel
    lateinit var progressBar: ProgressBar
    lateinit var payuConfig: PayuConfig
    private var isSamsungPaySupported = false
    private var isPhonePeSupported = false

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.view_add_money_upi_debit
    }

    override fun getViewModel(): AddMoneyUpiDebitViewModel {
        mViewModel = ViewModelProvider(this).get(AddMoneyUpiDebitViewModel::class.java)
        return mViewModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setToolbarAndTitle(
            context = this@AddMoneyUpiDebitView,
            toolbar = toolbar,
            isBackArrowVisible = true,
            toolbarTitle = getString(R.string.add_money_screen_title)
        )
        progressBar = ProgressBar(this)
        setObserver()
        mViewModel.amountToAdd.set(intent.getStringExtra(AppConstants.AMOUNT))
        mViewModel.callAddMoneyStep1Api()
        payuConfig = PayuConfig()


     /*   val ss = SpannableString(getString(R.string.account_verification_sub_title))
        val clickableSpan: ClickableSpan = object : ClickableSpan() {
            override fun onClick(textView: View) {
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = false
                ds.color = ContextCompat.getColor(applicationContext, R.color.text_color_dark)
            }
        }
        ss.setSpan(clickableSpan, 49, 52, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        tvSubTitle.text = ss
        tvSubTitle.movementMethod = LinkMovementMethod.getInstance()*/

    }

    /**
     * Create this method for observe the viewModel fields
     */
    private fun setObserver() {
        mViewModel.onUpiClicked.observe(this) {
            when (mViewModel.clickedPositionForUpi.get()) {
                0 -> {
                    callAddUpiBottomSheet()
                }
                1 -> {
                    callGooglePayIntent()
                }
                2 -> {
                    callPhonePayIntent()
                }
            }
        }
        mViewModel.onAddNewCardClicked.observe(this) {
            if (it) {
                callAddNewCardBottomSheet()
                mViewModel.onAddNewCardClicked.value = false
            }
        }

        mViewModel.onStep2Response.observe(this) {
            when (it) {
                AppConstants.API_SUCCESS -> {
                    callPaymentSuccessView()
                }
                AppConstants.API_FAIL -> {
                    callTransactionFailBottomSheet()
                }
            }

        }

    }


    /*
   * navigate to the Pay u success
   * */
    private fun callPaymentSuccessView() {
        val intent = Intent(this@AddMoneyUpiDebitView, PayUSuccessView::class.java)
        intent.putExtra(AppConstants.PAYU_RESPONSE, mViewModel.step2ApiResponse)
        startActivity(intent)
        finishAffinity()
    }

    private fun callGooglePayIntent() {
        //   val callback = PayUGpayResponse(this)

        callCustomBrowser(
            com.payu.paymentparamhelper.PayuConstants.TEZ,
            mViewModel.getPaymentParams(),
            mViewModel.getPaymentParams().txnId,
            PayuConstants.TEST_PAYMENT_URL
        )
        /*     GPay.getInstance().checkForPaymentAvailability(
                 this@AddMoneyUpiDebitView,
                 callback,
                 mViewModel.hash.get(),
                 mViewModel.merchantKey.get(),
                 mViewModel.merchantKey.get() + ":" + SharedPrefUtils.getLong(
                     application,
                     SharedPrefUtils.SF_KEY_USER_ID
                 )
             )*/
        /*    GPay.getInstance().makePayment(
                this@AddMoneyUpiDebitView,
                mViewModel.requestData.get(),
                callback,
                mViewModel.merchantKey.get(),
                progressBar
            )*/

    }


    override fun onGpayResponseListener(payuResponse: String?) {
        Log.d("gpay_responseeee", payuResponse.toString())
    }

    /*
    * Used to call in case of phone pay
    * */
    private fun callPhonePayIntent() {
        try {

            /*  callCustomBrowser(
                  com.payu.paymentparamhelper.PayuConstants.PHONEPE_INTENT,
                  mViewModel.getPaymentParams(),
                  mViewModel.getPaymentParams().txnId,
                  PayuConstants.PRODUCTION_PAYMENT_URL
              )*/
            PhonePe.getInstance().checkForPaymentAvailability(
                this@AddMoneyUpiDebitView,
                payUPhonePeCallback,
                mViewModel.hash.get(),
                "3TnMpV",
                mViewModel.merchantKey.get() + ":" + SharedPrefUtils.getLong(
                    application,
                    SharedPrefUtils.SF_KEY_USER_ID
                )

            )
            /* PhonePe.getInstance().makePayment(
                 payUPhonePeCallback,
                 this@AddMoneyUpiDebitView,
                 mViewModel.requestData.get(),
                 false,
                 progressBar
             )*/

        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    private var payUPhonePeCallback: PayUPhonePeCallback = object : PayUPhonePeCallback() {
        override fun onPaymentOptionFailure(payuResponse: String, merchantResponse: String) {
            Log.d("phone_pay_payment", "failed")
            //Called when Payment gets failed.
        }

        override fun onPaymentOptionInitialisationSuccess(result: Boolean) {
            super.onPaymentOptionInitialisationSuccess(result)
            Log.d("phone_pay_payment", "initial success")

            // Merchants are advised to show PhonePe option on their UI after this callback is called.
        }

        override fun onPaymentOptionSuccess(payuResponse: String, merchantResponse: String) {
            Log.d("phone_pay_payment", "success")

            //Called when Payment gets successful.
        }

        override fun onPaymentOptionInitialisationFailure(errorCode: Int, description: String) {
            Log.d("phone_pay_payment", errorCode.toString() + "  " + description)

            //Callback thrown in case PhonePe initialisation fails.
        }
    }

    /*
     * This method is used to call leave member
     * */
    private fun callAddNewCardBottomSheet() {
        val bottomSheet =
            AddNewCardBottomSheet(
                mViewModel.amountToAdd.get(),
                this
            )
        bottomSheet.dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.RED))
        bottomSheet.show(supportFragmentManager, "AddNewCard")
    }

    /*
         * This method is used to call transaction fail bottom sheet
         * */
    private fun callTransactionFailBottomSheet() {
        val bottomSheet =
            TransactionFailBottomSheet(
                "",
                mViewModel.amountToAdd.get()!!, this
            )
        bottomSheet.dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.RED))
        bottomSheet.show(supportFragmentManager, "TransactionFail")
    }

    /*
      * This method is used to call add upi bottom sheet
      * */
    private fun callAddUpiBottomSheet() {
        val bottomSheet =
            AddUpiBottomSheet(
                mViewModel.amountToAdd.get()!!, this
            )
        bottomSheet.dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.RED))
        bottomSheet.show(supportFragmentManager, "AddUPI")
    }

    override fun onAddNewCardButtonClick(addNewCardDetails: AddNewCardDetails) {
        try {
            callCustomBrowser(
                com.payu.paymentparamhelper.PayuConstants.CC,
                mViewModel.getPaymentParams(addNewCardDetails),
                mViewModel.getPaymentParams(addNewCardDetails).txnId,
                PayuConstants.PRODUCTION_PAYMENT_URL
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PayuConstants.PAYU_REQUEST_CODE) {
            //Lets pass the result back to previous activity
            setResult(resultCode, data)
            //   finish()
        }

    }

    override fun onPaymentRelatedDetailsResponse(payuResponse: PayuResponse?) {
    }

    override fun onValueAddedServiceApiResponse(p0: PayuResponse?) {
    }

    //set callback to track important events

    //set callback to track important events
    private var payUCustomBrowserCallback: PayUCustomBrowserCallback =
        object : PayUCustomBrowserCallback() {
            override fun onVpaEntered(
                vpa: String,
                packageListDialogFragment: PackageListDialogFragment
            ) {


                //This hash should be generated from server
                //    val input = "smsplus|validateVPA|$vpa|1b1b0"
                val input =
                    mViewModel.merchantKey.get() + "|" + "validateVPA" + "|" + mViewModel.upiEntered.get() + "|" + "g0nGFe03"
                Log.d("kcj0souv", input)
                val verifyVpaHash = calculateHash(input).result

                packageListDialogFragment.verifyVpa(verifyVpaHash)
            }

            private fun calculateHash(hashString: String): PostData {
                return try {
                    val hash = StringBuilder()
                    val messageDigest = MessageDigest.getInstance("SHA-512")
                    messageDigest.update(hashString.toByteArray())
                    val mdbytes = messageDigest.digest()
                    for (hashByte in mdbytes) {
                        hash.append(
                            Integer.toString((hashByte and 0xff.toByte()) + 0x100, 16).substring(1)
                        )
                    }
                    getReturnData(PayuErrors.NO_ERROR, PayuConstants.SUCCESS, hash.toString())
                } catch (e: NoSuchAlgorithmException) {
                    getReturnData(
                        PayuErrors.NO_SUCH_ALGORITHM_EXCEPTION,
                        PayuErrors.INVALID_ALGORITHM_SHA
                    )
                }
            }

            protected fun getReturnData(code: Int, status: String?, result: String?): PostData {
                val postData = PostData()
                postData.code = code
                postData.status = status
                postData.result = result
                return postData
            }

            protected fun getReturnData(code: Int, result: String?): PostData {
                return getReturnData(code, PayuConstants.ERROR, result)
            }

            /**
             * This method will be called after a failed transaction.
             *
             * @param payuResponse     response sent by PayU in App
             * @param merchantResponse response received from Furl
             */
            override fun onPaymentFailure(payuResponse: String, merchantResponse: String) {
                /*   val intent = Intent()
                   intent.putExtra("result", merchantResponse)
                   intent.putExtra("payu_response", payuResponse)
                   if (null != mViewModel.hash.get()) {
                       intent.putExtra(PayuConstants.MERCHANT_HASH, mViewModel.hash)
                   }
                   setResult(RESULT_CANCELED, intent)*/
                finish()
            }

            override fun onPaymentTerminate() {

            }

            /**
             * This method will be called after a successful transaction.
             *
             * @param payuResponse     response sent by PayU in App
             * @param merchantResponse response received from Furl
             */
            override fun onPaymentSuccess(payuResponse: String, merchantResponse: String) {
                mViewModel.payUResponse.set(payuResponse)
                mViewModel.callAddMoneyStep2Api()
            }

            override fun onCBErrorReceived(code: Int, errormsg: String) {
                Utility.showToast(code.toString() + errormsg)
            }

            override fun setCBProperties(webview: WebView, payUCustomBrowser: Bank) {
                Utility.showToast("setCBProperties")
                webview.webChromeClient = PayUWebChromeClient(payUCustomBrowser)

            }

            override fun onBackApprove() {
                this@AddMoneyUpiDebitView.finish()
            }

            override fun onBackDismiss() {
                super.onBackDismiss()
            }

            /**
             * This callback method will be invoked when setDisableBackButtonDialog is set to true.
             *
             * @param alertDialogBuilder a reference of AlertDialog.Builder to customize the dialog
             */
            override fun onBackButton(alertDialogBuilder: AlertDialog.Builder) {
                super.onBackButton(alertDialogBuilder)
            }

            override fun isPaymentOptionAvailable(resultData: CustomBrowserResultData) {
                when (resultData.paymentOption) {
                    com.payu.custombrowser.util.PaymentOption.SAMSUNGPAY -> isSamsungPaySupported =
                        resultData.isPaymentOptionAvailable
                    com.payu.custombrowser.util.PaymentOption.PHONEPE -> isPhonePeSupported =
                        resultData.isPaymentOptionAvailable
                }
            }
        }

    override fun onBottomSheetButtonClick(type: String) {

    }

    override fun onAddUpiClickListener(upiId: String, isUpiSaved: Boolean) {
        mViewModel.upiEntered.set(upiId)
        callCustomBrowser(
            com.payu.paymentparamhelper.PayuConstants.UPI,
            mViewModel.getPaymentParamsForUpi(upiId, isUpiSaved),
            mViewModel.getPaymentParamsForUpi(upiId, isUpiSaved).txnId,
            PayuConstants.PRODUCTION_PAYMENT_URL
        )
    }


    private fun callCustomBrowser(
        mode: String,
        paymentParams: PaymentParams,
        txnId: String,
        url: String
    ) {
        try {
            val postData = PaymentPostParams(
                paymentParams,
                mode
            ).paymentPostParams

            if (postData != null) {
                payuConfig.data = postData.result
                val customBrowserConfig = CustomBrowserConfig(
                    mViewModel.merchantKey.get()!!,
                    txnId
                )
                customBrowserConfig.setAutoApprove(false) // set true to automatically approve the OTP

                customBrowserConfig.setAutoSelectOTP(true) // set true to automatically select the OTP flow
                customBrowserConfig.setDisableBackButtonDialog(false)
                customBrowserConfig.setMerchantSMSPermission(true)
                customBrowserConfig.enableSurePay = 3
                customBrowserConfig.merchantCheckoutActivityPath =
                    "com.payu.testapp.MerchantCheckoutActivity"

                customBrowserConfig.postURL = url
                if (payuConfig != null) customBrowserConfig.payuPostData = payuConfig.data

                CustomBrowser().addCustomBrowser(
                    this@AddMoneyUpiDebitView,
                    customBrowserConfig,
                    payUCustomBrowserCallback
                )


            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


}
