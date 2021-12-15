package com.fypmoney.view.register

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseActivity
import com.fypmoney.databinding.ActivityInviteParentSiblingBinding
import com.fypmoney.util.AppConstants
import com.fypmoney.util.Utility
import com.fypmoney.view.activity.ChooseInterestRegisterView
import com.fypmoney.view.register.viewModel.InviteParentSiblingVM
import com.fypmoney.view.activity.NotificationView
import com.fypmoney.view.activity.UserProfileView
import kotlinx.android.synthetic.main.toolbar_animation.*

class InviteParentSiblingActivity :
    BaseActivity<ActivityInviteParentSiblingBinding, InviteParentSiblingVM>() {

    private var userType: String? = "90"
    private lateinit var binding: ActivityInviteParentSiblingBinding
    private val inviteParentSiblingVM by viewModels<InviteParentSiblingVM> { defaultViewModelProviderFactory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = getViewDataBinding()
        setLottieAnimationToolBar(
            isBackArrowVisible = false,//back arrow visibility
            isLottieAnimation = true,// lottie animation visibility
            imageView = ivToolBarBack,//back image view
            lottieAnimationView = ivAnimationGift
        )// lottie anima
        observeEvents()
        userType = intent.getStringExtra(AppConstants.USER_TYPE)

        if (userType == "90") {
            binding.skip.text = getString(R.string.skip_title)
        } else {
            binding.skip.text = getString(R.string.i_want_to_use)
        }
        binding.skip.setOnClickListener(View.OnClickListener {
            if (binding.skip.text == getString(R.string.i_want_to_use)) {
                val intent = Intent(this, WaitlistAprovalActivity::class.java)
                val bndlAnimation = ActivityOptions.makeCustomAnimation(
                    applicationContext,
                    com.fypmoney.R.anim.slideinleft,
                    com.fypmoney.R.anim.slideinright
                ).toBundle()
                startActivity(intent, bndlAnimation)
                finish()

            } else if (binding.skip.text == getString(R.string.skip_title)) {
                val intent = Intent(this, ChooseInterestRegisterView::class.java)
                val bndlAnimation = ActivityOptions.makeCustomAnimation(
                    applicationContext,
                    com.fypmoney.R.anim.slideinleft,
                    com.fypmoney.R.anim.slideinright
                ).toBundle()
                startActivity(intent, bndlAnimation)
                finish()

            }
        })

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

        inviteParentSiblingVM.user.observe(this, {

            if (it.isAppUserResponseDetails.isAppUser == true) {
                val intent = Intent(this, SelectRelationActivity::class.java)
                intent.putExtra("phone", binding.phoneEd.text.toString())
                intent.putExtra("name_user", it.isAppUserResponseDetails.name)
                intent.putExtra(AppConstants.USER_TYPE, it.isAppUserResponseDetails.name)


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