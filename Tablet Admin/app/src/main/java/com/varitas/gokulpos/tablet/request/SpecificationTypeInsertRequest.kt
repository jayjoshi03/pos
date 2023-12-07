package com.varitas.gokulpos.tablet.request

import com.google.gson.annotations.SerializedName

data class SpecificationTypeInsertRequest(
    @SerializedName("type") var type : String? = null,
    @SerializedName("actionBy") var actionBy : Int? = null,
    @SerializedName("storeId") var storeId : Int? = null
)