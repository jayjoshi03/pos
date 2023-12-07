package com.varitas.gokulpos.response

import com.google.gson.annotations.SerializedName

data class SalesPerson(
    @SerializedName("id") var id : Int? = 0,
    @SerializedName("name") var name : String? = null,
    @SerializedName("contactNo") var contactNo : String? = null,
    @SerializedName("vendorId") var vendorId : Int? = null,
    @SerializedName("sVendor") var sVendor : String? = null
)
