package com.fypmoney.view.activity

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.fyp.trackr.models.TrackrEvent
import com.fyp.trackr.models.TrackrField
import com.fyp.trackr.models.trackr
import com.fyp.trackr.services.TrackrServices
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseActivity
import com.fypmoney.databinding.ViewEnterAmountForPayRequestBinding
import com.fypmoney.model.SendMoneyResponse
import com.fypmoney.util.AppConstants
import com.fypmoney.util.SharedPrefUtils
import com.fypmoney.util.Utility
import com.fypmoney.view.AddMoneySuccessBottomSheet
import com.fypmoney.view.fragment.TaskMessageInsuficientFuntBottomSheet
import com.fypmoney.view.fragment.TransactionFailBottomSheet
import com.fypmoney.view.interfaces.AcceptRejectClickListener
import com.fypmoney.viewmodel.EnterAmountForPayRequestViewModel
import com.google.firebase.crashlytics.FirebaseCrashlytics
import kotlinx.android.synthetic.main.activity_add_task.*
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.android.synthetic.main.view_add_money.*
import kotlinx.android.synthetic.main.view_enter_amount_for_pay_request.*
import kotlinx.android.synthetic.main.view_enter_amount_for_pay_request.add_money_editext
import kotlinx.android.synthetic.main.view_enter_amount_for_pay_request.btnSendOtp
import java.lang.NumberFormatException


class EnterAmountForPayRequestView :
    BaseActivity<ViewEnterAmountForPayRequestBinding, EnterAmountForPayRequestViewModel>(),
    TransactionFailBottomSheet.OnBottomSheetClickListener {
    private lateinit var mViewModel: EnterAmountForPayRequestViewModel
    private var bottomsheetInsufficient: TaskMessageInsuficientFuntBottomSheet? = null

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.view_enter_amount_for_pay_request
    }

    override fun getViewModel(): EnterAmountForPayRequestViewModel {
        mViewModel = ViewModelProvider(this).get(EnterAmountForPayRequestViewModel::class.java)
        return mViewModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewModel.setResponseAfterContactSelected(
            intent.getParcelableExtra(AppConstants.CONTACT_SELECTED_RESPONSE),
            actionValue = intent.getStringExtra(AppConstants.WHICH_ACTION),
            qrCode = intent.getStringExtra(AppConstants.FUND_TRANSFER_QR_CODE)
        )

        add_money_editext.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                try{
                    if (s.toString().startsWith("0")) {
                        s.clear()
                    } else {
                        if (s.toString().isNotEmpty()) {
                            btnSendOtp.setBackgroundTintList(ColorStateList.valueOf(resources.getColor(R.color.text_color_dark)));
                            btnSendOtp.setTextColor(
                                ContextCompat.getColor(
                                    this@EnterAmountForPayRequestView,
                                    R.color.white
                                )
                            )
                            if (s.toString().toInt() > 9999) {
                                add_money_editext.setText(getString(R.string.amount_limit))
                                add_money_editext.text?.length?.let { add_money_editext.setSelection(it) };
                            }
                        } else {
                            btnSendOtp.setBackgroundTintList(ColorStateList.valueOf(resources.getColor(R.color.cb_grey)));
                            btnSendOtp.setTextColor(
                                ContextCompat.getColor(
                                    this@EnterAmountForPayRequestView,
                                    R.color.grey_heading
                                )
                            )

                        }
                    }
                }catch (e:NumberFormatException){
                    Utility.showToast(getString(R.string.you_can_send_or_request_up_to_10000))
                    FirebaseCrashlytics.getInstance()
                        .setCustomKey("p2p_amount", s.toString())
                    FirebaseCrashlytics.getInstance().recordException(e)
                    add_money_editext.setText("")
                }



            }
        })
        when (mViewModel.action.get()) {
            AppConstants.PAY, AppConstants.PAY_USING_QR -> {
                setToolbarAndTitle(
                    context = this@EnterAmountForPayRequestView,
                    toolbar = toolbar,
                    isBackArrowVisible = true, toolbarTitle = getString(R.string.pay_title)
                )
            }
            AppConstants.REQUEST -> {
                setToolbarAndTitle(
                    context = this@EnterAmountForPayRequestView,
                    toolbar = toolbar,
                    isBackArrowVisible = true, toolbarTitle = getString(R.string.request_title)
                )
            }
        }

        setObserver()
    }

    /**
     * Create this method for observe the viewModel fields
     */
    private fun setObserver() {
        mViewModel.setEdittextLength.observe(this) {
            if (it) {
                add_money_editext.text?.length?.let { it1 -> add_money_editext.setSelection(it1) };
                mViewModel.setEdittextLength.value = false
            }
        }

        mViewModel.onApiResponse.observe(this) {
            when (it) {
                AppConstants.API_FAIL -> {
                    trackr { it1 ->
                        it1.services = arrayListOf(TrackrServices.MOENGAGE)
                        it1.name = TrackrEvent.Tran_faliure
                        it1.add(TrackrField.user_mobile_no,SharedPrefUtils.getString(this@EnterAmountForPayRequestView,
                            SharedPrefUtils.SF_KEY_USER_MOBILE))
                        it1.add(TrackrField.transaction_amount,mViewModel.amountToBeAdded)
                    }
                    callBottomSheet()
                }
                AppConstants.API_SUCCESS -> {
                    trackr {
                        it.name = TrackrEvent.Tran_success
                    }
                    intentToActivity(HomeView::class.java)
                }
                AppConstants.INSUFFICIENT_ERROR_CODE -> {
                    callInsuficientFundMessageSheet(Utility.convertToRs(mViewModel.amountToBeAdded))
                }
            }
        }
        mViewModel.sendMoneyApiResponse.observe(this) {
            callTransactionSuccessBottomSheet(it)
        }

        mViewModel.onPayClicked.observe(this) {
            if (it) {
                askForDevicePassword()
                mViewModel.onPayClicked.value = false
            }
        }
    }

    private fun callInsuficientFundMessageSheet(amount:String?) {
         bottomsheetInsufficient =
            TaskMessageInsuficientFuntBottomSheet(object : AcceptRejectClickListener {
                override fun onAcceptClicked(pos: Int, str: String) {
                    bottomsheetInsufficient?.dismiss()
                    /*intentToPayActivity(ContactListView::class.java, AppConstants.PAY)*/
                }

                override fun onRejectClicked(pos: Int) {
                    bottomsheetInsufficient?.dismiss()
                    callActivity(AddMoneyView::class.java,amount)
                }

                override fun ondimiss() {
                }
            },title = resources.getString(R.string.insufficient_bank_balance),
                subTitle =  resources.getString(R.string.insufficient_bank_body),
                amount = resources.getString(R.string.add_money_title1)+resources.getString(R.string.Rs)+amount
            )


        bottomsheetInsufficient?.dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.RED))
        bottomsheetInsufficient?.show(supportFragmentManager, "INSUFFIECIENT")
    }

    private fun callActivity(aClass: Class<*>,amount:String?) {
        val intent = Intent(this, aClass)
        intent.putExtra("amountshouldbeadded", amount)
        startActivity(intent)
    }
    private fun intentToPayActivity(aClass: Class<*>, pay: String? = null) {
        val intent = Intent(this, aClass)
        intent.putExtra(AppConstants.FROM_WHICH_SCREEN, pay)
        startActivity(intent)
    }
    private fun callTransactionSuccessBottomSheet(sendMoneyResponse: SendMoneyResponse) {
        val bottomSheet =
            mViewModel.amountSelected.get()?.let {
                Utility.convertToRs(sendMoneyResponse.sendMoneyResponseDetails.currentBalance)?.let { it1 ->
                    AddMoneySuccessBottomSheet(
                        it,
                        it1,onViewDetailsClick=null,successTitle = "Payment Made Successfully to ${sendMoneyResponse.sendMoneyResponseDetails.receiverName}",onHomeViewClick = {
                            intentToHomeActivity(HomeView::class.java)
                        }
                    )
                }
            }
        bottomSheet?.dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.RED))
        bottomSheet?.show(supportFragmentManager, "TransactionSuccess")
    }

    private fun intentToHomeActivity(aClass: Class<*>) {
        startActivity(Intent(this, aClass))
    }

    /*private fun callViewPaymentDetails() {
        val intent = Intent(this, PayUSuccessView::class.java)
        intent.putExtra(AppConstants.RESPONSE, mViewModel.step2ApiResponse)
        intent.putExtra(AppConstants.FROM_WHICH_SCREEN, AppConstants.ADD_MONEY)
        startActivity(intent)
        finish()
    }*/

    override
    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            AppConstants.DEVICE_SECURITY_REQUEST_CODE -> {
                when (resultCode) {
                    RESULT_OK -> {
                        runOnUiThread {
                            when (mViewModel.action.get()) {
                                AppConstants.PAY -> {
                                    mViewModel.callSendMoneyApi()

                                }
                                AppConstants.PAY_USING_QR -> {
                                    mViewModel.callSendMoneyUsingQrApi()

                                }
                                AppConstants.REQUEST -> {
                                    mViewModel.callRequestMoneyApi()

                                }
                            }

                        }

                    }

                }
            }
        }
    }

    /**
     * Method to navigate to the different activity
     */
    private fun intentToActivity(aClass: Class<*>) {
        startActivity(Intent(this@EnterAmountForPayRequestView, aClass))
        finish()
    }

    /*
  * This method is used to call leave member
  * */
    private fun callBottomSheet() {
        val bottomSheet =
            TransactionFailBottomSheet(
                AppConstants.PAY,
                mViewModel.amountSelected.get()!!, this
            )
        bottomSheet.dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.RED))
        bottomSheet.show(supportFragmentManager, "TransactionFail")
    }

    override fun onBottomSheetButtonClick(type: String) {

    }

}
