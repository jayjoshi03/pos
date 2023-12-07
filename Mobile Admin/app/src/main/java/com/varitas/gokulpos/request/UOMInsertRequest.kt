package com.varitas.gokulpos.request

import com.google.gson.annotations.SerializedName

data class UOMInsertRequest(
    @SerializedName("uom") var uom : String? = null,
    @SerializedName("actionBy") var actionBy : Int? = null,
    @SerializedName("storeId") var storeId : Int? = null
)