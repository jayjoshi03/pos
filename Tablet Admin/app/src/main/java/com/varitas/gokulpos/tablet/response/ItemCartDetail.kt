package com.varitas.gokulpos.tablet.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class ItemCartDetail(
        @SerializedName("id") var id: Int? = null,
        @SerializedName("itemGuid") var itemGuid: String? = null,
        @SerializedName("name") var name: String? = null,
        @SerializedName("sku") var sku: String? = null,
        @SerializedName("type") var type: Int? = null,
        @SerializedName("description") var description: String? = null,
        @SerializedName("departmentId") var departmentId: Int? = null,
        @SerializedName("sDepartment") var sDepartment: String? = null,
        @SerializedName("brandId") var brandId: Int? = null,
        @SerializedName("sBrand") var sBrand: String? = null,
        @SerializedName("categoryId") var categoryId: Int? = null,
        @SerializedName("sCategory") var sCategory: String? = null,
        @SerializedName("subCategoryId") var subCategoryId: Int? = null,
        @SerializedName("sSubCategory") var sSubCategory: String? = null,
        @SerializedName("taxGroupId") var taxGroupId: Int? = null,
        @SerializedName("sTaxGroup") var sTaxGroup: String? = null,
        @SerializedName("isAgeVerification") var isAgeVerification: Boolean? = null,
        @SerializedName("itemTax") var itemTax: String? = null,
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
        @SerializedName("upc") var upc: String? = null,
        @SerializedName("itemSpecification") var itemSpecification: ArrayList<PriceList> = arrayListOf(),
        @SerializedName("listattributes") var attributes: ArrayList<ItemAttributes> = arrayListOf(),
        @SerializedName("discounts") var discounts: ArrayList<Discounts> = arrayListOf(),
        @SerializedName("taxList") var taxList: ArrayList<TaxList> = arrayListOf(),
        var quantity: Int = 0, var price: Double = 0.00, var priceTotal: Double = 0.00, var isSelected: Boolean = false, var originalPrice: Double = 0.00, var taxRate: Double = 0.00,
        var tax: Double = 0.00, var taxTotal: Double = 0.00, var specificationId: Int = 0, var orderId: Int = 0, var returnOrderItemId: Int = 0, var discountId: Int = 0,
        var discountAmount: Double = 0.00, var appliedOnOrder: Boolean = false
                         )

@Parcelize
data class PriceList(
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

@Parcelize
data class TaxList(
        @SerializedName("id") var id: Int? = null,
        @SerializedName("className") var className: String? = null,
        @SerializedName("rateValue") var rateValue: Double? = null,
                  ) : Parcelable

@Parcelize
data class SoldAlongData(
        @SerializedName("soldAlongItemId") var soldAlongItemId: Int? = null,
        @SerializedName("specificationId") var specificationId: Int? = null,
        @SerializedName("soldAlongQty") var soldAlongQty: Int? = null,
                        ) : Parcelable

data class ItemAttributes(
        @SerializedName("attributeId") var attributeId: Int? = null,
        @SerializedName("attributeName") var attributeName: String? = null,
        @SerializedName("listAttributes") var listAttributes: ArrayList<AttributeDetails> = arrayListOf(),
                         )

data class AttributeDetails(
        @SerializedName("id") var id: Int? = null,
        @SerializedName("name") var name: String? = null,
        @SerializedName("groups") var groups: ArrayList<ItemGroup> = arrayListOf(),
        var isSelected: Boolean = false
                           )

data class ItemGroup(
        @SerializedName("id") var id: Int? = null,
        @SerializedName("specificationId") var specificationId: Int? = null
                    )

data class Discounts(
        @SerializedName("discountId") var discountId: Int? = null,
        @SerializedName("discount") var discount: String? = null,
        @SerializedName("discountAppliedOn") var discountAppliedOn: String? = null,
        @SerializedName("discountAmount") var discountAmount: Double? = null,
        @SerializedName("isBOGO") var isBOGO: Boolean? = null,
        @SerializedName("itemBuy") var itemBuy: Int? = null,
        @SerializedName("itemGet") var itemGet: Int? = null,
        @SerializedName("discountType") var discountType: Int? = null,
        @SerializedName("sDiscountType") var sDiscountType: String? = null,
        @SerializedName("startDate") var startDate: String? = null,
        @SerializedName("endDate") var endDate: String? = null,
        @SerializedName("startTime") var startTime: String? = null,
        @SerializedName("endTime") var endTime: String? = null,
        @SerializedName("remark") var remark: String? = null,
        @SerializedName("weekDays") var weekDays: String? = null,
        @SerializedName("isLimitedTime") var isLimitedTime: Boolean? = null,
        @SerializedName("mWeekDaysList") var mWeekDaysList: ArrayList<WeekDaysList> = arrayListOf()
                    )

data class WeekDaysList(
        @SerializedName("id") var id: Int? = null,
        @SerializedName("name") var name: String? = null
                       )