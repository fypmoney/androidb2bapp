package com.fypmoney.database.dao


import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.fypmoney.database.entity.LogEntity

@Dao
abstract class LogDao : BaseDao<LogEntity>("log_entity"){
    // insert log
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertLog(log: LogEntity)


    // fetch all logs and return as a list of entity
    @Query("SELECT * FROM log_entity")
    abstract suspend fun getAllLogs(): List<LogEntity>


    //delete logs
    @Query("DELETE FROM log_entity")
    abstract suspend fun deleteAllLogs()

}