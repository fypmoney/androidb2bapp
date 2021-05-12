package com.fypmoney.database.dao


import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.fypmoney.model.InterestEntity

@Dao
abstract class InterestDao : BaseDao<InterestEntity>("interest_entity") {
    // insert interest
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertAllInterest(list: List<InterestEntity>)


    // fetch all interest and return as a list of entity
    @Query("SELECT * FROM interest_entity")
    abstract suspend fun getAllInterest(): List<InterestEntity>


    //delete interest
    @Query("DELETE FROM interest_entity")
    abstract suspend fun deleteAllInterest()

}