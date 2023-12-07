package com.varitas.gokulpos.tablet.repositories

import com.varitas.gokulpos.tablet.model.Brand
import com.varitas.gokulpos.tablet.model.BrandDetails
import com.varitas.gokulpos.tablet.model.Category
import com.varitas.gokulpos.tablet.model.CategoryInsertUpdate
import com.varitas.gokulpos.tablet.model.GroupDetail
import com.varitas.gokulpos.tablet.model.GroupInsertUpdateRequest
import com.varitas.gokulpos.tablet.model.ItemGroupDetail
import com.varitas.gokulpos.tablet.model.SelectedGroups
import com.varitas.gokulpos.tablet.request.AddSalePerson
import com.varitas.gokulpos.tablet.request.DepartmentInsertUpdate
import com.varitas.gokulpos.tablet.request.FacilityInsertRequest
import com.varitas.gokulpos.tablet.request.FacilityUpdateRequest
import com.varitas.gokulpos.tablet.request.ItemGroupRequest
import com.varitas.gokulpos.tablet.request.SpecificationInsertRequest
import com.varitas.gokulpos.tablet.request.SpecificationTypeInsertRequest
import com.varitas.gokulpos.tablet.request.SpecificationUpdateRequest
import com.varitas.gokulpos.tablet.request.TaxInsertRequest
import com.varitas.gokulpos.tablet.request.TaxUpdateRequest
import com.varitas.gokulpos.tablet.request.UOMInsertRequest
import com.varitas.gokulpos.tablet.request.UOMUpdateRequest
import com.varitas.gokulpos.tablet.request.VendorInsertRequest
import com.varitas.gokulpos.tablet.request.VendorUpdateRequest
import com.varitas.gokulpos.tablet.response.APIResponse
import com.varitas.gokulpos.tablet.response.BaseResponse
import com.varitas.gokulpos.tablet.response.BaseResponseList
import com.varitas.gokulpos.tablet.response.CommonDropDown
import com.varitas.gokulpos.tablet.response.Department
import com.varitas.gokulpos.tablet.response.DropDown
import com.varitas.gokulpos.tablet.response.Facility
import com.varitas.gokulpos.tablet.response.ItemCount
import com.varitas.gokulpos.tablet.response.ItemGroupTax
import com.varitas.gokulpos.tablet.response.Specification
import com.varitas.gokulpos.tablet.response.SubCategory
import com.varitas.gokulpos.tablet.response.Tax
import com.varitas.gokulpos.tablet.response.UOM
import com.varitas.gokulpos.tablet.response.Vendor


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

    suspend fun getSubCategory(parentId : Int, onSuccess : (response : BaseResponseList<CommonDropDown>) -> Unit, onFailure : (t : Throwable) -> Unit)

    suspend fun getDepartment(storeId : Int, onSuccess : (response : BaseResponseList<CommonDropDown>) -> Unit, onFailure : (t : Throwable) -> Unit)

    suspend fun updateCategory(req : CategoryInsertUpdate, onSuccess : (response : APIResponse) -> Unit, onFailure : (t : Throwable) -> Unit)

    suspend fun fetchBrands(storeId : Int, onSuccess : (response : BaseResponseList<Brand>) -> Unit, onFailure : (t : Throwable) -> Unit)

    suspend fun deleteBrand(id : Int, storeId : Int, onSuccess : (response : APIResponse) -> Unit, onFailure : (t : Throwable) -> Unit)

    suspend fun insertBrand(req : BrandDetails, onSuccess : (response : APIResponse) -> Unit, onFailure : (t : Throwable) -> Unit)

    suspend fun updateBrand(req : BrandDetails, onSuccess : (response : APIResponse) -> Unit, onFailure : (t : Throwable) -> Unit)

    suspend fun fetchBrandDetails(id : Int, storeId : Int, onSuccess : (response : BaseResponse<Brand>) -> Unit, onFailure : (t : Throwable) -> Unit)

    suspend fun fetchTaxes(storeId : Int, onSuccess : (response : BaseResponseList<Tax>) -> Unit, onFailure : (t : Throwable) -> Unit)

    suspend fun fetchTaxesFromGroup(groupId : Int, onSuccess : (response : BaseResponseList<Tax>) -> Unit, onFailure : (t : Throwable) -> Unit)

    suspend fun deleteTax(id : Int, storeId : Int, onSuccess : (response : APIResponse) -> Unit, onFailure : (t : Throwable) -> Unit)

    suspend fun fetchTaxDetails(id : Int, storeId : Int, onSuccess : (response : BaseResponse<Tax>) -> Unit, onFailure : (t : Throwable) -> Unit)

    suspend fun insertTax(req : TaxInsertRequest, onSuccess : (response : APIResponse) -> Unit, onFailure : (t : Throwable) -> Unit)

    suspend fun updateTax(req : TaxUpdateRequest, onSuccess : (response : APIResponse) -> Unit, onFailure : (t : Throwable) -> Unit)

    suspend fun fetchTaxGroup(storeId : Int, typeId : Int, onSuccess : (response : BaseResponseList<CommonDropDown>) -> Unit, onFailure : (t : Throwable) -> Unit)

    suspend fun fetchGroup(storeId: Int, onSuccess: (response: BaseResponseList<SelectedGroups>) -> Unit, onFailure: (t: Throwable) -> Unit)

    suspend fun deleteGroup(id : Int, storeId : Int, onSuccess : (response : APIResponse) -> Unit, onFailure : (t : Throwable) -> Unit)

    suspend fun fetchSpecification(storeId : Int, onSuccess : (response : BaseResponseList<Specification>) -> Unit, onFailure : (t : Throwable) -> Unit)

    suspend fun deleteSpecification(id : Int, storeId : Int, onSuccess : (response : APIResponse) -> Unit, onFailure : (t : Throwable) -> Unit)

    suspend fun getUOM(storeId : Int, onSuccess : (response : BaseResponseList<CommonDropDown>) -> Unit, onFailure : (t : Throwable) -> Unit)

    suspend fun getSpecificationType(storeId : Int,typeId : Int, onSuccess : (response : BaseResponseList<CommonDropDown>) -> Unit, onFailure : (t : Throwable) -> Unit)

    suspend fun getItemSpecification(itemId : Int, onSuccess : (response : BaseResponseList<CommonDropDown>) -> Unit, onFailure : (t : Throwable) -> Unit)

    suspend fun insertSpecification(req : SpecificationInsertRequest, onSuccess : (response : APIResponse) -> Unit, onFailure : (t : Throwable) -> Unit)

    suspend fun insertSpecificationType(req : SpecificationTypeInsertRequest, onSuccess : (response : APIResponse) -> Unit, onFailure : (t : Throwable) -> Unit)

    suspend fun fetchFacility(storeId : Int, onSuccess : (response : BaseResponseList<Facility>) -> Unit, onFailure : (t : Throwable) -> Unit)

    suspend fun deleteFacility(id : Int, storeId : Int, onSuccess : (response : APIResponse) -> Unit, onFailure : (t : Throwable) -> Unit)

    suspend fun fetchSpecificationDetails(id : Int, onSuccess : (response : BaseResponse<Specification>) -> Unit, onFailure : (t : Throwable) -> Unit)

    suspend fun fetchGroupDetails(groupId : Int, itemId : Int, specification : Int, price : Double, onSuccess : (response : BaseResponse<ItemGroupTax>) -> Unit, onFailure : (t : Throwable) -> Unit)

    suspend fun updateSpecification(req : SpecificationUpdateRequest, onSuccess : (response : APIResponse) -> Unit, onFailure : (t : Throwable) -> Unit)

    suspend fun insertFacility(req : FacilityInsertRequest, onSuccess : (response : APIResponse) -> Unit, onFailure : (t : Throwable) -> Unit)

    suspend fun fetchFacilityDetails(id : Int, onSuccess : (response : BaseResponse<Facility>) -> Unit, onFailure : (t : Throwable) -> Unit)

    suspend fun updateFacility(req : FacilityUpdateRequest, onSuccess : (response : APIResponse) -> Unit, onFailure : (t : Throwable) -> Unit)

    suspend fun insertGroup(req : GroupInsertUpdateRequest, onSuccess : (response : APIResponse) -> Unit, onFailure : (t : Throwable) -> Unit)

    suspend fun insertItemToGroup(req : ItemGroupRequest, onSuccess : (response : APIResponse) -> Unit, onFailure : (t : Throwable) -> Unit)

    suspend fun fetchVendors(storeId: Int, onSuccess: (response: BaseResponseList<Vendor>) -> Unit, onFailure: (t: Throwable) -> Unit)

    suspend fun fetchVendorDropDown(storeId: Int, onSuccess: (response: BaseResponseList<CommonDropDown>) -> Unit, onFailure: (t: Throwable) -> Unit)

    suspend fun deleteVendor(id: Int, storeId: Int, onSuccess: (response: APIResponse) -> Unit, onFailure: (t: Throwable) -> Unit)

    suspend fun fetchVendorDetails(id: Int, storeId: Int, onSuccess: (response: BaseResponse<Vendor>) -> Unit, onFailure: (t: Throwable) -> Unit)

    suspend fun insertVendor(req: VendorInsertRequest, onSuccess: (response: APIResponse) -> Unit, onFailure: (t: Throwable) -> Unit)

    suspend fun insertAddSalePerson(req: AddSalePerson, onSuccess: (response: APIResponse) -> Unit, onFailure: (t: Throwable) -> Unit)

    suspend fun updateVendor(req: VendorUpdateRequest, onSuccess: (response: APIResponse) -> Unit, onFailure: (t: Throwable) -> Unit)

    suspend fun fetchUOM(storeId: Int, onSuccess: (response: BaseResponseList<UOM>) -> Unit, onFailure: (t: Throwable) -> Unit)

    suspend fun deleteUOM(id: Int, storeId: Int, onSuccess: (response: APIResponse) -> Unit, onFailure: (t: Throwable) -> Unit)

    suspend fun insertUOM(req: UOMInsertRequest, onSuccess: (response: APIResponse) -> Unit, onFailure: (t: Throwable) -> Unit)

    suspend fun fetchUOMDetails(id: Int, onSuccess: (response: BaseResponse<UOM>) -> Unit, onFailure: (t: Throwable) -> Unit)

    suspend fun updateUOM(req : UOMUpdateRequest, onSuccess : (response : APIResponse) -> Unit, onFailure : (t : Throwable) -> Unit)

    suspend fun getSizeDropDown(storeId : Int, onSuccess : (response : BaseResponseList<CommonDropDown>) -> Unit, onFailure : (t : Throwable) -> Unit)

    suspend fun getPackDropDown(storeId : Int, onSuccess : (response : BaseResponseList<CommonDropDown>) -> Unit, onFailure : (t : Throwable) -> Unit)

    suspend fun getBrandDropDown(storeId : Int, onSuccess : (response : BaseResponseList<CommonDropDown>) -> Unit, onFailure : (t : Throwable) -> Unit)

    suspend fun getTenderDropDown(storeId : Int, onSuccess : (response : BaseResponseList<CommonDropDown>) -> Unit, onFailure : (t : Throwable) -> Unit)

    suspend fun getDepartmentItems(depID : Int, onSuccess : (response : BaseResponseList<ItemCount>) -> Unit, onFailure : (t : Throwable) -> Unit)

    suspend fun getBrandItems(brandID : Int, onSuccess : (response : BaseResponseList<ItemCount>) -> Unit, onFailure : (t : Throwable) -> Unit)

    suspend fun getCategoryItems(catID : Int, onSuccess : (response : BaseResponseList<ItemCount>) -> Unit, onFailure : (t : Throwable) -> Unit)

    suspend fun getGroupTypeDropdown(type : Int, onSuccess : (response : BaseResponseList<CommonDropDown>) -> Unit, onFailure : (t : Throwable) -> Unit)

    suspend fun getGroupTypeList(type: Int, storeId: Int, onSuccess: (response: BaseResponseList<SelectedGroups>) -> Unit, onFailure: (t: Throwable) -> Unit)

    suspend fun getGroupTypeDetails(groupId: Int, storeId: Int, onSuccess: (response: BaseResponse<GroupDetail>) -> Unit, onFailure: (t: Throwable) -> Unit)

    suspend fun updateGroup(req : GroupInsertUpdateRequest, onSuccess : (response : APIResponse) -> Unit, onFailure : (t : Throwable) -> Unit)

    suspend fun fetchGroups(storeId: Int, typeId: Int, onSuccess: (response: BaseResponseList<ItemGroupDetail>) -> Unit, onFailure: (t: Throwable) -> Unit)

    suspend fun fetchPromotionDropDown(storeId : Int, typeId : Int, onSuccess : (response : BaseResponseList<CommonDropDown>) -> Unit, onFailure : (t : Throwable) -> Unit)
}