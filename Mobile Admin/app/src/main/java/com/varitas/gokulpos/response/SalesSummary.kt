package com.varitas.gokulpos.response

import com.google.gson.annotations.SerializedName

data class SalesSummary(
    @SerializedName("avarageSales") val averageSales: Double? = null,
    @SerializedName("discounts") val discounts: Double? = null,
    @SerializedName("grossSales") val grossSales: Double? = null,
    @SerializedName("netSales") val netSales: Double? = null,
    @SerializedName("orderCount") val orderCount: Int? = null,
    @SerializedName("returns") val returnCount: Int? = null,
    @SerializedName("taxAmount") val taxAmount: Double? = null,
    @SerializedName("total") val total: Double? = null
)