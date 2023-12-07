package com.varitas.gokulpos.tablet.repositories

import android.util.Log
import com.varitas.gokulpos.tablet.api.ApiClient
import com.varitas.gokulpos.tablet.response.APIResponse
import com.varitas.gokulpos.tablet.response.BaseResponse
import com.varitas.gokulpos.tablet.response.BaseResponseList
import com.varitas.gokulpos.tablet.response.CommonDropDown
import com.varitas.gokulpos.tablet.response.CustomerDetail
import com.varitas.gokulpos.tablet.response.Customers
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CustomerRepositoryImpl(private val api : ApiClient) : CustomerRepository {

    override suspend fun fetchCustomers(storeId : Int,status : Int, onSuccess : (response : BaseResponseList<Customers>) -> Unit, onFailure : (t : Throwable) -> Unit) {
        api.getCustomers(storeId = storeId, status = status).enqueue(object : Callback<BaseResponseList<Customers>> {
            override fun onResponse(call : Call<BaseResponseList<Customers>>, response : Response<BaseResponseList<Customers>>) {
                response.body()?.let { customerList->
                    onSuccess.invoke(customerList)
                    Log.e("CUSTOMERS", customerList.toString())
                }
            }

            override fun onFailure(call : Call<BaseResponseList<Customers>>, t : Throwable) {
                onFailure.invoke(t)
                Log.e("CUSTOMERS ERROR", t.toString())
            }
        })
    }

    override suspend fun fetchCustomerDropDown(storeId: Int, onSuccess: (response: BaseResponseList<CommonDropDown>) -> Unit, onFailure: (t: Throwable) -> Unit) {
        api.getCustomerDropDown(storeId = storeId).enqueue(object : Callback<BaseResponseList<CommonDropDown>> {
            override fun onResponse(call : Call<BaseResponseList<CommonDropDown>>, response : Response<BaseResponseList<CommonDropDown>>) {
                response.body()?.let { customerList->
                    onSuccess.invoke(customerList)
                    Log.e("CUSTOMERS", customerList.toString())
                }
            }

            override fun onFailure(call : Call<BaseResponseList<CommonDropDown>>, t : Throwable) {
                onFailure.invoke(t)
                Log.e("CUSTOMERS ERROR", t.toString())
            }
        })
    }

    override suspend fun fetchCustomerDetail(storeId : Int, customerId : Int, onSuccess : (response : BaseResponse<CustomerDetail>) -> Unit, onFailure : (t : Throwable) -> Unit) {

    }

    override suspend fun deleteCustomer(id: Int, storeId: Int, onSuccess: (response: APIResponse) -> Unit, onFailure: (t: Throwable) -> Unit) {
        api.deleteCustomer(id).enqueue(object : Callback<APIResponse> {
            override fun onResponse(call: Call<APIResponse>, response: Response<APIResponse>) {
                response.body()?.let { list ->
                    onSuccess.invoke(list)
                    Log.e("DELETE CUSTOMER SUCCESS", list.toString())
                }
            }

            override fun onFailure(call: Call<APIResponse>, t: Throwable) {
                onFailure.invoke(t)
                Log.e("DELETE CUSTOMER SUCCESS", t.toString())
            }
        })
    }

    override suspend fun insertCustomer(req : Customers, onSuccess : (response : APIResponse) -> Unit, onFailure : (t : Throwable) -> Unit) {
        api.insertCustomer(req = req).enqueue(object : Callback<APIResponse> {
            override fun onResponse(call : Call<APIResponse>, response : Response<APIResponse>) {
                response.body()?.let { list->
                    onSuccess.invoke(list)
                    Log.e("INSERT CUSTOMER SUCCESS", list.toString())
                }
            }

            override fun onFailure(call : Call<APIResponse>, t : Throwable) {
                onFailure.invoke(t)
                Log.e("INSERT CUSTOMER ERROR", t.toString())
            }
        })
    }

    override suspend fun getCustomerDetail(id : Int, onSuccess : (response : BaseResponse<Customers>) -> Unit, onFailure : (t : Throwable) -> Unit) {
        api.getCustomerDetail(id = id).enqueue(object : Callback<BaseResponse<Customers>> {
            override fun onResponse(call : Call<BaseResponse<Customers>>, response : Response<BaseResponse<Customers>>) {
                response.body()?.let { list->
                    onSuccess.invoke(list)
                    Log.e("CUSTOMER DETAILS", list.toString())
                }
            }

            override fun onFailure(call : Call<BaseResponse<Customers>>, t : Throwable) {
                onFailure.invoke(t)
                Log.e("CUSTOMER DETAILS ERROR", t.toString())
            }
        })
    }

    override suspend fun updateCustomer(req : Customers, onSuccess : (response : APIResponse) -> Unit, onFailure : (t : Throwable) -> Unit) {
        api.updateCustomer(req = req).enqueue(object : Callback<APIResponse> {
            override fun onResponse(call : Call<APIResponse>, response : Response<APIResponse>) {
                response.body()?.let { list->
                    onSuccess.invoke(list)
                    Log.e("UPDATE CUSTOMER SUCCESS", list.toString())
                }
            }

            override fun onFailure(call : Call<APIResponse>, t : Throwable) {
                onFailure.invoke(t)
                Log.e("UPDATE CUSTOMER ERROR", t.toString())
            }
        })
    }
}