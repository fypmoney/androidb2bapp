package com.fypmoney.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.io.Serializable


@Keep
data class ContactRequest(
    @SerializedName("userPhoneContact") val contactRequestDetails: List<ContactRequestDetails>
) : BaseRequest()

@Keep
data class ContactRequestDetails(
    @SerializedName("contactNumber") var contactNumber: String? = null,
    @SerializedName("firstName") var firstName: String? = null,
    @SerializedName("lastName") var lastName: String? = null,
    @SerializedName("phoneBookIdentifier") var phoneBookIdentifier: String? = null,
) : BaseRequest()
@Keep
data class ContactResponse(
    @SerializedName("data") var contactResponseDetails: ContactResponseDetails?
) : Serializable
@Keep
data class ContactResponseDetails(
    @SerializedName("userId") var userId: String?,
    @SerializedName("profilePicResourceId") var profilePicResourceId: String?,
    @SerializedName("userPhoneContact") var userPhoneContact: ArrayList<UserPhoneContact>?
) : Serializable
@Keep
data class UserPhoneContact(
    @SerializedName("userId") var userId: String?,
    @SerializedName("contactNumber") var contactNumber: String?,
    @SerializedName("phoneBookIdentifier") var phoneBookIdentifier: String?,
    @SerializedName("isAppUser") var isAppUser: Boolean,
    @SerializedName("profilePicResourceId") var profilePicResourceId: String?
    ) : Serializable