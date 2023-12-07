package com.varitas.gokulpos.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Invoice(
    @SerializedName("id") var id : Int? = null,
    @SerializedName("poid") var poid : Int? = null,
    @SerializedName("vendorId") var vendorId : Int? = null,
    @SerializedName("storeId") var storeId : Int? = null,
    @SerializedName("date") var date : String? = null,
    @SerializedName("dueDate") var dueDate : String? = null,
    @SerializedName("subTotal") var subTotal : Double? = null,
    @SerializedName("tax") var tax : Double? = null,
    @SerializedName("grandTotal") var grandTotal : Double? = null,
    @SerializedName("discountAmount") var discountAmount : Double? = null,
    @SerializedName("actionBy") var actionBy : Int? = null,
    @SerializedName("status") var status : Int? = null,
    @SerializedName("paymentStatus") var paymentStatus : Int? = null,
    @SerializedName("details") var details : ArrayList<DetailsInvoice> = arrayListOf(),
    @SerializedName("payment") var payment : ArrayList<PaymentInvoice> = arrayListOf(),
    @SerializedName("invoiceNo") var invoiceNo : String? = null,
    @SerializedName("sVendor") var sVendor : String? = null,
    @SerializedName("createdBy") var createdBy : Int? = null,
    @SerializedName("createdOnUtc") var createdOnUtc : String? = null,
    @SerializedName("sStatus") var sStatus : String? = null,
) : Parcelable

@Parcelize
data class DetailsInvoice(
    @SerializedName("podetailId") var poDetailId : Int? = null,
    @SerializedName("itemId") var itemId : Int? = null,
    @SerializedName("specificationId") var specificationId : Int? = null,
    @SerializedName("quantity") var quantity : Long? = null,
    @SerializedName("amount") var amount : Double? = null,
    @SerializedName("totalAmount") var totalAmount : Double? = null,
    @SerializedName("uom") var uom : String? = null,
    @SerializedName("itemName") var itemName : String? = null,
    @SerializedName("tax") var tax : Double? = null,
) : Parcelable

@Parcelize
data class PaymentInvoice(
    @SerializedName("invoiceId") var invoiceId : Int? = null,
    @SerializedName("tenderType") var tenderType : Int? = null,
    @SerializedName("paymentDate") var paymentDate : String? = null,
    @SerializedName("description") var description : String? = null,
    @SerializedName("amount") var amount : Double? = null,
    @SerializedName("status") var status : Int? = null,
    @SerializedName("actionBy") var actionBy : Int? = null,
    @SerializedName("sTenderType") var sTender : String? = null
) : Parcelable
