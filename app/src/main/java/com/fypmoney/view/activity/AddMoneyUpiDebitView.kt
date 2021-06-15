package com.fypmoney.view.activity

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.webkit.WebView
import android.widget.ProgressBar
import androidx.lifecycle.ViewModelProvider
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseActivity
import com.fypmoney.databinding.ViewAddMoneyUpiDebitBinding
import com.fypmoney.model.AddNewCardDetails
import com.fypmoney.util.AppConstants
import com.fypmoney.util.PayUGpayResponse
import com.fypmoney.util.Utility
import com.fypmoney.view.fragment.AddNewCardBottomSheet
import com.fypmoney.view.fragment.AddUpiBottomSheet
import com.fypmoney.view.fragment.TransactionFailBottomSheet
import com.fypmoney.viewmodel.AddMoneyUpiDebitViewModel
import com.payu.custombrowser.*
import com.payu.custombrowser.bean.CustomBrowserConfig
import com.payu.gpay.GPay
import com.payu.india.Interfaces.PaymentRelatedDetailsListener
import com.payu.india.Interfaces.ValueAddedServiceApiListener
import com.payu.india.Model.PayuConfig
import com.payu.india.Model.PayuResponse
import com.payu.india.Payu.PayuConstants
import com.payu.india.Payu.PayuErrors
import com.payu.india.PostParams.PaymentPostParams
import com.payu.paymentparamhelper.PostData
import com.payu.phonepe.PhonePe
import com.payu.phonepe.callbacks.PayUPhonePeCallback
import com.payu.upisdk.PaymentOption
import com.payu.upisdk.Upi
import com.payu.upisdk.bean.UpiConfig
import com.payu.upisdk.callbacks.PayUUPICallback
import com.payu.upisdk.generatepostdata.PostDataGenerate.PostDataBuilder
import com.payu.upisdk.upi.IValidityCheck
import com.payu.upisdk.util.UpiConstant
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

        mViewModel.getAllSavedCardsApi()


        /*  val ss = SpannableString(getString(R.string.account_verification_sub_title))
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
          tvSubTitle.movementMethod = LinkMovementMethod.getInstance()
  */
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
        val callback = PayUGpayResponse(this)

        Log.d(
            "post data",
            mViewModel.requestData.get()!!
        )
        Log.d(
            "merchany_key",
            mViewModel.parseResponseOfStep1(mViewModel.requestData.get()).merchantKey.toString()
        )

        Log.d(
            "hash",
            mViewModel.parseResponseOfStep1(mViewModel.requestData.get()).paymentHash.toString()
        )

        GPay.getInstance().checkForPaymentAvailability(
            this@AddMoneyUpiDebitView,
            callback,
            mViewModel.hash.get(),
            "gtKFFx",
            "default"
        )
        GPay.getInstance().makePayment(
            this@AddMoneyUpiDebitView,
            mViewModel.requestData.get(),
            callback,
            mViewModel.merchantKey.get(),
            progressBar
        )

    }


    override fun onGpayResponseListener(payuResponse: String?) {
        Log.d("gpay_responseeee", payuResponse.toString())
    }

    /*
    * Used to call in case of phone pay
    * */
    private fun callPhonePayIntent() {
        try {
            PhonePe.getInstance().checkForPaymentAvailability(
                this@AddMoneyUpiDebitView,
                payUPhonePeCallback,
                mViewModel.hash.get(),
                mViewModel.merchantKey.get(),
                mViewModel.merchantKey.get() + ":" + "abcd"
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
            val postData = PaymentPostParams(
                mViewModel.getPaymentParams(addNewCardDetails),
                PayuConstants.CC
            ).paymentPostParams

            if (postData != null) {
                payuConfig.data = postData.result
                //Sets the configuration of custom browser

                //Sets the configuration of custom browser
                val customBrowserConfig = CustomBrowserConfig(
                    mViewModel.getPaymentParams(addNewCardDetails).key,
                    mViewModel.getPaymentParams(addNewCardDetails).txnId
                )
                customBrowserConfig.setAutoApprove(false) // set true to automatically approve the OTP

                customBrowserConfig.setAutoSelectOTP(true) // set true to automatically select the OTP flow


                //Set below flag to true to disable the default alert dialog of Custom Browser and use your custom dialog

                //Set below flag to true to disable the default alert dialog of Custom Browser and use your custom dialog
                customBrowserConfig.setDisableBackButtonDialog(false)


                //Below flag is used for One Click Payments. It should always be set to CustomBrowserConfig.STOREONECLICKHASH_MODE_SERVER


                //Set it to true to enable run time permission dialog to appear for all Android 6.0 and above devices


                //Below flag is used for One Click Payments. It should always be set to CustomBrowserConfig.STOREONECLICKHASH_MODE_SERVER


                //Set it to true to enable run time permission dialog to appear for all Android 6.0 and above devices
                customBrowserConfig.setMerchantSMSPermission(true)

                //Set it to true to enable Magic retry (If MR is enabled SurePay should be disabled and vice-versa)


                //Set it to false if you do not want the transaction with web-collect flow
                //customBrowserConfig.setEnableWebFlow(Payment.TEZ,true);


                /**
                 * Maximum number of times the SurePay dialog box will prompt the user to retry a transaction in case of network failures
                 * Setting the sure pay count to 0, diables the sure pay dialog
                 */

                //Set it to true to enable Magic retry (If MR is enabled SurePay should be disabled and vice-versa)


                //Set it to false if you do not want the transaction with web-collect flow
                //customBrowserConfig.setEnableWebFlow(Payment.TEZ,true);
                /**
                 * Maximum number of times the SurePay dialog box will prompt the user to retry a transaction in case of network failures
                 * Setting the sure pay count to 0, diables the sure pay dialog
                 */
                customBrowserConfig.enableSurePay = 3


                //htmlData - HTML string received from PayU webservice using Server to Server call.

                // customBrowserConfig.setHtmlData("");


                //surepayS2Surl - Url on which HTML received from PayU webservice using Server to Server call is hosted.

                // customBrowserConfig.setSurepayS2Surl("");


                /**
                 * set Merchant activity(Absolute path of activity)
                 * By the time CB detects good network, if CBWebview is destroyed, we resume the transaction by passing payment post data to,
                 * this, merchant checkout activity.
                 * */

                //htmlData - HTML string received from PayU webservice using Server to Server call.

                // customBrowserConfig.setHtmlData("");


                //surepayS2Surl - Url on which HTML received from PayU webservice using Server to Server call is hosted.

                // customBrowserConfig.setSurepayS2Surl("");
                /**
                 * set Merchant activity(Absolute path of activity)
                 * By the time CB detects good network, if CBWebview is destroyed, we resume the transaction by passing payment post data to,
                 * this, merchant checkout activity.
                 */
                customBrowserConfig.merchantCheckoutActivityPath =
                    "com.payu.testapp.MerchantCheckoutActivity"

                //Set the first url to open in WebView

                //Set the first url to open in WebView
                customBrowserConfig.postURL = PayuConstants.TEST_PAYMENT_URL

                //String postData = "device_type=1&udid=fd7637d3ed7d3ee5&imei=default&key=l80gyM&txnid=MFIT4691279-R1&amount=1&productinfo=gym workout - fitternity test page&firstname=akhil kulkarni&email=akhilkulkarni@fitternity.com&surl=https%3A%2F%2Fwww.fitternity.com%2Fpaymentsuccessandroid&furl=https%3A%2F%2Fwww.fitternity.com%2Fpaymentsuccessandroid&hash=26e07dbe45dee6cfb11c5c4698b3c027a32446d5e7bf84b503566832f452d73775d493b2a8169675aaa17fb8be2e7c2f14c0fb937965f7d86b5e4e5d89db69fc&udf1=&udf2=&udf3=&udf4=&udf5=&phone=7021705378&bankcode=TEZ&pg=upi";

                // String postData = "device_type=1&udid=51e15a3e697d56fe&imei=default&key=smsplus&txnid=1576147630600&amount=10000&productinfo=product_info&firstname=firstname&email=test@gmail.com&surl=+https%3A%2F%2Fpayuresponse.firebaseapp.com%2Fsuccess&furl=https%3A%2F%2Fpayuresponse.firebaseapp.com%2Ffailure&hash=dc48b7ce77bc34744bb0e264b44f302c8c4ec2a0eb171a2d4753eeecff96ce865df0c9e504a46bd72f0718eb56678ed0f5e56f8d3bc4baf1817e8828b88fcf17&udf1=udf1&udf2=udf2&udf3=udf3&udf4=udf4&udf5=udf5&phone=&pg=EMI&bankcode=HDFC03&ccnum=5326760132120978&ccvv=123&ccexpyr=2021&ccexpmon=8&ccname=PayuUser";

                // String postData = "device_type=1&udid=ea569f24d4748199&imei=default&key=l80gyM&txnid=MFIT4582680-R3&amount=300&productinfo=gym workout - zion fitness&firstname=kunal parte&email=kunal_coolrider14@hotmail.com&surl=https%3A%2F%2Fwww.fitternity.com%2Fpaymentsuccessandroid&furl=https%3A%2F%2Fwww.fitternity.com%2Fpaymentsuccessandroid&hash=9baff7f90f7af4d967697bc9495e67c8cb8aaed9ee9ffff6a53a10c0f88c608d10a6ac5d26fc71c554be7e5b6de0d696aec8bd17bdb9b6206553d09018643d4d&udf1=&udf2=&udf3=&udf4=&udf5=&phone=7021705378&bankcode=TEZ&pg=upi";

                // String postData = "device_type=1&udid=51e15a3e697d56fe&imei=default&key=smsplus&txnid=1576214482978&amount=2&productinfo=product_info&firstname=firstname&email=test@gmail.com&surl=+https%3A%2F%2Fpayuresponse.firebaseapp.com%2Fsuccess&furl=https%3A%2F%2Fpayuresponse.firebaseapp.com%2Ffailure&hash=4087690a25942342875eac830da473d0d8ca3a586ec49237816a929f602b5817c9c367474011031e3c1c5e9a441172b77cf90486f86f0a412df72ea4641d5d0e&udf1=udf1&udf2=udf2&udf3=udf3&udf4=udf4&udf5=udf5&phone=&pg=CC&bankcode=CC&ccnum=5326760132120978&ccvv=893&ccexpyr=2021&ccexpmon=7&ccname=PayuUser&si=1&store_card=1&user_credentials=smsplus:ras";

                // String postData = "device_type=1&udid=947b45e0194ca0b6&imei=default&key=VgZldf&txnid=1568978613464&amount=10&productinfo=product_info&firstname=firstname&email=test@gmail.com&surl=+https%3A%2F%2Fpayuresponse.firebaseapp.com%2Fsuccess&furl=https%3A%2F%2Fpayuresponse.firebaseapp.com%2Ffailure&hash=d20b29d2937473616186dc332f9889768e11919252a11347cf5e2cdc67ac1703a475eb4ad2b68349804a71835e55b831516804c7155d7232df799679f924b3f4&udf1=udf1&udf2=udf2&udf3=udf3&udf4=udf4&udf5=udf5&phone=&pg=CC&bankcode=CC&ccnum=5123456789012346&ccvv=123&ccexpyr=2020&ccexpmon=5&ccname=PayuUser&user_credentials=ra:ra&SI=1";

                //  String postData = "device_type=1&udid=ea569f24d4748199&imei=default&key=l80gyM&txnid=MFIT4582680-R3&amount=300&productinfo=gym workout - zion fitness&firstname=kunal parte&email=kunal_coolrider14@hotmail.com&surl=https%3A%2F%2Fwww.fitternity.com%2Fpaymentsuccessandroid&furl=https%3A%2F%2Fwww.fitternity.com%2Fpaymentsuccessandroid&hash=9baff7f90f7af4d967697bc9495e67c8cb8aaed9ee9ffff6a53a10c0f88c608d10a6ac5d26fc71c554be7e5b6de0d696aec8bd17bdb9b6206553d09018643d4d&udf1=&udf2=&udf3=&udf4=&udf5=&phone=7021705378&bankcode=TEZ&pg=upi";
                // String postData= "device_type=1&udid=51e15a3e697d56fe&imei=default&key=smsplus&txnid=1576147630600&amount=2&productinfo=product_info&firstname=firstname&email=test@gmail.com&surl=+https%3A%2F%2Fpayuresponse.firebaseapp.com%2Fsuccess&furl=https%3A%2F%2Fpayuresponse.firebaseapp.com%2Ffailure&hash=7e4c2be92f650c4c5f192ef6d9ea7a87ea1a435be993b130aa0fabe643e3687dd9da452c5f366e591e9e42be7f418e070df866605d1e309fe0a41d4d2dae7d3c&udf1=udf1&udf2=udf2&udf3=udf3&udf4=udf4&udf5=udf5&phone=&pg=CC&bankcode=CC&ccnum=5326760132120978&ccvv=123&ccexpyr=2021&ccexpmon=8&ccname=PayuUser&store_card=1&user_credentials=sa:sa";

                //String postData = "device_type=1&udid=fd7637d3ed7d3ee5&imei=default&key=l80gyM&txnid=MFIT4691279-R1&amount=1&productinfo=gym workout - fitternity test page&firstname=akhil kulkarni&email=akhilkulkarni@fitternity.com&surl=https%3A%2F%2Fwww.fitternity.com%2Fpaymentsuccessandroid&furl=https%3A%2F%2Fwww.fitternity.com%2Fpaymentsuccessandroid&hash=26e07dbe45dee6cfb11c5c4698b3c027a32446d5e7bf84b503566832f452d73775d493b2a8169675aaa17fb8be2e7c2f14c0fb937965f7d86b5e4e5d89db69fc&udf1=&udf2=&udf3=&udf4=&udf5=&phone=7021705378&bankcode=TEZ&pg=upi";

                // String postData = "device_type=1&udid=51e15a3e697d56fe&imei=default&key=smsplus&txnid=1576147630600&amount=10000&productinfo=product_info&firstname=firstname&email=test@gmail.com&surl=+https%3A%2F%2Fpayuresponse.firebaseapp.com%2Fsuccess&furl=https%3A%2F%2Fpayuresponse.firebaseapp.com%2Ffailure&hash=dc48b7ce77bc34744bb0e264b44f302c8c4ec2a0eb171a2d4753eeecff96ce865df0c9e504a46bd72f0718eb56678ed0f5e56f8d3bc4baf1817e8828b88fcf17&udf1=udf1&udf2=udf2&udf3=udf3&udf4=udf4&udf5=udf5&phone=&pg=EMI&bankcode=HDFC03&ccnum=5326760132120978&ccvv=123&ccexpyr=2021&ccexpmon=8&ccname=PayuUser";

                // String postData = "device_type=1&udid=ea569f24d4748199&imei=default&key=l80gyM&txnid=MFIT4582680-R3&amount=300&productinfo=gym workout - zion fitness&firstname=kunal parte&email=kunal_coolrider14@hotmail.com&surl=https%3A%2F%2Fwww.fitternity.com%2Fpaymentsuccessandroid&furl=https%3A%2F%2Fwww.fitternity.com%2Fpaymentsuccessandroid&hash=9baff7f90f7af4d967697bc9495e67c8cb8aaed9ee9ffff6a53a10c0f88c608d10a6ac5d26fc71c554be7e5b6de0d696aec8bd17bdb9b6206553d09018643d4d&udf1=&udf2=&udf3=&udf4=&udf5=&phone=7021705378&bankcode=TEZ&pg=upi";

                // String postData = "device_type=1&udid=51e15a3e697d56fe&imei=default&key=smsplus&txnid=1576214482978&amount=2&productinfo=product_info&firstname=firstname&email=test@gmail.com&surl=+https%3A%2F%2Fpayuresponse.firebaseapp.com%2Fsuccess&furl=https%3A%2F%2Fpayuresponse.firebaseapp.com%2Ffailure&hash=4087690a25942342875eac830da473d0d8ca3a586ec49237816a929f602b5817c9c367474011031e3c1c5e9a441172b77cf90486f86f0a412df72ea4641d5d0e&udf1=udf1&udf2=udf2&udf3=udf3&udf4=udf4&udf5=udf5&phone=&pg=CC&bankcode=CC&ccnum=5326760132120978&ccvv=893&ccexpyr=2021&ccexpmon=7&ccname=PayuUser&si=1&store_card=1&user_credentials=smsplus:ras";

                // String postData = "device_type=1&udid=947b45e0194ca0b6&imei=default&key=VgZldf&txnid=1568978613464&amount=10&productinfo=product_info&firstname=firstname&email=test@gmail.com&surl=+https%3A%2F%2Fpayuresponse.firebaseapp.com%2Fsuccess&furl=https%3A%2F%2Fpayuresponse.firebaseapp.com%2Ffailure&hash=d20b29d2937473616186dc332f9889768e11919252a11347cf5e2cdc67ac1703a475eb4ad2b68349804a71835e55b831516804c7155d7232df799679f924b3f4&udf1=udf1&udf2=udf2&udf3=udf3&udf4=udf4&udf5=udf5&phone=&pg=CC&bankcode=CC&ccnum=5123456789012346&ccvv=123&ccexpyr=2020&ccexpmon=5&ccname=PayuUser&user_credentials=ra:ra&SI=1";

                //  String postData = "device_type=1&udid=ea569f24d4748199&imei=default&key=l80gyM&txnid=MFIT4582680-R3&amount=300&productinfo=gym workout - zion fitness&firstname=kunal parte&email=kunal_coolrider14@hotmail.com&surl=https%3A%2F%2Fwww.fitternity.com%2Fpaymentsuccessandroid&furl=https%3A%2F%2Fwww.fitternity.com%2Fpaymentsuccessandroid&hash=9baff7f90f7af4d967697bc9495e67c8cb8aaed9ee9ffff6a53a10c0f88c608d10a6ac5d26fc71c554be7e5b6de0d696aec8bd17bdb9b6206553d09018643d4d&udf1=&udf2=&udf3=&udf4=&udf5=&phone=7021705378&bankcode=TEZ&pg=upi";
                // String postData= "device_type=1&udid=51e15a3e697d56fe&imei=default&key=smsplus&txnid=1576147630600&amount=2&productinfo=product_info&firstname=firstname&email=test@gmail.com&surl=+https%3A%2F%2Fpayuresponse.firebaseapp.com%2Fsuccess&furl=https%3A%2F%2Fpayuresponse.firebaseapp.com%2Ffailure&hash=7e4c2be92f650c4c5f192ef6d9ea7a87ea1a435be993b130aa0fabe643e3687dd9da452c5f366e591e9e42be7f418e070df866605d1e309fe0a41d4d2dae7d3c&udf1=udf1&udf2=udf2&udf3=udf3&udf4=udf4&udf5=udf5&phone=&pg=CC&bankcode=CC&ccnum=5326760132120978&ccvv=123&ccexpyr=2021&ccexpmon=8&ccname=PayuUser&store_card=1&user_credentials=sa:sa";
                if (payuConfig != null) customBrowserConfig.payuPostData = payuConfig.data


                /* if (isPaymentByPhonePe == true & isStandAlonePhonePayAvailable == true) {

                    phonePe.makePayment(payUPhonePeCallback, PaymentsActivity.this, payuConfig.getData(),false);

                } else*/
                /* if (isPaymentByPhonePe == true & isStandAlonePhonePayAvailable == true) {

                    phonePe.makePayment(payUPhonePeCallback, PaymentsActivity.this, payuConfig.getData(),false);

                } else*/
                CustomBrowser().addCustomBrowser(
                    this@AddMoneyUpiDebitView,
                    customBrowserConfig,
                    payUCustomBrowserCallback
                )


            }

        } catch (e: Exception) {
            e.printStackTrace()
        }




        Log.d("shnfveuvg8", addNewCardDetails.toString())
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
                val input = "smsplus|validateVPA|$vpa|1b1b0"
                val verifyVpaHash = mViewModel.hash.get()
                Log.d("kfkoefkepo", "onVpaEntered")

                packageListDialogFragment.verifyVpa(verifyVpaHash)
                //     Log.d("kfkoefkepo", "onVpaEntered")
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
            }

            override fun setCBProperties(webview: WebView, payUCustomBrowser: Bank) {
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
        }

    override fun onBottomSheetButtonClick(type: String) {

    }

    override fun onAddUpiClickListener(upiId: String, isUpiSaved: Boolean) {
        val params = mViewModel.getPaymentParamsForUpi(upiId, isUpiSaved)
        val postDataFromUpiSdk = PostDataBuilder(this).setPaymentMode(UpiConstant.UPI)
            .setPaymentParamUpiSdk(params).build().toString()
        val upi: Upi = Upi.getInstance()
        upi.checkForPaymentAvailability(
            this,
            PaymentOption.PHONEPE,
            payUUpiSdkCallback,
            mViewModel.hash.get(),
            mViewModel.merchantKey.get(),
            mViewModel.merchantKey.get() + ":" + "abcd"
        )
        upi.checkForPaymentAvailability(
            this,
            PaymentOption.SAMSUNGPAY,
            payUUpiSdkCallback,
            mViewModel.hash.get(),
            mViewModel.merchantKey.get(),
            mViewModel.merchantKey.get() + ":" + "abcd"
        )
        upi.checkForPaymentAvailability(
            this,
            PaymentOption.TEZ,
            payUUpiSdkCallback,
            mViewModel.hash.get(),
            mViewModel.merchantKey.get(),
            mViewModel.merchantKey.get() + ":" + "abcd"
        )

        val upiConfig = UpiConfig()
        upiConfig.merchantKey = mViewModel.merchantKey.get()
        upiConfig.payuPostData = postDataFromUpiSdk// that we generate above
        //In order to set CustomProgress View use below settings
        upiConfig.progressDialogCustomView = progressBar
        upiConfig.disableIntentSeamlessFailure=UpiConfig.FALSE
        upiConfig.postUrl=PayuConstants.TEST_PAYMENT_URL


        Upi.getInstance()
            .makePayment(payUUpiSdkCallbackUpiSdk, this@AddMoneyUpiDebitView, upiConfig)


    }

    /**
     * Callback of payment availability while doing through UPISDK.
     */
    private var payUUpiSdkCallback: PayUUPICallback = object : PayUUPICallback() {
        override fun isPaymentOptionAvailable(isAvailable: Boolean, paymentOption: PaymentOption) {
            super.isPaymentOptionAvailable(isAvailable, paymentOption)
            when (paymentOption) {
                PaymentOption.PHONEPE -> {
                }
                PaymentOption.SAMSUNGPAY -> {
                }

            }
        }
    }

   private var payUUpiSdkCallbackUpiSdk: PayUUPICallback = object : PayUUPICallback() {
        override fun onPaymentFailure(payuResult: String, merchantResponse: String) {
            super.onPaymentFailure(payuResult, merchantResponse)
            Utility.showToast("onPaymentFailure")

            //Payment failed
        }

        override fun onPaymentSuccess(payuResult: String, merchantResponse: String) {
            super.onPaymentSuccess(payuResult, merchantResponse)
            Utility.showToast("onPaymentSuccess")

            //Payment succeed
        }

        override fun onVpaEntered(vpa: String, iValidityCheck: IValidityCheck) {
            super.onVpaEntered(vpa, iValidityCheck)
            Utility.showToast("onVpaEntered")

            val input = "payu merchant key|validateVPA|$vpa|payu merchant salt"
            iValidityCheck.verifyVpa(mViewModel.hash.get())
        }

        override fun onUpiErrorReceived(code: Int, errormsg: String) {
            Utility.showToast("onUpiErrorReceived")
            super.onUpiErrorReceived(code, errormsg)

            //Any error on upisdk
        }
    }
}
