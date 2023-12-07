package com.varitas.gokulpos.model

import com.google.gson.annotations.SerializedName

data class Menus(
    @SerializedName("id") var id : Int? = null,
    @SerializedName("name") var name : String? = null,
    @SerializedName("url") var url : String? = null,
    @SerializedName("base64Content") var base64Content : String? = null,
    @SerializedName("fullAccess") var fullAccess : Boolean? = null,
    @SerializedName("viewAccess") var viewAccess : Boolean? = null,
    @SerializedName("editAccess") var editAccess : Boolean? = null,
    @SerializedName("deleteAccess") var deleteAccess : Boolean? = null,
    @SerializedName("addAccess") var addAccess : Boolean? = null,
    @SerializedName("storeId") var storeId : Int? = null,
    @SerializedName("isMenu") var isMenu : Boolean? = null,
    @SerializedName("align") var align : String? = null,
    @SerializedName("displayOrder") var displayOrder : Int? = null
)