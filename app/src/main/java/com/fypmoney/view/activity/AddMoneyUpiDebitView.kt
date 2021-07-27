package com.fypmoney.view.activity

import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
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
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseActivity
import com.fypmoney.databinding.ViewAddMoneyUpiDebitBinding
import com.fypmoney.model.AddNewCardDetails
import com.fypmoney.model.SavedCardResponseDetails
import com.fypmoney.model.UpiModel
import com.fypmoney.util.AppConstants
import com.fypmoney.util.SharedPrefUtils
import com.fypmoney.util.Utility
import com.fypmoney.view.fragment.AddNewCardBottomSheet
import com.fypmoney.view.fragment.AddUpiBottomSheet
import com.fypmoney.view.fragment.TransactionFailBottomSheet
import com.fypmoney.viewmodel.AddMoneyUpiDebitViewModel
import com.payu.custombrowser.*
import com.payu.custombrowser.bean.CustomBrowserConfig
import com.payu.custombrowser.bean.CustomBrowserResultData
import com.payu.india.Interfaces.PaymentRelatedDetailsListener
import com.payu.india.Interfaces.ValueAddedServiceApiListener
import com.payu.india.Model.MerchantWebService
import com.payu.india.Model.PayuConfig
import com.payu.india.Model.PayuResponse
import com.payu.india.Payu.PayuConstants
import com.payu.india.PostParams.MerchantWebServicePostParams
import com.payu.india.PostParams.PaymentPostParams
import com.payu.india.Tasks.GetPaymentRelatedDetailsTask
import com.payu.paymentparamhelper.PaymentParams
import com.payu.paymentparamhelper.PostData
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.android.synthetic.main.toolbar.toolbar
import kotlinx.android.synthetic.main.toolbar_for_gateway.*
import kotlinx.android.synthetic.main.view_aadhaar_account_activation.*
import kotlinx.android.synthetic.main.view_add_money_upi_debit.*


/*
* This class is used to handle school name city
* */
open class AddMoneyUpiDebitView :
    BaseActivity<ViewAddMoneyUpiDebitBinding, AddMoneyUpiDebitViewModel>(),
    AddNewCardBottomSheet.OnAddNewCardClickListener,
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
        Log.d("cjv","oncreate")
        setToolbarAndTitle(
            context = this@AddMoneyUpiDebitView,
            toolbar = toolbar,
            isBackArrowVisible = true,
            toolbarTitle = getString(R.string.add_money_screen_title)
        )
        getListOfApps()
        progressBar = ProgressBar(this)
        setObserver()
        mViewModel.amountToAdd1.set(
            getString(R.string.add_money_title1) + getString(R.string.Rs) + intent.getStringExtra(
                AppConstants.AMOUNT
            ) + getString(R.string.add_money_title2)
        )
        mViewModel.amountToAdd.set(intent.getStringExtra(AppConstants.AMOUNT))

        mViewModel.callAddMoneyStep1Api()
        payuConfig = PayuConfig()
        payuConfig.environment = PayuConstants.PRODUCTION_ENV

        val amountLength = intent.getStringExtra(AppConstants.AMOUNT)?.length


        val ss = SpannableString(mViewModel.amountToAdd1.get())
        val clickableSpan: ClickableSpan = object : ClickableSpan() {
            override fun onClick(textView: View) {
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = false
                ds.color = ContextCompat.getColor(applicationContext, R.color.text_color_dark)
            }
        }
        ss.setSpan(clickableSpan, 4, amountLength!! + 6, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        balance_text.text = ss
        balance_text.movementMethod = LinkMovementMethod.getInstance()

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
                else -> {
                    val params = mViewModel.getPaymentParams(type = AppConstants.TYPE_GENERIC)
                    try {
                        callCustomBrowser(
                            com.payu.paymentparamhelper.PayuConstants.UPI_INTENT,
                            params,
                            params.txnId,
                            PayuConstants.PRODUCTION_PAYMENT_URL,
                            isSpecificAppSet = true,
                            it.packageName
                        )
                    } catch (e: java.lang.Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
        mViewModel.onAddNewCardClicked.observe(this) {
            if (it) {
                callAddNewCardBottomSheet()
                mViewModel.onAddNewCardClicked.value = false
            }
        }

        mViewModel.onAddNewCardClicked.observe(this) {
            if (it) {
                callAddNewCardBottomSheet()
                mViewModel.onAddNewCardClicked.value = false
            }
        }
        mViewModel.callGetCardsApi.observe(this) {
            if (it) {
                callPayUApi(
                    command = PayuConstants.PAYMENT_RELATED_DETAILS_FOR_MOBILE_SDK,
                    var1 = mViewModel.merchantKey.get() + ":" + SharedPrefUtils.getLong(
                        applicationContext,
                        SharedPrefUtils.SF_KEY_USER_ID
                    ),
                    hash = mViewModel.getUserCardsHash.get()
                )
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

        mViewModel.onAddInSaveCardClicked.observe(this) {
            callDebitCardPaymentGateway(1, it)

        }
        mViewModel.onBackPress.observe(this) {
        }
    }


    /*
* navigate to the Pay u success
* */
    private fun callPaymentSuccessView() {
        val intent = Intent(this@AddMoneyUpiDebitView, PayUSuccessView::class.java)
        intent.putExtra(AppConstants.RESPONSE, mViewModel.step2ApiResponse)
        intent.putExtra(AppConstants.FROM_WHICH_SCREEN, AppConstants.ADD_MONEY)
        startActivity(intent)
        finishAffinity()
    }

    private fun callGooglePayIntent() {
        try {
            val params = mViewModel.getPaymentParams(type = AppConstants.TYPE_GOOGLE_PAY)
            callCustomBrowser(
                com.payu.paymentparamhelper.PayuConstants.TEZ,
                params,
                params.txnId,
                PayuConstants.PRODUCTION_PAYMENT_URL
            )
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }

    }


    /*
     * This method is used to call leave member
     * */
    private fun callAddNewCardBottomSheet() {
        val bottomSheet =
            AddNewCardBottomSheet(
                mViewModel.amountToAdd.get(),
                merchantKey = mViewModel.merchantKey.get(), this
            )
        bottomSheet.dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.RED))
        bottomSheet.show(supportFragmentManager, "AddNewCard")
    }

    /*
         * This method is used to call transaction fail bottom sheet
         * */
    private fun callTransactionFailBottomSheet() {
        mViewModel.isPaymentFail.set(false)
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
                mViewModel.amountToAdd.get()!!, merchantKey = mViewModel.merchantKey.get(), this
            )
        bottomSheet.dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.RED))
        bottomSheet.show(supportFragmentManager, "AddUPI")
    }

    override fun onAddNewCardButtonClick(addNewCardDetails: AddNewCardDetails) {
        callDebitCardPaymentGateway(0, addNewCardDetails)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PayuConstants.PAYU_REQUEST_CODE) {
            //Lets pass the result back to previous activity
            setResult(resultCode, data)
            //   finish()
        }

    }

    override fun onResume() {
        super.onResume()
        Log.d("cjv","oncreate")
        if (mViewModel.isPaymentFail.get() == true) {
            callTransactionFailBottomSheet()
        }

    }

    override fun onPaymentRelatedDetailsResponse(payuResponse: PayuResponse?) {
        val list = ArrayList<SavedCardResponseDetails>()
        mViewModel.isSavedCardProgressVisible.set(false)
        if (payuResponse?.storedCards?.isNullOrEmpty() == false) {
            payuResponse.storedCards?.forEach()
            {
                list.add(
                    SavedCardResponseDetails(
                        card_no = it.maskedCardNumber,
                        name_on_card = it.nameOnCard,
                        expiry_month = it.expiryMonth,
                        expiry_year = it.expiryYear,
                        is_expired = it.isExpired,
                        isDomestic = it.isDomestic,
                        card_brand = it.cardBrand,
                        card_token = it.cardToken,
                        card_type = it.cardType
                    )
                )
            }
            mViewModel.savedCardsAdapter.setList(list)
        }

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
                val verifyVpaHash = mViewModel.hash.get()
                packageListDialogFragment.verifyVpa(verifyVpaHash)
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
                mViewModel.isPaymentFail.set(true)
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
                webview.webChromeClient = PayUWebChromeClient(payUCustomBrowser)

            }

            override fun onBackApprove() {
                callTransactionFailBottomSheet()
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
                mViewModel.onBackPress.value = true
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
        try {
            val paymentParams =
                mViewModel.getPaymentParams(AppConstants.TYPE_UPI, upiId, isUpiSaved)
            callCustomBrowser(
                com.payu.paymentparamhelper.PayuConstants.UPI,
                paymentParams,
                paymentParams.txnId,
                PayuConstants.PRODUCTION_PAYMENT_URL
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private fun callCustomBrowser(
        mode: String,
        paymentParams: PaymentParams,
        txnId: String,
        url: String, isSpecificAppSet: Boolean? = false, packageName: String? = null
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
                customBrowserConfig.enableSurePay = 0
                customBrowserConfig.setDisableBackButtonDialog(true)
                customBrowserConfig.internetRestoredWindowTTL = CustomBrowserConfig.ENABLE

                customBrowserConfig.setViewPortWideEnable(true)
                customBrowserConfig.progressDialogCustomView =
                    showProgressDialogView()
                customBrowserConfig.toolBarView =
                    showToolBarView()
                customBrowserConfig.disableIntentSeamlessFailure = CustomBrowserConfig.DISABLE
                if (isSpecificAppSet == true) {
                    customBrowserConfig.packageNameForSpecificApp = packageName
                }
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

    /*
    * This is used to list the list of upi apps
    * */
    private fun getListOfApps() {
        val upiList = ArrayList<UpiModel>()
        upiList.add(UpiModel(name = getString(R.string.google_pay)))
        val intent = Intent()
        intent.data = Uri.parse(AppConstants.UPI_APPS_FETCH)
        val resolveInfos = packageManager.queryIntentActivities(
            intent,
            PackageManager.MATCH_DEFAULT_ONLY
        )
        for (resolveInfo in resolveInfos) {
            var packageInfo: PackageInfo
            val upiModel = UpiModel()
            try {
                packageInfo = packageManager.getPackageInfo(resolveInfo.activityInfo.packageName, 0)
                upiModel.name =
                    packageManager.getApplicationLabel(packageInfo.applicationInfo) as String
                upiModel.packageName = resolveInfo.activityInfo.packageName
                upiModel.imageUrl =
                    packageManager.getApplicationIcon(resolveInfo.activityInfo.packageName)
                if (!upiModel.packageName.equals(AppConstants.GPAY_PACKAGE_NAME))
                    upiList.add(upiModel)
            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
            }
        }
        mViewModel.addMoneyUpiAdapter.setList(upiList)

    }

    /*
    * This is used to handle the debit card payment
    * */
    private fun callDebitCardPaymentGateway(
        fromWhichType: Int,
        addNewCardDetails: AddNewCardDetails
    ) {
        try {
            val params = mViewModel.getPaymentParams(
                type = AppConstants.TYPE_DC,
                addNewCardDetails = addNewCardDetails, fromWhichType = fromWhichType
            )
            callCustomBrowser(
                com.payu.paymentparamhelper.PayuConstants.CC,
                params, params.txnId,
                PayuConstants.PRODUCTION_PAYMENT_URL
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
/*
This method is used to call the pay u api
*/

    private fun callPayUApi(hash: String?, command: String?, var1: String?) {
        val merchantWebService = MerchantWebService()
        merchantWebService.key = mViewModel.merchantKey.get()
        merchantWebService.command = command
        merchantWebService.var1 = var1

        merchantWebService.hash = hash
        // dont fetch the data if its been called from payment activity.
        val postData: PostData =
            MerchantWebServicePostParams(merchantWebService).merchantWebServicePostParams

        // ok we got the post params, let make an api call to payu to fetch the payment related details
        payuConfig.data = postData.result

        val paymentRelatedDetailsForMobileSdkTask = GetPaymentRelatedDetailsTask(this)
        paymentRelatedDetailsForMobileSdkTask.execute(payuConfig)

    }

    fun showProgressDialogView(): View {
        return layoutInflater.inflate(R.layout.progress_bar_layout, null)
    }

    private fun showToolBarView(): View {
        val view = layoutInflater.inflate(R.layout.toolbar_for_gateway, null)
        val toolbar = view.findViewById<Toolbar>(R.id.toolbar)
        toolbar.setBackgroundColor(
            ContextCompat.getColor(
                applicationContext,
                R.color.color_dark_green
            )
        )
        return view

    }
}

