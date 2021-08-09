package com.fypmoney.view.community

import android.app.Application
import androidx.lifecycle.LiveData
import com.fypmoney.base.BaseViewModel
import com.fypmoney.util.livedata.LiveEvent

class SocialCommunityActivityVM(application: Application):BaseViewModel(application) {

    val event:LiveData<SocialCommunityEvent>
        get() = _event
    private val _event = LiveEvent<SocialCommunityEvent>()

    fun joinOnInstagramClick(){
        _event.value = SocialCommunityEvent.JoinOnInstagram
    }
    fun joinOnFacebookClick(){
        _event.value = SocialCommunityEvent.JoinOnFacebook
    }
    fun joinOnYoutubeClick(){
        _event.value = SocialCommunityEvent.JoinOnYoutube
    }

    sealed class SocialCommunityEvent{
        object JoinOnInstagram:SocialCommunityEvent()
        object JoinOnFacebook:SocialCommunityEvent()
        object JoinOnYoutube:SocialCommunityEvent()
    }
}