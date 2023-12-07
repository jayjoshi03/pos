package com.varitas.gokulpos.request

import com.google.gson.annotations.SerializedName

data class VendorInsertRequest(
    @SerializedName("name") var name : String? = null,
    @SerializedName("code") var code : String? = null,
    @SerializedName("taxGroupId") var taxGroupId : Int? = null,
    @SerializedName("companyName") var companyName : String? = null,
    @SerializedName("personName") var personName : String? = null,
    @SerializedName("address") var address : String? = null,
    @SerializedName("city") var city : String? = null,
    @SerializedName("state") var state : String? = null,
    @SerializedName("country") var country : String? = null,
    @SerializedName("phoneNo") var phoneNo : String? = null,
    @SerializedName("email") var email : String? = null,
    @SerializedName("payTerm") var payTerm : Int? = null,
    @SerializedName("groupId") var groupId : Int? = null,
    @SerializedName("storeId") var storeId : Int? = null,
    @SerializedName("actionBy") var actionBy : Int? = null
)