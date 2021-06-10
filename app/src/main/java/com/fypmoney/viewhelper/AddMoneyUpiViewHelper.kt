package com.fypmoney.viewhelper

import com.fypmoney.model.UpiModel
import com.fypmoney.view.adapter.AddMoneyUpiAdapter

class AddMoneyUpiViewHelper(
    var position: Int,
    var upi: UpiModel?,
    var onUpiClickListener: AddMoneyUpiAdapter.OnUpiClickListener
) {

    fun onItemClicked() {
        onUpiClickListener.onUpiItemClicked(position, upiModel = upi!!)
    }
}