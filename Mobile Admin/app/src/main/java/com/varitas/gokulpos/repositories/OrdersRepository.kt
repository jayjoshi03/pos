package com.varitas.gokulpos.repositories

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


interface OrdersRepository {

    suspend fun fetchOrderAnalytics(storeId : Int, startDate : String, endDate : String, onSuccess : (response : BaseResponse<OrderAnalytics>) -> Unit, onFailure : (t : Throwable) -> Unit)
    suspend fun fetchOrders(status : Int, storeId : Int, startDate : String, endDate : String, onSuccess : (response : BaseResponseList<OrderList>) -> Unit, onFailure : (t : Throwable) -> Unit)
    suspend fun fetchOrderDetails(orderId : Int, onSuccess : (response : BaseResponse<OrderList>) -> Unit, onFailure : (t : Throwable) -> Unit)
    suspend fun getCustomerOrder(customerID : Int, startDate : String, endDate : String, onSuccess : (response : BaseResponseList<OrderList>) -> Unit, onFailure : (t : Throwable) -> Unit)
    suspend fun getEmployeeOrder(userId : Int, startDate : String, endDate : String, onSuccess : (response : BaseResponseList<OrderList>) -> Unit, onFailure : (t : Throwable) -> Unit)
    suspend fun getTenderList(storeId : Int, onSuccess : (response : BaseResponseList<Tender>) -> Unit, onFailure : (t : Throwable) -> Unit)
    suspend fun deleteTender(id : Int, storeId : Int, onSuccess : (response : APIResponse) -> Unit, onFailure : (t : Throwable) -> Unit)
    suspend fun insertTender(req : Tender, onSuccess : (response : APIResponse) -> Unit, onFailure : (t : Throwable) -> Unit)
    suspend fun getPaymentModeDropDown(onSuccess : (response : BaseResponseList<CommonDropDown>) -> Unit, onFailure : (t : Throwable) -> Unit)
    suspend fun getCardTypeDropDown(onSuccess : (response : BaseResponseList<CommonDropDown>) -> Unit, onFailure : (t : Throwable) -> Unit)
    suspend fun getTenderDetail(tenderID : Int, onSuccess : (response : BaseResponse<Tender>) -> Unit, onFailure : (t : Throwable) -> Unit)
    suspend fun updateTender(req : Tender, onSuccess : (response : APIResponse) -> Unit, onFailure : (t : Throwable) -> Unit)
    suspend fun getPurchaseOrderList(storeId : Int, status : Int, onSuccess : (response : BaseResponseList<PurchaseOrder>) -> Unit, onFailure : (t : Throwable) -> Unit)
    suspend fun deletePurchaseOrder(id : Int, onSuccess : (response : APIResponse) -> Unit, onFailure : (t : Throwable) -> Unit)
    suspend fun insertPurchaseOrder(req : PurchaseOrder, onSuccess : (response : APIResponse) -> Unit, onFailure : (t : Throwable) -> Unit)
    suspend fun getPurchaseOrderDetail(purchaseID : Int, onSuccess : (response : BaseResponse<PurchaseOrder>) -> Unit, onFailure : (t : Throwable) -> Unit)
    suspend fun updatePurchaseOrder(req : PurchaseOrder, onSuccess : (response : APIResponse) -> Unit, onFailure : (t : Throwable) -> Unit)
    suspend fun generateInvoice(orderId : Int, onSuccess : (response : APIResponse) -> Unit, onFailure : (t : Throwable) -> Unit)
    suspend fun getInvoiceList(storeId : Int, onSuccess : (response : BaseResponseList<Invoice>) -> Unit, onFailure : (t : Throwable) -> Unit)
    suspend fun deleteInvoice(id : Int, onSuccess : (response : APIResponse) -> Unit, onFailure : (t : Throwable) -> Unit)
    suspend fun fetchAutoCompletePurchaseOrder(storeId : Int, search : String, onSuccess : (response : BaseResponseList<PurchaseOrder>) -> Unit, onFailure : (t : Throwable) -> Unit)
    suspend fun insertInvoice(req : Invoice, onSuccess : (response : APIResponse) -> Unit, onFailure : (t : Throwable) -> Unit)
    suspend fun getInvoiceDetail(purchaseID : Int, onSuccess : (response : BaseResponse<Invoice>) -> Unit, onFailure : (t : Throwable) -> Unit)
    suspend fun updateInvoice(req : Invoice, onSuccess : (response : APIResponse) -> Unit, onFailure : (t : Throwable) -> Unit)
    suspend fun getStoreList(status : Int, onSuccess : (response : BaseResponseList<Store>) -> Unit, onFailure : (t : Throwable) -> Unit)
    suspend fun updateStoreStatus(req : StoreStatus, onSuccess : (response : APIResponse) -> Unit, onFailure : (t : Throwable) -> Unit)
    suspend fun fetchAllOrderList(employeeId : Int, storeId : Int, startDate : String, endDate : String,  status : Int, onSuccess : (response : BaseResponseList<OrderList>) -> Unit, onFailure : (t : Throwable) -> Unit)
    suspend fun getTenderDropDown(storeId : Int, onSuccess : (response : BaseResponseList<CommonDropDown>) -> Unit, onFailure : (t : Throwable) -> Unit)
}