package com.varitas.gokulpos.tablet.repositories

import com.varitas.gokulpos.tablet.model.Category
import com.varitas.gokulpos.tablet.request.ChangePriceRequest
import com.varitas.gokulpos.tablet.request.ChangeQtyRequest
import com.varitas.gokulpos.tablet.request.ItemPrice
import com.varitas.gokulpos.tablet.request.ItemPriceList
import com.varitas.gokulpos.tablet.request.ItemStock
import com.varitas.gokulpos.tablet.request.NotificationRequest
import com.varitas.gokulpos.tablet.request.ProductInsertRequest
import com.varitas.gokulpos.tablet.request.UpdateProductDetail
import com.varitas.gokulpos.tablet.response.APIResponse
import com.varitas.gokulpos.tablet.response.BaseResponse
import com.varitas.gokulpos.tablet.response.BaseResponseList
import com.varitas.gokulpos.tablet.response.CommonDropDown
import com.varitas.gokulpos.tablet.response.DataDetails
import com.varitas.gokulpos.tablet.response.DropDown
import com.varitas.gokulpos.tablet.response.DropDownCategories
import com.varitas.gokulpos.tablet.response.FavouriteItems
import com.varitas.gokulpos.tablet.response.ItemCartDetail
import com.varitas.gokulpos.tablet.response.ItemCount
import com.varitas.gokulpos.tablet.response.ItemStockSpecification
import com.varitas.gokulpos.tablet.response.Notifications
import com.varitas.gokulpos.tablet.response.ProductDetail
import com.varitas.gokulpos.tablet.response.ProductList
import com.varitas.gokulpos.tablet.response.ProductResponse
import com.varitas.gokulpos.tablet.response.ScanBarcode
import com.varitas.gokulpos.tablet.response.Tax
import com.varitas.gokulpos.tablet.utilities.Enums


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

    suspend fun updateItemPrice(req : ItemPrice, onSuccess : (response : APIResponse) -> Unit, onFailure : (t : Throwable) -> Unit)

    suspend fun makeShortcut(id : Int, onSuccess : (response : APIResponse) -> Unit, onFailure : (t : Throwable) -> Unit)

    suspend fun getItemStock(id : Int, onSuccess : (response : BaseResponseList<ItemStockSpecification>) -> Unit, onFailure : (t : Throwable) -> Unit)

    suspend fun updateItemStock(req : ArrayList<ItemStock>, onSuccess : (response : APIResponse) -> Unit, onFailure : (t : Throwable) -> Unit)

    suspend fun getItemDetails(id : Int, onSuccess : (response : BaseResponse<ProductResponse>) -> Unit, onFailure : (t : Throwable) -> Unit)

    suspend fun updateProduct(req : ProductInsertRequest, onSuccess : (response : APIResponse) -> Unit, onFailure : (t : Throwable) -> Unit)

    suspend fun getPriceList(id : Int, onSuccess : (response : BaseResponseList<ItemPriceList>) -> Unit, onFailure : (t : Throwable) -> Unit)

    suspend fun getSizePackDropDown(storeId : Int, typeId : Int, onSuccess : (response : BaseResponseList<CommonDropDown>) -> Unit, onFailure : (t : Throwable) -> Unit)

    suspend fun fetchFavouriteItems(storeId : Int, onSuccess : (response : BaseResponseList<FavouriteItems>) -> Unit, onFailure : (t : Throwable) -> Unit)

    suspend fun fetchFavouriteGroupItems(groupId: Int, onSuccess: (response: BaseResponseList<ItemCount>) -> Unit, onFailure: (t: Throwable) -> Unit)

    suspend fun fetchFavouriteMenuItems(storeId: Int, type: Int, id: Int, onSuccess: (response: BaseResponseList<DataDetails>) -> Unit, onFailure: (t: Throwable) -> Unit)

    suspend fun fetchAutoCompleteItems(msg: String, storeId: Int, onSuccess: (response: BaseResponseList<FavouriteItems>) -> Unit, onFailure: (t: Throwable) -> Unit)

    suspend fun fetchCartItemDetail(id : Int, onSuccess : (response : BaseResponse<ItemCartDetail>) -> Unit, onFailure : (t : Throwable) -> Unit)

    suspend fun fetchSoldAlongItemDetail(id : Int, onSuccess : (response : BaseResponse<List<ItemCartDetail>>) -> Unit, onFailure : (t : Throwable) -> Unit)

    suspend fun fetchProductsFilterWise(storeId: Int, departmentId: Int, categoryId: Int, brandId: Int, itemTypeId: Int, onSuccess: (response: BaseResponseList<ProductList>) -> Unit, onFailure: (t: Throwable) -> Unit)
}