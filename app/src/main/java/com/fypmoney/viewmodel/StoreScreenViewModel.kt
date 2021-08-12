package com.fypmoney.viewmodel

import android.app.Application
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.fypmoney.R
import com.fypmoney.application.PockketApplication
import com.fypmoney.base.BaseViewModel
import com.fypmoney.model.*
import com.fypmoney.view.adapter.RechargeItemAdapter
import com.fypmoney.view.adapter.StoreItemAdapter

class StoreScreenViewModel(application: Application) : BaseViewModel(application),
   StoreItemAdapter.OnStoreItemClick,RechargeItemAdapter.OnRechargeItemClick {
    var availableAmount =
        ObservableField(PockketApplication.instance.getString(R.string.dummy_amount))
    var onAddMoneyClicked = MutableLiveData(false)
    var onPayClicked = MutableLiveData(false)
    var onChoreClicked = MutableLiveData(false)
    var isFetchBalanceVisible = ObservableField(true)
    var isFeedVisible = ObservableField(false)
    var onUpiClicked = MutableLiveData<StoreDataModel>()
    var onRechargeClicked = MutableLiveData<StoreDataModel>()

    var onFeedButtonClick = MutableLiveData<FeedDetails>()
    var storeAdapter = StoreItemAdapter(this, application.applicationContext)
    var rechargeItemAdapter = RechargeItemAdapter(this)
    init {

    }

    /*
    * This is used to handle add money
    * */
    fun onAddMoneyClicked() {
        onAddMoneyClicked.value = true
    }

    /*
    * This is used to handle pay button click
    * */
    fun onPayClicked() {
        onPayClicked.value = true

    }

    /*
     * This method is used to get the balance of wallet
     * */



    /*
   * This is used to handle Open Chore
   * */
    /* fun onChoreClicked() {
         onChoreClicked.value = true

     }*/
    /*
   * This method is used to make fetch feeds request
   * */


    var clickedPositionForUpi = ObservableField<Int>()


    override fun onStoreItemClicked(position: Int, upiModel: StoreDataModel?) {
        clickedPositionForUpi.set(position)
        onUpiClicked.value = upiModel!!
    }


    override fun onRechargeItemClicked(position: Int, upiModel: StoreDataModel?) {
        clickedPositionForUpi.set(position)
        onRechargeClicked.value = upiModel!!
    }
}