package com.varitas.gokulpos.tablet.viewmodel

import android.text.TextUtils
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.varitas.gokulpos.tablet.repositories.EmployeeRepository
import com.varitas.gokulpos.tablet.response.CommonDropDown
import com.varitas.gokulpos.tablet.response.Employee
import com.varitas.gokulpos.tablet.utilities.Default
import com.varitas.gokulpos.tablet.utilities.Utils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel class EmployeeViewModel @Inject constructor(private val employeeRepository : EmployeeRepository) : ViewModel() {

    val showProgress : MutableLiveData<Boolean> = MutableLiveData()
    private val storeDetails = Utils.fetchLoginResponse()
    private val storeId = storeDetails.singleResult!!.storeId
    private val actionBy = storeDetails.singleResult!!.userId
    val errorMsg : MutableLiveData<String> = MutableLiveData()

    //region To fetch employees
    fun fetchEmployees() : MutableLiveData<List<Employee>> {
        val empList = MutableLiveData<List<Employee>>()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                showProgress.postValue(true)
                employeeRepository.fetchEmployees(storeId!!, { response->
                    showProgress.postValue(false)
                    if(response.status == Default.SUCCESS_API) empList.postValue(response.data)
                    else {
                        empList.postValue(ArrayList())
                        if(!TextUtils.isEmpty(response.message)) errorMsg.postValue(if(response.message != null) response.message else "")
                    }
                }, { t->
                    Log.e("Categories", "onFailure: ", t)
                    showProgress.postValue(false)
                })
            } catch(ex : Exception) {
                Utils.printAndWriteException(ex)
                showProgress.postValue(false)
            }
        }
        return empList
    } //endregion

    //region To fetch employee details
    fun fetchEmployeeDetails(empId : Int) : MutableLiveData<Employee?> {
        val employee = MutableLiveData<Employee?>()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                showProgress.postValue(true)
                employeeRepository.fetchEmployeeDetail(empId, { response->
                    showProgress.postValue(false)
                    if(response.status == Default.SUCCESS_API) employee.postValue(response.data)
                }, { t->
                    Log.e("Employee Detail", "onFailure: ", t)
                    showProgress.postValue(false)
                })
            } catch(ex : Exception) {
                Utils.printAndWriteException(ex)
                showProgress.postValue(false)
            }
        }
        return employee
    } //endregion

    //region To fetch roles
    fun fetchRoles() : MutableLiveData<List<CommonDropDown>> {
        val list = MutableLiveData<List<CommonDropDown>>()
        viewModelScope.launch(Dispatchers.IO) {
            try {
//                showProgress.postValue(true)
                employeeRepository.fetchRoles(storeId!!, { response->
                    showProgress.postValue(false)
                    if(response.status == Default.SUCCESS_API) list.postValue(response.data)
                    else list.postValue(ArrayList())
                }, { t->
                    Log.e("Roles", "onFailure: ", t)
                    showProgress.postValue(false)
                })
            } catch(ex : Exception) {
                Utils.printAndWriteException(ex)
                showProgress.postValue(false)
            }
        }
        return list
    } //endregion

    //region To insert employee
    fun insertEmployee(req : Employee) : MutableLiveData<Boolean> {
        val isSuccess = MutableLiveData<Boolean>()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                showProgress.postValue(true)
                req.storeId = storeId
                req.actionBy = actionBy
                employeeRepository.insertEmployee(req, { response->
                    showProgress.postValue(false)
                    isSuccess.postValue(response.status == Default.SUCCESS_API)
                    errorMsg.postValue(if(response.message != null) response.message else "")
                }, { t->
                    Log.e("Insert Employee", "onFailure: ", t)
                    showProgress.postValue(false)
                })
            } catch(ex : Exception) {
                Utils.printAndWriteException(ex)
                showProgress.postValue(false)
            }
        }
        return isSuccess
    } //endregion

    //region To delete customer
    fun deleteEmployee(id : Int) : MutableLiveData<Boolean> {
        val isSuccess = MutableLiveData<Boolean>()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                showProgress.postValue(true)
                employeeRepository.deleteEmployee(id, storeId!!, { response->
                    showProgress.postValue(false)
                    isSuccess.postValue(response.status == Default.SUCCESS_API)
                    errorMsg.postValue(if(response.message != null) response.message else "")
                }, { t->
                    Log.e("Delete Customer", "onFailure: ", t)
                    showProgress.postValue(false)
                })
            } catch(ex : Exception) {
                Utils.printAndWriteException(ex)
                showProgress.postValue(false)
            }
        }
        return isSuccess
    } //endregion

    //region To update Employee
    fun updateEmployee(req : Employee) : MutableLiveData<Boolean> {
        val isSuccess = MutableLiveData<Boolean>()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                showProgress.postValue(true)
                req.storeId = storeId!!
                req.actionBy = actionBy
                employeeRepository.updateEmployee(req, { response->
                    showProgress.postValue(false)
                    isSuccess.postValue(response.status == Default.SUCCESS_API)
                    errorMsg.postValue(if(response.message != null) response.message else "")
                }, { t->
                    Log.e("Update Employee", "onFailure: ", t)
                    showProgress.postValue(false)
                })
            } catch(ex : Exception) {
                Utils.printAndWriteException(ex)
                showProgress.postValue(false)
            }
        }
        return isSuccess
    } //endregion
}