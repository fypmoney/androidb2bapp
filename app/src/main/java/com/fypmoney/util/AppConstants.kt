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
    const val FRESH_CHAT_APP_KEY="8dbdc877-4045-4b7c-8a69-1b176c41e71c"
    const val FRESH_CHAT_APP_ID="03bfae8e-f0f7-48a9-948a-b737edd1a38b"
    const val FRESH_CHAT_DOMAIN="msdk.in.freshchat.com"
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
    const val DIALOG_MSG_AUTH = "Unlock Fyp"
    const val DIALOG_TITLE_AUTH = "Enter phone screen lock pattern, PIN, password or fingerprint"
    const val DEVICE_SECURITY_REQUEST_CODE = 1000
    const val PERMISSION_CODE = 100
    const val LOCATION_PERMISSION_REQUEST_CODE = 999
    val countryCodeList: List<CountryCode> =
        Gson().fromJson(countryCode, object : TypeToken<List<CountryCode>>() {}.type)
    const val FETCH_ALL_FEEDS_REQUEST =
        "{getAllFeed(page:0, size:200, id : null, screenName:\"Feed\",screenSection:\"Middle\") { id name description screenName screenSection sortOrder displayCard category{name code description scope } location{latitude longitude } tags resourceId title subTitle content backgroundColor action{ type url buttonText }}}"
    const val FEED_TYPE_HORIZONTAL = "HORIZONTAL"
    const val FEED_TYPE_VERTICAL = "VERTICAL"
    const val FEED_TYPE_VIDEO = "VIDEO"
    const val FEED_TYPE_DID_YOU_KNOW = "DIDYOUKNOW"
    const val FEED_TYPE_STATIC_IMAGE = "STATICIMAGE"
    const val FEED_TYPE_BLOG = "BLOG"
    const val FEED_TYPE_DEEPLINK = "DEEPLINK"
    const val FEED_TYPE_INAPPWEB = "INAPPWEB"
    const val FEED_TYPE_EXTWEBVIEW = "EXTWEBVIEW"
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
    const val SPLASH_TIME = 4000L
    const val FROM_WHICH_SCREEN = "from_which_screen"
    const val AMOUNT = "Amount"
    const val REMARKS = "Remarks"
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
    const val ACTIVATE_CARD = "activate_card"
    const val ORDER_CARD_PHYSICAL_CARD_CODE = "PhysicalCard"
    const val KIT_FOUR_DIGIT = "kit_four_digit"
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
    const val REQUEST = "request"
    const val PAY_USING_QR = "send_money_using QR code"
    const val WHICH_ACTION = "which_action"
    const val FUND_TRANSFER_TRANSACTION_TYPE = "FUND_TRANSAFER"
    const val FUND_TRANSFER_QR_CODE = "SPEND_BQR"
    const val RESPONSE = "response"
    const val SERVER_DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
    const val SERVER_DATE_TIME_FORMAT1 = "yyyy-MM-dd'T'HH:mm:ss'Z'"
    const val SERVER_DATE_TIME_FORMAT2 = "yyyy-MM-dd'T'HH:mm:ss.SSS"
    const val CHANGED_DATE_TIME_FORMAT = "MMM dd,yyyy-HH:mmaa"
    const val CHANGED_DATE_TIME_FORMAT1 = "MMM dd"
    const val CHANGED_DATE_TIME_FORMAT2 = "MMM dd HH:mmaa"
    const val CHANGED_DATE_TIME_FORMAT3 = "MMM dd, HH:mmaa"
    const val CHANGED_DATE_TIME_FORMAT4 = "MMM dd, yyyy"
    const val PAYU_SERVER = "pay u server"

    const val TYPE_UPI = "upi"
    const val TYPE_DC = "debit card"
    const val TYPE_GOOGLE_PAY = "gpay"
    const val TYPE_PHONEPE = "phonepe"
    const val TYPE_GENERIC = "generic"
    const val CREDITED = "Credited"
    const val DEBITED = "Debited"
    const val BANK_TRANSACTION = "bank_transaction"
    const val TRANSACTION = "transaction"
    const val ADD_MONEY = "add_money"

    const val BLOCK_CARD_ACTION = "BLOCKCARD"
    const val UNBLOCK_CARD_ACTION = "UNBLOCKCARD"
    const val MOD_ONLINE_LIMIT = "MODONLINELIMIT"
    const val CARD_TYPE_PHYSICAL_CARD = 1
    const val CARD_TYPE_VIRTUAL_CARD = 0
    const val CARD_TYPE_VIRTUAL = "VIRTUAL"
    const val CARD_TYPE_PHYSICAL = "PHYSICAL"
    const val OFF = 1
    const val ON = 0
    const val ENABLE_CHANNEL = "ENABLE_CHANNEL"
    const val Channel_ECOM = "ECOM"
    const val Channel_ATM = "ATM"
    const val Channel_POS = "POS"
    const val GPAY_PACKAGE_NAME = "com.google.android.apps.nbu.paisa.user"
    const val UPI_APPS_FETCH = "upi://pay"
    const val REQUEST_IMAGE = 200
    const val CARD_PRICE = "200"
    const val ORDER_CARD_PAYMENT_MODE = "WALLET"
    const val ENABLE= "ENABLED"


    const val ORDER_STATUS_ORDERED = "ORDER_PLACED"
    const val ORDER_STATUS_IN_PROGRESS = "IN_PROGRESS"
    const val ORDER_STATUS_SHIPPED = "SHIPPED"
    const val ORDER_STATUS_OUT_FOR_DELIVERY = "OUT_FOR_DELIVERY"
    const val ORDER_STATUS_DELIVERED = "DELIVERED"
    const val ORDER_STATUS_SEND_TO_VENDOR = "SEND_TO_VENDOR"
    const val GET_PRODUCT_RESPONSE = "get_product_response"
    const val ORDER_STATUS = "order_status"
    const val SET_PIN_URL = "set_pin_url"
    const val QR_FORMAT_NAME = "QR_CODE"


    const val CLIENT_TYPE = "Client_Type"
    const val CLIENT_TYPE_VALUE = "ANDROID"
    const val NOT_ALLOWED_MSG = "Invalid Version. Please uninstall your current app and install it from Play Store again."

}