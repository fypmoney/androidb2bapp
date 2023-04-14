package com.fypmoney.view.kycagent.ui

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.hardware.usb.UsbManager.ACTION_USB_DEVICE_ATTACHED
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.provider.Settings
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import com.bumptech.glide.Glide
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseFragment
import com.fypmoney.databinding.FragmentPhotoUploadKycBinding
import com.fypmoney.util.AppConstants
import com.fypmoney.util.Utility
import com.fypmoney.view.activity.ImagePickerView
import com.fypmoney.view.kycagent.viewmodel.PhotoUploadKycFragmentVM
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.yalantis.ucrop.util.FileUtils
import kotlinx.android.synthetic.main.toolbar.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.IOException


class PhotoUploadKycFragment : BaseFragment<FragmentPhotoUploadKycBinding, PhotoUploadKycFragmentVM>() {

    private lateinit var binding: FragmentPhotoUploadKycBinding
    private val photoUploadKycFragmentVM by viewModels<PhotoUploadKycFragmentVM> { defaultViewModelProviderFactory }

    override fun getBindingVariable(): Int = BR.viewModel

    override fun getLayoutId(): Int = R.layout.fragment_photo_upload_kyc

    override fun getViewModel(): PhotoUploadKycFragmentVM = photoUploadKycFragmentVM

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = getViewDataBinding()

        setToolbarAndTitle(
            context = requireContext(),
            toolbar = toolbar,
            isBackArrowVisible = true, toolbarTitle = "Become a Fyp Agent!",
            titleColor = Color.WHITE,
            backArrowTint = Color.WHITE
        )

//        binding.ivUpload.setOnClickListener {
//            openCameraAndGallery()
//        }

        LocalBroadcastManager.getInstance(requireContext())
            .registerReceiver(usbReceiver, IntentFilter(ACTION_USB_DEVICE_ATTACHED))

        photoUploadKycFragmentVM.profileState.observe(viewLifecycleOwner){
            when(it){
                is PhotoUploadKycFragmentVM.PhotoUploadState.ProfilePicUpdate -> {
                    if (viewLifecycleOwner.lifecycle.currentState == Lifecycle.State.RESUMED) {
                        openAgentAuthenticationScreen(it.shopData.isShopListed)
                    }
                }
                else -> {}
            }
        }

//        photoUploadKycFragmentVM.profileEvent.observe(viewLifecycleOwner){
//            when(it){
//                PhotoUploadKycFragmentVM.PhotoUploadEvent.OpenAgentAuthentication -> {
//
//                }
//            }
//        }

        binding.btnAddWithdrawSavings.setOnClickListener {
//            if (photoUploadKycFragmentVM.shopPhotoData != null)
//                photoUploadKycFragmentVM.callProfilePicUploadApi(photoUploadKycFragmentVM.shopPhotoData!!)
//            else
//                Utility.showToast("Please select photo first")
            openCameraAndGallery()
        }

    }

    private fun openAgentAuthenticationScreen(shopListed: String?) {
        //navigation_agent_authentication
        if (shopListed == null || shopListed == AppConstants.NO) {
            val bundle = Bundle()
            bundle.putString("via", "SelfKyc")
            findNavController().navigate(
                R.id.navigation_enter_aadhaar_number_kyc,
                bundle,
                navOptions {
                    anim {
                        popEnter = R.anim.slide_in_left
                        popExit = R.anim.slide_out_righ
                        enter = R.anim.slide_in_right
                        exit = R.anim.slide_out_left
                    }
                    popUpTo(R.id.navigation_kyc_agent){
                        inclusive = false
                    }
                })
        }else{
//            findNavController().popBackStack()
            val intent = Intent(requireActivity(), KycAgentActivity::class.java)
            startActivity(intent)
            requireActivity().finishAffinity()
        }
    }

//    fun testMethod(){
//        val usbManager = requireContext().getSystemService(Context.USB_SERVICE) as UsbManager
//        val intent = Intent("android.hardware.usb.action.USB_DEVICE_ATTACHED")
//        intent.addCategory("android.hardware.usb.action.USB_DEVICE_DETACHED")
//        val usbDeviceList: Map<String, UsbDevice> = usbManager.deviceList
//        Toast.makeText(this, "Deivce List Size: " + usbDeviceList.size, Toast.LENGTH_SHORT).show()
//
//        if (usbDeviceList.isNotEmpty()) {
//
//            //vid= vendor id .... pid= product id
//            Toast.makeText(
//                this,
//                "device pid: " + device.getProductId() + " Device vid: " + device.getVendorId(),
//                Toast.LENGTH_SHORT
//            ).show()
//        }
//    }

    override fun onTryAgainClicked() {}

    private fun openCameraAndGallery(){
        val PERMISSIONS2 =
//            if (Build.VERSION.SDK_INT >= 33) {
//            arrayOf(
//                Manifest.permission.CAMERA,
//                Manifest.permission.READ_MEDIA_IMAGES
//            )
//        }
//    else {
            arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
//        }
        Dexter.withContext(requireContext())
            .withPermissions(*PERMISSIONS2)
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
    }

    private fun showImagePickerOptions() {
        ImagePickerView.showImagePickerOptions(
            requireContext(),
            object : ImagePickerView.PickerOptionListener {
                override fun onTakeCameraSelected() {
                    launchCameraIntent()
                }

                override fun onChooseGallerySelected() {
                }
            })
    }

    private fun launchCameraIntent() {
        val intent = Intent(requireActivity(), ImagePickerView::class.java)
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

    private fun showSettingsDialog() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
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
        val uri = Uri.fromParts("package", requireContext().packageName, null)
        intent.data = uri
        startActivityForResult(intent, 101)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AppConstants.REQUEST_IMAGE) {
            if (resultCode == AppCompatActivity.RESULT_OK) {
                val uri = data?.getParcelableExtra<Uri>("path")
                try {
                    val file = File(FileUtils.getPath(requireContext().applicationContext, uri))
                    val requestFile =
                        file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
                    val body = MultipartBody.Part.createFormData("file", file.name, requestFile)

                    photoUploadKycFragmentVM.shopPhotoData = body

                    Glide.with(requireContext()).load(uri).into(binding.ivShopUploadedPhoto)

                    photoUploadKycFragmentVM.callProfilePicUploadApi(photoUploadKycFragmentVM.shopPhotoData!!)

                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }

    fun isOtgDevice(device: UsbDevice): Boolean {
        return true
    }

    private val usbReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            val action = intent.action
            if (ACTION_USB_DEVICE_ATTACHED == action) {
                // A USB device was attached
                val device =
                    intent.getParcelableExtra<Parcelable>(UsbManager.EXTRA_DEVICE) as UsbDevice?
                if (device != null) {
                    // Check if the device is an OTG device
                    if (isOtgDevice(device)) {
                        // Handle the OTG device connection
                        handleOtgDeviceConnection(device)
                    }
                }
            } else if (UsbManager.ACTION_USB_DEVICE_DETACHED == action) {
                // A USB device was detached
                val device =
                    intent.getParcelableExtra<Parcelable>(UsbManager.EXTRA_DEVICE) as UsbDevice?
                if (device != null) {
                    // Check if the device is an OTG device
                    if (isOtgDevice(device)) {
                        // Handle the OTG device disconnection
                        handleOtgDeviceDisconnection(device)
                    }
                }
            }
        }
    }

    private fun handleOtgDeviceConnection(device: UsbDevice) {
        // TODO: Implement your OTG device connection logic here
        Utility.showToast("Device Connected: $device")
    }

    private fun handleOtgDeviceDisconnection(device: UsbDevice) {
        // TODO: Implement your OTG device disconnection logic here
        Utility.showToast("Device Disconnected: $device")

    }

}