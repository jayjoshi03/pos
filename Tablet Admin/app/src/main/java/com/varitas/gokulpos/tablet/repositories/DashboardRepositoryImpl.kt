package com.varitas.gokulpos.tablet.repositories

import android.util.Log
import com.varitas.gokulpos.tablet.api.ApiClient
import com.varitas.gokulpos.tablet.response.BaseResponseList
import com.varitas.gokulpos.tablet.response.DropDown
import com.varitas.gokulpos.tablet.response.Menus
import com.varitas.gokulpos.tablet.response.TopSellingItems
import com.varitas.gokulpos.tablet.response.WeeklySaleBar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DashboardRepositoryImpl(private val api: ApiClient, private val salesApi: ApiClient) : DashboardRepository {

    override suspend fun fetchChartData(paymentMethod: Int, storeId: Int, onSuccess: (response: BaseResponseList<WeeklySaleBar>) -> Unit, onFailure: (t: Throwable) -> Unit) {

    }

    override suspend fun fetchTopSellingProducts(paymentMethod: Int, storeId: Int, onSuccess: (response: BaseResponseList<TopSellingItems>) -> Unit, onFailure: (t: Throwable) -> Unit) {

    }

    override suspend fun fetchTenders(pageNumber: Int, storeId: Int, onSuccess: (response: BaseResponseList<DropDown>) -> Unit, onFailure: (t: Throwable) -> Unit) {

    }

    override suspend fun fetchMenus(parentId: Int, actionBy: Int, onSuccess: (response: BaseResponseList<Menus>) -> Unit, onFailure: (t: Throwable) -> Unit) {
        api.getDashboardMenu(parentId, actionBy).enqueue(object : Callback<BaseResponseList<Menus>> {
            override fun onResponse(call: Call<BaseResponseList<Menus>>, response: Response<BaseResponseList<Menus>>) {
                response.body()?.let { list ->
                    onSuccess.invoke(list)
                    Log.e("MENUS", list.toString())
                }
            }

            override fun onFailure(call: Call<BaseResponseList<Menus>>, t: Throwable) {
                onFailure.invoke(t)
                Log.e("MENUS ERROR", t.toString())
            }
        })
    }
}