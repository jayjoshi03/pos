package com.varitas.gokulpos.response

import com.google.gson.annotations.SerializedName

data class OrderList(
    @SerializedName("id") var id : Int? = null,
    @SerializedName("createdDateTime") var createdDateTime : String? = null,
    @SerializedName("orderNo") var orderNo : String? = null,
    @SerializedName("subTotal") var subTotal : Double? = null,
    @SerializedName("tax") var tax : Double? = null,
    @SerializedName("totalIncTax") var totalIncTax : Double? = null,
    @SerializedName("discountAmount") var discountAmount : Double? = null,
    @SerializedName("extraCharges") var extraCharges : Double? = null,
    @SerializedName("sCreatedBy") var sCreatedBy : String? = null,
    @SerializedName("createdBy") var createdBy : Int? = null,
    @SerializedName("sStatus") var sStatus : String? = null,
    @SerializedName("sPaymentStatus") var sPaymentStatus : String? = null,
    @SerializedName("storeId") var storeId : Int? = null,
    @SerializedName("orderItemCount") var orderItemCount : Int? = null,
    @SerializedName("status") var status : Int? = null,
    @SerializedName("paymentStatus") var paymentStatus : Int? = null,
)