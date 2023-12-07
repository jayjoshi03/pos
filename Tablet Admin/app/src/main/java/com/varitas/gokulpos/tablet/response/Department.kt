package com.varitas.gokulpos.tablet.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Department(
        @SerializedName("id") var id: Int? = null,
        @SerializedName("name") var name: String? = null,
        @SerializedName("code") var code: String? = null,
        @SerializedName("allowFoodStamp") var allowFoodStamp: Boolean? = null,
        @SerializedName("ageVerification") var ageVerification: Boolean? = null,
        @SerializedName("showInOpenPrice") var showInOpenPrice: Boolean? = null,
        @SerializedName("isTaxable") var isTaxable: Boolean? = null,
        @SerializedName("taxGroupId") var taxGroupId: Int? = null,
        @SerializedName("priceRatioValue") var priceRatioValue: Double? = null,
        @SerializedName("priceRatioType") var priceRatioType: Int? = null,
        @SerializedName("sAllowFoodStamp") var sAllowFoodStamp: String? = null,
        @SerializedName("itemCount") var itemCount: Int? = null,
        @SerializedName("sIsTaxable") var sIsTaxable: String? = null) : Parcelable