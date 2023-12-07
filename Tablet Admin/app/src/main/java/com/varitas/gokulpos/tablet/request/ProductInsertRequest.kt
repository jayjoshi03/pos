package com.varitas.gokulpos.tablet.request

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class ProductInsertRequest(
        @SerializedName("id") var id: Int? = null,
        @SerializedName("itemGuid") var itemGuid: String? = null,
        @SerializedName("name") var name: String? = null,
        @SerializedName("sku") var sku: String? = null,
        @SerializedName("model") var model: String? = null,
        @SerializedName("type") var type: Int? = null,
        @SerializedName("description") var description: String? = null,
        @SerializedName("departmentId") var departmentId: Int? = null,
        @SerializedName("brandId") var brandId: Int? = null,
        @SerializedName("categoryId") var categoryId: Int? = null,
        @SerializedName("vintage") var vintage: String? = null,
        @SerializedName("unitType") var unitType: Int? = null,
        @SerializedName("taxGroupId") var taxGroupId: Int? = null,
        @SerializedName("storeId") var storeId: Int? = null,
        @SerializedName("isShortcut") var isShortcut: Boolean? = null,
        @SerializedName("buyCase") var buyCase: Boolean? = null,
        @SerializedName("actionBy") var actionBy: Int? = null,
        @SerializedName("nonStockItem") var nonStockItem: Boolean? = null,
        @SerializedName("promptForPrice") var promptForPrice: Boolean? = null,
        @SerializedName("promptForQuantity") var promptForQuantity: Boolean? = null,
        @SerializedName("nonDiscountable") var nonDiscountable: Boolean? = null,
        @SerializedName("countWithNoDisc") var countWithNoDisc: Boolean? = null,
        @SerializedName("returnItem") var returnItem: Boolean? = null,
        @SerializedName("acceptFoodStamp") var acceptFoodStamp: Boolean? = null,
        @SerializedName("depositItem") var depositItem: Boolean? = null,
        @SerializedName("acceptWiccheck") var acceptWiccheck: Boolean? = null,
        @SerializedName("nonRevenue") var nonRevenue: Boolean? = null,
        @SerializedName("webItem") var webItem: Boolean? = null,
        @SerializedName("nonCountable") var nonCountable: Boolean? = null,
        @SerializedName("weightedItem") var weightedItem: Boolean? = null,
        @SerializedName("nonPluItem") var nonPluItem: Boolean? = null,
        @SerializedName("status") var status: Int? = null,
        @SerializedName("itemdetails") var itemdetails: ArrayList<ItemDetails> = arrayListOf()
                               ) : Parcelable

@Parcelize
data class ItemDetails(
        @SerializedName("flag") var flag: Int? = null,
        @SerializedName("specification") var specification: ArrayList<SpecificationDetails> = arrayListOf(),
        @SerializedName("attributes") var attributes: ArrayList<Attributes> = arrayListOf(),
        @SerializedName("price") var price: ArrayList<ItemPrice> = arrayListOf(),
        @SerializedName("stock") var stock: ArrayList<ItemStock> = arrayListOf(),
        @SerializedName("soldalong") var soldAlong: ArrayList<SoldAlong> = arrayListOf(),
        @SerializedName("upc") var upcList: ArrayList<ItemUPC> = arrayListOf(),
        @SerializedName("qtydetail") var qtyDetail: QuantityDetail? = null,
                      ) : Parcelable

@Parcelize
data class ItemUPC(
        @SerializedName("itemupc") var itemUpc: String? = null,
        var isAuto: Boolean = false
                  ) : Parcelable

@Parcelize
data class SpecificationDetails(
        @SerializedName("specificationId") var specificationId: Int? = null,
        @SerializedName("id") var id: Int? = null,
        @SerializedName("value") var value: String? = null
                               ) : Parcelable

@Parcelize
data class Attributes(
        @SerializedName("id") var id: Int? = null,
        @SerializedName("itemId") var itemId: Int? = null,
        @SerializedName("value") var value: Int? = null,
        @SerializedName("uom") var uom: String? = null,
        @SerializedName("specificationId") var specificationId: Int? = null,
        @SerializedName("itemStockQuantity") var itemStockQuantity: Int? = null, var itemName : String = ""
                     ) : Parcelable

data class ManageSpecification(
        var id: Int? = null,
        var value: String? = null,
        var typeId: Int? = null, //specification Id i.e. Size, Pack etc
        var typeName: String? = null,
        var specificationId: Int?
                              ) {
    constructor() : this(0, "", 0, "", 0)
}

data class ItemPriceList(
        @SerializedName("price") var price: ArrayList<ItemPrice> = arrayListOf()

                        )

@Parcelize
data class ItemPrice(
        @SerializedName("id") var id: Int? = null,
        @SerializedName("specification") var specification: String? = null,
        @SerializedName("unitCost") var unitCost: Double? = 0.00,
        @SerializedName("unitPrice") var unitPrice: Double? = 0.00,
        @SerializedName("minPrice") var minPrice: Double? = 0.00,
        @SerializedName("buyDown") var buyDown: Double? = 0.00,
        @SerializedName("msrp") var msrp: Double? = 0.00,
        @SerializedName("salesPrice") var salesPrice: Double? = 0.00,
        @SerializedName("margin") var margin: Double? = 0.00,
        @SerializedName("markup") var markup: Double? = 0.00,
        @SerializedName("quantity") var quantity: Long? = 0,
                    ) : Parcelable

@Parcelize
data class ItemStock(
        @SerializedName("facilityId") var facilityId: Int? = null,
        @SerializedName("id") var id: Int? = null,
        @SerializedName("quantity") var quantity: Long? = null
                    ) : Parcelable

@Parcelize
data class SoldAlong(
        @SerializedName("soldalongId") var soldalongId: Int? = null,
        @SerializedName("soldalongquantity") var soldalongquantity: Int? = null,
        @SerializedName("soldalongItemId") var soldAlongItemId: Int? = null,
        @SerializedName("specificationId") var specificationId: Int? = null,
        var name: String = ""
                    ) : Parcelable

@Parcelize
data class QuantityDetail(
        @SerializedName("minwarnqty") var minWarnQty: Int? = null,
        @SerializedName("reorder") var reOrder: Int? = null,
        @SerializedName("id") var id: Int? = null,
                         ) : Parcelable