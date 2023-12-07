package com.varitas.gokulpos.response

import com.google.gson.annotations.SerializedName

data class ItemStockSpecification(
    @SerializedName("specification") var specification : String? = null,
    @SerializedName("stock") var stock : ArrayList<ItemStockGet> = arrayListOf(),
)

data class ItemStockGet(
    @SerializedName("id") var id : Int? = null,
    @SerializedName("facilityId") var facilityId : Int? = null,
    @SerializedName("sFacility") var sFacility : String? = null,
    @SerializedName("quantity") var quantity : Int? = null,
    @SerializedName("minWarnQty") var minWarnQty : Int? = null,
    var qty : Long = 0,
)