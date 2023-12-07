package com.varitas.gokulpos.request

import com.google.gson.annotations.SerializedName

data class FacilityUpdateRequest(
    @SerializedName("id") var id : Int? = null,
    @SerializedName("name") var name : String? = null,
    @SerializedName("contactName") var contactName : String? = null,
    @SerializedName("address") var address : String? = null,
    @SerializedName("city") var city : String? = null,
    @SerializedName("state") var state : String? = null,
    @SerializedName("country") var country : String? = null,
    @SerializedName("zip") var zip : String? = null,
    @SerializedName("contactNumber") var contactNumber : String? = null,
    @SerializedName("actionBy") var actionBy : Int? = null,
    @SerializedName("isDefault") var isDefault : Boolean? = null,
)
