package com.varitas.gokulpos.tablet.response

import com.google.gson.annotations.SerializedName
import com.varitas.gokulpos.tablet.utilities.Enums

data class ItemCount(
        @SerializedName("itemId") var itemId: Int? = null,
        @SerializedName("itemName") var itemName: String? = null
                    )

data class FavouriteMenu(
        var menuName: String,
        var type: Enums.Menus, var isSelected: Boolean
                        )
