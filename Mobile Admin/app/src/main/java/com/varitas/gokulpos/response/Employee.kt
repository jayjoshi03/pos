package com.varitas.gokulpos.response

import com.google.gson.annotations.SerializedName

data class Employee(
@SerializedName("id"       ) var id           : Int?    = null,
@SerializedName("name"     ) var name         : String? = null,
@SerializedName("userName" ) var userName     : String? = null,
@SerializedName("mobileNo" ) var mobileNo     : String? = null,
@SerializedName("roleId"   ) var roleId       : Int?    = null,
@SerializedName("roleName" ) var roleName     : String? = null,
@SerializedName("email"    ) var email        : String? = null,
@SerializedName("address1" ) var address1     : String? = null,
@SerializedName("city"     ) var city         : String? = null,
@SerializedName("state"    ) var state        : String? = null,
@SerializedName("country"  ) var country      : String? = null,
@SerializedName("zip"      ) var zip          : String? = null,
@SerializedName("storeId"  ) var storeId      : Int?    = null,
@SerializedName("status"   ) var status       : Int?    = null,
@SerializedName("actionBy" ) var actionBy     : Int?    = null,
@SerializedName("password" ) var password     : String? = null,
@SerializedName("pin"      ) var pin          : String? = null,
@SerializedName("cardNo"   ) var cardNo       : String? = null
)
