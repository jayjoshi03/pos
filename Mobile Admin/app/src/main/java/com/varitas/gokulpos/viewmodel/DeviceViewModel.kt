package com.varitas.gokulpos.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.varitas.gokulpos.repositories.DevicesRepository
import com.varitas.gokulpos.response.DeviceList
import com.varitas.gokulpos.utilities.Utils
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel class DeviceViewModel @Inject constructor(private val devicesRepository : DevicesRepository) : ViewModel() {

    val showProgress : MutableLiveData<Boolean> = MutableLiveData()
    private val storeDetails = Utils.fetchLoginResponse()
    private val storeId = storeDetails.singleResult!!.storeId

    //region To fetch devices
    fun fetchDevices() : MutableLiveData<List<DeviceList>> {
        val list = MutableLiveData<List<DeviceList>>()
//        viewModelScope.launch(Dispatchers.IO) {
//            try {
//                showProgress.postValue(true)
//                devicesRepository.fetchDevices(storeId!!,{ response ->
//                    showProgress.postValue(false)
//                    if (response.status == Default.SUCCESS_API) list.postValue(response.listResult)
//                    else list.postValue(ArrayList())
//                }, { t ->
//                    Log.e("Categories", "onFailure: ", t)
//                    showProgress.postValue(false)
//                })
//            } catch (ex: Exception) {
//                Utils.printAndWriteException(ex)
//                showProgress.postValue(false)
//            }
//        }
        return list
    } //endregion
}