package com.varitas.gokulpos.tablet.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class ScanBarcode(
        @SerializedName("id") var id: Int? = null,
        @SerializedName("name") var name: String? = null,
        @SerializedName("brandId") var brandId: Int? = null,
        @SerializedName("sBrand") var sBrand: String? = null,
        @SerializedName("departmentId") var departmentId: Int? = null,
        @SerializedName("sDepertment") var sDepartment: String? = null,
        @SerializedName("categoryId") var categoryId: Int? = null,
        @SerializedName("sCategory") var sCategory: String? = null,
        @SerializedName("upc") var upc: String? = null,
        @SerializedName("itemType") var itemType: Int? = null
                      ) : Parcelable