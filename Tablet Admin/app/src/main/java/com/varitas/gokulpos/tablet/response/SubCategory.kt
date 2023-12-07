package com.varitas.gokulpos.tablet.response

import com.google.gson.annotations.SerializedName

data class SubCategory(
    val actionBy: Int? = null,
    val createdOnUtc: String? = null,
    val description: String? = null,
    val discount: Double? = null,
    val id: Int? = null,
    val imageURL: String? = null,
    val minAge: Int? = null,
    val modifiedOnUtc: String? = null,
    val name: String? = null,
    val parentCategoryId: Int? = null,
    val pictureId: Int? = null,
    val price: Double? = null,
    val sCreatedOnUtc: String? = null,
    val sModifiedOnUtc: String? = null,
    @SerializedName("showonHomepage") val showOnHomepage: Boolean? = null,
    val status: Int? = null,
    val statusCls: String? = null,
    val statusTitle: String? = null,
    val storeId: Int? = null,
    val tax: Double? = null,
) {
    override fun toString(): String {
        return this.name!!
    }
}