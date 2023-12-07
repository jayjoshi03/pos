package com.varitas.gokulpos.tablet.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.varitas.gokulpos.tablet.repositories.ReportRepository
import com.varitas.gokulpos.tablet.response.Menus
import com.varitas.gokulpos.tablet.response.Payments
import com.varitas.gokulpos.tablet.response.SalesSummary
import com.varitas.gokulpos.tablet.response.TopItems
import com.varitas.gokulpos.tablet.response.TopItemsByCategories
import com.varitas.gokulpos.tablet.utilities.Default
import com.varitas.gokulpos.tablet.utilities.Utils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel class ReportsViewModel @Inject constructor(private val reportRepository: ReportRepository) : ViewModel() {

    val showProgress: MutableLiveData<Boolean> = MutableLiveData()
    private val storeDetails = Utils.fetchLoginResponse()
    private val storeId = storeDetails.singleResult!!.storeId

    //region To fetch menus
    fun fetchMenus(context: Context, isFromProduct : Boolean): MutableLiveData<List<Menus>> {
        val menuList = MutableLiveData<List<Menus>>()
        viewModelScope.launch(Dispatchers.IO) {
            menuList.postValue(reportRepository.getMenus(context, isFromProduct))
        }
        return menuList
    } //endregion

    //region To get sales summary
    fun fetchSalesSummary(startDate: String, endDate: String): MutableLiveData<SalesSummary?> {
        val summary = MutableLiveData<SalesSummary?>()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                showProgress.postValue(true)
                reportRepository.fetchSales(storeId!!, startDate, endDate, { response ->
                    showProgress.postValue(false)
                    if (response.status == Default.SUCCESS_API) summary.postValue(response.data)
                }, { t ->
                    Log.e("Sales", "onFailure: ", t)
                    showProgress.postValue(false)
                })
            } catch (ex: Exception) {
                Utils.printAndWriteException(ex)
                showProgress.postValue(false)
            }
        }
        return summary
    } //endregion

    //region To get payment types
    fun fetchPayments(startDate: String, endDate: String): MutableLiveData<List<Payments>> {
        val summary = MutableLiveData<List<Payments>>()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                showProgress.postValue(true)
                reportRepository.fetchPaymentTypes(storeId!!, startDate, endDate, { response ->
                    showProgress.postValue(false)
                    if (response.status == Default.SUCCESS_API) summary.postValue(response.data)
                    else summary.postValue(ArrayList())
                }, { t ->
                    Log.e("Payments", "onFailure: ", t)
                    showProgress.postValue(false)
                })
            } catch (ex: Exception) {
                Utils.printAndWriteException(ex)
                showProgress.postValue(false)
            }
        }
        return summary
    } //endregion

    //region To get top items
    fun fetchTopItems(startDate: String, endDate: String): MutableLiveData<List<TopItems>> {
        val itemList = MutableLiveData<List<TopItems>>()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                showProgress.postValue(true)
                reportRepository.fetchTopItems(storeId!!, startDate, endDate, { response ->
                    showProgress.postValue(false)
                    if (response.status == Default.SUCCESS_API) itemList.postValue(response.data)
                    else itemList.postValue(ArrayList())
                }, { t ->
                    Log.e("Top Items", "onFailure: ", t)
                    showProgress.postValue(false)
                })
            } catch (ex: Exception) {
                Utils.printAndWriteException(ex)
                showProgress.postValue(false)
            }
        }
        return itemList
    } //endregion

    //region To get sales summary
    fun fetchTopItemsByCategories(startDate: String, endDate: String): MutableLiveData<TopItemsByCategories?> {
        val list = MutableLiveData<TopItemsByCategories?>()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                showProgress.postValue(true)
                reportRepository.fetchTopCategories(storeId!!, startDate, endDate, { response ->
                    showProgress.postValue(false)
                    if (response.status == Default.SUCCESS_API) list.postValue(response.data)
                }, { t ->
                    Log.e("Sales", "onFailure: ", t)
                    showProgress.postValue(false)
                })
            } catch (ex: Exception) {
                Utils.printAndWriteException(ex)
                showProgress.postValue(false)
            }
        }
        return list
    } //endregion
}