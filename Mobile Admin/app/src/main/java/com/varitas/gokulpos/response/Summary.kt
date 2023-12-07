package com.varitas.gokulpos.response

import com.google.gson.annotations.SerializedName

data class Summary(@SerializedName("orderCount") var orderCount: Int? = null, @SerializedName("title") var title: String? = null) {

    fun titleName(): String {
        return title!!.substring(0, 1).uppercase() + title!!.substring(1).lowercase()
    }

}