package com.varitas.gokulpos.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.varitas.gokulpos.repositories.ProductFeatureRepository
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
import com.varitas.gokulpos.response.Brand
import com.varitas.gokulpos.response.Category
import com.varitas.gokulpos.response.CommonDropDown
import com.varitas.gokulpos.response.Department
import com.varitas.gokulpos.response.Facility
import com.varitas.gokulpos.response.Group
import com.varitas.gokulpos.response.ItemCount
import com.varitas.gokulpos.response.SalesPerson
import com.varitas.gokulpos.response.Specification
import com.varitas.gokulpos.response.Tax
import com.varitas.gokulpos.response.UOM
import com.varitas.gokulpos.response.Vendor
import com.varitas.gokulpos.utilities.Default
import com.varitas.gokulpos.utilities.Utils
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
                productFeatureRepository.fetchCategories(storeId!!, { response->
                    if(response.status == Default.SUCCESS_API) categoriesList.postValue(response.data)
                    else categoriesList.postValue(ArrayList())
                }, { t->
                    Log.e("Categories", "onFailure: ", t)
                })
            } catch(ex : Exception) {
                Utils.printAndWriteException(ex)
            }
        }
        return categoriesList
    } //endregion

    //region To fetch all sub-categories
    fun fetSubCategories(catId : Int) : MutableLiveData<List<CommonDropDown>> {
        val subCategory = MutableLiveData<List<CommonDropDown>>()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                showProgress.postValue(true)
                productFeatureRepository.getSubCategoryDropDown(catID = catId, { response->
                    showProgress.postValue(false)
                    if(response.status == Default.SUCCESS_API) subCategory.postValue(response.data)
                    else subCategory.postValue(ArrayList())
                }, { t->
                    Log.e("SUBCATEGORY DROPDOWN", "onFailure: ", t)
                    showProgress.postValue(false)
                })
            } catch(ex : Exception) {
                Utils.printAndWriteException(ex)
                showProgress.postValue(false)
            }
        }
        return subCategory
    } //endregion

    //region To insert categories
    fun insertCategories(req : CategoryInsertUpdate) : MutableLiveData<Boolean> {
        val isSuccess = MutableLiveData<Boolean>()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                showProgress.postValue(true)
                req.actionBy = actionBy
                req.storeId = storeId!!
                productFeatureRepository.insertCategories(req, { response->
                    showProgress.postValue(false)
                    isSuccess.postValue(response.status == Default.SUCCESS_API)
                    errorMsg.postValue(if(response.message != null) response.message else "")
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
    } //endregion

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
                        errorMsg.postValue(if(response.message != null) response.message else "")
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
                productFeatureRepository.fetchDepartments(storeId!!, { response->
                    if(response.status == Default.SUCCESS_API) departmentList.postValue(response.data)
                    else departmentList.postValue(ArrayList())
                }, { t->
                    Log.e("Departments", "onFailure: ", t)
                })
            } catch(ex : Exception) {
                Utils.printAndWriteException(ex)
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
                        errorMsg.postValue(if(response.message != null) response.message else "")
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
                productFeatureRepository.fetchBrands(storeId!!, { response->
                    if(response.status == Default.SUCCESS_API) brandList.postValue(response.data)
                    else brandList.postValue(ArrayList())
                }, { t->
                    Log.e("Categories", "onFailure: ", t)
                })
            } catch(ex : Exception) {
                Utils.printAndWriteException(ex)
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
    fun insertBrand(req : BrandInsertRequest) : MutableLiveData<Boolean> {
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
    fun updateBrand(req : BrandUpdateRequest) : MutableLiveData<Boolean> {
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
                    else {
                        itemCount.postValue(ArrayList())
                        errorMsg.postValue(if(response.message != null) response.message else "")
                    }
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
                productFeatureRepository.fetchTaxes(storeId!!, { response->
                    if(response.status == Default.SUCCESS_API) list.postValue(response.data)
                    else list.postValue(ArrayList())
                }, { t->
                    Log.e("Tax", "onFailure: ", t)
                })
            } catch(ex : Exception) {
                Utils.printAndWriteException(ex)
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

    //region GROUPS

    //region To fetch Tax/es
    fun fetchTaxGroups(type : Int) : MutableLiveData<List<CommonDropDown>> {
        val list = MutableLiveData<List<CommonDropDown>>()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                productFeatureRepository.fetchTaxGroup(type, storeId!!, { response->
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
    fun fetchGroups() : MutableLiveData<List<Group>> {
        val list = MutableLiveData<List<Group>>()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                productFeatureRepository.fetchGroup(storeId!!, { response->
                    if(response.status == Default.SUCCESS_API) list.postValue(response.data)
                    else list.postValue(ArrayList())
                }, { t->
                    Log.e("Groups", "onFailure: ", t)
                })
            } catch(ex : Exception) {
                Utils.printAndWriteException(ex)
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
    fun getGroupDetails(groupId : Int) : MutableLiveData<GroupInsertUpdateRequest> {
        val groupDetail = MutableLiveData<GroupInsertUpdateRequest>()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                showProgress.postValue(true)
                productFeatureRepository.getGroupDetails(groupId, storeId!!, { response->
                    showProgress.postValue(false)
                    if(response.status == Default.SUCCESS_API) groupDetail.postValue(response.data)
                }, { t->
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
                req.actionBy = actionBy
                req.storeId = storeId!!
                productFeatureRepository.insertGroup(req, { response->
                    showProgress.postValue(false)
                    isSuccess.postValue(response.status == Default.SUCCESS_API)
                    errorMsg.postValue(if(response.message != null) response.message else "")
                }, { t->
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
    fun getGroupTypeList(type : Int) : MutableLiveData<List<Group>> {
        val list = MutableLiveData<List<Group>>()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                productFeatureRepository.getGroupTypeList(type = type, storeId!!, { response->
                    if(response.status == Default.SUCCESS_API) list.postValue(response.data)
                    else list.postValue(ArrayList())
                }, { t->
                    Log.e("Group Type", "onFailure: ", t)
                })
            } catch(ex : Exception) {
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
                productFeatureRepository.updateGroup(req, { response->
                    showProgress.postValue(false)
                    isSuccess.postValue(response.status == Default.SUCCESS_API)
                    errorMsg.postValue(if(response.message != null) response.message else "")
                }, { t->
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

    //region To get group details by Id
    fun getGroupTypeDetails(groupId : Int) : MutableLiveData<GroupInsertUpdateRequest> {
        val groupDetail = MutableLiveData<GroupInsertUpdateRequest>()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                showProgress.postValue(true)
                productFeatureRepository.getGroupTypeDetails(groupId, storeId!!, { response->
                    showProgress.postValue(false)
//                    if (response.status == Default.SUCCESS_API) groupDetail.postValue(response.data)
//                    else errorMsg.postValue(if(response.message != null) response.message else "")
                    groupDetail.postValue(response.data)
                }, { t->
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

    //endregion

    //region SPECIFICATION

    //region To get Specification
    fun fetchSpecification() : MutableLiveData<List<Specification>> {
        val specification = MutableLiveData<List<Specification>>()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                productFeatureRepository.fetchSpecification(storeId!!, { response->
                    if(response.status == Default.SUCCESS_API) specification.postValue(response.data)
                    else specification.postValue(ArrayList())
                }, { t->
                    Log.e("Specification", "onFailure: ", t)
                })
            } catch(ex : Exception) {
                Utils.printAndWriteException(ex)
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
    fun getSpecificationType(type : Int) : MutableLiveData<List<CommonDropDown>> {
        val specificationType = MutableLiveData<List<CommonDropDown>>()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                showProgress.postValue(true)
                productFeatureRepository.getSpecificationType(type, storeId!!, { response->
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

    //region To insert specification Type
    fun insertSpecificationType(req : SpecificationTypeInsertRequest) : MutableLiveData<Boolean> {
        val isSuccess = MutableLiveData<Boolean>()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                showProgress.postValue(true)
                req.createdBy = actionBy
                req.storeId = storeId!!
                productFeatureRepository.insertSpecificationType(req, { response->
                    showProgress.postValue(false)
                    isSuccess.postValue(response.status == Default.SUCCESS_API)
                    errorMsg.postValue(if(response.message != null) response.message else "")
                }, { t->
                    Log.e("Insert Specification Type", "onFailure: ", t)
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

    //region To get Specification
    fun getSpecificationDropDown() : MutableLiveData<List<CommonDropDown>> {
        val specification = MutableLiveData<List<CommonDropDown>>()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                showProgress.postValue(true)
                productFeatureRepository.getSpecificationDropDown(storeId!!, { response->
                    showProgress.postValue(false)
                    if(response.status == Default.SUCCESS_API) specification.postValue(response.data)
                    else specification.postValue(ArrayList())
                }, { t->
                    Log.e("SPECIFICATION", "onFailure: ", t)
                    showProgress.postValue(false)
                })
            } catch(ex : Exception) {
                Utils.printAndWriteException(ex)
                showProgress.postValue(false)
            }
        }
        return specification
    } //endregion

    //endregion

    //region Facility

    //region To get Facility
    fun fetchFacility() : MutableLiveData<List<Facility>> {
        val facility = MutableLiveData<List<Facility>>()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                productFeatureRepository.fetchFacility(storeId!!, { response->
                    if(response.status == Default.SUCCESS_API) facility.postValue(response.data)
                    else facility.postValue(ArrayList())
                }, { t->
                    Log.e("Facility", "onFailure: ", t)
                })
            } catch(ex : Exception) {
                Utils.printAndWriteException(ex)
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
                productFeatureRepository.fetchUOM(storeId!!, { response->
                    if(response.status == Default.SUCCESS_API) uom.postValue(response.data)
                    else uom.postValue(ArrayList())
                }, { t->
                    Log.e("UOM", "onFailure: ", t)
                })
            } catch(ex : Exception) {
                Utils.printAndWriteException(ex)
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
        val brandList = MutableLiveData<List<Vendor>>()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                productFeatureRepository.fetchVendors(storeId!!, { response->
                    if(response.status == Default.SUCCESS_API) brandList.postValue(response.data)
                    else brandList.postValue(ArrayList())
                }, { t->
                    Log.e("Categories", "onFailure: ", t)
                })
            } catch(ex : Exception) {
                Utils.printAndWriteException(ex)
            }
        }
        return brandList
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

    //region To get vendor details
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
                    errorMsg.postValue(if(response.message != null) response.message else "")
                }, { t->
                    Log.e("Insert Vendor", "onFailure: ", t)
                    showProgress.postValue(false)
                })
            } catch(ex : Exception) {
                Utils.printAndWriteException(ex)
                showProgress.postValue(false)
            }
        }
        return isSuccess
    } //endregion

    //region To update vendor
    fun updateVendor(req : VendorUpdateRequest) : MutableLiveData<Boolean> {
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

    //region To fetch vendor dropdown
    fun getVendorDropDown() : MutableLiveData<List<CommonDropDown>> {
        val vendorDropdown = MutableLiveData<List<CommonDropDown>>()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                showProgress.postValue(true)
                productFeatureRepository.getVendorDropDown(storeId!!, { response->
                    showProgress.postValue(false)
                    if(response.status == Default.SUCCESS_API) vendorDropdown.postValue(response.data)
                    else vendorDropdown.postValue(ArrayList())
                }, { t->
                    Log.e("VENDOR DROPDOWN", "onFailure: ", t)
                    showProgress.postValue(false)
                })
            } catch(ex : Exception) {
                Utils.printAndWriteException(ex)
                showProgress.postValue(false)
            }
        }
        return vendorDropdown
    } //endregion

    //region To Add Sales Person
    fun addSalesPerson(req : SalesPerson) : MutableLiveData<Boolean> {
        val isSuccess = MutableLiveData<Boolean>()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                showProgress.postValue(true)
                productFeatureRepository.addSalesPerson(req, { response->
                    showProgress.postValue(false)
                    isSuccess.postValue(response.status == Default.SUCCESS_API)
                    errorMsg.postValue(if(response.message != null) response.message else "")
                }, { t->
                    Log.e("Add Sales Person", "onFailure: ", t)
                    showProgress.postValue(false)
                })
            } catch(ex : Exception) {
                Utils.printAndWriteException(ex)
                showProgress.postValue(false)
            }
        }
        return isSuccess
    } //endregion

    //region To fetch Sales person List
    fun getSalesPersonCount(id : Int) : MutableLiveData<List<SalesPerson>> {
        val salesPersonList = MutableLiveData<List<SalesPerson>>()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                showProgress.postValue(true)
                productFeatureRepository.getSalesPersonCount(id, { response->
                    showProgress.postValue(false)
                    if(response.status == Default.SUCCESS_API) salesPersonList.postValue(response.data)
                    else salesPersonList.postValue(ArrayList())
                }, { t->
                    Log.e("Sales Person Count", "onFailure: ", t)
                    showProgress.postValue(false)
                })
            } catch(ex : Exception) {
                Utils.printAndWriteException(ex)
                showProgress.postValue(false)
            }
        }
        return salesPersonList
    } //endregion

    //endregion
}