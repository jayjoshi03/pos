package com.varitas.gokulpos.tablet.request

import com.google.gson.annotations.SerializedName

data class FacilityInsertRequest(
        @SerializedName("name") var name: String? = null,
        @SerializedName("contactName") var contactName: String? = null,
        @SerializedName("address") var address: String? = null,
        @SerializedName("city") var city: String? = null,
        @SerializedName("state") var state: String? = null,
        @SerializedName("country") var country: String? = null,
        @SerializedName("zip") var zip: String? = null,
        @SerializedName("contactNumber") var contactNumber: String? = null,
        @SerializedName("actionBy") var actionBy: Int? = null,
        @SerializedName("storeId") var storeId: Int? = null,
        @SerializedName("isDefault") var isDefault: Boolean? = null
                                )
