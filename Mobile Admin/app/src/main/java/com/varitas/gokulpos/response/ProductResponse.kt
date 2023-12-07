package com.varitas.gokulpos.response

import com.google.gson.annotations.SerializedName
import com.varitas.gokulpos.request.ItemUPC
import com.varitas.gokulpos.request.QuantityDetail

data class ProductResponse(
    @SerializedName("id") var id: Int? = null,
    @SerializedName("itemGuid") var itemGuid: String? = null,
    @SerializedName("name") var name: String? = null,
    @SerializedName("sku") var sku: String? = null,
    @SerializedName("model") var model: String? = null,
    @SerializedName("type") var type: Int? = null,
    @SerializedName("itemType") var itemType: String? = null,
    @SerializedName("description") var description: String? = null,
    @SerializedName("departmentId") var departmentId: Int? = null,
    @SerializedName("sDepartment") var sDepartment: String? = null,
    @SerializedName("brandId") var brandId: Int? = null,
    @SerializedName("sBrand") var sBrand: String? = null,
    @SerializedName("categoryId") var categoryId: Int? = null,
    @SerializedName("sCategory") var sCategory: String? = null,
    @SerializedName("subCategory") var subCategory: Int? = null,
    @SerializedName("sSubCategory") var sSubCategory: String? = null,
    @SerializedName("vintage") var vintage: String? = null,
    @SerializedName("unitType") var unitType: Int? = null,
    @SerializedName("taxGroupId") var taxGroupId: Int? = null,
    @SerializedName("sTaxGroup") var sTaxGroup: String? = null,
    @SerializedName("status") var status: Int? = null,
    @SerializedName("storeId") var storeId: Int? = null,
    @SerializedName("isShortcut") var isShortcut: Boolean? = null,
    @SerializedName("buyCase") var buyCase: Boolean? = null,
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
    @SerializedName("itemdetails") var itemdetails: ArrayList<ItemData> = arrayListOf()
)

data class ItemData(
    @SerializedName("specification") var specification: ArrayList<SpecificationData> = arrayListOf(),
    @SerializedName("attribute") var attribute: ArrayList<AttributeDetail> = arrayListOf(),
    @SerializedName("price") var price: ArrayList<PriceDetail> = arrayListOf(),
    @SerializedName("stock") var stock: ArrayList<StockDetail> = arrayListOf(),
    @SerializedName("soldalong") var soldAlong: ArrayList<SoldAlongDetail> = arrayListOf(),
    @SerializedName("upc") var upcList: ArrayList<ItemUPC> = arrayListOf(),
    @SerializedName("qtydetail") var qtyDetail: QuantityDetail? = null,
)

data class PriceDetail(
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
    @SerializedName("quantity") var quantity: Long? = null
)

data class SpecificationData(
    @SerializedName("specificationId") var specificationId: Int? = null,
    @SerializedName("typeId") var typeId: Int? = null,
    @SerializedName("type") var type: String? = null,
    @SerializedName("id") var id: Int? = null,
    @SerializedName("value") var value: String? = null
)

data class StockDetail(
    @SerializedName("id") var id: Int? = null,
    @SerializedName("facilityId") var facilityId: Int? = null,
    @SerializedName("sFacility") var sFacility: String? = null,
    @SerializedName("quantity") var quantity: Long? = null
)

data class AttributeDetail(
    @SerializedName("id") var id: Int? = null,
    @SerializedName("itemId") var itemId: Int? = null,
    @SerializedName("itemName") var itemName: String? = null,
    @SerializedName("specificationId") var specificationId: Int? = null,
    @SerializedName("value") var value: Int? = null,
    @SerializedName("uom") var uom: String? = null,
    @SerializedName("itemStockQuantity") var itemStockQuantity: Int? = null
)

data class SoldAlongDetail(
    @SerializedName("soldalongId") var soldAlongId: Int? = null,
    @SerializedName("soldalongquantity") var soldAlongQuantity: Int? = null,
    @SerializedName("soldalongItemId") var soldAlongItemId: Int? = null,
    @SerializedName("soldalongItemName") var soldAlongItemName: String? = null,
    @SerializedName("specificationId") var specificationId: Int? = null,
)