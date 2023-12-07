package com.varitas.gokulpos.tablet.response

import com.google.gson.annotations.SerializedName

data class APIResponse(
    @SerializedName("status") var status: Int? = null,
    @SerializedName("errorMessage") var message: String? = null
)