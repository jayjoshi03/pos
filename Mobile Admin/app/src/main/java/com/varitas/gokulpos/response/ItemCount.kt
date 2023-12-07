package com.varitas.gokulpos.response

import com.google.gson.annotations.SerializedName

data class ItemCount(
    @SerializedName("itemId"   ) var itemId   : Int?    = null,
    @SerializedName("itemName" ) var itemName : String? = null
)
