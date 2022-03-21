package com.fypmoney.view.activity

import android.Manifest
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.text.TextUtils
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.fyp.trackr.models.TrackrEvent
import com.fyp.trackr.models.trackr
import com.fyp.trackr.services.TrackrServices
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseActivity
import com.fypmoney.databinding.ViewCreateAccountBinding
import com.fypmoney.util.AppConstants
import com.fypmoney.util.Utility
import com.fypmoney.view.home.main.homescreen.view.HomeActivity
import com.fypmoney.view.register.PanAdhaarSelectionActivity
import com.fypmoney.viewmodel.CreateAccountViewModel
import kotlinx.android.synthetic.main.screen_home.*
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.android.synthetic.main.toolbar_animation.*
import kotlinx.android.synthetic.main.view_create_account.*
import kotlinx.android.synthetic.main.view_enter_otp.*

/*
* This is used to handle create account functionality
* */
class CreateAccountView :
    BaseActivity<ViewCreateAccountBinding, CreateAccountViewModel>(), Utility.OnDateSelected {
    private lateinit var mViewModel: CreateAccountViewModel
    private lateinit var mViewBinding: ViewCreateAccountBinding

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.view_create_account
    }

    override fun getViewModel(): CreateAccountViewModel {
        mViewModel = ViewModelProvider(this).get(CreateAccountViewModel::class.java)
        return mViewModel
    }

    private var isNewUser: Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewBinding = getViewDataBinding()

        isNewUser = if (intent.hasExtra(AppConstants.USER_TYPE_NEW)) {
            intent.getBooleanExtra(AppConstants.USER_TYPE_NEW, false)
        } else {
            false
        }
        if (intent.hasExtra(AppConstants.USER_TYPE)) {
            when (intent.getStringExtra(AppConstants.USER_TYPE)) {
                "Teenager" -> {
                    mViewModel.teenager.value = 1
                }
                else -> {
                    mViewModel.teenager.value = 2

                }
            }
        }

        if (isNewUser) {
            // this method help us to hide or un hide items
            setLottieAnimationToolBar(
                isBackArrowVisible = isNewUser,//back arrow visibility
                isLottieAnimation = isNewUser,// lottie animation visibility
                imageView = ivToolBarBack,//back image view
                lottieAnimationView = ivAnimationGift,
                        screenName = CreateAccountView::class.java.simpleName

            )// lottie animation view
        } else {
            // this method help us to hide or un hide items
            setLottieAnimationToolBar(
                isBackArrowVisible = !isNewUser,//back arrow visibility
                isLottieAnimation = isNewUser,// lottie animation visibility
                imageView = ivToolBarBack,//back image view
                lottieAnimationView = ivAnimationGift,
                screenName = CreateAccountView::class.java.simpleName

            )// lottie animation view
        }



        btnCreateAccount.backgroundTintList =
            ContextCompat.getColorStateList(applicationContext, R.color.buttonUnselectedColor)
        setObserver()


    }
/*

    private fun teenagerSelected() {
        rad_parent.isChecked = false
        rad_teenger.isChecked = true
        lin_teenager.background =
            ContextCompat.getDrawable(this, R.drawable.round_rectangle_50_grey_stroke)
        lin_parent.background =
            ContextCompat.getDrawable(this, R.drawable.round_rectangle_50_grey_light)

        mViewModel.teenager.value = 1
    }

    private fun parentSelected() {
        rad_parent.isChecked = true
        rad_teenger.isChecked = false
        lin_teenager.background =
            ContextCompat.getDrawable(this, R.drawable.round_rectangle_50_grey_light)
        lin_parent.background =
            ContextCompat.getDrawable(this, R.drawable.round_rectangle_50_grey_stroke)
//            teenager.setTextColor(ContextCompat.getColor(this, R.color.grey));
//            parent_selected.setTextColor(ContextCompat.getColor(this, R.color.black));
    }
*/

/*
Create this method for observe the viewModel fields
 */

    private fun setObserver() {
        mViewModel.onDobClicked.observe(this) {
            if (it) {
                try {
                    Utility.showDatePickerDialog(
                        context = this,
                        onDateSelected = this,
                        isDateOfBirth = true
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                mViewModel.onDobClicked.value = false
            }
        }
        mViewModel.isEnabled.observe(this) {
            if (it) {
                btnCreateAccount.backgroundTintList = ColorStateList.valueOf(
                    ContextCompat.getColor(
                        this,
                        R.color.black
                    )
                )
                btnCreateAccount.setTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.white
                    )
                )
            }
        }
        mViewModel.onUpdateProfileSuccess.observe(this) {
            if (it) {
                if (Utility.getCustomerDataFromPreference()?.bankProfile?.isAccountActive == AppConstants.NO)
                    intentToActivity(PanAdhaarSelectionActivity::class.java)
                else {
                    if (hasPermissions(this, Manifest.permission.READ_CONTACTS)) {
                        intentToActivity(HomeActivity::class.java)
                    } else {
                        intentToActivity(PermissionsActivity::class.java)
                    }


                }
                trackr {
                    it.services = arrayListOf(TrackrServices.ADJUST, TrackrServices.FIREBASE)
                    it.name = TrackrEvent.KYCCOMPLETD
                }

                mViewModel.onUpdateProfileSuccess.value = false
            }
        }
        mViewModel.firstName.observe(this) {
            setContinuousButtonEnable()
        }
        mViewModel.lastName.observe(this) {
            setContinuousButtonEnable()
        }
        mViewModel.emailId.observe(this) {
            setContinuousButtonEnable()
        }

//        mViewModel.dob.observe(this) {
//            if (!TextUtils.isEmpty(mViewModel.dob.value)) {
//                if (!TextUtils.isEmpty(mViewModel.firstName.value)) {
//                    if(!TextUtils.isEmpty(mViewModel.lastName.value)){
//                        mViewModel.isEnabled.value=true
//                    }
//                }
//
//            }
//            else{
//                btnCreateAccount.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(
//                    this,
//                    R.color.buttonUnselectedColor
//                ))
//                btnCreateAccount.setTextColor(
//                    ContextCompat.getColor(
//                        this,
//                        R.color.text_color_little_dark
//                    )
//                )
//            }
//        }


        mViewModel.onLoginClicked.observe(this) {
            if (it) {
                intentToActivity(FirstScreenView::class.java)
                mViewModel.onLoginClicked.value = false
            }
        }

    }

    private fun setContinuousButtonEnable() {
        if (
            !TextUtils.isEmpty(mViewModel.firstName.value) &&
            !TextUtils.isEmpty(mViewModel.lastName.value) &&
            !TextUtils.isEmpty(mViewModel.emailId.value)
        ) {
            mViewModel.isEnabled.value = true
        } else {
            btnCreateAccount.backgroundTintList = ColorStateList.valueOf(
                ContextCompat.getColor(
                    this,
                    R.color.buttonUnselectedColor
                )
            )
            btnCreateAccount.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.text_color_little_dark
                )
            )

        }
    }

    override fun onDateSelected(dateOnEditText: String, dateOnServer: String, yearDifference: Int) {
        mViewModel.dob.value = dateOnEditText
        mViewModel.dobForServer.value = dateOnServer
    }


    /**
     * Method to navigate to the different activity
     */
    private fun intentToActivity(aClass: Class<*>) {
        startActivity(Intent(this@CreateAccountView, aClass))
        finishAffinity()
    }


}
