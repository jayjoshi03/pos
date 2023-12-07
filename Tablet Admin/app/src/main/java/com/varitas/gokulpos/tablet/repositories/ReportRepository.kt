package com.varitas.gokulpos.tablet.repositories


import android.content.Context
import com.varitas.gokulpos.tablet.response.BaseResponse
import com.varitas.gokulpos.tablet.response.BaseResponseList
import com.varitas.gokulpos.tablet.response.Menus
import com.varitas.gokulpos.tablet.response.Payments
import com.varitas.gokulpos.tablet.response.SalesSummary
import com.varitas.gokulpos.tablet.response.TopItems
import com.varitas.gokulpos.tablet.response.TopItemsByCategories


interface ReportRepository {

    suspend fun getMenus(context: Context,isFromProduct : Boolean): List<Menus>
    suspend fun fetchSales(storeId: Int, startDate: String, endDate: String, onSuccess: (response: BaseResponse<SalesSummary>) -> Unit, onFailure: (t: Throwable) -> Unit)
    suspend fun fetchPaymentTypes(storeId: Int, startDate: String, endDate: String, onSuccess: (response: BaseResponseList<Payments>) -> Unit, onFailure: (t: Throwable) -> Unit)
    suspend fun fetchTopItems(storeId: Int, startDate: String, endDate: String, onSuccess: (response: BaseResponseList<TopItems>) -> Unit, onFailure: (t: Throwable) -> Unit)
    suspend fun fetchTopCategories(storeId: Int, startDate: String, endDate: String, onSuccess: (response: BaseResponse<TopItemsByCategories>) -> Unit, onFailure: (t: Throwable) -> Unit)
}