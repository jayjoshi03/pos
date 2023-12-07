package com.varitas.gokulpos.tablet.response

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Tax(
        var id: Int? = null,
        var className: String? = null,
        var rateValue: Double? = null,
        var status: Int? = null,
        var isSelected: Boolean = false,
        var bySize: Boolean = false
              ) : Parcelable