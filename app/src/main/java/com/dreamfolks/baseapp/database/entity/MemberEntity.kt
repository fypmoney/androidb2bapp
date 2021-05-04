package com.dreamfolks.baseapp.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "member_entity")
data class MemberEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Long? = null,
    @ColumnInfo(name = "name")
    var name: String?,
    @ColumnInfo(name = "mobileNo")
    var mobileNo: String?,
    @ColumnInfo(name = "relation")
    var relation: String?,
    @ColumnInfo(name = "userId")
    var userId: Double?,
    @ColumnInfo(name = "relationDisplayName")
    var relationDisplayName: String?,
    @ColumnInfo(name = "familyName")
    var familyName: String?,
    @ColumnInfo(name = "status")
    var status: String?
)