package com.fypmoney.view.kycagent.viewmodel

import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.os.Parcelable
import android.util.Base64
import android.util.Log
import androidx.annotation.Keep
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.fyp.trackr.models.TrackrEvent
import com.fyp.trackr.models.trackr
import com.fypmoney.base.BaseViewModel
import com.fypmoney.util.livedata.LiveEvent
import kotlinx.android.parcel.Parcelize
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class VerifyBiometricFragmentVM(application: Application): BaseViewModel(application) {

    var mobileNumber: String ?= null
    lateinit var via: String
    var aadhaarNumber : String ?= null
    private val TAG = VerifyBiometricFragmentVM::class.java.simpleName

    val event: LiveData<VerifyBiometricEvent>
        get() = _event
    private val _event = LiveEvent<VerifyBiometricEvent>()

    val deviceState: LiveData<FingerPrintDevices>
        get() = _deviceState
    private val _deviceState = MutableLiveData<FingerPrintDevices>()

    var connectedDeviceInfo:FingerDeviceInfo? = null
    var deviceName:String? = null


    fun navigateToFillDetails(){
        _event.postValue(connectedDeviceInfo?.let { VerifyBiometricEvent.NavigateToFillKycDetailsPage(it) })
    }

    fun appInstalledOrNot(appId: String, context: Context): Boolean {
        val pm: PackageManager = context.packageManager
        return try {
            pm.getApplicationInfo(appId, 0)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }

//    fun parseXml(xml:String):CaptureFingerStatus {
//        val inputStream: InputStream = ByteArrayInputStream(xml.toByteArray())
//
//        val dom = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(inputStream)
//        val nodeList = dom.getElementsByTagName("Resp")
//        if(nodeList.length != 0){
//            val attributes = nodeList.item(0).attributes
//            val errCode = (attributes.getNamedItem("errCode") as Attr).value
//            Log.d(TAG,"errCode:- $errCode")
//            if(errCode=="0"){
//                val qScore = (attributes.getNamedItem("qScore") as Attr).value
//                Log.d(TAG,"qScore:- ${qScore}")
//                qScore.toIntOrNull()?.let {
//                    if(it<=10){
//                        return CaptureFingerStatus.CaptureFingerQualityIsNotGood
//                    }else{
//                        Log.d(TAG, "pidOptions xml: ${xml}")
//                        val pidOptions = dom.getElementsByTagName("PidData")
//                        Log.d(TAG,"pidOptions: $pidOptions")
//                        return CaptureFingerStatus.CapturedSuccessFully(xml)
//                    }
//                }?: kotlin.run {
//                    return CaptureFingerStatus.CaptureFingerQualityIsPoor
//
//                }
//
//            }else{
//                val errInfo = (attributes.getNamedItem("errInfo") as Attr).value
//
//                return CaptureFingerStatus.ErrorInCaptureFinger(errInfo)
//
//            }
//
//
//        }
//        return CaptureFingerStatus.UnableToCaptureFinger
//
//
//    }

    fun checkWhichDeviceIsAttached(
        productName: String?,
        manufacturerName: String?,
        fromBroadcast:Boolean
    ) {
        manufacturerName?.let { mfName->
            if(mfName.startsWith("Startek Eng-Inc",false)){
                connectedDeviceInfo = FingerDeviceInfo(
                    deviceManufactureName = "STARTEK",
                    deviceProductName = productName!!
                )
                _deviceState.value = FingerPrintDevices.StartTek(
                    morphoDevice = FingerDeviceInfo(
                        deviceManufactureName = "STARTEK",
                        deviceProductName = productName
                    )
                )
            }else if(mfName=="MANTRA"){
                if(fromBroadcast){
                    viewModelScope.launch {
                        progressDialog.postValue(true)
                        delay(20000)
                        progressDialog.postValue(false)
                        connectedDeviceInfo = FingerDeviceInfo(
                            deviceManufactureName = "MANTRA",
                            deviceProductName = productName!!
                        )
                        _deviceState.value = FingerPrintDevices.Mantra(
                            morphoDevice = FingerDeviceInfo(
                                deviceManufactureName = "MANTRA",
                                deviceProductName = productName
                            ))
                    }
                }else{
                    connectedDeviceInfo = FingerDeviceInfo(
                        deviceManufactureName = "MANTRA",
                        deviceProductName = productName!!
                    )
                    _deviceState.value = FingerPrintDevices.Mantra(
                        morphoDevice = FingerDeviceInfo(
                            deviceManufactureName = "MANTRA",
                            deviceProductName = productName
                        ))
                }



            }else if(mfName=="Morpho"){
                connectedDeviceInfo = FingerDeviceInfo(
                    deviceManufactureName = "MORPHO",
                    deviceProductName = productName!!
                )
                _deviceState.value = FingerPrintDevices.MorphoDevice(
                    morphoDevice = FingerDeviceInfo(
                        deviceManufactureName = "MORPHO",
                        deviceProductName = productName!!
                    ))
            }else{
                _deviceState.value = FingerPrintDevices.NotSuppourtedDevice

            }
        }

    }

    fun redirectToPlayStore(deviceName:String){
        if(deviceName.equals("STARTEK",false)){
            val url = "https://play.google.com/store/apps/details?id=com.acpl.registersdk"
            _event.value = VerifyBiometricEvent.NavigateToPlayStore(url)

        }else if(deviceName.equals("MANTRA")){
            val url = "https://play.google.com/store/apps/details?id=com.mantra.rdservice"
            _event.value = VerifyBiometricEvent.NavigateToPlayStore(url)

        }else if(deviceName.equals("MORPHO")){
            val url = "https://play.google.com/store/apps/details?id=com.scl.rdservice"
            _event.value = VerifyBiometricEvent.NavigateToPlayStore(url)

        }
    }

    sealed class VerifyBiometricEvent{
        data class NavigateToFillKycDetailsPage(var fingerDeviceInfo: FingerDeviceInfo):VerifyBiometricEvent()
        data class NavigateToPlayStore(var url:String):VerifyBiometricEvent()
    }


    sealed class FingerPrintDevices{
        data class MorphoDevice(val morphoDevice: FingerDeviceInfo):FingerPrintDevices()
        data class StartTek(val morphoDevice: FingerDeviceInfo):FingerPrintDevices()
        data class Mantra(val morphoDevice: FingerDeviceInfo):FingerPrintDevices()
        object NotSuppourtedDevice:FingerPrintDevices()
    }

    @Keep
    sealed class RdServiceStatus{
        object DeviceIsReady:RdServiceStatus()
        object DeviceIsNotReady:RdServiceStatus()
    }

//    sealed class CaptureFingerStatus{
//        data class ErrorInCaptureFinger(val message:String):CaptureFingerStatus()
//        data class CapturedSuccessFully(val pidOptions:String):CaptureFingerStatus()
//        object CaptureFingerQualityIsNotGood:CaptureFingerStatus()
//        object CaptureFingerQualityIsPoor:CaptureFingerStatus()
//        object UnableToCaptureFinger:CaptureFingerStatus()
//
//    }
//
//    sealed class FillKycEvent{
//        object CaptureFingrePrint:FillKycEvent()
//        object FullKycCompleted:FillKycEvent()
//    }

    @Parcelize
    @Keep
    data class FingerDeviceInfo(
        val deviceManufactureName:String,
        val deviceProductName:String,
        var deviceSerialNumber: String ? = null,
        var deviceVersion: String ?= null
    ): Parcelable

    fun convertPidDataIntoBase64(pidOptions: String): String {
        val data =  pidOptions.toByteArray()
        val base64 = Base64.encodeToString(data, Base64.NO_WRAP)
        Log.d(TAG,"converted base 64:- ${base64}")
        return base64
    }


}