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
import com.fyp.trackr.models.TrackrEvent
import com.fyp.trackr.models.trackr
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseFragment
import com.fypmoney.databinding.FragmentVerifyBiometricBinding
import com.fypmoney.util.DialogUtils
import com.fypmoney.util.Utility
import com.fypmoney.view.kycagent.viewmodel.VerifyBiometricFragmentVM
import com.google.firebase.crashlytics.FirebaseCrashlytics
import kotlinx.android.synthetic.main.toolbar.toolbar
import org.w3c.dom.Attr
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import org.xmlpull.v1.XmlPullParserFactory
import java.io.ByteArrayInputStream
import java.io.IOException
import java.io.InputStream
import java.io.StringReader
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

        if (arguments?.containsKey("mobileNumber") == true) {
            verifyBiometricFragmentVM.mobileNumber =
                arguments?.getString("mobileNumber").toString()
        }



        verifyBiometricFragmentVM.aadhaarNumber = arguments?.getString("aadhaarNumber")



        if (verifyBiometricFragmentVM.via == "UserKyc"){
            trackr {
                it.name = TrackrEvent.new_activate_sensor_view
            }
            //userkyc
        }else{
            //selfkyc
            trackr {
                it.name = TrackrEvent.signup_activate_sensor_view
            }
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
                handelState(it)
        }

        verifyBiometricFragmentVM.event.observe(viewLifecycleOwner){
            when(it){
                is VerifyBiometricFragmentVM.VerifyBiometricEvent.NavigateToFillKycDetailsPage -> {
                    if (verifyBiometricFragmentVM.via == "UserKyc"){
                        trackr {
                            it.name = TrackrEvent.new_activate_sensor_success
                        }
                        //userkyc
                    }else{
                        //selfkyc
                        trackr {
                            it.name = TrackrEvent.signup_activate_sensor_success
                        }
                    }
                    val bundle = Bundle()
                    bundle.putString("via", verifyBiometricFragmentVM.via)
                    bundle.putString("aadhaarNumber", verifyBiometricFragmentVM.aadhaarNumber)
                    if (verifyBiometricFragmentVM.mobileNumber != null)
                        bundle.putString("mobileNumber", verifyBiometricFragmentVM.mobileNumber)
                    bundle.putParcelable("DeviceData", it.fingerDeviceInfo)
                    findNavController().navigate(R.id.navigation_take_hand_finger, bundle, navOptions {
                        anim {
                            popEnter = R.anim.slide_in_left
                            popExit = R.anim.slide_out_righ
                            enter = R.anim.slide_in_right
                            exit = R.anim.slide_out_left
                        }
                        popUpTo(R.id.navigation_agent_authentication){
                            inclusive = false
                        }
                    })
                }
                is VerifyBiometricFragmentVM.VerifyBiometricEvent.NavigateToPlayStore -> {
                    val webIntent = Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse(it.url)
                    )
                    requireActivity().startActivity(webIntent)
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
                if (verifyBiometricFragmentVM.appInstalledOrNot(
                        appId = appId,
                        context = requireContext()
                    )
                ) {
                    getMantraDeviceInfo()
                } else {
                    showDeviceDriverBototmsheet("MANTRA")
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
                    showDeviceDriverBototmsheet("MORPHO")

                }
            }
            VerifyBiometricFragmentVM.FingerPrintDevices.NotSuppourtedDevice -> {
                showNoSupportedDevice()
            }
            is VerifyBiometricFragmentVM.FingerPrintDevices.StartTek -> {
                val appId = "com.acpl.registersdk"
                if(verifyBiometricFragmentVM.appInstalledOrNot(appId = appId, context =requireContext())){
                    getStartekData()
                }else{
                    showDeviceDriverBototmsheet("STARTEK")
                }
            }
            null -> {}
        }
    }


    private fun showDeviceDriverBototmsheet(deviceName:String){
        trackr {
            it.name = TrackrEvent.error_no_driver
        }
        val deviceDriverSheet = DeviceDriverBottomSheet(
            deviceName = deviceName,
            onInstallClick = {
                verifyBiometricFragmentVM.redirectToPlayStore(it)
            },
            onDialogDismiss = {
                findNavController().navigateUp()
            },
        )
        //deviceDriverSheet.isCancelable = false
        deviceDriverSheet.show(requireActivity().supportFragmentManager,"deviceDriver")

    }

    private fun showNoSupportedDevice(){
        trackr {
            it.name = TrackrEvent.error_device_support
        }
        val deviceBottomSheet = DeviceBottomSheet(onContinueClick = {
            findNavController().navigateUp()
        })
        deviceBottomSheet.show(requireActivity().supportFragmentManager,"device-bottomsheet")
    }

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
                    Log.d("UsbRecciver", "device data1 $device")
                    Utility.showToast("Device Detected ${device.manufacturerName}")
                    verifyBiometricFragmentVM.checkWhichDeviceIsAttached(
                        device.productName!!,
                        device.manufacturerName,true

                    )
                } else {
                    if (verifyBiometricFragmentVM.via == "UserKyc"){
                        trackr {
                            it.name = TrackrEvent.new_activate_sensor_fail
                        }
                        //userkyc
                    }else{
                        //selfkyc
                        trackr {
                            it.name = TrackrEvent.signup_activate_sensor_fail
                        }
                    }
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
                    verifyBiometricFragmentVM.checkWhichDeviceIsAttached(device.productName!!,device.manufacturerName,false)
                }
            }else{
                if (verifyBiometricFragmentVM.via == "UserKyc"){
                    trackr {
                        it.name = TrackrEvent.new_activate_sensor_fail
                    }
                    //userkyc
                }else{
                    //selfkyc
                    trackr {
                        it.name = TrackrEvent.signup_activate_sensor_fail
                    }
                }
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

//        val dom = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(inputStream)
//        Log.i(TAG,"dom ${dom.toString()}")
        try{
            val dom1 = DocumentBuilderFactory.newInstance()
            Log.i(TAG,"dom1 ${dom1.toString()}")
            val dom2 = dom1.newDocumentBuilder()
            Log.i(TAG,"dom2 ${dom2.toString()}")

            val dom3 = dom2.parse(inputStream)
            Log.i(TAG,"dom3 ${dom3.attributes}")

            val nodeList = dom3.getElementsByTagName("RDService")
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
        }catch (e:Exception){
            Log.i(TAG,"dom ${e.printStackTrace()}")
        }

        return VerifyBiometricFragmentVM.RdServiceStatus.DeviceIsNotReady

    }
    private fun parseXmlUsingXmlPullParser(xml: String): VerifyBiometricFragmentVM.RdServiceStatus {

        try {
            // Create a new instance of the XmlPullParser
            val factory = XmlPullParserFactory.newInstance()
            factory.isNamespaceAware = true
            val parser = factory.newPullParser()

            // Create a character stream from the XML String
            val stringReader = StringReader(xml)

            // Set the input for the parser
            parser.setInput(stringReader)

            // Parse the XML data
            var eventType = parser.eventType
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {
                    val deviceStatus = parser.getAttributeValue(null,"status")
                    Log.i("XML Parsing", "TAg name: ${parser.getAttributeValue(null,"status")}")
                    if (deviceStatus == "READY") {
                        return VerifyBiometricFragmentVM.RdServiceStatus.DeviceIsReady
                    } else {
                        return VerifyBiometricFragmentVM.RdServiceStatus.DeviceIsNotReady

                    }

//
                }
                eventType = parser.next()
            }
        } catch (e: XmlPullParserException) {
            e.printStackTrace()
            FirebaseCrashlytics.getInstance().recordException(e)
        } catch (e: IOException) {
            e.printStackTrace()
            FirebaseCrashlytics.getInstance().recordException(e)
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
                        when (parseXmlUsingXmlPullParser(rdServiceInfo)) {
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
                        when (parseXmlUsingXmlPullParser(rdServiceInfo)) {
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
        val filter = IntentFilter()
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED)
        requireActivity().registerReceiver(userBroadcastReceiver, filter)
//        isOtgEnabled(requireContext())

    }

    override fun onStart() {
        super.onStart()
        isOtgEnabled(requireContext())
    }

    override fun onPause() {
        super.onPause()
       requireActivity().unregisterReceiver(userBroadcastReceiver)
    }

    override fun getBindingVariable(): Int = BR.viewModel

    override fun getLayoutId(): Int = R.layout.fragment_verify_biometric

    override fun getViewModel(): VerifyBiometricFragmentVM = verifyBiometricFragmentVM

    override fun onTryAgainClicked() {}

}