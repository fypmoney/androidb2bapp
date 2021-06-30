package com.fypmoney.view.activity

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseActivity
import com.fypmoney.databinding.ViewUserProfileBinding
import com.fypmoney.util.SharedPrefUtils
import com.fypmoney.util.Utility
import com.fypmoney.view.adapter.MyProfileListAdapter
import com.fypmoney.view.fragment.LogoutBottomSheet
import com.fypmoney.viewmodel.UserProfileViewModel
import kotlinx.android.synthetic.main.toolbar.*


/*
* This class is used as Home Screen
* */
class UserProfileView : BaseActivity<ViewUserProfileBinding, UserProfileViewModel>(),
    MyProfileListAdapter.OnListItemClickListener, LogoutBottomSheet.OnLogoutClickListener {
    private lateinit var mViewModel: UserProfileViewModel
    private lateinit var mViewBinding: ViewUserProfileBinding

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.view_user_profile
    }

    override fun getViewModel(): UserProfileViewModel {
        mViewModel = ViewModelProvider(this).get(UserProfileViewModel::class.java)
        return mViewModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewBinding = getViewDataBinding()
        setToolbarAndTitle(
            context = this@UserProfileView,
            toolbar = toolbar,
            isBackArrowVisible = true, toolbarTitle = getString(R.string.my_profile_title)
        )

        try {
            mViewModel.buildVersion.set(
                applicationContext.packageManager
                    .getPackageInfo(applicationContext.packageName, 0).versionName
            )
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()

        }
        // set name
        mViewModel.userName.set(
            SharedPrefUtils.getString(
                applicationContext,
                SharedPrefUtils.SF_KEY_USERNAME
            )
        )
        val myProfileAdapter = MyProfileListAdapter(applicationContext, this)
        mViewBinding.profileList.adapter = myProfileAdapter

        val iconList = ArrayList<Int>()
        iconList.add(R.drawable.privacy)
        iconList.add(R.drawable.interest)
        iconList.add(R.drawable.help)
        iconList.add(R.drawable.logout)

        myProfileAdapter.setList(
            iconList1 = iconList,
            resources.getStringArray(R.array.my_profile_title_list).toMutableList()
        )

        setObserver()
    }

    /**
     * Create this method for observe the viewModel fields
     */
    private fun setObserver() {
        mViewModel.onLogoutSuccess.observe(this) {
            intentToActivity(LoginView::class.java, isFinishAll = true)
        }

    }


    /**
     * Method to navigate to the different activity
     */
    private fun intentToActivity(aClass: Class<*>, isFinishAll: Boolean? = false) {
        val intent = Intent(this@UserProfileView, aClass)
        startActivity(intent)
        if (isFinishAll == true) {
            finishAffinity()
        }
    }

    override fun onItemClick(position: Int) {
        when (position) {
            0 -> {
                Utility.goToAppSettings(applicationContext)
            }

            1 -> {
                intentToActivity(SelectInterestView::class.java)
            }

            3 -> {
                callLogOutBottomSheet()
            }

        }
    }


    /*
    * This method is used to call log out
    * */
    private fun callLogOutBottomSheet() {
        val bottomSheet =
            LogoutBottomSheet(this)
        bottomSheet.dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.RED))
        bottomSheet.show(supportFragmentManager, "LogOutBottomSheet")
    }

    override fun onLogoutButtonClick() {
        mViewModel.callLogOutApi()
    }

}
