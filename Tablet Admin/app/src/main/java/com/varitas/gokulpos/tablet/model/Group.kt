package com.varitas.gokulpos.tablet.model

import android.os.Parcelable
import android.text.TextUtils
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class GroupDetail(
        @SerializedName("id") var id: Int? = null,
        @SerializedName("name") var name: String? = null,
        @SerializedName("isShortcut") var isShortcut: Boolean? = null,
        @SerializedName("code") var code: String? = null,
        @SerializedName("type") var type: Int? = null,
        @SerializedName("sType") var sType: String? = null,
        @SerializedName("sortedData") var sortedData: SortedData? = SortedData()
                      ) : Parcelable

@Parcelize
data class SortedData(
        @SerializedName("selectedItems") var selectedItems: ArrayList<SelectedGroups> = arrayListOf(),
        @SerializedName("nonSelectedItems") var nonSelectedItems: ArrayList<SelectedGroups> = arrayListOf()
                     ) : Parcelable

@Parcelize
data class SelectedGroups(
        @SerializedName("id") var id: Int? = null,
        @SerializedName("specificationId") var specificationId: Int? = null,
        @SerializedName("name") var name: String? = null,
        @SerializedName("isGroup") var isGroup: Boolean? = null,
        @SerializedName("sType") var sType: String? = null,
        @SerializedName("itemCount") var itemCount: Int? = null,
        var type: Int? = null
                         ) : Parcelable

data class GroupInsertUpdateRequest(
        @SerializedName("id") var id: Int? = null,
        @SerializedName("name") var name: String? = null,
        @SerializedName("storeId") var storeId: Int? = null,
        @SerializedName("actionBy") var actionBy: Int? = null,
        @SerializedName("isShortcut") var isShortcut: Boolean? = null,
        @SerializedName("code") var code: String? = null,
        @SerializedName("type") var type: Int? = null,
        @SerializedName("data") var data: ArrayList<SelectedGroups> = arrayListOf()
                                   )

data class ItemGroupDetail(
        @SerializedName("id") var id: Int? = null,
        @SerializedName("name") var name: String? = null,
        @SerializedName("type") var type: Int? = null,
        @SerializedName("sType") var sType: String? = null,
        @SerializedName("itemCount") var itemCount: Int? = null
                          ) {
    override fun toString(): String {
        return if (!TextUtils.isEmpty(name)) name!! else ""
    }
}
