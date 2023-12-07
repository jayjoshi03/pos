package com.varitas.gokulpos.tablet.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class SalesPromotion(
        @SerializedName("id") var id: Int? = null,
        @SerializedName("name") var name: String? = null,
        @SerializedName("startDate") var startDate: String? = null,
        @SerializedName("endDate") var endDate: String? = null,
        @SerializedName("startTime") var startTime: String? = null,
        @SerializedName("endTime") var endTime: String? = null,
        @SerializedName("remarks") var remarks: String? = null,
        @SerializedName("weekDays") var weekDays: ArrayList<Int> = arrayListOf(),
        @SerializedName("sWeekdays") var sWeekdays: ArrayList<SWeekdays> = arrayListOf(),
        @SerializedName("storeId") var storeId: Int? = null,
        @SerializedName("status") var status: Int? = null,
        @SerializedName("couponCode") var couponCode: String? = null,
        @SerializedName("couponName") var couponName: String? = null,
        @SerializedName("isSerialized") var isSerialized: Boolean? = null,
        @SerializedName("startNum") var startNum: Int? = null,
        @SerializedName("endNum") var endNum: Int? = null,
        @SerializedName("createdBy") var createdBy: Int? = null,
        @SerializedName("createdDateTime") var createdDateTime: String? = null,
        @SerializedName("modifiedBy") var modifiedBy: Int? = null,
        @SerializedName("modifiedOnUtc") var modifiedOnUtc: String? = null,
        @SerializedName("discountMapList") var discountMapList: ArrayList<DiscountMapList> = arrayListOf(),
        @SerializedName("isLimitedTime") var isLimitedTime: Boolean? = null,
        @SerializedName("customerGroup") var customerGroup: Int? = null,
        @SerializedName("itemGroup") var itemGroup: Int? = null,
        @SerializedName("actionBy") var actionBy: Int? = null,
        @SerializedName("mWeekDaysList") var mWeekDaysList: ArrayList<DataDetails> = arrayListOf(),
                         )

data class DiscountMapList(
        @SerializedName("id") var id : Int? = null,
        @SerializedName("discountId") var discountId : Int? = null,
        @SerializedName("discountAppId") var discountAppId : Int? = null,
        @SerializedName("sDiscountAppliedOn") var sDiscountAppliedOn : String? = null,
        @SerializedName("detailId") var detailId : Int? = null,
        @SerializedName("quantity") var quantity: Long? = null,
        @SerializedName("discountAmount") var discountAmount: Double? = null,
        @SerializedName("itemBuy") var itemBuy: Int? = null,
        @SerializedName("itemGet") var itemGet: Int? = null,
        @SerializedName("isBogo") var isBogo: Boolean? = null,
        @SerializedName("discountType") var discountType: Int? = null,
        @SerializedName("specificationId") var specificationId: Int? = null,
        @SerializedName("isMinimunAmount") var isMinimumAmount: Boolean? = null,
        @SerializedName("minimumAmount") var minimumAmount: Double? = null,
        @SerializedName("requiredQuantity") var requiredQuantity: Int? = null,
        @SerializedName("priority") var priority: Int? = null,
        @SerializedName("sItemName") var sItemName: String? = null,
        @SerializedName("sDiscountType") var sDiscountType: String? = null,
        var discountName: String = "", var isSelected: Boolean = false,
                          )

@Parcelize
data class DataDetails(
        @SerializedName("id") var id: Int? = null,
        @SerializedName("name") var name: String? = null,
        var isSelected: Boolean = false, var isGroup: Boolean = false, var type: Int = 0
                      ) : Parcelable

data class SWeekdays(@SerializedName("name") var name: String? = null)

data class ValidatePromotion(
        @SerializedName("name") var name: String? = null,
        @SerializedName("startDate") var startDate: String? = null,
        @SerializedName("endDate") var endDate: String? = null,
        @SerializedName("startTime") var startTime: String? = null,
        @SerializedName("endTime") var endTime: String? = null,
        @SerializedName("remarks") var remarks: String? = null,
        @SerializedName("weekDays") var weekDays: ArrayList<String> = arrayListOf(),
        @SerializedName("mWeekDaysList") var mWeekDaysList: ArrayList<DataDetails> = arrayListOf(),
        @SerializedName("couponCode") var couponCode: String? = null,
        @SerializedName("storeId") var storeId: Int? = null,
        @SerializedName("actionBy") var actionBy: Int? = null,
        @SerializedName("discountMapList") var discountMapList: ArrayList<DiscountMapList> = arrayListOf()
                            )

data class AddItemToDiscount(
        @SerializedName("discountId") var discountId: Int? = null,
        @SerializedName("discountMapList") var discountMapList: ArrayList<ItemToDiscountMap> = arrayListOf()
                            )

data class ItemToDiscountMap(
        @SerializedName("discountAppId") var discountAppId: Int? = null,
        @SerializedName("detailId") var detailId: Int? = null,
        @SerializedName("quantity") var quantity: Int? = null,
        @SerializedName("discountAmount") var discountAmount: Double? = null,
        @SerializedName("itemBuy") var itemBuy: Int? = null,
        @SerializedName("itemGet") var itemGet: Int? = null,
        @SerializedName("isBogo") var isBogo: Boolean? = null,
        @SerializedName("discountType") var discountType: Int? = null,
        @SerializedName("specificationId") var specificationId: Int? = null,
        @SerializedName("isMinimunAmount") var isMinimumAmount: Boolean? = null,
        @SerializedName("minimumAmount") var minimumAmount: Double? = null,
        @SerializedName("requiredQuantity") var requiredQuantity: Int? = null,
        @SerializedName("priority") var priority: Int? = null
                            )
