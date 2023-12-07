package com.varitas.gokulpos.repositories

import android.util.Log
import com.varitas.gokulpos.api.ApiClient
import com.varitas.gokulpos.response.APIResponse
import com.varitas.gokulpos.response.BaseResponse
import com.varitas.gokulpos.response.BaseResponseList
import com.varitas.gokulpos.response.CommonDropDown
import com.varitas.gokulpos.response.Invoice
import com.varitas.gokulpos.response.OrderAnalytics
import com.varitas.gokulpos.response.OrderList
import com.varitas.gokulpos.response.PurchaseOrder
import com.varitas.gokulpos.response.Store
import com.varitas.gokulpos.response.StoreStatus
import com.varitas.gokulpos.response.Tender
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

    override suspend fun fetchOrders(status : Int, storeId : Int, startDate : String, endDate : String, onSuccess : (response : BaseResponseList<OrderList>) -> Unit, onFailure : (t : Throwable) -> Unit) {
        api.getOrderList(status = status, storeId = storeId, startDate = startDate, endDate = endDate).enqueue(object : Callback<BaseResponseList<OrderList>> {
            override fun onResponse(call : Call<BaseResponseList<OrderList>>, response : Response<BaseResponseList<OrderList>>) {
                response.body()?.let { list->
                    onSuccess.invoke(list)
                    Log.e("ORDERS", list.toString())
                }
            }

            override fun onFailure(call : Call<BaseResponseList<OrderList>>, t : Throwable) {
                onFailure.invoke(t)
                Log.e("ORDERS ERROR", t.toString())
            }
        })
    }

    override suspend fun fetchOrderDetails(orderId : Int, onSuccess : (response : BaseResponse<OrderList>) -> Unit, onFailure : (t : Throwable) -> Unit) {
        api.getOrderDetails(orderId = orderId).enqueue(object : Callback<BaseResponse<OrderList>> {
            override fun onResponse(call : Call<BaseResponse<OrderList>>, response : Response<BaseResponse<OrderList>>) {
                response.body()?.let { list->
                    onSuccess.invoke(list)
                    Log.e("ORDERS DETAILS", list.toString())
                }
            }

            override fun onFailure(call : Call<BaseResponse<OrderList>>, t : Throwable) {
                onFailure.invoke(t)
                Log.e("ORDERS DETAILS ERROR", t.toString())
            }
        })
    }

    override suspend fun getCustomerOrder(customerID : Int, startDate : String, endDate : String, onSuccess : (response : BaseResponseList<OrderList>) -> Unit, onFailure : (t : Throwable) -> Unit) {
        api.getCustomerOrder(customerId = customerID, startDate = startDate, endDate = endDate).enqueue(object : Callback<BaseResponseList<OrderList>> {
            override fun onResponse(call : Call<BaseResponseList<OrderList>>, response : Response<BaseResponseList<OrderList>>) {
                response.body()?.let { list->
                    onSuccess.invoke(list)
                    Log.e("CUSTOMER ORDERS", list.toString())
                }
            }

            override fun onFailure(call : Call<BaseResponseList<OrderList>>, t : Throwable) {
                onFailure.invoke(t)
                Log.e("CUSTOMER ORDERS ERROR", t.toString())
            }
        })
    }

    override suspend fun getEmployeeOrder(userId : Int, startDate : String, endDate : String, onSuccess : (response : BaseResponseList<OrderList>) -> Unit, onFailure : (t : Throwable) -> Unit) {
        api.getEmployeeOrder(userID = userId, startDate = startDate, endDate = endDate).enqueue(object : Callback<BaseResponseList<OrderList>> {
            override fun onResponse(call : Call<BaseResponseList<OrderList>>, response : Response<BaseResponseList<OrderList>>) {
                response.body()?.let { list->
                    onSuccess.invoke(list)
                    Log.e("EMPLOYEE ORDERS", list.toString())
                }
            }

            override fun onFailure(call : Call<BaseResponseList<OrderList>>, t : Throwable) {
                onFailure.invoke(t)
                Log.e("EMPLOYEE ORDERS ERROR", t.toString())
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

    override suspend fun getPurchaseOrderList(storeId : Int, status : Int, onSuccess : (response : BaseResponseList<PurchaseOrder>) -> Unit, onFailure : (t : Throwable) -> Unit) {
        api.getPurchaseOrderList(storeId = storeId, status = status).enqueue(object : Callback<BaseResponseList<PurchaseOrder>> {
            override fun onResponse(call : Call<BaseResponseList<PurchaseOrder>>, response : Response<BaseResponseList<PurchaseOrder>>) {
                response.body()?.let { list->
                    onSuccess.invoke(list)
                    Log.e("PURCHASE ORDER", list.toString())
                }
            }

            override fun onFailure(call : Call<BaseResponseList<PurchaseOrder>>, t : Throwable) {
                onFailure.invoke(t)
                Log.e("PURCHASE ORDER ERROR", t.toString())
            }
        })
    }

    override suspend fun deletePurchaseOrder(id : Int, onSuccess : (response : APIResponse) -> Unit, onFailure : (t : Throwable) -> Unit) {
        api.deletePurchaseOrder(id = id).enqueue(object : Callback<APIResponse> {
            override fun onResponse(call : Call<APIResponse>, response : Response<APIResponse>) {
                response.body()?.let { delete->
                    onSuccess.invoke(delete)
                    Log.e("DELETE PURCHASE ORDER SUCCESS", delete.toString())
                }
            }

            override fun onFailure(call : Call<APIResponse>, t : Throwable) {
                onFailure.invoke(t)
                Log.e("DELETE PURCHASE ORDER SUCCESS", t.toString())
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

            override fun onFailure(call : Call<BaseResponse<PurchaseOrder>>, t : Throwable) {
                onFailure.invoke(t)
                Log.e("PURCHASE ORDER DETAIL ERROR", t.toString())
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

    override suspend fun getInvoiceList(storeId : Int, onSuccess : (response : BaseResponseList<Invoice>) -> Unit, onFailure : (t : Throwable) -> Unit) {
        api.getInvoiceList(storeId).enqueue(object : Callback<BaseResponseList<Invoice>> {
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

    override suspend fun fetchAutoCompletePurchaseOrder(storeId : Int, search : String, onSuccess : (response : BaseResponseList<PurchaseOrder>) -> Unit, onFailure : (t : Throwable) -> Unit) {
        api.getAutoCompletePurchaseOrder(storeId = storeId, msg = search).enqueue(object : Callback<BaseResponseList<PurchaseOrder>> {
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
                Log.e("STORE ERROR", t.toString())
            }
        })
    }

    override suspend fun updateStoreStatus(req : StoreStatus, onSuccess : (response : APIResponse) -> Unit, onFailure : (t : Throwable) -> Unit) {
        api.updateStoreStatus(req).enqueue(object : Callback<APIResponse> {
            override fun onResponse(call : Call<APIResponse>, response : Response<APIResponse>) {
                response.body()?.let { update->
                    onSuccess.invoke(update)
                    Log.e("UPDATE STORE STATUS", update.toString())
                }
            }

            override fun onFailure(call : Call<APIResponse>, t : Throwable) {
                onFailure.invoke(t)
                Log.e("UPDATE STORE STATUS ERROR", t.toString())
            }
        })
    }

    override suspend fun fetchAllOrderList(employeeId : Int, storeId : Int,  startDate : String, endDate : String, status : Int, onSuccess : (response : BaseResponseList<OrderList>) -> Unit, onFailure : (t : Throwable) -> Unit) {
        api.getLastOrder(empId = employeeId, storeId = storeId, startDate = startDate, endDate = endDate, status = status).enqueue(object : Callback<BaseResponseList<OrderList>> {
            override fun onResponse(call : Call<BaseResponseList<OrderList>>, response : Response<BaseResponseList<OrderList>>) {
                response.body()?.let { list->
                    onSuccess.invoke(list)
                    Log.e("LAST ORDER DETAIL", list.toString())
                }
            }

            override fun onFailure(call : Call<BaseResponseList<OrderList>>, t : Throwable) {
                onFailure.invoke(t)
                Log.e("LAST ORDER DETAIL ERROR", t.toString())
            }
        })
    }

    override suspend fun getTenderDropDown(storeId : Int, onSuccess : (response : BaseResponseList<CommonDropDown>) -> Unit, onFailure : (t : Throwable) -> Unit) {
        api.getTenderDropDown(storeId).enqueue(object : Callback<BaseResponseList<CommonDropDown>> {
            override fun onResponse(call : Call<BaseResponseList<CommonDropDown>>, response : Response<BaseResponseList<CommonDropDown>>) {
                response.body()?.let { list->
                    onSuccess.invoke(list)
                    Log.e("TENDER DROPDOWN", list.toString())
                }
            }

            override fun onFailure(call : Call<BaseResponseList<CommonDropDown>>, t : Throwable) {
                onFailure.invoke(t)
                Log.e("TENDER DROPDOWN ERROR", t.toString())
            }
        })
    }
}