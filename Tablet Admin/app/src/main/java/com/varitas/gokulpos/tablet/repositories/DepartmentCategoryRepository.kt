package com.varitas.gokulpos.tablet.repositories

import com.varitas.gokulpos.tablet.model.Category
import com.varitas.gokulpos.tablet.response.Department
import com.varitas.gokulpos.tablet.response.BaseResponseList


interface DepartmentCategoryRepository {

    suspend fun fetchDepartments(onSuccess: (response: BaseResponseList<Department>) -> Unit, onFailure: (t: Throwable) -> Unit)

    suspend fun fetchCategories(onSuccess: (response: BaseResponseList<Category>) -> Unit, onFailure: (t: Throwable) -> Unit)

}