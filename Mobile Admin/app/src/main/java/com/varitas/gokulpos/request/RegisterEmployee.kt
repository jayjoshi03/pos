package com.varitas.gokulpos.request

import com.google.gson.annotations.SerializedName

data class RegisterEmployee(
    @SerializedName("id"       ) var id       : Int?    = null,
    @SerializedName("actionBy" ) var actionBy : Int?    = null,
    @SerializedName("status"   ) var status   : Int?    = null,
    @SerializedName("storeId"  ) var storeId  : Int?    = null,
    @SerializedName("name"     ) var name     : String? = null,
    @SerializedName("address"  ) var address  : String? = null,
    @SerializedName("city"     ) var city     : String? = null,
    @SerializedName("state"    ) var state    : String? = null,
    @SerializedName("country"  ) var country  : String? = null,
    @SerializedName("zipcode"  ) var zipcode  : String? = null,
    @SerializedName("email"    ) var email    : String? = null,
    @SerializedName("mobileNo" ) var mobileNo : String? = null,
    @SerializedName("userName" ) var userName : String? = null,
    @SerializedName("password" ) var password : String? = null,
    @SerializedName("cardNo"   ) var cardNo   : String? = null,
    @SerializedName("passPin"  ) var passPin  : String? = null,
    @SerializedName("roleId"   ) var roleId   : Int?    = null
)