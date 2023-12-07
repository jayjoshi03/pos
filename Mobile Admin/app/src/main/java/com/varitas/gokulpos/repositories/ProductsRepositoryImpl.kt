package com.varitas.gokulpos.repositories

import android.util.Log
import com.varitas.gokulpos.api.ApiClient
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

    override suspend fun getItemPrice(id : Int, onSuccess : (response : BaseResponse<ItemPrice>) -> Unit, onFailure : (t : Throwable) -> Unit) {
        api.getItemPrice(id).enqueue(object : Callback<BaseResponse<ItemPrice>> {
            override fun onResponse(call : Call<BaseResponse<ItemPrice>>, response : Response<BaseResponse<ItemPrice>>) {
                response.body()?.let { list->
                    onSuccess.invoke(list)
                    Log.e("ITEM PRICE DETAILS", list.toString())
                }
            }

            override fun onFailure(call : Call<BaseResponse<ItemPrice>>, t : Throwable) {
                onFailure.invoke(t)
                Log.e("ITEM PRICE DETAILS ERROR", t.toString())
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

            override fun onFailure(call : Call<APIResponse>, t : Throwable) {
                onFailure.invoke(t)
                Log.e("UPDATE ITEM PRICE ERROR", t.toString())
            }
        })
    }

    override suspend fun getItemStock(id : Int, onSuccess : (response : BaseResponseList<ItemStockSpecification>) -> Unit, onFailure : (t : Throwable) -> Unit) {
        api.getItemStock(id).enqueue(object : Callback<BaseResponseList<ItemStockSpecification>> {
            override fun onResponse(call : Call<BaseResponseList<ItemStockSpecification>>, response : Response<BaseResponseList<ItemStockSpecification>>) {
                response.body()?.let { list->
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

            override fun onFailure(call : Call<APIResponse>, t : Throwable) {
                onFailure.invoke(t)
                Log.e("UPDATE ITEM STOCK ERROR", t.toString())
            }
        })
    }

    override suspend fun getItemDetails(id : Int, onSuccess : (response : BaseResponse<ProductResponse>) -> Unit, onFailure : (t : Throwable) -> Unit) {
        api.getItemDetails(id).enqueue(object : Callback<BaseResponse<ProductResponse>> {
            override fun onResponse(call : Call<BaseResponse<ProductResponse>>, response : Response<BaseResponse<ProductResponse>>) {
                response.body()?.let { list->
                    onSuccess.invoke(list)
                    Log.e("ITEM DETAILS", list.toString())
                }
            }

            override fun onFailure(call : Call<BaseResponse<ProductResponse>>, t : Throwable) {
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

            override fun onFailure(call : Call<APIResponse>, t : Throwable) {
                onFailure.invoke(t)
                Log.e("UPDATE PRODUCT ERROR", t.toString())
            }
        })
    }

    override suspend fun getPriceList(id : Int, onSuccess : (response : BaseResponseList<ItemPriceList>) -> Unit, onFailure : (t : Throwable) -> Unit) {
        api.getPriceList(id).enqueue(object : Callback<BaseResponseList<ItemPriceList>> {
            override fun onResponse(call : Call<BaseResponseList<ItemPriceList>>, response : Response<BaseResponseList<ItemPriceList>>) {
                response.body()?.let { list->
                    onSuccess.invoke(list)
                    Log.e("ITEM PRICE DETAILS", list.toString())
                }
            }

            override fun onFailure(call : Call<BaseResponseList<ItemPriceList>>, t : Throwable) {
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

    override suspend fun fetchAutoCompleteItems(msg : String, storeId : Int, onSuccess : (response : BaseResponseList<FavouriteItems>) -> Unit, onFailure : (t : Throwable) -> Unit) {
        api.getAutoCompleteItems(msg, storeId).enqueue(object : Callback<BaseResponseList<FavouriteItems>> {
            override fun onResponse(call : Call<BaseResponseList<FavouriteItems>>, response : Response<BaseResponseList<FavouriteItems>>) {
                response.body()?.let { list->
                    onSuccess.invoke(list)
                    Log.e("AUTO COMPLETE ITEMS", list.toString())
                }
            }

            override fun onFailure(call : Call<BaseResponseList<FavouriteItems>>, t : Throwable) {
                onFailure.invoke(t)
                Log.e("AUTO COMPLETE ITEMS ERROR", t.toString())
            }
        })
    }
}