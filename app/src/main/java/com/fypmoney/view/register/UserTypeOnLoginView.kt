package com.fypmoney.view.register

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.fyp.trackr.models.TrackrEvent
import com.fyp.trackr.models.TrackrField
import com.fyp.trackr.models.trackr
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseActivity
import com.fypmoney.bindingAdapters.setBackgroundDrawable
import com.fypmoney.databinding.ActivityOnboardingUserTypeBinding
import com.fypmoney.util.AppConstants
import com.fypmoney.util.Utility
import com.fypmoney.view.activity.CreateAccountView
import com.fypmoney.view.register.viewModel.UserTypeOnLoginViewModel
import kotlinx.android.synthetic.main.toolbar_animation.*
import java.util.*

/*
* This class is used to handle account activation via aadhaar card
* */
class UserTypeOnLoginView :
    BaseActivity<ActivityOnboardingUserTypeBinding, UserTypeOnLoginViewModel>() {
    private lateinit var mViewModel: UserTypeOnLoginViewModel
    private lateinit var mViewBinding: ActivityOnboardingUserTypeBinding


    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_onboarding_user_type
    }

    override fun getViewModel(): UserTypeOnLoginViewModel {
        mViewModel = ViewModelProvider(this).get(UserTypeOnLoginViewModel::class.java)
        return mViewModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewBinding = getViewDataBinding()
        setObserver()
        setLottieAnimationToolBar(
            isBackArrowVisible = false,//back arrow visibility
            isLottieAnimation = true,// lottie animation visibility
            imageView = ivToolBarBack,//back image view
            lottieAnimationView = ivAnimationGift,
            screenName = UserTypeOnLoginView::class.java.simpleName

        )
    }

     private fun setObserver() {
         var userTypeSelected:String="NoUser"
         mViewModel.isGuardianClick.observe(this) {
             setSelectedUserType("Parent")
             userTypeSelected="Parent"

         }
         mViewModel.isTeenagerClicked.observe(this) {
             setSelectedUserType("Teenager")
             userTypeSelected="Teenager"

         }
         mViewModel.isContinueClick.observe(this) {
             if (userTypeSelected =="NoUser"){

                 Utility.showToast("Select user type")
             }else {
                 trackr {
                     it.name = TrackrEvent.cust_type_select
                     it.add(
                         TrackrField.cust_type, userTypeSelected.toLowerCase(Locale.ENGLISH))
                 }
                 val intent = Intent(this, CreateAccountView::class.java)
                 intent.putExtra(AppConstants.USER_TYPE_NEW, true)
                 intent.putExtra(AppConstants.USER_TYPE, userTypeSelected)
                 startActivity(intent)
             }
         }
     }

    private fun setSelectedUserType(type: String) {
        if (type == "Parent") {

            mViewBinding.ivUserTypeIcon.setImageDrawable(getDrawable(R.drawable.ic_teenager_icon))
            mViewBinding.ivUserTypeParentIcon.setImageDrawable(getDrawable(R.drawable.ic_guardian_selected))

            setBackgroundDrawable(
                mViewBinding.cvUserParent,
                resources.getColor(R.color.white),
                38f,
                resources.getColor(R.color.reward_golden_tickets_text),
                5f,
                false
            )

            setBackgroundDrawable(
                mViewBinding.cvUserTeenager,
                resources.getColor(R.color.white),
                38f,
                resources.getColor(R.color.white),
                5f,
                false
            )
        } else {
            mViewBinding.ivUserTypeIcon.setImageDrawable(getDrawable(R.drawable.ic_teenager_selected))
            mViewBinding.ivUserTypeParentIcon.setImageDrawable(getDrawable(R.drawable.ic_parent_usertype))

            setBackgroundDrawable(
                mViewBinding.cvUserParent,
                resources.getColor(R.color.white),
                38f,
                resources.getColor(R.color.white),
                5f,
                false
            )

            setBackgroundDrawable(
                mViewBinding.cvUserTeenager,
                resources.getColor(R.color.white),
                38f,
                resources.getColor(R.color.select_user_type_stock_color),
                5f,
                false
            )

        }

    }


}
