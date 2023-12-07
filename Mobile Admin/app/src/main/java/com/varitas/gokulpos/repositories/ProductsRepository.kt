package com.varitas.gokulpos.repositories

import com.varitas.gokulpos.request.ChangePriceRequest
import com.varitas.gokulpos.request.ChangeQtyRequest
import com.varitas.gokulpos.request.ItemPrice
import com.varitas.gokulpos.request.ItemPriceList
import com.varitas.gokulpos.request.ItemStock
import com.varitas.gokulpos.request.NotificationRequest
import com.varitas.gokulpos.request.ProductInsertRequest
import com.varitas.gokulpos.request.UpdateProductDetail
import com.varitas.gokulpos.response.APIResponse
import com.varitas.gokulpos.response.BaseResponse
import com.varitas.gokulpos.response.BaseResponseList
import com.varitas.gokulpos.response.Category
import com.varitas.gokulpos.response.CommonDropDown
import com.varitas.gokulpos.response.DropDown
import com.varitas.gokulpos.response.DropDownCategories
import com.varitas.gokulpos.response.FavouriteItems
import com.varitas.gokulpos.response.ItemStockSpecification
import com.varitas.gokulpos.response.Notifications
import com.varitas.gokulpos.response.ProductDetail
import com.varitas.gokulpos.response.ProductList
import com.varitas.gokulpos.response.ProductResponse
import com.varitas.gokulpos.response.ScanBarcode
import com.varitas.gokulpos.response.Tax
import com.varitas.gokulpos.utilities.Enums


interface ProductsRepository {
    suspend fun fetchProducts(storeId : Int, onSuccess : (response : BaseResponseList<ProductList>) -> Unit, onFailure : (t : Throwable) -> Unit)

    suspend fun fetchTax(storeId : Int, onSuccess : (response : BaseResponseList<Tax>) -> Unit, onFailure : (t : Throwable) -> Unit)

    suspend fun fetchDepartments(storeId : Int, onSuccess : (response : BaseResponseList<DropDown>) -> Unit, onFailure : (t : Throwable) -> Unit)

    suspend fun fetchItemTypes(storeId : Int, onSuccess : (response : BaseResponseList<DropDown>) -> Unit, onFailure : (t : Throwable) -> Unit)

    suspend fun fetchAttributes(enum : Enums.Attributes, onSuccess : (response : BaseResponseList<DropDown>) -> Unit, onFailure : (t : Throwable) -> Unit)

    suspend fun fetchCategories(storeId : Int, onSuccess : (response : BaseResponseList<DropDownCategories>) -> Unit, onFailure : (t : Throwable) -> Unit)

    suspend fun fetchSubCategories(onSuccess : (response : BaseResponseList<Category>) -> Unit, onFailure : (t : Throwable) -> Unit)

    suspend fun updatePrice(req : ChangePriceRequest, onSuccess : (response : APIResponse) -> Unit, onFailure : (t : Throwable) -> Unit)

    suspend fun updateQuantity(req : ChangeQtyRequest, onSuccess : (response : APIResponse) -> Unit, onFailure : (t : Throwable) -> Unit)

    suspend fun updateProductDetails(req : UpdateProductDetail, onSuccess : (response : APIResponse) -> Unit, onFailure : (t : Throwable) -> Unit)

    suspend fun fetchProductDetails(id : Int, onSuccess : (response : BaseResponse<ProductDetail>) -> Unit, onFailure : (t : Throwable) -> Unit)

    suspend fun fetchBarcodeDetails(upc : String, onSuccess : (response : BaseResponse<ScanBarcode>) -> Unit, onFailure : (t : Throwable) -> Unit)

    suspend fun fetchNotifications(storeId : Int, onSuccess : (response : BaseResponseList<Notifications>) -> Unit, onFailure : (t : Throwable) -> Unit)

    suspend fun updateNotifications(req : NotificationRequest, onSuccess : (response : APIResponse) -> Unit, onFailure : (t : Throwable) -> Unit)

    suspend fun insertProduct(req : ProductInsertRequest, onSuccess : (response : APIResponse) -> Unit, onFailure : (t : Throwable) -> Unit)

    suspend fun getItemPrice(id : Int, onSuccess : (response : BaseResponse<ItemPrice>) -> Unit, onFailure : (t : Throwable) -> Unit)

    suspend fun updateItemPrice(req : ItemPrice, onSuccess : (response : APIResponse) -> Unit, onFailure : (t : Throwable) -> Unit)

    suspend fun getItemStock(id : Int, onSuccess : (response : BaseResponseList<ItemStockSpecification>) -> Unit, onFailure : (t : Throwable) -> Unit)

    suspend fun updateItemStock(req : ArrayList<ItemStock>, onSuccess : (response : APIResponse) -> Unit, onFailure : (t : Throwable) -> Unit)

    suspend fun getItemDetails(id : Int, onSuccess : (response : BaseResponse<ProductResponse>) -> Unit, onFailure : (t : Throwable) -> Unit)

    suspend fun updateProduct(req : ProductInsertRequest, onSuccess : (response : APIResponse) -> Unit, onFailure : (t : Throwable) -> Unit)

    suspend fun getPriceList(id : Int, onSuccess : (response : BaseResponseList<ItemPriceList>) -> Unit, onFailure : (t : Throwable) -> Unit)

    suspend fun getSizePackDropDown(storeId : Int, typeId : Int, onSuccess : (response : BaseResponseList<CommonDropDown>) -> Unit, onFailure : (t : Throwable) -> Unit)

    suspend fun fetchAutoCompleteItems(msg: String, storeId : Int, onSuccess: (response: BaseResponseList<FavouriteItems>) -> Unit, onFailure: (t: Throwable) -> Unit)
}