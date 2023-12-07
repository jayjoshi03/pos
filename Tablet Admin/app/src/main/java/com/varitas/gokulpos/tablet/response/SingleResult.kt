package com.varitas.gokulpos.tablet.response

import com.google.gson.annotations.SerializedName

data class SingleResult(
        @SerializedName("userId") var userId: Int? = null,
        @SerializedName("name") var name: String? = null,
        @SerializedName("userName") var userName: String? = null,
        @SerializedName("password") var password: String? = null,
        @SerializedName("passPin") var passPin: String? = null,
        @SerializedName("cardNumber") var cardNumber: String? = null,
        @SerializedName("accessToken") var accessToken: String? = null,
        @SerializedName("email") var email: String? = null,
        @SerializedName("mobileNo") var mobileNo: String? = null,
        @SerializedName("storeId") var storeId: Int? = null,
        @SerializedName("storeName") var storeName: String? = null,
        @SerializedName("roleId") var roleId: Int? = null,
        @SerializedName("batchId") var batchId: Int? = null,
        @SerializedName("roleName") var roleName: String? = null)

