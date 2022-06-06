package com.fypmoney.util

import android.app.Activity
import android.app.DatePickerDialog
import android.content.*
import android.content.ClipboardManager
import android.content.pm.PackageManager
import android.content.res.Resources
import android.database.Cursor
import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.provider.ContactsContract
import android.provider.Settings
import android.text.*
import android.text.InputFilter.LengthFilter
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.DisplayMetrics
import android.util.Log
import android.util.Patterns
import android.view.View
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.widget.ImageView
import android.widget.TextView
import android.widget.TextView.BufferType
import android.widget.Toast
import androidx.annotation.ColorInt
import androidx.annotation.NonNull
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatEditText
import com.bumptech.glide.Glide
import com.fypmoney.R
import com.fypmoney.application.PockketApplication
import com.fypmoney.bindingAdapters.shimmerDrawable
import com.fypmoney.database.ContactRepository
import com.fypmoney.database.entity.ContactEntity
import com.fypmoney.model.CustomerInfoResponseDetails
import com.fypmoney.util.AppConstants.CHORES
import com.fypmoney.util.AppConstants.CardScreen
import com.fypmoney.util.AppConstants.DATE_FORMAT_CHANGED
import com.fypmoney.util.AppConstants.FEEDSCREEN
import com.fypmoney.util.AppConstants.FyperScreen
import com.fypmoney.util.AppConstants.HOMEVIEW
import com.fypmoney.util.AppConstants.JACKPOTTAB
import com.fypmoney.util.AppConstants.OfferScreen
import com.fypmoney.util.AppConstants.OrderCard
import com.fypmoney.util.AppConstants.ReferralScreen
import com.fypmoney.util.AppConstants.StoreScreen
import com.fypmoney.util.AppConstants.StoreofferScreen
import com.fypmoney.util.AppConstants.StoreshopsScreen
import com.fypmoney.util.AppConstants.TRACKORDER
import com.fypmoney.view.activity.ChoresActivity
import com.fypmoney.view.activity.OfferDetailActivity
import com.fypmoney.view.fragment.OffersStoreActivity
import com.fypmoney.view.fragment.StoresActivity
import com.fypmoney.view.home.main.homescreen.view.HomeActivity
import com.fypmoney.view.ordercard.OrderCardView
import com.fypmoney.view.ordercard.model.UserDeliveryAddress
import com.fypmoney.view.ordercard.trackorder.TrackOrderView
import com.fypmoney.view.referandearn.view.ReferAndEarnActivity
import com.fypmoney.view.storeoffers.OffersScreen
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.gson.Gson
import com.google.i18n.phonenumbers.PhoneNumberUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.io.InputStream
import java.nio.charset.Charset
import java.text.DecimalFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.Calendar.*
import java.util.regex.Matcher
import java.util.regex.Pattern


/*
      * This class is utility class used to set methods for entire app
      * */
object Utility {
    /*
      * This method is used to set color filters
      * */
    fun setColorFilter(@NonNull drawable: Drawable?, @ColorInt color: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            drawable?.colorFilter = BlendModeColorFilter(color, BlendMode.SRC_ATOP)
        } else {
            drawable?.setColorFilter(color, PorterDuff.Mode.SRC_ATOP)
        }
    }

    fun makeTextViewResizable(tv: TextView, maxLine: Int, expandText: String, viewMore: Boolean) {
        if (tv.tag == null) {
            tv.tag = tv.text
        }
        try{
            val vto = tv.viewTreeObserver
            vto.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    val text: String
                    val lineEndIndex: Int
                    val obs = tv.viewTreeObserver
                    obs.removeOnGlobalLayoutListener(this)
                    if (maxLine == 0) {
                        lineEndIndex = tv.layout.getLineEnd(0)
                        text = tv.text.subSequence(0, lineEndIndex - expandText.length + 1)
                            .toString() + " " + expandText
                        tv.text = text

                        tv.movementMethod = LinkMovementMethod.getInstance()

                        tv.setText(
                            addClickablePartTextViewResizable(
                                SpannableString(tv.text.toString()), tv, lineEndIndex, expandText,
                                viewMore
                            ), BufferType.SPANNABLE
                        )
                    } else if (maxLine > 0 && tv.lineCount >= maxLine) {
                        lineEndIndex = tv.layout.getLineEnd(maxLine - 1)
                        text = tv.text.subSequence(0, lineEndIndex - expandText.length + 1)
                            .toString() + " " + expandText
                        tv.text = text

                        tv.movementMethod = LinkMovementMethod.getInstance()

                        tv.setText(
                            addClickablePartTextViewResizable(
                                SpannableString(tv.text.toString()), tv, lineEndIndex, expandText,
                                viewMore
                            ), BufferType.SPANNABLE
                        )
                    } else {
                        lineEndIndex = tv.layout.getLineEnd(tv.layout.lineCount - 1)
                        text = tv.text.subSequence(0, lineEndIndex).toString()
                        tv.text = text

                        tv.movementMethod = LinkMovementMethod.getInstance()

                    }

                }
            })
        }catch (e:StringIndexOutOfBoundsException){
            FirebaseCrashlytics.getInstance().recordException(e)
        }

    }

    private fun addClickablePartTextViewResizable(
        strSpanned: Spanned, tv: TextView,
        maxLine: Int, spanableText: String, viewMore: Boolean
    ): SpannableStringBuilder? {
        val str = strSpanned.toString()
        val ssb = SpannableStringBuilder(strSpanned)
        if (str.contains(spanableText)) {
            ssb.setSpan(object : ClickableSpan() {
                override fun onClick(widget: View) {
                    tv.layoutParams = tv.layoutParams
                    tv.setText(tv.tag.toString(), BufferType.SPANNABLE)
                    tv.invalidate()
                    if (viewMore) {
                        makeTextViewResizable(tv, -1, "View Less", false)
                    } else {
                        makeTextViewResizable(tv, 3, "View More", true)
                    }
                }
            }, str.indexOf(spanableText), str.indexOf(spanableText) + spanableText.length, 0)
        }
        return ssb
    }

    /*
          * This method is used to convert dp into pixels
          * */
    fun convertDpToPixel(dp: Float, context: Context): Float {
        val resources = context.resources
        val metrics = resources.displayMetrics
        return dp * (metrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
    }

    /*
          * This method is used to get get id of the resource
          * */
    fun getDrawableResourceId(context: Context, name: String): Int {
        val resources: Resources = context.resources
        return resources.getIdentifier(
            name, "drawable",
            context.packageName
        )
    }

    /*
          * This method is used to set maximum length
          * */
    fun setEditTextMaxLength(editText: AppCompatEditText, length: Int) {
        val filterArray = arrayOfNulls<InputFilter>(1)
        filterArray[0] = LengthFilter(length)
        editText.filters = filterArray
    }

    /*
          * This method is used to extract only digits from string
          * */
    fun extractDigits(msg: String): String {
        val p = Pattern.compile("(\\d{4,8})")
        val m = p.matcher(msg ?: "")
        return if (m.find()) {
            m.group(0)
        } else ""
    }

    /*
        * This method is used to get json from assets
        * */
    fun getJsonFromAssets(context: Context, fileName: String): String? {
        val jsonString: String
        try {
            val inputStream: InputStream = context.assets.open(fileName)

            val size: Int = inputStream.available()
            val buffer = ByteArray(size)
            inputStream.read(buffer)
            inputStream.close()

            jsonString = String(buffer, Charset.forName("UTF-8"))
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        }

        return jsonString
    }

    /*
    * This method is used to check whether a string is email type or not
    * */
    fun isEmail(text: String?): Boolean {
        val expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$"
        val p: Pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE)
        val m: Matcher = p.matcher(text.toString())
        return m.matches()
    }

    /*
        * This method is used to check if it is phone type
        * */
    fun isPhone(text: String?): Boolean {
        return if (!TextUtils.isEmpty(text)) {
            (TextUtils.isDigitsOnly(text) && text?.length ?: 0 >= 6 && text?.length ?: 0 <= 15)
        } else {
            false
        }
    }

    /*
    * This method is used to show toast message
    * */
    fun showToast(message: String?) {
        if (!message.isNullOrEmpty())
            Toast.makeText(PockketApplication.instance, message, Toast.LENGTH_LONG).show()
    }


    /*
      * This method is used to set error in editext
      * */
    fun setError(editText: AppCompatEditText, errorString: String?) {
        editText.requestFocus()
        editText.error = errorString
    }

    /*
      * This method is used to clear error in editext
      * */
    fun clearError(editText: AppCompatEditText) {
        editText.error = null
        editText.clearFocus()
    }

    /*
    * This method is used to check the validity of Email
    * */
    fun isValidEmail(target: CharSequence): Boolean {
        return !TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches()
    }

    /*
     * This method is used to show the date picker dialog
     * */
    fun showDatePickerDialog(
        context: Context,
        onDateSelected: OnDateSelected,
        isDateOfBirth: Boolean = false
    ) {
        val c: Calendar = getInstance()
        val mYear = c.get(YEAR)
        val mMonth = c.get(MONTH)
        val mDay = c.get(DAY_OF_MONTH)
        val datePickerDialog = DatePickerDialog(
            context,
            { _, year, monthOfYear, dayOfMonth ->
                val simpleDateFormat =
                    SimpleDateFormat("yyyy MM dd", Locale.ROOT)
                val date: Date? =
                    simpleDateFormat.parse("${year} ${monthOfYear + 1} $dayOfMonth")
                val simpleDateFormatDate =
                    SimpleDateFormat(DATE_FORMAT_CHANGED, Locale.ROOT)
                // calculateDifferenceBetweenDates(date,getInstance().time)
                date?.let {
                    onDateSelected.onDateSelected(
                        simpleDateFormatDate.format(it), SimpleDateFormat(
                            AppConstants.DATE_TIME_FORMAT_SERVER,
                            Locale.ROOT
                        ).format(it), calculateDifferenceBetweenDates(date, getInstance().time)
                    )

                }
            },
            mYear,
            mMonth,
            mDay
        )
        if (isDateOfBirth) {
            datePickerDialog.datePicker.maxDate =
                (System.currentTimeMillis() - 347039786000)//11 years //Todo
            datePickerDialog.datePicker.minDate = (System.currentTimeMillis() - 2208984820000)//70

        } else {
            datePickerDialog.datePicker.maxDate = System.currentTimeMillis()
        }
        datePickerDialog.show()

    }

    fun showDatePickerInDateFormatDialog(
        context: Context,
        onDateSelected: OnDateSelectedWithDateFormat,
        isDateOfBirth: Boolean = false
    ) {
        val c: Calendar = getInstance()
        val mYear = c.get(YEAR)
        val mMonth = c.get(MONTH)
        val mDay = c.get(DAY_OF_MONTH)
        val datePickerDialog = DatePickerDialog(
            context,
            { _, year, monthOfYear, dayOfMonth ->
                val simpleDateFormat =
                    SimpleDateFormat("yyyy MM dd", Locale.ROOT)
                val date: Date? =
                    simpleDateFormat.parse("${year} ${monthOfYear + 1} $dayOfMonth")
                val simpleDateFormatDate =
                    SimpleDateFormat(DATE_FORMAT_CHANGED, Locale.ROOT)
                // calculateDifferenceBetweenDates(date,getInstance().time)
                date?.let {
                    onDateSelected.onDateSelectedWithDateFormat(
                        SimpleDateFormat(
                            AppConstants.DATE_TIME_FORMAT_SERVER,
                            Locale.ROOT
                        ).format(it),
                        simpleDateFormatDate.format(it)
                    )

                }
            },
            mYear,
            mMonth,
            mDay
        )
        if (isDateOfBirth) {
            datePickerDialog.datePicker.maxDate =
                (System.currentTimeMillis() - 347039786000)//11 years //Todo
            datePickerDialog.datePicker.minDate = (System.currentTimeMillis() - 2208984820000)//70

        } else {
            datePickerDialog.datePicker.maxDate = System.currentTimeMillis()
        }
        datePickerDialog.show()

    }

    fun showDatePickerDialogWithStartDate(
        context: Context,
        onDateSelectedwithStart: OnDateSelectedwithStart,
        isDateOfBirth: Boolean = false,
        calendar: Calendar?
    ) {
        val c: Calendar = getInstance()
        val cal: Calendar
        if (calendar == null) {
            cal = c
        } else {
            cal = calendar
        }
//        cal.set(Calendar.DAY_OF_MONTH,21)

        Log.d("Chackdate", cal.get(Calendar.DAY_OF_MONTH).toString())
        val datePickerDialog = DatePickerDialog(
            context,
            { _, year, monthOfYear, dayOfMonth ->
                val simpleDateFormat =
                    SimpleDateFormat("yyyy MM dd", Locale.ROOT)
                val date: Date? =
                    simpleDateFormat.parse("${year} ${monthOfYear + 1} $dayOfMonth")
                val simpleDateFormatDate =
                    SimpleDateFormat(DATE_FORMAT_CHANGED, Locale.ROOT)
                // calculateDifferenceBetweenDates(date,getInstance().time)
                if (calendar == null) {
                    cal.set(YEAR, year)
                    cal.set(MONTH, monthOfYear)
                    cal.set(DAY_OF_MONTH, dayOfMonth)
                }

                date?.let {
                    onDateSelectedwithStart.onDateSelectedwithStart(
                        simpleDateFormatDate.format(it), SimpleDateFormat(
                            AppConstants.DATE_TIME_FORMAT_SERVER,
                            Locale.ROOT
                        ).format(it), calculateDifferenceBetweenDates(date, getInstance().time),
                        cal
                    )

                }
            },
            cal.get(YEAR),
            cal.get(MONTH),
            cal.get(DAY_OF_MONTH)

        )
        if (calendar != null) {
            datePickerDialog.datePicker.minDate = cal.time.time
        }

//        if(isDateOfBirth){
//            datePickerDialog?.datePicker!!.maxDate = (System.currentTimeMillis() - 347039786000)//11 years //Todo
//            datePickerDialog?.datePicker.minDate = (System.currentTimeMillis() - 2208984820000)//70
//
//        }else{
//            datePickerDialog.datePicker.maxDate = System.currentTimeMillis()
//        }
        datePickerDialog.show()

    }

    /**
     *  navigate to application settings
     */
    fun goToAppSettings(context: Context) {
        val intent = Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.parse(AppConstants.APP_SETTINGS_PACKAGE_TEXT + context.packageName)
        )
        intent.addCategory(Intent.CATEGORY_DEFAULT)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }
    fun goToAppSettingsPermission(context: Activity,requestCode:Int) {
        val intent = Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.parse(AppConstants.APP_SETTINGS_PACKAGE_TEXT + context.packageName)
        )
        intent.addCategory(Intent.CATEGORY_DEFAULT)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivityForResult(intent, requestCode)

    }

    /*
    * This method is used to check if user is login or not
    * */
    fun isUserLoggedIn(context: Context): Boolean {
        //  return SharedPrefUtils.getLong(context, SharedPrefUtils.SF_KEY_ACCESS_TOKEN) != 0L
        return false
    }


    interface OnDateSelected {
        fun onDateSelected(dateOnEditText: String, dateOnServer: String, yearDifference: Int) {
        }
    }

    interface OnDateSelectedWithDateFormat {
        fun onDateSelectedWithDateFormat(dateOnServer: String?, dateOnEditText: String) {
        }
    }

    interface OnDateSelectedwithStart {
        fun onDateSelectedwithStart(
            dateOnEditText: String,
            dateOnServer: String,
            yearDifference: Int,
            c: Calendar
        ) {
        }
    }

    interface OnAllContactsAddedListener {
        fun onAllContactsSynced(contactEntity: MutableList<ContactEntity>? = null)
    }


    /*
     * This method will return all the contacts from the phone book
     * */
    fun getAllContactsInList(
        contentResolver: ContentResolver,
        onAllContactsAddedListener: OnAllContactsAddedListener,
        contactRepository: ContactRepository
    ) {
        val contactList = mutableListOf<ContactEntity>()
        GlobalScope.launch(Dispatchers.Main) {
            // Switch to a background (IO) thread
            withContext(Dispatchers.IO) {

                val contacts: Cursor?
                val lastDate: String? = SharedPrefUtils.getString(
                    PockketApplication.instance,
                    SharedPrefUtils.SF_KEY_LAST_CONTACTS_SINK_TIMESTAMP
                )
                if (lastDate != null) {
                        contacts = contentResolver.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_LAST_UPDATED_TIMESTAMP + " >= ?",
                            arrayOf(lastDate),
                            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY + " ASC"
                        )
                    } else {
                        contacts = contentResolver.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            null,
                            null,
                            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY + " ASC"
                        )

                    }
                    // Loop through the contacts
                    while (contacts?.moveToNext()!!) {
                        val contactEntity = ContactEntity()
                        // Get the current contact name
                        val name = contacts.getString(
                            contacts.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY)
                        )
                        var lastName: String? = if (name?.trim()?.split(" ")?.size ?: 0 > 1) {
                            ""
                        } else {
                            null
                        }
                        val list = name?.trim()?.split(" ")
                        list?.forEachIndexed { index, s ->
                            if (index > 0) {
                                lastName = "$lastName${s.trim()} "
                            }

                        }
                        if (list?.isNotEmpty() == true)
                            contactEntity.firstName = list[0].trim()
                        // Get the current contact last name
                        contactEntity.lastName = lastName?.trim()

                        // Get the current contact phone number
                        val number = contacts.getString(
                            contacts.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                        )
                        var updatedNumber = number.replace(" ", "").trim()
                        if (updatedNumber.length > 10) {
                            updatedNumber = updatedNumber.takeLast(10)
                        }
                        contactEntity.contactNumber = updatedNumber

                        // Get the current contact id
                        contactEntity.phoneBookIdentifier = contacts.getString(
                            contacts.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID)
                        )
                        setLastContactSinkDate()
                        val newList =
                            contactList.filter { it.contactNumber == contactEntity.contactNumber }
                        if (newList.isNullOrEmpty())
                            contactList.add(contactEntity)
                    }
                    contacts.close()


                if (SharedPrefUtils.getString(
                        PockketApplication.instance,
                        SharedPrefUtils.SF_KEY_LAST_CONTACTS_SINK_TIMESTAMP
                    ) == null
                ) {
                    contactRepository.insertAllContacts(contactList)
                } else {
                    when {
                        !contactList.isNullOrEmpty() -> {
                            contactList.forEach {
                                contactRepository.deleteContactsBasedOnLookupKey(it.phoneBookIdentifier!!)
                            }
                            contactRepository.insertAllContacts(contactList)
                        }
                        else -> {
                        }
                    }
                }
            }
            onAllContactsAddedListener.onAllContactsSynced(contactRepository.getContactsFromDatabase() as MutableList<ContactEntity>)

        }
    }


    /*
    * set the last sink date
    * */
    private fun setLastContactSinkDate() {
        SharedPrefUtils.putString(
            PockketApplication.instance,
            SharedPrefUtils.SF_KEY_LAST_CONTACTS_SINK_TIMESTAMP,
            System.currentTimeMillis().toString()
        )

    }

    /*
     * remove country code
     * */
    fun deleteCountryCode(phone: String): String {
        val phoneInstance = PhoneNumberUtil.getInstance()
        try {
            val phoneNumber = phoneInstance.parse(phone, null)
            return phoneNumber.nationalNumber.toString() ?: phone
        } catch (_: Exception) {
        }
        return phone
    }



    /*
    * This method will check if location is enabled or not
    * */
    private fun isLocationEnabled(context: Context): Boolean {
        var locationMode = 0
        val locationProviders: String
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            try {
                locationMode =
                    Settings.Secure.getInt(
                        context.contentResolver,
                        Settings.Secure.LOCATION_MODE
                    )
            } catch (e: Settings.SettingNotFoundException) {
                e.printStackTrace()
            }
            locationMode != Settings.Secure.LOCATION_MODE_OFF
        } else {
            locationProviders =
                Settings.Secure.getString(
                    context.contentResolver,
                    Settings.Secure.LOCATION_MODE
                )
            !TextUtils.isEmpty(locationProviders)
        }
    }

    fun showDialog(
        context: Context?,
        info: String?,
        positiveButton: String?,
        negativeButton: String?,
        cancelable: Boolean?,
        positiveListener: DialogInterface.OnClickListener?,
        negativeListener: DialogInterface.OnClickListener? = null
    ) {
        val builder = AlertDialog.Builder(
            context!!
        )
        builder.setMessage(info)
        builder.setPositiveButton(positiveButton, positiveListener)
        builder.setNegativeButton(negativeButton, negativeListener)
        val alertDialog = builder.create()
        alertDialog.setCancelable(cancelable!!)
        alertDialog.show()
    }

    /*
     * This method will reset after logout
     * */
    fun resetPreferenceAfterLogout() {
        SharedPrefUtils.putBoolean(
            PockketApplication.instance,
            SharedPrefUtils.SF_KEY_IS_LOGIN,
            false
        )
        SharedPrefUtils.putArrayList(
            PockketApplication.instance,
            SharedPrefUtils.SF_KEY_USER_INTEREST,
            null
        )
        SharedPrefUtils.putString(
            PockketApplication.instance,
            SharedPrefUtils.SF_KEY_ACCESS_TOKEN,
            ""
        )
        SharedPrefUtils.putString(
            PockketApplication.instance, key = SharedPrefUtils.SF_DICORD_CONNECTED,
            value = ""
        )
        SharedPrefUtils.putString(
            PockketApplication.instance,
            SharedPrefUtils.SF_KEY_USER_PROFILE_INFO,
            ""
        )

        SharedPrefUtils.putString(
            PockketApplication.instance,
            SharedPrefUtils.SF_KEY_USER_FIRST_NAME,
            ""
        )

        SharedPrefUtils.putString(
            PockketApplication.instance,
            SharedPrefUtils.SF_KEY_USER_EMAIL,
            ""
        )
        SharedPrefUtils.putString(
            PockketApplication.instance,
            SharedPrefUtils.SF_KEY_USER_LAST_NAME,
            ""
        )
        SharedPrefUtils.putString(
            PockketApplication.instance,
            SharedPrefUtils.SF_KEY_USER_MOBILE,
            ""
        )
        SharedPrefUtils.putString(
            PockketApplication.instance,
            SharedPrefUtils.SF_KEY_USER_ID,
            ""
        )
        SharedPrefUtils.putString(
            PockketApplication.instance,
            SharedPrefUtils.SF_KEY_USERNAME,
            ""
        )
        SharedPrefUtils.putString(
            PockketApplication.instance,
            SharedPrefUtils.SF_KEY_KIT_NUMBER,
            ""
        )
        SharedPrefUtils.putBoolean(
            PockketApplication.instance,
            SharedPrefUtils.SF_KEY_IS_ORDER_SCARTCH_CODE_DONE,
            false
        )
        SharedPrefUtils.putString(
            PockketApplication.instance,
            SharedPrefUtils.SF_KEY_NAME_ON_CARD,
            ""
        )
        SharedPrefUtils.putString(
            PockketApplication.instance,
            SharedPrefUtils.SF_KEY_USER_DELIVERY_ADDRESS,
            ""
        )
        SharedPrefUtils.putBoolean(
            PockketApplication.instance,
            SharedPrefUtils.SF_IS_INSTALLED_APPS_SYNCED,
            false
        )
        SharedPrefUtils.putBoolean(
            PockketApplication.instance,
            SharedPrefUtils.SF_CARD_PROMO_CODE_APPLIED,
            false
        )
    }

    /*
    * This method is used to calculate difference between 2 dates
    * */
    private fun calculateDifferenceBetweenDates(first: Date?, last: Date?): Int {
        val a = getCalendar(first)
        val b = getCalendar(last)
        var diff = b[YEAR] - a[YEAR]
        if (a[MONTH] > b[MONTH] ||
            a[MONTH] === b[MONTH] && a[DATE] > b[DATE]
        ) {
            diff--
        }
        return diff
    }

    private fun getCalendar(date: Date?): Calendar {
        val cal = getInstance(Locale.US)
        cal.time = date!!
        return cal
    }

    /*
    * This is used to store the data of customer in the preference
    * */
    fun saveCustomerDataInPreference(responseData: CustomerInfoResponseDetails?) {
        val gson = Gson()
        val json = gson.toJson(responseData)
        SharedPrefUtils.putString(
            PockketApplication.instance,
            SharedPrefUtils.SF_KEY_USER_PROFILE_INFO,
            json
        )
    }

    /*
        * This is used to get the data of customer from the preference
        * */
    fun getCustomerDataFromPreference(): CustomerInfoResponseDetails? {
        val gson = Gson()
        val json =
            SharedPrefUtils.getString(
                PockketApplication.instance,
                SharedPrefUtils.SF_KEY_USER_PROFILE_INFO
            )
        return gson.fromJson(json, CustomerInfoResponseDetails::class.java)
    }

    fun setCustomerDeliveryAddress(deliveryAddress: UserDeliveryAddress) {
        val gson = Gson()
        val json = gson.toJson(deliveryAddress)
        SharedPrefUtils.putString(
            PockketApplication.instance,
            SharedPrefUtils.SF_KEY_USER_DELIVERY_ADDRESS,
            json
        )
    }

    fun getCustomerDeliveryAddress(): UserDeliveryAddress? {
        val gson = Gson()
        val json =
            SharedPrefUtils.getString(
                PockketApplication.instance,
                SharedPrefUtils.SF_KEY_USER_DELIVERY_ADDRESS
            )
        return gson.fromJson(json, UserDeliveryAddress::class.java)
    }

    /*
    * This method is used to remove + or +91 or 0 from number
    * */
    fun removePlusOrNineOneFromNo(phone: String?): String {
        phone.let {
            when {
                phone?.startsWith("+91") == true -> {
                    return phone.replace("+91", "").replace(" ","")
                }
                phone?.startsWith("+") == true -> {
                    return phone.replace("+", "").replace(" ","")
                }
                phone?.startsWith("0") == true -> {
                    return phone.replace("0", "").replace(" ","")
                }
                else -> return phone!!.replace(" ","")
            }
        }
    }

    /*
    * This is used to format the amount
    * */
    fun getFormatedAmount(amount: String): String {
        //return NumberFormat.getNumberInstance(Locale.US).format(amount)
        return amount
    }

    /*
    * This method is used to copy the text to clipboard
    * */
    fun copyTextToClipBoard(clipboardManager: ClipboardManager, text: String? = null) {
        val clip =
            ClipData.newPlainText(AppConstants.COPY_LABEL, text)
        clipboardManager.setPrimaryClip(clip)
        showToast(PockketApplication.instance.getString(R.string.copy_to_clipboard_success))
    }


    /*
    * This method is used to convert amount to paise
    * */
    fun convertToPaise(amount: String?): String? {
        try {
            val format = DecimalFormat("0.#")
            return format.format(amount?.toDouble()!! * 100)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    /*
    * This method is used to convert amount to Rs
    * */
    fun convertToRs(amount: String?): String? {
        var result: String? = null
        try {
            result = (amount?.toDouble()!! / 100).toString()
            val list = result.split(".")
            if (list.size > 1) {
                if (list[1] == "0" || list[1] == "00") {
                    return list[0]
                } else {
                    return result
                }

            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return result
    }


    /*
 * This method is used to convert date time
 * */

    fun parseDateTime(
        dateTime: String? = null,
        inputFormat: String? = AppConstants.SERVER_DATE_TIME_FORMAT1,
        outputFormat: String? = AppConstants.CHANGED_DATE_TIME_FORMAT1
    ): String {
        return if (dateTime != null) {
            val input = SimpleDateFormat(inputFormat, Locale.getDefault())
            val output = SimpleDateFormat(outputFormat, Locale.getDefault())

            var d: Date? = null
            try {
                d = input.parse(dateTime)
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            output.format(d)
        } else {
            ""
        }
    }



    /*
    * This method is used to compare dates
    * */
    fun compareDates(
        fromDate: String, toDate: String
    ): Boolean? {
        var compareDate: Boolean? = false
        val sdf = SimpleDateFormat(DATE_FORMAT_CHANGED, Locale.getDefault())

        if (sdf.parse(toDate) == sdf.parse(fromDate)) {
            compareDate = true
        } else if (sdf.parse(toDate).after(sdf.parse(fromDate))) {
            compareDate = sdf.parse(toDate).after(sdf.parse(fromDate))
        }
        return compareDate
    }

    /*
     * This method is used to set image using glide
     * */
    fun setImageUsingGlide(
        context: Context? = PockketApplication.instance,
        url: String?,
        imageView: ImageView
    ) {
        url.let {
            if (!url.isNullOrEmpty()) {
                Glide.with(context!!).load(url).placeholder(R.drawable.ic_fyp_logo)
                    .into(imageView)
            } else {
                imageView.setImageResource(R.drawable.ic_user)

            }
        }
    }

    /*
     * This method is used to set image using glide
     * */
    fun setImageUsingGlideWithShimmerPlaceholder(
        context: Context? = PockketApplication.instance,
        url: String?,
        imageView: ImageView
    ) {


        url.let {
            if (!url.isNullOrEmpty()) {
                Glide.with(context!!).load(url).placeholder(shimmerDrawable())
                    .into(imageView)
            } else {
                imageView.setImageResource(R.drawable.ic_user)

            }
        }
    }

    fun setImageUsingGlide2(
        context: Context? = PockketApplication.instance,
        url: String?,
        imageView: ImageView
    ) {
        url.let {
            if (!url.isNullOrEmpty()) {
                Glide.with(context!!).load(url)
                    .into(imageView)
            } else {
                imageView.setImageResource(R.drawable.ic_user)

            }
        }
    }

    /*
    * This is used to call messaging app
    * */
    fun callMessagingApp(context: Context) {
        val intent = Intent(Intent.ACTION_MAIN)
        intent.addCategory(Intent.CATEGORY_APP_MESSAGING)
        context.startActivity(intent)
    }


    fun getFirstName(fullName: String?): String? {
        fullName?.let {
            val split = fullName.split(" ")
            if (split.isEmpty()) {
                return fullName;
            } else if (split.size > 1) {
                return split[0];

            }
        }
        return fullName
    }



    fun deeplinkRedirection(screenName: String, context: Context) {
        var intent: Intent? = null

        when (screenName) {
            HOMEVIEW -> {
                intent = Intent(context, HomeActivity::class.java)
            }
            ReferralScreen -> {
                intent = Intent(context, ReferAndEarnActivity::class.java)

            }
            JACKPOTTAB -> {
//                intent = Intent(context, RewardsActivity::class.java)
//                intent.putExtra(AppConstants.FROM_WHICH_SCREEN, JACKPOTTAB)
            }

            CardScreen -> {
                intent = Intent(context, HomeActivity::class.java)
                intent.putExtra(AppConstants.FROM_WHICH_SCREEN, CardScreen)

            }
            OfferScreen -> {
                intent = Intent(context, OffersScreen::class.java)
                intent.putExtra(AppConstants.FROM_WHICH_SCREEN, OfferScreen)

            }
            StoreScreen -> {
                intent = Intent(context, HomeActivity::class.java)
                intent.putExtra(AppConstants.FROM_WHICH_SCREEN, StoreScreen)

            }
            StoreofferScreen -> {
                intent = Intent(context, OffersStoreActivity::class.java)
                intent.putExtra(AppConstants.FROM_WHICH_SCREEN, StoreofferScreen)

            }

            StoreshopsScreen -> {
                intent = Intent(context, StoresActivity::class.java)
                intent.putExtra(AppConstants.FROM_WHICH_SCREEN, StoreshopsScreen)

            }

            FEEDSCREEN -> {
                intent = Intent(context, HomeActivity::class.java)
                intent.putExtra(AppConstants.FROM_WHICH_SCREEN, FEEDSCREEN)

            }
            FyperScreen -> {
                intent = Intent(context, HomeActivity::class.java)
                intent.putExtra(AppConstants.FROM_WHICH_SCREEN, FyperScreen)

            }
            TRACKORDER -> {
                intent = Intent(context, TrackOrderView::class.java)

            }
            CHORES -> {
                intent = Intent(context, ChoresActivity::class.java)

            }
            OrderCard -> {
                intent = Intent(context, OrderCardView::class.java)

            }
            else -> {
                if (screenName.startsWith("offerdetails_")) {
                    intent = Intent(context, OfferDetailActivity::class.java)
                    var feedId = screenName.split("_")[1]
                    intent.putExtra("feedid", feedId)

                }
            }
        }

        intent?.let {
            context.startActivity(intent)

        }
    }



    fun toTitleCase(string: String?): String? {

        // Check if String is null
        if (string == null) {
            return null
        }
        var whiteSpace = true
        val builder = StringBuilder(string) // String builder to store string
        val builderLength = builder.length

        // Loop through builder
        for (i in 0 until builderLength) {
            val c = builder[i] // Get character at builders position
            if (whiteSpace) {

                // Check if character is not white space
                if (!Character.isWhitespace(c)) {

                    // Convert to title case and leave whitespace mode.
                    builder.setCharAt(i, Character.toTitleCase(c))
                    whiteSpace = false
                }
            } else if (Character.isWhitespace(c)) {
                whiteSpace = true // Set character is white space
            } else {
                builder.setCharAt(i, Character.toLowerCase(c)) // Set character to lowercase
            }
        }
        return builder.toString() // Return builders text
    }

    fun stringToCardNumber(input:String): StringBuilder {
        val result = StringBuilder()
        for (i in input.indices) {
            if (i % 4 == 0 && i != 0) {
                result.append(" ")
            }
            result.append(input.get(i))
        }
        return result
    }

    fun onCopyClicked(textToCopy:String,context:Context) {
        copyTextToClipBoard(
            context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager,
            textToCopy
        )
    }

    fun isFirstInstall(context: Context): Boolean {
        return try {
            val firstInstallTime =
                context.packageManager.getPackageInfo(context.packageName, 0).firstInstallTime
            val lastUpdateTime =
                context.packageManager.getPackageInfo(context.packageName, 0).lastUpdateTime
            firstInstallTime == lastUpdateTime
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            true
        }
    }


    fun isInstallFromUpdate(context: Context): Boolean {
        return try {
            val firstInstallTime =
                context.packageManager.getPackageInfo(context.packageName, 0).firstInstallTime
            val lastUpdateTime =
                context.packageManager.getPackageInfo(context.packageName, 0).lastUpdateTime
            firstInstallTime != lastUpdateTime
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            false
        }
    }



    sealed class MobileNumberFromPhoneBook{
        data class MobileNumberFound(val phoneNumber:String):MobileNumberFromPhoneBook()
        data class UnableToFindMobileNumber(val errorMsg:String):MobileNumberFromPhoneBook()
    }
    fun getPhoneNumberFromContact(activity: Activity,data: Intent?):MobileNumberFromPhoneBook{
        val contactData = data!!.data
        val c: Cursor? =
            contactData?.let { activity.contentResolver.query(it, null, null, null, null) }
        if (c != null) {
            if (c.moveToFirst()) {
                val id = c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts._ID))
                val hasPhone = c.getString(c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))
                if (hasPhone.equals("1", ignoreCase = true)) {
                    val phones: Cursor? = activity.contentResolver.query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + id,
                        null, null
                    )
                    phones?.moveToFirst()
                    return if (phones != null) {
                        val cNumber = phones.getString(phones.getColumnIndex("data1"))
                        System.out.println("number is:$cNumber")
                        val phoneNuber = cNumber.replace("\\s".toRegex(), "")
                        phones.close()
                        MobileNumberFromPhoneBook.MobileNumberFound(phoneNuber.takeLast(10))

                    }else{
                        MobileNumberFromPhoneBook.UnableToFindMobileNumber(activity.getString(R.string.unable_to_pick_phone_number))

                    }
                }else{
                    return MobileNumberFromPhoneBook.UnableToFindMobileNumber(activity.getString(R.string.unable_to_pick_phone_number))

                }
            }else{
                return MobileNumberFromPhoneBook.UnableToFindMobileNumber(activity.getString(R.string.unable_to_pick_phone_number))
            }
        }
        return MobileNumberFromPhoneBook.UnableToFindMobileNumber(activity.getString(R.string.unable_to_pick_phone_number))

    }
}
