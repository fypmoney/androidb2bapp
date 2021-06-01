package com.fypmoney.view.activity

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseActivity
import com.fypmoney.databinding.ViewUserProfileBinding
import com.fypmoney.util.AppConstants
import com.fypmoney.util.SharedPrefUtils
import com.fypmoney.util.Utility
import com.fypmoney.view.adapter.MyProfileListAdapter
import com.fypmoney.viewmodel.UserProfileViewModel
import kotlinx.android.synthetic.main.toolbar.*


/*
* This class is used as Home Screen
* */
class UserProfileView : BaseActivity<ViewUserProfileBinding, UserProfileViewModel>(),
    MyProfileListAdapter.OnListItemClickListener {
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
            isBackArrowVisible = true
        )
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
        iconList.add(R.drawable.ic_profile)
        iconList.add(R.drawable.ic_profile)

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


    }

    /**
     * Method to navigate to the different activity
     */
    private fun intentToActivity(aClass: Class<*>) {
        val intent = Intent(this@UserProfileView, aClass)
        intent.putExtra(AppConstants.FROM_WHICH_SCREEN, AppConstants.LOGOUT)
        startActivity(intent)
        finishAffinity()
    }


    /**
     * Method to navigate to the different activity
     */
    private fun intentToFamilySettingsActivity(aClass: Class<*>) {
        val intent = Intent(this@UserProfileView, aClass)
        startActivity(intent)
    }

    override fun onItemClick(position: Int) {
        when (position) {
            0 -> {
                intentToFamilySettingsActivity(CommunityView::class.java)
            }
            1 -> {
                intentToFamilySettingsActivity(SelectInterestView::class.java)
            }
        }
    }

}
