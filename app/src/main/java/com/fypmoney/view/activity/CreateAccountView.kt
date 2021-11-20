package com.fypmoney.view.activity

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.CompoundButton
import android.widget.RadioGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.fypmoney.R
import com.fypmoney.BR
import com.fypmoney.base.BaseActivity
import com.fypmoney.databinding.ViewCreateAccountBinding
import com.fypmoney.model.CustomerInfoResponseDetails
import com.fypmoney.util.AppConstants
import com.fypmoney.util.Utility
import com.fypmoney.viewmodel.CreateAccountViewModel
import kotlinx.android.synthetic.main.screen_home.*
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.android.synthetic.main.toolbar.toolbar
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewBinding = getViewDataBinding()
        setToolbarAndTitle(
            context = this@CreateAccountView,
            toolbar = toolbar,
            isBackArrowVisible = true
        )
        teenagerSelected()
        lin_parent.setOnClickListener(View.OnClickListener {

            parentSelected()

        })
        rad_teenger.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                teenagerSelected()
            } else {
                parentSelected()

            }
        }
        rad_parent.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                parentSelected()
            } else {
                teenagerSelected()
            }
        }

        lin_teenager.setOnClickListener(View.OnClickListener {

            teenagerSelected()
        })
        btnCreateAccount.backgroundTintList =
            ContextCompat.getColorStateList(applicationContext, R.color.buttonUnselectedColor)
        setObserver()
    }

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
        mViewModel.teenager.value = 2
    }

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

                        btnCreateAccount.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(
                            this,
                            R.color.black
                        ))
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
                intentToActivity(CreateAccountSuccessView::class.java)
                mViewModel.onUpdateProfileSuccess.value = false
            }
        }
        mViewModel.firstName.observe(this) {
            if (!TextUtils.isEmpty(mViewModel.firstName.value)) {
                if (!TextUtils.isEmpty(mViewModel.lastName.value)) {

                        mViewModel.isEnabled.value=true

                }

            }
            else{
                btnCreateAccount.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(
                    this,
                    R.color.buttonUnselectedColor
                ))
                btnCreateAccount.setTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.text_color_little_dark
                    )
                )

            }
        }
        mViewModel.lastName.observe(this) {
            if (!TextUtils.isEmpty(mViewModel.lastName.value)) {
                if (!TextUtils.isEmpty(mViewModel.firstName.value)) {

                    mViewModel.isEnabled.value=true

                }

            }
            else{
                btnCreateAccount.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(
                    this,
                    R.color.buttonUnselectedColor
                ))
                btnCreateAccount.setTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.text_color_little_dark
                    )
                )

            }
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

    override fun onDateSelected(dateOnEditText: String, dateOnServer: String, yearDifference: Int) {
        mViewModel.dob.value = dateOnEditText
        mViewModel.dobForServer.value = dateOnServer
    }


    /**
     * Method to navigate to the different activity
     */
    private fun intentToActivity(aClass: Class<*>) {
        startActivity(Intent(this@CreateAccountView, aClass))
        finish()
    }


}
