package com.varitas.gokulpos.tablet.request

import com.google.gson.annotations.SerializedName

data class OrderInsertRequest(
        @SerializedName("customerId") var customerId: Int? = null,
        @SerializedName("storeId") var storeId: Int? = null,
        @SerializedName("paymentStatus") var paymentStatus: Int? = null,
        @SerializedName("status") var status: Int? = null,
        @SerializedName("tax") var tax: Double? = null,
        @SerializedName("totalIncTax") var totalIncTax: Double? = null,
        @SerializedName("discountAmount") var discountAmount: Double? = null,
        @SerializedName("discountId") var discountId: Int? = null,
        @SerializedName("couponCode") var couponCode: String? = null,
        @SerializedName("subTotal") var subTotal: Double? = null,
        @SerializedName("remark") var remark: String? = null,
        @SerializedName("extraCharges") var extraCharges: Double? = null,
        @SerializedName("createdBy") var createdBy: Int? = null,
        @SerializedName("isAgeVerificationRequired") var isAgeVerificationRequired: Boolean? = null,
        @SerializedName("orderDetailsApiModels") var orderDetailsApiModels: ArrayList<OrderDetailRequest> = arrayListOf(),
        @SerializedName("orderPaymentModel") var orderPaymentModel: ArrayList<OrderPaymentRequest> = arrayListOf(),
        @SerializedName("customerAgeModel") var customerAgeModel: CustomerAge? = CustomerAge()
                             )

data class UpdateOrderRequest(
        @SerializedName("id") var id: Int? = null,
        @SerializedName("customerId") var customerId: Int? = null,
        @SerializedName("storeId") var storeId: Int? = null,
        @SerializedName("paymentStatus") var paymentStatus: Int? = null,
        @SerializedName("status") var status: Int? = null,
        @SerializedName("subTotal") var subTotal: Double? = null,
        @SerializedName("tax") var tax: Double? = null,
        @SerializedName("totalIncTax") var totalIncTax: Double? = null,
        @SerializedName("discountAmount") var discountAmount: Double? = null,
        @SerializedName("discountId") var discountId: Int? = null,
        @SerializedName("extraCharges") var extraCharges: Double? = null,
        @SerializedName("createdBy") var createdBy: Int? = null,
        @SerializedName("isAgeVerificationRequired") var isAgeVerificationRequired: Boolean? = null,
        @SerializedName("orderDetailsApiModels") var orderDetailsApiModels: ArrayList<OrderDetailRequest> = arrayListOf(),
        @SerializedName("orderPaymentModel") var orderPaymentModel: ArrayList<OrderPaymentRequest> = arrayListOf(),
        @SerializedName("customerAgeModel") var customerAgeModel: CustomerAge? = CustomerAge()
                             )

data class OrderDetailRequest(
        @SerializedName("itemId") var itemId: Int? = null,
        @SerializedName("quantity") var quantity: Int? = null,
        @SerializedName("unitPrice") var unitPrice: Double? = null,
        @SerializedName("taxGroupId") var taxGroupId: Int? = null,
        @SerializedName("itemSpecification") var itemSpecificationId: Int? = null,//Need to provide an ID
        @SerializedName("discountAmount") var discountAmount: Double? = null,
        @SerializedName("discountId") var discountId: Int? = null,
        @SerializedName("totalPrice") var totalPrice: Double? = null,
        @SerializedName("taxAmount") var taxAmount: Double? = null,
        @SerializedName("returnOrderItemId") var returnOrderItemId: Int? = null
                             )

data class OrderPaymentRequest(
        @SerializedName("orderId") var orderId: Int? = null,
        @SerializedName("amount") var amount: Double? = null,
        @SerializedName("tenderType") var tenderType: Int? = null,
        @SerializedName("paymentStatus") var paymentStatus: Int? = null,
        @SerializedName("details") var details: String? = null,
        @SerializedName("actionBy") var actionBy: Int? = null,
        @SerializedName("storeId") var storeId: Int? = null,
        var tenderName : String = ""
                              )

data class CustomerAge(
        @SerializedName("orderId") var orderId: Int? = null,
        @SerializedName("customerName") var customerName: String? = null,
        @SerializedName("customerDateOfBirth") var customerDateOfBirth: String? = null,
        @SerializedName("customerLicense") var customerLicense: String? = null
                      )