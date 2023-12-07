package com.varitas.gokulpos.tablet.response

import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.RawValue

data class BaseResponse<T>(@SerializedName("status") var status: Int? = null,@SerializedName("isSuccess") var isSuccess: Boolean? = null, @SerializedName("errorMessage") var message: String? = null, @SerializedName("data") var data: @RawValue T? = null)