package com.varitas.gokulpos.tablet.response

import com.google.gson.annotations.SerializedName

data class Currency(
        @SerializedName("id") var id: Int? = null,
        @SerializedName("curCountryId") var curCountryId: Int? = null,
        @SerializedName("name") var name: String? = null,
        @SerializedName("unitValue") var unitValue: Double? = null,
        @SerializedName("status") var status: Int? = null,
        @SerializedName("createdBy") var createdBy: Int? = null,
        @SerializedName("createdOnUtc") var createdOnUtc: String? = null,
        @SerializedName("modifiedBy") var modifiedBy: String? = null,
        @SerializedName("modifiedOnUtc") var modifiedOnUtc: String? = null
                   )