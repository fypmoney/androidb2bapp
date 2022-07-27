package com.fypmoney.view.insights.view

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.fypmoney.application.PockketApplication
import com.fypmoney.base.BaseViewModel
import com.fypmoney.view.insights.model.AllTxnItem
import com.fypmoney.view.insights.model.CategoriesWiseTransactionUiModel
import com.fypmoney.view.insights.model.CategoriesWiseTransactionUiModel.Companion.mapAllTxnItemToCategoriesWiseTransactionUiModel

class CategoryWaiseTransactionListFragmentVM(application: Application) : BaseViewModel(application) {
    lateinit var categoryName:String
    var categoryWiseTxnList:List<AllTxnItem> = mutableListOf()

    val state:LiveData<CategoryWiseTxnListState>
        get() = _state
    private val _state = MutableLiveData<CategoryWiseTxnListState>()

    fun populateTxnList(){
        if(categoryWiseTxnList.isEmpty()){
            _state.value = CategoryWiseTxnListState.TxnIsEmpty
        }else{
            _state.value = CategoryWiseTxnListState.TxnFound(categoryWiseTxnList.map {
                mapAllTxnItemToCategoriesWiseTransactionUiModel(PockketApplication.instance,it)
            })
        }
    }
    sealed class CategoryWiseTxnListState{
        object TxnIsEmpty:CategoryWiseTxnListState()
        data class TxnFound(val categoriesWiseTransactionUiModel: List<CategoriesWiseTransactionUiModel>):CategoryWiseTxnListState()
    }
}