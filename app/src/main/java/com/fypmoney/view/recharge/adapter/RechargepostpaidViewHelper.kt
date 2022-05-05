package com.fypmoney.view.recharge.adapter

import com.fypmoney.model.StoreDataModel
import com.fypmoney.view.recharge.adapter.RechargeItemAdapter

class RechargepostpaidViewHelper(
    var position: Int,
    var upi: StoreDataModel?,
    var onUpiClickListener: RechargePostpaidAdapter.OnRechargeItemClick
) {

    fun onItemClicked() {
        onUpiClickListener.onRechargePostpaid(position, upiModel = upi!!)
    }
}