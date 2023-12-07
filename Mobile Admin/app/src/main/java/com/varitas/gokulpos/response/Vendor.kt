package com.varitas.gokulpos.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Vendor(
    val address : String? = null,
    val city : String? = null,
    val code : String? = null,
    val companyName : String? = null,
    val country : String? = null,
    val email : String? = null,
    val groupId : Int? = null,
    val id : Int? = null,
    var name : String? = null,
    val payTerm : Int? = null,
    val personName : String? = null,
    val phoneNo : String? = null,
    val state : String? = null,
    val taxGroupId : Int? = null,
    @SerializedName("salespersoncount") val salesPersonCount : Int? = null,
) : Parcelable