package com.varitas.gokulpos.request

data class BrandUpdateRequest(val allowOnWeb: Boolean? = null,  var actionBy : Int? = null, val itg: Boolean? = null, val logoId: Int? = null, val manufacture: String? = null, val name: String? = null, val pmusa: Boolean? = null, val rjrt: Boolean? = null, val id: Int? = null, val status: Int? = null)