package com.varitas.gokulpos.tablet.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class OrderList(
        @SerializedName("id") var id: Int? = null,
        @SerializedName("orderGuid") var orderGuid: String? = null,
        @SerializedName("customerId") var customerId: Int? = null,
        @SerializedName("customerName") var customerName: String? = null,
        @SerializedName("orderNo") var orderNo: String? = null,
        @SerializedName("storeId") var storeId: Int? = null,
        @SerializedName("paymentStatus") var paymentStatus: Int? = null,
        @SerializedName("status") var status: Int? = null,
        @SerializedName("subTotal") var subTotal: Double? = null,
        @SerializedName("tax") var tax: Double? = null,
        @SerializedName("totalIncTax") var totalIncTax: Double? = null,
        @SerializedName("discountAmount") var discountAmount: Double? = null,
        @SerializedName("extraCharges") var extraCharges: Double? = null,
        @SerializedName("isAgeVerificationRequired") var isAgeVerificationRequired: Boolean? = null,
        @SerializedName("createdBy") var createdBy: Int? = null,
        @SerializedName("createdDateTime") var createdDateTime: String? = null,
        @SerializedName("date") var date: String? = null,
        @SerializedName("remark") var remark: String? = null,
        @SerializedName("orderDetailsApiModels") var orderDetailsApiModels: ArrayList<OrderItemDetails> = arrayListOf()
                    ) : Parcelable


@Parcelize
data class CompletedOrder(
        @SerializedName("id") var id: Int? = null,
        @SerializedName("orderGuid") var orderGuid: String? = null,
        @SerializedName("customerId") var customerId: Int? = null,
        @SerializedName("customerName") var customerName: String? = null,
        @SerializedName("orderNo") var orderNo: String? = null,
        @SerializedName("storeId") var storeId: Int? = null,
        @SerializedName("paymentStatus") var paymentStatus: Int? = null,
        @SerializedName("sPaymentStatus") var sPaymentStatus: String? = null,
        @SerializedName("status") var status: Int? = null,
        @SerializedName("sStatus") var sStatus: String? = null,
        @SerializedName("subTotal") var subTotal: Double? = null,
        @SerializedName("tax") var tax: Double? = null,
        @SerializedName("totalIncTax") var totalIncTax: Double? = null,
        @SerializedName("discountAmount") var discountAmount: Double? = null,
        @SerializedName("extraCharges") var extraCharges: Double? = null,
        @SerializedName("isAgeVerificationRequired") var isAgeVerificationRequired: Boolean? = null,
        @SerializedName("createdBy") var createdBy: Int? = null,
        @SerializedName("createdDateTime") var createdDateTime: String? = null,
        @SerializedName("date") var date: String? = null,
        @SerializedName("remark") var remark: String? = null,
        @SerializedName("orderDetailsApiModels") var orderDetailsApiModels: ArrayList<OrderItemDetails> = arrayListOf(),
        @SerializedName("orderPaymentApiModel") var orderPaymentApiModel: ArrayList<CompletedOrderPayment> = arrayListOf()
                         ) : Parcelable

@Parcelize
data class CompletedOrderPayment(
        @SerializedName("id") var id: Int? = null,
        @SerializedName("orderId") var orderId: Int? = null,
        @SerializedName("amount") var amount: Double? = null,
        @SerializedName("tenderType") var tenderType: Int? = null,
        @SerializedName("paymentStatus") var paymentStatus: Int? = null,
        @SerializedName("details") var details: String? = null,
        @SerializedName("tenderName") var tenderName: String? = null
                                ) : Parcelable

data class AllOrders(
        @SerializedName("id") var id: Int? = null,
        @SerializedName("createdDateTime") var createdDateTime: String? = null,
        @SerializedName("orderNo") var orderNo: String? = null,
        @SerializedName("subTotal") var subTotal: Double? = null,
        @SerializedName("tax") var tax: Double? = null,
        @SerializedName("totalIncTax") var totalIncTax: Double? = null,
        @SerializedName("discountAmount") var discountAmount: Double? = null,
        @SerializedName("extraCharges") var extraCharges: Double? = null,
        @SerializedName("sCreatedBy") var sCreatedBy: String? = null,
        @SerializedName("createdBy") var createdBy: Int? = null,
        @SerializedName("sStatus") var sStatus: String? = null,
        @SerializedName("sPaymentStatus") var sPaymentStatus: String? = null,
        @SerializedName("storeId") var storeId: Int? = null,
        @SerializedName("orderItemCount") var orderItemCount: Int? = null,
        @SerializedName("status") var status: Int? = null,
        @SerializedName("paymentStatus") var paymentStatus: Int? = null
                    )