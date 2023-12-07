package com.varitas.gokulpos.response

import com.google.gson.annotations.SerializedName

data class DropDown(@SerializedName("id") var id: Int? = null, @SerializedName("value") var value: String? = null) {
    override fun toString(): String {
        return this.value!!
    }
}
