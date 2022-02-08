package com.fypmoney.view.discord.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import com.fypmoney.base.BaseViewModel
import com.fypmoney.util.livedata.LiveEvent

class DiscordActivityVM(application: Application) : BaseViewModel(application) {

    val event: LiveData<DiscordEvent>
        get() = _event
    private val _event = LiveEvent<DiscordEvent>()

    fun connectToDiscord() {
        _event.value = DiscordEvent.contect
    }


    sealed class DiscordEvent {
        object contect : DiscordEvent()
    }
}