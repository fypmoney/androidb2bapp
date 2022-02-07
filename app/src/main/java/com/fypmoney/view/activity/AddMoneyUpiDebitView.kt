package com.fypmoney.view.activity

import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.webkit.WebView
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.fyp.trackr.models.TrackrEvent
import com.fyp.trackr.models.TrackrField
import com.fyp.trackr.models.trackr
import com.fyp.trackr.services.TrackrServices
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.application.PockketApplication
import com.fypmoney.base.BaseActivity
import com.fypmoney.databinding.ViewAddMoneyUpiDebitBinding
import com.fypmoney.model.AddNewCardDetails
import com.fypmoney.model.UpiModel
import com.fypmoney.util.AppConstants
import com.fypmoney.util.SharedPrefUtils
import com.fypmoney.util.Utility
import com.fypmoney.view.AddMoneySuccessBottomSheet
import com.fypmoney.view.fragment.AddNewCardBottomSheet
import com.fypmoney.view.fragment.AddUpiBottomSheet
import com.fypmoney.view.fragment.TransactionFailBottomSheet
import com.fypmoney.view.home.main.homescreen.view.HomeActivity
import com.fypmoney.viewmodel.AddMoneyUpiDebitViewModel
import com.payu.custombrowser.*
import com.payu.custombrowser.bean.CustomBrowserConfig
import com.payu.custombrowser.bean.CustomBrowserResultData
import com.payu.india.Model.PayuConfig
import com.payu.india.Payu.PayuConstants
import com.payu.india.PostParams.PaymentPostParams
import com.payu.paymentparamhelper.PaymentParams
import kotlinx.android.synthetic.main.bottom_sheet_add_upi.*
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.android.synthetic.main.toolbar.toolbar
import kotlinx.android.synthetic.main.toolbar_for_gateway.*
import kotlinx.android.synthetic.main.view_aadhaar_account_activation.*
import kotlinx.android.synthetic.main.view_add_money_upi_debit.*


class AddMoneyUpiDebitView :
    BaseActivity<ViewAddMoneyUpiDebitBinding, AddMoneyUpiDebitViewModel>(),
    AddNewCardBottomSheet.OnAddNewCardClickListener,
    TransactionFailBottomSheet.OnBottomSheetClickListener, AddUpiBottomSheet.OnAddUpiClickListener {
    private lateinit var mViewModel: AddMoneyUpiDebitViewModel
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
        getListOfApps()
        setObserver()
        mViewModel.amountToAdd1.set(
            getString(R.string.add_money_title1) + getString(R.string.Rs) + intent.getStringExtra(
                AppConstants.AMOUNT
            ) + getString(R.string.add_money_title2)
        )
        mViewModel.amountToAdd.set(intent.getStringExtra(AppConstants.AMOUNT))

        payuConfig = PayuConfig()
        payuConfig.environment = PayuConstants.PRODUCTION_ENV


    }

    /**
     * Create this method for observe the viewModel fields
     */
    private fun setObserver() {
        mViewModel.event.observe(this,{
            when(it){
                AddMoneyUpiDebitViewModel.AddMoneyEvent.OnADDNewCardClickedEvent -> {
                    callAddNewCardBottomSheet()
                }
                AddMoneyUpiDebitViewModel.AddMoneyEvent.OnUPIClickedEvent -> {
                    callUpiIntent()
                }
            }
        })




        mViewModel.onStep2Response.observe(this) {
            when (it) {
                AppConstants.API_SUCCESS -> {
                    trackr {
                        it.services = arrayListOf(
                            TrackrServices.FIREBASE,
                            TrackrServices.MOENGAGE,
                            TrackrServices.FB,TrackrServices.ADJUST)
                        it.name = TrackrEvent.load_money_success
                    }
                    callTransactionSuccessBottomSheet()
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
    private fun callViewPaymentDetails() {
        val intent = Intent(this@AddMoneyUpiDebitView, PayUSuccessView::class.java)
        intent.putExtra(AppConstants.RESPONSE, mViewModel.step2ApiResponse)
        intent.putExtra(AppConstants.FROM_WHICH_SCREEN, AppConstants.ADD_MONEY)
        startActivity(intent)
        finish()
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

    private fun callTransactionSuccessBottomSheet() {
        val bottomSheet =
            Utility.convertToRs(mViewModel.step2ApiResponse.amount!!)?.let {
                Utility.convertToRs(mViewModel.step2ApiResponse.balance!!)?.let { it1 ->
                    AddMoneySuccessBottomSheet(
                        it,
                        it1,onViewDetailsClick={
                            callViewPaymentDetails()
                        },onHomeViewClick = {
                            intentToHomeActivity(HomeActivity::class.java)
                        }
                    )
                }
            }
        bottomSheet?.dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.RED))
        bottomSheet?.show(supportFragmentManager, "TransactionSuccess")
    }

    private fun intentToHomeActivity(aClass: Class<*>) {
        startActivity(Intent(this, aClass))
        finish()
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
        mViewModel.modeOfPayment.set(2)
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


            /**
             * This method will be called after a failed transaction.
             *
             * @param payuResponse     response sent by PayU in App
             * @param merchantResponse response received from Furl
             */
            override fun onPaymentFailure(payuResponse: String, merchantResponse: String) {
                //mViewModel.isPaymentFail.set(true)
                mViewModel.payUResponse.set(payuResponse)
                mViewModel.callAddMoneyStep2Api()
            }

            override fun onPaymentTerminate() {
                trackr {
                    it.name = TrackrEvent.load_money_external_terminate
                    it.add(
                        TrackrField.user_mobile_no, SharedPrefUtils.getString(
                            PockketApplication.instance,
                            SharedPrefUtils.SF_KEY_USER_MOBILE))
                    it.add(TrackrField.transaction_amount,mViewModel.amountToAdd)
                }
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
                super.onBackApprove()
                mViewModel.isPaymentFail.set(true)
                trackr {
                    it.name = TrackrEvent.load_user_back
                    it.add(
                        TrackrField.user_mobile_no, SharedPrefUtils.getString(
                            PockketApplication.instance,
                            SharedPrefUtils.SF_KEY_USER_MOBILE))
                    it.add(TrackrField.transaction_amount,mViewModel.amountToAdd)
                }
            }

            override fun onBackDismiss() {
                super.onBackDismiss()
                // Utility.showToast("onBackDismiss")
                trackr {
                    it.name = TrackrEvent.on_back_dismiss
                    it.add(
                        TrackrField.user_mobile_no, SharedPrefUtils.getString(
                            PockketApplication.instance,
                            SharedPrefUtils.SF_KEY_USER_MOBILE))
                    it.add(TrackrField.transaction_amount,mViewModel.amountToAdd)
                }
            }

            /**
             * This callback method will be invoked when setDisableBackButtonDialog is set to true.
             *
             * @param alertDialogBuilder a reference of AlertDialog.Builder to customize the dialog
             */
            override fun onBackButton(alertDialogBuilder: AlertDialog.Builder) {
                super.onBackButton(alertDialogBuilder)
                //Utility.showToast("onBackButton")
                trackr {
                    it.name = TrackrEvent.load_user_back
                    it.add(
                        TrackrField.user_mobile_no, SharedPrefUtils.getString(
                            PockketApplication.instance,
                            SharedPrefUtils.SF_KEY_USER_MOBILE))
                    it.add(TrackrField.transaction_amount,mViewModel.amountToAdd)
                }
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
        when (mViewModel.modeOfPayment.get()) {
            1 -> {
                mViewModel.onUpiClicked.value = true
                mViewModel.callAddMoneyStep1Api()
            }
            2->{
                mViewModel.onAddNewCardClicked.value = true
                mViewModel.callAddMoneyStep1Api()
            }
        }

    }

    override fun onAddUpiClickListener(upiId: String, isUpiSaved: Boolean) {
        mViewModel.upiEntered.set(upiId)
        mViewModel.modeOfPayment.set(1)

        val upiList = ArrayList<String>()


        val savedupiList =
            SharedPrefUtils.getArrayList(application, SharedPrefUtils.SF_UPI_LIST)
        savedupiList?.forEach {
            upiList.add(it)
        }
        if (!upiList.contains(upiId)) {
            upiList.add(upiId)
            SharedPrefUtils.putArrayList(
                applicationContext,
                SharedPrefUtils.SF_UPI_LIST, upiList

            )
        }


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
                // customBrowserConfig.setDisableBackButtonDialog(true)
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
                customBrowserConfig.payuPostData = payuConfig.data

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
        //upiList.add(UpiModel(name = getString(R.string.google_pay)))
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
                if (!upiModel.packageName.equals(AppConstants.FAMPAY_PACKAGE_NAME)){
                    upiList.add(upiModel)
                }
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


    private fun showProgressDialogView(): View {
        return layoutInflater.inflate(R.layout.progress_bar_layout, null)
    }

    private fun showToolBarView(): View {
        val view = layoutInflater.inflate(R.layout.toolbar_for_gateway, null)
        val toolbar = view.findViewById<Toolbar>(R.id.toolbar)
        toolbar.setBackgroundColor(
            ContextCompat.getColor(
                applicationContext,
                R.color.white
            )
        )
        return view

    }

    /*
    * This method is used to call upi intents
    * */
    fun callUpiIntent() {
        when (mViewModel.clickedPositionForUpi.get()) {
            0 -> {
                callAddUpiBottomSheet()
            }/*
            1 -> {
                callGooglePayIntent()
            }*/
            else -> {
                val params = mViewModel.getPaymentParams(type = AppConstants.TYPE_GENERIC)
                try {
                    callCustomBrowser(
                        com.payu.paymentparamhelper.PayuConstants.UPI_INTENT,
                        params,
                        params.txnId,
                        PayuConstants.PRODUCTION_PAYMENT_URL,
                        isSpecificAppSet = true,
                        packageName = mViewModel.clickedAppPackageName.get()
                    )

                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
            }
        }
    }


}

