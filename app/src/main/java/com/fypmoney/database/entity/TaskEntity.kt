package com.fypmoney.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "task_entity")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Long? = null,
    @ColumnInfo(name = "task")
    var task: String?=null,
    @ColumnInfo(name = "task_detail")
    var task_detail: String?=null,
    @ColumnInfo(name = "relation")
    var relation: String?=null,
    @ColumnInfo(name = "userId")
    var userId: Double?=null,
    @ColumnInfo(name = "amount")
    var amount: String?=null,
    @ColumnInfo(name = "time")
    var time: String?=null
)