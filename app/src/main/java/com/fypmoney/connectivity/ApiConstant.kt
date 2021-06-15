package com.fypmoney.connectivity

object ApiConstant {
   /* const val BASE_URL = "https://stage.services.dreamfolkstech.in"
    const val CLIENT_ID = "wTYi02TtRcRt53Dd"
    const val CLIENT_SECRET = "kba3BXrUPPwfcO4TFzGttoopWVyYduLV"
    const val APP_ID = "WEBAPP"
    const val GRANT_TYPE = "client_credentials"*/
    const val BASE_URL = "http://54.84.197.170:9300"
    const val PAYU_BASE_URL = "https://test.payu.in"
    const val CLIENT_ID = "web_app"
    const val CLIENT_SECRET = "user"
    const val APP_ID = "FYPMONEY"
    const val GRANT_TYPE = "client_credentials"
    const val API_SUCCESS = "Success"
    const val API_AUTH_LOGIN = "/auth/login"
    const val API_LOGIN_INIT = "/services/authservice/api/customer/login/init"
    const val API_FETCH_ALL_FEEDS = "/services/loyaltyservice/api/getDetails"
    const val API_ADD_FAMILY_MEMBER = "/services/authservice/api/family-member"
    const val API_LEAVE_FAMILY_MEMBER = "/services/authservice/api/leave-family"
    const val API_REMOVE_FAMILY_MEMBER = "/services/authservice/api/family-member/"
    const val API_CHECK_IS_APP_USER = "/services/loyaltyservice/api/phoneBook/checkUser/"
    const val API_SNC_CONTACTS = "/services/loyaltyservice/api/phoneBook"
    const val API_UPDATE_PROFILE = "/services/authservice/api/customer/update"
    const val API_GET_INTEREST = "/services/authservice/api/customer/interestMaster"
    const val API_GET_CUSTOMER_INFO = "/services/authservice/api/customer"
    const val API_GET_NOTIFICATION_LIST = "/services/PockketService/internal/get/approval-request"
    const val API_UPDATE_APPROVAL_REQUEST = "/services/PockketService/internal/approval-request"
    const val API_VERIFY_REFERRAL_CODE = "/services/authservice/api/customer/refer/"
    const val API_USER_TIMELINE = "/services/PockketService/api/user-timeline"
    const val API_UPDATE_FAMILY_NAME = "/services/authservice/api/family-detail?name="
    const val API_KYC_ACTIVATE_ACCOUNT = "/services/PockketService/api/activate/account/init"
    const val API_KYC_MOBILE_VERIFICATION = "/services/PockketService/api/mobile/verification"
    const val API_KYC_INIT = "/services/PockketService/api/kyc/init"
    const val API_KYC_VERIFICATION = "/services/PockketService/api/kyc/verification"
    const val API_GET_WALLET_BALANCE = "/services/PockketService/api/get/walletBalance"
    const val API_GET_VIRTUAL_CARD_REQUEST = "/services/PockketService/api/get/virtualCardRequest"
    const val API_FETCH_VIRTUAL_CARD_DETAILS = "/services/PockketService/api/fetch/virtualCard"
    const val API_FUND_TRANSFER = "/services/PockketService/api/fund/transfer"
    const val API_LOGIN = "/auth/app/login"
    const val API_REQUEST_MONEY = "/services/PockketService/api/request/money"
    const val API_ADD_CARD = "/services/PockketService/api/card-infos"
    const val API_ADD_MONEY_STEP1 = "/services/PockketService/api/add/money/wallet/step1/get/pgRequestData"
    const val API_ADD_MONEY_STEP2 = "/services/PockketService/api/add/money/wallet/step2/load-money"
    const val API_PAY_MONEY = "/services/PockketService/api/pay/money"
    const val API_CHECK_USER_ERROR_CODE = "LOY_5035"

 // pay u test url

 const val PAYU_TEST_URL="/merchant/postservice.php?form=2"
 const val PAYU_PRODUCTION_URL="https://info.payu.in/merchant/postservice.php?form=2"

 const val GET_USER_CARDS="get_user_cards"


}




