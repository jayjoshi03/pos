package com.varitas.gokulpos.tablet.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Role(
        @SerializedName("id") var id: Int? = null,
        @SerializedName("name") var name: String? = null,
        @SerializedName("storeId") var storeId: Int? = null,
        @SerializedName("actionBy") var actionBy: Int? = null
               ) : Parcelable
