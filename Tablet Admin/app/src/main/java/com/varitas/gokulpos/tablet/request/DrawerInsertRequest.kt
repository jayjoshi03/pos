package com.varitas.gokulpos.tablet.request

import com.google.gson.annotations.SerializedName

data class DrawerInsertRequest(
        @SerializedName("typeId") var typeId: Int? = null,
        @SerializedName("amount") var amount: Double? = null,
        @SerializedName("description") var description: String? = null,
        @SerializedName("storeId") var storeId: Int? = null,
        @SerializedName("actionBy") var actionBy: Int? = null,
                              )