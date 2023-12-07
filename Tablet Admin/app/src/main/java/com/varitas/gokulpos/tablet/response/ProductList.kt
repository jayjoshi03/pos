package com.varitas.gokulpos.tablet.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class ProductList(
        @SerializedName("id") var id: Int? = null,
        @SerializedName("name") var name: String? = null,
        @SerializedName("sku") var sku: String? = null,
        @SerializedName("specification") var specification: String? = null,
        @SerializedName("unitPrice") var unitPrice: Double? = null,
        @SerializedName("type") var type: Int? = null,
        @SerializedName("quantity") var quantity: Int? = null,
        @SerializedName("departmentName") var departmentName: String? = null,
        @SerializedName("sStatus") var sStatus: String? = null,
        @SerializedName("isShortcut") var isShortcut: Boolean? = null,
        @SerializedName("storeId") var storeId: Int? = null,
        @SerializedName("sType") var stype: String? = null,
        var isSelected: Boolean = false

                      ) : Parcelable {
                          constructor() : this(0, "", "", "", 0.00, 0, 0, "", "", false, 0,  "")
                      }