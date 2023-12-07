package com.varitas.gokulpos.repositories


import com.varitas.gokulpos.response.APIResponse
import com.varitas.gokulpos.response.BaseResponse
import com.varitas.gokulpos.response.BaseResponseList
import com.varitas.gokulpos.response.CommonDropDown
import com.varitas.gokulpos.response.Employee


interface EmployeeRepository {

    suspend fun fetchEmployees(storeId : Int, onSuccess : (response : BaseResponseList<Employee>) -> Unit, onFailure : (t : Throwable) -> Unit)

    suspend fun fetchEmployeeDetail(empId : Int, onSuccess : (response : BaseResponse<Employee>) -> Unit, onFailure : (t : Throwable) -> Unit)

    suspend fun fetchRoles(storeId : Int, onSuccess : (response : BaseResponseList<CommonDropDown>) -> Unit, onFailure : (t : Throwable) -> Unit)

    suspend fun insertEmployee(req : Employee, onSuccess : (response : APIResponse) -> Unit, onFailure : (t : Throwable) -> Unit)

    suspend fun deleteEmployee(id : Int, storeId : Int, onSuccess : (response : APIResponse) -> Unit, onFailure : (t : Throwable) -> Unit)

    suspend fun updateEmployee(req : Employee, onSuccess : (response : APIResponse) -> Unit, onFailure : (t : Throwable) -> Unit)
}