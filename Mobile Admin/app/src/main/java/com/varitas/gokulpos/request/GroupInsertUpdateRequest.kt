package com.varitas.gokulpos.request

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.varitas.gokulpos.response.Group
import kotlinx.parcelize.Parcelize

@Parcelize
data class GroupInsertUpdateRequest(
    @SerializedName("id") var id : Int? = null,
    @SerializedName("name") var name : String? = null,
    @SerializedName("storeId") var storeId : Int? = null,
    @SerializedName("actionBy") var actionBy : Int? = null,
    @SerializedName("type") var type : Int? = null,
    @SerializedName("sType") var sType : String? = null,
    @SerializedName("isShortcut") var isShortcut : Boolean? = null,
    @SerializedName("code") var code : String? = null,
    @SerializedName("data") var data : ArrayList<Group> = arrayListOf(),
    @SerializedName("sortedData") var sortedData : SortedData? = SortedData(),
) : Parcelable

@Parcelize
data class SortedData(
    @SerializedName("selectedItems") var selectedItems : ArrayList<Group> = arrayListOf(),
    @SerializedName("nonSelectedItems") var nonSelectedItems : ArrayList<Group> = arrayListOf(),
) : Parcelable