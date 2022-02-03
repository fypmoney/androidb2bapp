package com.fypmoney.util

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type


/**
 * SharedPreferences Utils class to hold the shared preferences in the application
 */
class SharedPrefUtils {

    companion object {
        const val SF_KEY_FIREBASE_TOKEN = "firebase_token"
        const val SF_KEY_NEW_MESSAGE = "new_message"
        const val SF_KEY_ACCESS_TOKEN = "access_token"
        const val SF_KEY_STORAGE_PERMANENTLY_DENY = "sf_key_storage_permanently_deny"
        const val SF_KEY_LAST_CONTACTS_SINK_TIMESTAMP = "last_contacts_sink_timestamp"
        const val SF_KEY_IS_LOGIN = "is_login"
        const val SF_KEY_USERNAME = "username"
        const val SF_KEY_USER_ID = "user_id"
        const val SF_KEY_USER_MOBILE = "user_mobile"
        const val SF_KEY_USER_INTEREST = "user_interest"
        const val SF_UPI_LIST = "saved_upi_list"
        const val SF_KEY_USER_PROFILE_INFO = "user_profile_info"
        const val SF_KEY_USER_FIRST_NAME = "first name"
        const val SF_KEY_USER_LAST_NAME = "last name"
        const val SF_KEY_USER_EMAIL = "email"
        const val SF_KEY_PROFILE_IMAGE = "imageurl"
        const val SF_KEY_KIT_NUMBER = "kitNo"
        const val SF_KEY_USER_FAMILY_NAME = "family name"
        const val SF_KEY_USER_DOB = "dob"
        const val SF_KEY_SELECTED_RELATION = "relation"
        const val SF_KEY_AADHAAR_NUMBER = "aadhaar_number"
        const val SF_KEY_APP_UPDATE_TYPE = "in_app_update_type"
        const val SF_KEY_APP_UPDATE_STATUS = "in_app_update_status"
        const val SF_KEY_CARD_FLAG = "card_screen_flag"
        const val SF_KEY_REFER_LINE1 = "refer_line_1"
        const val SF_KEY_REFER_LINE2 = "refer_line_2"
        const val SF_KEY_REFERAL_GLOBAL_MSG = "refer_global_msg"
        const val SF_KEY_REFEREE_CASHBACK = "refer_cashback_amount"
        const val SF_ADD_MONEY_VIDEO = "add_money_video"
        const val SF_KEY_ERROR_MESSAGE_HOME = "error_msg_home"
        const val SF_KEY_IS_NEW_FEED_AVAILABLE = "is_new_feed_available"
        const val SF_KEY_NAME_ON_CARD = "name_on_card"
        const val SF_KEY_USER_DELIVERY_ADDRESS = "delivery_addres"
        const val SF_KEY_IS_ORDER_SCARTCH_CODE_DONE = "order_on_card_scratch_done"
        const val SF_KEY_APP_VERSION_CODE = "app_version_code"
        const val SF_REFFERAL_MSG = "refer_share_message_0"
        const val SF_REGISTER_MSG_1 = "register_share_message_1"
        const val SF_REGISTER_MSG_90 = "register_share_message_90"
        const val SF_REFFERAL_MSG_2 = "refer_share_message_1"
        const val SF_IS_USER_LANDED_ON_HOME_SCREEN_TIME = "is_user_landed_on_home_screen_time"
        const val SF_REFERRAL_CODE_FROM_INVITE_LINK = "referral_code_from_invite_link"
        const val SF_IS_INSTALLED_APPS_SYNCED = "is_installed_apps_is_syncd"
        const val SF_KYC_TYPE = "kyc_type"

        private fun getSharedPreferences(context: Context): SharedPreferences? {
            return PreferenceManager.getDefaultSharedPreferences(context)
        }

        fun putString(context: Context, key: String, value: String?) {
            val editor = getSharedPreferences(context)?.edit()
            editor?.putString(key, value)?.apply()
        }

        fun putArrayList(context: Context, key: String, value: ArrayList<String>?) {
            val gson = Gson()

            val jsonText = gson.toJson(value)
            val editor = getSharedPreferences(context)?.edit()
            editor?.putString(key, jsonText)?.apply()
        }

        fun getArrayList(context: Context, key: String): ArrayList<String>? {
            val gson = Gson()
            val jsonText: String? = getSharedPreferences(context)?.getString(key, null)
            val type: Type = object : TypeToken<ArrayList<String?>?>() {}.type
            return gson.fromJson(jsonText, type)
        }


        fun getString(context: Context, key: String): String? {
            return getSharedPreferences(context)?.getString(key, null)
        }

        fun putInt(context: Context, key: String, value: Int?) {
            val editor = getSharedPreferences(context)?.edit()
            editor?.putInt(key, value!!)?.apply()
        }

        fun getInt(context: Context, key: String): Int? {
            return getSharedPreferences(context)?.getInt(key, 0)
        }

        fun putBoolean(context: Context, key: String, value: Boolean?) {
            val editor = getSharedPreferences(context)?.edit()
            editor?.putBoolean(key, value!!)?.apply()
        }

        fun getBoolean(context: Context, key: String): Boolean? {
            return getSharedPreferences(context)?.getBoolean(key, false)
        }

        fun putLong(context: Context, key: String, value: Long) {
            val editor = getSharedPreferences(context)?.edit()
            editor?.putLong(key, value)?.apply()
        }

        fun getLong(context: Context, key: String): Long {
            var value:Long? = 0L
            try {
                value =  getSharedPreferences(context)?.getLong(key, 0L)

            }catch (e:ClassCastException){
                FirebaseCrashlytics.getInstance()
                    .setCustomKey("preference_key", key)
                FirebaseCrashlytics.getInstance().recordException(e)
            }catch (e:Exception){
                FirebaseCrashlytics.getInstance()
                    .setCustomKey("preference_key", key)
                FirebaseCrashlytics.getInstance().recordException(e)
            }
            return value!!
        }
    }
}