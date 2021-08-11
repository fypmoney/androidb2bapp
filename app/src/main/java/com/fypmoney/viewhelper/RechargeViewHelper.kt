package com.fypmoney.viewhelper

import com.fypmoney.model.StoreDataModel
import com.fypmoney.model.UpiModel
import com.fypmoney.view.adapter.AddMoneyUpiAdapter
import com.fypmoney.view.adapter.RechargeItemAdapter
import com.fypmoney.view.adapter.StoreItemAdapter

class RechargeViewHelper(
    var position: Int,
    var upi: StoreDataModel?,
    var onUpiClickListener: RechargeItemAdapter.OnRechargeItemClick
) {

    fun onItemClicked() {
        onUpiClickListener.onRechargeItemClicked(position, upiModel = upi!!)
    }
}