package com.fypmoney.util

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
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


        private fun getSharedPreferences(context: Context): SharedPreferences? {
            return PreferenceManager.getDefaultSharedPreferences(context)
        }

        fun putString(context: Context, key: String, value: String?) {
            val editor = getSharedPreferences(context)?.edit()
            editor?.putString(key, value)?.apply()
        }

        fun putArrayList(context: Context, key: String, value: ArrayList<String>?) {
            val gson = Gson()
            val textList = ArrayList<String>(value)
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
            return getSharedPreferences(context)?.getLong(key, 0L)!!
        }
    }
}