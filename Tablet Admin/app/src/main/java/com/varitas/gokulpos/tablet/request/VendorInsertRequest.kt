package com.varitas.gokulpos.tablet.request

import com.google.gson.annotations.SerializedName

data class VendorInsertRequest(
        val address: String? = null,
        val city: String? = null,
        val code: String? = null,
        val companyName: String? = null,
        val country: String? = null,
        var actionBy: Int? = null,
        val email: String? = null,
        val groupId: Int? = null,
        val name: String? = null,
        val payTerm: Int? = null,
        val personName: String? = null,
        val phoneNo: String? = null,
        val state: String? = null,
        var storeId: Int? = null,
        val taxGroupId: Int? = null
                              )

data class AddSalePerson(
        @SerializedName("id") var id: Int? = null,
        @SerializedName("name") var name: String? = null,
        @SerializedName("contactNo") var contactNo: String? = null,
        @SerializedName("vendorId") var vendorId: Int? = null
                        )