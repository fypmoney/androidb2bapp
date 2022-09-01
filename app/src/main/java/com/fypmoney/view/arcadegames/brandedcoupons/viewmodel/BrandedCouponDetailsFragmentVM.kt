package com.fypmoney.view.arcadegames.brandedcoupons.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.fypmoney.R
import com.fypmoney.base.BaseViewModel
import com.fypmoney.connectivity.ApiConstant
import com.fypmoney.connectivity.ApiUrl
import com.fypmoney.connectivity.ErrorResponseInfo
import com.fypmoney.connectivity.network.NetworkUtil
import com.fypmoney.connectivity.retrofit.ApiRequest
import com.fypmoney.connectivity.retrofit.WebApiCaller
import com.fypmoney.model.BaseRequest
import com.fypmoney.util.livedata.LiveEvent
import com.fypmoney.view.arcadegames.brandedcoupons.adapter.CouponDetailsTitleUiModel
import com.fypmoney.view.arcadegames.brandedcoupons.model.BrandedCouponDetailsResponse
import com.fypmoney.view.arcadegames.brandedcoupons.model.BrandedCouponDetailsUiModel
import com.fypmoney.view.arcadegames.brandedcoupons.model.CouponDetails

class BrandedCouponDetailsFragmentVM(application: Application) : BaseViewModel(application) {


    lateinit var brandedCouponDetailsUiModel: BrandedCouponDetailsUiModel

    val state: LiveData<BrandedCouponDetailsState>
        get() = _state
    private val _state = MutableLiveData<BrandedCouponDetailsState>()

    val event :LiveData<BrandedCouponEvent>
        get() = _event
    private val _event = LiveEvent<BrandedCouponEvent>()


    fun showDetails(){
         with(brandedCouponDetailsUiModel){
            val listOfCouponDetailsTitle = mutableListOf<CouponDetailsTitleUiModel>()
            listOfCouponDetailsTitle.add(
                CouponDetailsTitleUiModel(
                    "How to redeem?",
                    R.drawable.icon_discount,
                    howToRedeem,
                    false
                )
            )
            listOfCouponDetailsTitle.add(
                CouponDetailsTitleUiModel(
                    "Offer details",
                    R.drawable.ic_receipt_search,
                    description,
                    false
                )
            )
            listOfCouponDetailsTitle.add(
                CouponDetailsTitleUiModel(
                    "Terms & Condition",
                    R.drawable.ic_receipt_item,
                    tnc,
                    false
                )
            )
            _state.value = BrandedCouponDetailsState.PopulateCouponDetails(
                brandedCouponDetailsUiModel = this
                , listOfCouponDetails = listOfCouponDetailsTitle
            )
        }

    }

    fun copyCouponCode(){
        _event.value = BrandedCouponEvent.CopyCouponCode
    }
    fun redeemNow(){
        _event.value = BrandedCouponEvent.RedeemNow
    }

    sealed class BrandedCouponDetailsState {
        data class PopulateCouponDetails(var brandedCouponDetailsUiModel: BrandedCouponDetailsUiModel,
                                         var listOfCouponDetails:List<CouponDetailsTitleUiModel>
        ):BrandedCouponDetailsState()
        object ErrorWhileShowingCouponDetails:BrandedCouponDetailsState()

    }
    sealed class BrandedCouponEvent{
        object CopyCouponCode:BrandedCouponEvent()
        object RedeemNow:BrandedCouponEvent()
    }

}