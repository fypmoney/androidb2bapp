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
import com.fypmoney.model.CustomerInfoResponse
import com.fypmoney.model.FeedDetails
import com.fypmoney.util.AppConstants
import com.fypmoney.util.AppConstants.FEED_RESPONSE
import com.fypmoney.view.fragment.AddMoneyTransactionFailBottomSheet
import com.fypmoney.view.fragment.FamilyNotificationBottomSheet
import com.fypmoney.viewmodel.NotificationViewModel
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.android.synthetic.main.view_user_feeds.*

/*
* This is used to show list of notification
* */
class NotificationView : BaseActivity<ViewNotificationBinding, NotificationViewModel>(),
    FamilyNotificationBottomSheet.OnBottomSheetClickListener {
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
                when (mViewModel.notificationSelectedResponse.get()?.requestCategoryCode) {
                    AppConstants.NOTIFICATION_TYPE_ADD_FAMILY -> {
                        callBottomSheet()
                    }
                    AppConstants.NOTIFICATION_TYPE_ADD_TASK -> {

                    }
                }

                mViewModel.onNotificationClicked.value = false
            }

        }
    }


    /*
    * This method is used to call leave member
    * */
    private fun callBottomSheet() {
        val bottomSheet =
            FamilyNotificationBottomSheet(
                mViewModel.notificationSelectedResponse.get()?.actionAllowed,
                mViewModel.notificationSelectedResponse.get()?.description,
                this
            )
        bottomSheet.dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.RED))
        bottomSheet.show(supportFragmentManager, "FamilyNotification")
    }


    override fun onBottomSheetButtonClick(actionAllowed: String?) {
        mViewModel.callUpdateApprovalRequestApi(actionAllowed!!)


    }

}
