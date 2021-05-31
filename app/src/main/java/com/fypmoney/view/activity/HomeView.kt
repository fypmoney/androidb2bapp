package com.fypmoney.view.activity

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseActivity
import com.fypmoney.database.entity.ContactEntity
import com.fypmoney.databinding.ViewHomeBinding
import com.fypmoney.util.AppConstants
import com.fypmoney.util.SharedPrefUtils
import com.fypmoney.util.Utility
import com.fypmoney.view.fragment.AddMemberScreen
import com.fypmoney.view.fragment.CardScreen
import com.fypmoney.view.fragment.HomeScreen
import com.fypmoney.viewmodel.HomeViewModel
import kotlinx.android.synthetic.main.view_home.*


/*
* This class is used as Home Screen
* */
class HomeView : BaseActivity<ViewHomeBinding, HomeViewModel>(),
    Utility.OnAllContactsAddedListener {
    private lateinit var mViewModel: HomeViewModel
    private lateinit var mViewBinding: ViewHomeBinding

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.view_home
    }

    override fun getViewModel(): HomeViewModel {
        mViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        return mViewModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewBinding = getViewDataBinding()
        setObserver()
        checkAndAskPermission()



        if (intent.getStringExtra(AppConstants.FROM_WHICH_SCREEN) == "stay_tuned") {
            setCurrentFragment(FamilySettingsView())
        } else {
            setCurrentFragment(HomeScreen())
        }

        mViewBinding.navigationView.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.home -> {
                    mViewModel.isScanVisible.set(true)
                    mViewModel.headerText.set("")
                    setCurrentFragment(HomeScreen())
                }
                R.id.feeds -> {
                    mViewModel.isScanVisible.set(false)
                    mViewModel.headerText.set(getString(R.string.feeds_bottom_nav_title))
                    setCurrentFragment(UserFeedsView())
                }
                R.id.store -> {
                    mViewModel.isScanVisible.set(false)
                    mViewModel.headerText.set(getString(R.string.store_bottom_nav_title))
                    setCurrentFragment(HomeScreen())
                }
                R.id.card -> {
                    mViewModel.isScanVisible.set(false)
                    mViewModel.headerText.set(getString(R.string.card_details_title))
                    setCurrentFragment(CardScreen())
                }
                R.id.family -> {
                    mViewModel.isScanVisible.set(false)
                    mViewModel.headerText.set(getString(
                            R.string.family_settings_toolbar_heading
                        )
                    )
                    setCurrentFragment(FamilySettingsView())
                }

            }
            true
        }

    }

    /**
     * Create this method for observe the viewModel fields
     */
    private fun setObserver() {
        mViewModel.onFeedClicked.observe(this) {
            if (it) {
                intentToActivity(UserFeedsView::class.java)
                mViewModel.onFeedClicked.value = false
            }
        }

        mViewModel.onLocationClicked.observe(this) {
            if (it) {
                intentToActivity(PermissionView::class.java)
                mViewModel.onLocationClicked.value = false
            }
        }
        mViewModel.onProfileClicked.observe(this) {
            if (it) {
                intentToActivity(UserProfileView::class.java)
                mViewModel.onProfileClicked.value = false
            }
        }
        mViewModel.onNotificationClicked.observe(this) {
            if (it) {
                intentToActivity(NotificationView::class.java)
                mViewModel.onNotificationClicked.value = false
            }
        }

    }

    /**
     * Method to navigate to the different activity
     */
    private fun intentToActivity(aClass: Class<*>) {
        startActivity(Intent(this@HomeView, aClass))
    }

    /*
 * This method is used to check for permissions
 * */
    private fun checkAndAskPermission() {
        when (checkPermission()) {
            true -> {
                Utility.getAllContactsInList(
                    contentResolver,
                    this,
                    contactRepository = mViewModel.contactRepository
                )
            }
            else -> {
                requestPermission()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        for (permission in permissions) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                //denied
                requestPermission()
            } else {
                if (ActivityCompat.checkSelfPermission(
                        this,
                        permission
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    //allow
                    var list = Utility.getAllContactsInList(
                        contentResolver,
                        this,
                        contactRepository = mViewModel.contactRepository
                    )

                } else {
                    //set to never ask again
                    SharedPrefUtils.putBoolean(
                        applicationContext,
                        SharedPrefUtils.SF_KEY_STORAGE_PERMANENTLY_DENY,
                        true
                    )
                }
            }
        }


    }

    override fun onAllContactsSynced(contactEntity: MutableList<ContactEntity>?) {
        mViewModel.callContactSyncApi()
    }

    private fun setCurrentFragment(fragment: Fragment) =
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.container, fragment)
            commit()
        }

}

