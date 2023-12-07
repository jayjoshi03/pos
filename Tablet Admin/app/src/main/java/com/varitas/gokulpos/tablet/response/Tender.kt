package com.varitas.gokulpos.tablet.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Tender(
        @SerializedName("id") var id : Int? = null,
        @SerializedName("name") var name : String? = null,
        @SerializedName("paymentMode") var paymentMode : Int? = null,
        @SerializedName("sPaymentMode") var sPaymentMode : String? = null,
        @SerializedName("cardType") var cardType: Int? = null,
        @SerializedName("sCardType") var sCardType: String? = null,
        @SerializedName("exchangeRate") var exchangeRate: Double? = null,
        @SerializedName("conversionRate") var conversionRate: Double? = null,
        @SerializedName("currencySymbol") var currencySymbol: String? = null,
        @SerializedName("minPaymentAmount") var minPaymentAmount: Double? = null,
        @SerializedName("imageId") var imageId: Int? = null,
        @SerializedName("actionBy") var actionBy: Int? = null,
        @SerializedName("storeId") var storeId: Int? = null,
        @SerializedName("status") var status: Int? = null
                 ) : Parcelable
