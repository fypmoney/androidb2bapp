package com.fypmoney.view.giftcard.viewModel

import android.app.Application
import android.graphics.Color
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.fypmoney.base.BaseViewModel
import com.fypmoney.extension.SimpleTextUiModel
import com.fypmoney.util.livedata.LiveEvent
import org.json.JSONArray

class HowToRedeemGiftCardBottomSheetVM(application: Application):BaseViewModel(application) {
    lateinit var howToRedeemTxt:String
    lateinit var howToRedeemLink:String

    val event: LiveData<HowToRedeemGiftCardEvent>
        get() = _event
    private val _event = LiveEvent<HowToRedeemGiftCardEvent>()

   val state: LiveData<HowToRedeemGiftCardState>
        get() = _state
    private val _state = MutableLiveData<HowToRedeemGiftCardState>()

    fun onRedeemNowClick(){
        _event.value = HowToRedeemGiftCardEvent.NavigateToBrandShoppingScreen(howToRedeemLink)
    }
    fun showHowToRedeemSteps(){
        val jsonArr = JSONArray(howToRedeemTxt)
        val list: ArrayList<SimpleTextUiModel> = ArrayList()
        for (i in 0 until jsonArr.length()) {
            list.add(SimpleTextUiModel(txt = jsonArr[i] as String, txtColor = Color.WHITE))
        }
        _state.value = HowToRedeemGiftCardState.HowToRedeemSteps(list)

    }
    sealed class HowToRedeemGiftCardState{
        data class HowToRedeemSteps(val steps:List<SimpleTextUiModel>):HowToRedeemGiftCardState()
    }

    sealed class HowToRedeemGiftCardEvent{
        data class NavigateToBrandShoppingScreen(var howToRedeemLink:String):HowToRedeemGiftCardEvent()
    }
}