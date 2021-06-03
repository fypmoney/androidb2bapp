package com.fypmoney.util

import android.app.DatePickerDialog
import android.content.*
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
import android.text.InputFilter
import android.text.InputFilter.LengthFilter
import android.text.TextUtils
import android.util.DisplayMetrics
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.annotation.ColorInt
import androidx.annotation.NonNull
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatEditText
import com.fypmoney.application.PockketApplication
import com.fypmoney.database.ContactRepository
import com.fypmoney.database.entity.ContactEntity
import com.fypmoney.model.CustomerInfoResponse
import com.google.gson.Gson
import com.google.i18n.phonenumbers.PhoneNumberUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.io.InputStream
import java.nio.charset.Charset
import java.text.NumberFormat
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
    fun extractDigits(msg: String?): String? {
        val p = Pattern.compile("(\\d{6})")
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
    fun showToast(message: String) {
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
    fun showDatePickerDialog(context: Context, onDateSelected: OnDateSelected) {
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
                    simpleDateFormat.parse("$year ${monthOfYear + 1} $dayOfMonth")
                val simpleDateFormatDate =
                    SimpleDateFormat(AppConstants.DATE_FORMAT_CHANGED, Locale.ROOT)
                Log.d(
                    "snebfieuf_yesr",
                    calculateDifferenceBetweenDates(date, getInstance().time).toString()
                )
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
        datePickerDialog.datePicker.maxDate = System.currentTimeMillis()
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

    /*
    * This method will return all the contacts from the phone book
    * */
    fun getAllContacts(
        contentResolver: ContentResolver,
        contactRepository: ContactRepository, onAllContactsAddedListener: OnAllContactsAddedListener
    ) {
        val contactList = mutableListOf<ContactEntity>()
        GlobalScope.launch(Dispatchers.Main) {
            // Switch to a background (IO) thread
            withContext(Dispatchers.IO) {
                val lastDate: String? = SharedPrefUtils.getString(
                    PockketApplication.instance,
                    SharedPrefUtils.SF_KEY_LAST_CONTACTS_SINK_TIMESTAMP
                )
                val contacts: Cursor?
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
                    contactEntity.contactNumber = number.replace(" ", "").trim()


                    /*  // Get the current contact lookup key
                      contactEntity.lookupKey = contacts.getString(
                          contacts.getColumnIndex(ContactsContract.CommonDataKinds.Phone.LOOKUP_KEY)

                      )*/
                    // Get the current contact id
                    contactEntity.phoneBookIdentifier = contacts.getString(
                        contacts.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID)
                    )

                    /*     // Get the current contact id
                         contactEntity.email = contacts.getString(
                             contacts.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA)
                         )

                         Log.d("hdddhdhdh", contactEntity.email!!)
             */
                    setLastContactSinkDate()
                    contactList.add(contactEntity)
                }
                contacts.close()

            }
            // switch to the main thread
            if (SharedPrefUtils.getString(
                    PockketApplication.instance,
                    SharedPrefUtils.SF_KEY_LAST_CONTACTS_SINK_TIMESTAMP
                ) == null
            ) {
                contactRepository.insertAllContacts(contactList)
            } else {
                if (!contactList.isNullOrEmpty()) {
                    contactList.forEach {
                        contactRepository.deleteContactsBasedOnLookupKey(it.phoneBookIdentifier!!)
                    }
                    contactRepository.insertAllContacts(contactList)
                }
            }
            onAllContactsAddedListener.onAllContactsSynced()
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
                //  GlobalScope.launch(Dispatchers.Main) {
                // Switch to a background (IO) thread

                val lastDate: String? = SharedPrefUtils.getString(
                    PockketApplication.instance,
                    SharedPrefUtils.SF_KEY_LAST_CONTACTS_SINK_TIMESTAMP
                )
                try {
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
                        contactEntity.contactNumber = contacts.getString(
                            contacts.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                        ).replace(" ", "").trim()

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

                } catch (
                    e: Exception
                ) {
                    e.printStackTrace()
                }

                //  Log.d("contacts", "step1_before_insertion")


                if (SharedPrefUtils.getString(
                        PockketApplication.instance,
                        SharedPrefUtils.SF_KEY_LAST_CONTACTS_SINK_TIMESTAMP
                    ) == null
                ) {
                    contactRepository.insertAllContacts(contactList)
                    //  Log.d("contacts", "step2_after_insertion")

                } else {
                    when {
                        !contactList.isNullOrEmpty() -> {
                            contactList.forEach {
                                contactRepository.deleteContactsBasedOnLookupKey(it.phoneBookIdentifier!!)
                            }
                            contactRepository.insertAllContacts(contactList)
                            //    Log.d("contacts", "step3_after_insertion")
                        }
                        else -> {
                        }
                    }


                }
            }
            // switch to the main thread
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

    fun canGetLocation(context: Context): Boolean {
        return isLocationEnabled(context) // application context
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
        negativeListener: DialogInterface.OnClickListener?
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
    fun saveCustomerDataInPreference(responseData: CustomerInfoResponse) {
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
    fun getCustomerDataFromPreference(): CustomerInfoResponse? {
        val gson = Gson()
        val json =
            SharedPrefUtils.getString(
                PockketApplication.instance,
                SharedPrefUtils.SF_KEY_USER_PROFILE_INFO
            )
        return gson.fromJson(json, CustomerInfoResponse::class.java)
    }

    /*
    * This method is used to remove + or +91 or 0 from number
    * */
    fun removePlusOrNineOneFromNo(phone: String): String {
        if (phone.startsWith("+91")) {
            return phone.replace("+91", "")
        }
        if (phone.startsWith("+")) {
            return phone.replace("+", "")
        }
        if (phone.startsWith("0")) {
            return phone.replace("0", "")
        }

        return phone

    }

    /*
    * This is used to format the amount
    * */
    fun getFormatedAmount(amount: String): String? {
        return NumberFormat.getNumberInstance(Locale.US).format(amount.toInt())
    }

    /*
    * This method is used to copy the text to clipboard
    * */
    fun copyTextToClipBoard(clipboardManager: ClipboardManager, text: String? = null) {
        val clip =
            ClipData.newPlainText(AppConstants.COPY_LABEL, text)
        clipboardManager.setPrimaryClip(clip)
    }
}
