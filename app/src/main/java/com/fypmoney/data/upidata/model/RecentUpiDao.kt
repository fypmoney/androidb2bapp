package com.fypmoney.data.upidata.model

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface RecentUpiDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUpiId(upi: RecentlyUsedUpi)

    @Query("SELECT * FROM  recently_used_upi  ORDER BY id")
    fun getAllUpiId(): Flow<List<RecentlyUsedUpi>>

    @Query("UPDATE recently_used_upi SET tag =:tag WHERE upi_id=:upiId")
    fun updateTag(tag:String,upiId:String)

    @Query("UPDATE recently_used_upi SET no_of_transaction =:noOfTransaction WHERE upi_id=:upiId")
    fun updateNoOfTransaction(noOfTransaction:String,upiId:String)


}