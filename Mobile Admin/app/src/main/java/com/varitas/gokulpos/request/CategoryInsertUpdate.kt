package com.varitas.gokulpos.request

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class CategoryInsertUpdate(
    //Insert
    @SerializedName("name") var name : String? = null,
    @SerializedName("parentCategory") var parentCategory : Int? = null,
    @SerializedName("departmentId") var departmentId : Int? = null,
    @SerializedName("pictureId") var pictureId : Int? = null,
    @SerializedName("allowOnWeb") var allowOnWeb : Boolean? = null,
    @SerializedName("actionBy") var actionBy : Int? = null,
    @SerializedName("storeId") var storeId : Int? = null,
    @SerializedName("isFoodStamp") var isFoodStamp : Boolean? = null,
    @SerializedName("allowInBrand") var allowInBrand : Boolean? = null,
    @SerializedName("brandId") var brandId : Int? = null,
    @SerializedName("isNonRevenue") var isNonRevenue : Boolean? = null,
    @SerializedName("nonDiscountable") var nonDiscountable : Boolean? = null,
    @SerializedName("nonStock") var nonStock : Boolean? = null,
    @SerializedName("nonCountable") var nonCountable : Boolean? = null,
    @SerializedName("weightItemFlag") var weightItemFlag : Boolean? = null,
    @SerializedName("allowWiccheck") var allowWicCheck : Boolean? = null,
    @SerializedName("webItemFlag") var webItemFlag : Boolean? = null,
    //get and update
    @SerializedName("id") var id : Int? = null,
    @SerializedName("sParentCategory") var sParentCategory : String? = null,
    @SerializedName("sDepartment") var sDepartment : String? = null,
    @SerializedName("status") var status : Int? = null,
    @SerializedName("sBrand") var sBrand : String? = null,
) : Parcelable