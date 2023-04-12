package com.fypmoney.view.kycagent.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import com.fypmoney.base.BaseViewModel
import com.fypmoney.util.livedata.LiveEvent
import com.fypmoney.view.delegate.FingreDelegate
import com.fypmoney.view.delegate.HandDelegate
import org.w3c.dom.Attr
import java.io.ByteArrayInputStream
import java.io.InputStream
import javax.xml.parsers.DocumentBuilderFactory

class TakeHandFingerFragmentVM(application: Application) : BaseViewModel(application) {

    val handDelegate = HandDelegate()
    val fingreDelegate = FingreDelegate()
    lateinit var via: String

    val kycEvent: LiveData<FillKycEvent>
        get() = _kycEvent
    private val _kycEvent = LiveEvent<FillKycEvent>()

    private val TAG = TakeHandFingerFragmentVM::class.java.simpleName
    var connectedDeviceInfo: VerifyBiometricFragmentVM.FingerDeviceInfo? = null
    var deviceName:String? = null

    fun parseXml(xml:String):CaptureFingerStatus {
        val inputStream: InputStream = ByteArrayInputStream(xml.toByteArray())

        val dom = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(inputStream)
        val nodeList = dom.getElementsByTagName("Resp")
        if(nodeList.length != 0){
            val attributes = nodeList.item(0).attributes
            val errCode = (attributes.getNamedItem("errCode") as Attr).value
            Log.d(TAG,"errCode:- $errCode")
            if(errCode=="0"){
                val qScore = (attributes.getNamedItem("qScore") as Attr).value
                Log.d(TAG,"qScore:- ${qScore}")
                qScore.toIntOrNull()?.let {
                    if(it<=10){
                        return CaptureFingerStatus.CaptureFingerQualityIsNotGood
                    }else{
                        Log.d(TAG, "pidOptions xml: ${xml}")
                        val pidOptions = dom.getElementsByTagName("PidData")
                        Log.d(TAG,"pidOptions: $pidOptions")
                        return CaptureFingerStatus.CapturedSuccessFully(xml)
                    }
                }?: kotlin.run {
                    return CaptureFingerStatus.CaptureFingerQualityIsPoor

                }

            }else{
                val errInfo = (attributes.getNamedItem("errInfo") as Attr).value

                return CaptureFingerStatus.ErrorInCaptureFinger(errInfo)

            }


        }
        return CaptureFingerStatus.UnableToCaptureFinger


    }

    sealed class CaptureFingerStatus{
        data class ErrorInCaptureFinger(val message:String):CaptureFingerStatus()
        data class CapturedSuccessFully(val pidOptions:String):CaptureFingerStatus()
        object CaptureFingerQualityIsNotGood:CaptureFingerStatus()
        object CaptureFingerQualityIsPoor:CaptureFingerStatus()
        object UnableToCaptureFinger:CaptureFingerStatus()
    }

    sealed class FillKycEvent{
        object CaptureFingrePrint:FillKycEvent()
        object FullKycCompleted:FillKycEvent()
    }

}