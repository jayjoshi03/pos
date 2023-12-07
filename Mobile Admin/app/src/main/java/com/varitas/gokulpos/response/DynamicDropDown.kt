package com.varitas.gokulpos.response

data class DynamicDropDown(
    val list : ArrayList<CommonDropDown> = ArrayList(),
    var id : Int = 0, var specificationId : Int = 0, var isCheck: Int = 0
)
