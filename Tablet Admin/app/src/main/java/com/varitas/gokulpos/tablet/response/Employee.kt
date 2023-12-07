package com.varitas.gokulpos.tablet.response

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Employee(
        @SerializedName("id") var id : Int? = null,
        @SerializedName("name") var name : String? = null,
        @SerializedName("address1") var address1 : String? = null,
        @SerializedName("city") var city : String? = null,
        @SerializedName("state") var state : String? = null,
        @SerializedName("country") var country : String? = null,
        @SerializedName("zip") var zip : String? = null,
        @SerializedName("gender") var gender : String? = null,
        @SerializedName("genderType") var genderType : String? = null,
        @SerializedName("mobileNo") var mobileNo : String? = null,
        @SerializedName("email") var email : String? = null,
        @SerializedName("roleId") var roleId : Int? = null,
        @SerializedName("roleName") var roleName : String? = null,
        @SerializedName("storeId") var storeId : Int? = null,
        @SerializedName("status") var status : Int? = null,
        @SerializedName("sstatus") var sstatus : String? = null,
        @SerializedName("actionBy") var actionBy : Int? = null,
        @SerializedName("userName") var userName : String? = null,
        @SerializedName("password") var password : String? = null,
        @SerializedName("pin") var pin : String? = null,
        @SerializedName("cardNo") var cardNo : String? = null,
):Serializable
