package com.fypmoney.database

import android.content.ContentResolver
import android.database.Cursor
import android.provider.ContactsContract
import android.util.Log
import com.fypmoney.application.PockketApplication
import com.fypmoney.database.entity.ContactEntity
import com.fypmoney.model.ContactRequestDetails
import com.fypmoney.model.UserPhoneContact
import com.fypmoney.util.SharedPrefUtils
import com.fypmoney.util.Utility
import kotlinx.coroutines.runBlocking


/**
 * This class is used to deal with contacts for CRUD operations in local database.
 */

class ContactRepository(mDB: AppDatabase?) {
    private var appDB: AppDatabase? = mDB
    private val TAG = ContactRepository::class.java.simpleName
    /*  */
    /**
     * Method to fetch the contacts from local database.
     *//*
    fun getContactsFromDatabase(): List<ContactEntity>? {
        return runBlocking {
            appDB?.contactDao()?.getAllContacts()
        }
    }*/

    /**
     * Method to fetch the contacts from local database.
     */
    fun getContactsFromDatabase(): List<ContactEntity>? {
        return runBlocking {
            appDB?.contactDao()?.getAllContacts()
        }
    }

    /**
     * Method to fetch the contacts from local database.
     */
    fun getDistinctContactsFromDatabase(): List<ContactEntity?>? {
        return runBlocking {
            appDB?.contactDao()?.fetchDistinctUser()


        }
    }

    /**
     * Method to insert contacts in local database.
     */
    fun insertAllContacts(contactList: List<ContactEntity>) {
        runBlocking {
            appDB?.contactDao()?.insertAllContacts(contactList)
            //  Log.d("contacts","insertAllContacts")
        }
    }


    /**
     * Method to delete contacts in local database.
     */
    fun deleteAllContacts() {
        runBlocking {
            appDB?.contactDao()?.deleteAllContacts()

        }
    }

    /**
     * Method to parse the log entity fetched from local database to model class.
     */
    private fun parseModelFromEntity(entityList: List<ContactEntity>?): List<ContactRequestDetails> {
        val contactRequestDetailsList = ArrayList<ContactRequestDetails>()
        entityList?.forEach {
            val contactRequest = ContactRequestDetails()
            contactRequest.contactNumber = it.contactNumber
            contactRequest.firstName = it.firstName
            contactRequest.lastName = it.lastName
            contactRequest.phoneBookIdentifier = it.phoneBookIdentifier
            contactRequestDetailsList.add(contactRequest)
        }
        return contactRequestDetailsList

    }

    /*
    * This is used to get all noin synced contacts
    * */
    fun getAllNotSyncedContacts(): List<ContactRequestDetails> {
        return runBlocking {
            parseModelFromEntity(
                appDB?.contactDao()?.getAllNotSyncedContacts(false)
            )
        }

    }

    /**
     * Method to delete contacts in local database based on lookup key.
     */
    fun deleteContactsBasedOnLookupKey(phoneBookIdentifier: String) {
        runBlocking {
            appDB?.contactDao()?.deleteContactsBasedOnLookupKey(phoneBookIdentifier)

        }
    }

    /*
    * This method is used to change the status of contacts which are uploaded to server
    * */
    fun updateIsSyncAndIsAppUserStatus(contactList: ArrayList<UserPhoneContact>?) {
        runBlocking {
            if (!contactList.isNullOrEmpty()) {
                contactList.forEachIndexed { index, userPhoneContact ->
                    appDB?.contactDao()
                        ?.updateIsAppUserStatus(
                            isAppUser = true,
                            contactNum = userPhoneContact.contactNumber,
                            profilePicUrl = userPhoneContact.profilePicResourceId,
                            userId = userPhoneContact.userId,
                            isSync = true
                        )


                }
            }
        }

    }

    fun getContactsFromPhoneBookAndStoreInRoom(contentResolver: ContentResolver){
        //Get last sync time
        val lastSyncTimeInMilis: String? = SharedPrefUtils.getString(
            PockketApplication.instance,
            SharedPrefUtils.SF_KEY_LAST_CONTACTS_SINK_TIMESTAMP
        )
        Log.d(TAG,"last date and time for sync:$lastSyncTimeInMilis")
        var contactsCursor:Cursor? = null
        lastSyncTimeInMilis?.let {
            contactsCursor = contentResolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null,
                ContactsContract.CommonDataKinds.Phone.CONTACT_LAST_UPDATED_TIMESTAMP + " >= ?",
                arrayOf(it),
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC"
            )
        }?: kotlin.run {
            Log.d(TAG,"lastSyncTimeInMilis null :$lastSyncTimeInMilis")
            contactsCursor = contentResolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null,
                null,
                null,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC"
            )
        }
        contactsCursor?.let {
            while (it.moveToNext()){
                val contactEntity = ContactEntity()
                val name = it.getString(it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
                Log.d(TAG,"contact name: $name")

            }

        }?: kotlin.run {
            Log.d(TAG,"contactsCursor:$contactsCursor")
        }


    }


}