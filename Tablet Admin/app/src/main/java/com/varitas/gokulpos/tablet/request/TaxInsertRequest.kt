package com.varitas.gokulpos.tablet.request

data class TaxInsertRequest(
        val className: String? = null,
        val rateValue: Double? = null,
        val bySize: Boolean? = null,
        var storeId: Int? = null,
        var actionBy: Int? = null)