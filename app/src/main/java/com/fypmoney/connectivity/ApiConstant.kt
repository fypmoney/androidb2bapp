package com.fypmoney.connectivity

object ApiConstant {
    /* const val BASE_URL = "https://stage.services.dreamfolkstech.in"
     const val CLIENT_ID = "wTYi02TtRcRt53Dd"
     const val CLIENT_SECRET = "kba3BXrUPPwfcO4TFzGttoopWVyYduLV"
     const val APP_ID = "WEBAPP"
     const val GRANT_TYPE = "client_credentials"*/
    const val BASE_URL = "http://54.84.197.170:9300"

  //  const val BASE_URL = "https://techservices.fyp-e.in"
    const val PAYU_BASE_URL = "https://info.payu.in"
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
    const val API_GET_TASKMASTER = "/services/loyaltyservice/api/taskMaster"
    const val API_ASSIGN_TASK = "/services/loyaltyservice/api/task/assign/search"
  const val API_YOUR_TASK =
    "/services/loyaltyservice/api/task/assign/search?page=0&size=20&sort=createdDate,desc"



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
    const val API_ADD_MONEY_STEP1 =
        "/services/PockketService/api/add/money/wallet/step1/get/pgRequestData"
    const val API_ADD_MONEY_STEP2 = "/services/PockketService/api/add/money/wallet/step2/load-money"
    const val API_PAY_MONEY = "/services/PockketService/api/pay/money"
    const val API_BANK_TRANSACTION_HISTORY = "/services/PockketService/api/bank/transaction/history"
    const val API_TRANSACTION_HISTORY = "/services/PockketService/api/transaction/history"
    const val API_GET_HASH = "/services/PockketService/api/get/hash"
    const val API_UPDATE_CARD_SETTINGS = "/services/PockketService/api/card-infos"
    const val API_ACTIVATE_CARD_INIT = "/services/PockketService/api/activate/physical-card/init"
    const val API_ACTIVATE_CARD = "/services/PockketService/api/activate/physical-card"
    const val API_LOGOUT = "/auth/logout"
    const val API_UPDATE_CARD_LIMIT = "/services/PockketService/api/wallet-balances"
    const val API_GET_BANK_PROFILE = "/services/PockketService/api/getBankProfile"
    const val API_QR_CODE_SCANNER = "/services/PockketService/api/spend/bharat-qr"
    const val API_UPLOAD_PROFILE_PIC = "/services/authservice/api/upload/profile-pic"
    const val API_ORDER_CARD = "/services/PockketService/api/order-card"
    const val API_CREATE_TASK = "/services/loyaltyservice/api/task"
    const val API_GET_ORDER_CARD_STATUS = "/services/PockketService/api/order-card/"
    const val API_GET_ALL_PRODUCTS = "/services/loyaltyservice/api/product-details/product-type/"
    const val API_GET_ALL_PRODUCTS_BY_CODE = "/services/loyaltyservice/api/product-details/"
    const val API_SET_CHANGE_PIN = "/services/PockketService/api/set/pin/"
    const val API_PHYSICAL_CARD_INIT = "/services/PockketService/api/activate/physical-card/init"
    const val API_GET_STATE = "/services/loyaltyservice/api/states"
    const val API_GET_CITY = "/services/loyaltyservice/api/cities/"

  const val API_CHECK_USER_ERROR_CODE = "LOY_5035"

  // pay u test url

  const val PAYU_PRODUCTION_URL = "/merchant/postservice.php?form=2"

  const val GET_USER_CARDS = "get_user_cards"

  const val CHECK_APP_UPDATE = "/app/version"
  const val API_TASK_DETAIL = "/services/PockketService/internal/approval-request/"
  const val API_TASK_UPDATE = "/services/loyaltyservice/api/task"
  const val API_HISTORY_TASK =
    "/services/loyaltyservice/api/task/history?page=0&size=10&sort=createdDate,desc"

    const val TOP_TEN_USER_API = "/services/PockketService/api/recent/wallet/transaction"
    const val TOTAL_REFERRAL_CASHBACK_API = "/services/PockketService/api/total/referral/points"

}




