package com.varitas.gokulpos.viewmodel


import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.varitas.gokulpos.utilities.Utils
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel class ReportsViewModel @Inject constructor() : ViewModel() {
    val showProgress : MutableLiveData<Boolean> = MutableLiveData()
    private val storeDetails = Utils.fetchLoginResponse()
    val storeId = storeDetails.singleResult!!.storeId
    val errorMsg = MutableLiveData<String>()
}