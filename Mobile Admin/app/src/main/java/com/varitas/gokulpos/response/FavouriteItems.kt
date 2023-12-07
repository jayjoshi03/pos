package com.varitas.gokulpos.response

import com.google.gson.annotations.SerializedName

data class FavouriteItems(
    @SerializedName("id") var id : Int? = null,
    @SerializedName("name") var name : String? = null,
    @SerializedName("itemName") var itemName : String? = null,
    @SerializedName("isSize") var isSize : Boolean? = null,
    @SerializedName("specificationId") var specificationId : Int? = null,
    @SerializedName("specification") var specification : String? = null,
) {
    override fun toString() : String {
        return itemName!! + " (${specification!!})"
    }
}

