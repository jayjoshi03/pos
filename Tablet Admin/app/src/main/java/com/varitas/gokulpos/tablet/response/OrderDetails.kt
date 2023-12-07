package com.varitas.gokulpos.tablet.response

import com.google.gson.annotations.SerializedName

data class OrderDetails(@SerializedName("id") var id: Int? = null, @SerializedName("paymentStatus") var paymentStatus: String? = null, @SerializedName("tax") var tax: Double? = null, @SerializedName("orderDiscount") var orderDiscount: Double? = null, @SerializedName("createdOnUtc") var createdOnUtc: String? = null, @SerializedName("paymentMethod") var paymentMethod: String? = null, @SerializedName("orderTotal") var orderTotal: Double? = null, @SerializedName("customerName") var customerName: String? = null, @SerializedName("statusClass") var statusClass: String? = null, @SerializedName("subTotal") var subTotal: Double? = null, @SerializedName("orderdetailsdata") var orderdetailsdata: ArrayList<OrderItemDetails> = arrayListOf())
