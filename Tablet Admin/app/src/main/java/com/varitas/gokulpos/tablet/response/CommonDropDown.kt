package com.varitas.gokulpos.tablet.response

import android.text.TextUtils
import com.google.gson.annotations.SerializedName

data class CommonDropDown(
        @SerializedName("value") var value: Int? = null,
        @SerializedName("label") var label: String? = null,
        var isSelected: Boolean = false
                         ) {
    override fun toString() : String {
        return if(!TextUtils.isEmpty(label)) label!! else ""
    }
}
