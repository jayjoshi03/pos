package com.varitas.gokulpos.tablet.viewmodel


import android.text.TextUtils
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.varitas.gokulpos.tablet.R
import com.varitas.gokulpos.tablet.activity.DashboardActivity
import com.varitas.gokulpos.tablet.repositories.DashboardRepository
import com.varitas.gokulpos.tablet.response.DropDown
import com.varitas.gokulpos.tablet.response.Menus
import com.varitas.gokulpos.tablet.response.TopSellingItems
import com.varitas.gokulpos.tablet.utilities.Default
import com.varitas.gokulpos.tablet.utilities.Utils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel class DashViewModel @Inject constructor(private val dashboardRepository: DashboardRepository) : ViewModel() {

    val showProgress: MutableLiveData<Boolean> = MutableLiveData()
    val errorMsg: MutableLiveData<String> = MutableLiveData()
    private val storeDetails = Utils.fetchLoginResponse()
    private val storeId = storeDetails.singleResult!!.storeId
    private val roleId = storeDetails.singleResult!!.roleId

    //region To setup bar chart data
    fun setBarChart(paymentMethod: Int): MutableLiveData<BarData> {
        val barList = MutableLiveData<BarData>()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                showProgress.postValue(true)
                dashboardRepository.fetchChartData(paymentMethod, storeId!!, { response ->
                    showProgress.postValue(false)
                    val chartData = ArrayList<BarEntry>()
                    if (response.status == Default.SUCCESS_API) {
                        if (response.data!!.size <= 7) {
                            for (i in 1..7 - response.data!!.size) chartData.add(BarEntry(chartData.size.toFloat() + 1, 0.00F))
                        }

                        if (response.data!!.isNotEmpty()) {
                            for (j in response.data!!.indices) chartData.add(BarEntry(chartData.size.toFloat() + 1, (response.data!![j].orderTotal!!).toFloat(), response.data!![j]))
                        }

                        val barDataSet = BarDataSet(chartData, DashboardActivity.Instance.resources.getString(R.string.lbl_LastSevenDaysSell))
                        barDataSet.apply {
                            setColors(ContextCompat.getColor(DashboardActivity.Instance, R.color.base_color), ContextCompat.getColor(DashboardActivity.Instance, R.color.pink))
                            valueTextColor = R.color.darkGrey // setting text size
                            valueTextSize = 16f
                        }

                        val barData = BarData(barDataSet)
                        barData.barWidth = 0.4f
                        barList.postValue(barData)
                    } else barList.postValue(BarData())
                }, { t ->
                    Log.e("Weekly Sale", "onFailure: ", t)
                    showProgress.postValue(false)
                })
            } catch (ex: Exception) {
                Utils.printAndWriteException(ex)
                showProgress.postValue(false)
            }
        }
        return barList
    } //endregion

    //region To fetch top selling products
    fun fetchTopProducts(paymentMethod: Int): MutableLiveData<List<TopSellingItems>> {
        val productList = MutableLiveData<List<TopSellingItems>>()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                showProgress.postValue(true)
                dashboardRepository.fetchTopSellingProducts(paymentMethod, storeId!!, { response ->
                    showProgress.postValue(false)
                    if (response.status == Default.SUCCESS_API) productList.postValue(response.data)
                    else productList.postValue(ArrayList())
                    if (!TextUtils.isEmpty(response.message)) errorMsg.postValue(if (response.message != null) response.message else "")
                }, { t ->
                    Log.e("Top Selling Products", "onFailure: ", t)
                    showProgress.postValue(false)
                })
            } catch (ex: Exception) {
                Utils.printAndWriteException(ex)
                showProgress.postValue(false)
            }
        }
        return productList
    } //endregion

    //region To fetch menus
    fun fetchMenus(parentId : Int = 0): MutableLiveData<List<Menus>> {
        val menuList = MutableLiveData<List<Menus>>()
        viewModelScope.launch(Dispatchers.IO) {
            showProgress.postValue(true)
            dashboardRepository.fetchMenus(parentId, roleId!!, { response ->
                showProgress.postValue(false)
                if (response.status == Default.SUCCESS_API) menuList.postValue(response.data)
                else menuList.postValue(ArrayList())
                if (!TextUtils.isEmpty(response.message)) errorMsg.postValue(if (response.message != null) response.message else "")
            }, { t ->
                Log.e("Menus", "onFailure: ", t)
                showProgress.postValue(false)
            })
        }
        return menuList
    } //endregion

    //region To fetch tenders
    fun fetchTenders(): MutableLiveData<List<DropDown>> {
        val tenders = MutableLiveData<List<DropDown>>()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                showProgress.postValue(true)
                dashboardRepository.fetchTenders(pageNumber = 1, storeId!!, { response ->
                    showProgress.postValue(false)
                    if (response.status == Default.SUCCESS_API) tenders.postValue(response.data)
                    else tenders.postValue(ArrayList())
                    if (!TextUtils.isEmpty(response.message)) errorMsg.postValue(if (response.message != null) response.message else "")
                }, { t ->
                    Log.e("Tenders", "onFailure: ", t)
                    showProgress.postValue(false)
                })
            } catch (ex: Exception) {
                Utils.printAndWriteException(ex)
                showProgress.postValue(false)
            }
        }
        return tenders
    } //endregion
}