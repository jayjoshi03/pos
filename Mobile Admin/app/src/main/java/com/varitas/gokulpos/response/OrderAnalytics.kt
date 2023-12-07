package com.varitas.gokulpos.response

import com.google.gson.annotations.SerializedName

data class OrderAnalytics(
    @SerializedName("ordersummary"  ) var orderSummary  : ArrayList<Summary>  = arrayListOf(),
    @SerializedName("paymentStatus" ) var paymentStatus : ArrayList<Summary> = arrayListOf(),
    @SerializedName("paymentMethod" ) var paymentMethod : ArrayList<Summary> = arrayListOf()
)