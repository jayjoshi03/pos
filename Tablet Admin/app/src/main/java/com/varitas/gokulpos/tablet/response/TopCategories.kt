package com.varitas.gokulpos.tablet.response

import com.google.gson.annotations.SerializedName

data class TopCategories(
    @SerializedName("category") val category: String? = null,
    @SerializedName("count") val count: Int? = null,
    @SerializedName("departmentId") val departmentId: Int? = null,
    @SerializedName("gross") val gross: Double? = null,
    @SerializedName("topItem") val topItem: List<TopItems>? = null,
    var isExpanded : Boolean = false
)