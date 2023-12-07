package com.varitas.gokulpos.tablet.viewmodel

import android.text.TextUtils
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.varitas.gokulpos.tablet.model.Brand
import com.varitas.gokulpos.tablet.model.BrandDetails
import com.varitas.gokulpos.tablet.model.Category
import com.varitas.gokulpos.tablet.model.CategoryInsertUpdate
import com.varitas.gokulpos.tablet.model.GroupDetail
import com.varitas.gokulpos.tablet.model.GroupInsertUpdateRequest
import com.varitas.gokulpos.tablet.model.ItemGroupDetail
import com.varitas.gokulpos.tablet.model.SelectedGroups
import com.varitas.gokulpos.tablet.repositories.ProductFeatureRepository
import com.varitas.gokulpos.tablet.request.AddSalePerson
import com.varitas.gokulpos.tablet.request.DepartmentInsertUpdate
import com.varitas.gokulpos.tablet.request.FacilityInsertRequest
import com.varitas.gokulpos.tablet.request.FacilityUpdateRequest
import com.varitas.gokulpos.tablet.request.ItemGroupRequest
import com.varitas.gokulpos.tablet.request.SpecificationInsertRequest
import com.varitas.gokulpos.tablet.request.SpecificationUpdateRequest
import com.varitas.gokulpos.tablet.request.TaxInsertRequest
import com.varitas.gokulpos.tablet.request.TaxUpdateRequest
import com.varitas.gokulpos.tablet.request.UOMInsertRequest
import com.varitas.gokulpos.tablet.request.UOMUpdateRequest
import com.varitas.gokulpos.tablet.request.VendorInsertRequest
import com.varitas.gokulpos.tablet.request.VendorUpdateRequest
import com.varitas.gokulpos.tablet.response.CommonDropDown
import com.varitas.gokulpos.tablet.response.Department
import com.varitas.gokulpos.tablet.response.Facility
import com.varitas.gokulpos.tablet.response.ItemCount
import com.varitas.gokulpos.tablet.response.ItemGroupTax
import com.varitas.gokulpos.tablet.response.Specification
import com.varitas.gokulpos.tablet.response.Tax
import com.varitas.gokulpos.tablet.response.UOM
import com.varitas.gokulpos.tablet.response.Vendor
import com.varitas.gokulpos.tablet.utilities.Default
import com.varitas.gokulpos.tablet.utilities.Utils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel class ProductFeatureViewModel @Inject constructor(private val productFeatureRepository : ProductFeatureRepository) : ViewModel() {

    val showProgress : MutableLiveData<Boolean> = MutableLiveData()
    val errorMsg : MutableLiveData<String> = MutableLiveData()
    private val storeDetails = Utils.fetchLoginResponse()
    private val storeId = storeDetails.singleResult!!.storeId
    private val actionBy = storeDetails.singleResult!!.userId

    //region CATEGORIES

    //region To fetch categories
    fun fetchCategories() : MutableLiveData<List<Category>> {
        val categoriesList = MutableLiveData<List<Category>>()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                showProgress.postValue(true)
                productFeatureRepository.fetchCategories(storeId!!, { response->
                    showProgress.postValue(false)
                    if(response.status == Default.SUCCESS_API) categoriesList.postValue(response.data)
                    else categoriesList.postValue(ArrayList())
                }, { t->
                    Log.e("Categories", "onFailure: ", t)
                    showProgress.postValue(false)
                })
            } catch(ex : Exception) {
                Utils.printAndWriteException(ex)
                showProgress.postValue(false)
            }
        }
        return categoriesList
    } //endregion


    //region To insert categories
    fun insertCategories(req : CategoryInsertUpdate) : MutableLiveData<Boolean> {
        val isSuccess = MutableLiveData<Boolean>()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                showProgress.postValue(true)
                req.actionBy = actionBy!!
                req.storeId = storeId!!
                productFeatureRepository.insertCategories(req, { response->
                    showProgress.postValue(false)
                    isSuccess.postValue(response.status == Default.SUCCESS_API)
                    if(!TextUtils.isEmpty(response.message)) errorMsg.postValue(if(response.message != null) response.message else "")
                }, { t->
                    Log.e("Insert Category", "onFailure: ", t)
                    showProgress.postValue(false)
                })
            } catch(ex : Exception) {
                Utils.printAndWriteException(ex)
                showProgress.postValue(false)
            }
        }
        return isSuccess
    } //    endregion

    //region To delete category
    fun deleteCategory(id : Int) : MutableLiveData<Boolean> {
        val isSuccess = MutableLiveData<Boolean>()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                showProgress.postValue(true)
                productFeatureRepository.deleteCategory(id, storeId!!, { response->
                    showProgress.postValue(false)
                    isSuccess.postValue(response.status == Default.SUCCESS_API)
                    errorMsg.postValue(if(response.message != null) response.message else "")
                }, { t->
                    Log.e("Delete Category", "onFailure: ", t)
                    showProgress.postValue(false)
                })
            } catch(ex : Exception) {
                Utils.printAndWriteException(ex)
                showProgress.postValue(false)
            }
        }
        return isSuccess
    } //endregion

    //region To get category details by Id
    fun getCategoryDetails(categoryId : Int) : MutableLiveData<CategoryInsertUpdate> {
        val categoryDetail = MutableLiveData<CategoryInsertUpdate>()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                showProgress.postValue(true)
                productFeatureRepository.fetchCategoryDetails(categoryId, { response->
                    showProgress.postValue(false)
                    if(response.status == Default.SUCCESS_API) categoryDetail.postValue(response.data)
                }, { t->
                    Log.e("Get Category", "onFailure: ", t)
                    showProgress.postValue(false)
                })
            } catch(ex : Exception) {
                Utils.printAndWriteException(ex)
                showProgress.postValue(false)
            }
        }
        return categoryDetail
    } //endregion

    //region To get parentCategory
    fun getParentCategory() : MutableLiveData<List<CommonDropDown>> {
        val parentCategoryList = MutableLiveData<List<CommonDropDown>>()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                showProgress.postValue(true)
                productFeatureRepository.getParentCategory(storeId!!, { response->
                    showProgress.postValue(false)
                    if(response.status == Default.SUCCESS_API) parentCategoryList.postValue(response.data)
                    else parentCategoryList.postValue(ArrayList())
                }, { t->
                    Log.e("Parent Category", "onFailure: ", t)
                    showProgress.postValue(false)
                })
            } catch(ex : Exception) {
                Utils.printAndWriteException(ex)
                showProgress.postValue(false)
            }
        }
        return parentCategoryList
    } //endregion

    //region To get parentCategory
    fun getSubCategory(parentId : Int) : MutableLiveData<List<CommonDropDown>> {
        val subCategoryList = MutableLiveData<List<CommonDropDown>>()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                showProgress.postValue(true)
                productFeatureRepository.getSubCategory(parentId, { response->
                    showProgress.postValue(false)
                    if(response.status == Default.SUCCESS_API) subCategoryList.postValue(response.data)
                    else subCategoryList.postValue(ArrayList())
                }, { t->
                    Log.e("Sub-Category", "onFailure: ", t)
                    showProgress.postValue(false)
                })
            } catch(ex : Exception) {
                Utils.printAndWriteException(ex)
                showProgress.postValue(false)
            }
        }
        return subCategoryList
    } //endregion

    //region To update category
    fun updateCategory(req : CategoryInsertUpdate) : MutableLiveData<Boolean> {
        val isSuccess = MutableLiveData<Boolean>()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                showProgress.postValue(true)
                req.actionBy = actionBy
                productFeatureRepository.updateCategory(req, { response->
                    showProgress.postValue(false)
                    isSuccess.postValue(response.status == Default.SUCCESS_API)
                    errorMsg.postValue(if(response.message != null) response.message else "")
                }, { t->
                    Log.e("Update Category", "onFailure: ", t)
                    showProgress.postValue(false)
                })
            } catch(ex : Exception) {
                Utils.printAndWriteException(ex)
                showProgress.postValue(false)
            }
        }
        return isSuccess
    } //endregion

    //region To get brand Dropdown
    fun getBrandDropDown() : MutableLiveData<List<CommonDropDown>> {
        val brand = MutableLiveData<List<CommonDropDown>>()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                showProgress.postValue(true)
                productFeatureRepository.getBrandDropDown(storeId!!, { response->
                    showProgress.postValue(false)
                    if(response.status == Default.SUCCESS_API) brand.postValue(response.data)
                    else brand.postValue(ArrayList())
                }, { t->
                    Log.e("BRAND DROPDOWN", "onFailure: ", t)
                    showProgress.postValue(false)
                })
            } catch(ex : Exception) {
                Utils.printAndWriteException(ex)
                showProgress.postValue(false)
            }
        }
        return brand
    } //endregion

    //region To get category item count
    fun getCategoryItems(catID : Int) : MutableLiveData<List<ItemCount>> {
        val itemCount = MutableLiveData<List<ItemCount>>()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                showProgress.postValue(true)
                productFeatureRepository.getCategoryItems(catID = catID, { response->
                    showProgress.postValue(false)
                    if(response.status == Default.SUCCESS_API) itemCount.postValue(response.data)
                    else {
                        itemCount.postValue(ArrayList())
//                        errorMsg.postValue(if(response.message != null) response.message else "")
                    }
                }, { t->
                    Log.e("CATEGORY ITEM COUNT", "onFailure: ", t)
                    showProgress.postValue(false)
                })
            } catch(ex : Exception) {
                Utils.printAndWriteException(ex)
                showProgress.postValue(false)
            }
        }
        return itemCount
    } //endregion

    //endregion

    //region DEPARTMENT

    //region To fetch departments
    fun fetchDepartment() : MutableLiveData<List<Department>> {
        val departmentList = MutableLiveData<List<Department>>()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                showProgress.postValue(true)
                productFeatureRepository.fetchDepartments(storeId!!, { response->
                    showProgress.postValue(false)
                    if(response.status == Default.SUCCESS_API) departmentList.postValue(response.data)
                    else departmentList.postValue(ArrayList())
                }, { t->
                    Log.e("Departments", "onFailure: ", t)
                    showProgress.postValue(false)
                })
            } catch(ex : Exception) {
                Utils.printAndWriteException(ex)
                showProgress.postValue(false)
            }
        }
        return departmentList
    } //endregion

    //region To insert department
    fun insertDepartment(req : DepartmentInsertUpdate) : MutableLiveData<Boolean> {
        val isSuccess = MutableLiveData<Boolean>()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                showProgress.postValue(true)
                req.actionBy = actionBy
                req.storeId = storeId
                productFeatureRepository.insertDepartment(req, { response->
                    showProgress.postValue(false)
                    isSuccess.postValue(response.status == Default.SUCCESS_API)
                    errorMsg.postValue(if(response.message != null) response.message else "")
                }, { t->
                    Log.e("Insert Department", "onFailure: ", t)
                    showProgress.postValue(false)
                })
            } catch(ex : Exception) {
                Utils.printAndWriteException(ex)
                showProgress.postValue(false)
            }
        }
        return isSuccess
    } //endregion

    //region To get Department
    fun getDepartment() : MutableLiveData<List<CommonDropDown>> {
        val departmentList = MutableLiveData<List<CommonDropDown>>()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                showProgress.postValue(true)
                productFeatureRepository.getDepartment(storeId!!, { response->
                    showProgress.postValue(false)
                    if(response.status == Default.SUCCESS_API) departmentList.postValue(response.data)
                    else departmentList.postValue(ArrayList())
                }, { t->
                    Log.e("Department", "onFailure: ", t)
                    showProgress.postValue(false)
                })
            } catch(ex : Exception) {
                Utils.printAndWriteException(ex)
                showProgress.postValue(false)
            }
        }
        return departmentList
    } //endregion

    //region To delete department
    fun deleteDepartment(id : Int) : MutableLiveData<Boolean> {
        val isSuccess = MutableLiveData<Boolean>()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                showProgress.postValue(true)
                productFeatureRepository.deleteDepartment(id, storeId!!, { response->
                    showProgress.postValue(false)
                    isSuccess.postValue(response.status == Default.SUCCESS_API)
                    errorMsg.postValue(if(response.message != null) response.message else "")
                }, { t->
                    Log.e("Delete Department", "onFailure: ", t)
                    showProgress.postValue(false)
                })
            } catch(ex : Exception) {
                Utils.printAndWriteException(ex)
                showProgress.postValue(false)
            }
        }
        return isSuccess
    } //endregion

    //region To get department details
    fun getDepartmentDetails(id : Int) : MutableLiveData<DepartmentInsertUpdate?> {
        val details = MutableLiveData<DepartmentInsertUpdate?>()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                showProgress.postValue(true)
                productFeatureRepository.fetchDepartmentDetails(id, storeId!!, { response->
                    showProgress.postValue(false)
                    if(response.status == Default.SUCCESS_API) details.postValue(response.data)
                }, { t->
                    Log.e("Department Details", "onFailure: ", t)
                    showProgress.postValue(false)
                })
            } catch(ex : Exception) {
                Utils.printAndWriteException(ex)
                showProgress.postValue(false)
            }
        }
        return details
    } //endregion

    //region To insert department
    fun updateDepartment(req : DepartmentInsertUpdate) : MutableLiveData<Boolean> {
        val isSuccess = MutableLiveData<Boolean>()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                showProgress.postValue(true)
                req.actionBy = actionBy
                productFeatureRepository.updateDepartment(req, { response->
                    showProgress.postValue(false)
                    isSuccess.postValue(response.status == Default.SUCCESS_API)
                    errorMsg.postValue(if(response.message != null) response.message else "")
                }, { t->
                    Log.e("Update Department", "onFailure: ", t)
                    showProgress.postValue(false)
                })
            } catch(ex : Exception) {
                Utils.printAndWriteException(ex)
                showProgress.postValue(false)
            }
        }
        return isSuccess
    } //endregion

    //region To get department item count
    fun getDepartmentItems(depID : Int) : MutableLiveData<List<ItemCount>> {
        val itemCount = MutableLiveData<List<ItemCount>>()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                showProgress.postValue(true)
                productFeatureRepository.getDepartmentItems(depID = depID, { response->
                    showProgress.postValue(false)
                    if(response.status == Default.SUCCESS_API) itemCount.postValue(response.data)
                    else {
                        itemCount.postValue(ArrayList())
//                        errorMsg.postValue(if(response.message != null) response.message else "")
                    }
                }, { t->
                    Log.e("DEPARTMENT ITEM COUNT", "onFailure: ", t)
                    showProgress.postValue(false)
                })
            } catch(ex : Exception) {
                Utils.printAndWriteException(ex)
                showProgress.postValue(false)
            }
        }
        return itemCount
    } //endregion

    //endregion

    //region BRANDS

    //region To fetch brands
    fun fetchBrands() : MutableLiveData<List<Brand>> {
        val brandList = MutableLiveData<List<Brand>>()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                showProgress.postValue(true)
                productFeatureRepository.fetchBrands(storeId!!, { response->
                    showProgress.postValue(false)
                    if(response.status == Default.SUCCESS_API) brandList.postValue(response.data)
                    else brandList.postValue(ArrayList())
                }, { t->
                    Log.e("Categories", "onFailure: ", t)
                    showProgress.postValue(false)
                })
            } catch(ex : Exception) {
                Utils.printAndWriteException(ex)
                showProgress.postValue(false)
            }
        }
        return brandList
    } //endregion

    //region To delete brand
    fun deleteBrand(id : Int) : MutableLiveData<Boolean> {
        val isSuccess = MutableLiveData<Boolean>()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                showProgress.postValue(true)
                productFeatureRepository.deleteBrand(id, storeId!!, { response->
                    showProgress.postValue(false)
                    isSuccess.postValue(response.status == Default.SUCCESS_API)
                    errorMsg.postValue(if(response.message != null) response.message else "")
                }, { t->
                    Log.e("Delete Brand", "onFailure: ", t)
                    showProgress.postValue(false)
                })
            } catch(ex : Exception) {
                Utils.printAndWriteException(ex)
                showProgress.postValue(false)
            }
        }
        return isSuccess
    } //endregion

    //region To insert brand
    fun insertBrand(req : BrandDetails) : MutableLiveData<Boolean> {
        val isSuccess = MutableLiveData<Boolean>()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                showProgress.postValue(true)
                req.actionBy = actionBy
                req.storeId = storeId!!
                productFeatureRepository.insertBrand(req, { response->
                    showProgress.postValue(false)
                    isSuccess.postValue(response.status == Default.SUCCESS_API)
                    errorMsg.postValue(if(response.message != null) response.message else "")
                }, { t->
                    Log.e("Insert Brand", "onFailure: ", t)
                    showProgress.postValue(false)
                })
            } catch(ex : Exception) {
                Utils.printAndWriteException(ex)
                showProgress.postValue(false)
            }
        }
        return isSuccess
    } //endregion

    //region To get brand details
    fun getBrandDetails(id : Int) : MutableLiveData<Brand?> {
        val details = MutableLiveData<Brand?>()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                showProgress.postValue(true)
                productFeatureRepository.fetchBrandDetails(id, storeId!!, { response->
                    showProgress.postValue(false)
                    if(response.status == Default.SUCCESS_API) details.postValue(response.data)
                }, { t->
                    Log.e("Brand", "onFailure: ", t)
                    showProgress.postValue(false)
                })
            } catch(ex : Exception) {
                Utils.printAndWriteException(ex)
                showProgress.postValue(false)
            }
        }
        return details
    } //endregion

    //region To insert brand
    fun updateBrand(req : BrandDetails) : MutableLiveData<Boolean> {
        val isSuccess = MutableLiveData<Boolean>()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                showProgress.postValue(true)
                req.actionBy = actionBy
                productFeatureRepository.updateBrand(req, { response->
                    showProgress.postValue(false)
                    isSuccess.postValue(response.status == Default.SUCCESS_API)
                    errorMsg.postValue(if(response.message != null) response.message else "")
                }, { t->
                    Log.e("Insert Brand", "onFailure: ", t)
                    showProgress.postValue(false)
                })
            } catch(ex : Exception) {
                Utils.printAndWriteException(ex)
                showProgress.postValue(false)
            }
        }
        return isSuccess
    } //endregion

    //region To get brand item count
    fun getBrandItems(brandId : Int) : MutableLiveData<List<ItemCount>> {
        val itemCount = MutableLiveData<List<ItemCount>>()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                showProgress.postValue(true)
                productFeatureRepository.getBrandItems(brandID = brandId, { response->
                    showProgress.postValue(false)
                    if(response.status == Default.SUCCESS_API) itemCount.postValue(response.data)
                    else itemCount.postValue(ArrayList())
                }, { t->
                    Log.e("BRAND ITEM COUNT", "onFailure: ", t)
                    showProgress.postValue(false)
                })
            } catch(ex : Exception) {
                Utils.printAndWriteException(ex)
                showProgress.postValue(false)
            }
        }
        return itemCount
    } //endregion

    //endregion

    //region Tax/es

    //region To fetch Tax/es
    fun fetchTaxes() : MutableLiveData<List<Tax>> {
        val list = MutableLiveData<List<Tax>>()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                showProgress.postValue(true)
                productFeatureRepository.fetchTaxes(storeId!!, { response->
                    showProgress.postValue(false)
                    if(response.status == Default.SUCCESS_API) list.postValue(response.data)
                    else list.postValue(ArrayList())
                }, { t->
                    Log.e("Tax", "onFailure: ", t)
                    showProgress.postValue(false)
                })
            } catch(ex : Exception) {
                Utils.printAndWriteException(ex)
                showProgress.postValue(false)
            }
        }
        return list
    } //endregion

    //region To fetch Tax/es
    fun fetchTaxesFromGroup(groupId : Int) : MutableLiveData<List<Tax>> {
        val list = MutableLiveData<List<Tax>>()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                showProgress.postValue(true)
                productFeatureRepository.fetchTaxesFromGroup(groupId, { response->
                    showProgress.postValue(false)
                    if(response.status == Default.SUCCESS_API) list.postValue(response.data)
                    else list.postValue(ArrayList())
                }, { t->
                    Log.e("Tax", "onFailure: ", t)
                    showProgress.postValue(false)
                })
            } catch(ex : Exception) {
                Utils.printAndWriteException(ex)
                showProgress.postValue(false)
            }
        }
        return list
    } //endregion

    //region To delete Tax
    fun deleteTax(id : Int) : MutableLiveData<Boolean> {
        val isSuccess = MutableLiveData<Boolean>()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                showProgress.postValue(true)
                productFeatureRepository.deleteTax(id, storeId!!, { response->
                    showProgress.postValue(false)
                    isSuccess.postValue(response.status == Default.SUCCESS_API)
                    errorMsg.postValue(if(response.message != null) response.message else "")
                }, { t->
                    Log.e("Delete Tax", "onFailure: ", t)
                    showProgress.postValue(false)
                })
            } catch(ex : Exception) {
                Utils.printAndWriteException(ex)
                showProgress.postValue(false)
            }
        }
        return isSuccess
    } //endregion

    //region To get tax details
    fun getTaxDetails(id : Int) : MutableLiveData<Tax?> {
        val details = MutableLiveData<Tax?>()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                showProgress.postValue(true)
                productFeatureRepository.fetchTaxDetails(id, storeId!!, { response->
                    showProgress.postValue(false)
                    if(response.status == Default.SUCCESS_API) details.postValue(response.data)
                }, { t->
                    Log.e("Tax", "onFailure: ", t)
                    showProgress.postValue(false)
                })
            } catch(ex : Exception) {
                Utils.printAndWriteException(ex)
                showProgress.postValue(false)
            }
        }
        return details
    } //endregion

    //region To insert tax
    fun insertTax(req : TaxInsertRequest) : MutableLiveData<Boolean> {
        val isSuccess = MutableLiveData<Boolean>()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                showProgress.postValue(true)
                req.actionBy = actionBy
                req.storeId = storeId!!
                productFeatureRepository.insertTax(req, { response->
                    showProgress.postValue(false)
                    isSuccess.postValue(response.status == Default.SUCCESS_API)
                    errorMsg.postValue(if(response.message != null) response.message else "")
                }, { t->
                    Log.e("Insert Tax", "onFailure: ", t)
                    showProgress.postValue(false)
                })
            } catch(ex : Exception) {
                Utils.printAndWriteException(ex)
                showProgress.postValue(false)
            }
        }
        return isSuccess
    } //endregion

    //region To update tax
    fun updateTax(req : TaxUpdateRequest) : MutableLiveData<Boolean> {
        val isSuccess = MutableLiveData<Boolean>()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                showProgress.postValue(true)
                req.actionBy = actionBy
                productFeatureRepository.updateTax(req, { response->
                    showProgress.postValue(false)
                    isSuccess.postValue(response.status == Default.SUCCESS_API)
                    errorMsg.postValue(if(response.message != null) response.message else "")
                }, { t->
                    Log.e("Update Tax", "onFailure: ", t)
                    showProgress.postValue(false)
                })
            } catch(ex : Exception) {
                Utils.printAndWriteException(ex)
                showProgress.postValue(false)
            }
        }
        return isSuccess
    } //endregion

    //endregion

    //region TAX GROUPS

    //region To fetch Tax/es
    fun fetchGroupsById(typeId : Int) : MutableLiveData<List<CommonDropDown>> {
        val list = MutableLiveData<List<CommonDropDown>>()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                showProgress.postValue(true)
                productFeatureRepository.fetchTaxGroup(storeId!!, typeId, { response->
                    showProgress.postValue(false)
                    if(response.status == Default.SUCCESS_API) list.postValue(response.data)
                    else list.postValue(ArrayList())
                }, { t->
                    Log.e("Tax Groups", "onFailure: ", t)
                    showProgress.postValue(false)
                })
            } catch(ex : Exception) {
                Utils.printAndWriteException(ex)
                showProgress.postValue(false)
            }
        }
        return list
    } //endregion

    //region To fetch Group
    fun fetchGroups(): MutableLiveData<List<SelectedGroups>> {
        val list = MutableLiveData<List<SelectedGroups>>()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                showProgress.postValue(true)
                productFeatureRepository.fetchGroup(storeId!!, { response ->
                    showProgress.postValue(false)
                    if (response.status == Default.SUCCESS_API) list.postValue(response.data)
                    else list.postValue(ArrayList())
                }, { t ->
                    Log.e("Groups", "onFailure: ", t)
                    showProgress.postValue(false)
                })
            } catch(ex : Exception) {
                Utils.printAndWriteException(ex)
                showProgress.postValue(false)
            }
        }
        return list
    } //endregion

    //region To delete group
    fun deleteGroup(id : Int) : MutableLiveData<Boolean> {
        val isSuccess = MutableLiveData<Boolean>()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                showProgress.postValue(true)
                productFeatureRepository.deleteGroup(id, storeId!!, { response->
                    showProgress.postValue(false)
                    isSuccess.postValue(response.status == Default.SUCCESS_API)
                    errorMsg.postValue(if(response.message != null) response.message else "")
                }, { t->
                    Log.e("Delete Group", "onFailure: ", t)
                    showProgress.postValue(false)
                })
            } catch(ex : Exception) {
                Utils.printAndWriteException(ex)
                showProgress.postValue(false)
            }
        }
        return isSuccess
    } //endregion

    //region To get group details by Id
    fun getGroupTypeDetails(groupId: Int): MutableLiveData<GroupDetail> {
        val groupDetail = MutableLiveData<GroupDetail>()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                showProgress.postValue(true)
                productFeatureRepository.getGroupTypeDetails(groupId, storeId!!, { response ->
                    showProgress.postValue(false)
                    if (response.status == Default.SUCCESS_API) groupDetail.postValue(response.data)
                    else errorMsg.postValue(if(response.message != null) response.message else "")
                    groupDetail.postValue(response.data)
                }, { t ->
                    Log.e("Get Group", "onFailure: ", t)
                    showProgress.postValue(false)
                })
            } catch(ex : Exception) {
                Utils.printAndWriteException(ex)
                showProgress.postValue(false)
            }
        }
        return groupDetail
    } //endregion

    //region To insert tax
    fun insertGroup(req : GroupInsertUpdateRequest) : MutableLiveData<Boolean> {
        val isSuccess = MutableLiveData<Boolean>()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                showProgress.postValue(true)
                req.storeId = storeId!!
                req.actionBy = actionBy!!
                productFeatureRepository.insertGroup(req, { response ->
                    showProgress.postValue(false)
                    isSuccess.postValue(response.status == Default.SUCCESS_API)
                    errorMsg.postValue(if (response.message != null) response.message else "")
                }, { t ->
                    Log.e("Insert Group", "onFailure: ", t)
                    showProgress.postValue(false)
                })
            } catch(ex : Exception) {
                Utils.printAndWriteException(ex)
                showProgress.postValue(false)
            }
        }
        return isSuccess
    } //endregion

    //region Group Type Dropdown
    fun getGroupTypeDropdown(type : Int) : MutableLiveData<List<CommonDropDown>> {
        val list = MutableLiveData<List<CommonDropDown>>()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                showProgress.postValue(true)
                productFeatureRepository.getGroupTypeDropdown(type = type, { response->
                    showProgress.postValue(false)
                    if(response.status == Default.SUCCESS_API) list.postValue(response.data)
                    else list.postValue(ArrayList())
                }, { t->
                    Log.e("Group Type", "onFailure: ", t)
                    showProgress.postValue(false)
                })
            } catch(ex : Exception) {
                Utils.printAndWriteException(ex)
                showProgress.postValue(false)
            }
        }
        return list
    } //endregion

    //region To fetch Group Type
    fun getGroupTypeList(type: Int): MutableLiveData<List<SelectedGroups>> {
        val list = MutableLiveData<List<SelectedGroups>>()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                productFeatureRepository.getGroupTypeList(type = type, storeId!!, { response ->
                    if (response.status == Default.SUCCESS_API) list.postValue(response.data)
                    else list.postValue(ArrayList())
                }, { t ->
                    Log.e("Group Type", "onFailure: ", t)
                })
            } catch (ex: Exception) {
                Utils.printAndWriteException(ex)
            }
        }
        return list
    } //endregion

    //region To update Group
    fun updateGroup(req : GroupInsertUpdateRequest) : MutableLiveData<Boolean> {
        val isSuccess = MutableLiveData<Boolean>()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                showProgress.postValue(true)
                req.actionBy = actionBy
                req.storeId = storeId!!
                productFeatureRepository.updateGroup(req, { response ->
                    showProgress.postValue(false)
                    isSuccess.postValue(response.status == Default.SUCCESS_API)
                    errorMsg.postValue(if (response.message != null) response.message else "")
                }, { t ->
                    Log.e("Update Group", "onFailure: ", t)
                    showProgress.postValue(false)
                })
            } catch(ex : Exception) {
                Utils.printAndWriteException(ex)
                showProgress.postValue(false)
            }
        }
        return isSuccess
    } //endregion

    //region To insert tax
    fun insertItemToGroup(req : ItemGroupRequest) : MutableLiveData<Boolean> {
        val isSuccess = MutableLiveData<Boolean>()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                showProgress.postValue(true)
                productFeatureRepository.insertItemToGroup(req, { response->
                    showProgress.postValue(false)
                    isSuccess.postValue(response.status == Default.SUCCESS_API)
                    errorMsg.postValue(if(response.message != null) response.message else "")
                }, { t->
                    Log.e("Insert Group to Item", "onFailure: ", t)
                    showProgress.postValue(false)
                })
            } catch(ex : Exception) {
                Utils.printAndWriteException(ex)
                showProgress.postValue(false)
            }
        }
        return isSuccess
    } //endregion

    //region To get group details by Id
    fun getGroupDetails(groupId : Int, itemId : Int, specification : Int, price : Double) : MutableLiveData<ItemGroupTax> {
        val data = MutableLiveData<ItemGroupTax>()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                showProgress.postValue(true)
                productFeatureRepository.fetchGroupDetails(groupId, itemId, specification, price, { response->
                    showProgress.postValue(false)
                    if(response.status == Default.SUCCESS_API) data.postValue(response.data)
                }, { t->
                    Log.e("Get Group", "onFailure: ", t)
                    showProgress.postValue(false)
                })
            } catch(ex : Exception) {
                Utils.printAndWriteException(ex)
                showProgress.postValue(false)
            }
        }
        return data
    } //endregion

    //endregion

    //region SPECIFICATION

    //region To get Specification
    fun fetchSpecification() : MutableLiveData<List<Specification>> {
        val specification = MutableLiveData<List<Specification>>()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                showProgress.postValue(true)
                productFeatureRepository.fetchSpecification(storeId!!, { response->
                    showProgress.postValue(false)
                    if(response.status == Default.SUCCESS_API) specification.postValue(response.data)
                    else specification.postValue(ArrayList())
                }, { t->
                    Log.e("Specification", "onFailure: ", t)
                    showProgress.postValue(false)
                })
            } catch(ex : Exception) {
                Utils.printAndWriteException(ex)
                showProgress.postValue(false)
            }
        }
        return specification
    } //endregion

    //region To delete specification
    fun deleteSpecification(id : Int) : MutableLiveData<Boolean> {
        val isSuccess = MutableLiveData<Boolean>()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                showProgress.postValue(true)
                productFeatureRepository.deleteSpecification(id, storeId!!, { response->
                    showProgress.postValue(false)
                    isSuccess.postValue(response.status == Default.SUCCESS_API)
                    errorMsg.postValue(if(response.message != null) response.message else "")
                }, { t->
                    Log.e("Delete Specification", "onFailure: ", t)
                    showProgress.postValue(false)
                })
            } catch(ex : Exception) {
                Utils.printAndWriteException(ex)
                showProgress.postValue(false)
            }
        }
        return isSuccess
    } //endregion

    //region To get UOM
    fun getUOM() : MutableLiveData<List<CommonDropDown>> {
        val uomList = MutableLiveData<List<CommonDropDown>>()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                showProgress.postValue(true)
                productFeatureRepository.getUOM(storeId!!, { response->
                    showProgress.postValue(false)
                    if(response.status == Default.SUCCESS_API) uomList.postValue(response.data)
                    else uomList.postValue(ArrayList())
                }, { t->
                    Log.e("UOM", "onFailure: ", t)
                    showProgress.postValue(false)
                })
            } catch(ex : Exception) {
                Utils.printAndWriteException(ex)
                showProgress.postValue(false)
            }
        }
        return uomList
    } //endregion

    //region To get Specification Type
    fun getSpecificationType(typeId : Int) : MutableLiveData<List<CommonDropDown>> {
        val specificationType = MutableLiveData<List<CommonDropDown>>()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                showProgress.postValue(true)
                productFeatureRepository.getSpecificationType(storeId!!,typeId, { response->
                    showProgress.postValue(false)
                    if(response.status == Default.SUCCESS_API) specificationType.postValue(response.data)
                    else specificationType.postValue(ArrayList())
                }, { t->
                    Log.e("SPECIFICATION TYPE", "onFailure: ", t)
                    showProgress.postValue(false)
                })
            } catch(ex : Exception) {
                Utils.printAndWriteException(ex)
                showProgress.postValue(false)
            }
        }
        return specificationType
    } //endregion

    //region To get Item Specification
    fun getItemSpecification(itemId : Int) : MutableLiveData<List<CommonDropDown>> {
        val specificationType = MutableLiveData<List<CommonDropDown>>()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                showProgress.postValue(true)
                productFeatureRepository.getItemSpecification(itemId, { response->
                    showProgress.postValue(false)
                    if(response.status == Default.SUCCESS_API) specificationType.postValue(response.data)
                    else specificationType.postValue(ArrayList())
                }, { t->
                    Log.e("ITEM SPECIFICATION", "onFailure: ", t)
                    showProgress.postValue(false)
                })
            } catch(ex : Exception) {
                Utils.printAndWriteException(ex)
                showProgress.postValue(false)
            }
        }
        return specificationType
    } //endregion

    //region To insert specification
    fun insertSpecification(req : SpecificationInsertRequest) : MutableLiveData<Boolean> {
        val isSuccess = MutableLiveData<Boolean>()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                showProgress.postValue(true)
                req.actionBy = actionBy
                req.storeId = storeId!!
                productFeatureRepository.insertSpecification(req, { response->
                    showProgress.postValue(false)
                    isSuccess.postValue(response.status == Default.SUCCESS_API)
                    errorMsg.postValue(if(response.message != null) response.message else "")
                }, { t->
                    Log.e("Insert Specification", "onFailure: ", t)
                    showProgress.postValue(false)
                })
            } catch(ex : Exception) {
                Utils.printAndWriteException(ex)
                showProgress.postValue(false)
            }
        }
        return isSuccess
    } //endregion


    //region To get specification details by Id
    fun getSpecificationDetails(specificationId : Int) : MutableLiveData<Specification> {
        val specificationDetails = MutableLiveData<Specification>()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                showProgress.postValue(true)
                productFeatureRepository.fetchSpecificationDetails(specificationId, { response->
                    showProgress.postValue(false)
                    if(response.status == Default.SUCCESS_API) specificationDetails.postValue(response.data)
                }, { t->
                    Log.e("Get Specification", "onFailure: ", t)
                    showProgress.postValue(false)
                })
            } catch(ex : Exception) {
                Utils.printAndWriteException(ex)
                showProgress.postValue(false)
            }
        }
        return specificationDetails
    } //endregion

    //region To update specification
    fun updateSpecification(req : SpecificationUpdateRequest) : MutableLiveData<Boolean> {
        val isSuccess = MutableLiveData<Boolean>()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                showProgress.postValue(true)
                req.actionBy = actionBy
                productFeatureRepository.updateSpecification(req, { response->
                    showProgress.postValue(false)
                    isSuccess.postValue(response.status == Default.SUCCESS_API)
                    errorMsg.postValue(if(response.message != null) response.message else "")
                }, { t->
                    Log.e("Update Specification", "onFailure: ", t)
                    showProgress.postValue(false)
                })
            } catch(ex : Exception) {
                Utils.printAndWriteException(ex)
                showProgress.postValue(false)
            }
        }
        return isSuccess
    } //endregion

    //endregion

    //region Facility

    //region To get Facility
    fun fetchFacility() : MutableLiveData<List<Facility>> {
        val facility = MutableLiveData<List<Facility>>()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                showProgress.postValue(true)
                productFeatureRepository.fetchFacility(storeId!!, { response->
                    showProgress.postValue(false)
                    if(response.status == Default.SUCCESS_API) facility.postValue(response.data)
                    else facility.postValue(ArrayList())
                }, { t->
                    Log.e("Facility", "onFailure: ", t)
                    showProgress.postValue(false)
                })
            } catch(ex : Exception) {
                Utils.printAndWriteException(ex)
                showProgress.postValue(false)
            }
        }
        return facility
    } //endregion

    //region To delete facility
    fun deleteFacility(id : Int) : MutableLiveData<Boolean> {
        val isSuccess = MutableLiveData<Boolean>()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                showProgress.postValue(true)
                productFeatureRepository.deleteFacility(id, storeId!!, { response->
                    showProgress.postValue(false)
                    isSuccess.postValue(response.status == Default.SUCCESS_API)
                    errorMsg.postValue(if(response.message != null) response.message else "")
                }, { t->
                    Log.e("Delete Facility", "onFailure: ", t)
                    showProgress.postValue(false)
                })
            } catch(ex : Exception) {
                Utils.printAndWriteException(ex)
                showProgress.postValue(false)
            }
        }
        return isSuccess
    } //endregion

    //region To insert facility
    fun insertFacility(req : FacilityInsertRequest) : MutableLiveData<Boolean> {
        val isSuccess = MutableLiveData<Boolean>()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                showProgress.postValue(true)
                req.actionBy = actionBy
                req.storeId = storeId!!
                productFeatureRepository.insertFacility(req, { response->
                    showProgress.postValue(false)
                    isSuccess.postValue(response.status == Default.SUCCESS_API)
                    errorMsg.postValue(if(response.message != null) response.message else "")
                }, { t->
                    Log.e("Insert Facility", "onFailure: ", t)
                    showProgress.postValue(false)
                })
            } catch(ex : Exception) {
                Utils.printAndWriteException(ex)
                showProgress.postValue(false)
            }
        }
        return isSuccess
    } //endregion

    //region To get facility details by Id
    fun getFacilityDetails(facilityID : Int) : MutableLiveData<Facility> {
        val facilityDetails = MutableLiveData<Facility>()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                showProgress.postValue(true)
                productFeatureRepository.fetchFacilityDetails(facilityID, { response->
                    showProgress.postValue(false)
                    if(response.status == Default.SUCCESS_API) facilityDetails.postValue(response.data)
                }, { t->
                    Log.e("Get Facility", "onFailure: ", t)
                    showProgress.postValue(false)
                })
            } catch(ex : Exception) {
                Utils.printAndWriteException(ex)
                showProgress.postValue(false)
            }
        }
        return facilityDetails
    } //endregion

    //region To update facility
    fun updateFacility(req : FacilityUpdateRequest) : MutableLiveData<Boolean> {
        val isSuccess = MutableLiveData<Boolean>()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                showProgress.postValue(true)
                req.actionBy = actionBy
                productFeatureRepository.updateFacility(req, { response->
                    showProgress.postValue(false)
                    isSuccess.postValue(response.status == Default.SUCCESS_API)
                    errorMsg.postValue(if(response.message != null) response.message else "")
                }, { t->
                    Log.e("Update Facility", "onFailure: ", t)
                    showProgress.postValue(false)
                })
            } catch(ex : Exception) {
                Utils.printAndWriteException(ex)
                showProgress.postValue(false)
            }
        }
        return isSuccess
    } //endregion

    //endregion

    //region UOM

    //region To get UOM
    fun fetchUOM() : MutableLiveData<List<UOM>> {
        val uom = MutableLiveData<List<UOM>>()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                showProgress.postValue(true)
                productFeatureRepository.fetchUOM(storeId!!, { response->
                    showProgress.postValue(false)
                    if(response.status == Default.SUCCESS_API) uom.postValue(response.data)
                    else uom.postValue(ArrayList())
                }, { t->
                    Log.e("UOM", "onFailure: ", t)
                    showProgress.postValue(false)
                })
            } catch(ex : Exception) {
                Utils.printAndWriteException(ex)
                showProgress.postValue(false)
            }
        }
        return uom
    } //endregion

    //region To delete UOM
    fun deleteUOM(id : Int) : MutableLiveData<Boolean> {
        val isSuccess = MutableLiveData<Boolean>()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                showProgress.postValue(true)
                productFeatureRepository.deleteUOM(id, storeId!!, { response->
                    showProgress.postValue(false)
                    isSuccess.postValue(response.status == Default.SUCCESS_API)
                    errorMsg.postValue(if(response.message != null) response.message else "")
                }, { t->
                    Log.e("Delete UOM", "onFailure: ", t)
                    showProgress.postValue(false)
                })
            } catch(ex : Exception) {
                Utils.printAndWriteException(ex)
                showProgress.postValue(false)
            }
        }
        return isSuccess
    } //endregion

    //region To insert UOM
    fun insertUOM(req : UOMInsertRequest) : MutableLiveData<Boolean> {
        val isSuccess = MutableLiveData<Boolean>()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                showProgress.postValue(true)
                req.actionBy = actionBy
                req.storeId = storeId!!
                productFeatureRepository.insertUOM(req, { response->
                    showProgress.postValue(false)
                    isSuccess.postValue(response.status == Default.SUCCESS_API)
                    errorMsg.postValue(if(response.message != null) response.message else "")
                }, { t->
                    Log.e("Insert UOM", "onFailure: ", t)
                    showProgress.postValue(false)
                })
            } catch(ex : Exception) {
                Utils.printAndWriteException(ex)
                showProgress.postValue(false)
            }
        }
        return isSuccess
    } //endregion

    //region To get UOM details by Id
    fun getUOMDetails(uomID : Int) : MutableLiveData<UOM> {
        val uomDetails = MutableLiveData<UOM>()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                showProgress.postValue(true)
                productFeatureRepository.fetchUOMDetails(uomID, { response->
                    showProgress.postValue(false)
                    if(response.status == Default.SUCCESS_API) uomDetails.postValue(response.data)
                }, { t->
                    Log.e("Get UOM", "onFailure: ", t)
                    showProgress.postValue(false)
                })
            } catch(ex : Exception) {
                Utils.printAndWriteException(ex)
                showProgress.postValue(false)
            }
        }
        return uomDetails
    } //endregion

    //region To update UOM
    fun updateUOM(req : UOMUpdateRequest) : MutableLiveData<Boolean> {
        val isSuccess = MutableLiveData<Boolean>()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                showProgress.postValue(true)
                req.actionBy = actionBy
                productFeatureRepository.updateUOM(req, { response->
                    showProgress.postValue(false)
                    isSuccess.postValue(response.status == Default.SUCCESS_API)
                    errorMsg.postValue(if(response.message != null) response.message else "")
                }, { t->
                    Log.e("Update UOM", "onFailure: ", t)
                    showProgress.postValue(false)
                })
            } catch(ex : Exception) {
                Utils.printAndWriteException(ex)
                showProgress.postValue(false)
            }
        }
        return isSuccess
    } //endregion

    //endregion

    //region VENDORS

    //region To fetch vendors
    fun fetchVendors() : MutableLiveData<List<Vendor>> {
        val vendorList = MutableLiveData<List<Vendor>>()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                showProgress.postValue(true)
                productFeatureRepository.fetchVendors(storeId!!, { response->
                    showProgress.postValue(false)
                    if(response.status == Default.SUCCESS_API) vendorList.postValue(response.data)
                    else vendorList.postValue(ArrayList())
                }, { t->
                    Log.e("Vendor", "onFailure: ", t)
                    showProgress.postValue(false)
                })
            } catch(ex : Exception) {
                Utils.printAndWriteException(ex)
                showProgress.postValue(false)
            }
        }
        return vendorList
    } //endregion

    //region To fetch vendors drop down
    fun fetchVendorDropDown() : MutableLiveData<List<CommonDropDown>> {
        val list = MutableLiveData<List<CommonDropDown>>()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                showProgress.postValue(true)
                productFeatureRepository.fetchVendorDropDown(storeId!!, { response->
                    showProgress.postValue(false)
                    if(response.status == Default.SUCCESS_API) list.postValue(response.data)
                    else list.postValue(ArrayList())
                }, { t->
                    Log.e("Vendor Drop Down", "onFailure: ", t)
                    showProgress.postValue(false)
                })
            } catch(ex : Exception) {
                Utils.printAndWriteException(ex)
                showProgress.postValue(false)
            }
        }
        return list
    } //endregion

    //region To delete vendor
    fun deleteVendor(id : Int) : MutableLiveData<Boolean> {
        val isSuccess = MutableLiveData<Boolean>()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                showProgress.postValue(true)
                productFeatureRepository.deleteVendor(id, storeId!!, { response->
                    showProgress.postValue(false)
                    isSuccess.postValue(response.status == Default.SUCCESS_API)
                    errorMsg.postValue(if(response.message != null) response.message else "")
                }, { t->
                    Log.e("Delete Vendor", "onFailure: ", t)
                    showProgress.postValue(false)
                })
            } catch(ex : Exception) {
                Utils.printAndWriteException(ex)
                showProgress.postValue(false)
            }
        }
        return isSuccess
    } //endregion

    //region To get brand details
    fun getVendorDetails(id : Int) : MutableLiveData<Vendor?> {
        val details = MutableLiveData<Vendor?>()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                showProgress.postValue(true)
                productFeatureRepository.fetchVendorDetails(id, storeId!!, { response->
                    showProgress.postValue(false)
                    if(response.status == Default.SUCCESS_API) details.postValue(response.data)
                }, { t->
                    Log.e("Brand", "onFailure: ", t)
                    showProgress.postValue(false)
                })
            } catch(ex : Exception) {
                Utils.printAndWriteException(ex)
                showProgress.postValue(false)
            }
        }
        return details
    } //endregion

    //region To insert vendor
    fun insertVendor(req : VendorInsertRequest) : MutableLiveData<Boolean> {
        val isSuccess = MutableLiveData<Boolean>()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                showProgress.postValue(true)
                req.actionBy = actionBy
                req.storeId = storeId
                productFeatureRepository.insertVendor(req, { response->
                    showProgress.postValue(false)
                    isSuccess.postValue(response.status == Default.SUCCESS_API)
                    errorMsg.postValue(if (response.message != null) response.message else "")
                }, { t ->
                    Log.e("Insert Vendor", "onFailure: ", t)
                    showProgress.postValue(false)
                })
            } catch (ex: Exception) {
                Utils.printAndWriteException(ex)
                showProgress.postValue(false)
            }
        }
        return isSuccess
    } //endregion

    //region To insert vendor
    fun insertSalePerson(req: AddSalePerson): MutableLiveData<Boolean> {
        val isSuccess = MutableLiveData<Boolean>()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                showProgress.postValue(true)
                productFeatureRepository.insertAddSalePerson(req, { response ->
                    showProgress.postValue(false)
                    isSuccess.postValue(response.status == Default.SUCCESS_API)
                    errorMsg.postValue(if (response.message != null) response.message else "")
                }, { t ->
                    Log.e("Insert Sale Person", "onFailure: ", t)
                    showProgress.postValue(false)
                })
            } catch (ex: Exception) {
                Utils.printAndWriteException(ex)
                showProgress.postValue(false)
            }
        }
        return isSuccess
    } //endregion

    //region To update vendor
    fun updateVendor(req: VendorUpdateRequest): MutableLiveData<Boolean> {
        val isSuccess = MutableLiveData<Boolean>()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                showProgress.postValue(true)
                req.actionBy = actionBy
                productFeatureRepository.updateVendor(req, { response->
                    showProgress.postValue(false)
                    isSuccess.postValue(response.status == Default.SUCCESS_API)
                    errorMsg.postValue(if(response.message != null) response.message else "")
                }, { t->
                    Log.e("Update Facility", "onFailure: ", t)
                    showProgress.postValue(false)
                })
            } catch(ex : Exception) {
                Utils.printAndWriteException(ex)
                showProgress.postValue(false)
            }
        }
        return isSuccess
    } //endregion

    //endregion

    //region To get Item Group
    fun fetchItemGroups(typeId : Int) : MutableLiveData<List<ItemGroupDetail>> {
        val groupList = MutableLiveData<List<ItemGroupDetail>>()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                showProgress.postValue(true)
                productFeatureRepository.fetchGroups(storeId!!,typeId, { response->
                    showProgress.postValue(false)
                    if(response.status == Default.SUCCESS_API) groupList.postValue(response.data)
                    else groupList.postValue(ArrayList())
                }, { t->
                    Log.e("Group List", "onFailure: ", t)
                    showProgress.postValue(false)
                })
            } catch(ex : Exception) {
                Utils.printAndWriteException(ex)
                showProgress.postValue(false)
            }
        }
        return groupList
    }
    //endregion

    //region To get brand Dropdown
    fun getTenderDropDown() : MutableLiveData<List<CommonDropDown>> {
        val tender = MutableLiveData<List<CommonDropDown>>()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                showProgress.postValue(true)
                productFeatureRepository.getTenderDropDown(storeId!!, { response->
                    showProgress.postValue(false)
                    if(response.status == Default.SUCCESS_API) tender.postValue(response.data)
                    else tender.postValue(ArrayList())
                }, { t->
                    Log.e("TENDER DROPDOWN", "onFailure: ", t)
                    showProgress.postValue(false)
                })
            } catch(ex : Exception) {
                Utils.printAndWriteException(ex)
                showProgress.postValue(false)
            }
        }
        return tender
    } //endregion

    //region To fetch Tax/es
    fun fetchPromotionDropDown(typeId : Int) : MutableLiveData<List<CommonDropDown>> {
        val list = MutableLiveData<List<CommonDropDown>>()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                showProgress.postValue(true)
                productFeatureRepository.fetchPromotionDropDown(storeId!!, typeId, { response->
                    showProgress.postValue(false)
                    if(response.status == Default.SUCCESS_API) list.postValue(response.data)
                    else list.postValue(ArrayList())
                }, { t->
                    Log.e("Promotion", "onFailure: ", t)
                    showProgress.postValue(false)
                })
            } catch(ex : Exception) {
                Utils.printAndWriteException(ex)
                showProgress.postValue(false)
            }
        }
        return list
    } //endregion
}