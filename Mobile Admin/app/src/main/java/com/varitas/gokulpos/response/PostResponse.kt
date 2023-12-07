package com.varitas.gokulpos.response

import com.google.gson.annotations.SerializedName

/**
 * @suppress Use this for Login Response
 */
data class PostResponse(
    @SerializedName("status") var status: Int? = null,
    @SerializedName("message") var message: String? = null,
    @SerializedName("data") var singleResult: SingleResult? = SingleResult()
)
