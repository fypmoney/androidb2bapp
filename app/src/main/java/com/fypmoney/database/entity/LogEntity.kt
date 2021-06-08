package com.fypmoney.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.sql.Timestamp

@Entity(tableName = "log_entity")
data class LogEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Long? = null,
    @ColumnInfo(name = "methodName")
    var methodName: String?=null,
    @ColumnInfo(name = "methodValue")
    var methodValue: String?=null,
    @ColumnInfo(name = "timestamp")
    var timestamp: Long?=null,

    )