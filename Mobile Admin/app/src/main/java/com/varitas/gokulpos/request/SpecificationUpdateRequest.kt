package com.varitas.gokulpos.request

import com.google.gson.annotations.SerializedName

data class SpecificationUpdateRequest(
    @SerializedName("id") var id : Int? = null,
    @SerializedName("name") var name : String? = null,
    @SerializedName("uomid") var uomId : Int? = null,
    @SerializedName("typeId") var typeId : Int? = null,
    @SerializedName("type") var type : String? = null,
    @SerializedName("uom") var uom : String? = null,
    @SerializedName("noOfUnit") var noOfUnit : Int? = null,
    @SerializedName("taxFactor") var taxFactor : Int? = null,
    @SerializedName("unitIn") var unitIn : Int? = null,
    @SerializedName("unitPriceFactor") var unitPriceFactor : Int? = null,
    @SerializedName("unitPriceUom") var unitPriceUom : Int? = null,
    @SerializedName("actionBy") var actionBy : Int? = null,
    @SerializedName("isAttribute") var isAttribute : Boolean? = null,
    @SerializedName("isSpecification") var isSpecification : Boolean? = null,
)
