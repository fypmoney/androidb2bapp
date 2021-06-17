package com.fypmoney.util

import com.fypmoney.model.CountryCode
import com.fypmoney.model.countryCode
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
    const val DIALOG_MSG_AUTH = "Unlock FYP Money"
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
    const val BASE_ACTIVITY_URL = "com.fypmoney.view.activity."
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
    const val AMOUNT = "Amount"
    const val LOGOUT = "logout"
    const val API_SUCCESS = "Success"
    const val API_FAIL = "Fail"
    const val FEED_SCREEN_NAME = "FEED"
    const val FEED_SCREEN_NAME_HOME = "HOME"
    const val FEED_WITHIN_RADIUS = "20"
    const val IS_PROFILE_COMPLETED = "isProfileCompleted"
    const val RELATION = "relation"
    const val CREATE_ACCOUNT_SUCCESS = "create_account_success"
    const val LOGIN_SCREEN = "login_screen"
    const val AADHAAR_VERIFICATION = "aadhaar_verification"
    const val KYC_MOBILE_VERIFICATION = "mobile_verification"
    const val CONTACT_SELECTED_RESPONSE = "contact_selected"
    const val NOTIFICATION_TYPE_ADD_TASK = "ADD_TASK"
    const val NOTIFICATION_TYPE_ADD_FAMILY = "ADD_FAMILY_MEMBER"
    const val NOTIFICATION_TYPE_REQUEST_MONEY = "REQUEST_MONEY"


    const val NOTIFICATION_TYPE_IN_APP_DIRECT = "IN_APP_DIRECT"
    const val NOTIFICATION_TYPE_WEB_DIRECT = "WEB_DIRECT"
    const val NOTIFICATION_TYPE_EXT_APP_DIRECT = "EXT_APP_DIRECT"
    const val NOTIFICATION_TYPE_DEFAULT = "DEFAULT"


    const val TYPE_POP_UP_NOTIFICATION = "POP_UP"
    const val TYPE_APP_SLIDER_NOTIFICATION = "APP_SLIDER"
    const val TYPE_NONE_NOTIFICATION = "NONE"

    const val STAY_TUNED_BOTTOM_SHEET = "stay_tuned"
    const val NOTIFICATION = "NOTIFICATION"
    const val NOTIFICATION_APRID = "APRID"

    const val NOTIFICATION_KEY_TYPE = "type"
    const val NOTIFICATION_KEY_NOTIFICATION_TYPE = "notificationType"
    const val NOTIFICATION_KEY_APRID = "aprid"


    const val KYC_ACTION_MOBILE_AUTH = "MOBILE_AUTH"
    const val KYC_ACTION_ADHAR_AUTH = "ADHAR_AUTH"
    const val KYC_ACTIVATION_TOKEN = "kyc token"
    const val KYC_MODE = "ADHAR_OTP"
    const val KYC_TYPE = "SEMI"
    const val KYC_DOCUMENT_TYPE = "adhar"

    const val COPY_LABEL = "label"
    const val PAY = "pay"
    const val WHICH_ACTION = "which_action"
    const val FUND_TRANSFER_TRANSACTION_TYPE = "FUND_TRANSAFER"
    const val PAYU_RESPONSE = "payu_response"
    const val SERVER_DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
    const val SERVER_DATE_TIME_FORMAT1 = "yyyy-MM-dd'T'HH:mm:ss'Z'"
    const val CHANGED_DATE_TIME_FORMAT = "MMM dd,yyyy-HH:mmaa"
    const val CHANGED_DATE_TIME_FORMAT1 = "MMM dd"
    const val CHANGED_DATE_TIME_FORMAT2 = "MMM dd HH:mmaa"
    const val PAYU_SERVER = "pay u server"


}