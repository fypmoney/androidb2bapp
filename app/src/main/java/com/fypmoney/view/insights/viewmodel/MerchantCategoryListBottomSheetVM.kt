package com.fypmoney.view.insights.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.fypmoney.base.BaseViewModel
import com.fypmoney.view.insights.model.MerchantCategoryUiModel

class MerchantCategoryListBottomSheetVM(application: Application):BaseViewModel(application) {

    lateinit var listOfCategory:List<MerchantCategoryUiModel>
    val state:LiveData<MerchantCategoryListState>
        get() = _state
    private val _state = MutableLiveData<MerchantCategoryListState>()

    fun showCategories(){
        _state.value = MerchantCategoryListState.ShowCategoryList(listOfCategory)
    }
    sealed class MerchantCategoryListState{
        data class ShowCategoryList(val list:List<MerchantCategoryUiModel>):MerchantCategoryListState()
    }


}