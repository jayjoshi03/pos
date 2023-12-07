package com.varitas.gokulpos.repositories

import android.util.Log
import com.varitas.gokulpos.api.ApiClient
import com.varitas.gokulpos.response.APIResponse
import com.varitas.gokulpos.response.BaseResponse
import com.varitas.gokulpos.response.BaseResponseList
import com.varitas.gokulpos.response.CommonDropDown
import com.varitas.gokulpos.response.Employee
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EmployeeRepositoryImpl(private val api : ApiClient) : EmployeeRepository {
    override suspend fun fetchEmployees(storeId : Int, onSuccess : (response : BaseResponseList<Employee>) -> Unit, onFailure : (t : Throwable) -> Unit) {
        api.getEmployees(storeId = storeId).enqueue(object : Callback<BaseResponseList<Employee>> {
            override fun onResponse(call : Call<BaseResponseList<Employee>>, response : Response<BaseResponseList<Employee>>) {
                response.body()?.let { customerList->
                    onSuccess.invoke(customerList)
                    Log.e("EMPLOYEE", customerList.toString())
                }
            }

            override fun onFailure(call : Call<BaseResponseList<Employee>>, t : Throwable) {
                onFailure.invoke(t)
                Log.e("EMPLOYEE ERROR", t.toString())
            }
        })
    }

    override suspend fun fetchEmployeeDetail(empId : Int, onSuccess : (response : BaseResponse<Employee>) -> Unit, onFailure : (t : Throwable) -> Unit) {
        api.getEmployeeDetail(empId).enqueue(object : Callback<BaseResponse<Employee>> {
            override fun onResponse(call : Call<BaseResponse<Employee>>, response : Response<BaseResponse<Employee>>) {
                response.body()?.let { customerList->
                    onSuccess.invoke(customerList)
                    Log.e("EMPLOYEE DETAIL", customerList.toString())
                }
            }

            override fun onFailure(call : Call<BaseResponse<Employee>>, t : Throwable) {
                onFailure.invoke(t)
                Log.e("EMPLOYEE DETAIL ERROR", t.toString())
            }
        })
    }

    override suspend fun fetchRoles(storeId : Int, onSuccess : (response : BaseResponseList<CommonDropDown>) -> Unit, onFailure : (t : Throwable) -> Unit) {
        api.getRoleDropDown(storeId = storeId).enqueue(object : Callback<BaseResponseList<CommonDropDown>> {
            override fun onResponse(call : Call<BaseResponseList<CommonDropDown>>, response : Response<BaseResponseList<CommonDropDown>>) {
                response.body()?.let { customerList->
                    onSuccess.invoke(customerList)
                    Log.e("ROLES DETAIL", customerList.toString())
                }
            }

            override fun onFailure(call : Call<BaseResponseList<CommonDropDown>>, t : Throwable) {
                onFailure.invoke(t)
                Log.e("ROLES DETAIL ERROR", t.toString())
            }
        })
    }

    override suspend fun insertEmployee(req : Employee, onSuccess : (response : APIResponse) -> Unit, onFailure : (t : Throwable) -> Unit) {
        api.insertEmployee(req).enqueue(object : Callback<APIResponse> {
            override fun onResponse(call : Call<APIResponse>, response : Response<APIResponse>) {
                response.body()?.let { login->
                    onSuccess.invoke(login)
                    Log.e("INSERT EMPLOYEE SUCCESS", login.toString())
                }
            }

            override fun onFailure(call : Call<APIResponse>, t : Throwable) {
                onFailure.invoke(t)
                Log.e("INSERT EMPLOYEE ERROR", t.toString())
            }
        })
    }

    override suspend fun deleteEmployee(id : Int, storeId : Int, onSuccess : (response : APIResponse) -> Unit, onFailure : (t : Throwable) -> Unit) {
        api.deleteEmployee(id = id).enqueue(object : Callback<APIResponse> {
            override fun onResponse(call : Call<APIResponse>, response : Response<APIResponse>) {
                response.body()?.let { list->
                    onSuccess.invoke(list)
                    Log.e("DELETE EMPLOYEE SUCCESS", list.toString())
                }
            }

            override fun onFailure(call : Call<APIResponse>, t : Throwable) {
                onFailure.invoke(t)
                Log.e("DELETE EMPLOYEE SUCCESS", t.toString())
            }
        })
    }

    override suspend fun updateEmployee(req : Employee, onSuccess : (response : APIResponse) -> Unit, onFailure : (t : Throwable) -> Unit) {
        api.updateEmployee(req = req).enqueue(object : Callback<APIResponse> {
            override fun onResponse(call : Call<APIResponse>, response : Response<APIResponse>) {
                response.body()?.let { list->
                    onSuccess.invoke(list)
                    Log.e("UPDATE EMPLOYEE SUCCESS", list.toString())
                }
            }

            override fun onFailure(call : Call<APIResponse>, t : Throwable) {
                onFailure.invoke(t)
                Log.e("UPDATE EMPLOYEE ERROR", t.toString())
            }
        })
    }
}