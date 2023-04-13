package com.fypmoney.view.activity

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.fypmoney.BR
import com.fypmoney.R

import com.fypmoney.base.BaseActivity
import com.fypmoney.databinding.ViewNotificationBinding
import com.fypmoney.model.NotificationModel
import com.fypmoney.model.SendMoneyResponseDetails
import com.fypmoney.model.UpdateTaskGetResponse
import com.fypmoney.util.AppConstants
import com.fypmoney.util.SharedPrefUtils
import com.fypmoney.util.Utility
import com.fypmoney.view.AddMoneySuccessBottomSheet
import com.fypmoney.view.contacts.model.CONTACT_ACTIVITY_UI_MODEL
import com.fypmoney.view.contacts.model.ContactActivityActionEvent
import com.fypmoney.view.contacts.model.ContactsActivityUiModel
import com.fypmoney.view.contacts.view.PayToContactsActivity
import com.fypmoney.view.fragment.*
import com.fypmoney.view.home.main.homescreen.view.HomeActivity
import com.fypmoney.view.interfaces.AcceptRejectClickListener
import com.fypmoney.view.interfaces.MessageSubmitClickListener
import com.fypmoney.view.kycagent.ui.KycAgentActivity
import com.fypmoney.viewmodel.NotificationViewModel
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.android.synthetic.main.view_notification.*


/*
* This is used to show list of notification
* */

const val FOR_APPRICATE_AND_PAY = "appricate_and_pay"
const val FOR_REQUEST_AND_PAY = "request_and_pay"
class NotificationView : BaseActivity<ViewNotificationBinding, NotificationViewModel>(),
    FamilyNotificationBottomSheet.OnBottomSheetClickListener,
    RequestMoneyBottomSheet.OnRequestMoneyBottomSheetClickListener {
    private var commentstr: String? = null
    private var choresModel: NotificationModel.NotificationResponseDetails? = null
    private var bottomsheetInsufficient: TaskMessageInsuficientFuntBottomSheet? = null
    private var bottomSheet: TaskActionBottomSheetnotificationactivity? = null
    private var taskMessageBottomSheet3: TaskMessageBottomSheet3? = null
    private var bottomSheetMessage: TaskMessageBottomSheet2? = null
    private var deviceSecurityAskedFor: String? = null
    private var actionAllowed: String? = null

    companion object {
        lateinit var mViewModel: NotificationViewModel
    }

    private val tabIcons = intArrayOf(
        R.drawable.ic_timeline_tab,
        R.drawable.ic_request_tab_noti,

        )

    private lateinit var mViewBinding: ViewNotificationBinding
    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.view_notification
    }

    override fun getViewModel(): NotificationViewModel {
        mViewModel = ViewModelProvider(this).get(NotificationViewModel::class.java)
        return mViewModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewBinding = getViewDataBinding()
        setToolbarAndTitle(
            context = this@NotificationView,
            toolbar = toolbar,
            isBackArrowVisible = true, toolbarTitle = getString(R.string.notification_text)
        )
        mViewBinding.shimmerLayout.startShimmer()
        setObserver()
        SharedPrefUtils.putString(applicationContext, SharedPrefUtils.SF_KEY_NEW_MESSAGE, null)
        initializeTabs(tabLayout)
    }

    private fun initializeTabs(tabLayout: TabLayout) {


        val adapter = ChoresActivity.ViewPagerAdapter(supportFragmentManager)

        adapter.addFragment(NotiTimelineFragment(), getString(R.string.timeline))
        adapter.addFragment(NotiRequestFragment(), getString(R.string.requests))


        viewPager.adapter = adapter
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.tabRippleColor = null;
        setupTabIcons()


    }

    private fun setupTabIcons() {
        tabLayout.getTabAt(0)?.setIcon(tabIcons[0])
        tabLayout.getTabAt(1)?.setIcon(tabIcons[1])

    }

    /**
     * Create this method for observe the viewModel fields
     */
    private fun setObserver() {
        mViewModel.onNotificationClicked.observe(this) {
            if (it) {
                when (mViewModel.notificationSelectedResponse.requestCategoryCode) {

                    AppConstants.NOTIFICATION_TYPE_ADD_FAMILY -> {
                        callBottomSheet(mViewModel.notificationSelectedResponse)
                    }
                    AppConstants.NOTIFICATION_TYPE_REQUEST_MONEY -> {
                        callRequestMoneyBottomSheet()
                    }
                    AppConstants.NOTIFICATION_TYPE_ADD_TASK -> {

                        if (mViewModel.notificationSelectedResponse.actionAllowed == "REJECT,ACCEPT" || mViewModel.notificationSelectedResponse.actionAllowed == "CANCEL" || mViewModel.notificationSelectedResponse.actionAllowed == "DEPRECIATE,APPRECIATEANDPAY" || mViewModel.notificationSelectedResponse.actionAllowed == "COMPLETE" || mViewModel.notificationSelectedResponse.actionAllowed == "") {

                            if (mViewModel.notificationSelectedResponse.actionAllowed == "" && mViewModel.notificationSelectedResponse.actionAllowed == "DEPRECIATE") {
                                callTaskMessageSheet(mViewModel.notificationSelectedResponse)
                            } else {
                                callTaskActionSheet(mViewModel.notificationSelectedResponse)
                            }
                        } else if (mViewModel.notificationSelectedResponse.actionAllowed == "CANCEL") {
                            callTaskMessageSheet(mViewModel.notificationSelectedResponse)
                        }
                    }
                }

                mViewModel.onNotificationClicked.value = false
            }

        }

        mViewModel.sendMoneyApiResponse.observe(this) {
            callTransactionSuccessBottomSheet(it)
        }
        mViewModel.showShimmerEffect.observe(this) {
            if (!it) {
                mViewBinding.shimmerLayout.stopShimmer()
            }

        }
        mViewModel.error.observe(this, { errorcode ->
            if (errorcode == AppConstants.INSUFFICIENT_ERROR_CODE) {
                callInsuficientFundMessageSheet(Utility.convertToRs(mViewModel.amountToBeAdded))
            }


        })
        mViewModel!!.bottomSheetStatus.observe(this, androidx.lifecycle.Observer { status ->
            bottomSheetMessage?.dismiss()
            taskMessageBottomSheet3?.dismiss()
            bottomSheet?.dismiss()
            mViewModel.onRefresh()

            if (status.currentState == "ACCEPT") {

                callTaskMessageSheet(status)
            }
            if (status.currentState == "REJECT") {

                callTaskMessageSheet(status)
            }
            if (status.currentState == "CANCEL") {

                callTaskMessageSheet(status)
            }
            if (status.currentState == "COMPLETE") {

                callTaskMessageSheet(status)
            }
            if (status.currentState == "DEPRECIATE") {

                callTaskMessageSheet(status)
            }
            if (status.currentState == "APPRECIATEANDPAY") {

                callTaskMessageSheet(status)
            }
        })
    }


    private fun callTransactionSuccessBottomSheet(sendMoneyResponse: SendMoneyResponseDetails) {
        val bottomSheet =
            mViewModel.notificationSelectedResponse.additionalAttributes?.amount?.let {
                Utility.convertToRs(sendMoneyResponse.currentBalance)?.let { it1 ->
                    Utility.convertToRs(it)?.let { it2 ->
                        AddMoneySuccessBottomSheet(
                            it2,
                            it1,onViewDetailsClick=null,successTitle = "Payment Made Successfully to ${sendMoneyResponse.receiverName}",onHomeViewClick = {
                                intentToActivity(KycAgentActivity::class.java)
                            }
                        )
                    }
                }
            }
        bottomSheet?.dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.RED))
        bottomSheet?.show(supportFragmentManager, "TransactionSuccess")
    }


    private fun callTaskActionSheet(list: NotificationModel.NotificationResponseDetails) {
        val itemClickListener2 = object : AcceptRejectClickListener {
            override fun onAcceptClicked(pos: Int, str: String) {
                if (pos == 56) {
                    choresModel = list
                    commentstr = str
                    deviceSecurityAskedFor  = FOR_APPRICATE_AND_PAY
                    askForDevicePassword()

                }


            }

            override fun onRejectClicked(pos: Int) {

            }

            override fun ondimiss() {

            }
        }
        bottomSheet =
            TaskActionBottomSheetnotificationactivity(itemClickListener2, list)
        bottomSheet?.dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.RED))
        bottomSheet?.show(supportFragmentManager, "TASKACCEPTREJECT")
    }

    private fun callTaskMessageSheet(list: NotificationModel.NotificationResponseDetails) {
        val itemClickListener2 = object : MessageSubmitClickListener {
            override fun onSubmit() {
                taskMessageBottomSheet3?.dismiss()
            }

        }
        taskMessageBottomSheet3 =
            TaskMessageBottomSheet3(itemClickListener2, list, list.entityId)
        taskMessageBottomSheet3?.dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.RED))
        taskMessageBottomSheet3?.show(supportFragmentManager, "TASKMESSAGE")
    }
    private fun callInsuficientFundMessageSheet(amount: String?) {
        val itemClickListener2 = object : AcceptRejectClickListener {
            override fun onAcceptClicked(pos: Int, str: String) {
                bottomsheetInsufficient?.dismiss()
                val intent = Intent(this@NotificationView, PayToContactsActivity::class.java)
                intent.putExtra(
                    CONTACT_ACTIVITY_UI_MODEL, ContactsActivityUiModel(toolBarTitle = getString(R.string.pay),
                        showLoadingBalance = true,contactClickAction = ContactActivityActionEvent.PayToContact)
                )
                startActivity(intent)
            }

            override fun onRejectClicked(pos: Int) {
                bottomsheetInsufficient?.dismiss()
                callActivity(AddMoneyView::class.java, amount)
            }

            override fun ondimiss() {

            }
        }
        bottomsheetInsufficient =
            TaskMessageInsuficientFuntBottomSheet(
                itemClickListener2, title = resources.getString(R.string.insufficient_bank_balance),
                subTitle = resources.getString(R.string.insufficient_bank_body),
                amount = resources.getString(R.string.add_money_title1) + resources.getString(R.string.Rs) + amount
            )
        bottomsheetInsufficient?.dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.RED))
        bottomsheetInsufficient?.show(supportFragmentManager, "TASKMESSAGE")
    }
    private fun intentToPayActivity(aClass: Class<*>, pay: String? = null) {
        val intent = Intent(this, aClass)
        intent.putExtra(AppConstants.FROM_WHICH_SCREEN, pay)
        startActivity(intent)
    }


    private fun callActivity(aClass: Class<*>, amount: String?) {
        val intent = Intent(this, aClass)
        intent.putExtra("amountshouldbeadded", amount)
        startActivity(intent)
    }
    private fun callTaskMessageSheet(list: UpdateTaskGetResponse) {
        var itemClickListener2 = object : MessageSubmitClickListener {

            override fun onSubmit() {
                bottomSheetMessage?.dismiss()
            }
        }
        bottomSheetMessage =
            TaskMessageBottomSheet2(itemClickListener2, list, list.id?.toString())
        bottomSheetMessage?.dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.RED))
        bottomSheetMessage?.show(supportFragmentManager, "TASKMESSAGE")
    }

    override fun onBottomSheetButtonClick(actionAllowed: String?) {
        mViewModel.callUpdateApprovalRequestApi(actionAllowed!!)


    }

    override fun onRequestMoneyBottomSheetButtonClick(actionAllowed: String?) {
        deviceSecurityAskedFor  = FOR_REQUEST_AND_PAY
        this.actionAllowed = actionAllowed
        askForDevicePassword()

    }

    /*
    * This method is used to call activity
    * */
    private fun intentToActivity(aClass: Class<*>) {
        startActivity(Intent(this@NotificationView, aClass))
        finish()
    }
    /*
* This method is used to call leave member
* */
    private fun callBottomSheet(notificationResponse: NotificationModel.NotificationResponseDetails?) {
        val bottomSheet =
            FamilyNotificationBottomSheet(
                notificationResponse?.actionAllowed,
                notificationResponse?.description,
                notificationResponse?.isApprovalProcessed,
                notificationResponse,
                this
            )
        bottomSheet.dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.RED))
        bottomSheet.show(supportFragmentManager, "FamilyNotification")
    }





    /*
   * This method is used to call leave member
   * */
    private fun callRequestMoneyBottomSheet() {
        val bottomSheet =
            RequestMoneyBottomSheet(
                response = mViewModel.notificationSelectedResponse,
                this
            )
        bottomSheet.dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.RED))
        bottomSheet.show(supportFragmentManager, "RequestMoneyNotification")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            AppConstants.DEVICE_SECURITY_REQUEST_CODE -> {
                when (resultCode) {
                    RESULT_OK -> {
                        when(deviceSecurityAskedFor){
                            FOR_APPRICATE_AND_PAY->{

                                if (commentstr == null) {
                                    commentstr = ""
                                }
                                mViewModel.callTaskAccept(
                                    "APPRECIATEANDPAY", choresModel?.entityId.toString(), commentstr!!
                                )
                            }
                            FOR_REQUEST_AND_PAY->{
                                mViewModel.callPayMoneyApi(actionAllowed!!)
                            }
                        }


                    }

                }
            }
        }
    }

}
