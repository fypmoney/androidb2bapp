package com.fypmoney.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "member_entity")
data class MemberEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Long? = null,
    @ColumnInfo(name = "name")
    var name: String?=null,
    @ColumnInfo(name = "mobileNo")
    var mobileNo: String? = null,
    @ColumnInfo(name = "relation")
    var relation: String? = null,
    @ColumnInfo(name = "userId")
    var userId: Double? = null,
    @ColumnInfo(name = "relationDisplayName")
    var relationDisplayName: String? = null,
    @ColumnInfo(name = "familyName")
    var familyName: String? = null,
    @ColumnInfo(name = "status")
    var status: String? = null,
    @ColumnInfo(name = "profilePicResourceId")
    var profilePicResourceId: String? = null,

    )