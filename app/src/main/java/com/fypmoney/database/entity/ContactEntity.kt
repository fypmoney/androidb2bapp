package com.fypmoney.database.entity

import android.os.Parcelable
import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
@Keep
@Parcelize
@Entity(tableName = "contact_entity")
data class ContactEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Long? = null,
    @ColumnInfo(name = "firstName")
    var firstName: String? = null,
    @ColumnInfo(name = "lastName")
    var lastName: String? = null,
    @ColumnInfo(name = "contactNumber")
    var contactNumber: String? = null,
    @ColumnInfo(name = "phoneBookIdentifier")
    var phoneBookIdentifier: String? = null,
    @ColumnInfo(name = "profilePicResourceId")
    var profilePicResourceId: String? = null,
    @ColumnInfo(name = "isSync")
    var isSync: Boolean? = false,
    @ColumnInfo(name = "userId")
    var userId: String? = null,
    @ColumnInfo(name = "isAppUser")
    var isAppUser: Boolean? = false,
    @ColumnInfo(name = "isSelected")
    var isSelected: Boolean? = false
) : Parcelable