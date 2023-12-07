package com.varitas.gokulpos.tablet.response

import com.google.gson.annotations.SerializedName

data class Payments(@SerializedName("orderTotal") val orderTotal: Double, @SerializedName("paymentMethod") val paymentMethod: String)