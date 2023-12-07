package com.varitas.gokulpos.repositories

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


interface ProductFeatureRepository {
    suspend fun fetchDepartments(storeId : Int, onSuccess : (response : BaseResponseList<Department>) -> Unit, onFailure : (t : Throwable) -> Unit)
    suspend fun fetchCategories(storeId : Int, onSuccess : (response : BaseResponseList<Category>) -> Unit, onFailure : (t : Throwable) -> Unit)
    suspend fun fetchAllCategories(storeId : Int, onSuccess : (response : BaseResponseList<DropDown>) -> Unit, onFailure : (t : Throwable) -> Unit)
    suspend fun insertCategories(req : CategoryInsertUpdate, onSuccess : (response : APIResponse) -> Unit, onFailure : (t : Throwable) -> Unit)
    suspend fun insertDepartment(req : DepartmentInsertUpdate, onSuccess : (response : APIResponse) -> Unit, onFailure : (t : Throwable) -> Unit)
    suspend fun fetchCategoryDetails(catId : Int, onSuccess : (response : BaseResponse<CategoryInsertUpdate>) -> Unit, onFailure : (t : Throwable) -> Unit)
    suspend fun fetchSubCategories(categoryId : Int, onSuccess : (response : BaseResponseList<SubCategory>) -> Unit, onFailure : (t : Throwable) -> Unit)
    suspend fun deleteDepartment(id : Int, storeId : Int, onSuccess : (response : APIResponse) -> Unit, onFailure : (t : Throwable) -> Unit)
    suspend fun fetchDepartmentDetails(id : Int, storeId : Int, onSuccess : (response : BaseResponse<DepartmentInsertUpdate>) -> Unit, onFailure : (t : Throwable) -> Unit)
    suspend fun updateDepartment(req : DepartmentInsertUpdate, onSuccess : (response : APIResponse) -> Unit, onFailure : (t : Throwable) -> Unit)
    suspend fun deleteCategory(id : Int, storeId : Int, onSuccess : (response : APIResponse) -> Unit, onFailure : (t : Throwable) -> Unit)
    suspend fun getParentCategory(storeId : Int, onSuccess : (response : BaseResponseList<CommonDropDown>) -> Unit, onFailure : (t : Throwable) -> Unit)
    suspend fun getDepartment(storeId : Int, onSuccess : (response : BaseResponseList<CommonDropDown>) -> Unit, onFailure : (t : Throwable) -> Unit)
    suspend fun updateCategory(req : CategoryInsertUpdate, onSuccess : (response : APIResponse) -> Unit, onFailure : (t : Throwable) -> Unit)
    suspend fun fetchBrands(storeId : Int, onSuccess : (response : BaseResponseList<Brand>) -> Unit, onFailure : (t : Throwable) -> Unit)
    suspend fun deleteBrand(id : Int, storeId : Int, onSuccess : (response : APIResponse) -> Unit, onFailure : (t : Throwable) -> Unit)
    suspend fun insertBrand(req : BrandInsertRequest, onSuccess : (response : APIResponse) -> Unit, onFailure : (t : Throwable) -> Unit)
    suspend fun updateBrand(req : BrandUpdateRequest, onSuccess : (response : APIResponse) -> Unit, onFailure : (t : Throwable) -> Unit)
    suspend fun fetchBrandDetails(id : Int, storeId : Int, onSuccess : (response : BaseResponse<Brand>) -> Unit, onFailure : (t : Throwable) -> Unit)
    suspend fun fetchTaxes(storeId : Int, onSuccess : (response : BaseResponseList<Tax>) -> Unit, onFailure : (t : Throwable) -> Unit)
    suspend fun deleteTax(id : Int, storeId : Int, onSuccess : (response : APIResponse) -> Unit, onFailure : (t : Throwable) -> Unit)
    suspend fun fetchTaxDetails(id : Int, storeId : Int, onSuccess : (response : BaseResponse<Tax>) -> Unit, onFailure : (t : Throwable) -> Unit)
    suspend fun insertTax(req : TaxInsertRequest, onSuccess : (response : APIResponse) -> Unit, onFailure : (t : Throwable) -> Unit)
    suspend fun updateTax(req : TaxUpdateRequest, onSuccess : (response : APIResponse) -> Unit, onFailure : (t : Throwable) -> Unit)
    suspend fun fetchTaxGroup(type : Int, storeId : Int, onSuccess : (response : BaseResponseList<CommonDropDown>) -> Unit, onFailure : (t : Throwable) -> Unit)
    suspend fun fetchGroup(storeId : Int, onSuccess : (response : BaseResponseList<Group>) -> Unit, onFailure : (t : Throwable) -> Unit)
    suspend fun deleteGroup(id : Int, storeId : Int, onSuccess : (response : APIResponse) -> Unit, onFailure : (t : Throwable) -> Unit)
    suspend fun fetchSpecification(storeId : Int, onSuccess : (response : BaseResponseList<Specification>) -> Unit, onFailure : (t : Throwable) -> Unit)
    suspend fun deleteSpecification(id : Int, storeId : Int, onSuccess : (response : APIResponse) -> Unit, onFailure : (t : Throwable) -> Unit)
    suspend fun getUOM(storeId : Int, onSuccess : (response : BaseResponseList<CommonDropDown>) -> Unit, onFailure : (t : Throwable) -> Unit)
    suspend fun getSpecificationType(type : Int, storeId : Int, onSuccess : (response : BaseResponseList<CommonDropDown>) -> Unit, onFailure : (t : Throwable) -> Unit)
    suspend fun insertSpecification(req : SpecificationInsertRequest, onSuccess : (response : APIResponse) -> Unit, onFailure : (t : Throwable) -> Unit)
    suspend fun insertSpecificationType(req : SpecificationTypeInsertRequest, onSuccess : (response : APIResponse) -> Unit, onFailure : (t : Throwable) -> Unit)
    suspend fun fetchFacility(storeId : Int, onSuccess : (response : BaseResponseList<Facility>) -> Unit, onFailure : (t : Throwable) -> Unit)
    suspend fun deleteFacility(id : Int, storeId : Int, onSuccess : (response : APIResponse) -> Unit, onFailure : (t : Throwable) -> Unit)
    suspend fun fetchSpecificationDetails(id : Int, onSuccess : (response : BaseResponse<Specification>) -> Unit, onFailure : (t : Throwable) -> Unit)
    suspend fun updateSpecification(req : SpecificationUpdateRequest, onSuccess : (response : APIResponse) -> Unit, onFailure : (t : Throwable) -> Unit)
    suspend fun insertFacility(req : FacilityInsertRequest, onSuccess : (response : APIResponse) -> Unit, onFailure : (t : Throwable) -> Unit)
    suspend fun fetchFacilityDetails(id : Int, onSuccess : (response : BaseResponse<Facility>) -> Unit, onFailure : (t : Throwable) -> Unit)
    suspend fun updateFacility(req : FacilityUpdateRequest, onSuccess : (response : APIResponse) -> Unit, onFailure : (t : Throwable) -> Unit)
    suspend fun insertGroup(req : GroupInsertUpdateRequest, onSuccess : (response : APIResponse) -> Unit, onFailure : (t : Throwable) -> Unit)
    suspend fun fetchVendors(storeId : Int, onSuccess : (response : BaseResponseList<Vendor>) -> Unit, onFailure : (t : Throwable) -> Unit)
    suspend fun deleteVendor(id : Int, storeId : Int, onSuccess : (response : APIResponse) -> Unit, onFailure : (t : Throwable) -> Unit)
    suspend fun fetchVendorDetails(id : Int, storeId : Int, onSuccess : (response : BaseResponse<Vendor>) -> Unit, onFailure : (t : Throwable) -> Unit)
    suspend fun insertVendor(req : VendorInsertRequest, onSuccess : (response : APIResponse) -> Unit, onFailure : (t : Throwable) -> Unit)
    suspend fun updateVendor(req : VendorUpdateRequest, onSuccess : (response : APIResponse) -> Unit, onFailure : (t : Throwable) -> Unit)
    suspend fun fetchUOM(storeId : Int, onSuccess : (response : BaseResponseList<UOM>) -> Unit, onFailure : (t : Throwable) -> Unit)
    suspend fun deleteUOM(id : Int, storeId : Int, onSuccess : (response : APIResponse) -> Unit, onFailure : (t : Throwable) -> Unit)
    suspend fun insertUOM(req : UOMInsertRequest, onSuccess : (response : APIResponse) -> Unit, onFailure : (t : Throwable) -> Unit)
    suspend fun fetchUOMDetails(id : Int, onSuccess : (response : BaseResponse<UOM>) -> Unit, onFailure : (t : Throwable) -> Unit)
    suspend fun updateUOM(req : UOMUpdateRequest, onSuccess : (response : APIResponse) -> Unit, onFailure : (t : Throwable) -> Unit)
    suspend fun getSpecificationDropDown(storeId : Int, onSuccess : (response : BaseResponseList<CommonDropDown>) -> Unit, onFailure : (t : Throwable) -> Unit)
    suspend fun getPackDropDown(storeId : Int, onSuccess : (response : BaseResponseList<CommonDropDown>) -> Unit, onFailure : (t : Throwable) -> Unit)
    suspend fun getBrandDropDown(storeId : Int, onSuccess : (response : BaseResponseList<CommonDropDown>) -> Unit, onFailure : (t : Throwable) -> Unit)
    suspend fun getDepartmentItems(depID : Int, onSuccess : (response : BaseResponseList<ItemCount>) -> Unit, onFailure : (t : Throwable) -> Unit)
    suspend fun getBrandItems(brandID : Int, onSuccess : (response : BaseResponseList<ItemCount>) -> Unit, onFailure : (t : Throwable) -> Unit)
    suspend fun getCategoryItems(catID : Int, onSuccess : (response : BaseResponseList<ItemCount>) -> Unit, onFailure : (t : Throwable) -> Unit)
    suspend fun getGroupTypeDropdown(type : Int, onSuccess : (response : BaseResponseList<CommonDropDown>) -> Unit, onFailure : (t : Throwable) -> Unit)
    suspend fun getGroupTypeList(type : Int, storeId : Int, onSuccess : (response : BaseResponseList<Group>) -> Unit, onFailure : (t : Throwable) -> Unit)
    suspend fun getSubCategoryDropDown(catID : Int, onSuccess : (response : BaseResponseList<CommonDropDown>) -> Unit, onFailure : (t : Throwable) -> Unit)
    suspend fun getGroupDetails(groupId : Int, storeId : Int, onSuccess : (response : BaseResponse<GroupInsertUpdateRequest>) -> Unit, onFailure : (t : Throwable) -> Unit)
    suspend fun updateGroup(req : GroupInsertUpdateRequest, onSuccess : (response : APIResponse) -> Unit, onFailure : (t : Throwable) -> Unit)
    suspend fun getVendorDropDown(storeId : Int, onSuccess : (response : BaseResponseList<CommonDropDown>) -> Unit, onFailure : (t : Throwable) -> Unit)
    suspend fun getGroupTypeDetails(groupId: Int, storeId: Int, onSuccess: (response: BaseResponse<GroupInsertUpdateRequest>) -> Unit, onFailure: (t: Throwable) -> Unit)
    suspend fun addSalesPerson(req : SalesPerson, onSuccess : (response : APIResponse) -> Unit, onFailure : (t : Throwable) -> Unit)
    suspend fun getSalesPersonCount(id : Int, onSuccess : (response : BaseResponseList<SalesPerson>) -> Unit, onFailure : (t : Throwable) -> Unit)
}