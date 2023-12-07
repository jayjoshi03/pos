package com.varitas.gokulpos.response

import com.google.gson.annotations.SerializedName

data class DropDownCategories(
    @SerializedName("categoryId") var categoryId: Int? = null,
    @SerializedName("departmentId") var departmentId: Int? = null,
    @SerializedName("categoryName") var categoryName: String? = null) {
    override fun toString(): String {
        return this.categoryName!!
    }
}
