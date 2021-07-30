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
import com.fypmoney.util.AppConstants
import com.fypmoney.view.fragment.FamilyNotificationBottomSheet
import com.fypmoney.view.fragment.RequestMoneyBottomSheet
import com.fypmoney.viewmodel.NotificationViewModel
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.android.synthetic.main.view_user_feeds.*

/*
* This is used to show list of notification
* */
class NotificationView : BaseActivity<ViewNotificationBinding, NotificationViewModel>(),
    FamilyNotificationBottomSheet.OnBottomSheetClickListener,
    RequestMoneyBottomSheet.OnRequestMoneyBottomSheetClickListener {
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
}
