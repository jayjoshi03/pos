package com.varitas.gokulpos.response

import com.google.gson.annotations.SerializedName

data class Category(
    @SerializedName("id") var id : Int? = null,
    @SerializedName("name") var name : String? = null,
    @SerializedName("sParentCategory") var sParentCategory : String? = null,
    @SerializedName("sDepartment") var sDepartment : String? = null,
    @SerializedName("itemCount") var itemCount : Int? = null
) {
    override fun toString() : String {
        return this.name!!
    }
}
