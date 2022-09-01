package com.fypmoney.view.activity

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.fyp.trackr.models.UserTrackr
import com.fyp.trackr.models.logOut
import com.fypmoney.BR
import com.fypmoney.BuildConfig
import com.fypmoney.R
import com.fypmoney.base.BaseActivity
import com.fypmoney.bindingAdapters.loadImage
import com.fypmoney.databinding.ViewUserNewProfileBinding
import com.fypmoney.util.AppConstants
import com.fypmoney.util.AppConstants.KYC_UPGRADE_FROM_WHICH_SCREEN
import com.fypmoney.util.SharedPrefUtils
import com.fypmoney.util.Utility
import com.fypmoney.view.adapter.GlobalListAdapter
import com.fypmoney.view.adapter.ListUiModel
import com.fypmoney.view.community.SocialCommunityActivity
import com.fypmoney.view.discord.DiscordInviteActivity
import com.fypmoney.view.discord.DiscordProfileActivity
import com.fypmoney.view.fragment.LogoutBottomSheet
import com.fypmoney.view.pocketmoneysettings.ui.PocketMoneySettingsFragment
import com.fypmoney.view.register.PanAdhaarSelectionActivity
import com.fypmoney.view.upgradetokyc.UpgradeToKycInfoActivity
import com.fypmoney.view.whatsappnoti.ui.NotificationSettingsFragment
import com.fypmoney.viewmodel.UserProfileViewModel
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.yalantis.ucrop.util.FileUtils.getPath
import kotlinx.android.synthetic.main.toolbar.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.IOException


/*
* This class is used as Home Screen
* */
class UserProfileView : BaseActivity<ViewUserNewProfileBinding, UserProfileViewModel>(), LogoutBottomSheet.OnLogoutClickListener {
    private lateinit var mViewModel: UserProfileViewModel
    private lateinit var mViewBinding: ViewUserNewProfileBinding

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.view_user_new_profile
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
            toolbar = toolbar, backArrowTint = Color.WHITE,
            titleColor = Color.WHITE,
            isBackArrowVisible = true, toolbarTitle = getString(R.string.my_profile_title)
        )

        loadProfile(
            SharedPrefUtils.getString(
                applicationContext,
                SharedPrefUtils.SF_KEY_PROFILE_IMAGE
            )
        )

        mViewBinding.playStoreTv.setOnClickListener {
            try {
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("market://details?id=$packageName")
                    )
                )
            } catch (e: ActivityNotFoundException) {
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/apps/details?id=${BuildConfig.APPLICATION_ID}")
                    )
                )
            }
        }
        mViewModel.setInitialData()
        try {
            mViewModel.buildVersion.set(
                applicationContext.packageManager
                    .getPackageInfo(applicationContext.packageName, 0).versionName
            )
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()

        }

        mViewBinding.profileList.adapter = GlobalListAdapter(this@UserProfileView,
            onListItemClicked = {
            when (it.postion) {

                0 ->{
                    val mainFragment = NotificationSettingsFragment()
                    supportFragmentManager.beginTransaction().add(R.id.container, mainFragment)
                        .commit()
                }

                1 -> {
                    startActivity(
                        Intent(
                            this@UserProfileView,
                            BankTransactionHistoryView::class.java
                        )
                    )
                }

                2 -> {
                    Utility.goToAppSettings(applicationContext)
                }

                3 -> {
                    val dicordconnected =
                        SharedPrefUtils.getString(
                            application,
                            SharedPrefUtils.SF_DICORD_CONNECTED
                        )
                    if (!dicordconnected.isNullOrEmpty() && dicordconnected == "connected") {

                        intentToActivityMain(
                            this@UserProfileView,
                            DiscordProfileActivity::class.java
                        )
                    } else {
                        startActivity(Intent(this, DiscordInviteActivity::class.java))


                    }
                    }

                4 -> {
                    intentToActivityMain(this@UserProfileView, SocialCommunityActivity::class.java)
                }
                5 -> {
                    openWebPageFor(
                        getString(R.string.privacy_policy),
                        "https://www.fypmoney.in/fyp/privacy-policy/"
                    )
                }
                6 -> {
                    openWebPageFor(
                        getString(R.string.terms_and_conditions),
                        "https://www.fypmoney.in/fyp/terms-of-use/"
                    )
                }

                7 -> {
                    callFreshChat(applicationContext)
                }

                8 -> {
                    callLogOutBottomSheet()
                }
                9->{
                    addFragmentToActivity()
                }
                10 -> {
                    addPocketFragmentToActivity()
                }
            }

        })

        mViewModel.callGetCustomerProfileApi()


        setObserver()
    }

    private fun addFragmentToActivity(){
        val fm = supportFragmentManager
        val tr = fm.beginTransaction()
        tr.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left,R.anim.slide_in_left,R.anim.slide_out_righ);
        tr.add(R.id.container, FamilySettingsView())
        tr.addToBackStack("family")
        tr.commit()
        //mViewBinding.container.toVisible()
    }

    private fun addPocketFragmentToActivity(){
        val fm = supportFragmentManager
        val tr = fm.beginTransaction()
        tr.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left,R.anim.slide_in_left,R.anim.slide_out_righ);
        tr.add(R.id.container, PocketMoneySettingsFragment())
        tr.addToBackStack("pocket")
        tr.commit()
    }

    override fun onStart() {
        super.onStart()
        (mViewBinding.profileList.adapter as GlobalListAdapter).submitList(prepareOptions())

    }
    private fun prepareOptions(): ArrayList<ListUiModel> {

        val iconList = ArrayList<ListUiModel>()
        iconList.add(
            ListUiModel(
                postion = 0,
                name = getString(R.string.notification_settings),
                icon = AppCompatResources.getDrawable(this, R.drawable.ic_whats_app)
            )
        )
        iconList.add(
            ListUiModel(
                postion = 1,
                name = getString(R.string.trans_history_heading),
                icon = AppCompatResources.getDrawable(this, R.drawable.ic_account_statement)
            )
        )
        iconList.add(
            ListUiModel(
                postion = 2,
                name = getString(R.string.privacy_settings),
                icon = AppCompatResources.getDrawable(this, R.drawable.ic_profile_privacy_settings)
            )
        )

       val discoredConnected=  SharedPrefUtils.getString(
            application,
            SharedPrefUtils.SF_DICORD_CONNECTED
        )
        if(!discoredConnected.isNullOrEmpty() && (discoredConnected == "connected")){
            iconList.add(
                ListUiModel(
                    postion = 3,
                    name = getString(R.string.discord_profile),
                    icon = AppCompatResources.getDrawable(this, R.drawable.ic_discord_profile)
                )
            )
        }else{
            iconList.add(
                ListUiModel(
                    postion = 3,
                    name = getString(R.string.connect_to_discord),
                    icon = AppCompatResources.getDrawable(this, R.drawable.ic_discord_profile)
                )
            )
        }

        iconList.add(
            ListUiModel(
                postion = 4,
                name = getString(R.string.community_settings),
                icon = AppCompatResources.getDrawable(this, R.drawable.ic_community)
            )
        )
        iconList.add(
            ListUiModel(
                postion = 5,
                name = getString(R.string.privacy_policy),
                icon = AppCompatResources.getDrawable(this, R.drawable.ic_privacy)
            )
        )
        iconList.add(
            ListUiModel(
                postion = 6,
                name = getString(R.string.t_n_c),
                icon = AppCompatResources.getDrawable(this, R.drawable.ic_term_and_condition)
            )
        )
        iconList.add(
            ListUiModel(
                postion = 7,
                name = getString(R.string.help),
                icon = AppCompatResources.getDrawable(this, R.drawable.ic_help)
            )
        )
        iconList.add(
            ListUiModel(
                postion = 8,
                name = getString(R.string.log_out),
                icon = AppCompatResources.getDrawable(this, R.drawable.ic_log_out)
            )
        )

        iconList.add(
            ListUiModel(
                postion = 9,
                name = getString(R.string.family_settings_screen_title),
                icon = AppCompatResources.getDrawable(this, R.drawable.ic_add_family_member)
            )
        )
        iconList.add(
            ListUiModel(
                postion = 10,
                name = getString(R.string.pocket_money_settings_screen_title),
                icon = AppCompatResources.getDrawable(this, R.drawable.ic_pocket_settings)
            )
        )

        return iconList
    }

    /**
     * Create this method for observe the viewModel fields
     */
    private fun setObserver() {
        mViewModel.onLogoutSuccess.observe(this) {
            UserTrackr.logOut()
            intentToActivityMain(this@UserProfileView,LoginView::class.java, isFinishAll = true)
        }

        mViewModel.onProfileSuccess.observe(this) {
            if (it) {
                loadProfile(
                    SharedPrefUtils.getString(
                        applicationContext,
                        SharedPrefUtils.SF_KEY_PROFILE_IMAGE
                    )
                )
                mViewModel.onProfileSuccess.value = false
            }
            // loadProfile(uri.toString())

        }
        mViewModel.onProfileClicked.observe(this) {
            if (it) {
                Dexter.withActivity(this)
                    .withPermissions(
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )
                    .withListener(object : MultiplePermissionsListener {
                        override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                            if (report.areAllPermissionsGranted()) {
                                showImagePickerOptions()
                            }
                            if (report.isAnyPermissionPermanentlyDenied) {
                                showSettingsDialog()
                            }
                        }

                        override fun onPermissionRationaleShouldBeShown(
                            permissions: List<PermissionRequest>,
                            token: PermissionToken
                        ) {
                            token.continuePermissionRequest()
                        }
                    }).check()
                mViewModel.onProfileClicked.value = false
            }
        }
        mViewModel.onUpgradeKycClicked.observe(this) {
            if (it) {
                Utility.getCustomerDataFromPreference()?.let {
                    if(it.postKycScreenCode.isNullOrEmpty()){
                        val intent = Intent(this, PanAdhaarSelectionActivity::class.java)
                        startActivity(intent)
                    }else{
                        val intent  = Intent(this,UpgradeToKycInfoActivity::class.java).apply {
                            putExtra(KYC_UPGRADE_FROM_WHICH_SCREEN,UserProfileView::class.java.simpleName)
                        }
                        startActivity(intent)
                    }
                }

                mViewModel.onUpgradeKycClicked.value = false
            }
        }

    }

    private fun showImagePickerOptions() {
        ImagePickerView.showImagePickerOptions(
            this,
            object : ImagePickerView.PickerOptionListener {
                override fun onTakeCameraSelected() {
                    launchCameraIntent()
                }

                override fun onChooseGallerySelected() {
                    launchGalleryIntent()
                }
            })
    }

    private fun launchCameraIntent() {
        val intent = Intent(this@UserProfileView, ImagePickerView::class.java)
        intent.putExtra(
            ImagePickerView.INTENT_IMAGE_PICKER_OPTION,
            ImagePickerView.REQUEST_IMAGE_CAPTURE
        )

        // setting aspect ratio
        intent.putExtra(ImagePickerView.INTENT_LOCK_ASPECT_RATIO, true)
        intent.putExtra(ImagePickerView.INTENT_ASPECT_RATIO_X, 1) // 16x9, 1x1, 3:4, 3:2
        intent.putExtra(ImagePickerView.INTENT_ASPECT_RATIO_Y, 1)

        // setting maximum bitmap width and height
        intent.putExtra(ImagePickerView.INTENT_SET_BITMAP_MAX_WIDTH_HEIGHT, true)
        intent.putExtra(ImagePickerView.INTENT_BITMAP_MAX_WIDTH, 1000)
        intent.putExtra(ImagePickerView.INTENT_BITMAP_MAX_HEIGHT, 1000)
        startActivityForResult(intent, AppConstants.REQUEST_IMAGE)
    }

    private fun launchGalleryIntent() {
        val intent = Intent(this@UserProfileView, ImagePickerView::class.java)
        intent.putExtra(
            ImagePickerView.INTENT_IMAGE_PICKER_OPTION,
            ImagePickerView.REQUEST_GALLERY_IMAGE
        )

        // setting aspect ratio
        intent.putExtra(ImagePickerView.INTENT_LOCK_ASPECT_RATIO, true)
        intent.putExtra(ImagePickerView.INTENT_ASPECT_RATIO_X, 1) // 16x9, 1x1, 3:4, 3:2
        intent.putExtra(ImagePickerView.INTENT_ASPECT_RATIO_Y, 1)
        startActivityForResult(intent, AppConstants.REQUEST_IMAGE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AppConstants.REQUEST_IMAGE) {
            if (resultCode == RESULT_OK) {
                val uri = data?.getParcelableExtra<Uri>("path")
                try {
                    val file = File(getPath(applicationContext, uri))
                    val requestFile =
                        file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
                    val body = MultipartBody.Part.createFormData("file", file.name, requestFile)
                    mViewModel.callProfilePicUploadApi(body)


                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        } else if (requestCode == AppConstants.REQUEST_IMAGE) {

        }
    }

    private fun loadProfile(url: String?) {
        url?.let {
            loadImage(
                mViewBinding.ivUserProfileImage,
                it,
                ContextCompat.getDrawable(this, R.drawable.progress_bar_drawable),
                true
            )
            /*mViewBinding.userIv.setColorFilter(
                ContextCompat.getColor(
                    this,
                    android.R.color.transparent
                ))*/

        }
    }

    /**
     * Showing Alert Dialog with Settings option
     * Navigates user to app settings
     * NOTE: Keep proper title and message depending on your app
     */
    private fun showSettingsDialog() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this@UserProfileView)
        builder.setTitle(getString(R.string.dialog_permission_title))
        builder.setMessage(getString(R.string.dialog_permission_message))
        builder.setPositiveButton(getString(R.string.go_to_settings)) { dialog, which ->
            dialog.cancel()
            openSettings()
        }
        builder.setNegativeButton(getString(android.R.string.cancel)) { dialog, which -> dialog.cancel() }
        builder.show()
    }

    // navigating user to app settings
    private fun openSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", packageName, null)
        intent.data = uri
        startActivityForResult(intent, 101)
    }


    /**
     * Method to navigate to the different activity
     */
    private fun intentToActivityWithSomeData(aClass: Class<*>, isFinishAll: Boolean? = false) {
        val intent = Intent(this@UserProfileView, aClass)
        startActivity(intent)
        if (isFinishAll == true) {
            finishAffinity()
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
