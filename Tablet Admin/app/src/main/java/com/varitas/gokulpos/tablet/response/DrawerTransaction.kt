package com.varitas.gokulpos.tablet.response

import com.google.gson.annotations.SerializedName

data class DrawerTransaction(
        @SerializedName("id") var id: Int? = null,
        @SerializedName("typeId") var typeId: Int? = null,
        @SerializedName("amount") var amount: Double? = null,
        @SerializedName("description") var description: String? = null,
        @SerializedName("drawerType") var drawerType: String? = null,
        @SerializedName("transactionDateTime") var transactionDateTime: String? = null
                            )