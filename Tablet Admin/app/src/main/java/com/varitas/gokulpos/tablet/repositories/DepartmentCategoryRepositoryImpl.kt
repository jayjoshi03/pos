package com.varitas.gokulpos.tablet.repositories

import android.util.Log
import com.varitas.gokulpos.tablet.api.ApiClient
import com.varitas.gokulpos.tablet.model.Category
import com.varitas.gokulpos.tablet.response.BaseResponseList
import com.varitas.gokulpos.tablet.response.Department
import com.varitas.gokulpos.tablet.utilities.Utils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DepartmentCategoryRepositoryImpl(private val api: ApiClient) : DepartmentCategoryRepository {

    private val storeDetails = Utils.fetchLoginResponse()
    private val storeId = storeDetails.singleResult!!.storeId

    override suspend fun fetchDepartments(onSuccess: (response: BaseResponseList<Department>) -> Unit, onFailure: (t: Throwable) -> Unit) {
        api.getDepartments(storeId = storeId!!).enqueue(object : Callback<BaseResponseList<Department>> {
            override fun onResponse(call: Call<BaseResponseList<Department>>, response: Response<BaseResponseList<Department>>) {
                response.body()?.let { department ->
                    onSuccess.invoke(department)
                    Log.e("CATEGORIES", department.toString())
                }
            }

            override fun onFailure(call: Call<BaseResponseList<Department>>, t: Throwable) {
                onFailure.invoke(t)
                Log.e("CATEGORIES ERROR", t.toString())
            }
        })
    }

    override suspend fun fetchCategories(onSuccess: (response: BaseResponseList<Category>) -> Unit, onFailure: (t: Throwable) -> Unit) {
        api.getCategories(storeId = storeId!!).enqueue(object : Callback<BaseResponseList<Category>> {
            override fun onResponse(call: Call<BaseResponseList<Category>>, response: Response<BaseResponseList<Category>>) {
                response.body()?.let { categories ->
                    onSuccess.invoke(categories)
                    Log.e("CATEGORIES", categories.toString())
                }
            }

            override fun onFailure(call: Call<BaseResponseList<Category>>, t: Throwable) {
                onFailure.invoke(t)
                Log.e("CATEGORIES ERROR", t.toString())
            }
        })
    }

}