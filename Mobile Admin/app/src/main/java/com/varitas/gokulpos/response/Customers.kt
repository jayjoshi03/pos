package com.varitas.gokulpos.response

import com.google.gson.annotations.SerializedName

data class Customers(
    @SerializedName("id"             ) var id             : Int?    = null,
    @SerializedName("name"           ) var name           : String? = null,
    @SerializedName("customerGuid"   ) var customerGuid   : String? = null,
    @SerializedName("companyName"    ) var companyName    : String? = null,
    @SerializedName("drivingLicense" ) var drivingLicense : String? = null,
    @SerializedName("address"        ) var address        : String? = null,
    @SerializedName("city"           ) var city           : String? = null,
    @SerializedName("state"          ) var state          : String? = null,
    @SerializedName("zip"            ) var zip            : String? = null,
    @SerializedName("country"        ) var country        : String? = null,
    @SerializedName("birthDate"      ) var birthDate      : String? = null,
    @SerializedName("phonenNo"       ) var mobileNo       : String? = null,
    @SerializedName("emailId"        ) var emailId        : String? = null,
    @SerializedName("groupId"        ) var groupId        : Int?    = null,
    @SerializedName("sGroup"         ) var sGroup         : String? = null,
    @SerializedName("storeId"        ) var storeId        : Int?    = null,
    @SerializedName("status"         ) var status         : Int?    = null,
    @SerializedName("createdBy"      ) var createdBy      : Int?    = null,
    @SerializedName("createdOnUtc"   ) var createdOnUtc   : String? = null,
    @SerializedName("modifiedBy"     ) var modifiedBy     : String? = null,
    @SerializedName("modifiedOnUtc"  ) var modifiedOnUtc  : String? = null,
    //Insert/Update Add
    @SerializedName("actionBy"       ) var actionBy       : Int?    = null
)