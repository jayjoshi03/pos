package com.varitas.gokulpos.tablet.viewmodel

import android.text.TextUtils
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.varitas.gokulpos.tablet.repositories.CustomerRepository
import com.varitas.gokulpos.tablet.response.CommonDropDown
import com.varitas.gokulpos.tablet.response.CustomerDetail
import com.varitas.gokulpos.tablet.response.Customers
import com.varitas.gokulpos.tablet.utilities.Default
import com.varitas.gokulpos.tablet.utilities.Utils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel class CustomerViewModel @Inject constructor(private val customerRepository: CustomerRepository) : ViewModel() {

    val showProgress: MutableLiveData<Boolean> = MutableLiveData()
    val errorMsg: MutableLiveData<String> = MutableLiveData()
    private val storeDetails = Utils.fetchLoginResponse()
    private val storeId = storeDetails.singleResult!!.storeId
    private val actionBy = storeDetails.singleResult!!.userId

    //region To fetch customers
    fun fetchCustomers(): MutableLiveData<List<Customers>> {
        val customerList = MutableLiveData<List<Customers>>()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                showProgress.postValue(true)
                customerRepository.fetchCustomers(storeId!!, 1,  { response ->
                    showProgress.postValue(false)
                    if (response.status == Default.SUCCESS_API) {
                        if (response.data != null) customerList.postValue(response.data!!.filter { it.status == Default.ACTIVE })
                    } else customerList.postValue(ArrayList())
                    if (!TextUtils.isEmpty(response.message)) errorMsg.postValue(if (response.message != null) response.message else "")
                }, { t ->
                    Log.e("Categories", "onFailure: ", t)
                    showProgress.postValue(false)
                })
            } catch (ex: Exception) {
                Utils.printAndWriteException(ex)
                showProgress.postValue(false)
            }
        }
        return customerList
    } //endregion

    //region To fetch customer details
    fun fetchCustomerDetails(customerId: Int): MutableLiveData<CustomerDetail?> {
        val customer = MutableLiveData<CustomerDetail?>()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                showProgress.postValue(true)
                customerRepository.fetchCustomerDetail(storeId!!, customerId, { response ->
                    showProgress.postValue(false)
                    if (response.status == Default.SUCCESS_API) customer.postValue(response.data)
                    else if (!TextUtils.isEmpty(response.message)) errorMsg.postValue(if (response.message != null) response.message else "")
                }, { t ->
                    Log.e("Categories", "onFailure: ", t)
                    showProgress.postValue(false)
                })
            } catch (ex: Exception) {
                Utils.printAndWriteException(ex)
                showProgress.postValue(false)
            }
        }
        return customer
    } //endregion

    //region To delete customer
    fun deleteCustomer(id: Int): MutableLiveData<Boolean> {
        val isSuccess = MutableLiveData<Boolean>()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                showProgress.postValue(true)
                customerRepository.deleteCustomer(id, storeId!!, { response ->
                    showProgress.postValue(false)
                    isSuccess.postValue(response.status == Default.SUCCESS_API)
                    errorMsg.postValue(if (response.message != null) response.message else "")
                }, { t ->
                    Log.e("Delete Customer", "onFailure: ", t)
                    showProgress.postValue(false)
                })
            } catch (ex: Exception) {
                Utils.printAndWriteException(ex)
                showProgress.postValue(false)
            }
        }
        return isSuccess
    } //endregion

    //region To insert customer
    fun insertCustomer(req : Customers) : MutableLiveData<Boolean> {
        val isSuccess = MutableLiveData<Boolean>()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                showProgress.postValue(true)
                req.actionBy = actionBy
                req.storeId = storeId!!
                customerRepository.insertCustomer(req, { response->
                    showProgress.postValue(false)
                    isSuccess.postValue(response.status == Default.SUCCESS_API)
                    errorMsg.postValue(if(response.message != null) response.message else "")
                }, { t->
                    Log.e("Insert Customer", "onFailure: ", t)
                    showProgress.postValue(false)
                })
            } catch(ex : Exception) {
                Utils.printAndWriteException(ex)
                showProgress.postValue(false)
            }
        }
        return isSuccess
    } //endregion

    //region To get customers by id
    fun getCustomerDetail(customerID : Int) : MutableLiveData<Customers> {
        val customerDetail = MutableLiveData<Customers>()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                showProgress.postValue(true)
                customerRepository.getCustomerDetail(customerID, { response->
                    showProgress.postValue(false)
                    if(response.status == Default.SUCCESS_API) customerDetail.postValue(response.data)
                }, { t->
                    Log.e("Customer Detail", "onFailure: ", t)
                    showProgress.postValue(false)
                })
            } catch(ex : Exception) {
                Utils.printAndWriteException(ex)
                showProgress.postValue(false)
            }
        }
        return customerDetail
    } //endregion

    //region To update Customer
    fun updateCustomer(req : Customers) : MutableLiveData<Boolean> {
        val isSuccess = MutableLiveData<Boolean>()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                showProgress.postValue(true)
                req.storeId = storeId!!
                req.actionBy = actionBy
                customerRepository.updateCustomer(req, { response->
                    showProgress.postValue(false)
                    isSuccess.postValue(response.status == Default.SUCCESS_API)
                    errorMsg.postValue(if(response.message != null) response.message else "")
                }, { t->
                    Log.e("Update Customer", "onFailure: ", t)
                    showProgress.postValue(false)
                })
            } catch(ex : Exception) {
                Utils.printAndWriteException(ex)
                showProgress.postValue(false)
            }
        }
        return isSuccess
    } //endregion


    //region To get Customer drop down
    fun getCustomerDropDown(): MutableLiveData<List<CommonDropDown>> {
        val parentCategoryList = MutableLiveData<List<CommonDropDown>>()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                showProgress.postValue(true)
                customerRepository.fetchCustomerDropDown(storeId!!, { response ->
                    showProgress.postValue(false)
                    if (response.status == Default.SUCCESS_API) parentCategoryList.postValue(response.data)
                    else parentCategoryList.postValue(ArrayList())
                }, { t ->
                    Log.e("Customer DropDown", "onFailure: ", t)
                    showProgress.postValue(false)
                })
            } catch (ex: Exception) {
                Utils.printAndWriteException(ex)
                showProgress.postValue(false)
            }
        }
        return parentCategoryList
    } //endregion
}