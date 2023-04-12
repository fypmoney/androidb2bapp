package com.fypmoney.view.kycagent.ui

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseFragment
import com.fypmoney.databinding.FragmentVerifyBiometricBinding
import com.fypmoney.util.DialogUtils
import com.fypmoney.util.Utility
import com.fypmoney.view.kycagent.viewmodel.VerifyBiometricFragmentVM
import kotlinx.android.synthetic.main.toolbar.*
import org.w3c.dom.Attr
import java.io.ByteArrayInputStream
import java.io.InputStream
import javax.xml.parsers.DocumentBuilderFactory

class VerifyBiometricFragment :
    BaseFragment<FragmentVerifyBiometricBinding, VerifyBiometricFragmentVM>() {

    private lateinit var binding: FragmentVerifyBiometricBinding
    private val verifyBiometricFragmentVM by viewModels<VerifyBiometricFragmentVM> { defaultViewModelProviderFactory }
    private val TAG = VerifyBiometricFragment::class.java.simpleName

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = getViewDataBinding()

        if (arguments?.containsKey("via") == true) {
            verifyBiometricFragmentVM.via = arguments?.getString("via").toString()
        }

        setToolbarAndTitle(
            context = requireContext(),
            toolbar = toolbar,
            isBackArrowVisible = true,
            toolbarTitle = if (verifyBiometricFragmentVM.via == "UserKyc") "Complete Full KYC" else "Become a Fyp Agent!",
            titleColor = Color.WHITE,
            backArrowTint = Color.WHITE
        )

        verifyBiometricFragmentVM.deviceState.observe(viewLifecycleOwner) {
            if (viewLifecycleOwner.lifecycle.currentState == androidx.lifecycle.Lifecycle.State.RESUMED) {
                handelState(it)
            }
        }

        verifyBiometricFragmentVM.event.observe(viewLifecycleOwner){
            when(it){
                is VerifyBiometricFragmentVM.VerifyBiometricEvent.NavigateToFillKycDetailsPage -> {
                    val bundle = Bundle()
                    bundle.putString("via", verifyBiometricFragmentVM.via)
                    findNavController().navigate(R.id.navigation_take_hand_finger, bundle, navOptions {
                        anim {
                            popEnter = R.anim.slide_in_left
                            popExit = R.anim.slide_out_righ
                            enter = R.anim.slide_in_right
                            exit = R.anim.slide_out_left
                        }
                    })
                }
            }
        }

    }

//    private fun setUpObserver() {
//        verifyBiometricFragmentVM.kycEvent.observe(viewLifecycleOwner){
//            handelEvent(it)
//        }
//    }

    private fun handelState(it: VerifyBiometricFragmentVM.FingerPrintDevices?) {
        when (it) {
            is VerifyBiometricFragmentVM.FingerPrintDevices.Mantra -> {
                val appId = "com.mantra.rdservice"
                //val appId = "in.gov.uidai.rdservice.fp.INFO"
                if (verifyBiometricFragmentVM.appInstalledOrNot(
                        appId = appId,
                        context = requireContext()
                    )
                ) {
                    getMantraDeviceInfo()
                } else {
                    val webIntent = Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/apps/details?id=$appId")
                    )
                    requireActivity().startActivity(webIntent)
                }
            }

            is VerifyBiometricFragmentVM.FingerPrintDevices.MorphoDevice -> {
                val appId = "com.scl.rdservice"
                if (verifyBiometricFragmentVM.appInstalledOrNot(
                        appId = appId,
                        context = requireContext()
                    )
                ) {
                    getMorophoDeviceInfo()
                } else {
                    val webIntent = Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/apps/details?id=$appId")
                    )
                    requireActivity().startActivity(webIntent)
                }
            }
            VerifyBiometricFragmentVM.FingerPrintDevices.NoDeviceConnected -> {
                verifyBiometricFragmentVM.alertDialog.postValue(
                    DialogUtils.AlertStateUiModel(
                        icon = ContextCompat.getDrawable(
                            requireContext(),
                            R.drawable.ic_error_alert
                        )!!,
                        message = "No device is connected",
                        backgroundColor = ContextCompat.getColor(
                            requireContext(),
                            R.color.errorAlertBgColor
                        )
                    )
                )
            }
            is VerifyBiometricFragmentVM.FingerPrintDevices.StartTek -> {
                val appId = "com.acpl.registersdk"
                //getMantraDeviceInfo()
                if(verifyBiometricFragmentVM.appInstalledOrNot(appId = appId, context =requireContext())){
                    getStartekData()
                }else{
                    val webIntent = Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/apps/details?id=$appId")
                    )
                    requireActivity().startActivity(webIntent)
                }
            }
            null -> {}
        }
    }

//    private fun handelEvent(it: VerifyBiometricFragmentVM.FillKycEvent?) {
//        when(it){
//            VerifyBiometricFragmentVM.FillKycEvent.CaptureFingrePrint -> {
//                if(verifyBiometricFragmentVM.deviceName!!.startsWith("Startek Eng-Inc",false)){
//                    captureStarTekFigure()
//                }else if(verifyBiometricFragmentVM.deviceName == "MANTRA"){
//                    captureMantraFigure()
//                }else if(verifyBiometricFragmentVM.deviceName == "Morpho"){
//                    captureMorphoFigure()
//                }
//            }
//            null -> {
//
//            }
//            VerifyBiometricFragmentVM.FillKycEvent.FullKycCompleted -> {
//                findNavController().navigateUp()
//            }
//        }
//    }

    private val userBroadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            if (UsbManager.ACTION_USB_DEVICE_ATTACHED == action) {
                // An OTG device is connected
                val device =
                    intent.getParcelableExtra<Parcelable>(UsbManager.EXTRA_DEVICE) as UsbDevice?
                if (device != null) {
                    // Do something with the device

                    Log.d("UsbRecciver", "Device is detected")

                    Log.d("UsbRecciver", "device data ${device.deviceName}")
                    Log.d("UsbRecciver", "device data1 ${device.toString()}")
                    Utility.showToast("Device Detected ${device.manufacturerName}")
                    verifyBiometricFragmentVM.checkWhichDeviceIsAttached(
                        device.productName!!,
                        device.manufacturerName
                    )
                } else {
                    Log.d("UsbRecciver", "Device is not detected")

                }
            } else if (UsbManager.ACTION_USB_DEVICE_DETACHED == action) {
                // A USB device has been detached
                val device =
                    intent.getParcelableExtra<Parcelable>(UsbManager.EXTRA_DEVICE) as UsbDevice?
                if (device != null) {
                    Utility.showToast("Device de attached ${device.manufacturerName}")

                }
            }
        }
    }

    private fun isOtgEnabled(context: Context) {
        val usbManager = context.getSystemService(Context.USB_SERVICE) as UsbManager
        if (usbManager != null) {
            val deviceList = usbManager.deviceList
            if (deviceList.size!=0){
                for (device in deviceList.values) {
                    verifyBiometricFragmentVM.checkWhichDeviceIsAttached(device.productName!!,device.manufacturerName)

                }
            }else{
                verifyBiometricFragmentVM.alertDialog.postValue(  DialogUtils.AlertStateUiModel(
                    icon = ContextCompat.getDrawable(requireContext(),R.drawable.ic_error_alert)!!,
                    message = "Please check OTG is enabled and device is connected",
                    backgroundColor = ContextCompat.getColor(requireContext(),R.color.errorAlertBgColor)
                ))
            }

        }
        //Log.d(TAG, "OTG is enabled ${otgEnabled}")
    }

    private fun parseXml(xml: String): VerifyBiometricFragmentVM.RdServiceStatus {
        val inputStream: InputStream = ByteArrayInputStream(xml.toByteArray())

        val dom = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(inputStream)
        val nodeList = dom.getElementsByTagName("RDService")
        if (nodeList.length != 0) {
            val attributes = nodeList.item(0).attributes
            val status = (attributes.getNamedItem("status") as Attr).value
            Log.d(TAG, "Rd services status:- ${status}")
            if (status == "READY") {
                return VerifyBiometricFragmentVM.RdServiceStatus.DeviceIsReady
            } else {
                return VerifyBiometricFragmentVM.RdServiceStatus.DeviceIsNotReady

            }

        }
        return VerifyBiometricFragmentVM.RdServiceStatus.DeviceIsNotReady

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 1001) {
                val bundle = data?.extras
                if (bundle != null) {
                    val deviceInfo = bundle.getString("DEVICE_INFO", "device")
                    val rdServiceInfo = bundle.getString("RD_SERVICE_INFO", "rd_service")
                    val dnc = bundle.getString("DNC", "")
                    val dnr = bundle.getString("DNR", "")
                    if (dnc.isNotEmpty() || dnr.isNotEmpty()) {
                        Log.d(
                            TAG,
                            "Device Info dnc:= ${dnc} dnr:= ${dnr} deviceInfo:=${deviceInfo} " +
                                    "rdServiceInfo:=${rdServiceInfo}"
                        )
                        when (parseXml(rdServiceInfo)) {
                            VerifyBiometricFragmentVM.RdServiceStatus.DeviceIsNotReady -> {
                                verifyBiometricFragmentVM.alertDialog.postValue(
                                    DialogUtils.AlertStateUiModel(
                                        icon = ContextCompat.getDrawable(
                                            requireContext(),
                                            R.drawable.ic_error_alert
                                        )!!,
                                        message = "Device is Not ready, please wait for sometime...",
                                        backgroundColor = ContextCompat.getColor(
                                            requireContext(),
                                            R.color.errorAlertBgColor
                                        )
                                    )
                                )
                            }
                            VerifyBiometricFragmentVM.RdServiceStatus.DeviceIsReady -> {
                                verifyBiometricFragmentVM.navigateToFillDetails()
                            }
                        }

                    } else {
                        Log.d(
                            TAG,
                            "Device Info=: deviceInfo:=${deviceInfo} rdServiceInfo:=${rdServiceInfo}"
                        )
                        when (parseXml(rdServiceInfo)) {
                            VerifyBiometricFragmentVM.RdServiceStatus.DeviceIsNotReady -> {
                                verifyBiometricFragmentVM.alertDialog.postValue(
                                    DialogUtils.AlertStateUiModel(
                                        icon = ContextCompat.getDrawable(
                                            requireContext(),
                                            R.drawable.ic_error_alert
                                        )!!,
                                        message = "Device is Not ready, please connect and wait..",
                                        backgroundColor = ContextCompat.getColor(
                                            requireContext(),
                                            R.color.errorAlertBgColor
                                        )
                                    )
                                )
                            }
                            VerifyBiometricFragmentVM.RdServiceStatus.DeviceIsReady -> {
                                verifyBiometricFragmentVM.navigateToFillDetails()
                            }
                        }
                    }
                } else {
                    verifyBiometricFragmentVM.alertDialog.postValue(
                        DialogUtils.AlertStateUiModel(
                            icon = ContextCompat.getDrawable(
                                requireContext(),
                                R.drawable.ic_error_alert
                            )!!,
                            message = "Bundle is empty",
                            backgroundColor = ContextCompat.getColor(
                                requireContext(),
                                R.color.errorAlertBgColor
                            )
                        )
                    )
                }
            }
//            else if (requestCode == 1002) {
//                val bundle = data?.extras
//                val pid = bundle?.getString("PID_DATA", "")
//                val dnc = bundle?.getString("DNC", "")
//                val dnr = bundle?.getString("DNR", "")
//                if (pid!!.isNotEmpty()) {
//                    Log.d(TAG, "Pid Info : ${pid}")
//                    val result = verifyBiometricFragmentVM.parseXml(pid)
//                    when (result) {
//                        VerifyBiometricFragmentVM.CaptureFingerStatus.CaptureFingerQualityIsNotGood -> {
//                            verifyBiometricFragmentVM.alertDialog.postValue(
//                                DialogUtils.AlertStateUiModel(
//                                    icon = ContextCompat.getDrawable(
//                                        requireContext(),
//                                        R.drawable.ic_error_alert
//                                    )!!,
//                                    message = "Finger quality is not good.Please clean device and fingre and rtey Again",
//                                    backgroundColor = ContextCompat.getColor(
//                                        requireContext(),
//                                        R.color.errorAlertBgColor
//                                    )
//                                )
//                            )
//                        }
//                        VerifyBiometricFragmentVM.CaptureFingerStatus.CaptureFingerQualityIsPoor -> {
//                            verifyBiometricFragmentVM.alertDialog.postValue(
//                                DialogUtils.AlertStateUiModel(
//                                    icon = ContextCompat.getDrawable(
//                                        requireContext(),
//                                        R.drawable.ic_error_alert
//                                    )!!,
//                                    message = "Finger quality is very poor.Please clean device and fingre and rtey Again",
//                                    backgroundColor = ContextCompat.getColor(
//                                        requireContext(),
//                                        R.color.errorAlertBgColor
//                                    )
//                                )
//                            )
//                        }
//                        VerifyBiometricFragmentVM.CaptureFingerStatus.UnableToCaptureFinger -> {
//                            verifyBiometricFragmentVM.alertDialog.postValue(
//                                DialogUtils.AlertStateUiModel(
//                                    icon = ContextCompat.getDrawable(
//                                        requireContext(),
//                                        R.drawable.ic_error_alert
//                                    )!!,
//                                    message = "unable to scan finger.try Again...",
//                                    backgroundColor = ContextCompat.getColor(
//                                        requireContext(),
//                                        R.color.errorAlertBgColor
//                                    )
//                                )
//                            )
//                        }
//                        is VerifyBiometricFragmentVM.CaptureFingerStatus.CapturedSuccessFully -> {
//                            //verifyBiometricFragmentVM.postKycData(captureInfo = result.pidOptions)
////                            verifyBiometricFragmentVM.postKycData(captureInfo = verifyBiometricFragmentVM.convertPidDataIntoBase64(result.pidOptions))
//                        }
//                        is VerifyBiometricFragmentVM.CaptureFingerStatus.ErrorInCaptureFinger -> {
//                            verifyBiometricFragmentVM.alertDialog.postValue(
//                                DialogUtils.AlertStateUiModel(
//                                    icon = ContextCompat.getDrawable(
//                                        requireContext(),
//                                        R.drawable.ic_error_alert
//                                    )!!,
//                                    message = result.message,
//                                    backgroundColor = ContextCompat.getColor(
//                                        requireContext(),
//                                        R.color.errorAlertBgColor
//                                    )
//                                )
//                            )
//                        }
//                    }
//                } else if (dnc!!.isEmpty()) {
//                    Log.d(TAG, "Device not connected")
//                    verifyBiometricFragmentVM.alertDialog.postValue(
//                        DialogUtils.AlertStateUiModel(
//                            icon = ContextCompat.getDrawable(
//                                requireContext(),
//                                R.drawable.ic_error_alert
//                            )!!,
//                            message = "Device not connected",
//                            backgroundColor = ContextCompat.getColor(
//                                requireContext(),
//                                R.color.errorAlertBgColor
//                            )
//                        )
//                    )
//
//                } else if (dnr!!.isEmpty()) {
//                    Log.d(TAG, "Device not registered")
//                    verifyBiometricFragmentVM.alertDialog.postValue(
//                        DialogUtils.AlertStateUiModel(
//                            icon = ContextCompat.getDrawable(
//                                requireContext(),
//                                R.drawable.ic_error_alert
//                            )!!,
//                            message = "Device not registered",
//                            backgroundColor = ContextCompat.getColor(
//                                requireContext(),
//                                R.color.errorAlertBgColor
//                            )
//                        )
//                    )
//                }
//
//            }
            else {
                verifyBiometricFragmentVM.alertDialog.postValue(
                    DialogUtils.AlertStateUiModel(
                        icon = ContextCompat.getDrawable(
                            requireContext(),
                            R.drawable.ic_error_alert
                        )!!,
                        message = "Unable to register Morpho Device.",
                        backgroundColor = ContextCompat.getColor(
                            requireContext(),
                            R.color.errorAlertBgColor
                        )
                    )
                )
            }

        } else {
            verifyBiometricFragmentVM.alertDialog.postValue(
                DialogUtils.AlertStateUiModel(
                    icon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_error_alert)!!,
                    message = "No Device Attached",
                    backgroundColor = ContextCompat.getColor(
                        requireContext(),
                        R.color.errorAlertBgColor
                    )
                )
            )
        }
    }

    private fun getMorophoDeviceInfo() {
        val intent = Intent("in.gov.uidai.rdservice.fp.INFO")
        intent.setPackage("com.scl.rdservice")
        startActivityForResult(intent, 1001)
    }

    private fun getMantraDeviceInfo() {
        val intent = Intent("in.gov.uidai.rdservice.fp.INFO")
        intent.setPackage("com.mantra.rdservice")
        startActivityForResult(intent, 1001)
    }

    private fun getStartekData() {
        val intent = Intent("in.gov.uidai.rdservice.fp.INFO")
        intent.setPackage("com.acpl.registersdk")
        startActivityForResult(intent, 1001)
    }

    override fun onResume() {
        super.onResume()
//        val filter = IntentFilter()
//        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED)
//        requireActivity().registerReceiver(userBroadcastReceiver, filter)
        isOtgEnabled(requireContext())


    }

    override fun onPause() {
        super.onPause()
//        requireActivity().unregisterReceiver(userBroadcastReceiver)
    }

    override fun getBindingVariable(): Int = BR.viewModel

    override fun getLayoutId(): Int = R.layout.fragment_verify_biometric

    override fun getViewModel(): VerifyBiometricFragmentVM = verifyBiometricFragmentVM

    override fun onTryAgainClicked() {}

}