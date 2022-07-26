package com.fypmoney.view.arcadegames.viewmodel

import android.app.Application
import com.fypmoney.base.BaseViewModel

class SlotMachineFragmentVM(application: Application): BaseViewModel(application) {
    //To store explore redirection code
    lateinit var productCode: String

    sealed class SlotMachineState{
        data class Loading(var apiName:String): SlotMachineState()
        data class Error(var apiName:String): SlotMachineState()
        data class TicketSuccess(val totalTickets: Int?): SlotMachineState()
        data class MyntsSuccess(val totalMynts: Int?): SlotMachineState()
        data class CashSuccess(val totalCash: Int?): SlotMachineState()
    }

}