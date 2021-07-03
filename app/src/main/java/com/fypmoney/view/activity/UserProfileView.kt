package com.fypmoney.view.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseActivity
import com.fypmoney.databinding.ViewUserProfileBinding
import com.fypmoney.util.AppConstants
import com.fypmoney.util.Utility
import com.fypmoney.view.adapter.MyProfileListAdapter
import com.fypmoney.view.fragment.LogoutBottomSheet
import com.fypmoney.viewmodel.UserProfileViewModel
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.yalantis.ucrop.util.FileUtils.getPath
import kotlinx.android.synthetic.main.toolbar.*
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.IOException


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

        // Clearing older images from cache directory
        // don't call this line if you want to choose multiple images in the same activity
        // call this once the bitmap(s) usage is over
       //ImagePickerView.clearCache(this)
        mViewModel.setInitialData()
        try {
            mViewModel.buildVersion.set(
                applicationContext.packageManager
                    .getPackageInfo(applicationContext.packageName, 0).versionName
            )
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()

        }
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



                        // You can update this bitmap to your server
                    val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, uri)
                    loadProfile(uri.toString())
                    val file = File(getPath(applicationContext, uri))

                    val fileBody = file.asRequestBody("image/*".toMediaTypeOrNull())
                    val image: MultipartBody.Part = MultipartBody.Part.createFormData(
                        "file",
                        file.name,
                        fileBody
                    )
                    mViewModel.callProfilePicUploadApi(image)


                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun loadProfile(url: String) {
        Log.d("imageUrl", "Image cache path: $url")
        Glide.with(this).load(url)
            .into(mViewBinding.ivServiceLogo)
        mViewBinding.ivServiceLogo.setColorFilter(
            ContextCompat.getColor(
                this,
                android.R.color.transparent
            )
        )
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
