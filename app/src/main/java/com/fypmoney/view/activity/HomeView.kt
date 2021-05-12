package com.fypmoney.view.activity

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseActivity
import com.fypmoney.database.entity.ContactEntity
import com.fypmoney.databinding.ViewHomeBinding
import com.fypmoney.util.SharedPrefUtils
import com.fypmoney.util.Utility
import com.fypmoney.viewmodel.HomeViewModel
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
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
                intentToActivity(FamilyNotificationView::class.java)
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


}
