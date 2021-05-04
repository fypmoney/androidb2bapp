package com.dreamfolks.baseapp.database


import com.dreamfolks.baseapp.database.entity.MemberEntity
import kotlinx.coroutines.runBlocking


/**
 * This class is used to deal with members for CRUD operations in local database.
 */

class MemberRepository(mDB: AppDatabase?) {
    private var appDB: AppDatabase? = mDB

    /**
     * Method to fetch the members from local database.
     */
    fun getAllMembersFromDatabase(): List<MemberEntity>? {
        return runBlocking {
            appDB?.memberDao()?.getAllMembers()


        }
    }

    /**
     * Method to insert members in local database.
     */
    fun insertAllMembers(memberList: List<MemberEntity>) {
        runBlocking {
            appDB?.memberDao()?.insertAllMembers(memberList)

        }
    }


    /**
     * Method to delete members in local database.
     */
    fun deleteAllMembers() {
        runBlocking {
            appDB?.memberDao()?.deleteAllMembers()

        }
    }




}