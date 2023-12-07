package com.varitas.gokulpos.tablet.request

import com.google.gson.annotations.SerializedName

data class LoginRequest(@SerializedName("userName") var user: String, @SerializedName("password") var password: String, @SerializedName("appToken") var appToken: String, @SerializedName("type") var type: String = "1")
