package com.fypmoney.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

data class InterestResponse(
    @SerializedName("data") var interestDetails: List<InterestEntity>?
)

@Entity(tableName = "interest_entity")
data class InterestEntity(
    @PrimaryKey(autoGenerate = true)
    var tableId: Long? = null,
    @ColumnInfo(name = "id")
    var id: Int?,
    @ColumnInfo(name = "name")
    var name: String?,
    @ColumnInfo(name = "code")
    var code: String?,
    @ColumnInfo(name = "resourceId")
    var resourceId: String?,
    @ColumnInfo(name = "backgroundColor")
    var backgroundColor: String?,
    @ColumnInfo(name = "description")
    var description: String?,
    @ColumnInfo(name = "status")
    var status: String?,
    var isSelected: Boolean
)