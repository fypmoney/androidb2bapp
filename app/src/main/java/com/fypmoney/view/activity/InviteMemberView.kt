package com.fypmoney.view.activity

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseActivity
import com.fypmoney.databinding.ViewStayTunedBinding
import com.fypmoney.viewmodel.InviteMemberViewModel
import kotlinx.android.synthetic.main.toolbar.*

/*
* This class is used to invite the family member
* */
class InviteMemberView : BaseActivity<ViewStayTunedBinding, InviteMemberViewModel>() {
    private lateinit var mViewModel: InviteMemberViewModel

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.view_invite_member
    }

    override fun getViewModel(): InviteMemberViewModel {
        mViewModel = ViewModelProvider(this).get(InviteMemberViewModel::class.java)
        return mViewModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setToolbarAndTitle(
            context = this@InviteMemberView,
            toolbar = toolbar,
            isBackArrowVisible = true
        )
        setObserver()

    }

    /**
     * Create this method for observe the viewModel fields
     */
    private fun setObserver() {
        mViewModel.onInviteClicked.observe(this)
        {
            if (it) {
                inviteUser()
                mViewModel.onInviteClicked.value = false
            }

        }
    }
}