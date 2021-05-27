package com.fypmoney.view.activity

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseActivity
import com.fypmoney.database.entity.MemberEntity
import com.fypmoney.databinding.ViewMemberBinding
import com.fypmoney.util.AppConstants
import com.fypmoney.view.fragment.LeaveMemberBottomSheetFragment
import com.fypmoney.viewmodel.MemberViewModel
import kotlinx.android.synthetic.main.toolbar.*


/*
* This class is used as Home Screen
* */
class MemberView : BaseActivity<ViewMemberBinding, MemberViewModel>(),
    LeaveMemberBottomSheetFragment.OnBottomSheetClickListener {
    private lateinit var mViewModel: MemberViewModel
    private lateinit var mViewBinding: ViewMemberBinding

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.view_member
    }

    override fun getViewModel(): MemberViewModel {
        mViewModel = ViewModelProvider(this).get(MemberViewModel::class.java)
        return mViewModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewBinding = getViewDataBinding()
        setToolbarAndTitle(
            context = this@MemberView,
            toolbar = toolbar,
            isBackArrowVisible = true
        )
        val lbm = LocalBroadcastManager.getInstance(this)
        lbm.registerReceiver(receiver, IntentFilter(AppConstants.AFTER_ADD_MEMBER_BROADCAST_NAME))


        setObserver()
    }

    var receiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            mViewModel.memberAdapter.setList(mViewModel.memberRepository.getAllMembersFromDatabase())
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver)
    }

    /**
     * Create this method for observe the viewModel fields
     */
    private fun setObserver() {
        mViewModel.onSendLinkClicked.observe(this)
        {
            if (it) {
                inviteUser()
                mViewModel.onSendLinkClicked.value = false
            }

        }

        mViewModel.onLeaveMemberClicked.observe(this)
        {
            when (it) {
                R.id.btnLeave -> {
                    callLeaveRemoveBottomSheet(
                        mViewModel.memberDetailsData.get(),
                        AppConstants.LEAVE_MEMBER
                    )
                }
               /* R.id.removeIcon -> {
                    callLeaveRemoveBottomSheet(
                        mViewModel.memberDetailsData.get(),
                        AppConstants.REMOVE_MEMBER
                    )
                }
*/

            }

        }
        mViewModel.onAddMemberClicked.observe(this) {
            if (it) {
                intentToActivity(AddMemberView::class.java)
                mViewModel.onAddMemberClicked.value = false
            }
        }
        mViewModel.onLeaveMemberSuccess.observe(this) {
            if (it) {
                callBroadCast()
                mViewModel.onLeaveMemberSuccess.value = false
            }
        }


    }

    /*
    * This method is used to call leave member
    * */
    private fun callLeaveRemoveBottomSheet(memberEntity: MemberEntity?, type: String) {
        val bottomSheet =
            LeaveMemberBottomSheetFragment(
                memberEntity,
                type, this
            )
        bottomSheet.dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.RED))
        bottomSheet.show(supportFragmentManager, "LeaveMemberBottomSheet")
    }


    /**
     * Method to navigate to the different activity
     */
    private fun intentToActivity(aClass: Class<*>) {
        val intent = Intent(this@MemberView, aClass)
        intent.putExtra(AppConstants.FROM_WHICH_SCREEN, "")
        startActivity(intent)
    }

    override fun onBottomSheetButtonClick(type: String) {
        when (type) {
            AppConstants.LEAVE_MEMBER -> {
                mViewModel.callLeaveFamilyApi()
            }
            AppConstants.REMOVE_MEMBER -> {
                mViewModel.callRemoveMemberApi()

            }
        }
    }

    /*
  * This method is used to call the Broadcast receiver
  * */
    private fun callBroadCast() {
        LocalBroadcastManager.getInstance(applicationContext)
            .sendBroadcast(Intent(AppConstants.AFTER_ADD_MEMBER_BROADCAST_NAME))
    }


}
