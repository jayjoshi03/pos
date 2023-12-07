package com.varitas.gokulpos.request

import com.google.gson.annotations.SerializedName

data class UOMUpdateRequest(
    @SerializedName("id") var id : Int? = null,
    @SerializedName("uom") var uom : String? = null,
    @SerializedName("actionBy") var actionBy : Int? = null
)