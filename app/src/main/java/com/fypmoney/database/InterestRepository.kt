package com.fypmoney.database


import com.fypmoney.model.InterestEntity
import kotlinx.coroutines.runBlocking


/**
 * This class is used to deal with members for CRUD operations in local database.
 */

class InterestRepository(mDB: AppDatabase?) {
    private var appDB: AppDatabase? = mDB

    /**
     * Method to fetch the interest from local database.
     */
    fun getAllInterestFromDatabase(): List<InterestEntity>? {
        return runBlocking {
            appDB?.interestDao()?.getAllInterest()


        }
    }

    /**
     * Method to insert interest in local database.
     */
    fun insertAllInterest(interestList: List<InterestEntity>) {
        runBlocking {
            appDB?.interestDao()?.insertAllInterest(interestList)

        }
    }


    /**
     * Method to delete interest in local database.
     */
    fun deleteAllInterest() {
        runBlocking {
            appDB?.interestDao()?.deleteAllInterest()

        }
    }




}