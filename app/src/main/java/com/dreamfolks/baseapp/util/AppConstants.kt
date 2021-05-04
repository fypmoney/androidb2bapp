package com.dreamfolks.baseapp.util

import com.dreamfolks.baseapp.model.CountryCode
import com.dreamfolks.baseapp.model.countryCode
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


/*
* This class is used to store the Constants of the application
* */
object AppConstants {
    const val GOOGLE_CLIENT_ID: String =
        "916744568213-gr0bfi1ued1eguo2sjr77pd45ak4796r.apps.googleusercontent.com"
    const val API_TIMEOUT_SECONDS: Long = 60
    const val DATE_FORMAT_CHANGED = "dd/MM/yyyy"
    const val DATE_TIME_FORMAT_SERVER = "yyyy-MM-dd'T'HH:mm:ss'Z'"
    const val APP_SETTINGS_PACKAGE_TEXT = "package:"
    const val EMAIL_TYPE = "EMAIL"
    const val MOBILE_TYPE = "MOBILE"
    const val PLATFORM = "ANDROID"
    const val DEFAULT_COUNTRY_CODE = "+91"
    const val REGISTRATION_MODE_APP = "APP"
    const val REGISTRATION_MODE_GOOGLE = "GOOGLE"
    const val RESEND_OTP_DURATION = 30000L
    const val RC_SIGN_IN = 9001
    const val DIALOG_MSG_AUTH = "Unlock DreamFolks"
    const val DIALOG_TITLE_AUTH = "Enter phone screen lock pattern, PIN, password or fingerprint"
    const val DEVICE_SECURITY_REQUEST_CODE = 1000
    const val READ_CONTACTS_PERMISSION_CODE = 100
    const val LOCATION_PERMISSION_REQUEST_CODE = 999
    val countryCodeList: List<CountryCode> =
        Gson().fromJson(countryCode, object : TypeToken<List<CountryCode>>() {}.type)
    const val FETCH_ALL_FEEDS_REQUEST =
        "{getAllFeed(page:0, size:200, id : null, screenName:\"Feed\",screenSection:\"Middle\") { id name description screenName screenSection sortOrder displayCard category{name code description scope } location{latitude longitude } tags resourceId title subTitle content backgroundColor action{ type url buttonText }}}"
    const val FEED_TYPE_HORIZONTAL = "HORIZONTAL"
    const val FEED_TYPE_VERTICAL = "VERTICAL"
    const val FEED_TYPE_VIDEO = "VIDEO"
    const val FEED_RESPONSE = "feed_response"
    const val FEED_TYPE_IN_APP = "INAPP"
    const val FEED_TYPE_IN_APP_WEBVIEW = "INAPPWEBVIEW"
    const val FEED_TYPE_EXTERNAL_WEBVIEW = "EXTERNALWEBVIEW"
    const val FEED_TYPE_NONE = "NONE"
    const val FEED_TYPE_FEED = "FEED"
    const val BASE_ACTIVITY_URL = "com.dreamfolks.baseapp.view.activity."
    const val MOBILE_WITHOUT_COUNTRY_CODE = "mobile_without_country_code"
    const val LEAVE_MEMBER = "leave_member"
    const val REMOVE_MEMBER = "remove_member"
    const val CONTACT_BROADCAST_NAME = "Contact Broadcast"
    const val AFTER_ADD_MEMBER_BROADCAST_NAME = "After Add Member Broadcast"
    const val CONTACT_BROADCAST_KEY = "Contact Broadcast key"
    const val ADD_MEMBER_STATUS_APPROVED = "APPROVED"
    const val ADD_MEMBER_STATUS_INVITED = "INVITED"
    const val YES = "YES"
    const val NO = "NO"
    const val CUSTOMER_INFO_RESPONSE = "customer_info_response"
    const val SPLASH_TIME = 500L
    const val FROM_WHICH_SCREEN = "from_which_screen"
    const val LOGOUT = "logout"
    const val API_SUCCESS = "Success"
    const val API_FAIL = "Fail"
    const val FEED_SCREEN_NAME = "FEED"
    const val FEED_WITHIN_RADIUS = "20"
}