package com.varitas.gokulpos.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Tax(
    var id : Int? = null,
    var className : String? = null,
    var rateValue : Double? = null,
    var status : Int? = null,
    var isSelected : Boolean = false,
    @SerializedName("bySize") var bySize : Boolean? = null,
) : Parcelable