package com.varitas.gokulpos.tablet.response

import com.google.gson.annotations.SerializedName
import com.varitas.gokulpos.tablet.model.SelectedGroups

data class ItemGroupTax(
        @SerializedName("group") var group: SelectedGroups? = SelectedGroups(),
        @SerializedName("tax") var tax: ArrayList<TaxDetails> = arrayListOf()
                       )

data class TaxDetails(
        @SerializedName("id") var id: Int? = null,
        @SerializedName("taxName") var taxName: String? = null,
        @SerializedName("taxRate") var taxRate: Double? = null,
        @SerializedName("unitPrice") var unitPrice: Double? = null,
        @SerializedName("taxAmount") var taxAmount: Double? = null
                     )