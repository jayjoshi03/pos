package com.varitas.gokulpos.tablet.response

import com.google.gson.annotations.SerializedName

data class UPC(@SerializedName("productName") var productName: String? = null, @SerializedName("attributeType") var attributeType: String? = null, @SerializedName("attributeValue") var attributeValue: String? = null, @SerializedName("isManufactureBarcode") var isManufactureBarcode: Boolean? = null, @SerializedName("barcode") var barcode: String? = null, @SerializedName("costPrice") var costPrice: Double? = null, @SerializedName("unitPrice") var unitPrice: Double? = null, @SerializedName("salesPrice") var salesPrice: Double? = null, @SerializedName("id") var id: Int? = null, @SerializedName("status") var status: Int? = null, @SerializedName("statusTitle") var statusTitle: String? = null, @SerializedName("statusCls") var statusCls: String? = null, @SerializedName("createdOnUtc") var createdOnUtc: String? = null, @SerializedName("actionBy") var actionBy: String? = null, @SerializedName("modifiedOnUtc") var modifiedOnUtc: String? = null, @SerializedName("storeId") var storeId: Int? = null)