package com.varitas.gokulpos.tablet.repositories

import android.util.Log
import com.varitas.gokulpos.tablet.api.ApiClient
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
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProductsRepositoryImpl(private val api : ApiClient) : ProductsRepository {

    override suspend fun fetchProducts(storeId : Int, onSuccess : (response : BaseResponseList<ProductList>) -> Unit, onFailure : (t : Throwable) -> Unit) {
        api.getAllProducts(storeId = storeId).enqueue(object : Callback<BaseResponseList<ProductList>> {
            override fun onResponse(call : Call<BaseResponseList<ProductList>>, response : Response<BaseResponseList<ProductList>>) {
                response.body()?.let { list->
                    onSuccess.invoke(list)
                    Log.e("PRODUCTS", list.toString())
                }
            }

            override fun onFailure(call : Call<BaseResponseList<ProductList>>, t : Throwable) {
                onFailure.invoke(t)
                Log.e("PRODUCTS ERROR", t.toString())
            }
        })
    }

    override suspend fun fetchDepartments(storeId : Int, onSuccess : (response : BaseResponseList<DropDown>) -> Unit, onFailure : (t : Throwable) -> Unit) {
//        api.getDepartmentDropDown(storeId = storeId).enqueue(object : Callback<BaseResponseList<DropDown>> {
//            override fun onResponse(call: Call<BaseResponseList<DropDown>>, response: Response<BaseResponseList<DropDown>>) {
//                response.body()?.let { list ->
//                    onSuccess.invoke(list)
//                    Log.e("DROPDOWN DEPARTMENT", list.toString())
//                }
//            }
//
//            override fun onFailure(call: Call<BaseResponseList<DropDown>>, t: Throwable) {
//                onFailure.invoke(t)
//                Log.e("DROPDOWN ERROR", t.toString())
//            }
//        })
    }

    override suspend fun fetchItemTypes(storeId : Int, onSuccess : (response : BaseResponseList<DropDown>) -> Unit, onFailure : (t : Throwable) -> Unit) {
//        api.getItemTypeDropDown(storeId = storeId).enqueue(object : Callback<BaseResponseList<DropDown>> {
//            override fun onResponse(call: Call<BaseResponseList<DropDown>>, response: Response<BaseResponseList<DropDown>>) {
//                response.body()?.let { list ->
//                    onSuccess.invoke(list)
//                    Log.e("DROPDOWN ITEM TYPE", list.toString())
//                }
//            }
//
//            override fun onFailure(call: Call<BaseResponseList<DropDown>>, t: Throwable) {
//                onFailure.invoke(t)
//                Log.e("DROPDOWN ERROR", t.toString())
//            }
//        })
    }

    override suspend fun fetchCategories(storeId : Int, onSuccess : (response : BaseResponseList<DropDownCategories>) -> Unit, onFailure : (t : Throwable) -> Unit) {
//        api.getCategoryDropDown(storeId = storeId).enqueue(object : Callback<BaseResponseList<DropDownCategories>> {
//            override fun onResponse(call: Call<BaseResponseList<DropDownCategories>>, response: Response<BaseResponseList<DropDownCategories>>) {
//                response.body()?.let { list ->
//                    onSuccess.invoke(list)
//                    Log.e("DROPDOWN CATEGORY", list.toString())
//                }
//            }
//
//            override fun onFailure(call: Call<BaseResponseList<DropDownCategories>>, t: Throwable) {
//                onFailure.invoke(t)
//                Log.e("DROPDOWN CATEGORY", t.toString())
//            }
//        })
    }

    override suspend fun updatePrice(req : ChangePriceRequest, onSuccess : (response : APIResponse) -> Unit, onFailure : (t : Throwable) -> Unit) {
        Log.e("CHANGE PRICE REQUEST", req.toString())
//        api.updatePrice(req).enqueue(object : Callback<APIResponse> {
//            override fun onResponse(call: Call<APIResponse>, response: Response<APIResponse>) {
//                response.body()?.let { login ->
//                    onSuccess.invoke(login)
//                    Log.e("CHANGE PRICE SUCCESS", login.toString())
//                }
//            }
//
//            override fun onFailure(call: Call<APIResponse>, t: Throwable) {
//                onFailure.invoke(t)
//                Log.e("CHANGE PRICE ERROR", t.toString())
//            }
//        })
    }

    override suspend fun updateQuantity(req : ChangeQtyRequest, onSuccess : (response : APIResponse) -> Unit, onFailure : (t : Throwable) -> Unit) {
        Log.e("CHANGE QUANTITY REQUEST", req.toString())
//        api.updateQuantity(req).enqueue(object : Callback<APIResponse> {
//            override fun onResponse(call: Call<APIResponse>, response: Response<APIResponse>) {
//                response.body()?.let { login ->
//                    onSuccess.invoke(login)
//                    Log.e("CHANGE QUANTITY SUCCESS", login.toString())
//                }
//            }
//
//            override fun onFailure(call: Call<APIResponse>, t: Throwable) {
//                onFailure.invoke(t)
//                Log.e("CHANGE QUANTITY ERROR", t.toString())
//            }
//        })
    }

    override suspend fun updateProductDetails(req : UpdateProductDetail, onSuccess : (response : APIResponse) -> Unit, onFailure : (t : Throwable) -> Unit) {
        Log.e("UPDATE PRODUCT REQUEST", req.toString())
//        api.updateProduct(req).enqueue(object : Callback<APIResponse> {
//            override fun onResponse(call: Call<APIResponse>, response: Response<APIResponse>) {
//                response.body()?.let { login ->
//                    onSuccess.invoke(login)
//                    Log.e("UPDATE PRODUCT SUCCESS", login.toString())
//                }
//            }
//
//            override fun onFailure(call: Call<APIResponse>, t: Throwable) {
//                onFailure.invoke(t)
//                Log.e("UPDATE PRODUCT ERROR", t.toString())
//            }
//        })
    }

    override suspend fun fetchProductDetails(id : Int, onSuccess : (response : BaseResponse<ProductDetail>) -> Unit, onFailure : (t : Throwable) -> Unit) {
//        api.getProductDetails(id).enqueue(object : Callback<BaseResponse<ProductDetail>> {
//            override fun onResponse(call: Call<BaseResponse<ProductDetail>>, response: Response<BaseResponse<ProductDetail>>) {
//                response.body()?.let { list ->
//                    onSuccess.invoke(list)
//                    Log.e("PRODUCT DETAILS", list.toString())
//                }
//            }
//
//            override fun onFailure(call: Call<BaseResponse<ProductDetail>>, t: Throwable) {
//                onFailure.invoke(t)
//                Log.e("PRODUCT DETAILS ERROR", t.toString())
//            }
//        })
    }

    override suspend fun fetchTax(storeId : Int, onSuccess : (response : BaseResponseList<Tax>) -> Unit, onFailure : (t : Throwable) -> Unit) {
//        api.getTaxes(storeId = storeId).enqueue(object : Callback<BaseResponseList<Tax>> {
//            override fun onResponse(call: Call<BaseResponseList<Tax>>, response: Response<BaseResponseList<Tax>>) {
//                response.body()?.let { list ->
//                    onSuccess.invoke(list)
//                    Log.e("TAX", list.toString())
//                }
//            }
//
//            override fun onFailure(call: Call<BaseResponseList<Tax>>, t: Throwable) {
//                onFailure.invoke(t)
//                Log.e("TAX ERROR", t.toString())
//            }
//        })
    }

    override suspend fun fetchAttributes(enum : Enums.Attributes, onSuccess : (response : BaseResponseList<DropDown>) -> Unit, onFailure : (t : Throwable) -> Unit) {
//        api.getAttributes(if (enum == Enums.Attributes.PACK) Default.PACK else Default.SIZE).enqueue(object : Callback<BaseResponseList<DropDown>> {
//            override fun onResponse(call: Call<BaseResponseList<DropDown>>, response: Response<BaseResponseList<DropDown>>) {
//                response.body()?.let { list ->
//                    onSuccess.invoke(list)
//                    Log.e("DROPDOWN ATTRIBUTES", list.toString())
//                }
//            }
//
//            override fun onFailure(call: Call<BaseResponseList<DropDown>>, t: Throwable) {
//                onFailure.invoke(t)
//                Log.e("DROPDOWN ATTRIBUTES", t.toString())
//            }
//        })
    }

    override suspend fun fetchSubCategories(onSuccess : (response : BaseResponseList<Category>) -> Unit, onFailure : (t : Throwable) -> Unit) {
//        api.getSubCategories().enqueue(object : Callback<BaseResponseList<Category>> {
//            override fun onResponse(call: Call<BaseResponseList<Category>>, response: Response<BaseResponseList<Category>>) {
//                response.body()?.let { list ->
//                    onSuccess.invoke(list)
//                    Log.e("DROPDOWN CATEGORY", list.toString())
//                }
//            }
//
//            override fun onFailure(call: Call<BaseResponseList<Category>>, t: Throwable) {
//                onFailure.invoke(t)
//                Log.e("DROPDOWN CATEGORY", t.toString())
//            }
//        })
    }

    override suspend fun fetchBarcodeDetails(upc : String, onSuccess : (response : BaseResponse<ScanBarcode>) -> Unit, onFailure : (t : Throwable) -> Unit) {
        api.getBarcodeDetails(upc).enqueue(object : Callback<BaseResponse<ScanBarcode>> {
            override fun onResponse(call : Call<BaseResponse<ScanBarcode>>, response : Response<BaseResponse<ScanBarcode>>) {
                response.body()?.let { list->
                    onSuccess.invoke(list)
                    Log.e("BARCODE DETAILS", list.toString())
                }
            }

            override fun onFailure(call : Call<BaseResponse<ScanBarcode>>, t : Throwable) {
                onFailure.invoke(t)
                Log.e("BARCODE DETAILS ERROR", t.toString())
            }
        })
    }

    override suspend fun fetchNotifications(storeId : Int, onSuccess : (response : BaseResponseList<Notifications>) -> Unit, onFailure : (t : Throwable) -> Unit) {
//        api.getNotifications(storeId).enqueue(object : Callback<BaseResponseList<Notifications>> {
//            override fun onResponse(call: Call<BaseResponseList<Notifications>>, response: Response<BaseResponseList<Notifications>>) {
//                response.body()?.let { list ->
//                    onSuccess.invoke(list)
//                    Log.e("NOTIFICATIONS", list.toString())
//                }
//            }
//
//            override fun onFailure(call: Call<BaseResponseList<Notifications>>, t: Throwable) {
//                onFailure.invoke(t)
//                Log.e("NOTIFICATIONS ERROR", t.toString())
//            }
//        })
    }

    override suspend fun updateNotifications(req : NotificationRequest, onSuccess : (response : APIResponse) -> Unit, onFailure : (t : Throwable) -> Unit) {
//        api.updateNotifications(req).enqueue(object : Callback<APIResponse> {
//            override fun onResponse(call: Call<APIResponse>, response: Response<APIResponse>) {
//                response.body()?.let { list ->
//                    onSuccess.invoke(list)
//                    Log.e("UPDATE NOTIFICATIONS", list.toString())
//                }
//            }
//
//            override fun onFailure(call: Call<APIResponse>, t: Throwable) {
//                onFailure.invoke(t)
//                Log.e("UPDATE NOTIFICATIONS", t.toString())
//            }
//        })
    }

    override suspend fun insertProduct(req : ProductInsertRequest, onSuccess : (response : APIResponse) -> Unit, onFailure : (t : Throwable) -> Unit) {
        api.insertProduct(req).enqueue(object : Callback<APIResponse> {
            override fun onResponse(call : Call<APIResponse>, response : Response<APIResponse>) {
                response.body()?.let { list->
                    onSuccess.invoke(list)
                    Log.e("INSERT PRODUCT SUCCESS", list.toString())
                }
            }

            override fun onFailure(call : Call<APIResponse>, t : Throwable) {
                onFailure.invoke(t)
                Log.e("INSERT PRODUCT ERROR", t.toString())
            }
        })
    }

    override suspend fun updateItemPrice(req : ItemPrice, onSuccess : (response : APIResponse) -> Unit, onFailure : (t : Throwable) -> Unit) {
        api.updateItemPrice(req).enqueue(object : Callback<APIResponse> {
            override fun onResponse(call : Call<APIResponse>, response : Response<APIResponse>) {
                response.body()?.let { list->
                    onSuccess.invoke(list)
                    Log.e("UPDATE ITEM PRICE SUCCESS", list.toString())
                }
            }

            override fun onFailure(call: Call<APIResponse>, t: Throwable) {
                onFailure.invoke(t)
                Log.e("UPDATE ITEM PRICE ERROR", t.toString())
            }
        })
    }

    override suspend fun makeShortcut(id: Int, onSuccess: (response: APIResponse) -> Unit, onFailure: (t: Throwable) -> Unit) {
        api.createShortcut(id).enqueue(object : Callback<APIResponse> {
            override fun onResponse(call: Call<APIResponse>, response: Response<APIResponse>) {
                response.body()?.let { list ->
                    onSuccess.invoke(list)
                    Log.e("CREATE SHORTCUT SUCCESS", list.toString())
                }
            }

            override fun onFailure(call: Call<APIResponse>, t: Throwable) {
                onFailure.invoke(t)
                Log.e("CREATE SHORTCUT ERROR", t.toString())
            }
        })
    }

    override suspend fun getItemStock(id: Int, onSuccess: (response: BaseResponseList<ItemStockSpecification>) -> Unit, onFailure: (t: Throwable) -> Unit) {
        api.getItemStock(id).enqueue(object : Callback<BaseResponseList<ItemStockSpecification>> {
            override fun onResponse(call: Call<BaseResponseList<ItemStockSpecification>>, response: Response<BaseResponseList<ItemStockSpecification>>) {
                response.body()?.let { list ->
                    onSuccess.invoke(list)
                    Log.e("ITEM STOCK DETAILS", list.toString())
                }
            }

            override fun onFailure(call : Call<BaseResponseList<ItemStockSpecification>>, t : Throwable) {
                onFailure.invoke(t)
                Log.e("ITEM STOCK DETAILS ERROR", t.toString())
            }
        })
    }

    override suspend fun updateItemStock(req : ArrayList<ItemStock>, onSuccess : (response : APIResponse) -> Unit, onFailure : (t : Throwable) -> Unit) {
        api.updateItemStock(req).enqueue(object : Callback<APIResponse> {
            override fun onResponse(call : Call<APIResponse>, response : Response<APIResponse>) {
                response.body()?.let { list->
                    onSuccess.invoke(list)
                    Log.e("UPDATE ITEM STOCK SUCCESS", list.toString())
                }
            }

            override fun onFailure(call: Call<APIResponse>, t: Throwable) {
                onFailure.invoke(t)
                Log.e("UPDATE ITEM STOCK ERROR", t.toString())
            }
        })
    }

    override suspend fun getItemDetails(id: Int, onSuccess: (response: BaseResponse<ProductResponse>) -> Unit, onFailure: (t: Throwable) -> Unit) {
        api.getItemDetails(id).enqueue(object : Callback<BaseResponse<ProductResponse>> {
            override fun onResponse(call: Call<BaseResponse<ProductResponse>>, response: Response<BaseResponse<ProductResponse>>) {
                response.body()?.let { list ->
                    onSuccess.invoke(list)
                    Log.e("ITEM DETAILS", list.toString())
                }
            }

            override fun onFailure(call: Call<BaseResponse<ProductResponse>>, t: Throwable) {
                onFailure.invoke(t)
                Log.e("ITEM DETAILS ERROR", t.toString())
            }
        })
    }

    override suspend fun updateProduct(req : ProductInsertRequest, onSuccess : (response : APIResponse) -> Unit, onFailure : (t : Throwable) -> Unit) {
        api.updateProduct(req).enqueue(object : Callback<APIResponse> {
            override fun onResponse(call : Call<APIResponse>, response : Response<APIResponse>) {
                response.body()?.let { list->
                    onSuccess.invoke(list)
                    Log.e("UPDATE PRODUCT SUCCESS", list.toString())
                }
            }

            override fun onFailure(call: Call<APIResponse>, t: Throwable) {
                onFailure.invoke(t)
                Log.e("UPDATE PRODUCT ERROR", t.toString())
            }
        })
    }

    override suspend fun getPriceList(id: Int, onSuccess: (response: BaseResponseList<ItemPriceList>) -> Unit, onFailure: (t: Throwable) -> Unit) {
        api.getPriceList(id).enqueue(object : Callback<BaseResponseList<ItemPriceList>> {
            override fun onResponse(call: Call<BaseResponseList<ItemPriceList>>, response: Response<BaseResponseList<ItemPriceList>>) {
                response.body()?.let { list ->
                    onSuccess.invoke(list)
                    Log.e("ITEM PRICE DETAILS", list.toString())
                }
            }

            override fun onFailure(call: Call<BaseResponseList<ItemPriceList>>, t: Throwable) {
                onFailure.invoke(t)
                Log.e("ITEM PRICE DETAILS ERROR", t.toString())
            }
        })
    }

    override suspend fun getSizePackDropDown(storeId : Int, typeId : Int, onSuccess : (response : BaseResponseList<CommonDropDown>) -> Unit, onFailure : (t : Throwable) -> Unit) {
        api.getSizePackDropDown(storeId = storeId, typeId = typeId).enqueue(object : Callback<BaseResponseList<CommonDropDown>> {
            override fun onResponse(call : Call<BaseResponseList<CommonDropDown>>, response : Response<BaseResponseList<CommonDropDown>>) {
                response.body()?.let { list->
                    onSuccess.invoke(list)
                    Log.e("SIZE/PACK DROPDOWN", list.toString())
                }
            }

            override fun onFailure(call : Call<BaseResponseList<CommonDropDown>>, t : Throwable) {
                onFailure.invoke(t)
                Log.e("SIZE/PACK DROPDOWN ERROR", t.toString())
            }
        })
    }

    override suspend fun fetchFavouriteItems(storeId: Int, onSuccess: (response: BaseResponseList<FavouriteItems>) -> Unit, onFailure: (t: Throwable) -> Unit) {
        api.getFavouriteItems(storeId).enqueue(object : Callback<BaseResponseList<FavouriteItems>> {
            override fun onResponse(call: Call<BaseResponseList<FavouriteItems>>, response: Response<BaseResponseList<FavouriteItems>>) {
                response.body()?.let { list ->
                    onSuccess.invoke(list)
                    Log.e("FAVOURITE ITEMS", list.toString())
                }
            }

            override fun onFailure(call: Call<BaseResponseList<FavouriteItems>>, t: Throwable) {
                onFailure.invoke(t)
                Log.e("FAVOURITE ITEMS ERROR", t.toString())
            }
        })
    }

    override suspend fun fetchFavouriteGroupItems(groupId: Int, onSuccess: (response: BaseResponseList<ItemCount>) -> Unit, onFailure: (t: Throwable) -> Unit) {
        api.getFavouriteGroupItems(groupId).enqueue(object : Callback<BaseResponseList<ItemCount>> {
            override fun onResponse(call: Call<BaseResponseList<ItemCount>>, response: Response<BaseResponseList<ItemCount>>) {
                response.body()?.let { list ->
                    onSuccess.invoke(list)
                    Log.e("FAVOURITE ITEMS", list.toString())
                }
            }

            override fun onFailure(call: Call<BaseResponseList<ItemCount>>, t: Throwable) {
                onFailure.invoke(t)
                Log.e("FAVOURITE ITEMS ERROR", t.toString())
            }
        })
    }

    override suspend fun fetchFavouriteMenuItems(storeId: Int, type: Int, id: Int, onSuccess: (response: BaseResponseList<DataDetails>) -> Unit, onFailure: (t: Throwable) -> Unit) {
        api.getFavouriteMenuItem(storeId, type, id).enqueue(object : Callback<BaseResponseList<DataDetails>> {
            override fun onResponse(call: Call<BaseResponseList<DataDetails>>, response: Response<BaseResponseList<DataDetails>>) {
                response.body()?.let { list ->
                    onSuccess.invoke(list)
                    Log.e("FAVOURITE ITEMS", list.toString())
                }
            }

            override fun onFailure(call: Call<BaseResponseList<DataDetails>>, t: Throwable) {
                onFailure.invoke(t)
                Log.e("FAVOURITE ITEMS ERROR", t.toString())
            }
        })
    }

    override suspend fun fetchAutoCompleteItems(msg: String, storeId: Int, onSuccess: (response: BaseResponseList<FavouriteItems>) -> Unit, onFailure: (t: Throwable) -> Unit) {
        api.getAutoCompleteItems(msg, storeId).enqueue(object : Callback<BaseResponseList<FavouriteItems>> {
            override fun onResponse(call: Call<BaseResponseList<FavouriteItems>>, response: Response<BaseResponseList<FavouriteItems>>) {
                response.body()?.let { list ->
                    onSuccess.invoke(list)
                    Log.e("AUTO COMPLETE ITEMS", list.toString())
                }
            }

            override fun onFailure(call: Call<BaseResponseList<FavouriteItems>>, t: Throwable) {
                onFailure.invoke(t)
                Log.e("AUTO COMPLETE ITEMS ERROR", t.toString())
            }
        })
    }

    override suspend fun fetchCartItemDetail(id: Int, onSuccess: (response: BaseResponse<ItemCartDetail>) -> Unit, onFailure: (t: Throwable) -> Unit) {
        api.getCartItemDetail(id).enqueue(object : Callback<BaseResponse<ItemCartDetail>> {
            override fun onResponse(call: Call<BaseResponse<ItemCartDetail>>, response: Response<BaseResponse<ItemCartDetail>>) {
                response.body()?.let { list ->
                    onSuccess.invoke(list)
                    Log.e("CART ITEM DETAILS", list.toString())
                }
            }

            override fun onFailure(call: Call<BaseResponse<ItemCartDetail>>, t: Throwable) {
                onFailure.invoke(t)
                Log.e("CART ITEM DETAILS ERROR", t.toString())
            }
        })
    }

    override suspend fun fetchSoldAlongItemDetail(id: Int, onSuccess: (response: BaseResponse<List<ItemCartDetail>>) -> Unit, onFailure: (t: Throwable) -> Unit) {
        api.getSoldAlongItemDetail(id).enqueue(object : Callback<BaseResponse<List<ItemCartDetail>>> {
            override fun onResponse(call: Call<BaseResponse<List<ItemCartDetail>>>, response: Response<BaseResponse<List<ItemCartDetail>>>) {
                response.body()?.let { list ->
                    onSuccess.invoke(list)
                    Log.e("SOLD ALONG CART ITEM DETAILS", list.toString())
                }
            }

            override fun onFailure(call: Call<BaseResponse<List<ItemCartDetail>>>, t: Throwable) {
                onFailure.invoke(t)
                Log.e("SOLD ALONG CART ITEM DETAILS ERROR", t.toString())
            }
        })
    }

    override suspend fun fetchProductsFilterWise(storeId: Int, departmentId: Int, categoryId: Int, brandId: Int, itemTypeId: Int, onSuccess: (response: BaseResponseList<ProductList>) -> Unit, onFailure: (t: Throwable) -> Unit) {
        api.getProductFilterWise(storeId = storeId, departmentId = departmentId, categoryId = categoryId, brandId = brandId, itemType = itemTypeId, spId = 0).enqueue(object : Callback<BaseResponseList<ProductList>> {
            override fun onResponse(call : Call<BaseResponseList<ProductList>>, response : Response<BaseResponseList<ProductList>>) {
                response.body()?.let { list->
                    onSuccess.invoke(list)
                    Log.e("PRODUCTS", list.toString())
                }
            }

            override fun onFailure(call : Call<BaseResponseList<ProductList>>, t : Throwable) {
                onFailure.invoke(t)
                Log.e("PRODUCTS ERROR", t.toString())
            }
        })
    }
}