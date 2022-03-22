package com.fypmoney.view.discord.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.fyp.trackr.models.*
import com.fyp.trackr.services.TrackrServices
import com.fypmoney.application.PockketApplication
import com.fypmoney.base.BaseViewModel
import com.fypmoney.connectivity.ApiConstant
import com.fypmoney.connectivity.ApiUrl
import com.fypmoney.connectivity.ErrorResponseInfo
import com.fypmoney.connectivity.network.NetworkUtil
import com.fypmoney.connectivity.retrofit.ApiRequest
import com.fypmoney.connectivity.retrofit.WebApiCaller
import com.fypmoney.model.CustomerInfoResponse
import com.fypmoney.model.SettingsResponse
import com.fypmoney.util.AppConstants
import com.fypmoney.util.SharedPrefUtils
import com.fypmoney.util.Utility
import com.fypmoney.util.livedata.LiveEvent
import com.fypmoney.view.discord.model.DiscordProfileResponse
import com.fypmoney.view.referandearn.model.ReferMessageResponse
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.moengage.core.internal.MoEConstants

class DiscordFailBottomSheetVM(application: Application) : BaseViewModel(application) {

}