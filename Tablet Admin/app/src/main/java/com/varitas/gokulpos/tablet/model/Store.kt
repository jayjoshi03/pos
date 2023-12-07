package com.varitas.gokulpos.tablet.model

import com.google.gson.annotations.SerializedName

data class Store(
        @SerializedName("id") var id: Int? = null,
        @SerializedName("name") var name: String? = null,
        @SerializedName("guid") var guid: String? = null,
        @SerializedName("addressId") var addressId: Int? = null,
        @SerializedName("email") var email: String? = null,
        @SerializedName("type") var type: Int? = null,
        @SerializedName("mobileNumber") var mobileNumber: String? = null,
        @SerializedName("landlineNumber") var landlineNumber: String? = null,
        @SerializedName("businessName") var businessName: String? = null,
        @SerializedName("fnsnumber") var fnsnumber: String? = null,
        @SerializedName("federalNumber") var federalNumber: String? = null,
        @SerializedName("storeLogo") var storeLogo: Int? = null,
        @SerializedName("reportLogo") var reportLogo: Int? = null,
        @SerializedName("signatureLogo") var signatureLogo: Int? = null,
        @SerializedName("storeWaterMarkLogo") var storeWaterMarkLogo: Int? = null,
        @SerializedName("website") var website: String? = null,
        @SerializedName("domainDetails") var domainDetails: String? = null,
        @SerializedName("zoneId") var zoneId: Int? = null,
        @SerializedName("parentStoreId") var parentStoreId: Int? = null,
        @SerializedName("ownerId") var ownerId: Int? = null,
        @SerializedName("year") var year: String? = null,
        @SerializedName("status") var status: Int? = null,
        @SerializedName("createdBy") var createdBy: Int? = null,
        @SerializedName("createdOnUtc") var createdOnUtc: String? = null,
        @SerializedName("modifiedBy") var modifiedBy: String? = null,
        @SerializedName("modifiedOnUtc") var modifiedOnUtc: String? = null
                )

data class StoreUpdate(
        @SerializedName("storeId") var storeId: Int? = null,
        @SerializedName("statusId") var statusId: Int? = null,
        @SerializedName("actionBy") var actionBy: Int? = null,
        @SerializedName("remark") var remark: String? = null,
                      )
