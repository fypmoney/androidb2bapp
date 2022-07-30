package com.fypmoney.view.insights.view

import android.app.Application
import androidx.annotation.Keep
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.fypmoney.R
import com.fypmoney.application.PockketApplication
import com.fypmoney.base.BaseViewModel
import com.fypmoney.connectivity.ApiConstant
import com.fypmoney.connectivity.ApiConstant.API_CHANGE_TXN_CATEGORY
import com.fypmoney.connectivity.ApiUrl
import com.fypmoney.connectivity.ErrorResponseInfo
import com.fypmoney.connectivity.network.NetworkUtil
import com.fypmoney.connectivity.retrofit.ApiRequest
import com.fypmoney.connectivity.retrofit.WebApiCaller
import com.fypmoney.model.BaseRequest
import com.fypmoney.model.CashbackEarnedResponse
import com.fypmoney.model.RewardsEarnedResponse
import com.fypmoney.util.AppConstants
import com.fypmoney.util.Utility
import com.fypmoney.util.livedata.LiveEvent
import com.fypmoney.view.insights.model.*
import com.fypmoney.view.insights.model.MerchantCategoryUiModel.Companion.mapMerchantCategoryToMerchantCategoryUiModel
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import com.google.gson.JsonParser.parseString
import org.json.JSONObject

class CategoryWaiseTransactionDetailsFragmentVM(application: Application) : BaseViewModel(application) {
    lateinit var txnItem: AllTxnItem

    val state:LiveData<CategoryWaiseTxnDetailsState>
        get() = _state
    private val _state = MutableLiveData<CategoryWaiseTxnDetailsState>()

    val cashbackState:LiveData<CashbackEarnedState>
        get() = _cashbackState
    private val _cashbackState = MutableLiveData<CashbackEarnedState>()

    val rewardsState:LiveData<RewardsEarnedState>
        get() = _rewardsState
    private val _rewardsState = MutableLiveData<RewardsEarnedState>()

    val event:LiveData<CategoryWaiseTxnDetailsEvent>
        get() = _event
    private val _event = LiveEvent<CategoryWaiseTxnDetailsEvent>()

    fun onGetHelpClick(){
        _event.value = CategoryWaiseTxnDetailsEvent.ShowHelp
    }

    fun onCategoryEditClick(){
        getMerchantCategory()
    }
    fun callCashbackEarned() {
        _cashbackState.postValue(CashbackEarnedState.Loading)
        WebApiCaller.getInstance().request(
            ApiRequest(
                purpose = ApiConstant.API_GET_CASHBACK_EARNED,
                endpoint = NetworkUtil.endURL(ApiConstant.API_GET_CASHBACK_EARNED+"${txnItem.mrn}/"),
                request_type = ApiUrl.GET,
                onResponse = this, isProgressBar = false,
                param = ""
            )
        )
    }
    fun callRewardsEarned() {
        _rewardsState.postValue(RewardsEarnedState.Loading)
        WebApiCaller.getInstance().request(
            ApiRequest(
                purpose = ApiConstant.API_GET_REWARDS_EARNED,
                endpoint = NetworkUtil.endURL(ApiConstant.API_GET_REWARDS_EARNED+"${txnItem.mrn}/"),
                request_type = ApiUrl.GET,
                onResponse = this, isProgressBar = false,
                param = ""
            )
        )
    }

    private fun getMerchantCategory(){
        WebApiCaller.getInstance().request(
            ApiRequest(
                purpose = ApiConstant.API_GET_MERCHANT_CATEGORY,
                endpoint = NetworkUtil.endURL(ApiConstant.API_GET_MERCHANT_CATEGORY),
                request_type = ApiUrl.POST,
                onResponse = this, isProgressBar = true,
                param = BaseRequest()
            )
        )
    }
    fun postCategoryChangeData(categoryCode:String){
        WebApiCaller.getInstance().request(
            ApiRequest(
                purpose = API_CHANGE_TXN_CATEGORY,
                endpoint = NetworkUtil.endURL(API_CHANGE_TXN_CATEGORY),
                request_type = ApiUrl.PUT,
                onResponse = this, isProgressBar = true,
                param = ChangeTxnCategory(accReferenceNumber = txnItem.accReferenceNumber!!, categoryCode = categoryCode)
            )
        )
    }
    fun showData(){
        val categoryTxnDetailsUiModel = CategoryTxnDetailsUiModel(
            txnCategoryImage = txnItem.iconLink,
            txnName = txnItem.userName,
            txnUserMobileNumber = txnItem.mobileNo,
            txnAmount = String.format(PockketApplication.instance.getString(R.string.amount_with_currency),Utility.convertToRs(txnItem.amount)),
            txnStatusDateAndTime = if(txnItem.transactionType=="Debited") String.format(PockketApplication.instance.getString(R.string.sent_with_time),
                Utility.parseDateTimeWithPlusFiveThirty(txnItem.transactionDate?.replace("+05:30","Z"),
                AppConstants.SERVER_DATE_TIME_FORMAT3,
                AppConstants.CHANGED_DATE_TIME_FORMAT3
            ),) else if(txnItem.transactionType.equals("Credited",false)) String.format(PockketApplication.instance.getString(R.string.reccived_with_time),
                Utility.parseDateTimeWithPlusFiveThirty(txnItem.transactionDate?.replace("+05:30","Z"),
                    AppConstants.SERVER_DATE_TIME_FORMAT3,
                    AppConstants.CHANGED_DATE_TIME_FORMAT3
                ),)
                    else{
                        ""
            },
            txnCategory = txnItem.category,
            txnFypTxnId = txnItem.accReferenceNumber,
            bankTxnId = txnItem.bankReferenceNumber
        )
        _state.value = CategoryWaiseTxnDetailsState.ShowTxnDetails(categoryTxnDetailsUiModel)
    }

    override fun onSuccess(purpose: String, responseData: Any) {
        super.onSuccess(purpose, responseData)
        when(purpose){
            ApiConstant.API_GET_CASHBACK_EARNED->{
                val data = Gson().fromJson(responseData.toString(), CashbackEarnedResponse::class.java)
                if(data.data.amount!=0){
                    _cashbackState.value = CashbackEarnedState.Success(cashbackAmount = data.data.getAmountInRuppes()!!)
                }else{
                    _cashbackState.value = CashbackEarnedState.CashbackNotReceived
                }

            }
            ApiConstant.API_GET_REWARDS_EARNED->{
                val data = Gson().fromJson(responseData.toString(), RewardsEarnedResponse::class.java)
                if(!data.data.points.isNullOrEmpty() && data.data.points!="0"){
                    _rewardsState.value = RewardsEarnedState.Success(rewardsEarned = data.data.points)
                }else{
                    _rewardsState.value = RewardsEarnedState.RewardsNotReceived
                }
            }
            ApiConstant.API_GET_MERCHANT_CATEGORY->{
                val response = JSONObject(responseData.toString())

                val daa = response.getString("data")
                val jsonArray = parseString(daa) as JsonArray
                val list= GsonBuilder().create().fromJson(jsonArray,Array<MerchantCategory>::class.java).toList()
                _event.value = list.map {
                    mapMerchantCategoryToMerchantCategoryUiModel(it,txnItem.categoryCode!!)
                }.let { CategoryWaiseTxnDetailsEvent.ShowMerchantCategoryListBottomsheet(it) }
            }
            API_CHANGE_TXN_CATEGORY->{
                val data = getObject(responseData.toString(),UpdatedCategoryResponse::class.java)
                if(data is UpdatedCategoryResponse){
                    txnItem.category = data.data?.category!!
                    txnItem.iconLink = data.data.iconLink!!
                    txnItem.categoryCode = data.data.categoryCode!!

                    _state.value = CategoryWaiseTxnDetailsState.MerchantCategoryUpdated(
                        MerchantCategory(category = data.data.category, iconLink = data.data.iconLink, categoryCode = data.data.categoryCode)
                    )
                }
            }
        }
    }

    override fun onError(purpose: String, errorResponseInfo: ErrorResponseInfo) {
        super.onError(purpose, errorResponseInfo)
        when(purpose){
            ApiConstant.API_GET_CASHBACK_EARNED->{
                _cashbackState.value = CashbackEarnedState.Error

            }
            ApiConstant.API_GET_REWARDS_EARNED->{
               _rewardsState.value = RewardsEarnedState.Error

            }

            ApiConstant.API_GET_MERCHANT_CATEGORY->{
                _state.value = CategoryWaiseTxnDetailsState.MerchantCategoryListError
            }
            API_CHANGE_TXN_CATEGORY->{
                _state.value = CategoryWaiseTxnDetailsState.ChangeMerchantCategoryError
            }
        }
    }


    sealed class CategoryWaiseTxnDetailsState{
        data class ShowTxnDetails(var categoryTxnDetailsUiModel: CategoryTxnDetailsUiModel):CategoryWaiseTxnDetailsState()
        object MerchantCategoryListError:CategoryWaiseTxnDetailsState()
        object ChangeMerchantCategoryError:CategoryWaiseTxnDetailsState()
        data class MerchantCategoryUpdated(val changeCategory: MerchantCategory):CategoryWaiseTxnDetailsState()
    }

    sealed class CashbackEarnedState{
        object Loading:CashbackEarnedState()
        object Error:CashbackEarnedState()
        object CashbackNotReceived:CashbackEarnedState()
        data class Success(val cashbackAmount: String):CashbackEarnedState()
    }
    sealed class RewardsEarnedState{
        object Loading:RewardsEarnedState()
        object Error:RewardsEarnedState()
        object RewardsNotReceived:RewardsEarnedState()
        data class Success(val rewardsEarned: String):RewardsEarnedState()
    }
    sealed class CategoryWaiseTxnDetailsEvent{
        object ShowHelp:CategoryWaiseTxnDetailsEvent()
        data class ShowMerchantCategoryListBottomsheet(val category: List<MerchantCategoryUiModel>):CategoryWaiseTxnDetailsEvent()

    }

    @Keep
    data class CategoryTxnDetailsUiModel(
        var txnCategoryImage:String?,
        var txnName:String?,
        var txnUserMobileNumber:String?,
        var txnAmount:String?,
        var txnStatusDateAndTime:String?,
        var txnCategory:String?,
        var txnFypTxnId:String?,
        var bankTxnId:String?
    )
    private fun <T> getObject(response: String, instance: Class<T>): Any? {
        return Gson().fromJson(response, instance)
    }

}