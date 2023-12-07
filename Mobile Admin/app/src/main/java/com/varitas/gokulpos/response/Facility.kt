package com.varitas.gokulpos.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Facility(
    @SerializedName("id") var id : Int? = null,
    @SerializedName("name") var name : String? = null,
    @SerializedName("contactName") var contactName : String? = null,
    @SerializedName("address") var address : String? = null,
    @SerializedName("city") var city : String? = null,
    @SerializedName("state") var state : String? = null,
    @SerializedName("country") var country : String? = null,
    @SerializedName("zip") var zip : String? = null,
    @SerializedName("contactNumber") var contactNumber : String? = null,
    @SerializedName("status") var status : Int? = null,
    @SerializedName("storeId") var storeId : Int? = null,
    @SerializedName("isDefault") var isDefault : Boolean? = null,
    @SerializedName("actionBy") var actionBy : Int? = null,
    var quantity : Int = 0,
) : Parcelable
