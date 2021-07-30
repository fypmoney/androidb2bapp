package com.fypmoney.viewhelper

import com.fypmoney.model.StoreDataModel
import com.fypmoney.model.UpiModel
import com.fypmoney.view.adapter.AddMoneyUpiAdapter
import com.fypmoney.view.adapter.StoreItemAdapter

class StoreViewHelper(
    var position: Int,
    var upi: StoreDataModel?,
    var onUpiClickListener: StoreItemAdapter.OnStoreItemClick
) {

    fun onItemClicked() {
        onUpiClickListener.onStoreItemClicked(position, upiModel = upi!!)
    }
}