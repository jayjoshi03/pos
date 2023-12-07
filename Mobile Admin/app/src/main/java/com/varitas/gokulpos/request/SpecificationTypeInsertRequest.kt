package com.varitas.gokulpos.request

import com.google.gson.annotations.SerializedName

data class SpecificationTypeInsertRequest(
    @SerializedName("type") var type : String? = null,
    @SerializedName("createdBy") var createdBy : Int? = null,
    @SerializedName("storeId") var storeId : Int? = null
)