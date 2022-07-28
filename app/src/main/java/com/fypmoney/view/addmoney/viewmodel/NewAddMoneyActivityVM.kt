package com.fypmoney.view.addmoney.viewmodel

import android.app.Application
import android.graphics.Bitmap
import android.graphics.Color
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
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
import com.fypmoney.model.BaseRequest
import com.fypmoney.model.GetWalletBalanceResponse
import com.fypmoney.util.Utility
import com.fypmoney.util.livedata.LiveEvent
import com.fypmoney.view.addmoney.model.BankDetailsUiModel
import com.fypmoney.view.addmoney.model.BankProfileDetailsNetworkResponse
import com.fypmoney.view.addmoney.model.LoadMoneyWalletBalanceUIModel
import com.fypmoney.view.addmoney.model.Visibility
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter

class NewAddMoneyActivityVM(application: Application):BaseViewModel(application) {


    val state:LiveData<BankDetailsState>
        get() = _state
    private val _state = MutableLiveData<BankDetailsState>()

    val event:LiveData<BankDetailsEvent>
        get() = _event
    private val _event = LiveEvent<BankDetailsEvent>()
    //private var isClicked = true
    init {
        callGetWalletBalanceApi()
        getBankDetails()
    }



    fun onIncreaseLimitClicked(){
        trackr {it1->
            it1.name = TrackrEvent.increase_limit_clicked
        }
        _event.value = BankDetailsEvent.OnIncreaseLimitClicked
    }

    fun onLoadMoneyViaPayUClicked(){
        _event.value = BankDetailsEvent.AddViaPayUClicked
    }
    fun onHowItWorks(){
        _event.value = BankDetailsEvent.HowItWorks
    }

    fun onShareVirtualAccountInfo(){
        _event.value = BankDetailsEvent.ShareVirtualAccountInfo
    }
    fun onShareUpiQrCode(){
        _event.value = BankDetailsEvent.ShareQRCodeAndUpi
    }
    fun copyAccountNo(){
        _event.value = BankDetailsEvent.CopyAccountNumber
    }
    fun copyIfscCode(){
        _event.value = BankDetailsEvent.CopyIFSC
    }
    fun copyUPIid(){
        _event.value = BankDetailsEvent.CopyUPIId
    }
    fun watchHowAddMoneyVideo(){
        _event.value = BankDetailsEvent.WatchAddMoneyVideo
    }

    fun reloadAddViaUpiDetails(){
        //isClicked = false
        _event.value = BankDetailsEvent.ReloadAddViaUPI
        getBankDetails()
    }

    /*
       * This method is used to get the balance of wallet
       * */
    fun callGetWalletBalanceApi() {
        _state.postValue(BankDetailsState.BalanceIsLoading)
        WebApiCaller.getInstance().request(
            ApiRequest(
                ApiConstant.API_GET_WALLET_BALANCE,
                NetworkUtil.endURL(ApiConstant.API_GET_WALLET_BALANCE),
                ApiUrl.GET,
                BaseRequest(),
                this, isProgressBar = false
            )
        )
    }

    private fun getBankDetails() {
        _state.postValue(BankDetailsState.Loading)
        WebApiCaller.getInstance().request(
            ApiRequest(
                ApiConstant.API_BANK_DETAILS,
                NetworkUtil.endURL(ApiConstant.API_BANK_DETAILS),
                ApiUrl.GET,
                BaseRequest(),
                this, isProgressBar = false
            )
        )
    }


    override fun onSuccess(purpose: String, responseData: Any) {
        super.onSuccess(purpose, responseData)
        when (purpose) {
            ApiConstant.API_GET_WALLET_BALANCE -> {
                if (responseData is GetWalletBalanceResponse) {
                    _state.value = BankDetailsState.BalanceSuccess(LoadMoneyWalletBalanceUIModel(
                        balance = Utility.getFormatedAmount(Utility.convertToRs(responseData.getWalletBalanceResponseDetails.accountBalance)!!),
                        remainingLoadLimitTxt = PockketApplication.instance.getString(R.string.add_money_screen_text) + Utility.convertToRs(
                            responseData.getWalletBalanceResponseDetails.remainingLoadLimit
                        ) + PockketApplication.instance.getString(R.string.add_money_screen_text1),
                        instructionOnBankTransfer = "Receive money using UPI apps or net banking \n" +
                                "Minimum: ₹10 | Maximum: ₹${Utility.convertToRs(responseData.getWalletBalanceResponseDetails.remainingLoadLimit)}",
                        instructionOnUpiQrCode = String.format(PockketApplication.instance.getString(R.string.instruction_on_static_qr),Utility.convertToRs(responseData.getWalletBalanceResponseDetails.remainingLoadLimit))

                    ))
                }
            }
            ApiConstant.API_BANK_DETAILS->{
                if(responseData is BankProfileDetailsNetworkResponse){
                    _state.value = BankDetailsState.Success
                    if(responseData.data!!.isNotEmpty()){
                        //Check for upi transfer
                        responseData.data.forEach {
                            if(it!!.mode=="SHOW_VIRTUAL_ACCOUNT_INFO"){
                                _state.value = BankDetailsState.LoadViaBankTransfer(
                                    data = BankDetailsUiModel(
                                        loadMoneyMod = it.mode!!,
                                        modeVisibility = if((it.toShow!!)=="YES") Visibility.Visible else Visibility.InVisible,
                                        identifier1 = it.identifier1,
                                        identifier2 = it.identifier2
                                    )
                                )
                            }else if(it.mode=="SHOW_STATIC_QR_INFO"){
                                /*if(isClicked){
                                    it.toShow = "YES"
                                }else{
                                    it.toShow = "YES"
                                    it.identifier1 = "test@Gmail.com"
                                    it.identifier2 = "dsadasd"
                                }*/
                                if(((it.toShow!!)=="YES") && (it.identifier1.isNullOrEmpty() || it.identifier2.isNullOrEmpty())){
                                    _state.value = BankDetailsState.UnableToGenerateUPIIdState
                                }else{
                                    _state.value = BankDetailsState.LoadViaUPIOrQRCode(
                                        data = BankDetailsUiModel(
                                            loadMoneyMod = it.mode,
                                            modeVisibility = if((it.toShow)=="YES") Visibility.Visible else Visibility.InVisible,
                                            identifier1 = it.identifier1,
                                            identifier2 = it.identifier2
                                        )
                                    )
                                    if((it.toShow)=="YES" && !it.identifier2.isNullOrEmpty()){
                                        generateQrCodeForUpiId(it.identifier2!!,onQrCodeGenerated= {
                                            _state.value = BankDetailsState.QRCodeGenerated(it)
                                        })
                                    }

                                }

                            }else if(it.mode=="PG_PAYU_INFO"){
                                _state.value = BankDetailsState.LoadViaPayU(
                                    data = BankDetailsUiModel(
                                        loadMoneyMod = it.mode,
                                        modeVisibility = if((it.toShow!!)=="YES") Visibility.Visible else Visibility.InVisible,
                                        identifier1 = it.identifier1,
                                        identifier2 = it.identifier2
                                    )
                                )
                            }
                        }

                    }else{
                        _state.value = BankDetailsState.Error
                    }
                }
            }

        }
    }

    override fun onError(purpose: String, errorResponseInfo: ErrorResponseInfo) {
        super.onError(purpose, errorResponseInfo)
        when (purpose) {
            ApiConstant.API_GET_WALLET_BALANCE -> {
                _state.value = BankDetailsState.BalanceError
            }
            ApiConstant.API_BANK_DETAILS -> {
                _state.value = BankDetailsState.Error
            }
        }

    }

    sealed class BankDetailsState{
        object Loading:BankDetailsState()
        object Error:BankDetailsState()
        object BalanceIsLoading:BankDetailsState()
        object BalanceError:BankDetailsState()
        object Success:BankDetailsState()
        data class BalanceSuccess(var loadMoneyWalletBalanceUIModel: LoadMoneyWalletBalanceUIModel):BankDetailsState()
        data class LoadViaPayU(var data: BankDetailsUiModel):BankDetailsState()
        data class LoadViaUPIOrQRCode(var data: BankDetailsUiModel):BankDetailsState()
        data class LoadViaBankTransfer(var data: BankDetailsUiModel):BankDetailsState()
        data class QRCodeGenerated(val qrCode:Bitmap):BankDetailsState()
        object UnableToGenerateUPIIdState:BankDetailsState()
    }

    sealed class BankDetailsEvent{
        object AddViaPayUClicked:BankDetailsEvent()
        object OnIncreaseLimitClicked:BankDetailsEvent()
        object HowItWorks:BankDetailsEvent()
        object ShareVirtualAccountInfo:BankDetailsEvent()
        object ShareQRCodeAndUpi:BankDetailsEvent()
        object CopyAccountNumber:BankDetailsEvent()
        object CopyIFSC:BankDetailsEvent()
        object CopyUPIId:BankDetailsEvent()
        object WatchAddMoneyVideo:BankDetailsEvent()
        object ReloadAddViaUPI:BankDetailsEvent()
    }

    private fun generateQrCodeForUpiId(upiId:String, onQrCodeGenerated:(bitMap: Bitmap)->Unit){
        val writer = QRCodeWriter()
        val bitMatrix = writer.encode(upiId, BarcodeFormat.QR_CODE, 512, 512)
        val width = bitMatrix.width
        val height = bitMatrix.height
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
        for (x in 0 until width) {
            for (y in 0 until height) {
                bitmap.setPixel(x, y, if (bitMatrix.get(x, y)) Color.BLACK else Color.WHITE)
            }
        }
        onQrCodeGenerated(bitmap)
    }

}