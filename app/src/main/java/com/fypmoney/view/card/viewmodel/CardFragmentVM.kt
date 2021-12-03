package com.fypmoney.view.card.viewmodel

import android.app.Application
import androidx.appcompat.content.res.AppCompatResources
import com.fypmoney.R
import com.fypmoney.application.PockketApplication
import com.fypmoney.base.BaseViewModel
import com.fypmoney.connectivity.ErrorResponseInfo
import com.fypmoney.model.BankProfileResponse
import com.fypmoney.model.BankProfileResponseDetails
import com.fypmoney.view.card.model.CardOptionEvent
import com.fypmoney.view.card.model.CardOptionUiModel

class CardFragmentVM(application: Application) : BaseViewModel(application) {
    lateinit var bankProfileResponse: BankProfileResponse

    fun prepareInitialCardOption(){
        val cardOptionList = mutableListOf<CardOptionUiModel>()
        cardOptionList.add(CardOptionUiModel(optionEvent = CardOptionEvent.OrderCard,
            icon = AppCompatResources.getDrawable(PockketApplication.instance, R.drawable.ic_order_card),
            name = PockketApplication.instance.getString(R.string.order_card)))
        cardOptionList.add(CardOptionUiModel(optionEvent = CardOptionEvent.CardSettings,
            icon = AppCompatResources.getDrawable(PockketApplication.instance, R.drawable.ic_card_settings),
            name = PockketApplication.instance.getString(R.string.card_settings)))
        cardOptionList.add(CardOptionUiModel(optionEvent = CardOptionEvent.CardSettings,
            icon = AppCompatResources.getDrawable(PockketApplication.instance, R.drawable.ic_account_statement),
            name = PockketApplication.instance.getString(R.string.account_stmt)))
    }

    sealed class CardState{
        object Loading:CardState()
        data class BankProfileResponse(var bankProfileResponse: BankProfileResponseDetails):CardState()
        data class Error(var error: ErrorResponseInfo):CardState()
    }
}