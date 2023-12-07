package com.varitas.gokulpos.tablet.response

import com.google.gson.annotations.SerializedName

data class PurchaseOrder(
    @SerializedName("id") var id : Int? = null,
    @SerializedName("storeId") var storeId : Int? = null,
    @SerializedName("purchaseOrderNo") var purchaseOrderNo : String? = null,
    @SerializedName("vendorId") var vendorId : Int? = null,
    @SerializedName("sVendor") var sVendor : String? = null,
    @SerializedName("date") var date : String? = null,
    @SerializedName("expectedDeliveryDate") var expectedDeliveryDate : String? = null,
    @SerializedName("addressId") var addressId : Int? = null,
    @SerializedName("shipto") var shipto : String? = null,
    @SerializedName("subTotal") var subTotal : Double? = null,
    @SerializedName("tax") var tax : Double? = null,
    @SerializedName("grandTotal") var grandTotal : Double? = null,
    @SerializedName("note") var note : String? = null,
    @SerializedName("status") var status : Int? = null,
    @SerializedName("createdBy") var createdBy : Int? = null,
    @SerializedName("createdDateTime") var createdDateTime : String? = null,
    @SerializedName("modifiedBy") var modifiedBy : String? = null,
    @SerializedName("modifiedDateTime") var modifiedDateTime : String? = null,
    @SerializedName("details") var details : ArrayList<PurchaseOrderDetail> = arrayListOf(),
    @SerializedName("actionBy") var actionBy : Int? = null,
    var isMake : Boolean = false
){
    override fun toString() : String {
        return purchaseOrderNo!!
    }
}

data class PurchaseOrderDetail(
    @SerializedName("id") var id : Int? = null,
    @SerializedName("poid") var poid : Int? = null,
    @SerializedName("itemId") var itemId : Int? = null,
    @SerializedName("quantity") var quantity : Long? = null,
    @SerializedName("uom") var uom : String? = null,
    @SerializedName("lastCost") var lastCost : Double? = null,
    @SerializedName("total") var total : Double? = null,
    @SerializedName("tax") var tax : Double? = null,
    @SerializedName("status") var status : Int? = null,
    @SerializedName("specificationId") var specificationId : Int? = null,
    @SerializedName("receivedQuantity") var receivedQuantity : Int? = null,
    @SerializedName("sItemName") var sItemName : String? = null,
    @SerializedName("podetailId") var poDetailId : Int? = null,
    @SerializedName("itemName") var itemName : String? = null,
    @SerializedName("amount") var amount : Double? = null,
    @SerializedName("totalAmount") var totalAmount : Double? = null
)