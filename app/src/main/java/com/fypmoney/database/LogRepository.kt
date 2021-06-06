package com.fypmoney.database


import com.fypmoney.database.entity.LogEntity
import kotlinx.coroutines.runBlocking


/**
 * This class is used to deal with members for CRUD operations in local database.
 */

class LogRepository(mDB: AppDatabase?) {
    private var appDB: AppDatabase? = mDB

    /**
     * Method to fetch the logs from local database.
     */
    fun getAllLogsFromDatabase(): List<LogEntity>? {
        return runBlocking {
            appDB?.logDao()?.getAllLogs()


        }
    }

    /**
     * Method to insert logs in local database.
     */
    fun insertLog(log: LogEntity) {
        runBlocking {
            appDB?.logDao()?.insertLog(log)

        }
    }


    /**
     * Method to delete logs in local database.
     */
    fun deleteAllLogs() {
        runBlocking {
            appDB?.logDao()?.deleteAllLogs()

        }
    }




}