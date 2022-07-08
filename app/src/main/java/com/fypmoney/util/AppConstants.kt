package com.fypmoney.util

import com.fypmoney.model.CountryCode
import com.fypmoney.model.countryCode
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


/*
* This class is used to store the Constants of the application
* */
object AppConstants {
    val INTEREST_TYPE: String="InterestType"
    const val FRESH_CHAT_APP_KEY="8dbdc877-4045-4b7c-8a69-1b176c41e71c"
    const val FRESH_CHAT_APP_ID="03bfae8e-f0f7-48a9-948a-b737edd1a38b"
    const val FRESH_CHAT_DOMAIN="msdk.in.freshchat.com"
    const val API_TIMEOUT_SECONDS: Long = 30
    const val DATE_FORMAT_CHANGED = "dd/MM/yyyy"
    const val DATE_TIME_FORMAT_SERVER = "yyyy-MM-dd'T'HH:mm:ss'Z'"
    const val APP_SETTINGS_PACKAGE_TEXT = "package:"
    const val EMAIL_TYPE = "EMAIL"
    const val ERROR_TYPE_SPIN_ALLOWED = "spinAllowed"
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
    const val FEED_TYPE_STATIC_IMAGE1x1 = "STATICIMAGE1X1"
    const val FEED_TYPE_STATIC_IMAGE1x1_5 = "STATICIMAGE1X1.5"
    const val IN_APP_WEBVIEW1x1 = "INAPPWEB1X1"

    const val FEED_TYPE_BLOG = "BLOG"
    const val FEED_TYPE_DEEPLINK = "DEEPLINK"
    const val FEED_TYPE_INAPPWEB = "INAPPWEB"
    const val FEED_TYPE_INAPPWEB2 = "INAPPWEB1X1"

    const val FEED_TYPE_EXTWEBVIEW = "EXTWEBVIEW"
    const val FEED_TYPE_EXTWEBVIEW2 = "EXTWEBVIEW1X1"
    const val FEED_TYPE_STORIES = "STORIES"
    const val EXPLORE_TYPE_STORIES = "STORY"
    const val EXPLORE_IN_APP_WEBVIEW = "IN_APP_WEB_VIEW"
    const val EXPLORE_IN_APP = "IN_APP"
    const val EXPLORE_SECTION_EXPLORE = "EXPLORE"

    const val TYPE_VIDEO = "VIDEO"
    const val TYPE_VIDEO_EXPLORE = "VIDEO_EXPLORE"
    const val FEED_RESPONSE = "feed_response"
    const val EXPLORE_RESPONSE = "explore_response"
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
    const val ADD_MEMBER_STATUS_DECLINED = "DECLINED"
    const val ACTIONFLAG = "ACTIONFLAG"

    const val YES = "YES"
    const val NO = "NO"
    const val PRODUCT_SPIN = "SPIN_WHEEL"
    const val PRODUCT_SCRATCH = "SCRATCH_CARD"
    const val CUSTOMER_INFO_RESPONSE = "customer_info_response"
    const val SPLASH_TIME = 4000L
    const val FROM_WHICH_SCREEN = "from_which_screen"
    const val EXPLORE_ITEM_ID = "explore_item_id"
    const val AMOUNT = "Amount"
    const val REMARKS = "Remarks"
    const val LOGOUT = "logout"
    const val API_SUCCESS = "Success"
    const val API_FAIL = "Fail"
    const val FEED_SCREEN_NAME = "FEED"
    const val REWARD_SCREEN_NAME = "REWARD"
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
    const val CHANGED_DATE_TIME_FORMAT9 = "dd MMM,yyyy-HH:mmaa"
    const val CHANGED_DATE_TIME_FORMAT1 = "MMM dd"
    const val CHANEGEDATE_DATE_MONTH = "dd MMM"
    const val CHANGED_DATE_TIME_FORMAT2 = "MMM dd HH:mmaa"
    const val CHANGED_DATE_TIME_FORMAT3 = "MMM dd, HH:mmaa"
    const val CHANGED_DATE_TIME_FORMAT4 = "MMM dd, yyyy"
    const val CHANGED_DATE_TIME_FORMAT6 = "MMM dd, yy"
    const val CHANGED_DATE_TIME_FORMAT8 = "dd MMM, yyyy"
    const val CHANGED_DATE_TIME_FORMAT7 = "HH:mm,  MMM dd, yy"
    const val CHANGED_DATE_TIME_FORMAT5 = " dd/MM/yyyy"
    const val REWARD_HISTORY_OUTPUT = "MMM dd' ,'hh:mm a"
    const val REWARD_HISTORY_INPUT = "MMM dd' ,'hh:mm a"

    const val PAYU_SERVER = "pay u server"

    const val TYPE_UPI = "upi"
    const val TYPE_DC = "debit card"
    const val TYPE_GOOGLE_PAY = "gpay"
    const val TYPE_PHONEPE = "phonepe"
    const val TYPE_GENERIC = "generic"
    const val CREDITED = "Credited"
    const val LOAD = "LOAD"
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
    const val FAMPAY_PACKAGE_NAME = "com.fampay.in"
    const val NUMBER_SELECTED = "number_selected"
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
    const val APP_VERSION = "app_version"
    const val CLIENT_TYPE_VALUE = "ANDROID"
    const val NOT_ALLOWED_MSG = "Invalid Version. Please uninstall your current app and install it from Play Store again."
    const val PLAY_STORE_URL = "http://onelink.to/fypmoney"
    const val CASHBACK_AMOUNT = "25"
    const val INSTAGRAM_PAGE = "https://www.instagram.com/fypmoney.in/?hl=en"
    const val FACEBOOK_PAGE = "https://www.facebook.com/fypmoney.in/"
    const val YOUTUBE_PAGE = "https://www.youtube.com/channel/UCDoPH4GT9fP8Sp2bgr_95LA"

    //DeepLinks constants
    const val HOMEVIEW = "HomeView"
    const val ReferralScreen = "ReferralScrren"
    const val GiftScreen = "GiftCardScreen"
    const val CardScreen = "CardScreen"
    const val OfferScreen = "offerscreen"
    const val StoreScreen = "StoreScreen"
    const val FyperScreen = "FyperScreen"
    const val FEEDSCREEN = "FeedScreen"
    const val TRACKORDER = "TrackOrderScreen"
    const val CHORES = "ChoresScreen"
    const val OrderCard = "OrderCard"
    const val JACKPOTTAB = "Jackpot"
    const val ARCADE = "Arcade"

    const val RechargeHomeScreen = "RechargeHomeScreen"
    const val RewardHistory = "RewardHistory"
    const val StoreofferScreen = "Storeofferscreen"
    const val StoreshopsScreen = "Storeshopsscreen"
    const val GIFT_VOUCHER = "GIFT_VOUCHER"

    /* Home : "HomeView, 0"

     Refereal Screen : "ReferralScrren, 1"

     Card Screen : "CardScreen, 2"

     Stores : "StoreScreen, 3"

     Fyper : "FyperScreen, 4"

     Feed : "FeedScreen, 5"

     Track Order : "TrackOrderScreen, 6"

     Chores :" ChoresScreen, 7"*/
    const val ERROR_TYPE_AFTER_SPIN = "AfterSpin"
    const val INSUFFICIENT_ERROR_CODE = "PKT_2037"
    const val AADHAR_VERIFICATION_ERROR_CODE = "PKT_2006"
    const val NEW = "Received"
    const val Accepted = "Accepted"
    const val Completed = "Complete"
    const val Pay = "Pay"
    const val REFER_MSG_SHARED_1 = "REFERAL_PKYC0"
    const val REFER_MSG_SHARED_2 = "REFERAL_PKYC1"

    const val ONBOARD_SHARE_90 = "ONBOARD_SHARE_90"
    const val ONBOARD_SHARE_1 = "ONBOARD_SHARE_1"
    const val CARD_ORDER_FLAG = "CARD_ORDER_FLAG"
    const val REFER_LINE1 = "REFER_LINE1"
    const val REFER_LINE2 = "REFER_LINE2"
    const val REFEREE_CASHBACK = "REFEREE_CASHBACK"
    const val ERROR_MESSAGE_HOME = "ERROR_MESSAGE_HOME"
    const val IS_NEW_FEED_AVAILABLE = "IS_NEW_FEED_AVAILABLE"
    const val PHYSICAL_CARD_CODE = "PHYCRD"
    const val ORDER_CARD_INFO = "order_card_info"
    const val REFER_CODE_CHECKING_VARIABLE = "$@%$"
    const val ADD_MONEY_VIDEO = "ADD_MONEY_VIDEO"
    const val ADD_MONEY_VIDEO_NEW = "ADD_MONEY_VIDEO_NEW"

    const val IN_APP_URL = "IN_APP_URL"
    const val ORDER_NUM = "ORDERID"
    const val PRODUCT_HIDE_IMAGE = "HIDE_IMAGE"
    const val SECTION_ID = "SECTIONID"
    const val NO_GOLDED_CARD = "GOLDEDCARD"
    const val PRODUCT_CODE = "PRODUCT_CODE"
    const val TRANS_TYPE_EARN = "EARN"
    const val JACKPOT_SCREEN_NAME = "JACKPOT"
    const val EXT_WEBVIEW = "EXT_REDIRECT"
    const val SHOW_MORE = "SHOWMORE"
    const val IN_APP_WITH_CARD = "IN_APP_VIEW_WITH_CARD_OPTION"
    const val OFFER_REDIRECTION = "OFFER_REDIRECTION"

    const val USER_TYPE_NEW = "newUser"
    const val USER_TYPE = "UserType"
    const val KYC_type = "kyctype"
    const val POSTKYCKEY = "postkyc"

    const val GIFT_BRAND_SELECTED = "gift_brand_selected"
    const val GIFT_HISTORY_SELECTED = "gift_history_selected"
    const val GIFT_ID = "gift_id"
    const val SEMI = "SEMI"
    const val MINIMUM = "MINIMUM"
    const val POSTPAID = "POSTPAID"
    const val PREPAID = "PREPAID"
    const val DTH = "DTH"
    const val KYC_UPGRADE_FROM_WHICH_SCREEN = "kyc_upgrade_from_which_screen"
    const val BROADBAND_RECHARGE_URL = "https://www.amazon.in/hfc/bill/landline"
    const val SHOW_RECHARGE_SCREEN = "SHOW_RECHARGE_SCREEN"
    const val IS_GIFT_CARD_IS_AVAILABLE = "IS_GIFT_CARD_AVAILABLE"
    const val SERVER_IS_UNDER_MAINTENANCE = "SERVER_IS_UNDER_MAINTENANCE"
    const val SERVER_MAINTENANCE_DESCRIPTION = "SERVER_MAINTENANCE_DESCRIPTION"
    const val OFFER_REDIRECTION_EXTERNAL_WEB_VIEW = "EXTERNAL_WEB_VIEW"
    const val OFFER_REDIRECTION_EXTERNAL_WEB_VIEW_SUB = "EXTERNAL_WEB_VIEW_SUB"
    const val OFFER_REDIRECTION_IN_APP_VIEW_WITH_CARD_OPTION_SUBs =
        "IN_APP_VIEW_WITH_CARD_OPTION_SUBs"
    const val OFFER_REDIRECTION_IN_APP_VIEW_WITH_CARD_OPTIONS = "IN_APP_VIEW_WITH_CARD_OPTIONS"

    //const val DISCORD_URL = "https://discord.com/oauth2/authorize?response_type=code&client_id=945553921005457468&scope=connections%20email%20gdm.join%20guilds%20guilds.join%20guilds.members.read%20identify%20messages.read&state={authtoken}&prompt=consent"
    const val DISCORD_URL = "https://discord.com/oauth2/authorize?response_type=code&client_id=945553921005457468&scope=connections%20email%20gdm.join%20guilds%20guilds.join%20guilds.members.read%20identify&state={authtoken}&prompt=consent"

}