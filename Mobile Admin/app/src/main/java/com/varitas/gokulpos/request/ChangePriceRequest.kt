package com.varitas.gokulpos.request

import com.google.gson.annotations.SerializedName
import com.varitas.gokulpos.utilities.Default

data class ChangePriceRequest(
    @SerializedName("id") var id: Int = 0,
    @SerializedName("status") var status: Int = Default.ACTIVE,
    @SerializedName("actionBy") var actionBy: Int = 0,
    @SerializedName("storeId") var storeId: Int = 0,
    @SerializedName("productId") var productId: Int,
    @SerializedName("attributeMapId") var attributeMapId: Int = 0,
    @SerializedName("unitCost") var unitCost: Double,
    @SerializedName("unitPrice") var unitPrice: Double,
    @SerializedName("minPrice") var minPrice: Double,
    @SerializedName("buyDown") var buyDown: Double,
    @SerializedName("msrp") var msrp: Double,
    @SerializedName("salesPrice") var salesPrice: Double,
    @SerializedName("quantity") var quantity: Int = 0,
) {
    constructor() : this(0, 0, 0, 0, 0, 0, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
}
