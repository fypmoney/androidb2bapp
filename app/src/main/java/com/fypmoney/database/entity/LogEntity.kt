package com.fypmoney.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "log_entity")
data class LogEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Long? = null,
    @ColumnInfo(name = "methodName")
    var methodName: String?=null,
    @ColumnInfo(name = "methodValue")
    var methodValue: String?=null
)