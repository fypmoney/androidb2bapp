package com.fypmoney.view.activity

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.fyp.trackr.models.TrackrEvent
import com.fyp.trackr.models.trackr
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.application.PockketApplication
import com.fypmoney.base.BaseActivity
import com.fypmoney.databinding.ViewAddMoneyBinding
import com.fypmoney.extension.toGone
import com.fypmoney.extension.toVisible
import com.fypmoney.util.AppConstants
import com.fypmoney.util.SharedPrefUtils
import com.fypmoney.util.Utility
import com.fypmoney.util.roundOfAmountToCeli
import com.fypmoney.util.videoplayer.VideoActivity2
import com.fypmoney.view.fragment.MaxLoadAmountReachedWarningDialogFragment
import com.fypmoney.view.upgradetokyc.UpgradeToKycInfoActivity
import com.fypmoney.view.webview.ARG_WEB_URL_TO_OPEN
import com.fypmoney.viewmodel.AddMoneyViewModel
import com.google.firebase.crashlytics.FirebaseCrashlytics
import kotlinx.android.synthetic.main.bottom_sheet_redeem_coins.view.*
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.android.synthetic.main.view_add_money.*
import kotlinx.android.synthetic.main.view_add_money.add_money_editext
import kotlinx.android.synthetic.main.view_add_money.btnSendOtp
import kotlinx.android.synthetic.main.view_enter_amount_for_pay_request.*

/*
* This class is used to add money
* */
class AddMoneyView : BaseActivity<ViewAddMoneyBinding, AddMoneyViewModel>(){
    private lateinit var mViewModel: AddMoneyViewModel

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.view_add_money
    }

    override fun getViewModel(): AddMoneyViewModel {
        mViewModel = ViewModelProvider(this).get(AddMoneyViewModel::class.java)
        return mViewModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setToolbarAndTitle(
            context = this@AddMoneyView,
            toolbar = toolbar,
            isBackArrowVisible = true, toolbarTitle = getString(R.string.add_money_screen_title)
        )
        val amount = intent.getStringExtra("amountshouldbeadded")
        setObserver()
        if (!amount.isNullOrEmpty()) {
            add_money_editext.setText(roundOfAmountToCeli(amount))
        }
        val videoLink = SharedPrefUtils.getString(
            this,
            SharedPrefUtils.SF_ADD_MONEY_VIDEO
        )
        video.setOnClickListener(View.OnClickListener {
            if (!videoLink.isNullOrEmpty()) {
                val intent = Intent(this, VideoActivity2::class.java)
                intent.putExtra(ARG_WEB_URL_TO_OPEN, videoLink)

                startActivity(intent)
            }

        })
    }

    override fun onStart() {
        super.onStart()
        if (checkUpgradeKycStatus()) {
            increase_limit.toGone()
        } else {
            increase_limit.toVisible()

        }
    }

    /**
     * Create this method for observe the viewModel fields
     */
    private fun setObserver() {
        add_money_editext.setSelection(add_money_editext.text.length)
        add_money_editext.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                try {
                    if (s.toString().startsWith("0")) {
                        s.clear()
                    } else {
                        if (s.toString().isNotEmpty()) {
                            /*btnSendOtp.backgroundTintList =
                                ColorStateList.valueOf(resources.getColor(R.color.text_color_dark))
                            btnSendOtp.setTextColor(
                                ContextCompat.getColor(
                                    this@AddMoneyView,
                                    R.color.white
                                )
                            )*/
                            btnSendOtp.isEnabled = true
                            if (!mViewModel.remainingLoadLimitAmount.get()
                                    .isNullOrEmpty() && s.toString()
                                    .toInt() > mViewModel.remainingLoadLimitAmount.get()!!.toInt() / 100
                            ) {
                                add_money_editext.setText(
                                    (mViewModel.remainingLoadLimitAmount.get()!!
                                        .toInt() / 100).toString()
                                )
                                add_money_editext.setSelection(add_money_editext.text.length)
                            }

                        } else {
                            /*btnSendOtp.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.cb_grey))
                            btnSendOtp.setTextColor(
                                ContextCompat.getColor(
                                    this@AddMoneyView,
                                    R.color.grey_heading
                                )
                            )*/
                            btnSendOtp.isEnabled = false


                        }
                    }
                }catch (e:NumberFormatException){
                    mViewModel.remainingLoadLimit.get()?.let {
                        Utility.showToast(mViewModel.remainingLoadLimit.get())
                        FirebaseCrashlytics.getInstance()
                            .setCustomKey("load_amount", it)
                        FirebaseCrashlytics.getInstance().recordException(e)
                        add_money_editext.setText("")
                    }

                }

            }
        })
        mViewModel.setEdittextLength.observe(this) {
            if (it) {
                add_money_editext.setSelection(add_money_editext.text.length)
                mViewModel.setEdittextLength.value = false
                }
            }
        mViewModel.increseLimitClicked.observe(this) {
            if (it) {
                trackr {
                    it.name = TrackrEvent.increase_limit_clicked
                }
                val intent  = Intent(this, UpgradeToKycInfoActivity::class.java).apply {
                    putExtra(AppConstants.KYC_UPGRADE_FROM_WHICH_SCREEN,AddMoneyView::class.java.simpleName)
                }
                startActivity(intent)
                  mViewModel.increseLimitClicked.value = false
                }
            }

            mViewModel.onAddClicked.observe(this) {
                if (it) {
                    intentToActivity(AddMoneyUpiDebitView::class.java)
                    // callBottomSheet(mViewModel.amountSelected.get()!!, getString(R.string.dummy_amount))
                    mViewModel.onAddClicked.value = false
                }
            }
            mViewModel.maxLoadLimitReached.observe(this) {
                if (it) {
                    val showMaxLoadAmountReachedWarningDialogFragment = MaxLoadAmountReachedWarningDialogFragment()
                    showMaxLoadAmountReachedWarningDialogFragment.show(supportFragmentManager, "showMaxLoadAmountReachedWarningDialogFragment")
                    mViewModel.maxLoadLimitReached.value = false
                }
            }
        }

        override fun onTryAgainClicked() {

        }


    private fun checkUpgradeKycStatus():Boolean{
         SharedPrefUtils.getString(PockketApplication.instance, SharedPrefUtils.SF_KYC_TYPE)?.let {
            return it != AppConstants.MINIMUM
        }?:run {
            return true
        }

    }


        /**
         * Method to navigate to the different activity
         */
        private fun intentToActivity(aClass: Class<*>) {
            val intent = Intent(applicationContext, aClass)
            intent.putExtra(AppConstants.AMOUNT, mViewModel.amountSelected.get())
            startActivity(intent)
        }
    

}
