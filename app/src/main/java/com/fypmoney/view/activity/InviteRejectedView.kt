package com.fypmoney.view.activity

import android.content.ClipboardManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseActivity
import com.fypmoney.databinding.ViewInviteRejectedBinding
import com.fypmoney.databinding.ViewStayTunedBinding
import com.fypmoney.view.fragment.InviteBottomSheet
import com.fypmoney.viewmodel.InviteMemberViewModel
import com.fypmoney.viewmodel.InviteRejectedViewModel
import kotlinx.android.synthetic.main.toolbar.*

/*
* This class is used to invite the family member
* */
class InviteRejectedView : BaseActivity<ViewInviteRejectedBinding, InviteRejectedViewModel>(),
    InviteBottomSheet.OnShareClickListener {
    private lateinit var mViewModel: InviteRejectedViewModel

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.view_invite_rejected
    }

    override fun getViewModel(): InviteRejectedViewModel {
        mViewModel = ViewModelProvider(this).get(InviteRejectedViewModel::class.java)
        return mViewModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setToolbarAndTitle(
            context = this@InviteRejectedView,
            toolbar = toolbar,
            isBackArrowVisible = true
        )
        setObserver()

    }

    /**
     * Create this method for observe the viewModel fields
     */
    private fun setObserver() {
        mViewModel.onRequestAgainClicked.observe(this)
        {
            if (it) {
                callInviteBottomSheet()
                mViewModel.onRequestAgainClicked.value = false
            }

        }

    }

    /*
* This method is used to call invite sheet
* */
    private fun callInviteBottomSheet() {
        val bottomSheet =
            InviteBottomSheet(getSystemService(CLIPBOARD_SERVICE) as ClipboardManager, this)
        bottomSheet.dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.RED))
        bottomSheet.show(supportFragmentManager, "InviteView")
    }

    override fun onShareClickListener(referralCode: String) {
        inviteUser()
    }
}
