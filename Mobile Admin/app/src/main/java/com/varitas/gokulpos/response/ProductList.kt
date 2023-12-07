package com.varitas.gokulpos.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class ProductList(
    @SerializedName("id") var id : Int? = null,
    @SerializedName("sku") var sku : String? = null,
    @SerializedName("name") var name : String? = null,
    @SerializedName("size") var size : String? = null,
    @SerializedName("pack") var pack : String? = null,
    @SerializedName("quantity") var quantity : Int? = null,
    @SerializedName("unitPrice") var price : Double? = null,
    @SerializedName("specification") var specification : String? = null,
    @SerializedName("type") var type : Int? = null,
    @SerializedName("departmentName") var departmentName : String? = null,
    @SerializedName("sStatus") var sStatus : String? = null,
    @SerializedName("isShortcut") var isShortcut : Boolean? = null,
    @SerializedName("storeId") var storeId : Int? = null,
    @SerializedName("sType") var stype : String? = null,
) : Parcelable {
    fun productWithSKU() : String {
        return try {
            "$name ($sku)"
        } catch(ex : Exception) {
            ""
        }
    }
}