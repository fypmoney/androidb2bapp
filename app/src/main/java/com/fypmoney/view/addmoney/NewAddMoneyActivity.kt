package com.fypmoney.view.addmoney

import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.application.PockketApplication
import com.fypmoney.base.BaseActivity
import com.fypmoney.databinding.ActivityNewAddMoneyBinding
import com.fypmoney.extension.toGone
import com.fypmoney.extension.toVisible
import com.fypmoney.util.AppConstants
import com.fypmoney.util.SharedPrefUtils
import com.fypmoney.util.Utility
import com.fypmoney.util.Utility.shareScreenShotContent
import com.fypmoney.util.videoplayer.VideoActivity2
import com.fypmoney.view.activity.AddMoneyView
import com.fypmoney.view.addmoney.model.Visibility
import com.fypmoney.view.addmoney.viewmodel.NewAddMoneyActivityVM
import com.fypmoney.view.upgradetokyc.UpgradeToKycInfoActivity
import com.fypmoney.view.webview.ARG_WEB_URL_TO_OPEN
import kotlinx.android.synthetic.main.toolbar.*

class NewAddMoneyActivity : BaseActivity<ActivityNewAddMoneyBinding, NewAddMoneyActivityVM>() {

    private val newAddMoneyActivityVM by viewModels<NewAddMoneyActivityVM>()


    private lateinit var binding: ActivityNewAddMoneyBinding

    /**
     * Override for set binding variable
     *
     * @return variable id
     */
    override fun getBindingVariable(): Int = BR.viewModel

    /**
     * @return layout resource id
     */
    override fun getLayoutId(): Int = R.layout.activity_new_add_money

    /**
     * Override for set view model
     *
     * @return view model instance
     */
    override fun getViewModel(): NewAddMoneyActivityVM = newAddMoneyActivityVM

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = getViewDataBinding()
        setToolbarAndTitle(
            context = this@NewAddMoneyActivity,
            toolbar = toolbar,
            isBackArrowVisible = true, toolbarTitle = getString(R.string.add_money_screen_title)
        )
        setObserver()
    }


    private fun setObserver() {

        newAddMoneyActivityVM.state.observe(this) {
            handelState(it)
        }
        newAddMoneyActivityVM.event.observe(this) {
            handelEvent(it)
        }

    }


    private fun handelState(it: NewAddMoneyActivityVM.BankDetailsState?) {
        when (it) {
            NewAddMoneyActivityVM.BankDetailsState.BalanceError -> {
                binding.reloadCardDetailsBtn.toVisible()
            }
            NewAddMoneyActivityVM.BankDetailsState.BalanceIsLoading -> {
                binding.amountFetching.toVisible()
            }
            is NewAddMoneyActivityVM.BankDetailsState.BalanceSuccess -> {
                binding.amount.text = String.format(
                    getString(R.string.amount_with_currency),
                    it.loadMoneyWalletBalanceUIModel.balance
                )
                binding.instruction.text =
                    it.loadMoneyWalletBalanceUIModel.instructionOnBankTransfer
                binding.addMoneyText.text = it.loadMoneyWalletBalanceUIModel.remainingLoadLimitTxt
                binding.tvInstructionAddViaUPI.text =
                    it.loadMoneyWalletBalanceUIModel.instructionOnUpiQrCode
            }
            NewAddMoneyActivityVM.BankDetailsState.Error -> {
                binding.tvPaymentModeNotAvailable.toVisible()
                binding.clPayModeDetailsCl.toGone()
                binding.shimmerLayout.toGone()
            }
            is NewAddMoneyActivityVM.BankDetailsState.LoadViaBankTransfer -> {
                when (it.data.modeVisibility) {
                    Visibility.InVisible -> {
                        binding.virtualAccountInfoCv.toGone()

                    }
                    Visibility.Visible -> {
                        binding.virtualAccountInfoCv.toVisible()
                        binding.accountNumberTv.text = it.data.identifier2
                        binding.ifscCodeTv.text = it.data.identifier1
                    }
                }
            }
            is NewAddMoneyActivityVM.BankDetailsState.LoadViaPayU -> {
                when (it.data.modeVisibility) {
                    Visibility.InVisible -> {
                        binding.addViaDebitOrCardCv.toGone()
                    }
                    Visibility.Visible -> {
                        binding.addViaDebitOrCardCv.toVisible()
                    }
                }
            }
            is NewAddMoneyActivityVM.BankDetailsState.LoadViaUPIOrQRCode -> {
                binding.cvAddViaUpiError.toGone()
                when (it.data.modeVisibility) {
                    Visibility.InVisible -> {
                        binding.cvAddViaUpiIdOrQRCode.toGone()
                    }
                    Visibility.Visible -> {
                        binding.cvAddViaUpiIdOrQRCode.toVisible()
                        binding.tvYourUpiIdValue.text = it.data.identifier1
                    }
                }
            }
            NewAddMoneyActivityVM.BankDetailsState.Loading -> {
                binding.tvPaymentModeNotAvailable.toGone()
                binding.clPayModeDetailsCl.toGone()
                binding.shimmerLayout.toVisible()
            }
            null -> {}
            is NewAddMoneyActivityVM.BankDetailsState.QRCodeGenerated -> {
                binding.icQrcode.setImageBitmap(it.qrCode)
            }
            NewAddMoneyActivityVM.BankDetailsState.Success -> {
                binding.tvPaymentModeNotAvailable.toGone()
                binding.clPayModeDetailsCl.toVisible()
                binding.shimmerLayout.toGone()
            }
            NewAddMoneyActivityVM.BankDetailsState.UnableToGenerateUPIIdState -> {
                binding.cvAddViaUpiError.toVisible()
                binding.cvAddViaUpiIdOrQRCode.toGone()
            }
        }
    }

    private fun handelEvent(it: NewAddMoneyActivityVM.BankDetailsEvent?) {
        when (it) {
            NewAddMoneyActivityVM.BankDetailsEvent.AddViaPayUClicked -> {
                val intent = Intent(this, AddMoneyView::class.java)
                startActivity(intent)
            }
            NewAddMoneyActivityVM.BankDetailsEvent.OnIncreaseLimitClicked -> {

                val intent = Intent(this, UpgradeToKycInfoActivity::class.java).apply {
                    putExtra(
                        AppConstants.KYC_UPGRADE_FROM_WHICH_SCREEN,
                        NewAddMoneyActivity::class.java.simpleName
                    )
                }
                startActivity(intent)
            }
            null -> {}
            NewAddMoneyActivityVM.BankDetailsEvent.HowItWorks -> {
                SharedPrefUtils.getString(
                    this,
                    SharedPrefUtils.SF_ADD_MONEY_VIDEO_NEW
                )?.let {
                    val intent = Intent(this, VideoActivity2::class.java)
                    intent.putExtra(ARG_WEB_URL_TO_OPEN, it)
                    startActivity(intent)
                }
            }
            NewAddMoneyActivityVM.BankDetailsEvent.ShareQRCodeAndUpi -> {
                Utility.takeScreenShot(binding.icQrcode)?.let { it1 ->
                    shareScreenShotContent(
                        it1,
                        this,
                        "Here's my Fyp QR Code and UPI ID. Please send money directly to my Fyp account using any UPI apps-\n" +
                                "\n" +
                                "\n" +
                                "Link: https://fypmoney.in/add/${binding.tvYourUpiIdValue.text}" +
                                "\n" +
                                "\n" +
                                "UPI ID : ${binding.tvYourUpiIdValue.text}" +
                                "\n" +
                                "\n" +
                                "You can also get your own QR on Fyp, India's leading payments app, trusted by 1 Mn+ teens and families: https://fypmoney.in/app"
                    )
                }

            }
            NewAddMoneyActivityVM.BankDetailsEvent.ShareVirtualAccountInfo -> {
                val content =
                    "Here are my Fyp bank account details. Please send money directly to my account using any UPI apps or NEFT/IMPS-\n" +
                            "\n" +
                            "Bank Account No.:  ${binding.accountNumberTv.text} \n" +
                            "\n" +
                            "IFSC Code: ${binding.ifscCodeTv.text} \n" +
                            "\n" +
                            "Download Fyp, India's leading payments app, trusted by 1 Mn+ teens and families: https://fypmoney.in/app"
                onInviteUser(content)
            }
            NewAddMoneyActivityVM.BankDetailsEvent.CopyAccountNumber -> {
                if (!binding.accountNumberTv.text.isNullOrEmpty()) {
                    onCopyClicked(binding.accountNumberTv.text.toString(), this)
                }
            }
            NewAddMoneyActivityVM.BankDetailsEvent.CopyIFSC -> {
                if (!binding.ifscCodeTv.text.isNullOrEmpty()) {
                    onCopyClicked(binding.ifscCodeTv.text.toString(), this)
                }
            }
            NewAddMoneyActivityVM.BankDetailsEvent.CopyUPIId -> {
                if (!binding.tvYourUpiIdValue.text.isNullOrEmpty()) {
                    onCopyClicked(binding.tvYourUpiIdValue.text.toString(), this)
                }
            }
            NewAddMoneyActivityVM.BankDetailsEvent.WatchAddMoneyVideo -> {
                val videoLink = SharedPrefUtils.getString(
                    this,
                    SharedPrefUtils.SF_ADD_MONEY_VIDEO_NEW
                )
                if (!videoLink.isNullOrEmpty()) {
                    val intent = Intent(this, VideoActivity2::class.java)
                    intent.putExtra(ARG_WEB_URL_TO_OPEN, videoLink)
                    startActivity(intent)
                }
            }
            NewAddMoneyActivityVM.BankDetailsEvent.ReloadAddViaUPI -> {

            }
        }
    }

    private fun checkUpgradeKycStatus(): Boolean {
        SharedPrefUtils.getString(PockketApplication.instance, SharedPrefUtils.SF_KYC_TYPE)?.let {
            return it != AppConstants.MINIMUM
        } ?: run {
            return true
        }

    }

    private fun onCopyClicked(textToCopy: String, context: Context) {
        Utility.copyTextToClipBoard(
            context.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager,
            textToCopy
        )
    }
}