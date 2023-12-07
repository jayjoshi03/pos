package com.varitas.gokulpos.tablet.request

import com.google.gson.annotations.SerializedName

data class SpecificationInsertRequest(
        @SerializedName("name") var name: String? = null,
        @SerializedName("uomid") var uomId: Int? = null,
        @SerializedName("typeId") var typeId: Int? = null,
        @SerializedName("type") var type: String? = null,
        @SerializedName("uom") var uom: String? = null,
        @SerializedName("noOfUnit") var noOfUnit: Int? = null,
        @SerializedName("taxFactor") var taxFactor: Double? = null,
        @SerializedName("unitIn") var unitIn: Int? = null,
        @SerializedName("unitPriceFactor") var unitPriceFactor: Double? = null,
        @SerializedName("unitPriceUom") var unitPriceUom: Int? = null,
        @SerializedName("storeId") var storeId: Int? = null,
        @SerializedName("actionBy") var actionBy: Int? = null
                                     )
