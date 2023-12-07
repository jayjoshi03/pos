package com.varitas.gokulpos.tablet.request

import com.google.gson.annotations.SerializedName

data class ItemGroupRequest(
        @SerializedName("id") var id: Int? = null,
        @SerializedName("data") var data: ArrayList<GroupDetails> = arrayListOf()
                           )

data class GroupDetails(
        @SerializedName("id") var id: Int? = null,
        @SerializedName("specificationId") var specificationId: Int? = null
                       )