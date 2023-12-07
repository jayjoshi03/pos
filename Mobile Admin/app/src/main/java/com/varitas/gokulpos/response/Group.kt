package com.varitas.gokulpos.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Group(
    @SerializedName("id") var id : Int? = null,
    @SerializedName("specificationId") var specificationId : Int? = null,
    @SerializedName("name") var name : String? = null,
    @SerializedName("sType") var sType : String? = null,
    @SerializedName("isGroup") var isGroup : Boolean = false,
    var type : Int? = null,
) : Parcelable