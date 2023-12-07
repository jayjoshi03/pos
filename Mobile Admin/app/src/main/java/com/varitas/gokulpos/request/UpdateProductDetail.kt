package com.varitas.gokulpos.request

import com.google.gson.annotations.SerializedName

data class UpdateProductDetail(
    @SerializedName("id"                      ) var id                      : Int?                               = null,
    @SerializedName("status"                  ) var status                  : Int?                               = null,
    @SerializedName("actionBy"                ) var actionBy                : Int?                               = null,
    @SerializedName("storeId"                 ) var storeId                 : Int?                               = null,
    @SerializedName("name"                    ) var name                    : String?                            = null,
    @SerializedName("sku"                     ) var sku                     : String?                            = null,
    @SerializedName("manufacrturerPartNo"     ) var manufacrturerPartNo     : String?                            = null,
    @SerializedName("productTypeId"           ) var productTypeId           : Int?                               = null,
    @SerializedName("shortDescription"        ) var shortDescription        : String?                            = null,
    @SerializedName("fullDescription"         ) var fullDescription         : String?                            = null,
    @SerializedName("vendorId"                ) var vendorId                : Int?                               = null,
    @SerializedName("showOnHomePage"          ) var showOnHomePage          : Boolean?                           = null,
    @SerializedName("warehouseId"             ) var warehouseId             : Int?                               = null,
    @SerializedName("modelNo"                 ) var modelNo                 : String?                            = null,
    @SerializedName("unitType"                ) var unitType                : Int?                               = null,
    @SerializedName("serialNo"                ) var serialNo                : String?                            = null,
    @SerializedName("productCode"             ) var productCode             : Int?                               = null,
    @SerializedName("isPublish"               ) var isPublish               : Boolean?                           = null,
    @SerializedName("hsncode"                 ) var hsncode                 : String?                            = null,
    @SerializedName("uom"                     ) var uom                     : String?                            = null,
    @SerializedName("categoryId"              ) var categoryId              : Int?                               = null,
    @SerializedName("manufactureId"           ) var manufactureId           : Int?                               = null,
    @SerializedName("departmentId"            ) var departmentId            : Int?                               = null,
    @SerializedName("isCountyTax"             ) var isCountyTax             : Boolean?                           = null,
    @SerializedName("promptForPrice"          ) var promptForPrice          : Boolean?                           = null,
    @SerializedName("promptForQuantity"       ) var promptForQuantity       : Boolean?                           = null,
    @SerializedName("nonPluitem"              ) var nonPluitem              : Boolean?                           = null,
    @SerializedName("sizeId"                  ) var sizeId                  : Int?                               = null,
    @SerializedName("packId"                  ) var packId                  : Int?                               = null,
    @SerializedName("addProductTaxMaps"       ) var addProductTaxMaps       : ArrayList<ProductTax>              = arrayListOf(),
    @SerializedName("addPriceMapModel"        ) var addPriceMapModel        : ChangePriceRequest?                = ChangePriceRequest(),
    @SerializedName("barcodeMapModel"         ) var barcodeMapModel         : ArrayList<UPCRequest>              = arrayListOf()
)