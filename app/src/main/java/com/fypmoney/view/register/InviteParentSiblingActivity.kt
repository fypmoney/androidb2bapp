package com.fypmoney.view.register

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseActivity
import com.fypmoney.databinding.ActivityInviteParentSiblingBinding
import com.fypmoney.util.Utility
import com.fypmoney.view.register.viewModel.InviteParentSiblingVM
import com.fypmoney.view.activity.NotificationView
import com.fypmoney.view.activity.UserProfileView

class InviteParentSiblingActivity :
    BaseActivity<ActivityInviteParentSiblingBinding, InviteParentSiblingVM>() {

    private lateinit var binding: ActivityInviteParentSiblingBinding
    private val inviteParentSiblingVM by viewModels<InviteParentSiblingVM> { defaultViewModelProviderFactory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = getViewDataBinding()

        observeEvents()

        binding.continueBtn.setOnClickListener {


            if (!binding.phoneEd.text.isNullOrEmpty()) {
                if (binding.phoneEd.text.toString().length != 10) {
                    Utility.showToast("Enter a valid number")
                } else {

                    inviteParentSiblingVM.callIsAppUserApi(binding.phoneEd.text.toString())

                }
            } else {
                Utility.showToast("Enter mobile number")
            }


        }


    }


    private fun observeEvents() {
        inviteParentSiblingVM.event.observe(this, {
            handelEvents(it)
        })
        inviteParentSiblingVM.user.observe(this, {

            if (it.isAppUserResponseDetails.isAppUser == true) {
                val intent = Intent(this, SelectRelationActivity::class.java)
                intent.putExtra("phone", binding.phoneEd.text.toString())
                intent.putExtra("name_user", it.isAppUserResponseDetails.name)
                val bndlAnimation = ActivityOptions.makeCustomAnimation(
                    applicationContext,
                    com.fypmoney.R.anim.slideinleft,
                    com.fypmoney.R.anim.slideinright
                ).toBundle()
                startActivity(intent, bndlAnimation)


            } else {
                Utility.showToast("Not a fyp user")
            }

        })
    }

    private fun handelEvents(it: InviteParentSiblingVM.HomeActivityEvent?) {
        when (it) {
            InviteParentSiblingVM.HomeActivityEvent.NotificationClicked -> {
                startActivity(Intent(this, NotificationView::class.java))

            }
            InviteParentSiblingVM.HomeActivityEvent.ProfileClicked -> {
                startActivity(Intent(this, UserProfileView::class.java))
            }

        }
    }

    /**
     * Override for set binding variable
     *
     * @return variable id
     */
    override fun getBindingVariable(): Int = BR.viewModel

    /**
     * @return layout resource id
     */
    override fun getLayoutId(): Int = R.layout.activity_invite_parent_sibling

    /**
     * Override for set view model
     *
     * @return view model instance
     */
    override fun getViewModel(): InviteParentSiblingVM = inviteParentSiblingVM


}