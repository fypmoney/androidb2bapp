package com.fypmoney.view.activity

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseActivity
import com.fypmoney.databinding.ViewNotificationBinding
import com.fypmoney.model.NotificationModel
import com.fypmoney.model.UpdateTaskGetResponse
import com.fypmoney.util.AppConstants
import com.fypmoney.view.fragment.*
import com.fypmoney.view.interfaces.AcceptRejectClickListener
import com.fypmoney.view.interfaces.MessageSubmitClickListener
import com.fypmoney.viewmodel.NotificationViewModel
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.android.synthetic.main.view_user_feeds.*

/*
* This is used to show list of notification
* */
class NotificationView : BaseActivity<ViewNotificationBinding, NotificationViewModel>(),
    FamilyNotificationBottomSheet.OnBottomSheetClickListener,
    RequestMoneyBottomSheet.OnRequestMoneyBottomSheetClickListener {
    private var bottomSheet: TaskActionBottomSheetnotificationactivity? = null
    private var taskMessageBottomSheet3: TaskMessageBottomSheet3? = null
    private var bottomSheetMessage: TaskMessageBottomSheet2? = null
    private lateinit var mViewModel: NotificationViewModel
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
        toolbar_image.visibility = View.VISIBLE
        setToolbarAndTitle(
            context = this@NotificationView,
            toolbar = toolbar,
            isBackArrowVisible = true, toolbarTitle = getString(R.string.notification_text)
        )
        setObserver()
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

                        if (mViewModel.notificationSelectedResponse.actionAllowed == "REJECT,ACCEPT") {
                            callTaskActionSheet(mViewModel.notificationSelectedResponse.entityId)
                        } else if (mViewModel.notificationSelectedResponse.actionAllowed == "CANCEL") {
                            callTaskMessageSheet(mViewModel.notificationSelectedResponse)
                        }
                    }
                }

                mViewModel.onNotificationClicked.value = false
            }

        }

        mViewModel.onPaySuccess.observe(this) {
            if (it) {
                intentToActivity(NotificationView::class.java)
                mViewModel.onPaySuccess.value = false
            }

        }
        mViewModel!!.bottomSheetStatus.observe(this, androidx.lifecycle.Observer { list ->
            bottomSheetMessage?.dismiss()
            taskMessageBottomSheet3?.dismiss()
            bottomSheet?.dismiss()
            if (list.currentState == "ACCEPT") {

                callTaskMessageSheet(list)
            }
            if (list.currentState == "REJECT") {

                callTaskMessageSheet(list)
            }
            if (list.currentState == "CANCEL") {

                callTaskMessageSheet(list)
            }
        })
    }


    private fun callTaskActionSheet(list: String?) {
        var itemClickListener2 = object : AcceptRejectClickListener {
            override fun onAcceptClicked(pos: Int) {
                mViewModel!!.callTaskAccept("ACCEPT", list)


            }

            override fun onRejectClicked(pos: Int) {
                mViewModel!!.callTaskAccept("REJECT", list)


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
        var itemClickListener2 = object : MessageSubmitClickListener {
            override fun onSubmit() {
                taskMessageBottomSheet3?.dismiss()
            }

        }
        taskMessageBottomSheet3 =
            TaskMessageBottomSheet3(itemClickListener2, list, list.entityId)
        taskMessageBottomSheet3?.dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.RED))
        taskMessageBottomSheet3?.show(supportFragmentManager, "TASKMESSAGE")
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
        mViewModel.callPayMoneyApi(actionAllowed!!)

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



}
