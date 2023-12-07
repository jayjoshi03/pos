package com.varitas.gokulpos.tablet.repositories


import com.varitas.gokulpos.tablet.response.APIResponse
import com.varitas.gokulpos.tablet.response.BaseResponse
import com.varitas.gokulpos.tablet.response.BaseResponseList
import com.varitas.gokulpos.tablet.response.CommonDropDown
import com.varitas.gokulpos.tablet.response.CustomerDetail
import com.varitas.gokulpos.tablet.response.Customers


interface CustomerRepository {
    suspend fun fetchCustomers(storeId: Int, status: Int, onSuccess: (response: BaseResponseList<Customers>) -> Unit, onFailure: (t: Throwable) -> Unit)

    suspend fun fetchCustomerDropDown(storeId: Int, onSuccess: (response: BaseResponseList<CommonDropDown>) -> Unit, onFailure: (t: Throwable) -> Unit)

    suspend fun fetchCustomerDetail(storeId: Int, customerId: Int, onSuccess: (response: BaseResponse<CustomerDetail>) -> Unit, onFailure: (t: Throwable) -> Unit)

    suspend fun deleteCustomer(id: Int, storeId: Int, onSuccess: (response: APIResponse) -> Unit, onFailure: (t: Throwable) -> Unit)

    suspend fun insertCustomer(req : Customers, onSuccess : (response : APIResponse) -> Unit, onFailure : (t : Throwable) -> Unit)

    suspend fun getCustomerDetail(id : Int, onSuccess : (response : BaseResponse<Customers>) -> Unit, onFailure : (t : Throwable) -> Unit)

    suspend fun updateCustomer(req : Customers, onSuccess : (response : APIResponse) -> Unit, onFailure : (t : Throwable) -> Unit)
}