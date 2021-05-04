package com.dreamfolks.baseapp.database

import android.util.Log
import com.dreamfolks.baseapp.database.entity.ContactEntity
import com.dreamfolks.baseapp.model.ContactRequestDetails
import com.dreamfolks.baseapp.model.UserPhoneContact
import kotlinx.coroutines.runBlocking


/**
 * This class is used to deal with contacts for CRUD operations in local database.
 */

class ContactRepository(mDB: AppDatabase?) {
    private var appDB: AppDatabase? = mDB

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
            appDB?.contactDao()?.updateIsSyncStatus(isSync = false, isSyncChanged = true)
            if (!contactList.isNullOrEmpty()) {
                contactList.forEachIndexed { index, userPhoneContact ->
                    appDB?.contactDao()
                        ?.updateIsAppUserStatus(
                            isAppUser = true,
                            contactNum = userPhoneContact.contactNumber
                        )

                }
            }
        }

    }

}