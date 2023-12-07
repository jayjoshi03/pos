package com.varitas.gokulpos.tablet.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Specification(
        @SerializedName("id") var id: Int? = null,
        @SerializedName("name") var name: String? = null,
        @SerializedName("type") var type: String? = null,
        @SerializedName("uom") var uom: String? = null,
        @SerializedName("typeId") var typeId: Int? = null,
        @SerializedName("uomid") var uomId: Int? = null,
        @SerializedName("noOfUnit") var noOfUnit: Int? = null,
        @SerializedName("taxFactor") var taxFactor: Double? = null,
        @SerializedName("unitIn") var unitIn: Int? = null,
        @SerializedName("unitPriceFactor") var unitPriceFactor: Double? = null,
        @SerializedName("unitPriceUom") var unitPriceUom: Int? = null,
        @SerializedName("storeId") var storeId: Int? = null,
        @SerializedName("status") var status: Int? = null,
                        ) : Parcelable

