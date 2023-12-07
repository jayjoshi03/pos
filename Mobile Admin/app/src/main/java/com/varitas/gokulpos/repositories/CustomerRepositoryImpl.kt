package com.varitas.gokulpos.repositories

import android.util.Log
import com.varitas.gokulpos.api.ApiClient
import com.varitas.gokulpos.response.APIResponse
import com.varitas.gokulpos.response.BaseResponse
import com.varitas.gokulpos.response.BaseResponseList
import com.varitas.gokulpos.response.CustomerDetail
import com.varitas.gokulpos.response.Customers
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CustomerRepositoryImpl(private val api : ApiClient) : CustomerRepository {

    override suspend fun fetchCustomers(storeId : Int, onSuccess : (response : BaseResponseList<Customers>) -> Unit, onFailure : (t : Throwable) -> Unit) {
        api.getCustomers(storeId = storeId).enqueue(object : Callback<BaseResponseList<Customers>> {
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

    override suspend fun fetchCustomerDetail(storeId : Int, customerId : Int, onSuccess : (response : BaseResponse<CustomerDetail>) -> Unit, onFailure : (t : Throwable) -> Unit) {
//        api.getCustomerDetail(customerId, storeId = storeId).enqueue(object : Callback<BaseResponse<CustomerDetail>> {
//            override fun onResponse(call: Call<BaseResponse<CustomerDetail>>, response: Response<BaseResponse<CustomerDetail>>) {
//                response.body()?.let { customerList ->
//                    onSuccess.invoke(customerList)
//                    Log.e("CUSTOMER DETAIL", customerList.toString())
//                }
//            }
//
//            override fun onFailure(call: Call<BaseResponse<CustomerDetail>>, t: Throwable) {
//                onFailure.invoke(t)
//                Log.e("CUSTOMER DETAIL ERROR", t.toString())
//            }
//        })
    }

    override suspend fun deleteCustomer(id : Int, storeId : Int, onSuccess : (response : APIResponse) -> Unit, onFailure : (t : Throwable) -> Unit) {
        api.deleteCustomer(id = id).enqueue(object : Callback<APIResponse> {
            override fun onResponse(call : Call<APIResponse>, response : Response<APIResponse>) {
                response.body()?.let { list->
                    onSuccess.invoke(list)
                    Log.e("DELETE CUSTOMER SUCCESS", list.toString())
                }
            }

            override fun onFailure(call : Call<APIResponse>, t : Throwable) {
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