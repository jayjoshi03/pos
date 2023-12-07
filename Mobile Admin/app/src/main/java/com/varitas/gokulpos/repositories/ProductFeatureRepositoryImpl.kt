package com.varitas.gokulpos.repositories

import android.util.Log
import com.varitas.gokulpos.api.ApiClient
import com.varitas.gokulpos.request.BrandInsertRequest
import com.varitas.gokulpos.request.BrandUpdateRequest
import com.varitas.gokulpos.request.CategoryInsertUpdate
import com.varitas.gokulpos.request.DepartmentInsertUpdate
import com.varitas.gokulpos.request.FacilityInsertRequest
import com.varitas.gokulpos.request.FacilityUpdateRequest
import com.varitas.gokulpos.request.GroupInsertUpdateRequest
import com.varitas.gokulpos.request.SpecificationInsertRequest
import com.varitas.gokulpos.request.SpecificationTypeInsertRequest
import com.varitas.gokulpos.request.SpecificationUpdateRequest
import com.varitas.gokulpos.request.TaxInsertRequest
import com.varitas.gokulpos.request.TaxUpdateRequest
import com.varitas.gokulpos.request.UOMInsertRequest
import com.varitas.gokulpos.request.UOMUpdateRequest
import com.varitas.gokulpos.request.VendorInsertRequest
import com.varitas.gokulpos.request.VendorUpdateRequest
import com.varitas.gokulpos.response.APIResponse
import com.varitas.gokulpos.response.BaseResponse
import com.varitas.gokulpos.response.BaseResponseList
import com.varitas.gokulpos.response.Brand
import com.varitas.gokulpos.response.Category
import com.varitas.gokulpos.response.CommonDropDown
import com.varitas.gokulpos.response.Department
import com.varitas.gokulpos.response.DropDown
import com.varitas.gokulpos.response.Facility
import com.varitas.gokulpos.response.Group
import com.varitas.gokulpos.response.ItemCount
import com.varitas.gokulpos.response.SalesPerson
import com.varitas.gokulpos.response.Specification
import com.varitas.gokulpos.response.SubCategory
import com.varitas.gokulpos.response.Tax
import com.varitas.gokulpos.response.UOM
import com.varitas.gokulpos.response.Vendor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProductFeatureRepositoryImpl(private val api : ApiClient) : ProductFeatureRepository {

    override suspend fun fetchDepartments(
        storeId : Int,
        onSuccess : (response : BaseResponseList<Department>) -> Unit,
        onFailure : (t : Throwable) -> Unit,
    ) {
        api.getDepartments(storeId = storeId).enqueue(object : Callback<BaseResponseList<Department>> {
            override fun onResponse(
                call : Call<BaseResponseList<Department>>,
                response : Response<BaseResponseList<Department>>,
            ) {
                response.body()?.let { list->
                    onSuccess.invoke(list)
                    Log.e("DEPARTMENTS", list.toString())
                }
            }

            override fun onFailure(call : Call<BaseResponseList<Department>>, t : Throwable) {
                onFailure.invoke(t)
                Log.e("DEPARTMENTS ERROR", t.toString())
            }
        })
    }

    override suspend fun fetchCategories(
        storeId : Int,
        onSuccess : (response : BaseResponseList<Category>) -> Unit,
        onFailure : (t : Throwable) -> Unit,
    ) {
        api.getCategories(storeId = storeId).enqueue(object : Callback<BaseResponseList<Category>> {
            override fun onResponse(
                call : Call<BaseResponseList<Category>>,
                response : Response<BaseResponseList<Category>>,
            ) {
                response.body()?.let { list->
                    onSuccess.invoke(list)
                    Log.e("CATEGORIES", list.toString())
                }
            }

            override fun onFailure(call : Call<BaseResponseList<Category>>, t : Throwable) {
                onFailure.invoke(t)
                Log.e("CATEGORIES ERROR", t.toString())
            }
        })
    }

    override suspend fun fetchAllCategories(
        storeId : Int,
        onSuccess : (response : BaseResponseList<DropDown>) -> Unit, onFailure : (t : Throwable) -> Unit,
    ) {
//        api.getCategory(storeId = storeId).enqueue(object : Callback<BaseResponseList<DropDown>> {
//            override fun onResponse(
//                call : Call<BaseResponseList<DropDown>>,
//                response : Response<BaseResponseList<DropDown>>,
//            ) {
//                response.body()?.let { list->
//                    onSuccess.invoke(list)
//                    Log.e("CATEGORY", list.toString())
//                }
//            }
//
//            override fun onFailure(call : Call<BaseResponseList<DropDown>>, t : Throwable) {
//                onFailure.invoke(t)
//                Log.e("CATEGORY ERROR", t.toString())
//            }
//        })
    }

    override suspend fun insertCategories(req : CategoryInsertUpdate, onSuccess : (response : APIResponse) -> Unit, onFailure : (t : Throwable) -> Unit) {
        api.insertCategory(req).enqueue(object : Callback<APIResponse> {
            override fun onResponse(call : Call<APIResponse>, response : Response<APIResponse>) {
                response.body()?.let { list->
                    onSuccess.invoke(list)
                    Log.e("INSERT CATEGORY SUCCESS", list.toString())
                }
            }

            override fun onFailure(call : Call<APIResponse>, t : Throwable) {
                onFailure.invoke(t)
                Log.e("INSERT CATEGORY ERROR", t.toString())
            }
        })
    }

    override suspend fun insertDepartment(req : DepartmentInsertUpdate, onSuccess : (response : APIResponse) -> Unit, onFailure : (t : Throwable) -> Unit) {
        api.insertDepartment(req).enqueue(object : Callback<APIResponse> {
            override fun onResponse(call : Call<APIResponse>, response : Response<APIResponse>) {
                response.body()?.let { list->
                    onSuccess.invoke(list)
                    Log.e("INSERT DEPARTMENT SUCCESS", list.toString())
                }
            }

            override fun onFailure(call : Call<APIResponse>, t : Throwable) {
                onFailure.invoke(t)
                Log.e("INSERT DEPARTMENT ERROR", t.toString())
            }
        })
    }

    override suspend fun fetchCategoryDetails(catId : Int, onSuccess : (response : BaseResponse<CategoryInsertUpdate>) -> Unit, onFailure : (t : Throwable) -> Unit) {
        api.getCategoryDetails(catId).enqueue(object : Callback<BaseResponse<CategoryInsertUpdate>> {
            override fun onResponse(
                call : Call<BaseResponse<CategoryInsertUpdate>>,
                response : Response<BaseResponse<CategoryInsertUpdate>>,
            ) {
                response.body()?.let { list->
                    onSuccess.invoke(list)
                    Log.e("CATEGORY DETAILS", list.toString())
                }
            }

            override fun onFailure(call : Call<BaseResponse<CategoryInsertUpdate>>, t : Throwable) {
                onFailure.invoke(t)
                Log.e("CATEGORY DETAILS ERROR", t.toString())
            }
        })
    }

    override suspend fun fetchSubCategories(categoryId : Int, onSuccess : (response : BaseResponseList<SubCategory>) -> Unit, onFailure : (t : Throwable) -> Unit) {
//        api.getDetailsByParentCategory(parentCategoryId = categoryId).enqueue(object : Callback<BaseResponseList<SubCategory>> {
//            override fun onResponse(
//                call : Call<BaseResponseList<SubCategory>>,
//                response : Response<BaseResponseList<SubCategory>>,
//            ) {
//                response.body()?.let { list->
//                    onSuccess.invoke(list)
//                    Log.e("SUBCATEGORY", list.toString())
//                }
//            }
//
//            override fun onFailure(call : Call<BaseResponseList<SubCategory>>, t : Throwable) {
//                onFailure.invoke(t)
//                Log.e("SUBCATEGORY ERROR", t.toString())
//            }
//        })
    }

    override suspend fun deleteDepartment(id : Int, storeId : Int, onSuccess : (response : APIResponse) -> Unit, onFailure : (t : Throwable) -> Unit) {
        api.deleteDepartment(id).enqueue(object : Callback<APIResponse> {
            override fun onResponse(call : Call<APIResponse>, response : Response<APIResponse>) {
                response.body()?.let { list->
                    onSuccess.invoke(list)
                    Log.e("DELETE DEPARTMENT SUCCESS", list.toString())
                }
            }

            override fun onFailure(call : Call<APIResponse>, t : Throwable) {
                onFailure.invoke(t)
                Log.e("DELETE DEPARTMENT SUCCESS", t.toString())
            }
        })
    }

    override suspend fun fetchDepartmentDetails(id : Int, storeId : Int, onSuccess : (response : BaseResponse<DepartmentInsertUpdate>) -> Unit, onFailure : (t : Throwable) -> Unit) {
        api.getDepartmentDetails(id).enqueue(object : Callback<BaseResponse<DepartmentInsertUpdate>> {
            override fun onResponse(call : Call<BaseResponse<DepartmentInsertUpdate>>, response : Response<BaseResponse<DepartmentInsertUpdate>>) {
                response.body()?.let { list->
                    onSuccess.invoke(list)
                    Log.e("DEPARTMENT DETAILS", list.toString())
                }
            }

            override fun onFailure(call : Call<BaseResponse<DepartmentInsertUpdate>>, t : Throwable) {
                onFailure.invoke(t)
                Log.e("DEPARTMENT DETAILS ERROR", t.toString())
            }
        })
    }

    override suspend fun updateDepartment(req : DepartmentInsertUpdate, onSuccess : (response : APIResponse) -> Unit, onFailure : (t : Throwable) -> Unit) {
        api.updateDepartment(req).enqueue(object : Callback<APIResponse> {
            override fun onResponse(call : Call<APIResponse>, response : Response<APIResponse>) {
                response.body()?.let { list->
                    onSuccess.invoke(list)
                    Log.e("UPDATE DEPARTMENT SUCCESS", list.toString())
                }
            }

            override fun onFailure(call : Call<APIResponse>, t : Throwable) {
                onFailure.invoke(t)
                Log.e("UPDATE DEPARTMENT ERROR", t.toString())
            }
        })
    }

    override suspend fun deleteCategory(id : Int, storeId : Int, onSuccess : (response : APIResponse) -> Unit, onFailure : (t : Throwable) -> Unit) {
        api.deleteCategory(id).enqueue(object : Callback<APIResponse> {
            override fun onResponse(call : Call<APIResponse>, response : Response<APIResponse>) {
                response.body()?.let { list->
                    onSuccess.invoke(list)
                    Log.e("DELETE CATEGORY SUCCESS", list.toString())
                }
            }

            override fun onFailure(call : Call<APIResponse>, t : Throwable) {
                onFailure.invoke(t)
                Log.e("DELETE CATEGORY SUCCESS", t.toString())
            }
        })
    }

    override suspend fun getParentCategory(storeId : Int, onSuccess : (response : BaseResponseList<CommonDropDown>) -> Unit, onFailure : (t : Throwable) -> Unit) {
        api.getParentCategory(storeId).enqueue(object : Callback<BaseResponseList<CommonDropDown>> {
            override fun onResponse(call : Call<BaseResponseList<CommonDropDown>>, response : Response<BaseResponseList<CommonDropDown>>) {
                response.body()?.let { list->
                    onSuccess.invoke(list)
                    Log.e("PARENT CATEGORY", list.toString())
                }
            }

            override fun onFailure(call : Call<BaseResponseList<CommonDropDown>>, t : Throwable) {
                onFailure.invoke(t)
                Log.e("PARENT CATEGORY ERROR", t.toString())
            }
        })
    }

    override suspend fun getDepartment(storeId : Int, onSuccess : (response : BaseResponseList<CommonDropDown>) -> Unit, onFailure : (t : Throwable) -> Unit) {
        api.getDepartment(storeId).enqueue(object : Callback<BaseResponseList<CommonDropDown>> {
            override fun onResponse(call : Call<BaseResponseList<CommonDropDown>>, response : Response<BaseResponseList<CommonDropDown>>) {
                response.body()?.let { list->
                    onSuccess.invoke(list)
                    Log.e("DEPARTMENT", list.toString())
                }
            }

            override fun onFailure(call : Call<BaseResponseList<CommonDropDown>>, t : Throwable) {
                onFailure.invoke(t)
                Log.e("DEPARTMENT ERROR", t.toString())
            }
        })
    }

    override suspend fun updateCategory(req : CategoryInsertUpdate, onSuccess : (response : APIResponse) -> Unit, onFailure : (t : Throwable) -> Unit) {
        api.updateCategory(req).enqueue(object : Callback<APIResponse> {
            override fun onResponse(call : Call<APIResponse>, response : Response<APIResponse>) {
                response.body()?.let { list->
                    onSuccess.invoke(list)
                    Log.e("UPDATE CATEGORY SUCCESS", list.toString())
                }
            }

            override fun onFailure(call : Call<APIResponse>, t : Throwable) {
                onFailure.invoke(t)
                Log.e("UPDATE CATEGORY ERROR", t.toString())
            }
        })
    }


    override suspend fun fetchBrands(storeId : Int, onSuccess : (response : BaseResponseList<Brand>) -> Unit, onFailure : (t : Throwable) -> Unit) {
        api.getAllBrands(storeId = storeId).enqueue(object : Callback<BaseResponseList<Brand>> {
            override fun onResponse(
                call : Call<BaseResponseList<Brand>>,
                response : Response<BaseResponseList<Brand>>,
            ) {
                response.body()?.let { list->
                    onSuccess.invoke(list)
                    Log.e("BRANDS", list.toString())
                }
            }

            override fun onFailure(call : Call<BaseResponseList<Brand>>, t : Throwable) {
                onFailure.invoke(t)
                Log.e("BRANDS ERROR", t.toString())
            }
        })
    }

    override suspend fun deleteBrand(id : Int, storeId : Int, onSuccess : (response : APIResponse) -> Unit, onFailure : (t : Throwable) -> Unit) {
        api.deleteBrand(id).enqueue(object : Callback<APIResponse> {
            override fun onResponse(call : Call<APIResponse>, response : Response<APIResponse>) {
                response.body()?.let { list->
                    onSuccess.invoke(list)
                    Log.e("DELETE BRAND SUCCESS", list.toString())
                }
            }

            override fun onFailure(call : Call<APIResponse>, t : Throwable) {
                onFailure.invoke(t)
                Log.e("DELETE BRAND ERROR", t.toString())
            }
        })
    }

    override suspend fun insertBrand(req : BrandInsertRequest, onSuccess : (response : APIResponse) -> Unit, onFailure : (t : Throwable) -> Unit) {
        api.insertBrand(req).enqueue(object : Callback<APIResponse> {
            override fun onResponse(call : Call<APIResponse>, response : Response<APIResponse>) {
                response.body()?.let { list->
                    onSuccess.invoke(list)
                    Log.e("INSERT BRAND SUCCESS", list.toString())
                }
            }

            override fun onFailure(call : Call<APIResponse>, t : Throwable) {
                onFailure.invoke(t)
                Log.e("INSERT BRAND ERROR", t.toString())
            }
        })
    }

    override suspend fun updateBrand(req : BrandUpdateRequest, onSuccess : (response : APIResponse) -> Unit, onFailure : (t : Throwable) -> Unit) {
        api.updateBrand(req).enqueue(object : Callback<APIResponse> {
            override fun onResponse(call : Call<APIResponse>, response : Response<APIResponse>) {
                response.body()?.let { list->
                    onSuccess.invoke(list)
                    Log.e("UPDATE BRAND SUCCESS", list.toString())
                }
            }

            override fun onFailure(call : Call<APIResponse>, t : Throwable) {
                onFailure.invoke(t)
                Log.e("UPDATE BRAND ERROR", t.toString())
            }
        })
    }

    override suspend fun fetchBrandDetails(id : Int, storeId : Int, onSuccess : (response : BaseResponse<Brand>) -> Unit, onFailure : (t : Throwable) -> Unit) {
        api.getBrandDetails(id).enqueue(object : Callback<BaseResponse<Brand>> {
            override fun onResponse(call : Call<BaseResponse<Brand>>, response : Response<BaseResponse<Brand>>) {
                response.body()?.let { list->
                    onSuccess.invoke(list)
                    Log.e("BRAND DETAILS", list.toString())
                }
            }

            override fun onFailure(call : Call<BaseResponse<Brand>>, t : Throwable) {
                onFailure.invoke(t)
                Log.e("BRAND DETAILS ERROR", t.toString())
            }
        })
    }

    override suspend fun fetchTaxes(storeId : Int, onSuccess : (response : BaseResponseList<Tax>) -> Unit, onFailure : (t : Throwable) -> Unit) {
        api.getAllTaxes(storeId = storeId).enqueue(object : Callback<BaseResponseList<Tax>> {
            override fun onResponse(
                call : Call<BaseResponseList<Tax>>,
                response : Response<BaseResponseList<Tax>>,
            ) {
                response.body()?.let { list->
                    onSuccess.invoke(list)
                    Log.e("BRANDS", list.toString())
                }
            }

            override fun onFailure(call : Call<BaseResponseList<Tax>>, t : Throwable) {
                onFailure.invoke(t)
                Log.e("BRANDS ERROR", t.toString())
            }
        })
    }

    override suspend fun deleteTax(id : Int, storeId : Int, onSuccess : (response : APIResponse) -> Unit, onFailure : (t : Throwable) -> Unit) {
        api.deleteTax(id).enqueue(object : Callback<APIResponse> {
            override fun onResponse(call : Call<APIResponse>, response : Response<APIResponse>) {
                response.body()?.let { list->
                    onSuccess.invoke(list)
                    Log.e("DELETE TAX SUCCESS", list.toString())
                }
            }

            override fun onFailure(call : Call<APIResponse>, t : Throwable) {
                onFailure.invoke(t)
                Log.e("DELETE TAX SUCCESS", t.toString())
            }
        })
    }

    override suspend fun fetchTaxDetails(id : Int, storeId : Int, onSuccess : (response : BaseResponse<Tax>) -> Unit, onFailure : (t : Throwable) -> Unit) {
        api.getTaxDetails(id).enqueue(object : Callback<BaseResponse<Tax>> {
            override fun onResponse(call : Call<BaseResponse<Tax>>, response : Response<BaseResponse<Tax>>) {
                response.body()?.let { list->
                    onSuccess.invoke(list)
                    Log.e("TAX DETAILS", list.toString())
                }
            }

            override fun onFailure(call : Call<BaseResponse<Tax>>, t : Throwable) {
                onFailure.invoke(t)
                Log.e("TAX DETAILS ERROR", t.toString())
            }
        })
    }

    override suspend fun insertTax(req : TaxInsertRequest, onSuccess : (response : APIResponse) -> Unit, onFailure : (t : Throwable) -> Unit) {
        api.insertTax(req).enqueue(object : Callback<APIResponse> {
            override fun onResponse(call : Call<APIResponse>, response : Response<APIResponse>) {
                response.body()?.let { list->
                    onSuccess.invoke(list)
                    Log.e("INSERT TAX SUCCESS", list.toString())
                }
            }

            override fun onFailure(call : Call<APIResponse>, t : Throwable) {
                onFailure.invoke(t)
                Log.e("INSERT TAX ERROR", t.toString())
            }
        })
    }

    override suspend fun updateTax(req : TaxUpdateRequest, onSuccess : (response : APIResponse) -> Unit, onFailure : (t : Throwable) -> Unit) {
        api.updateTax(req).enqueue(object : Callback<APIResponse> {
            override fun onResponse(call : Call<APIResponse>, response : Response<APIResponse>) {
                response.body()?.let { list->
                    onSuccess.invoke(list)
                    Log.e("UPDATE TAX SUCCESS", list.toString())
                }
            }

            override fun onFailure(call : Call<APIResponse>, t : Throwable) {
                onFailure.invoke(t)
                Log.e("UPDATE TAX ERROR", t.toString())
            }
        })
    }

    override suspend fun fetchTaxGroup(type : Int, storeId : Int, onSuccess : (response : BaseResponseList<CommonDropDown>) -> Unit, onFailure : (t : Throwable) -> Unit) {
        api.getTaxGroups(type = type, storeId = storeId).enqueue(object : Callback<BaseResponseList<CommonDropDown>> {
            override fun onResponse(
                call : Call<BaseResponseList<CommonDropDown>>,
                response : Response<BaseResponseList<CommonDropDown>>,
            ) {
                response.body()?.let { list->
                    onSuccess.invoke(list)
                    Log.e("TAX GROUPS", list.toString())
                }
            }

            override fun onFailure(call : Call<BaseResponseList<CommonDropDown>>, t : Throwable) {
                onFailure.invoke(t)
                Log.e("TAX GROUPS", t.toString())
            }
        })
    }

    override suspend fun fetchGroup(storeId : Int, onSuccess : (response : BaseResponseList<Group>) -> Unit, onFailure : (t : Throwable) -> Unit) {
        api.getGroups(storeId = storeId).enqueue(object : Callback<BaseResponseList<Group>> {
            override fun onResponse(
                call : Call<BaseResponseList<Group>>,
                response : Response<BaseResponseList<Group>>,
            ) {
                response.body()?.let { list->
                    onSuccess.invoke(list)
                    Log.e("GROUPS", list.toString())
                }
            }

            override fun onFailure(call : Call<BaseResponseList<Group>>, t : Throwable) {
                onFailure.invoke(t)
                Log.e("GROUPS", t.toString())
            }
        })
    }

    override suspend fun deleteGroup(id : Int, storeId : Int, onSuccess : (response : APIResponse) -> Unit, onFailure : (t : Throwable) -> Unit) {
        api.deleteGroup(id).enqueue(object : Callback<APIResponse> {
            override fun onResponse(call : Call<APIResponse>, response : Response<APIResponse>) {
                response.body()?.let { list->
                    onSuccess.invoke(list)
                    Log.e("DELETE GROUP SUCCESS", list.toString())
                }
            }

            override fun onFailure(call : Call<APIResponse>, t : Throwable) {
                onFailure.invoke(t)
                Log.e("DELETE GROUP SUCCESS", t.toString())
            }
        })
    }

    override suspend fun insertGroup(req : GroupInsertUpdateRequest, onSuccess : (response : APIResponse) -> Unit, onFailure : (t : Throwable) -> Unit) {
        api.insertGroup(req).enqueue(object : Callback<APIResponse> {
            override fun onResponse(call : Call<APIResponse>, response : Response<APIResponse>) {
                response.body()?.let { list->
                    onSuccess.invoke(list)
                    Log.e("INSERT GROUP SUCCESS", list.toString())
                }
            }

            override fun onFailure(call : Call<APIResponse>, t : Throwable) {
                onFailure.invoke(t)
                Log.e("INSERT GROUP ERROR", t.toString())
            }
        })
    }

    override suspend fun fetchSpecification(storeId : Int, onSuccess : (response : BaseResponseList<Specification>) -> Unit, onFailure : (t : Throwable) -> Unit) {
        api.getSpecification(storeId = storeId).enqueue(object : Callback<BaseResponseList<Specification>> {
            override fun onResponse(
                call : Call<BaseResponseList<Specification>>,
                response : Response<BaseResponseList<Specification>>,
            ) {
                response.body()?.let { list->
                    onSuccess.invoke(list)
                    Log.e("SPECIFICATION", list.toString())
                }
            }

            override fun onFailure(call : Call<BaseResponseList<Specification>>, t : Throwable) {
                onFailure.invoke(t)
                Log.e("SPECIFICATION ERROR", t.toString())
            }
        })
    }

    override suspend fun deleteSpecification(id : Int, storeId : Int, onSuccess : (response : APIResponse) -> Unit, onFailure : (t : Throwable) -> Unit) {
        api.deleteSpecification(id).enqueue(object : Callback<APIResponse> {
            override fun onResponse(call : Call<APIResponse>, response : Response<APIResponse>) {
                response.body()?.let { list->
                    onSuccess.invoke(list)
                    Log.e("DELETE SPECIFICATION SUCCESS", list.toString())
                }
            }

            override fun onFailure(call : Call<APIResponse>, t : Throwable) {
                onFailure.invoke(t)
                Log.e("DELETE SPECIFICATION ERROR", t.toString())
            }
        })
    }

    override suspend fun getUOM(storeId : Int, onSuccess : (response : BaseResponseList<CommonDropDown>) -> Unit, onFailure : (t : Throwable) -> Unit) {
        api.getUOM(storeId = storeId).enqueue(object : Callback<BaseResponseList<CommonDropDown>> {
            override fun onResponse(call : Call<BaseResponseList<CommonDropDown>>, response : Response<BaseResponseList<CommonDropDown>>) {
                response.body()?.let { list->
                    onSuccess.invoke(list)
                    Log.e("UOM DROPDOWN", list.toString())
                }
            }

            override fun onFailure(call : Call<BaseResponseList<CommonDropDown>>, t : Throwable) {
                onFailure.invoke(t)
                Log.e("UOM DROPDOWN ERROR", t.toString())
            }
        })
    }

    override suspend fun getSpecificationType(type : Int, storeId : Int, onSuccess : (response : BaseResponseList<CommonDropDown>) -> Unit, onFailure : (t : Throwable) -> Unit) {
        api.getSpecificationType(typeId = type, storeId = storeId).enqueue(object : Callback<BaseResponseList<CommonDropDown>> {
            override fun onResponse(call : Call<BaseResponseList<CommonDropDown>>, response : Response<BaseResponseList<CommonDropDown>>) {
                response.body()?.let { list->
                    onSuccess.invoke(list)
                    Log.e("SPECIFICATION TYPE DROPDOWN", list.toString())
                }
            }

            override fun onFailure(call : Call<BaseResponseList<CommonDropDown>>, t : Throwable) {
                onFailure.invoke(t)
                Log.e("SPECIFICATION TYPE DROPDOWN ERROR", t.toString())
            }
        })
    }

    override suspend fun insertSpecification(req : SpecificationInsertRequest, onSuccess : (response : APIResponse) -> Unit, onFailure : (t : Throwable) -> Unit) {
        api.insertSpecification(req).enqueue(object : Callback<APIResponse> {
            override fun onResponse(call : Call<APIResponse>, response : Response<APIResponse>) {
                response.body()?.let { list->
                    onSuccess.invoke(list)
                    Log.e("INSERT SPECIFICATION SUCCESS", list.toString())
                }
            }

            override fun onFailure(call : Call<APIResponse>, t : Throwable) {
                onFailure.invoke(t)
                Log.e("INSERT SPECIFICATION ERROR", t.toString())
            }
        })
    }

    override suspend fun insertSpecificationType(req : SpecificationTypeInsertRequest, onSuccess : (response : APIResponse) -> Unit, onFailure : (t : Throwable) -> Unit) {
        api.insertSpecificationType(req).enqueue(object : Callback<APIResponse> {
            override fun onResponse(call : Call<APIResponse>, response : Response<APIResponse>) {
                response.body()?.let { list->
                    onSuccess.invoke(list)
                    Log.e("INSERT SPECIFICATION TYPE SUCCESS", list.toString())
                }
            }

            override fun onFailure(call : Call<APIResponse>, t : Throwable) {
                onFailure.invoke(t)
                Log.e("INSERT SPECIFICATION TYPE ERROR", t.toString())
            }
        })
    }

    override suspend fun fetchFacility(storeId : Int, onSuccess : (response : BaseResponseList<Facility>) -> Unit, onFailure : (t : Throwable) -> Unit) {
        api.getFacility(storeId = storeId).enqueue(object : Callback<BaseResponseList<Facility>> {
            override fun onResponse(
                call : Call<BaseResponseList<Facility>>,
                response : Response<BaseResponseList<Facility>>,
            ) {
                response.body()?.let { list->
                    onSuccess.invoke(list)
                    Log.e("FACILITY", list.toString())
                }
            }

            override fun onFailure(call : Call<BaseResponseList<Facility>>, t : Throwable) {
                onFailure.invoke(t)
                Log.e("FACILITY ERROR", t.toString())
            }
        })
    }

    override suspend fun deleteFacility(id : Int, storeId : Int, onSuccess : (response : APIResponse) -> Unit, onFailure : (t : Throwable) -> Unit) {
        api.deleteFacility(id).enqueue(object : Callback<APIResponse> {
            override fun onResponse(call : Call<APIResponse>, response : Response<APIResponse>) {
                response.body()?.let { list->
                    onSuccess.invoke(list)
                    Log.e("DELETE FACILITY SUCCESS", list.toString())
                }
            }

            override fun onFailure(call : Call<APIResponse>, t : Throwable) {
                onFailure.invoke(t)
                Log.e("DELETE FACILITY ERROR", t.toString())
            }
        })
    }

    override suspend fun fetchSpecificationDetails(id : Int, onSuccess : (response : BaseResponse<Specification>) -> Unit, onFailure : (t : Throwable) -> Unit) {
        api.getSpecificationDetails(id).enqueue(object : Callback<BaseResponse<Specification>> {
            override fun onResponse(call : Call<BaseResponse<Specification>>, response : Response<BaseResponse<Specification>>) {
                response.body()?.let { list->
                    onSuccess.invoke(list)
                    Log.e("SPECIFICATION DETAILS", list.toString())
                }
            }

            override fun onFailure(call : Call<BaseResponse<Specification>>, t : Throwable) {
                onFailure.invoke(t)
                Log.e("SPECIFICATION DETAILS ERROR", t.toString())
            }
        })
    }

    override suspend fun updateSpecification(req : SpecificationUpdateRequest, onSuccess : (response : APIResponse) -> Unit, onFailure : (t : Throwable) -> Unit) {
        api.updateSpecification(req).enqueue(object : Callback<APIResponse> {
            override fun onResponse(call : Call<APIResponse>, response : Response<APIResponse>) {
                response.body()?.let { list->
                    onSuccess.invoke(list)
                    Log.e("UPDATE SPECIFICATION SUCCESS", list.toString())
                }
            }

            override fun onFailure(call : Call<APIResponse>, t : Throwable) {
                onFailure.invoke(t)
                Log.e("UPDATE SPECIFICATION ERROR", t.toString())
            }
        })
    }

    override suspend fun insertFacility(req : FacilityInsertRequest, onSuccess : (response : APIResponse) -> Unit, onFailure : (t : Throwable) -> Unit) {
        api.insertFacility(req).enqueue(object : Callback<APIResponse> {
            override fun onResponse(call : Call<APIResponse>, response : Response<APIResponse>) {
                response.body()?.let { list->
                    onSuccess.invoke(list)
                    Log.e("INSERT FACILITY SUCCESS", list.toString())
                }
            }

            override fun onFailure(call : Call<APIResponse>, t : Throwable) {
                onFailure.invoke(t)
                Log.e("INSERT FACILITY ERROR", t.toString())
            }
        })
    }

    override suspend fun fetchFacilityDetails(id : Int, onSuccess : (response : BaseResponse<Facility>) -> Unit, onFailure : (t : Throwable) -> Unit) {
        api.getFacilityDetails(id).enqueue(object : Callback<BaseResponse<Facility>> {
            override fun onResponse(call : Call<BaseResponse<Facility>>, response : Response<BaseResponse<Facility>>) {
                response.body()?.let { list->
                    onSuccess.invoke(list)
                    Log.e("FACILITY DETAILS", list.toString())
                }
            }

            override fun onFailure(call : Call<BaseResponse<Facility>>, t : Throwable) {
                onFailure.invoke(t)
                Log.e("FACILITY ERROR", t.toString())
            }
        })
    }

    override suspend fun updateFacility(req : FacilityUpdateRequest, onSuccess : (response : APIResponse) -> Unit, onFailure : (t : Throwable) -> Unit) {
        api.updateFacility(req).enqueue(object : Callback<APIResponse> {
            override fun onResponse(call : Call<APIResponse>, response : Response<APIResponse>) {
                response.body()?.let { list->
                    onSuccess.invoke(list)
                    Log.e("UPDATE FACILITY SUCCESS", list.toString())
                }
            }

            override fun onFailure(call : Call<APIResponse>, t : Throwable) {
                onFailure.invoke(t)
                Log.e("UPDATE FACILITY ERROR", t.toString())
            }
        })
    }

    override suspend fun fetchVendors(storeId : Int, onSuccess : (response : BaseResponseList<Vendor>) -> Unit, onFailure : (t : Throwable) -> Unit) {
        api.getVendors(storeId = storeId).enqueue(object : Callback<BaseResponseList<Vendor>> {
            override fun onResponse(
                call : Call<BaseResponseList<Vendor>>,
                response : Response<BaseResponseList<Vendor>>,
            ) {
                response.body()?.let { list->
                    onSuccess.invoke(list)
                    Log.e("VENDORS", list.toString())
                }
            }

            override fun onFailure(call : Call<BaseResponseList<Vendor>>, t : Throwable) {
                onFailure.invoke(t)
                Log.e("VENDORS ERROR", t.toString())
            }
        })
    }

    override suspend fun deleteVendor(id : Int, storeId : Int, onSuccess : (response : APIResponse) -> Unit, onFailure : (t : Throwable) -> Unit) {
        api.deleteVendor(id).enqueue(object : Callback<APIResponse> {
            override fun onResponse(call : Call<APIResponse>, response : Response<APIResponse>) {
                response.body()?.let { list->
                    onSuccess.invoke(list)
                    Log.e("DELETE VENDOR SUCCESS", list.toString())
                }
            }

            override fun onFailure(call : Call<APIResponse>, t : Throwable) {
                onFailure.invoke(t)
                Log.e("DELETE VENDOR ERROR", t.toString())
            }
        })
    }

    override suspend fun fetchVendorDetails(id : Int, storeId : Int, onSuccess : (response : BaseResponse<Vendor>) -> Unit, onFailure : (t : Throwable) -> Unit) {
        api.getVendorDetails(id).enqueue(object : Callback<BaseResponse<Vendor>> {
            override fun onResponse(call : Call<BaseResponse<Vendor>>, response : Response<BaseResponse<Vendor>>) {
                response.body()?.let { list->
                    onSuccess.invoke(list)
                    Log.e("VENDOR DETAILS", list.toString())
                }
            }

            override fun onFailure(call : Call<BaseResponse<Vendor>>, t : Throwable) {
                onFailure.invoke(t)
                Log.e("VENDOR ERROR", t.toString())
            }
        })
    }

    override suspend fun insertVendor(req : VendorInsertRequest, onSuccess : (response : APIResponse) -> Unit, onFailure : (t : Throwable) -> Unit) {
        api.insertVendor(req).enqueue(object : Callback<APIResponse> {
            override fun onResponse(call : Call<APIResponse>, response : Response<APIResponse>) {
                response.body()?.let { list->
                    onSuccess.invoke(list)
                    Log.e("INSERT VENDOR SUCCESS", list.toString())
                }
            }

            override fun onFailure(call : Call<APIResponse>, t : Throwable) {
                onFailure.invoke(t)
                Log.e("INSERT VENDOR ERROR", t.toString())
            }
        })
    }

    override suspend fun updateVendor(req : VendorUpdateRequest, onSuccess : (response : APIResponse) -> Unit, onFailure : (t : Throwable) -> Unit) {
        api.updateVendor(req).enqueue(object : Callback<APIResponse> {
            override fun onResponse(call : Call<APIResponse>, response : Response<APIResponse>) {
                response.body()?.let { list->
                    onSuccess.invoke(list)
                    Log.e("UPDATE VENDOR SUCCESS", list.toString())
                }
            }

            override fun onFailure(call : Call<APIResponse>, t : Throwable) {
                onFailure.invoke(t)
                Log.e("UPDATE VENDOR ERROR", t.toString())
            }
        })
    }

    override suspend fun fetchUOM(storeId : Int, onSuccess : (response : BaseResponseList<UOM>) -> Unit, onFailure : (t : Throwable) -> Unit) {
        api.fetchUOM(storeId = storeId).enqueue(object : Callback<BaseResponseList<UOM>> {
            override fun onResponse(
                call : Call<BaseResponseList<UOM>>,
                response : Response<BaseResponseList<UOM>>,
            ) {
                response.body()?.let { list->
                    onSuccess.invoke(list)
                    Log.e("UOM", list.toString())
                }
            }

            override fun onFailure(call : Call<BaseResponseList<UOM>>, t : Throwable) {
                onFailure.invoke(t)
                Log.e("UOM ERROR", t.toString())
            }
        })
    }

    override suspend fun deleteUOM(id : Int, storeId : Int, onSuccess : (response : APIResponse) -> Unit, onFailure : (t : Throwable) -> Unit) {
        api.deleteUOM(id).enqueue(object : Callback<APIResponse> {
            override fun onResponse(call : Call<APIResponse>, response : Response<APIResponse>) {
                response.body()?.let { list->
                    onSuccess.invoke(list)
                    Log.e("DELETE UOM SUCCESS", list.toString())
                }
            }

            override fun onFailure(call : Call<APIResponse>, t : Throwable) {
                onFailure.invoke(t)
                Log.e("DELETE UOM ERROR", t.toString())
            }
        })
    }

    override suspend fun insertUOM(req : UOMInsertRequest, onSuccess : (response : APIResponse) -> Unit, onFailure : (t : Throwable) -> Unit) {
        api.insertUOM(req).enqueue(object : Callback<APIResponse> {
            override fun onResponse(call : Call<APIResponse>, response : Response<APIResponse>) {
                response.body()?.let { list->
                    onSuccess.invoke(list)
                    Log.e("INSERT UOM SUCCESS", list.toString())
                }
            }

            override fun onFailure(call : Call<APIResponse>, t : Throwable) {
                onFailure.invoke(t)
                Log.e("INSERT UOM ERROR", t.toString())
            }
        })
    }

    override suspend fun fetchUOMDetails(id : Int, onSuccess : (response : BaseResponse<UOM>) -> Unit, onFailure : (t : Throwable) -> Unit) {
        api.getUOMDetails(id).enqueue(object : Callback<BaseResponse<UOM>> {
            override fun onResponse(call : Call<BaseResponse<UOM>>, response : Response<BaseResponse<UOM>>) {
                response.body()?.let { list->
                    onSuccess.invoke(list)
                    Log.e("UOM DETAILS", list.toString())
                }
            }

            override fun onFailure(call : Call<BaseResponse<UOM>>, t : Throwable) {
                onFailure.invoke(t)
                Log.e("UOM ERROR", t.toString())
            }
        })
    }

    override suspend fun updateUOM(req : UOMUpdateRequest, onSuccess : (response : APIResponse) -> Unit, onFailure : (t : Throwable) -> Unit) {
        api.updateUOM(req).enqueue(object : Callback<APIResponse> {
            override fun onResponse(call : Call<APIResponse>, response : Response<APIResponse>) {
                response.body()?.let { list->
                    onSuccess.invoke(list)
                    Log.e("UPDATE UOM SUCCESS", list.toString())
                }
            }

            override fun onFailure(call : Call<APIResponse>, t : Throwable) {
                onFailure.invoke(t)
                Log.e("UPDATE UOM ERROR", t.toString())
            }
        })
    }

    override suspend fun getSpecificationDropDown(storeId : Int, onSuccess : (response : BaseResponseList<CommonDropDown>) -> Unit, onFailure : (t : Throwable) -> Unit) {
        api.getSpecificationDropDown(storeId = storeId).enqueue(object : Callback<BaseResponseList<CommonDropDown>> {
            override fun onResponse(call : Call<BaseResponseList<CommonDropDown>>, response : Response<BaseResponseList<CommonDropDown>>) {
                response.body()?.let { list->
                    onSuccess.invoke(list)
                    Log.e("SPECIFICATION DROPDOWN", list.toString())
                }
            }

            override fun onFailure(call : Call<BaseResponseList<CommonDropDown>>, t : Throwable) {
                onFailure.invoke(t)
                Log.e("SPECIFICATION DROPDOWN ERROR", t.toString())
            }
        })
    }

    override suspend fun getPackDropDown(storeId : Int, onSuccess : (response : BaseResponseList<CommonDropDown>) -> Unit, onFailure : (t : Throwable) -> Unit) {
        api.getPackDropDown(storeId = storeId).enqueue(object : Callback<BaseResponseList<CommonDropDown>> {
            override fun onResponse(call : Call<BaseResponseList<CommonDropDown>>, response : Response<BaseResponseList<CommonDropDown>>) {
                response.body()?.let { list->
                    onSuccess.invoke(list)
                    Log.e("PACK DROPDOWN", list.toString())
                }
            }

            override fun onFailure(call : Call<BaseResponseList<CommonDropDown>>, t : Throwable) {
                onFailure.invoke(t)
                Log.e("PACK DROPDOWN ERROR", t.toString())
            }
        })
    }

    override suspend fun getBrandDropDown(storeId : Int, onSuccess : (response : BaseResponseList<CommonDropDown>) -> Unit, onFailure : (t : Throwable) -> Unit) {
        api.getBrandDropDown(storeId = storeId).enqueue(object : Callback<BaseResponseList<CommonDropDown>> {
            override fun onResponse(call : Call<BaseResponseList<CommonDropDown>>, response : Response<BaseResponseList<CommonDropDown>>) {
                response.body()?.let { list->
                    onSuccess.invoke(list)
                    Log.e("BRAND DROPDOWN", list.toString())
                }
            }

            override fun onFailure(call : Call<BaseResponseList<CommonDropDown>>, t : Throwable) {
                onFailure.invoke(t)
                Log.e("BRAND DROPDOWN ERROR", t.toString())
            }
        })
    }

    override suspend fun getDepartmentItems(depID : Int, onSuccess : (response : BaseResponseList<ItemCount>) -> Unit, onFailure : (t : Throwable) -> Unit) {
        api.getDepartmentItems(id = depID).enqueue(object : Callback<BaseResponseList<ItemCount>> {
            override fun onResponse(
                call : Call<BaseResponseList<ItemCount>>,
                response : Response<BaseResponseList<ItemCount>>,
            ) {
                response.body()?.let { list->
                    onSuccess.invoke(list)
                    Log.e("DEPARTMENTS ITEM COUNT", list.toString())
                }
            }

            override fun onFailure(call : Call<BaseResponseList<ItemCount>>, t : Throwable) {
                onFailure.invoke(t)
                Log.e("DEPARTMENTS ITEM COUNT ERROR", t.toString())
            }
        })
    }

    override suspend fun getBrandItems(brandID : Int, onSuccess : (response : BaseResponseList<ItemCount>) -> Unit, onFailure : (t : Throwable) -> Unit) {
        api.getBrandItems(id = brandID).enqueue(object : Callback<BaseResponseList<ItemCount>> {
            override fun onResponse(
                call : Call<BaseResponseList<ItemCount>>,
                response : Response<BaseResponseList<ItemCount>>,
            ) {
                response.body()?.let { list->
                    onSuccess.invoke(list)
                    Log.e("BRAND ITEM COUNT", list.toString())
                }
            }

            override fun onFailure(call : Call<BaseResponseList<ItemCount>>, t : Throwable) {
                onFailure.invoke(t)
                Log.e("BRAND ITEMS COUNT ERROR", t.toString())
            }
        })
    }

    override suspend fun getCategoryItems(catID : Int, onSuccess : (response : BaseResponseList<ItemCount>) -> Unit, onFailure : (t : Throwable) -> Unit) {
        api.getCategoryItems(id = catID).enqueue(object : Callback<BaseResponseList<ItemCount>> {
            override fun onResponse(
                call : Call<BaseResponseList<ItemCount>>,
                response : Response<BaseResponseList<ItemCount>>,
            ) {
                response.body()?.let { list->
                    onSuccess.invoke(list)
                    Log.e("CATEGORY ITEM COUNT", list.toString())
                }
            }

            override fun onFailure(call : Call<BaseResponseList<ItemCount>>, t : Throwable) {
                onFailure.invoke(t)
                Log.e("CATEGORY ITEMS COUNT ERROR", t.toString())
            }
        })
    }

    override suspend fun getGroupTypeDropdown(type : Int, onSuccess : (response : BaseResponseList<CommonDropDown>) -> Unit, onFailure : (t : Throwable) -> Unit) {
        api.getGroupTypeDropdown(type = type).enqueue(object : Callback<BaseResponseList<CommonDropDown>> {
            override fun onResponse(call : Call<BaseResponseList<CommonDropDown>>, response : Response<BaseResponseList<CommonDropDown>>) {
                response.body()?.let { list->
                    onSuccess.invoke(list)
                    Log.e("GROUP TYPE DROPDOWN", list.toString())
                }
            }

            override fun onFailure(call : Call<BaseResponseList<CommonDropDown>>, t : Throwable) {
                onFailure.invoke(t)
                Log.e("GROUP TYPE DROPDOWN ERROR", t.toString())
            }
        })
    }

    override suspend fun getGroupTypeList(type : Int, storeId : Int, onSuccess : (response : BaseResponseList<Group>) -> Unit, onFailure : (t : Throwable) -> Unit) {
        api.getGroupTypeList(type = type, storeId = storeId).enqueue(object : Callback<BaseResponseList<Group>> {
            override fun onResponse(
                call : Call<BaseResponseList<Group>>,
                response : Response<BaseResponseList<Group>>,
            ) {
                response.body()?.let { list->
                    onSuccess.invoke(list)
                    Log.e("GROUP TYPE", list.toString())
                }
            }

            override fun onFailure(call : Call<BaseResponseList<Group>>, t : Throwable) {
                onFailure.invoke(t)
                Log.e("GROUP TYPE ERROR", t.toString())
            }
        })
    }

    override suspend fun getSubCategoryDropDown(catID : Int, onSuccess : (response : BaseResponseList<CommonDropDown>) -> Unit, onFailure : (t : Throwable) -> Unit) {
        api.getSubCategoryDropDown(catID = catID).enqueue(object : Callback<BaseResponseList<CommonDropDown>> {
            override fun onResponse(call : Call<BaseResponseList<CommonDropDown>>, response : Response<BaseResponseList<CommonDropDown>>) {
                response.body()?.let { list->
                    onSuccess.invoke(list)
                    Log.e("SUBCATEGORY DROPDOWN", list.toString())
                }
            }

            override fun onFailure(call : Call<BaseResponseList<CommonDropDown>>, t : Throwable) {
                onFailure.invoke(t)
                Log.e("SUBCATEGORY DROPDOWN ERROR", t.toString())
            }
        })
    }

    override suspend fun getGroupDetails(groupId : Int, storeId : Int, onSuccess : (response : BaseResponse<GroupInsertUpdateRequest>) -> Unit, onFailure : (t : Throwable) -> Unit) {
        api.getGroupDetails(groupId = groupId, storeId = storeId).enqueue(object : Callback<BaseResponse<GroupInsertUpdateRequest>> {
            override fun onResponse(
                call : Call<BaseResponse<GroupInsertUpdateRequest>>,
                response : Response<BaseResponse<GroupInsertUpdateRequest>>,
            ) {
                response.body()?.let { list->
                    onSuccess.invoke(list)
                    Log.e("GROUP DETAILS", list.toString())
                }
            }

            override fun onFailure(call : Call<BaseResponse<GroupInsertUpdateRequest>>, t : Throwable) {
                onFailure.invoke(t)
                Log.e("GROUP DETAILS ERROR", t.toString())
            }
        })
    }

    override suspend fun updateGroup(req : GroupInsertUpdateRequest, onSuccess : (response : APIResponse) -> Unit, onFailure : (t : Throwable) -> Unit) {
        api.updateGroup(req).enqueue(object : Callback<APIResponse> {
            override fun onResponse(call : Call<APIResponse>, response : Response<APIResponse>) {
                response.body()?.let { list->
                    onSuccess.invoke(list)
                    Log.e("UPDATE GROUP SUCCESS", list.toString())
                }
            }

            override fun onFailure(call : Call<APIResponse>, t : Throwable) {
                onFailure.invoke(t)
                Log.e("UPDATE GROUP ERROR", t.toString())
            }
        })
    }

    override suspend fun getVendorDropDown(storeId : Int, onSuccess : (response : BaseResponseList<CommonDropDown>) -> Unit, onFailure : (t : Throwable) -> Unit) {
        api.getVendorDropDown(storeId = storeId).enqueue(object : Callback<BaseResponseList<CommonDropDown>> {
            override fun onResponse(call : Call<BaseResponseList<CommonDropDown>>, response : Response<BaseResponseList<CommonDropDown>>) {
                response.body()?.let { list->
                    onSuccess.invoke(list)
                    Log.e("VENDOR DROPDOWN", list.toString())
                }
            }

            override fun onFailure(call : Call<BaseResponseList<CommonDropDown>>, t : Throwable) {
                onFailure.invoke(t)
                Log.e("VENDOR DROPDOWN ERROR", t.toString())
            }
        })
    }

    override suspend fun getGroupTypeDetails(groupId : Int, storeId : Int, onSuccess : (response : BaseResponse<GroupInsertUpdateRequest>) -> Unit, onFailure : (t : Throwable) -> Unit) {
        api.getGroupTypeDetails(groupId = groupId).enqueue(object : Callback<BaseResponse<GroupInsertUpdateRequest>> {
            override fun onResponse(
                call : Call<BaseResponse<GroupInsertUpdateRequest>>,
                response : Response<BaseResponse<GroupInsertUpdateRequest>>,
            ) {
                response.body()?.let { list->
                    onSuccess.invoke(list)
                    Log.e("GROUP DETAILS", list.toString())
                }
            }

            override fun onFailure(call : Call<BaseResponse<GroupInsertUpdateRequest>>, t : Throwable) {
                onFailure.invoke(t)
                Log.e("GROUP DETAILS ERROR", t.toString())
            }
        })
    }

    override suspend fun addSalesPerson(req : SalesPerson, onSuccess : (response : APIResponse) -> Unit, onFailure : (t : Throwable) -> Unit) {
        api.addSalesPerson(req).enqueue(object : Callback<APIResponse> {
            override fun onResponse(call : Call<APIResponse>, response : Response<APIResponse>) {
                response.body()?.let { list->
                    onSuccess.invoke(list)
                    Log.e("ADD SALES PERSON SUCCESS", list.toString())
                }
            }

            override fun onFailure(call : Call<APIResponse>, t : Throwable) {
                onFailure.invoke(t)
                Log.e("ADD SALES PERSON ERROR", t.toString())
            }
        })
    }

    override suspend fun getSalesPersonCount(id : Int, onSuccess : (response : BaseResponseList<SalesPerson>) -> Unit, onFailure : (t : Throwable) -> Unit) {
        api.getSalesPersonCount(id = id).enqueue(object : Callback<BaseResponseList<SalesPerson>> {
            override fun onResponse(call : Call<BaseResponseList<SalesPerson>>, response : Response<BaseResponseList<SalesPerson>>) {
                response.body()?.let { list->
                    onSuccess.invoke(list)
                    Log.e("SALES PERSON COUNT", list.toString())
                }
            }

            override fun onFailure(call : Call<BaseResponseList<SalesPerson>>, t : Throwable) {
                onFailure.invoke(t)
                Log.e("SALES PERSON COUNT ERROR", t.toString())
            }
        })
    }
}