package com.varitas.gokulpos.repositories


import com.varitas.gokulpos.response.APIResponse
import com.varitas.gokulpos.response.BaseResponse
import com.varitas.gokulpos.response.BaseResponseList
import com.varitas.gokulpos.response.CustomerDetail
import com.varitas.gokulpos.response.Customers


interface CustomerRepository {
    suspend fun fetchCustomers(storeId : Int, onSuccess : (response : BaseResponseList<Customers>) -> Unit, onFailure : (t : Throwable) -> Unit)

    suspend fun fetchCustomerDetail(storeId : Int, customerId : Int, onSuccess : (response : BaseResponse<CustomerDetail>) -> Unit, onFailure : (t : Throwable) -> Unit)

    suspend fun deleteCustomer(id : Int, storeId : Int, onSuccess : (response : APIResponse) -> Unit, onFailure : (t : Throwable) -> Unit)

    suspend fun insertCustomer(req : Customers, onSuccess : (response : APIResponse) -> Unit, onFailure : (t : Throwable) -> Unit)

    suspend fun getCustomerDetail(id : Int, onSuccess : (response : BaseResponse<Customers>) -> Unit, onFailure : (t : Throwable) -> Unit)

    suspend fun updateCustomer(req : Customers, onSuccess : (response : APIResponse) -> Unit, onFailure : (t : Throwable) -> Unit)
}