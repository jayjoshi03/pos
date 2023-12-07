package com.varitas.gokulpos.request

import com.google.gson.annotations.SerializedName

data class TaxUpdateRequest(
    val className : String? = null,
    val rateValue : Double? = null,
    var id : Int? = null,
    var actionBy : Int? = null,
    @SerializedName("bySize") var bySize : Boolean? = null
)