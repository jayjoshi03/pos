package com.varitas.gokulpos.tablet.request

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class DepartmentInsertUpdate(
        @SerializedName("id") var id: Int? = null,
        @SerializedName("name") var name: String? = null,
        @SerializedName("code") var code: String? = null,
        @SerializedName("allowFoodStamp") var allowFoodStamp: Boolean? = null,
        @SerializedName("isTaxable") var isTaxable: Boolean? = null,
        @SerializedName("taxGroupId") var taxGroupId: Int? = null,
        @SerializedName("sGroupName") var sGroupName: String? = null,
        @SerializedName("showInOpenPrice") var showInOpenPrice: Boolean? = null,
        @SerializedName("priceRatioType") var priceRatioType: Int? = null,
        @SerializedName("priceRatioValue") var priceRatioValue: Double? = null,
        @SerializedName("ageVerification") var ageVerification: Boolean? = null,
        @SerializedName("pictureId") var pictureId: Int? = null,
        @SerializedName("status") var status: Int? = null,
        @SerializedName("departmentId") var departmentId: Int? = null,
        @SerializedName("allowInBrand") var allowInBrand: Boolean? = null,
        @SerializedName("brandId") var brandId: Int? = null,
        @SerializedName("isNonRevenue") var isNonRevenue: Boolean? = null,
        @SerializedName("nonDiscountable") var nonDiscountable: Boolean? = null,
        @SerializedName("nonStock") var nonStock: Boolean? = null,
        @SerializedName("nonCountable") var nonCountable: Boolean? = null,
        @SerializedName("weightItemFlag") var weightItemFlag: Boolean? = null,
        @SerializedName("allowWiccheck") var allowWicCheck: Boolean? = null,
        @SerializedName("webItemFlag") var webItemFlag: Boolean? = null,
        @SerializedName("isFoodStamp") var isFoodStamp: Boolean? = null,
        @SerializedName("sBrand") var sBrand: String? = null,
    //Insert
        @SerializedName("actionBy") var actionBy: Int? = null,
        @SerializedName("storeId") var storeId: Int? = null,
        @SerializedName("displayOnWeb") var displayOnWeb: Boolean? = null,
                                 ) : Parcelable