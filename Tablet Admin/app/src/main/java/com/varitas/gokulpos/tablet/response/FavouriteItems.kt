package com.varitas.gokulpos.tablet.response

import com.google.gson.annotations.SerializedName

data class FavouriteItems(
        @SerializedName("id") var id: Int? = null,
        @SerializedName("name") var name: String? = null,
        @SerializedName("itemName") var itemName: String? = null,
        @SerializedName("specification") var specification: String? = null,
        @SerializedName("sku") var sku: String? = null,
        @SerializedName("isSize") var isSize: Boolean? = null,
        @SerializedName("specificationId") var specificationId: Int? = null,
        @SerializedName("isGroup") var isGroup: Boolean? = null
                         ) {
    override fun toString(): String {
        return name!! + if (specification != null) " (${specification!!})" else ""
    }
}

