package com.varitas.gokulpos.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Brand(
    val id : Int? = null,
    var name : String? = null,
    val manufacture : String? = null,
    val itg : Boolean? = null,
    @SerializedName("pmusa") val pmUsa : Boolean? = null,
    @SerializedName("rjrt") val rjrt : Boolean? = null,
    @SerializedName("sPmusa") val sPmusa : String? = null,
    @SerializedName("itemCount") var itemCount : Int? = null,
    val sRjrt : String? = null,
) : Parcelable