package com.varitas.gokulpos.repositories

import android.util.Log
import com.varitas.gokulpos.api.ApiClient
import com.varitas.gokulpos.model.Menus
import com.varitas.gokulpos.response.BaseResponseList
import com.varitas.gokulpos.response.DropDown
import com.varitas.gokulpos.response.TopSellingItems
import com.varitas.gokulpos.response.WeeklySaleBar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DashboardRepositoryImpl(private val api : ApiClient, private val salesApi : ApiClient) : DashboardRepository {

    override suspend fun fetchChartData(paymentMethod : Int, storeId : Int, onSuccess : (response : BaseResponseList<WeeklySaleBar>) -> Unit, onFailure : (t : Throwable) -> Unit) {
//        salesApi.getWeeklySales(paymentMethod = paymentMethod, storeId = storeId).enqueue(object : Callback<BaseResponseList<WeeklySaleBar>> {
//            override fun onResponse(call: Call<BaseResponseList<WeeklySaleBar>>, response: Response<BaseResponseList<WeeklySaleBar>>) {
//                response.body()?.let { list ->
//                    onSuccess.invoke(list)
//                    Log.e("WEEKLY SALES", list.toString())
//                }
//            }
//
//            override fun onFailure(call: Call<BaseResponseList<WeeklySaleBar>>, t: Throwable) {
//                onFailure.invoke(t)
//                Log.e("WEEKLY SALES ERROR", t.toString())
//            }
//        })
    }

    override suspend fun fetchTopSellingProducts(paymentMethod : Int, storeId : Int, onSuccess : (response : BaseResponseList<TopSellingItems>) -> Unit, onFailure : (t : Throwable) -> Unit) {
//        salesApi.getTopSellingProducts(paymentMethod = paymentMethod, storeId = storeId).enqueue(object : Callback<BaseResponseList<TopSellingItems>> {
//            override fun onResponse(call: Call<BaseResponseList<TopSellingItems>>, response: Response<BaseResponseList<TopSellingItems>>) {
//                response.body()?.let { list ->
//                    onSuccess.invoke(list)
//                    Log.e("TENDERS", list.toString())
//                }
//            }
//
//            override fun onFailure(call: Call<BaseResponseList<TopSellingItems>>, t: Throwable) {
//                onFailure.invoke(t)
//                Log.e("TENDERS ERROR", t.toString())
//            }
//        })
    }

    override suspend fun fetchMenus(parentId : Int, actionBy : Int, onSuccess : (response : BaseResponseList<Menus>) -> Unit, onFailure : (t : Throwable) -> Unit) {
        api.getDashboardMenu(parentId, actionBy).enqueue(object : Callback<BaseResponseList<Menus>> {
            override fun onResponse(call : Call<BaseResponseList<Menus>>, response : Response<BaseResponseList<Menus>>) {
                response.body()?.let { list->
                    onSuccess.invoke(list)
                    Log.e("MENUS", list.toString())
                }
            }

            override fun onFailure(call : Call<BaseResponseList<Menus>>, t : Throwable) {
                onFailure.invoke(t)
                Log.e("MENUS ERROR", t.toString())
            }
        })
    }

    override suspend fun fetchTenders(pageNumber : Int, storeId : Int, onSuccess : (response : BaseResponseList<DropDown>) -> Unit, onFailure : (t : Throwable) -> Unit) {
//        api.getTenders(storeId = storeId).enqueue(object : Callback<BaseResponseList<DropDown>> {
//            override fun onResponse(call: Call<BaseResponseList<DropDown>>, response: Response<BaseResponseList<DropDown>>) {
//                response.body()?.let { list ->
//                    onSuccess.invoke(list)
//                    Log.e("TENDERS", list.toString())
//                }
//            }
//
//            override fun onFailure(call: Call<BaseResponseList<DropDown>>, t: Throwable) {
//                onFailure.invoke(t)
//                Log.e("TENDERS ERROR", t.toString())
//            }
//        })
    }
}