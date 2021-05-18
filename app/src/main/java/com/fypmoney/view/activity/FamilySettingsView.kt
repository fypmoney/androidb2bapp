package com.fypmoney.view.activity

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseActivity
import com.fypmoney.databinding.ViewFamilySettingsBinding
import com.fypmoney.util.AppConstants
import com.fypmoney.viewmodel.FamilySettingsViewModel
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.android.synthetic.main.view_family_settings.*


/*
* This class is used as Home Screen
* */
class FamilySettingsView : BaseActivity<ViewFamilySettingsBinding, FamilySettingsViewModel>() {
    private lateinit var mViewModel: FamilySettingsViewModel
    private lateinit var mViewBinding: ViewFamilySettingsBinding

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.view_family_settings
    }

    override fun getViewModel(): FamilySettingsViewModel {
        mViewModel = ViewModelProvider(this).get(FamilySettingsViewModel::class.java)
        return mViewModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewBinding = getViewDataBinding()
        setToolbarAndTitle(
            context = this@FamilySettingsView,
            toolbar = toolbar,
            isBackArrowVisible = true
        )
        recycler_view.layoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.HORIZONTAL,
            true
        )
        mViewModel.callGetMemberApi()
        val lbm = LocalBroadcastManager.getInstance(this)
        lbm.registerReceiver(receiver, IntentFilter(AppConstants.AFTER_ADD_MEMBER_BROADCAST_NAME))

        setObserver()
    }

    var receiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            mViewModel.callGetMemberApi()
        }
    }

    /**
     * Create this method for observe the viewModel fields
     */
    private fun setObserver() {
        mViewModel.onViewAllClicked.observe(this) {
            if (it) {
                intentToActivity(MemberView::class.java)
                mViewModel.onViewAllClicked.value = false
            }
        }

        mViewModel.onAddMemberClicked.observe(this) {
            if (it) {
                intentToAddMemberActivity(AddMemberView::class.java)
                mViewModel.onAddMemberClicked.value = false
            }
        }

    }

    /**
     * Method to navigate to the different activity
     */
    private fun intentToActivity(aClass: Class<*>) {
        startActivity(Intent(this@FamilySettingsView, aClass))
    }

    /**
     * Method to navigate to the different activity
     */
    private fun intentToAddMemberActivity(aClass: Class<*>) {
        val intent = Intent(this@FamilySettingsView, aClass)
        intent.putExtra(AppConstants.FROM_WHICH_SCREEN, "")
        startActivity(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver)
    }


}
