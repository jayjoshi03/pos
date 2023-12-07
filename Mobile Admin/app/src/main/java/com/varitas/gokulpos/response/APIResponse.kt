package com.varitas.gokulpos.response

import com.google.gson.annotations.SerializedName

data class APIResponse(
    @SerializedName("status") var status: Int? = null,
    @SerializedName("errorMessage") var message: String? = null
)