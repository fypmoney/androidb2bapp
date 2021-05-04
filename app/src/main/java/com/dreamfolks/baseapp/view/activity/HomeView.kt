package com.dreamfolks.baseapp.view.activity

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import com.dreamfolks.baseapp.BR
import com.dreamfolks.baseapp.R
import com.dreamfolks.baseapp.base.BaseActivity
import com.dreamfolks.baseapp.database.entity.ContactEntity
import com.dreamfolks.baseapp.databinding.ViewHomeBinding
import com.dreamfolks.baseapp.util.SharedPrefUtils
import com.dreamfolks.baseapp.util.Utility
import com.dreamfolks.baseapp.viewmodel.HomeViewModel
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
        //  Utility.getContactsNew(contentResolver)
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
                //    Utility.getAllContacts(contentResolver, mViewModel.contactRepository, this)
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
                //  Utility.showToast(getString(R.string.permission_required))
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

                    // Utility.getAllContacts(contentResolver, mViewModel.contactRepository, this)
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
      //  Log.d("contacts","step4_onAllContactsSynced_interface")

        var newList = ArrayList<String>()
        contactEntity?.forEach {
            newList.add(it.firstName + "     " + it.contactNumber)

        }

        val adapter: ArrayAdapter<String> = ArrayAdapter<String>(
            this,
            android.R.layout.simple_dropdown_item_1line, newList
        )
        mobile_list.setAdapter(adapter)
        mViewModel.callContactSyncApi()
    }


}
