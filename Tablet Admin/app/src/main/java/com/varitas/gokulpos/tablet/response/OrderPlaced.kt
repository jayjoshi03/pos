package com.varitas.gokulpos.tablet.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class OrderPlaced(
        @SerializedName("orderId") var orderId: Int? = null,
        @SerializedName("orderNo") var orderNo: String? = null,
        @SerializedName("orderTotal") var orderTotal: Double? = null,
        @SerializedName("paymentTotal") var paymentTotal: Double? = null,
        @SerializedName("message") var message: String? = null,
        @SerializedName("isStock") var isStock: Boolean? = null,
        var due: Double = 0.00
                      ) : Parcelable
