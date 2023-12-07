package com.varitas.gokulpos.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class UOM(
    @SerializedName("id") var id : Int? = null,
    @SerializedName("uom") var uom : String? = null,
) : Parcelable
