package com.fypmoney.connectivity

import com.fypmoney.connectivity.ApiConstant.API_AUTH_LOGIN
import com.fypmoney.connectivity.ApiConstant.API_CHECK_IS_APP_USER
import com.fypmoney.connectivity.ApiConstant.API_GET_CUSTOMER_INFO
import com.fypmoney.connectivity.ApiConstant.API_GET_INTEREST
import com.fypmoney.connectivity.ApiConstant.API_GET_NOTIFICATION_LIST
import com.fypmoney.connectivity.ApiConstant.API_LEAVE_FAMILY_MEMBER
import com.fypmoney.connectivity.ApiConstant.API_LOGIN
import com.fypmoney.connectivity.ApiConstant.API_LOGIN_INIT
import com.fypmoney.connectivity.ApiConstant.API_SNC_CONTACTS
import com.fypmoney.connectivity.ApiConstant.API_UPDATE_PROFILE
import com.fypmoney.connectivity.network.NetworkUtil
import com.fypmoney.connectivity.retrofit.ApiRequest
import com.fypmoney.model.*
import com.fypmoney.view.addmoney.model.BankProfileDetailsNetworkResponse
import com.fypmoney.view.arcadegames.brandedcoupons.model.BrandedCouponDetailsResponse
import com.fypmoney.view.arcadegames.brandedcoupons.model.BrandedCouponResponse
import com.fypmoney.view.arcadegames.brandedcoupons.model.BrandedActiveCouponResponse
import com.fypmoney.view.arcadegames.brandedcoupons.model.BrandedCouponCountResponse
import com.fypmoney.view.arcadegames.model.*
import com.fypmoney.view.giftcard.model.*
import com.fypmoney.view.home.main.home.model.networkmodel.CallToActionNetworkResponse
import com.fypmoney.view.kycagent.model.*
import com.fypmoney.view.ordercard.model.UserOfferCardResponse
import com.fypmoney.view.pocketmoneysettings.model.PocketMoneyOtpVerifyResponse
import com.fypmoney.view.pocketmoneysettings.model.PocketMoneyReminderListResponse
import com.fypmoney.view.pocketmoneysettings.model.PocketMoneyReminderResponse
import com.fypmoney.view.recharge.model.RecentRechargesResponse
import com.fypmoney.view.whatsappnoti.model.WhatsAppNotificationResponse
import com.google.gson.Gson
import com.payu.india.Payu.PayuConstants
import okhttp3.ResponseBody


class ApiDataParsing {
    companion object {
        private var mApiDataParsing: ApiDataParsing? = null

        /**
         * @description Method to get single instance of WebApiCaller class.
         */
        @Synchronized
        fun getInstance(): ApiDataParsing {
            if (mApiDataParsing == null) {
                mApiDataParsing = ApiDataParsing()
            }
            return mApiDataParsing as ApiDataParsing
        }
    }

    /**
     * @param request ApiRequest;
     * @param body    ResponseBody;
     * @description Method is used to parse the data.
     */
    fun parseData(request: ApiRequest, body: ResponseBody, command: String? = null): Any? {
        return try {
            val response = body.string()
            when (request.purpose) {
                API_AUTH_LOGIN -> {
                    getObject(response, AuthLoginResponse::class.java)
                }
                API_LOGIN_INIT -> {
                    getObject(response, LoginInitResponse::class.java)
                }
                API_LOGIN -> {
                    getObject(response, LoginResponse::class.java)
                }
//                API_FETCH_ALL_FEEDS -> {
//                    getObject(response, FeedResponseModel::class.java)
//                }
                ApiConstant.API_ADD_FAMILY_MEMBER -> {
                    when (request.request_type) {
                        ApiUrl.GET -> {
                            getObject(response, GetMemberResponse::class.java)
                        }
                        else -> {
                            getObject(response, AddFamilyMemberResponse::class.java)
                        }
                    }

                }
                API_CHECK_IS_APP_USER -> {
                    getObject(response, IsAppUserResponse::class.java)
                }
                API_SNC_CONTACTS -> {
                    getObject(response, ContactResponse::class.java)
                }
                API_GET_CUSTOMER_INFO -> {
                    getObject(response, CustomerInfoResponse::class.java)
                }
                API_UPDATE_PROFILE -> {
                    getObject(response, CustomerInfoResponse::class.java)
                }

                API_GET_INTEREST -> {
                    getObject(response, InterestResponse::class.java)
                }
                API_LEAVE_FAMILY_MEMBER -> {
                    getObject(response, LeaveFamilyResponse::class.java)
                }
                ApiConstant.API_REMOVE_FAMILY_MEMBER -> {
                    getObject(response, RemoveFamilyResponse::class.java)
                }
                API_GET_NOTIFICATION_LIST -> {
                    getObject(response, NotificationModel.NotificationResponse::class.java)
                }
                ApiConstant.API_UPDATE_APPROVAL_REQUEST -> {
                    getObject(response, UpdateFamilyApprovalResponse::class.java)
                }
                ApiConstant.API_USER_TIMELINE -> {
                    getObject(response, NotificationModel.UserTimelineResponse::class.java)
                }
                ApiConstant.API_VERIFY_REFERRAL_CODE -> {
                    getObject(response, ReferralCodeResponse::class.java)
                }
                ApiConstant.API_UPDATE_FAMILY_NAME -> {
                    getObject(response, UpdateFamilyNameResponse::class.java)
                }

                ApiConstant.API_KYC_ACTIVATE_ACCOUNT -> {
                    getObject(response, KycActivateAccountResponse::class.java)
                }
                ApiConstant.API_KYC_INIT -> {
                    getObject(response, KycInitResponse::class.java)
                }
                ApiConstant.API_UPGRADE_KYC_ACCOUNT -> {
                    getObject(response, KycInitResponse::class.java)
                }
                ApiConstant.API_KYC_UPGARDE_VERIFICATION -> {
                    getObject(response, KycVerificationResponse::class.java)
                }
                ApiConstant.API_CHECK_PROMO_CODE -> {
                    getObject(response, UserOfferCardResponse::class.java)
                }
                ApiConstant.API_KYC_MOBILE_VERIFICATION -> {
                    getObject(response, KycMobileVerifyResponse::class.java)
                }
                ApiConstant.API_KYC_VERIFICATION -> {
                    getObject(response, KycVerificationResponse::class.java)
                }

                ApiConstant.API_GET_WALLET_BALANCE -> {
                    getObject(response, GetWalletBalanceResponse::class.java)
                }

                ApiConstant.API_GET_VIRTUAL_CARD_REQUEST -> {
                    getObject(response, VirtualCardRequestResponse::class.java)
                }

                ApiConstant.API_FETCH_VIRTUAL_CARD_DETAILS -> {
                    getObject(response, FetchVirtualCardResponse::class.java)
                }
                ApiConstant.API_FUND_TRANSFER -> {
                    getObject(response, SendMoneyResponse::class.java)
                }

                ApiConstant.API_REQUEST_MONEY -> {
                    getObject(response, RequestMoneyResponse::class.java)
                }
                ApiConstant.API_ADD_MONEY_STEP1 -> {
                    getObject(response, AddMoneyStep1Response::class.java)
                }
                ApiConstant.API_ADD_MONEY_STEP2 -> {
                    getObject(response, AddMoneyStep2Response::class.java)
                }
                ApiConstant.API_PAY_MONEY -> {
                    getObject(response, PayMoneyResponse::class.java)
                }
                ApiConstant.GET_USER_CARDS -> {
                    getObject(response, SavedCardResponseDetails::class.java)
                }
                ApiConstant.API_TRANSACTION_HISTORY -> {
                    getObject(response, TransactionHistoryResponse::class.java)
                }
                ApiConstant.API_BANK_TRANSACTION_HISTORY -> {
                    getObject(response, BankTransactionHistoryResponse::class.java)
                }
                ApiConstant.API_GET_HASH -> {
                    getObject(response, GetHashResponse::class.java)
                }
                ApiConstant.API_UPDATE_CARD_LIMIT -> {
                    getObject(response, UpdateCardLimitResponse::class.java)
                }
                ApiConstant.API_UPDATE_CARD_SETTINGS -> {
                    getObject(response, UpdateCardSettingsResponse::class.java)
                }
                ApiConstant.API_GET_BANK_PROFILE -> {
                    getObject(response, BankProfileResponse::class.java)
                }
                ApiConstant.API_LOGOUT -> {
                    getObject(response, LogOutResponse::class.java)
                }
                ApiConstant.API_QR_CODE_SCANNER -> {
                    getObject(response, QrCodeScannerResponse::class.java)
                }
                ApiConstant.API_UPLOAD_PROFILE_PIC -> {
                    getObject(response, ProfileImageUploadResponse::class.java)
                }
                ApiConstant.API_UPLOAD_SHOP_PIC -> {
                    getObject(response, FetchShopDetailsResponse::class.java)
                }
                ApiConstant.API_ORDER_CARD -> {
                    getObject(response, OrderCardResponse::class.java)
                }
                ApiConstant.API_GET_ORDER_CARD_STATUS -> {
                    getObject(response, GetOrderCardStatusResponse::class.java)
                }
                ApiConstant.API_ORDER_CARD -> {
                    getObject(response, OrderCardResponse::class.java)
                }
                ApiConstant.API_GET_ORDER_CARD_STATUS -> {
                    getObject(response, GetOrderCardStatusResponse::class.java)
                }
                ApiConstant.API_GET_ALL_PRODUCTS, ApiConstant.API_GET_ALL_PRODUCTS_BY_CODE -> {
                    getObject(response, GetAllProductsResponse::class.java)
                }

                ApiConstant.API_ACTIVATE_CARD -> {
                    getObject(response, ActivateCardResponse::class.java)
                }
                ApiConstant.API_PHYSICAL_CARD_INIT -> {
                    getObject(response, PhysicalCardInitResponse::class.java)
                }
                ApiConstant.API_GET_STATE -> {
                    getObject(response, GetStatesResponse::class.java)
                }
                ApiConstant.API_GET_CITY -> {
                    getObject(response, GetCityResponse::class.java)
                }
                ApiConstant.API_SET_CHANGE_PIN -> {
                    getObject(response, SetPinResponse::class.java)
                }
                ApiConstant.API_CALLTO_ACTION -> {
                    getObject(response, CallToActionNetworkResponse::class.java)
                }
                ApiConstant.API_RECENT_RECHARGE -> {
                    getObject(response, RecentRechargesResponse::class.java)
                }
                ApiConstant.API_BRAND_DETAILS -> {
                    getObject(response, CreateGiftCardBrandNetworkResponse::class.java)
                }
                ApiConstant.PURCHASE_GIFT_CARD -> {
                    getObject(response, PurchaseGiftCardNetworkResponse::class.java)
                }
                ApiConstant.API_GIFT_CARD_DETAILS -> {
                    getObject(response, GiftCardDetailsNetworkResponse::class.java)
                }
                ApiConstant.GET_HISTORY_LIST -> {
                    getObject(response, GiftCardHistoryListNetworkResponse::class.java)
                }
                ApiConstant.GET_GIFT_VOUCHER_STATUS -> {
                    getObject(response, GiftCardStatusNetworkResponse::class.java)
                }
                ApiConstant.API_BANK_DETAILS -> {
                    getObject(response, BankProfileDetailsNetworkResponse::class.java)
                }
                ApiConstant.API_GET_REWARD_SINGLE_PRODUCTS_PURPOSE -> {
                    getObject(response, SingleSpinWheelProductNetworkResponse::class.java)
                }
                ApiConstant.API_GET_TREASURE_DATA -> {
                    getObject(response, TreasureBoxNetworkResponse::class.java)
                }
                ApiConstant.API_GET_REWARD_SLOT_MACHINE_PURPOSE -> {
                    getObject(response, SlotMachineResponse::class.java)
                }
                ApiConstant.API_GET_BRANDED_COUPONS_PURPOSE -> {
                    getObject(response, BrandedCouponResponse::class.java)
                }
//                ApiConstant.API_GET_SLOT_MACHINE_DATA -> {
//                    getObject(response, SlotMachineResponse::class.java)
//                }

                ApiConstant.API_GET_LEADERBOARD_DATA -> {
                    getObject(response, LeaderBoardResponse::class.java)
                }

                ApiConstant.API_GET_COUPON_REWARD_DATA -> {
                    getObject(response, BrandedCouponDetailsResponse::class.java)
                }

                ApiConstant.API_GET_ACTIVE_COUPON_DATA -> {
                    getObject(response, BrandedActiveCouponResponse::class.java)
                }

                ApiConstant.API_GET_ACTIVE_COUPON_COUNT_DATA -> {
                    getObject(response, BrandedCouponCountResponse::class.java)
                }

                ApiConstant.API_GET_ALL_JACKPOTS_PRODUCTWISE -> {
                    getObject(response, MultipleJackpotNetworkResponse::class.java)
                }

                ApiConstant.API_POST_OPT_STATUS_DATA -> {
                    getObject(response, WhatsAppNotificationResponse::class.java)
                }

                ApiConstant.API_GET_POCKET_MONEY_REMINDER_DATA -> {
                    getObject(response, PocketMoneyReminderListResponse::class.java)
                }

                ApiConstant.API_DELETE_POCKET_MONEY_REMINDER -> {
                    getObject(response, PocketMoneyReminderResponse::class.java)
                }

                ApiConstant.API_VERIFY_OTP_POCKET_MONEY_REMINDER -> {
                    getObject(response, PocketMoneyOtpVerifyResponse::class.java)
                }

                ApiConstant.API_ADD_POCKET_MONEY_REMINDER -> {
                    getObject(response, PocketMoneyReminderResponse::class.java)
                }

                ApiConstant.API_SAVE_SHOP_DETAILS -> {
                    getObject(response, SaveShopDetailsResponse::class.java)
                }

                ApiConstant.API_FETCH_SHOP_DETAILS -> {
                    getObject(response, FetchShopDetailsResponse::class.java)
                }

                ApiConstant.API_SEND_OTP_KYC -> {
                    getObject(response, SendOtpResponse::class.java)
                }

                ApiConstant.API_VERIFY_OTP_KYC -> {
                    getObject(response, OtpVerifyResponse::class.java)
                }

                ApiConstant.API_AGENTS_EARNINGS_LIST -> {
                    getObject(response, MyEarningsListResponse::class.java)
                }

                ApiConstant.API_FULL_KYC -> {
                    getObject(response, FullKycResponse::class.java)
                }

                ApiConstant.PAYU_PRODUCTION_URL -> {
                    when (command) {
                        PayuConstants.VALIDATE_VPA -> {
                            getObject(response, ValidateVpaResponse::class.java)
                        }
                        else -> {
                            getObject(response, CheckIsDomesticResponse::class.java)
                        }
                    }

                }
                else -> response
            }
        } catch (e: Exception) {
            request.onResponse.onError(request.purpose, NetworkUtil.responseData(e)!!)
            e.toString()
        }
    }

    /**
     * @description Method is used to convert json data into respective POJO class.
     */
    private fun <T> getObject(response: String, instance: Class<T>): Any? {
        return Gson().fromJson(response, instance)
    }
}