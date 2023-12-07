package com.varitas.gokulpos.request

import com.google.gson.annotations.SerializedName

data class ChangeQtyRequest(
    @SerializedName("id") var id: Int = 0,
    @SerializedName("type") var type: Int = 0,
    @SerializedName("stockStatus") var stockStatus: Int = 0,
    @SerializedName("productId") var productId: Int = 0,
    @SerializedName("storageId") var storageId: Int = 0,
    @SerializedName("attributeMapId") var attributeMapId: Int = 0,
    @SerializedName("actionBy") var actionBy: Int = 0,
    @SerializedName("actionDateTime") var actionDateTime: String = "",//2023-05-25T11:26:48.036Z
    @SerializedName("inStoreQty") var inStoreQty: Int = 0,
    @SerializedName("inWarehouseQty") var inWarehouseQty: Int = 0,
)
