package com.varitas.gokulpos.tablet.request

data class VendorUpdateRequest(
    val address: String? = null,
    val city: String? = null,
    val code: String? = null,
    val companyName: String? = null,
    val country: String? = null,
    val email: String? = null,
    val groupId: Int? = null,
    val id: Int? = null,
    var actionBy: Int? = null,
    val name: String? = null,
    val payTerm: Int? = null,
    val personName: String? = null,
    val phoneNo: String? = null,
    val state: String? = null,
    val taxGroupId: Int? = null
)