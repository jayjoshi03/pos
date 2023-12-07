package com.varitas.gokulpos.tablet.request

data class TaxUpdateRequest(
        val className: String? = null,
        val rateValue: Double? = null,
        val bySize: Boolean? = null,
        var id: Int? = null,
        var actionBy: Int? = null)