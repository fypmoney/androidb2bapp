package com.fypmoney.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.fypmoney.database.entity.ContactEntity


@Dao
abstract class ContactDao : BaseDao<ContactEntity>("contact_entity") {
    // insert contact
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertAllContacts(list: List<ContactEntity>)

    // update contact Is Sync status
    @Query("UPDATE contact_entity  set isSync = :isSyncChanged WHERE isSync = :isSync")
    abstract suspend fun updateIsSyncStatus(
        isSync: Boolean, isSyncChanged: Boolean
    )

    // update contact is App user Status
    @Query("UPDATE contact_entity  set isAppUser = :isAppUser,profilePicResourceId= :profilePicUrl WHERE contactNumber LIKE'%' || :contactNum || '%'")
    abstract suspend fun updateIsAppUserStatus(
        contactNum: String?, isAppUser: Boolean, profilePicUrl: String?
    )

    // fetch all contact and return as a list of entity
    @Query("SELECT * FROM contact_entity ORDER BY isAppUser DESC")
    abstract suspend fun getAllContacts(): List<ContactEntity>

    // fetch all contacts which are not synced to server and return as a list of entity
    @Query("SELECT * FROM contact_entity WHERE isSync = :isSync")
    abstract suspend fun getAllNotSyncedContacts(isSync: Boolean): List<ContactEntity>

    //delete contacts
    @Query("DELETE FROM contact_entity")
    abstract suspend fun deleteAllContacts()

    @Query("DELETE FROM contact_entity WHERE phoneBookIdentifier = :phoneBookIdentifier")
    abstract suspend fun deleteContactsBasedOnLookupKey(phoneBookIdentifier: String?)

    @Query("SELECT * FROM contact_entity group by contactNumber ORDER BY isAppUser DESC")
    abstract suspend fun fetchDistinctUser(): List<ContactEntity?>?
}