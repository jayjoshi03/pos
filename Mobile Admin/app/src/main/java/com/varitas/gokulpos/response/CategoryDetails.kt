package com.varitas.gokulpos.response

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CategoryDetails(
    val id : Int? = null,
    val name : String? = null,
    val parentCategory : Int? = null,
    val departmentId : Int? = null,
    val status : Int? = null,
    val pictureId : Int? = null,
    val allowOnWeb : Boolean? = null,
) : Parcelable