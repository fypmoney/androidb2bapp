package com.fypmoney.view.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseActivity
import com.fypmoney.database.entity.ContactEntity
import com.fypmoney.databinding.ViewHomeBinding
import com.fypmoney.model.NotificationModel
import com.fypmoney.model.UpdateTaskGetResponse
import com.fypmoney.util.AppConstants
import com.fypmoney.util.SharedPrefUtils
import com.fypmoney.util.Utility
import com.fypmoney.view.fragment.*
import com.fypmoney.view.interfaces.AcceptRejectClickListener
import com.fypmoney.view.interfaces.MessageSubmitClickListener
import com.fypmoney.viewmodel.HomeViewModel
import kotlinx.android.synthetic.main.view_home.*


/*
* This class is used as Home Screen
* */
class HomeView : BaseActivity<ViewHomeBinding, HomeViewModel>(),
    Utility.OnAllContactsAddedListener, FamilyNotificationBottomSheet.OnBottomSheetClickListener,
    RequestMoneyBottomSheet.OnRequestMoneyBottomSheetClickListener {
    private var taskMessageBottomSheet3: TaskActionBottomSheetnotification? = null
    private var bottomSheetMessage: TaskMessageBottomSheet3? = null
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
        setCurrentFragment(HomeScreen())

        loadProfile(
            SharedPrefUtils.getString(
                applicationContext,
                SharedPrefUtils.SF_KEY_PROFILE_IMAGE
            )
        )
        when (intent.getStringExtra(AppConstants.FROM_WHICH_SCREEN)) {
            AppConstants.STAY_TUNED_BOTTOM_SHEET -> {
                mViewBinding.toolbar.setBackgroundColor( ContextCompat.getColor(this,R.color.white))
                mViewBinding.toolbarTitle.setTextColor(ContextCompat.getColor(this,R.color.black))
                mViewBinding.ivNotificationBell.setImageResource(R.drawable.ic_bell_icon_black)
                mViewModel.isScanVisible.set(false)
                mViewModel.headerText.set(
                    getString(
                        R.string.family_settings_toolbar_heading
                    )
                )
                setCurrentFragment(FamilySettingsView())

            }
            AppConstants.NOTIFICATION -> {
                mViewModel.callGetFamilyNotificationApi(intent.getStringExtra(AppConstants.NOTIFICATION_APRID))

            }

        }

        mViewBinding.navigationView.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.home -> {
                    mViewBinding.toolbar.setBackgroundColor( ContextCompat.getColor(this,R.color.text_color_dark))
                    mViewBinding.toolbarTitle.setTextColor(ContextCompat.getColor(this,R.color.text_color_dark))
                    mViewBinding.ivNotificationBell.setImageResource(R.drawable.ic_bell_icon)
                    mViewModel.isScanVisible.set(true)
                    mViewModel.headerText.set("")
                    setCurrentFragment(HomeScreen())
                }
                R.id.feeds -> {
                    mViewBinding.toolbar.setBackgroundColor( ContextCompat.getColor(this,R.color.white))
                    mViewBinding.toolbarTitle.setTextColor(ContextCompat.getColor(this,R.color.black))
                    mViewBinding.ivNotificationBell.setImageResource(R.drawable.ic_bell_icon_black)
                    mViewModel.isScanVisible.set(false)
                    mViewModel.headerText.set(getString(R.string.feeds_bottom_nav_title))
                    setCurrentFragment(UserFeedsView())
                }
                R.id.store -> {
                    mViewBinding.toolbar.setBackgroundColor( ContextCompat.getColor(this,R.color.white))
                    mViewBinding.toolbarTitle.setTextColor(ContextCompat.getColor(this,R.color.black))
                    mViewBinding.ivNotificationBell.setImageResource(R.drawable.ic_bell_icon_black)
                    mViewModel.isScanVisible.set(false)
                    mViewModel.headerText.set(getString(R.string.store_bottom_nav_title))
                    setCurrentFragment(StoreScreen())
                }
                R.id.card -> {
                    mViewBinding.toolbar.setBackgroundColor( ContextCompat.getColor(this,R.color.white))
                    mViewBinding.toolbarTitle.setTextColor(ContextCompat.getColor(this,R.color.black))
                    mViewBinding.ivNotificationBell.setImageResource(R.drawable.ic_bell_icon_black)
                    mViewModel.isScanVisible.set(false)
                    mViewModel.headerText.set(getString(R.string.card_details_title))
                    setCurrentFragment(CardScreen())
                }
                R.id.family -> {
                    mViewBinding.toolbar.setBackgroundColor( ContextCompat.getColor(this,R.color.white))
                    mViewBinding.toolbarTitle.setTextColor(ContextCompat.getColor(this,R.color.black))
                    mViewBinding.ivNotificationBell.setImageResource(R.drawable.ic_bell_icon_black)
                    mViewModel.isScanVisible.set(false)
                    mViewModel.headerText.set(
                        getString(
                            R.string.family_settings_toolbar_heading
                        )
                    )
                    setCurrentFragment(FamilySettingsView())
                }

            }
            true
        }

    }

    private fun loadProfile(url: String?) {
        url?.let {
            Glide.with(this).load(it).apply(RequestOptions().circleCrop()).into(myProfile)

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
        mViewModel.onQrCodeClicked.observe(this) {
            if (it) {
                intentToActivity(QrCodeScannerView::class.java)
                mViewModel.onQrCodeClicked.value = false
            }
        }
        mViewModel.onNotificationClicked.observe(this) {
            if (it) {
                intentToActivity(NotificationView::class.java)
                mViewModel.onNotificationClicked.value = false
            }
        }

        mViewModel.onNotificationListener.observe(this) {
            when (it?.requestCategoryCode) {
                AppConstants.NOTIFICATION_TYPE_ADD_FAMILY -> {
                    callBottomSheet(it)
                }
                AppConstants.NOTIFICATION_TYPE_REQUEST_MONEY -> {
                    callRequestMoneyBottomSheet()
                }
                AppConstants.NOTIFICATION_TYPE_ADD_TASK -> {
                    if (it.actionAllowed == "ACCEPTED,REJECTED") {
                        callTaskActionSheet(it)
                    } else if (it.actionAllowed == "CANCEL") {
                        callTaskMessageSheet(it)
                    }
                }
            }

        }
        mViewModel!!.bottomSheetStatus.observe(this, androidx.lifecycle.Observer { list ->
            bottomSheetMessage?.dismiss()
            taskMessageBottomSheet3?.dismiss()
            if (list.currentState == "ACCEPT") {

                callTaskMessageSheet(list)
            }
            if (list.currentState == "REJECT") {

                callTaskMessageSheet(list)
            }
            if (list.currentState == "CANCEL") {

                callTaskMessageSheet(list)
            }
        })

    }

    private fun callTaskMessageSheet(list: NotificationModel.NotificationResponseDetails?) {
        var itemClickListener2 = object : MessageSubmitClickListener {
            override fun onSubmit() {

            }

        }
        bottomSheetMessage =
            TaskMessageBottomSheet3(itemClickListener2, list, list?.entityId)
        bottomSheetMessage?.dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.RED))
        bottomSheetMessage?.show(supportFragmentManager, "TASKMESSAGE")
    }

    private fun callTaskMessageSheet(list: UpdateTaskGetResponse?) {
        var itemClickListener2 = object : MessageSubmitClickListener {
            override fun onSubmit() {

            }

        }
        val bottomSheet =
            TaskMessageBottomSheet2(itemClickListener2, list, list?.id?.toString())
        bottomSheet.dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.RED))
        bottomSheet.show(supportFragmentManager, "TASKMESSAGE")
    }


    private fun callTaskActionSheet(list: NotificationModel.NotificationResponseDetails?) {
        var itemClickListener2 = object : AcceptRejectClickListener {
            override fun onAcceptClicked(pos: Int) {
                mViewModel!!.callTaskAccept("ACCEPT", list?.entityId)


            }

            override fun onRejectClicked(pos: Int) {
                mViewModel!!.callTaskAccept("REJECT", list?.entityId)


            }

            override fun ondimiss() {

            }
        }
        taskMessageBottomSheet3 =
            TaskActionBottomSheetnotification(itemClickListener2, list)
        taskMessageBottomSheet3?.dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.RED))
        taskMessageBottomSheet3?.show(supportFragmentManager, "TASKACCEPTREJECT")
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
        when (checkPermission(Manifest.permission.READ_CONTACTS)) {
            true -> {
                Utility.getAllContactsInList(
                    contentResolver,
                    this,
                    contactRepository = mViewModel.contactRepository
                )
            }
            else -> {
                requestPermission(Manifest.permission.READ_CONTACTS)
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
                requestPermission(Manifest.permission.READ_CONTACTS)
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

    /*
  * This method is used to call leave member
  * */
    private fun callBottomSheet(notificationResponse: NotificationModel.NotificationResponseDetails?) {
        val bottomSheet =
            FamilyNotificationBottomSheet(
                notificationResponse?.actionAllowed,
                notificationResponse?.description,
                notificationResponse?.isApprovalProcessed,
                this
            )
        bottomSheet.dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.RED))
        bottomSheet.show(supportFragmentManager, "FamilyNotification")
    }


    override fun onBottomSheetButtonClick(actionAllowed: String?) {
        mViewModel.callUpdateApprovalRequestApi(actionAllowed!!)


    }


    /*
   * This method is used to call leave member
   * */
    private fun callRequestMoneyBottomSheet() {
        val bottomSheet =
            RequestMoneyBottomSheet(
                response = mViewModel.notificationSelectedResponse,
                this
            )
        bottomSheet.dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.RED))
        bottomSheet.show(supportFragmentManager, "RequestMoneyNotification")
    }

    override fun onRequestMoneyBottomSheetButtonClick(actionAllowed: String?) {
        mViewModel.callPayMoneyApi(actionAllowed!!)

    }

}

