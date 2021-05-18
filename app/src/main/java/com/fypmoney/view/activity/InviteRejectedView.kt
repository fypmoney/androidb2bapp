package com.fypmoney.view.activity

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseActivity
import com.fypmoney.databinding.ViewInviteRejectedBinding
import com.fypmoney.databinding.ViewStayTunedBinding
import com.fypmoney.viewmodel.InviteMemberViewModel
import com.fypmoney.viewmodel.InviteRejectedViewModel

/*
* This class is used to invite the family member
* */
class InviteRejectedView : BaseActivity<ViewInviteRejectedBinding, InviteRejectedViewModel>() {
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
        setObserver()

    }

    /**
     * Create this method for observe the viewModel fields
     */
    private fun setObserver() {
        mViewModel.onRequestAgainClicked.observe(this)
        {
            if (it) {
                inviteUser()
                mViewModel.onRequestAgainClicked.value = false
            }

        }
    }
}