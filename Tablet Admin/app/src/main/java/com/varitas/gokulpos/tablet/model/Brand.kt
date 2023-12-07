package com.varitas.gokulpos.tablet.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Brand(
        val id: Int? = null,
        var name: String? = null,
        val manufacture: String? = null,
        val itg: Boolean? = null,
        @SerializedName("pmusa") val pmUsa: Boolean? = null,
        @SerializedName("rjrt") val rjrt: Boolean? = null,
        @SerializedName("sPmusa") val sPmusa: String? = null,
        @SerializedName("itemCount") var itemCount: Int? = null,
        val sRjrt: String? = null
                ) : Parcelable

data class BrandDetails(
        @SerializedName("allowOnWeb") var allowOnWeb: Boolean? = null,
        @SerializedName("actionBy") var actionBy: Int? = null,
        @SerializedName("itg") var itg: Boolean? = null,
        @SerializedName("logoId") var logoId: Int? = null,
        @SerializedName("manufacture") var manufacture: String? = null,
        @SerializedName("name") var name: String? = null,
        @SerializedName("pmusa") var pmusa: Boolean? = null,
        @SerializedName("rjrt") var rjrt: Boolean? = null,
        @SerializedName("id") var id: Int? = null,
        @SerializedName("status") var status: Int? = null,
        @SerializedName("storeId") var storeId: Int? = null,
                       )
