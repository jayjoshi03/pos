package com.varitas.gokulpos.tablet.repositories

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


interface OrdersRepository {

    suspend fun fetchOrderAnalytics(storeId : Int, startDate : String, endDate : String, onSuccess : (response : BaseResponse<OrderAnalytics>) -> Unit, onFailure : (t : Throwable) -> Unit)
    suspend fun fetchHoldOrders(storeId : Int, empId : Int, startDate : String, endDate : String, onSuccess : (response : BaseResponseList<OrderList>) -> Unit, onFailure : (t : Throwable) -> Unit)
    suspend fun fetchCompleteOrders(storeId : Int, status : Int, startDate : String, endDate : String, onSuccess : (response : BaseResponseList<CompletedOrder>) -> Unit, onFailure : (t : Throwable) -> Unit)
    suspend fun fetchOrderDetails(id : Int, onSuccess : (response : BaseResponse<OrderDetails>) -> Unit, onFailure : (t : Throwable) -> Unit)
    suspend fun fetchCurrency(id : Int, onSuccess : (response : BaseResponseList<Currency>) -> Unit, onFailure : (t : Throwable) -> Unit)
    suspend fun openBatch(req : OpenBatchRequest, onSuccess : (response : BaseResponse<OpenBatch>) -> Unit, onFailure : (t : Throwable) -> Unit)
    suspend fun placeOrder(req : OrderInsertRequest, onSuccess : (response : BaseResponse<OrderPlaced>) -> Unit, onFailure : (t : Throwable) -> Unit)
    suspend fun updateOrder(req : UpdateOrderRequest, onSuccess : (response : BaseResponse<OrderPlaced>) -> Unit, onFailure : (t : Throwable) -> Unit)
    suspend fun manageAmount(orderAmount : Double, onSuccess : (response : ArrayList<Double>) -> Unit, onFailure : (t : Throwable) -> Unit)
    suspend fun saveDraft(orderId : Int, onSuccess : (response : BaseResponse<OrderPlaced>) -> Unit, onFailure : (t : Throwable) -> Unit)
    suspend fun deleteHold(orderId : Int, onSuccess : (response : BaseResponse<OrderPlaced>) -> Unit, onFailure : (t : Throwable) -> Unit)
    suspend fun deletePromotion(id : Int, onSuccess : (response : APIResponse) -> Unit, onFailure : (t : Throwable) -> Unit)
    suspend fun deletePurchaseOrder(id : Int, onSuccess : (response : APIResponse) -> Unit, onFailure : (t : Throwable) -> Unit)
    suspend fun closeBatch(id : Int, onSuccess : (response : BaseResponse<OrderPlaced>) -> Unit, onFailure : (t : Throwable) -> Unit)
    suspend fun fetchDrawerTransactions(storeId : Int, startDate : String, endDate : String, onSuccess : (response : BaseResponseList<DrawerTransaction>) -> Unit, onFailure : (t : Throwable) -> Unit)
    suspend fun fetchPurchaseOrders(storeId : Int, status : Int, onSuccess : (response : BaseResponseList<PurchaseOrder>) -> Unit, onFailure : (t : Throwable) -> Unit)
    suspend fun fetchSalesPromotion(storeId : Int, status : Int, onSuccess : (response : BaseResponseList<SalesPromotion>) -> Unit, onFailure : (t : Throwable) -> Unit)
    suspend fun fetchRoles(storeId : Int, onSuccess : (response : BaseResponseList<Role>) -> Unit, onFailure : (t : Throwable) -> Unit)
    suspend fun insertDrawerTransaction(req : DrawerInsertRequest, onSuccess : (response : APIResponse) -> Unit, onFailure : (t : Throwable) -> Unit)
    suspend fun validatePromotion(req : ValidatePromotion, onSuccess : (response : APIResponse) -> Unit, onFailure : (t : Throwable) -> Unit)
    suspend fun getTenderList(storeId : Int, onSuccess : (response : BaseResponseList<Tender>) -> Unit, onFailure : (t : Throwable) -> Unit)
    suspend fun getStoreList(status : Int, onSuccess : (response : BaseResponseList<Store>) -> Unit, onFailure : (t : Throwable) -> Unit)
    suspend fun deleteTender(id : Int, storeId : Int, onSuccess : (response : APIResponse) -> Unit, onFailure : (t : Throwable) -> Unit)
    suspend fun insertTender(req : Tender, onSuccess : (response : APIResponse) -> Unit, onFailure : (t : Throwable) -> Unit)
    suspend fun getPaymentModeDropDown(onSuccess : (response : BaseResponseList<CommonDropDown>) -> Unit, onFailure : (t : Throwable) -> Unit)
    suspend fun getCardTypeDropDown(onSuccess : (response : BaseResponseList<CommonDropDown>) -> Unit, onFailure : (t : Throwable) -> Unit)
    suspend fun getTenderDetail(tenderID : Int, onSuccess : (response : BaseResponse<Tender>) -> Unit, onFailure : (t : Throwable) -> Unit)
    suspend fun updateTender(req : Tender, onSuccess : (response : APIResponse) -> Unit, onFailure : (t : Throwable) -> Unit)
    suspend fun insertRole(req : Role, onSuccess : (response : APIResponse) -> Unit, onFailure : (t : Throwable) -> Unit)
    suspend fun getRoleDetail(roleID: Int, onSuccess: (response: BaseResponse<Role>) -> Unit, onFailure: (t: Throwable) -> Unit)
    suspend fun updateRole(req: Role, onSuccess: (response: APIResponse) -> Unit, onFailure: (t: Throwable) -> Unit)
    suspend fun getPromotionTypeDropDown(onSuccess: (response: BaseResponseList<CommonDropDown>) -> Unit, onFailure: (t: Throwable) -> Unit)
    suspend fun getCommonGroupDropDown(type: Int, storeId: Int, onSuccess: (response: BaseResponseList<CommonDropDown>) -> Unit, onFailure: (t: Throwable) -> Unit)
    suspend fun insertSalesPromotion(req: SalesPromotion, onSuccess: (response: APIResponse) -> Unit, onFailure: (t: Throwable) -> Unit)
    suspend fun insertItemToPromotion(req: AddItemToDiscount, onSuccess: (response: APIResponse) -> Unit, onFailure: (t: Throwable) -> Unit)
    suspend fun getSalesPromotionDetail(discountId: Int, onSuccess: (response: BaseResponse<SalesPromotion>) -> Unit, onFailure: (t: Throwable) -> Unit)
    suspend fun updateSalesPromotion(req: SalesPromotion, onSuccess: (response: APIResponse) -> Unit, onFailure: (t: Throwable) -> Unit)
    suspend fun insertPurchaseOrder(req: PurchaseOrder, onSuccess: (response: APIResponse) -> Unit, onFailure: (t: Throwable) -> Unit)
    suspend fun getPurchaseOrderDetail(purchaseID: Int, onSuccess: (response: BaseResponse<PurchaseOrder>) -> Unit, onFailure: (t: Throwable) -> Unit)
    suspend fun fetchAllOrderList(employeeId: Int, storeId: Int, startDate: String, endDate: String, onSuccess: (response: BaseResponseList<AllOrders>) -> Unit, onFailure: (t: Throwable) -> Unit)
    suspend fun fetchLastOrder(employeeId: Int, onSuccess: (response: BaseResponse<AllOrders>) -> Unit, onFailure: (t: Throwable) -> Unit)
    suspend fun updatePurchaseOrder(req: PurchaseOrder, onSuccess: (response: APIResponse) -> Unit, onFailure: (t: Throwable) -> Unit)
    suspend fun updateStoreStatus(req: StoreUpdate, onSuccess: (response: APIResponse) -> Unit, onFailure: (t: Throwable) -> Unit)
    suspend fun fetchOpenDiscount(storeId: Int, onSuccess: (response: BaseResponseList<SalesPromotion>) -> Unit, onFailure: (t: Throwable) -> Unit)
    suspend fun generateInvoice(orderId : Int, onSuccess : (response : APIResponse) -> Unit, onFailure : (t : Throwable) -> Unit)
    suspend fun getInvoiceList(storeId : Int, status : Int, onSuccess : (response : BaseResponseList<Invoice>) -> Unit, onFailure : (t : Throwable) -> Unit)
    suspend fun deleteInvoice(id : Int, onSuccess : (response : APIResponse) -> Unit, onFailure : (t : Throwable) -> Unit)
    suspend fun getAutoPurchaseOrderList(storeId : Int, search : String, onSuccess : (response : BaseResponseList<PurchaseOrder>) -> Unit, onFailure : (t : Throwable) -> Unit)
    suspend fun insertInvoice(req : Invoice, onSuccess : (response : APIResponse) -> Unit, onFailure : (t : Throwable) -> Unit)
    suspend fun getInvoiceDetail(purchaseID : Int, onSuccess : (response : BaseResponse<Invoice>) -> Unit, onFailure : (t : Throwable) -> Unit)
    suspend fun updateInvoice(req : Invoice, onSuccess : (response : APIResponse) -> Unit, onFailure : (t : Throwable) -> Unit)
}