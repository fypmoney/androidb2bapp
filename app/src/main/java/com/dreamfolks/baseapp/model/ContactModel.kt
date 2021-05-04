package com.dreamfolks.baseapp.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class ContactModel(
    var name: String? = null,
    var number: String? = null,
    var isSelected: Boolean? = false,
) {
}

data class ContactRequest(
    @SerializedName("userPhoneContact") val contactRequestDetails: List<ContactRequestDetails>
) : BaseRequest()


data class ContactRequestDetails(
    @SerializedName("contactNumber") var contactNumber: String? = null,
    @SerializedName("firstName") var firstName: String? = null,
    @SerializedName("lastName") var lastName: String? = null,
    @SerializedName("phoneBookIdentifier") var phoneBookIdentifier: String? = null,
) : BaseRequest()

data class ContactResponse(
    @SerializedName("data") var contactResponseDetails: ContactResponseDetails?
) : Serializable

data class ContactResponseDetails(
    @SerializedName("userId") var userId: String?,
    @SerializedName("userPhoneContact") var userPhoneContact: ArrayList<UserPhoneContact>?
) : Serializable

data class UserPhoneContact(
    @SerializedName("userId") var userId: String?,
    @SerializedName("contactNumber") var contactNumber: String?,
    @SerializedName("phoneBookIdentifier") var phoneBookIdentifier: String?,
    @SerializedName("isAppUser") var isAppUser: Boolean?,
) : Serializable