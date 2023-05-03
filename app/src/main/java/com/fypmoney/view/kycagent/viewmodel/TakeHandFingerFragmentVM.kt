package com.fypmoney.view.kycagent.viewmodel

import android.app.Application
import android.util.Base64
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import com.fyp.trackr.models.TrackrEvent
import com.fyp.trackr.models.trackr
import com.fypmoney.R
import com.fypmoney.application.PockketApplication
import com.fypmoney.base.BaseViewModel
import com.fypmoney.connectivity.ApiConstant
import com.fypmoney.connectivity.ApiUrl
import com.fypmoney.connectivity.ErrorResponseInfo
import com.fypmoney.connectivity.network.NetworkUtil
import com.fypmoney.connectivity.retrofit.ApiRequest
import com.fypmoney.connectivity.retrofit.WebApiCaller
import com.fypmoney.util.AppConstants
import com.fypmoney.util.DialogUtils
import com.fypmoney.util.livedata.LiveEvent
import com.fypmoney.view.delegate.FingreDelegate
import com.fypmoney.view.delegate.HandDelegate
import com.fypmoney.view.kycagent.model.FullKycNetworkRequest
import com.fypmoney.view.kycagent.model.FullKycResponse
import com.google.firebase.crashlytics.FirebaseCrashlytics
import org.w3c.dom.Attr
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import org.xmlpull.v1.XmlPullParserFactory
import java.io.ByteArrayInputStream
import java.io.IOException
import java.io.InputStream
import java.io.StringReader
import javax.xml.parsers.DocumentBuilderFactory


class TakeHandFingerFragmentVM(application: Application) : BaseViewModel(application) {

    val handDelegate = HandDelegate()
    val fingreDelegate = FingreDelegate()
    lateinit var via: String

    val kycEvent: LiveData<FillKycEvent>
        get() = _kycEvent
    private val _kycEvent = LiveEvent<FillKycEvent>()

    private val TAG = TakeHandFingerFragmentVM::class.java.simpleName
    var deviceName: String? = null

    var customerNumber: String? = null
    var customerAadhaarNumber: String? = null

    var deviceDetails: VerifyBiometricFragmentVM.FingerDeviceInfo? = null

    fun parseXml(xml: String): CaptureFingerStatus {
        val inputStream: InputStream = ByteArrayInputStream(xml.toByteArray())

        val dom = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(inputStream)
        val nodeList = dom.getElementsByTagName("Resp")
        if (nodeList.length != 0) {
            val attributes = nodeList.item(0).attributes
            val errCode = (attributes.getNamedItem("errCode") as Attr).value
            Log.d(TAG, "errCode:- $errCode")
            if (errCode == "0") {
                if (deviceDetails?.deviceManufactureName == "MORPHO") {
                    //Rds version
                    deviceDetails?.deviceVersion = (dom.getElementsByTagName("DeviceInfo")
                        .item(0).attributes.getNamedItem("rdsVer") as Attr).value

//Device Serial No

                    deviceDetails?.deviceSerialNumber = (dom.getElementsByTagName("DeviceInfo")
                        .item(0).childNodes.item(1).childNodes.item(1).attributes.getNamedItem("value") as Attr).value
                }

                val qScore = (attributes.getNamedItem("qScore") as Attr).value
                Log.d(TAG, "qScore:- ${qScore}")
                qScore.toIntOrNull()?.let {
                    if (it <= 10) {
                        return CaptureFingerStatus.CaptureFingerQualityIsNotGood
                    } else {
                        Log.d(TAG, "pidOptions xml: ${xml}")
                        val pidOptions = dom.getElementsByTagName("PidData")
                        Log.d(TAG, "pidOptions: $pidOptions")
                        return CaptureFingerStatus.CapturedSuccessFully(xml)
                    }
                } ?: kotlin.run {
                    return CaptureFingerStatus.CaptureFingerQualityIsPoor

                }

            } else {
                val errInfo = (attributes.getNamedItem("errInfo") as Attr).value

                return CaptureFingerStatus.ErrorInCaptureFinger(errInfo)

            }


        }
        return CaptureFingerStatus.UnableToCaptureFinger


    }


    fun parseXmlUsingXmlPullParser(xml: String): CaptureFingerStatus {
//        val inputStream: InputStream = ByteArrayInputStream(xml.toByteArray())
//
//        val dom = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(inputStream)
//        val nodeList = dom.getElementsByTagName("Resp")
//        if (nodeList.length != 0) {
//            val attributes = nodeList.item(0).attributes
//            val errCode = (attributes.getNamedItem("errCode") as Attr).value
//            Log.d(TAG, "errCode:- $errCode")
//            if (errCode == "0") {
//                if (deviceDetails?.deviceManufactureName == "MORPHO") {
//                    //Rds version
//                    deviceDetails?.deviceVersion = (dom.getElementsByTagName("DeviceInfo")
//                        .item(0).attributes.getNamedItem("rdsVer") as Attr).value
//
////Device Serial No
//
//                    deviceDetails?.deviceSerialNumber = (dom.getElementsByTagName("DeviceInfo")
//                        .item(0).childNodes.item(1).childNodes.item(1).attributes.getNamedItem("value") as Attr).value
//                }
//
//                val qScore = (attributes.getNamedItem("qScore") as Attr).value
//                Log.d(TAG, "qScore:- ${qScore}")
//                qScore.toIntOrNull()?.let {
//                    if (it <= 10) {
//                        return CaptureFingerStatus.CaptureFingerQualityIsNotGood
//                    } else {
//                        Log.d(TAG, "pidOptions xml: ${xml}")
//                        val pidOptions = dom.getElementsByTagName("PidData")
//                        Log.d(TAG, "pidOptions: $pidOptions")
//                        return CaptureFingerStatus.CapturedSuccessFully(xml)
//                    }
//                } ?: kotlin.run {
//                    return CaptureFingerStatus.CaptureFingerQualityIsPoor
//
//                }
//
//            } else {
//                val errInfo = (attributes.getNamedItem("errInfo") as Attr).value
//
//                return CaptureFingerStatus.ErrorInCaptureFinger(errInfo)
//
//            }
//
//
//        }


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
                    val tagName = parser.name
                    if(tagName == "DeviceInfo"){
                        if((parser.getAttributeValue(null,"rdsVer")) != null){
                            deviceDetails?.deviceVersion = parser.getAttributeValue(null,"rdsVer")
                        }
                    }else if(tagName=="Param"){
                        if (deviceDetails?.deviceManufactureName == "MORPHO") {
                            //Rds version
                            if(parser.getAttributeValue(null,"value")!= null){
                                deviceDetails?.deviceSerialNumber = parser.getAttributeValue(null,"value")
                            }
                        }
                    }
                    else if(tagName=="Resp"){
                        val errCode = parser.getAttributeValue(null,"errCode")
                        if(errCode !=null){
                            if (errCode == "0") {

                                val qScore = parser.getAttributeValue(null,"qScore")
                                Log.d(TAG, "qScore:- ${qScore}")
                                qScore.toIntOrNull()?.let {
                                    if (it <= 10) {
                                        return CaptureFingerStatus.CaptureFingerQualityIsNotGood
                                    } else {
                                        Log.d(TAG, "pidOptions xml: ${xml}")
//                                val pidOptions = dom.getElementsByTagName("PidData")
//                                Log.d(TAG, "pidOptions: $pidOptions")
                                        return CaptureFingerStatus.CapturedSuccessFully(xml)
                                    }
                                } ?: kotlin.run {
                                    return CaptureFingerStatus.CaptureFingerQualityIsPoor

                                }

                            } else {
                                val errInfo = parser.getAttributeValue(null,"errInfo")

                                return CaptureFingerStatus.ErrorInCaptureFinger(errInfo)

                            }
                        }
                    }



                }
                eventType = parser.next()
            }
        } catch (e: XmlPullParserException) {
            e.printStackTrace()
            FirebaseCrashlytics.getInstance().recordException(e.detail)
        } catch (e: IOException) {
            e.printStackTrace()
            FirebaseCrashlytics.getInstance().recordException(e)
        }



        return CaptureFingerStatus.UnableToCaptureFinger


    }


    fun onContinueClick() {
        if (handDelegate.getValue().isNotEmpty() && fingreDelegate.getValue().isNotEmpty()) {
            _kycEvent.value = FillKycEvent.CaptureFingrePrint
        }
    }

    fun postKycData(capturedInfo: String) {
        getFinger(handDelegate.hand.value!!, fingreDelegate.fingre.value!!)?.let {
            val fullKycNetworkRequest = FullKycNetworkRequest(
                customerMobile = customerNumber!!,
                customerAadhaarNumber = customerAadhaarNumber!!,
                currentAddressCheck = AppConstants.YES,
                selectedFinger = it,
                capturedInfo = capturedInfo,
                deviceSerialNumber = deviceDetails?.deviceSerialNumber ?: "",
                deviceType = deviceDetails?.deviceManufactureName!!,
                deviceVersionNo = deviceDetails?.deviceVersion ?: "",
                deviceCertificateExpriy = "2029-01-01"
            )

            WebApiCaller.getInstance().request(
                ApiRequest(
                    ApiConstant.API_FULL_KYC,
                    NetworkUtil.endURL(ApiConstant.API_FULL_KYC),
                    ApiUrl.POST,
                    fullKycNetworkRequest,
                    this, isProgressBar = true
                )
            )
        } ?: kotlin.run {
            alertDialog.postValue(
                DialogUtils.AlertStateUiModel(
                    icon = ContextCompat.getDrawable(
                        PockketApplication.instance,
                        R.drawable.ic_error_alert
                    )!!,
                    message = "Finger is not captured,Please try again...",
                    backgroundColor = ContextCompat.getColor(
                        PockketApplication.instance,
                        R.color.errorAlertBgColor
                    )
                )
            )
        }


    }

//    private fun getDeviceType(manufactureName: String) : Int{
//        if(manufactureName.startsWith("Startek Eng-Inc",false)){
//            return 3
//        }else if(manufactureName == "MANTRA"){
//            return 6
//        }else if(manufactureName == "Morpho"){
//            return 1
//        }
//    }

    override fun onSuccess(purpose: String, responseData: Any) {
        super.onSuccess(purpose, responseData)

        when (purpose) {
            ApiConstant.API_FULL_KYC -> {
                if (responseData is FullKycResponse) {
                    if (via == "UserKyc"){
                        trackr {
                            it.name = TrackrEvent.new_kyc_success
                        }
                        //userkyc
                    }else{
                        //selfkyc
                        trackr {
                            it.name = TrackrEvent.signup_kyc_success
                        }
                    }
                    _kycEvent.value = FillKycEvent.FullKycCompleted(responseData.msg)
                }
            }
        }
    }

    override fun onError(purpose: String, errorResponseInfo: ErrorResponseInfo) {
        super.onError(purpose, errorResponseInfo)

        when (purpose) {
            ApiConstant.API_FULL_KYC -> {
                val map = HashMap<String,Any>()
                map.put("Reasson",errorResponseInfo.msg)
                if (via == "UserKyc"){
                    trackr {
                        it.name = TrackrEvent.new_kyc_fail
                        it.map = map
                    }
                    //userkyc
                }else{
                    //selfkyc
                    trackr {
                        it.name = TrackrEvent.signup_kyc_fail
                        it.map = map
                    }
                }
                alertDialog.postValue(
                    DialogUtils.AlertStateUiModel(
                        icon = ContextCompat.getDrawable(
                            PockketApplication.instance,
                            R.drawable.ic_error_alert
                        )!!,
                        message = errorResponseInfo.msg,
                        backgroundColor = ContextCompat.getColor(
                            PockketApplication.instance,
                            R.color.errorAlertBgColor
                        )
                    )
                )

            }
        }
    }

    fun convertPidDataIntoBase64(pidOptions: String): String {
        val data = pidOptions.toByteArray()
        val base64 = Base64.encodeToString(data, Base64.NO_WRAP)
        Log.d(TAG, "converted base 64:- ${base64}")
        return base64
    }

    private fun getFinger(hand: String, finger: String): String? {
        if (hand == "Right") {
            when (finger) {
                "Thumb Finger" -> {
                    return "6~RightThumb"
                }
                "Index Finger" -> {
                    return "7~RightIndex"

                }
                "Middle Finger" -> {
                    return "8~RightMiddle"

                }
                "Ring Finger" -> {
                    return "9~RightRing"

                }
                "Little Finger" -> {
                    return "10~RightLittle"
                }
            }
        } else {
            when (finger) {
                "Thumb Finger" -> {
                    return "5~LeftThumb"
                }
                "Index Finger" -> {
                    return "4~LeftIndex"

                }
                "Middle Finger" -> {
                    return "3~LeftMiddle"

                }
                "Ring Finger" -> {
                    return "2~LeftRing"

                }
                "Little Finger" -> {
                    return "1~LeftLittle"
                }
            }
        }
        return null
    }

    sealed class CaptureFingerStatus {
        data class ErrorInCaptureFinger(val message: String) : CaptureFingerStatus()
        data class CapturedSuccessFully(val pidOptions: String) : CaptureFingerStatus()
        object CaptureFingerQualityIsNotGood : CaptureFingerStatus()
        object CaptureFingerQualityIsPoor : CaptureFingerStatus()
        object UnableToCaptureFinger : CaptureFingerStatus()
    }

    sealed class FillKycEvent {
        object CaptureFingrePrint : FillKycEvent()
        data class FullKycCompleted(val message: String) : FillKycEvent()
    }

}