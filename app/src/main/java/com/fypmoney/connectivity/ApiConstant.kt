package com.fypmoney.connectivity

object ApiConstant {
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
    const val API_USER_TIMELINE =
        "/services/PockketService/api/user-timeline?sort=createdDate,desc"
    const val API_GET_TASKMASTER = "/services/loyaltyservice/api/taskMaster"
    const val API_ASSIGN_TASK =
        "/services/loyaltyservice/api/task/assign/search/V1?sort=createdDate,desc"
    const val API_YOUR_TASK =
        "/services/loyaltyservice/api/task/assign/search/V1"


    const val API_UPDATE_FAMILY_NAME = "/services/authservice/api/family-detail?name="
    const val API_KYC_ACTIVATE_ACCOUNT = "/services/PockketService/api/activate/account/init/v1"
    const val API_UPGRADE_KYC_ACCOUNT = "services/PockketService/api/kyc/init/v2"
    const val API_KYC_MOBILE_VERIFICATION = "/services/PockketService/api/mobile/verification/v1"

    const val API_KYC_INIT = "/services/PockketService/api/kyc/init"
    const val API_KYC_VERIFICATION = "/services/PockketService/api/kyc/verification"
    const val API_KYC_UPGARDE_VERIFICATION = "services/PockketService/api/kyc/verification/v2"
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
    const val API_UPLOAD_SHOP_PIC = "/services/loyaltyservice/api/upload/shop-pic"
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
    const val API_TASK_DETAIL = "/services/PockketService/internal/approval-request/V1/"
    const val API_TASK_UPDATE = "/services/loyaltyservice/api/task"
    const val API_HISTORY_TASK =
        "/services/loyaltyservice/api/task/history"

    const val TOP_TEN_USER_API = "/services/PockketService/api/recent/wallet/transaction"
    const val TOTAL_REFERRAL_CASHBACK_API = "/services/PockketService/api/total/referral/points"

    const val REFERRAL_SCREEN_MESSAGES_API = "/services/authservice/api/user-additional-data"


    const val API_STORY = "/services/loyaltyservice/api/story/split"
    const val API_GET_REWARDS_API = "/services/authservice/api/rewards/v2"
    const val API_SPIN_WHEEL = "/services/authservice/api/spin-wheel/v2/1"
    const val API_GET_REWARDS_HISTORY = "/services/authservice/api/all/rewards"
    const val API_GET_REDEEM_DETAILS_API = "/services/authservice/api/rewards/details"
    const val API_REDEEM_COINS_API = "/services/authservice/api/redeem/rewards"

    const val API_USER_DEVICE_INFO = "/services/authservice/api/users/device-info"
    const val API_SETTINGS = "/services/PockketService/api/get/system-properties/by-keys"
    const val API_USER_OFFER_CARD = "/services/loyaltyservice/api/user-offer/card"
    const val API_PIN_CODE = "/services/loyaltyservice/api/service-available/check/"

    const val RewardsHistory =
        "services/loyaltyservice/api/rewardHistory"
    const val CashbackHistory =
        "services/PockketService/api/cashbackHistory"

    const val API_REWARD_SUMMARY =
        "services/loyaltyservice/api/loyalty/points/summary"
    const val API_GET_REWARD_PRODUCTS =
        "services/PockketService/api/list/reward-product"
    //TODO need to change APIData parsing logic
    const val API_GET_REWARD_PRODUCTS_PURPOSE =
        "reward-product_purpose"
    const val API_GET_REWARD_EARNINGS =
        "services/PockketService/api/total-cashback-earned"
    const val API_POST_CHECK_OFFLINE_CARD =
        "/services/PockketService/api/order-card/offline"
    const val REWARD_PRODUCT_DETAILS =
        "/services/PockketService/api/reward-product/by/orderId/"
    const val API_REDEEM_REWARD =
        "/services/PockketService/api/purchase/reward-product/"
    const val API_PLAY_SPIN =
        "services/PockketService/api/total-cashback-earned"
    const val PLAY_ORDER_API =
        "/services/PockketService/api/play/order/"
    const val API_GET_CASHBACK_EARNED =
        "/services/PockketService/api/cashback-earned/bymrn/"
    const val API_GET_REWARDS_EARNED =
        "services/loyaltyservice/api/loyalty/transaction/bymrn/"
    const val GET_BRAND_GIFT_CARD =
        "services/PockketService/api/product/"
    const val PURCHASE_GIFT_CARD = "services/PockketService/api/purchase-voucher"
    const val API_GET_JACKPOT_CARDS =
        "services/PockketService/api/jackpot/tickets"

    const val Api_OfferList = "services/PockketService/api/fyp-offer"
    const val Api_LIGHTENING_DEALS = "services/PockketService/api/lightening-deals"

    const val API_CALLTO_ACTION = "/services/PockketService/api/actions/screen-sections/"

    const val API_Explore = "services/PockketService/api/screen-sections/"

    const val API_FETCH_FEED_DETAILS = "/services/loyaltyservice/api/feed/"

    const val API_FETCH_OFFER_DETAILS = "/services/PockketService/api/fyp-offer/"
    const val Api_Request_Siblin_parent = "services/authservice/api/family-member/userKycCode/"
    const val Api_Your_Gifts = "services/loyaltyservice/api/onboarding-gifts"
    const val API_CHECK_PROMO_CODE = "/services/loyaltyservice/api/promocode"
    const val API_RECHARGE_PLANS = "services/loyaltyservice/api/mobile-recharge-plans"
    const val API_GET_OPERATOR_LIST_MOBILE =
        "services/loyaltyservice/api/operator-list/MOBILE"
    const val API_GET_DTH_OPERATORS =
        "services/loyaltyservice/api/operator-list/DTH"

    const val API_GET_CIRCLE_LIST = "services/loyaltyservice/api/circle-list/"
    const val API_GET_HLR_CHECK = "services/loyaltyservice/api/hlrcheck"
    const val API_FETCH_BILL = "services/loyaltyservice/api/fetch-bill"
    const val API_MOBILE_RECHARGE = "services/PockketService/api/mobileRecharge"

    const val API_PAY_BILL = "services/PockketService/api/billPayment"
    const val API_RECENT_RECHARGE = "services/PockketService/api/recharge-orders"
    const val Api_GET_DISCORD_PROFILE = "/services/loyaltyservice/api/fetch-discord-data"
    const val API_30_DAYS_TRANSACTION = "/services/PockketService/api/fetch/data-for/30Days_transaction"

    const val GET_GIFTS_LIST = "services/PockketService/api/search-voucher"

    const val GET_HISTORY_LIST =
        "services/PockketService/api/purchased-voucher?size=10&sort=id,desc&gifted=NO&page="
    const val GET_HISTORY_UNUSED_LIST =
        "services/PockketService/api/unused-voucher?page=0&size=10&sort=id,desc"
    const val GET_GIFT_VOUCHER_STATUS =
        "/services/PockketService/api/voucher-status/"

    const val GET_GIFT_VOUCHER_DETAILS =
        "services/PockketService/api/voucher-order-detail/"

    const val API_BRAND_DETAILS = "services/PockketService/api/product/"
    const val API_GIFT_CARD_DETAILS = "/services/PockketService/api/voucher-order-detail/"
    const val API_BANK_DETAILS = "/services/PockketService/api/bank-details"
    const val API_GET_REWARD_SINGLE_PRODUCTS =
        "services/PockketService/api/list/reward-product/"

    const val API_GET_REWARD_SINGLE_PRODUCTS_PURPOSE =
        "reward-product"

    const val API_GET_REWARD_SLOT_MACHINE_PURPOSE = "reward-product/slot"

    const val API_GET_BRANDED_COUPONS_PURPOSE = "reward-product/branded"

    const val API_GET_BRANDED_COUPONS = "services/PockketService/api/list/reward-product"

    const val API_GET_TREASURE_DATA =
        "services/PockketService/api/list/reward-product"

    const val API_GET_SLOT_MACHINE_DATA =
        "services/PockketService/api/list/reward-product"

    const val API_GET_ALL_JACKPOTS_PRODUCTWISE =
        "services/PockketService/api/jackpot/tickets/productwise/"

    const val API_GET_LEADERBOARD_DATA = "services/PockketService/api/reward/leaderboard/"
    const val API_GET_MERCHANT_CATEGORY = "/services/PockketService/api/fetch/data-for/ALL_MCC_CATEGORY"
    const val API_CHANGE_TXN_CATEGORY = "/services/PockketService/api/account-transaction/icon"

    const val API_GET_COUPON_REWARD_DATA = "services/PockketService/api/coupon/details/"

    const val API_GET_ACTIVE_COUPON_DATA = "services/PockketService/api/list/active-coupons/"

    const val API_GET_ACTIVE_COUPON_COUNT_DATA = "services/PockketService/api/active-coupon-count/"

    const val API_POST_OPT_STATUS_DATA = "services/NotificationService/services/NotificationService/api/whatsapp/opt/"

    const val API_GET_POCKET_MONEY_REMINDER_DATA = "services/PockketService/api/pocketmoneyreminder/list/"

    const val API_DELETE_POCKET_MONEY_REMINDER = "services/PockketService/api/pocketmoneyreminder/disable/"

    const val API_VERIFY_OTP_POCKET_MONEY_REMINDER = "services/PockketService/api/pocketmoneyreminder/verify/"

    const val API_ADD_POCKET_MONEY_REMINDER = "services/PockketService/api/pocketmoneyreminder/"

    const val API_SAVE_SHOP_DETAILS = "/services/loyaltyservice/api/save-shop-details/"

    const val API_FETCH_SHOP_DETAILS = "/services/loyaltyservice/api/fetch/shop-details/"

    const val API_SEND_OTP_KYC = "/services/PockketService/api/full-kyc/send-otp/"

    const val API_VERIFY_OTP_KYC = "/services/PockketService/api/agent-fullkyc/verify-otp/"

    const val API_AGENTS_EARNINGS_LIST = "/services/PockketService/api/list/agent/earnings/"

    const val API_FULL_KYC = "/services/PockketService/api/agent-fullkyc"
}




