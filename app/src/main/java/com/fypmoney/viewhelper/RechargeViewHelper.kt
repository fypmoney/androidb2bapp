package com.fypmoney.viewhelper

import com.fypmoney.model.StoreDataModel
import com.fypmoney.view.recharge.adapter.RechargeItemAdapter

class RechargeViewHelper(
    var position: Int,
    var upi: StoreDataModel?,
    var onUpiClickListener: RechargeItemAdapter.OnRechargeItemClick
) {

    fun onItemClicked() {
        onUpiClickListener.onRechargeItemClicked(position, upiModel = upi!!)
    }
}