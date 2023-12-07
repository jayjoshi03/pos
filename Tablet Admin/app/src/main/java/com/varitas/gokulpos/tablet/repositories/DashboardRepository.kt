package com.varitas.gokulpos.tablet.repositories


import com.varitas.gokulpos.tablet.response.BaseResponseList
import com.varitas.gokulpos.tablet.response.DropDown
import com.varitas.gokulpos.tablet.response.Menus
import com.varitas.gokulpos.tablet.response.TopSellingItems
import com.varitas.gokulpos.tablet.response.WeeklySaleBar


interface DashboardRepository {

    suspend fun fetchChartData(paymentMethod: Int, storeId: Int, onSuccess: (response: BaseResponseList<WeeklySaleBar>) -> Unit, onFailure: (t: Throwable) -> Unit)
    suspend fun fetchTopSellingProducts(paymentMethod: Int, storeId: Int, onSuccess: (response: BaseResponseList<TopSellingItems>) -> Unit, onFailure: (t: Throwable) -> Unit)
    suspend fun fetchTenders(pageNumber: Int, storeId: Int, onSuccess: (response: BaseResponseList<DropDown>) -> Unit, onFailure: (t: Throwable) -> Unit)
    suspend fun fetchMenus(parentId: Int, actionBy: Int, onSuccess: (response: BaseResponseList<Menus>) -> Unit, onFailure: (t: Throwable) -> Unit)
}