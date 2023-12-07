package com.varitas.gokulpos.tablet.response

data class DynamicDropDown(
        var list: ArrayList<CommonDropDown> = ArrayList(),
        var id: Int = 0, var specificationId: Int = 0
                          )

