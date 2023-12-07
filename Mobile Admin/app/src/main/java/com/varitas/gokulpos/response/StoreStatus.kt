package com.varitas.gokulpos.response

import com.google.gson.annotations.SerializedName

data class StoreStatus(
    @SerializedName("storeId") var storeId : Int? = null,
    @SerializedName("statusId") var statusId : Int? = null,
    @SerializedName("actionBy") var actionBy : Int? = null,
    @SerializedName("remark") var remark : String? = null,
)
