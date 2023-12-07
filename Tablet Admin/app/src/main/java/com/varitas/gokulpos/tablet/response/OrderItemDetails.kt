package com.varitas.gokulpos.tablet.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class OrderItemDetails(
        @SerializedName("id") var id: Int? = null,
        @SerializedName("orderId") var orderId: Int? = null,
        @SerializedName("orderIguid") var orderIguid: String? = null,
        @SerializedName("itemId") var itemId: Int? = null,
        @SerializedName("quantity") var quantity: Int? = null,
        @SerializedName("ordreItemGuid") var ordreItemGuid: String? = null,
        @SerializedName("unitPrice") var unitPrice: Double? = null,
        @SerializedName("totalPrice") var totalPrice: Double? = null,
        @SerializedName("taxGroupId") var taxGroupId: Int? = null,
        @SerializedName("taxAmount") var taxAmount: Double? = null,
        @SerializedName("taxList") var taxList: ArrayList<TaxList> = arrayListOf(),
        @SerializedName("itemSpecification") var itemSpecification: Int? = null,
        @SerializedName("discountAmount") var discountAmount: Int? = null,
        @SerializedName("itemName") var itemName: String? = null,
        @SerializedName("isTax") var isTax: Boolean? = null,
        @SerializedName("specification") var specification: OrderItemSpecification? = null,
        @SerializedName("sku") var sku: String? = null,
        @SerializedName("acceptFoodStamp") var acceptFoodStamp: Boolean? = null,
        var isSelected: Boolean = false
                           ) : Parcelable

@Parcelize
data class OrderItemSpecification(
        @SerializedName("id") var id: Int? = null,
        @SerializedName("specification") var specification: String? = null,
        @SerializedName("unitCost") var unitCost: Double? = null,
        @SerializedName("unitPrice") var unitPrice: Double? = null,
        @SerializedName("minPrice") var minPrice: Double? = null,
        @SerializedName("buyDown") var buyDown: Double? = null,
        @SerializedName("msrp") var msrp: Double? = null,
        @SerializedName("salesPrice") var salesPrice: Double? = null,
        @SerializedName("margin") var margin: Double? = null,
        @SerializedName("markup") var markup: Double? = null,
        @SerializedName("quantity") var quantity: Int? = null,
        @SerializedName("soldalong") var soldAlong: ArrayList<SoldAlongData> = arrayListOf(),
        @SerializedName("tax") var tax: Double? = null
                                 ) : Parcelable
