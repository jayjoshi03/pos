package com.varitas.gokulpos.utilities

import android.annotation.SuppressLint
import java.text.SimpleDateFormat

object Constants {
    @SuppressLint("SimpleDateFormat") val dateFormat_MMM_dd_yyyy: SimpleDateFormat = SimpleDateFormat("MMM dd, yyyy")
    @SuppressLint("SimpleDateFormat") val dateFormat_yyyy_MM_dd: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd")
    @SuppressLint("SimpleDateFormat") val dateFormatT: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
    @SuppressLint("SimpleDateFormat") val dateFormatTZ: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    @SuppressLint("SimpleDateFormat") val dateFormat_MM_dd_yyyy: SimpleDateFormat = SimpleDateFormat("MM-dd-yyyy")
    const val clickDelay = 400L
    const val decimalFormatTwoPrecision: String = "0.00"
    const val dateFormat_UPC = "yyyy-MM-dd'T'HH:mm:ss"
}