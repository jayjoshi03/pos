package com.varitas.gokulpos.repositories

import com.varitas.gokulpos.api.ApiClient
import com.varitas.gokulpos.response.BaseResponseList
import com.varitas.gokulpos.response.DeviceList

class DevicesRepositoryImpl(private val api : ApiClient) : DevicesRepository {

    override suspend fun fetchDevices(storeId : Int, onSuccess : (response : BaseResponseList<DeviceList>) -> Unit, onFailure : (t : Throwable) -> Unit) {
//        api.getDevices(storeId = storeId).enqueue(object : Callback<BaseResponseList<DeviceList>> {
//            override fun onResponse(call: Call<BaseResponseList<DeviceList>>, response: Response<BaseResponseList<DeviceList>>) {
//                response.body()?.let { customerList ->
//                    onSuccess.invoke(customerList)
//                    Log.e("EMPLOYEE", customerList.toString())
//                }
//            }
//
//            override fun onFailure(call: Call<BaseResponseList<DeviceList>>, t: Throwable) {
//                onFailure.invoke(t)
//                Log.e("EMPLOYEE ERROR", t.toString())
//            }
//        })
    }
}