package com.varitas.gokulpos.response

import com.google.gson.annotations.SerializedName

data class CommonDropDown(
    @SerializedName("value") var value : Int? = null,
    @SerializedName("label") var label : String? = null
) {
    override fun toString() : String {
        return this.label!!
    }
}
