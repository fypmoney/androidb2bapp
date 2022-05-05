package com.fypmoney.view.addmoney

import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import com.fyp.trackr.models.TrackrEvent
import com.fyp.trackr.models.trackr
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
import com.fypmoney.util.videoplayer.VideoActivity2
import com.fypmoney.view.activity.AddMoneyView
import com.fypmoney.view.addmoney.viewmodel.NewAddMoneyActivityVM
import com.fypmoney.view.fragment.MaxLoadAmountReachedWarningDialogFragment
import com.fypmoney.view.upgradetokyc.UpgradeToKycInfoActivity
import com.fypmoney.view.webview.ARG_WEB_URL_TO_OPEN
import kotlinx.android.synthetic.main.toolbar.*

class NewAddMoneyActivity : BaseActivity<ActivityNewAddMoneyBinding,NewAddMoneyActivityVM>() {

    private val newAddMoneyActivityVM by viewModels<NewAddMoneyActivityVM>()

    private var videoLink: String?=null

    private lateinit var binding:ActivityNewAddMoneyBinding

    /**
     * Override for set binding variable
     *
     * @return variable id
     */
    override fun getBindingVariable(): Int  = BR.viewModel

    /**
     * @return layout resource id
     */
    override fun getLayoutId(): Int  = R.layout.activity_new_add_money

    /**
     * Override for set view model
     *
     * @return view model instance
     */
    override fun getViewModel(): NewAddMoneyActivityVM  = newAddMoneyActivityVM

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = getViewDataBinding()
        setToolbarAndTitle(
            context = this@NewAddMoneyActivity,
            toolbar = toolbar,
            isBackArrowVisible = true, toolbarTitle = getString(R.string.add_money_screen_title)
        )
        videoLink = SharedPrefUtils.getString(
            this,
            SharedPrefUtils.SF_ADD_MONEY_VIDEO_NEW
        )

        setBindings()
        setObserver()
        setUpVirtualAccountInfo()
        binding.howItWorksTv.setOnClickListener {
            SharedPrefUtils.getString(
                this,
                SharedPrefUtils.SF_ADD_MONEY_VIDEO_NEW
            )?.let {
                val intent = Intent(this, VideoActivity2::class.java)
                intent.putExtra(ARG_WEB_URL_TO_OPEN, it)
                startActivity(intent)
            }


        }
    }

    private fun setUpVirtualAccountInfo() {
        Utility.getCustomerDataFromPreference()?.let {
            if(it.bankProfile?.virtualAccountNo.isNullOrEmpty() &&
                it.bankProfile?.virtualAccountIfscCode.isNullOrEmpty()){
                binding.virtualAccountInfoCv.toGone()
            }else{
                binding.virtualAccountInfoCv.toVisible()
                binding.accountNumberTv.text = it.bankProfile?.virtualAccountNo
                binding.ifscCodeTv.text = it.bankProfile?.virtualAccountIfscCode
            }
        }
    }

    override fun onStart() {
        super.onStart()
        if (checkUpgradeKycStatus()) {
            binding.increaseLimitTv.toGone()
        } else {
            binding.increaseLimitTv.toVisible()

        }
    }
    private fun setBindings() {

        binding.video.setOnClickListener {
            if (!videoLink.isNullOrEmpty()) {
                val intent = Intent(this, VideoActivity2::class.java)
                intent.putExtra(ARG_WEB_URL_TO_OPEN, videoLink)
                startActivity(intent)
            }

        }
        binding.copyBankAc.setOnClickListener {
            if(!binding.accountNumberTv.text.isNullOrEmpty()){
                onCopyClicked(binding.accountNumberTv.text.toString(),this)
            }
        }
        binding.copyIfscIv.setOnClickListener {
            if(!binding.ifscCodeTv.text.isNullOrEmpty()){
                onCopyClicked(binding.ifscCodeTv.text.toString(),this)
            }
        }
        binding.shareVirtualAccountDetailsIv.setOnClickListener {
            val content = "Here are my Fyp bank account details. Please send money directly to my account using any UPI apps or NEFT/IMPS-\n" +
                    " Bank Account No.:  ${binding.accountNumberTv.text} \n" +
                    "\n"+
                    " IFSC Code: ${binding.ifscCodeTv.text} \n" +
                    "\n"+
                    " Download Fyp, India's leading payments app, trusted by 1 Mn+ teens and families: https://fypmoney.in/app"
            onInviteUser(content)
        }
        binding.addViaDebitOrCardCv.setOnClickListener {
            val intent  = Intent(this, AddMoneyView::class.java)
            startActivity(intent)
        }
    }
    private fun setObserver() {


        newAddMoneyActivityVM.increseLimitClicked.observe(this) {
            if (it) {
                trackr {it1->
                    it1.name = TrackrEvent.increase_limit_clicked
                }
                val intent  = Intent(this, UpgradeToKycInfoActivity::class.java).apply {
                    putExtra(AppConstants.KYC_UPGRADE_FROM_WHICH_SCREEN,NewAddMoneyActivity::class.java.simpleName)
                }
                startActivity(intent)
                newAddMoneyActivityVM.increseLimitClicked.value = false
            }
        }

        newAddMoneyActivityVM.maxLoadLimitReached.observe(this) {
            if (it) {
                val showMaxLoadAmountReachedWarningDialogFragment = MaxLoadAmountReachedWarningDialogFragment()
                showMaxLoadAmountReachedWarningDialogFragment.show(supportFragmentManager, "showMaxLoadAmountReachedWarningDialogFragment")
                newAddMoneyActivityVM.maxLoadLimitReached.value = false
            }
        }

    }

    private fun checkUpgradeKycStatus():Boolean{
        SharedPrefUtils.getString(PockketApplication.instance, SharedPrefUtils.SF_KYC_TYPE)?.let {
            return it != AppConstants.MINIMUM
        }?:run {
            return true
        }

    }

    fun onCopyClicked(textToCopy:String,context: Context) {
        Utility.copyTextToClipBoard(
            context.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager,
            textToCopy
        )
    }
}