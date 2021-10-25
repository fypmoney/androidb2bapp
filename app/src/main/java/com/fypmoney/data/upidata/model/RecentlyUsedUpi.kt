package com.fypmoney.data.upidata.model

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "recently_used_upi",indices = [Index(value = ["upi_id","tag"],unique = true)])
data class RecentlyUsedUpi(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val id:Long? = 0,
    @NonNull @ColumnInfo(name = "upi_id") val upiId:String,
    @NonNull @ColumnInfo(name = "tag") val tag:String,
    @ColumnInfo(name = "no_of_transaction") val noOfTransaction: Long = 0
)
