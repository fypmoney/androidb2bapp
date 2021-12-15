package com.fypmoney.view.register.model

data class SendKycDetails(
    var documentIdentifier: String? = null,
    var firstName: String? = null,
    var lastName: String? = null,
    var pincode: String? = null,
    var address: String? = null,
    var gender: String? = null,
    var dob: String? = null,
    var kycDocumentType: String? = null
)

