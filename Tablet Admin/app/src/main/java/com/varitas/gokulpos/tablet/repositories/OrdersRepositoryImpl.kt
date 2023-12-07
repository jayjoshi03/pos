package com.varitas.gokulpos.tablet.repositories

import android.util.Log
import com.varitas.gokulpos.tablet.api.ApiClient
import com.varitas.gokulpos.tablet.model.Store
import com.varitas.gokulpos.tablet.model.StoreUpdate
import com.varitas.gokulpos.tablet.request.DrawerInsertRequest
import com.varitas.gokulpos.tablet.request.OrderInsertRequest
import com.varitas.gokulpos.tablet.request.UpdateOrderRequest
import com.varitas.gokulpos.tablet.response.APIResponse
import com.varitas.gokulpos.tablet.response.AddItemToDiscount
import com.varitas.gokulpos.tablet.response.AllOrders
import com.varitas.gokulpos.tablet.response.BaseResponse
import com.varitas.gokulpos.tablet.response.BaseResponseList
import com.varitas.gokulpos.tablet.response.CommonDropDown
import com.varitas.gokulpos.tablet.response.CompletedOrder
import com.varitas.gokulpos.tablet.response.Currency
import com.varitas.gokulpos.tablet.response.DrawerTransaction
import com.varitas.gokulpos.tablet.response.Invoice
import com.varitas.gokulpos.tablet.response.OpenBatch
import com.varitas.gokulpos.tablet.response.OpenBatchRequest
import com.varitas.gokulpos.tablet.response.OrderAnalytics
import com.varitas.gokulpos.tablet.response.OrderDetails
import com.varitas.gokulpos.tablet.response.OrderList
import com.varitas.gokulpos.tablet.response.OrderPlaced
import com.varitas.gokulpos.tablet.response.PurchaseOrder
import com.varitas.gokulpos.tablet.response.Role
import com.varitas.gokulpos.tablet.response.SalesPromotion
import com.varitas.gokulpos.tablet.response.Tender
import com.varitas.gokulpos.tablet.response.ValidatePromotion
import com.varitas.gokulpos.tablet.utilities.ChangeDueCalculator
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OrdersRepositoryImpl(private val api : ApiClient) : OrdersRepository {

    override suspend fun fetchOrderAnalytics(storeId : Int, startDate : String, endDate : String, onSuccess : (response : BaseResponse<OrderAnalytics>) -> Unit, onFailure : (t : Throwable) -> Unit) {
//        api.getOrderAnalysis(startDate, endDate, storeId).enqueue(object : Callback<BaseResponse<OrderAnalytics>> {
//            override fun onResponse(call: Call<BaseResponse<OrderAnalytics>>, response: Response<BaseResponse<OrderAnalytics>>) {
//                response.body()?.let { list ->
//                    onSuccess.invoke(list)
//                    Log.e("ORDERS ANALYSIS DETAILS", list.toString())
//                }
//            }
//
//            override fun onFailure(call: Call<BaseResponse<OrderAnalytics>>, t: Throwable) {
//                onFailure.invoke(t)
//                Log.e("ORDERS ANALYSIS ERROR", t.toString())
//            }
//        })
    }

    override suspend fun fetchHoldOrders(storeId : Int, empId : Int, startDate : String, endDate : String, onSuccess : (response : BaseResponseList<OrderList>) -> Unit, onFailure : (t : Throwable) -> Unit) {
        api.getHoldOrders(storeId = storeId, empId = empId, startDate = startDate, endDate = endDate).enqueue(object : Callback<BaseResponseList<OrderList>> {
            override fun onResponse(call : Call<BaseResponseList<OrderList>>, response : Response<BaseResponseList<OrderList>>) {
                response.body()?.let { list->
                    onSuccess.invoke(list)
                    Log.e("HOLD", list.toString())
                }
            }

            override fun onFailure(call : Call<BaseResponseList<OrderList>>, t : Throwable) {
                onFailure.invoke(t)
                Log.e("HOLD ERROR", t.toString())
            }
        })
    }

    override suspend fun fetchCompleteOrders(storeId : Int, status : Int, startDate : String, endDate : String, onSuccess : (response : BaseResponseList<CompletedOrder>) -> Unit, onFailure : (t : Throwable) -> Unit) {
        api.getCompletedOrders(storeId = storeId, status = status, startDate = startDate, endDate = endDate).enqueue(object : Callback<BaseResponseList<CompletedOrder>> {
            override fun onResponse(call : Call<BaseResponseList<CompletedOrder>>, response : Response<BaseResponseList<CompletedOrder>>) {
                response.body()?.let { list->
                    onSuccess.invoke(list)
                    Log.e("COMPLETED", list.toString())
                }
            }

            override fun onFailure(call : Call<BaseResponseList<CompletedOrder>>, t : Throwable) {
                onFailure.invoke(t)
                Log.e("COMPLETED ERROR", t.toString())
            }
        })
    }

    override suspend fun fetchOrderDetails(id : Int, onSuccess : (response : BaseResponse<OrderDetails>) -> Unit, onFailure : (t : Throwable) -> Unit) {
//        api.getOrderDetails(id).enqueue(object : Callback<BaseResponse<OrderDetails>> {
//            override fun onResponse(call: Call<BaseResponse<OrderDetails>>, response: Response<BaseResponse<OrderDetails>>) {
//                response.body()?.let { list ->
//                    onSuccess.invoke(list)
//                    Log.e("ORDERS DETAILS", list.toString())
//                }
//            }
//
//            override fun onFailure(call: Call<BaseResponse<OrderDetails>>, t: Throwable) {
//                onFailure.invoke(t)
//                Log.e("ORDERS DETAILS ERROR", t.toString())
//            }
//        })
    }

    override suspend fun fetchCurrency(id : Int, onSuccess : (response : BaseResponseList<Currency>) -> Unit, onFailure : (t : Throwable) -> Unit) {
        api.getCurrency(id).enqueue(object : Callback<BaseResponseList<Currency>> {
            override fun onResponse(call : Call<BaseResponseList<Currency>>, response : Response<BaseResponseList<Currency>>) {
                response.body()?.let { list->
                    onSuccess.invoke(list)
                    Log.e("CURRENCY DETAILS", list.toString())
                }
            }

            override fun onFailure(call : Call<BaseResponseList<Currency>>, t : Throwable) {
                onFailure.invoke(t)
                Log.e("CURRENCY ERROR", t.toString())
            }
        })
    }

    override suspend fun openBatch(req : OpenBatchRequest, onSuccess : (response : BaseResponse<OpenBatch>) -> Unit, onFailure : (t : Throwable) -> Unit) {
        api.insertOpenBatch(req).enqueue(object : Callback<BaseResponse<OpenBatch>> {
            override fun onResponse(call : Call<BaseResponse<OpenBatch>>, response : Response<BaseResponse<OpenBatch>>) {
                response.body()?.let { list->
                    onSuccess.invoke(list)
                    Log.e("OPEN BATCH DETAILS", list.toString())
                }
            }

            override fun onFailure(call : Call<BaseResponse<OpenBatch>>, t : Throwable) {
                onFailure.invoke(t)
                Log.e("OPEN BATCH ERROR", t.toString())
            }
        })
    }

    override suspend fun placeOrder(req : OrderInsertRequest, onSuccess : (response : BaseResponse<OrderPlaced>) -> Unit, onFailure : (t : Throwable) -> Unit) {
        api.insertOrder(req).enqueue(object : Callback<BaseResponse<OrderPlaced>> {
            override fun onResponse(call : Call<BaseResponse<OrderPlaced>>, response : Response<BaseResponse<OrderPlaced>>) {
                response.body()?.let { list->
                    onSuccess.invoke(list)
                    Log.e("ORDER PLACE DETAILS", list.toString())
                }
            }

            override fun onFailure(call : Call<BaseResponse<OrderPlaced>>, t : Throwable) {
                onFailure.invoke(t)
                Log.e("ORDER PLACE ERROR", t.toString())
            }
        })
    }

    override suspend fun updateOrder(req : UpdateOrderRequest, onSuccess : (response : BaseResponse<OrderPlaced>) -> Unit, onFailure : (t : Throwable) -> Unit) {
        api.updateOrder(req).enqueue(object : Callback<BaseResponse<OrderPlaced>> {
            override fun onResponse(call : Call<BaseResponse<OrderPlaced>>, response : Response<BaseResponse<OrderPlaced>>) {
                response.body()?.let { list->
                    onSuccess.invoke(list)
                    Log.e("ORDER PLACE UPDATE DETAILS", list.toString())
                }
            }

            override fun onFailure(call : Call<BaseResponse<OrderPlaced>>, t : Throwable) {
                onFailure.invoke(t)
                Log.e("ORDER PLACE UPDATE ERROR", t.toString())
            }
        })
    }

    override suspend fun manageAmount(orderAmount : Double, onSuccess : (response : ArrayList<Double>) -> Unit, onFailure : (t : Throwable) -> Unit) {
        val list = arrayListOf(5.00, 10.00, 20.00, 50.00, 100.00)
        try {
            onSuccess.invoke(ChangeDueCalculator.getFullBillValues(orderAmount))
        } catch(ex : Exception) {
            onSuccess.invoke(list)
        }
    }

    override suspend fun saveDraft(orderId : Int, onSuccess : (response : BaseResponse<OrderPlaced>) -> Unit, onFailure : (t : Throwable) -> Unit) {
        api.saveDraftOrder(orderId).enqueue(object : Callback<BaseResponse<OrderPlaced>> {
            override fun onResponse(call : Call<BaseResponse<OrderPlaced>>, response : Response<BaseResponse<OrderPlaced>>) {
                response.body()?.let { list->
                    onSuccess.invoke(list)
                    Log.e("ORDER DRAFT DETAILS", list.toString())
                }
            }

            override fun onFailure(call : Call<BaseResponse<OrderPlaced>>, t : Throwable) {
                onFailure.invoke(t)
                Log.e("ORDER DRAFT ERROR", t.toString())
            }
        })
    }

    override suspend fun deleteHold(orderId : Int, onSuccess : (response : BaseResponse<OrderPlaced>) -> Unit, onFailure : (t : Throwable) -> Unit) {
        api.deleteHoldOrder(orderId).enqueue(object : Callback<BaseResponse<OrderPlaced>> {
            override fun onResponse(call : Call<BaseResponse<OrderPlaced>>, response : Response<BaseResponse<OrderPlaced>>) {
                response.body()?.let { list->
                    onSuccess.invoke(list)
                    Log.e("DELETE HOLD ORDER DETAILS", list.toString())
                }
            }

            override fun onFailure(call : Call<BaseResponse<OrderPlaced>>, t : Throwable) {
                onFailure.invoke(t)
                Log.e("DELETE HOLD ORDER ERROR", t.toString())
            }
        })
    }

    override suspend fun deletePromotion(id : Int, onSuccess : (response : APIResponse) -> Unit, onFailure : (t : Throwable) -> Unit) {
        api.deletePromotion(id).enqueue(object : Callback<APIResponse> {
            override fun onResponse(call : Call<APIResponse>, response : Response<APIResponse>) {
                response.body()?.let { list->
                    onSuccess.invoke(list)
                    Log.e("DELETE PROMOTION", list.toString())
                }
            }

            override fun onFailure(call : Call<APIResponse>, t : Throwable) {
                onFailure.invoke(t)
                Log.e("DELETE PROMOTION", t.toString())
            }
        })
    }

    override suspend fun deletePurchaseOrder(id : Int, onSuccess : (response : APIResponse) -> Unit, onFailure : (t : Throwable) -> Unit) {
        api.deletePurchaseOrder(id).enqueue(object : Callback<APIResponse> {
            override fun onResponse(call : Call<APIResponse>, response : Response<APIResponse>) {
                response.body()?.let { list->
                    onSuccess.invoke(list)
                    Log.e("DELETE PURCHASE ORDER", list.toString())
                }
            }

            override fun onFailure(call : Call<APIResponse>, t : Throwable) {
                onFailure.invoke(t)
                Log.e("DELETE PURCHASE ORDER", t.toString())
            }
        })
    }

    override suspend fun closeBatch(id : Int, onSuccess : (response : BaseResponse<OrderPlaced>) -> Unit, onFailure : (t : Throwable) -> Unit) {
        api.closeBatch(id).enqueue(object : Callback<BaseResponse<OrderPlaced>> {
            override fun onResponse(call : Call<BaseResponse<OrderPlaced>>, response : Response<BaseResponse<OrderPlaced>>) {
                response.body()?.let { list->
                    onSuccess.invoke(list)
                    Log.e("BATCH CLOSE DETAILS", list.toString())
                }
            }

            override fun onFailure(call : Call<BaseResponse<OrderPlaced>>, t : Throwable) {
                onFailure.invoke(t)
                Log.e("BATCH CLOSE ERROR", t.toString())
            }
        })
    }

    override suspend fun fetchDrawerTransactions(storeId : Int, startDate : String, endDate : String, onSuccess : (response : BaseResponseList<DrawerTransaction>) -> Unit, onFailure : (t : Throwable) -> Unit) {
        api.getDrawerTransactions(storeId, startDate, endDate).enqueue(object : Callback<BaseResponseList<DrawerTransaction>> {
            override fun onResponse(call : Call<BaseResponseList<DrawerTransaction>>, response : Response<BaseResponseList<DrawerTransaction>>) {
                response.body()?.let { list->
                    onSuccess.invoke(list)
                    Log.e("DRAWER TRANSACTION DETAILS", list.toString())
                }
            }

            override fun onFailure(call : Call<BaseResponseList<DrawerTransaction>>, t : Throwable) {
                onFailure.invoke(t)
                Log.e("DRAWER TRANSACTION ERROR", t.toString())
            }
        })
    }

    override suspend fun fetchPurchaseOrders(storeId : Int, status : Int, onSuccess : (response : BaseResponseList<PurchaseOrder>) -> Unit, onFailure : (t : Throwable) -> Unit) {
        api.getPurchaseOrders(storeId, status).enqueue(object : Callback<BaseResponseList<PurchaseOrder>> {
            override fun onResponse(call : Call<BaseResponseList<PurchaseOrder>>, response : Response<BaseResponseList<PurchaseOrder>>) {
                response.body()?.let { list->
                    onSuccess.invoke(list)
                    Log.e("PURCHASE ORDER DETAILS", list.toString())
                }
            }

            override fun onFailure(call : Call<BaseResponseList<PurchaseOrder>>, t : Throwable) {
                onFailure.invoke(t)
                Log.e("PURCHASE ORDER ERROR", t.toString())
            }
        })
    }

    override suspend fun fetchSalesPromotion(storeId : Int, status : Int, onSuccess : (response : BaseResponseList<SalesPromotion>) -> Unit, onFailure : (t : Throwable) -> Unit) {
        api.getPromotions(storeId, status).enqueue(object : Callback<BaseResponseList<SalesPromotion>> {
            override fun onResponse(call : Call<BaseResponseList<SalesPromotion>>, response : Response<BaseResponseList<SalesPromotion>>) {
                response.body()?.let { list->
                    onSuccess.invoke(list)
                    Log.e("SALES PROMOTION DETAILS", list.toString())
                }
            }

            override fun onFailure(call : Call<BaseResponseList<SalesPromotion>>, t : Throwable) {
                onFailure.invoke(t)
                Log.e("SALES PROMOTION ERROR", t.toString())
            }
        })
    }

    override suspend fun fetchRoles(storeId : Int, onSuccess : (response : BaseResponseList<Role>) -> Unit, onFailure : (t : Throwable) -> Unit) {
        api.getRoles(storeId).enqueue(object : Callback<BaseResponseList<Role>> {
            override fun onResponse(call : Call<BaseResponseList<Role>>, response : Response<BaseResponseList<Role>>) {
                response.body()?.let { list->
                    onSuccess.invoke(list)
                    Log.e("ROLE DETAILS", list.toString())
                }
            }

            override fun onFailure(call : Call<BaseResponseList<Role>>, t : Throwable) {
                onFailure.invoke(t)
                Log.e("ROLE ERROR", t.toString())
            }
        })
    }

    override suspend fun insertDrawerTransaction(req : DrawerInsertRequest, onSuccess : (response : APIResponse) -> Unit, onFailure : (t : Throwable) -> Unit) {
        api.insertDrawerTransaction(req).enqueue(object : Callback<APIResponse> {
            override fun onResponse(call : Call<APIResponse>, response : Response<APIResponse>) {
                response.body()?.let { list->
                    onSuccess.invoke(list)
                    Log.e("INSERT DRAWER TRANSACTION DETAILS", list.toString())
                }
            }

            override fun onFailure(call : Call<APIResponse>, t : Throwable) {
                onFailure.invoke(t)
                Log.e("INSERT DRAWER TRANSACTION ERROR", t.toString())
            }
        })
    }

    override suspend fun validatePromotion(req : ValidatePromotion, onSuccess : (response : APIResponse) -> Unit, onFailure : (t : Throwable) -> Unit) {
        api.validateAppliedPromotion(req).enqueue(object : Callback<APIResponse> {
            override fun onResponse(call : Call<APIResponse>, response : Response<APIResponse>) {
                response.body()?.let { list->
                    onSuccess.invoke(list)
                    Log.e("VALIDATE PROMOTION DETAILS", list.toString())
                }
            }

            override fun onFailure(call : Call<APIResponse>, t : Throwable) {
                onFailure.invoke(t)
                Log.e("VALIDATE PROMOTION ERROR", t.toString())
            }
        })
    }

    override suspend fun getTenderList(storeId : Int, onSuccess : (response : BaseResponseList<Tender>) -> Unit, onFailure : (t : Throwable) -> Unit) {
        api.getTenderList(storeId).enqueue(object : Callback<BaseResponseList<Tender>> {
            override fun onResponse(call : Call<BaseResponseList<Tender>>, response : Response<BaseResponseList<Tender>>) {
                response.body()?.let { list->
                    onSuccess.invoke(list)
                    Log.e("TENDER", list.toString())
                }
            }

            override fun onFailure(call : Call<BaseResponseList<Tender>>, t : Throwable) {
                onFailure.invoke(t)
                Log.e("TENDER ERROR", t.toString())
            }
        })
    }

    override suspend fun getStoreList(status : Int, onSuccess : (response : BaseResponseList<Store>) -> Unit, onFailure : (t : Throwable) -> Unit) {
        api.getStoreList(status).enqueue(object : Callback<BaseResponseList<Store>> {
            override fun onResponse(call : Call<BaseResponseList<Store>>, response : Response<BaseResponseList<Store>>) {
                response.body()?.let { list->
                    onSuccess.invoke(list)
                    Log.e("STORE", list.toString())
                }
            }

            override fun onFailure(call : Call<BaseResponseList<Store>>, t : Throwable) {
                onFailure.invoke(t)
                Log.e("TENDER ERROR", t.toString())
            }
        })
    }

    override suspend fun deleteTender(id : Int, storeId : Int, onSuccess : (response : APIResponse) -> Unit, onFailure : (t : Throwable) -> Unit) {
        api.deleteTender(id = id).enqueue(object : Callback<APIResponse> {
            override fun onResponse(call : Call<APIResponse>, response : Response<APIResponse>) {
                response.body()?.let { list->
                    onSuccess.invoke(list)
                    Log.e("DELETE TENDER SUCCESS", list.toString())
                }
            }

            override fun onFailure(call : Call<APIResponse>, t : Throwable) {
                onFailure.invoke(t)
                Log.e("DELETE TENDER SUCCESS", t.toString())
            }
        })
    }

    override suspend fun insertTender(req : Tender, onSuccess : (response : APIResponse) -> Unit, onFailure : (t : Throwable) -> Unit) {
        api.insertTender(req).enqueue(object : Callback<APIResponse> {
            override fun onResponse(call : Call<APIResponse>, response : Response<APIResponse>) {
                response.body()?.let { login->
                    onSuccess.invoke(login)
                    Log.e("INSERT TENDER", login.toString())
                }
            }

            override fun onFailure(call : Call<APIResponse>, t : Throwable) {
                onFailure.invoke(t)
                Log.e("INSERT TENDER ERROR", t.toString())
            }
        })
    }

    override suspend fun getPaymentModeDropDown(onSuccess : (response : BaseResponseList<CommonDropDown>) -> Unit, onFailure : (t : Throwable) -> Unit) {
        api.getPaymentModeDropDown().enqueue(object : Callback<BaseResponseList<CommonDropDown>> {
            override fun onResponse(call : Call<BaseResponseList<CommonDropDown>>, response : Response<BaseResponseList<CommonDropDown>>) {
                response.body()?.let { list->
                    onSuccess.invoke(list)
                    Log.e("PAYMENT MODE DROPDOWN", list.toString())
                }
            }

            override fun onFailure(call : Call<BaseResponseList<CommonDropDown>>, t : Throwable) {
                onFailure.invoke(t)
                Log.e("PAYMENT MODE DROPDOWN ERROR", t.toString())
            }
        })
    }

    override suspend fun getCardTypeDropDown(onSuccess : (response : BaseResponseList<CommonDropDown>) -> Unit, onFailure : (t : Throwable) -> Unit) {
        api.getCardTypeDropDown().enqueue(object : Callback<BaseResponseList<CommonDropDown>> {
            override fun onResponse(call : Call<BaseResponseList<CommonDropDown>>, response : Response<BaseResponseList<CommonDropDown>>) {
                response.body()?.let { list->
                    onSuccess.invoke(list)
                    Log.e("CARD TYPE DROPDOWN", list.toString())
                }
            }

            override fun onFailure(call : Call<BaseResponseList<CommonDropDown>>, t : Throwable) {
                onFailure.invoke(t)
                Log.e("CARD TYPE DROPDOWN ERROR", t.toString())
            }
        })
    }

    override suspend fun getTenderDetail(tenderID : Int, onSuccess : (response : BaseResponse<Tender>) -> Unit, onFailure : (t : Throwable) -> Unit) {
        api.getTenderDetail(tenderID).enqueue(object : Callback<BaseResponse<Tender>> {
            override fun onResponse(call : Call<BaseResponse<Tender>>, response : Response<BaseResponse<Tender>>) {
                response.body()?.let { customerList->
                    onSuccess.invoke(customerList)
                    Log.e("TENDER DETAIL", customerList.toString())
                }
            }

            override fun onFailure(call : Call<BaseResponse<Tender>>, t : Throwable) {
                onFailure.invoke(t)
                Log.e("TENDER DETAIL ERROR", t.toString())
            }
        })
    }

    override suspend fun updateTender(req : Tender, onSuccess : (response : APIResponse) -> Unit, onFailure : (t : Throwable) -> Unit) {
        api.updateTender(req).enqueue(object : Callback<APIResponse> {
            override fun onResponse(call : Call<APIResponse>, response : Response<APIResponse>) {
                response.body()?.let { list->
                    onSuccess.invoke(list)
                    Log.e("UPDATE TENDER", list.toString())
                }
            }

            override fun onFailure(call : Call<APIResponse>, t : Throwable) {
                onFailure.invoke(t)
                Log.e("UPDATE TENDER ERROR", t.toString())
            }

        })
    }

    override suspend fun insertRole(req : Role, onSuccess : (response : APIResponse) -> Unit, onFailure : (t : Throwable) -> Unit) {
        api.insertRole(req).enqueue(object : Callback<APIResponse> {
            override fun onResponse(call : Call<APIResponse>, response : Response<APIResponse>) {
                response.body()?.let { login->
                    onSuccess.invoke(login)
                    Log.e("INSERT ROLE", login.toString())
                }
            }

            override fun onFailure(call : Call<APIResponse>, t : Throwable) {
                onFailure.invoke(t)
                Log.e("INSERT ROLE ERROR", t.toString())
            }
        })
    }

    override suspend fun getRoleDetail(roleID : Int, onSuccess : (response : BaseResponse<Role>) -> Unit, onFailure : (t : Throwable) -> Unit) {
        api.getRoleDetail(roleID).enqueue(object : Callback<BaseResponse<Role>> {
            override fun onResponse(call : Call<BaseResponse<Role>>, response : Response<BaseResponse<Role>>) {
                response.body()?.let { customerList->
                    onSuccess.invoke(customerList)
                    Log.e("TENDER DETAIL", customerList.toString())
                }
            }

            override fun onFailure(call : Call<BaseResponse<Role>>, t : Throwable) {
                onFailure.invoke(t)
                Log.e("TENDER DETAIL ERROR", t.toString())
            }
        })
    }

    override suspend fun updateRole(req : Role, onSuccess : (response : APIResponse) -> Unit, onFailure : (t : Throwable) -> Unit) {
        api.updateRole(req).enqueue(object : Callback<APIResponse> {
            override fun onResponse(call : Call<APIResponse>, response : Response<APIResponse>) {
                response.body()?.let { list->
                    onSuccess.invoke(list)
                    Log.e("UPDATE TENDER", list.toString())
                }
            }

            override fun onFailure(call : Call<APIResponse>, t : Throwable) {
                onFailure.invoke(t)
                Log.e("UPDATE TENDER ERROR", t.toString())
            }

        })
    }

    override suspend fun getPromotionTypeDropDown(onSuccess : (response : BaseResponseList<CommonDropDown>) -> Unit, onFailure : (t : Throwable) -> Unit) {
        api.getPromotionTypeDropDown().enqueue(object : Callback<BaseResponseList<CommonDropDown>> {
            override fun onResponse(call : Call<BaseResponseList<CommonDropDown>>, response : Response<BaseResponseList<CommonDropDown>>) {
                response.body()?.let { list->
                    onSuccess.invoke(list)
                    Log.e("PROMOTION TYPE DROPDOWN", list.toString())
                }
            }

            override fun onFailure(call : Call<BaseResponseList<CommonDropDown>>, t : Throwable) {
                onFailure.invoke(t)
                Log.e("PROMOTION TYPE DROPDOWN ERROR", t.toString())
            }
        })
    }

    override suspend fun getCommonGroupDropDown(type : Int, storeId : Int, onSuccess : (response : BaseResponseList<CommonDropDown>) -> Unit, onFailure : (t : Throwable) -> Unit) {
        api.getCommonGroupDropDown(type = type, storeId = storeId).enqueue(object : Callback<BaseResponseList<CommonDropDown>> {
            override fun onResponse(call : Call<BaseResponseList<CommonDropDown>>, response : Response<BaseResponseList<CommonDropDown>>) {
                response.body()?.let { list->
                    onSuccess.invoke(list)
                    Log.e("PROMOTION TYPE DROPDOWN", list.toString())
                }
            }

            override fun onFailure(call : Call<BaseResponseList<CommonDropDown>>, t : Throwable) {
                onFailure.invoke(t)
                Log.e("PROMOTION TYPE DROPDOWN ERROR", t.toString())
            }
        })
    }

    override suspend fun insertSalesPromotion(req : SalesPromotion, onSuccess : (response : APIResponse) -> Unit, onFailure : (t : Throwable) -> Unit) {
        api.insertSalesPromotion(req).enqueue(object : Callback<APIResponse> {
            override fun onResponse(call : Call<APIResponse>, response : Response<APIResponse>) {
                response.body()?.let { login->
                    onSuccess.invoke(login)
                    Log.e("INSERT SALES PROMOTION", login.toString())
                }
            }

            override fun onFailure(call: Call<APIResponse>, t: Throwable) {
                onFailure.invoke(t)
                Log.e("INSERT SALES PROMOTION ERROR", t.toString())
            }
        })
    }

    override suspend fun insertItemToPromotion(req: AddItemToDiscount, onSuccess: (response: APIResponse) -> Unit, onFailure: (t: Throwable) -> Unit) {
        api.insertItemToPromotion(req).enqueue(object : Callback<APIResponse> {
            override fun onResponse(call: Call<APIResponse>, response: Response<APIResponse>) {
                response.body()?.let { login ->
                    onSuccess.invoke(login)
                    Log.e("INSERT ITEMS TO PROMOTION", login.toString())
                }
            }

            override fun onFailure(call: Call<APIResponse>, t: Throwable) {
                onFailure.invoke(t)
                Log.e("INSERT ITEMS TO PROMOTION ERROR", t.toString())
            }
        })
    }

    override suspend fun getSalesPromotionDetail(discountId: Int, onSuccess: (response: BaseResponse<SalesPromotion>) -> Unit, onFailure: (t: Throwable) -> Unit) {
        api.getSalesPromotionDetail(discountId).enqueue(object : Callback<BaseResponse<SalesPromotion>> {
            override fun onResponse(call: Call<BaseResponse<SalesPromotion>>, response: Response<BaseResponse<SalesPromotion>>) {
                response.body()?.let { customerList ->
                    onSuccess.invoke(customerList)
                    Log.e("SALE PROMOTION DETAIL", customerList.toString())
                }
            }

            override fun onFailure(call : Call<BaseResponse<SalesPromotion>>, t : Throwable) {
                onFailure.invoke(t)
                Log.e("TENDER DETAIL ERROR", t.toString())
            }
        })
    }

    override suspend fun updateSalesPromotion(req : SalesPromotion, onSuccess : (response : APIResponse) -> Unit, onFailure : (t : Throwable) -> Unit) {
        api.updateSalesPromotion(req).enqueue(object : Callback<APIResponse> {
            override fun onResponse(call : Call<APIResponse>, response : Response<APIResponse>) {
                response.body()?.let { list->
                    onSuccess.invoke(list)
                    Log.e("UPDATE SALES PROMOTION", list.toString())
                }
            }

            override fun onFailure(call : Call<APIResponse>, t : Throwable) {
                onFailure.invoke(t)
                Log.e("UPDATE SALES PROMOTION ERROR", t.toString())
            }

        })
    }

    override suspend fun insertPurchaseOrder(req : PurchaseOrder, onSuccess : (response : APIResponse) -> Unit, onFailure : (t : Throwable) -> Unit) {
        api.insertPurchaseOrder(req).enqueue(object : Callback<APIResponse> {
            override fun onResponse(call : Call<APIResponse>, response : Response<APIResponse>) {
                response.body()?.let { insert->
                    onSuccess.invoke(insert)
                    Log.e("INSERT PURCHASE ORDER", insert.toString())
                }
            }

            override fun onFailure(call : Call<APIResponse>, t : Throwable) {
                onFailure.invoke(t)
                Log.e("INSERT PURCHASE ORDER ERROR", t.toString())
            }
        })
    }

    override suspend fun getPurchaseOrderDetail(purchaseID : Int, onSuccess : (response : BaseResponse<PurchaseOrder>) -> Unit, onFailure : (t : Throwable) -> Unit) {
        api.getPurchaseOrderDetail(purchaseID).enqueue(object : Callback<BaseResponse<PurchaseOrder>> {
            override fun onResponse(call : Call<BaseResponse<PurchaseOrder>>, response : Response<BaseResponse<PurchaseOrder>>) {
                response.body()?.let { list->
                    onSuccess.invoke(list)
                    Log.e("PURCHASE ORDER DETAIL", list.toString())
                }
            }

            override fun onFailure(call: Call<BaseResponse<PurchaseOrder>>, t: Throwable) {
                onFailure.invoke(t)
                Log.e("PURCHASE ORDER DETAIL ERROR", t.toString())
            }
        })
    }

    override suspend fun fetchAllOrderList(employeeId: Int, storeId: Int, startDate: String, endDate: String, onSuccess: (response: BaseResponseList<AllOrders>) -> Unit, onFailure: (t: Throwable) -> Unit) {
        api.getAllReceipts(employeeId, storeId, startDate, endDate).enqueue(object : Callback<BaseResponseList<AllOrders>> {
            override fun onResponse(call: Call<BaseResponseList<AllOrders>>, response: Response<BaseResponseList<AllOrders>>) {
                response.body()?.let { list ->
                    onSuccess.invoke(list)
                    Log.e("ALL ORDER DETAIL", list.toString())
                }
            }

            override fun onFailure(call: Call<BaseResponseList<AllOrders>>, t: Throwable) {
                onFailure.invoke(t)
                Log.e("ALL ORDER DETAIL ERROR", t.toString())
            }
        })
    }

    override suspend fun fetchLastOrder(employeeId: Int, onSuccess: (response: BaseResponse<AllOrders>) -> Unit, onFailure: (t: Throwable) -> Unit) {
        api.getLastPrint(employeeId).enqueue(object : Callback<BaseResponse<AllOrders>> {
            override fun onResponse(call: Call<BaseResponse<AllOrders>>, response: Response<BaseResponse<AllOrders>>) {
                response.body()?.let { list ->
                    onSuccess.invoke(list)
                    Log.e("LAST ORDER DETAIL", list.toString())
                }
            }

            override fun onFailure(call: Call<BaseResponse<AllOrders>>, t: Throwable) {
                onFailure.invoke(t)
                Log.e("LAST ORDER DETAIL ERROR", t.toString())
            }
        })
    }

    override suspend fun updatePurchaseOrder(req : PurchaseOrder, onSuccess : (response : APIResponse) -> Unit, onFailure : (t : Throwable) -> Unit) {
        api.updatePurchaseOrder(req).enqueue(object : Callback<APIResponse> {
            override fun onResponse(call : Call<APIResponse>, response : Response<APIResponse>) {
                response.body()?.let { update->
                    onSuccess.invoke(update)
                    Log.e("UPDATE PURCHASE ORDER", update.toString())
                }
            }

            override fun onFailure(call : Call<APIResponse>, t : Throwable) {
                onFailure.invoke(t)
                Log.e("UPDATE PURCHASE ORDER ERROR", t.toString())
            }
        })
    }

    override suspend fun updateStoreStatus(req: StoreUpdate, onSuccess: (response: APIResponse) -> Unit, onFailure: (t: Throwable) -> Unit) {
        api.updateStoreStatus(req).enqueue(object : Callback<APIResponse> {
            override fun onResponse(call : Call<APIResponse>, response : Response<APIResponse>) {
                response.body()?.let { update->
                    onSuccess.invoke(update)
                    Log.e("UPDATE STORE STATUS", update.toString())
                }
            }

            override fun onFailure(call: Call<APIResponse>, t: Throwable) {
                onFailure.invoke(t)
                Log.e("UPDATE STORE STATUS ERROR", t.toString())
            }
        })
    }

    override suspend fun fetchOpenDiscount(storeId: Int, onSuccess: (response: BaseResponseList<SalesPromotion>) -> Unit, onFailure: (t: Throwable) -> Unit) {
        api.getOpenDiscount(storeId).enqueue(object : Callback<BaseResponseList<SalesPromotion>> {
            override fun onResponse(call : Call<BaseResponseList<SalesPromotion>>, response : Response<BaseResponseList<SalesPromotion>>) {
                response.body()?.let { list->
                    onSuccess.invoke(list)
                    Log.e("OPEN DISCOUNT DETAILS", list.toString())
                }
            }

            override fun onFailure(call : Call<BaseResponseList<SalesPromotion>>, t : Throwable) {
                onFailure.invoke(t)
                Log.e("OPEN DISCOUNT ERROR", t.toString())
            }
        })
    }

    override suspend fun generateInvoice(orderId : Int, onSuccess : (response : APIResponse) -> Unit, onFailure : (t : Throwable) -> Unit) {
        api.generateInvoice(orderId).enqueue(object : Callback<APIResponse> {
            override fun onResponse(call : Call<APIResponse>, response : Response<APIResponse>) {
                response.body()?.let { insert->
                    onSuccess.invoke(insert)
                    Log.e("GENERATE INVOICE ORDER", insert.toString())
                }
            }

            override fun onFailure(call : Call<APIResponse>, t : Throwable) {
                onFailure.invoke(t)
                Log.e("GENERATE INVOICE ERROR", t.toString())
            }
        })
    }

    override suspend fun getInvoiceList(storeId : Int, status : Int, onSuccess : (response : BaseResponseList<Invoice>) -> Unit, onFailure : (t : Throwable) -> Unit) {
        api.getInvoiceList(storeId = storeId, status = status).enqueue(object : Callback<BaseResponseList<Invoice>> {
            override fun onResponse(call : Call<BaseResponseList<Invoice>>, response : Response<BaseResponseList<Invoice>>) {
                response.body()?.let { list->
                    onSuccess.invoke(list)
                    Log.e("INVOICE", list.toString())
                }
            }

            override fun onFailure(call : Call<BaseResponseList<Invoice>>, t : Throwable) {
                onFailure.invoke(t)
                Log.e("INVOICE ERROR", t.toString())
            }
        })
    }

    override suspend fun deleteInvoice(id : Int, onSuccess : (response : APIResponse) -> Unit, onFailure : (t : Throwable) -> Unit) {
        api.deleteInvoice(id = id).enqueue(object : Callback<APIResponse> {
            override fun onResponse(call : Call<APIResponse>, response : Response<APIResponse>) {
                response.body()?.let { delete->
                    onSuccess.invoke(delete)
                    Log.e("DELETE INVOICE SUCCESS", delete.toString())
                }
            }

            override fun onFailure(call : Call<APIResponse>, t : Throwable) {
                onFailure.invoke(t)
                Log.e("DELETE INVOICE SUCCESS", t.toString())
            }
        })
    }

    override suspend fun getAutoPurchaseOrderList(storeId : Int, search : String, onSuccess : (response : BaseResponseList<PurchaseOrder>) -> Unit, onFailure : (t : Throwable) -> Unit) {
        api.getAutoPurchaseOrderList(storeId = storeId, msg = search).enqueue(object : Callback<BaseResponseList<PurchaseOrder>> {
            override fun onResponse(call : Call<BaseResponseList<PurchaseOrder>>, response : Response<BaseResponseList<PurchaseOrder>>) {
                response.body()?.let { list->
                    onSuccess.invoke(list)
                    Log.e("AUTO COMPLETE PURCHASE ORDER", list.toString())
                }
            }

            override fun onFailure(call : Call<BaseResponseList<PurchaseOrder>>, t : Throwable) {
                onFailure.invoke(t)
                Log.e("AUTO COMPLETE PURCHASE ORDER ERROR", t.toString())
            }
        })
    }

    override suspend fun insertInvoice(req : Invoice, onSuccess : (response : APIResponse) -> Unit, onFailure : (t : Throwable) -> Unit) {
        api.insertInvoice(req).enqueue(object : Callback<APIResponse> {
            override fun onResponse(call : Call<APIResponse>, response : Response<APIResponse>) {
                response.body()?.let { insert->
                    onSuccess.invoke(insert)
                    Log.e("INSERT INVOICE", insert.toString())
                }
            }

            override fun onFailure(call : Call<APIResponse>, t : Throwable) {
                onFailure.invoke(t)
                Log.e("INSERT INVOICE ERROR", t.toString())
            }
        })
    }

    override suspend fun getInvoiceDetail(purchaseID : Int, onSuccess : (response : BaseResponse<Invoice>) -> Unit, onFailure : (t : Throwable) -> Unit) {
        api.getInvoiceDetail(purchaseID).enqueue(object : Callback<BaseResponse<Invoice>> {
            override fun onResponse(call : Call<BaseResponse<Invoice>>, response : Response<BaseResponse<Invoice>>) {
                response.body()?.let { list->
                    onSuccess.invoke(list)
                    Log.e("INVOICE DETAIL", list.toString())
                }
            }

            override fun onFailure(call : Call<BaseResponse<Invoice>>, t : Throwable) {
                onFailure.invoke(t)
                Log.e("INVOICE DETAIL ERROR", t.toString())
            }
        })
    }

    override suspend fun updateInvoice(req : Invoice, onSuccess : (response : APIResponse) -> Unit, onFailure : (t : Throwable) -> Unit) {
        api.updateInvoice(req).enqueue(object : Callback<APIResponse> {
            override fun onResponse(call : Call<APIResponse>, response : Response<APIResponse>) {
                response.body()?.let { update->
                    onSuccess.invoke(update)
                    Log.e("UPDATE INVOICE", update.toString())
                }
            }

            override fun onFailure(call : Call<APIResponse>, t : Throwable) {
                onFailure.invoke(t)
                Log.e("UPDATE INVOICE ERROR", t.toString())
            }
        })
    }
}