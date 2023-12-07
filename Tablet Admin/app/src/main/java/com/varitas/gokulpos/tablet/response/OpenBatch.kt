package com.varitas.gokulpos.tablet.response

import com.google.gson.annotations.SerializedName

data class OpenBatch(
        @SerializedName("id") var id: Int? = null
                    )

data class OpenBatchRequest(
        @SerializedName("userId") var userId: Int? = null,
        @SerializedName("storeId") var storeId: Int? = null,
        @SerializedName("openingBalance") var openingBalance: Double? = null
                           )

