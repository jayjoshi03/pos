package com.varitas.gokulpos.repositories


import android.content.Context
import com.varitas.gokulpos.model.Menus
import com.varitas.gokulpos.response.BaseResponse
import com.varitas.gokulpos.response.BaseResponseList
import com.varitas.gokulpos.response.Payments
import com.varitas.gokulpos.response.SalesSummary
import com.varitas.gokulpos.response.TopItems
import com.varitas.gokulpos.response.TopItemsByCategories


interface ReportRepository {

    suspend fun getMenus(context: Context,isFromProduct : Boolean): List<Menus>
    suspend fun fetchSales(storeId: Int, startDate: String, endDate: String, onSuccess: (response: BaseResponse<SalesSummary>) -> Unit, onFailure: (t: Throwable) -> Unit)
    suspend fun fetchPaymentTypes(storeId: Int, startDate: String, endDate: String, onSuccess: (response: BaseResponseList<Payments>) -> Unit, onFailure: (t: Throwable) -> Unit)
    suspend fun fetchTopItems(storeId: Int, startDate: String, endDate: String, onSuccess: (response: BaseResponseList<TopItems>) -> Unit, onFailure: (t: Throwable) -> Unit)
    suspend fun fetchTopCategories(storeId: Int, startDate: String, endDate: String, onSuccess: (response: BaseResponse<TopItemsByCategories>) -> Unit, onFailure: (t: Throwable) -> Unit)
}