package com.varitas.gokulpos.tablet.repositories


import com.varitas.gokulpos.tablet.response.APIResponse
import com.varitas.gokulpos.tablet.response.BaseResponse
import com.varitas.gokulpos.tablet.response.BaseResponseList
import com.varitas.gokulpos.tablet.response.CommonDropDown
import com.varitas.gokulpos.tablet.response.Employee


interface EmployeeRepository {

    suspend fun fetchEmployees(storeId : Int, onSuccess : (response : BaseResponseList<Employee>) -> Unit, onFailure : (t : Throwable) -> Unit)

    suspend fun fetchEmployeeDetail(empId : Int, onSuccess : (response : BaseResponse<Employee>) -> Unit, onFailure : (t : Throwable) -> Unit)

    suspend fun fetchRoles(storeId : Int, onSuccess : (response : BaseResponseList<CommonDropDown>) -> Unit, onFailure : (t : Throwable) -> Unit)

    suspend fun insertEmployee(req : Employee, onSuccess : (response : APIResponse) -> Unit, onFailure : (t : Throwable) -> Unit)

    suspend fun deleteEmployee(id : Int, storeId : Int, onSuccess : (response : APIResponse) -> Unit, onFailure : (t : Throwable) -> Unit)

    suspend fun updateEmployee(req : Employee, onSuccess : (response : APIResponse) -> Unit, onFailure : (t : Throwable) -> Unit)
}