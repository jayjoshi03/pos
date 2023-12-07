package com.varitas.gokulpos.tablet.request

import com.google.gson.annotations.SerializedName

data class UPCRequest(
    @SerializedName("id") var id: Int? = null,
    @SerializedName("status") var status: Int? = null,
    @SerializedName("actionBy") var actionBy: Int? = null,
    @SerializedName("storeId") var storeId: Int? = null,
    @SerializedName("productId") var productId: Int? = null,
    @SerializedName("attributeMapId") var attributeMapId: Int? = null,
    @SerializedName("costPrice") var costPrice: Double? = null,
    @SerializedName("unitPrice") var unitPrice: Double? = null,
    @SerializedName("salesPrice") var salesPrice: Double? = null,
    @SerializedName("isManufactureBarcode") var isManufactureBarcode: Boolean? = null,
    @SerializedName("barcode") var barcode: String? = null
)