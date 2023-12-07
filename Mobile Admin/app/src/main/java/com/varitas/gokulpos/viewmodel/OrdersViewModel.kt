package com.varitas.gokulpos.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.varitas.gokulpos.repositories.OrdersRepository
import com.varitas.gokulpos.repositories.ProductsRepository
import com.varitas.gokulpos.response.CommonDropDown
import com.varitas.gokulpos.response.FavouriteItems
import com.varitas.gokulpos.response.Invoice
import com.varitas.gokulpos.response.OrderList
import com.varitas.gokulpos.response.PurchaseOrder
import com.varitas.gokulpos.response.Store
import com.varitas.gokulpos.response.StoreStatus
import com.varitas.gokulpos.response.Tender
import com.varitas.gokulpos.utilities.Constants
import com.varitas.gokulpos.utilities.Default
import com.varitas.gokulpos.utilities.Utils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.moallemi.tools.extension.date.now
import me.moallemi.tools.extension.date.toDay
import javax.inject.Inject

@HiltViewModel class OrdersViewModel @Inject constructor(private val ordersRepository : OrdersRepository, private val productsRepository : ProductsRepository) : ViewModel() {

    val showProgress : MutableLiveData<Boolean> = MutableLiveData()
    private val storeDetails = Utils.fetchLoginResponse()
    private val storeId = storeDetails.singleResult!!.storeId
    val errorMsg : MutableLiveData<String> = MutableLiveData()
    private val actionBy = storeDetails.singleResult!!.userId

    //region Order

    //region To get Customer Orders
    fun getCustomerOrder(customerID : Int, startDate : String, endDate : String) : MutableLiveData<List<OrderList>> {
        val list = MutableLiveData<List<OrderList>>()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                ordersRepository.getCustomerOrder(customerID, startDate, endDate, { response->
                    if(response.status == Default.SUCCESS_API) list.postValue(response.data)
                    else list.postValue(ArrayList())
                }, { t->
                    Log.e("Customer Orders", "onFailure: ", t)
                })
            } catch(ex : Exception) {
                Utils.printAndWriteException(ex)
            }
        }
        return list
    } //endregion

    //region To get Employee Orders
    fun getEmployeeOrder(userID : Int, startDate : String, endDate : String) : MutableLiveData<List<OrderList>> {
        val list = MutableLiveData<List<OrderList>>()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                ordersRepository.getEmployeeOrder(userID, startDate, endDate, { response->
                    if(response.status == Default.SUCCESS_API) list.postValue(response.data)
                    else list.postValue(ArrayList())
                }, { t->
                    Log.e("Employee Orders", "onFailure: ", t)
                })
            } catch(ex : Exception) {
                Utils.printAndWriteException(ex)
            }
        }
        return list
    } //endregion

    //region To last order by employee
    fun fetchAllOrderList() : MutableLiveData<List<OrderList>> {
        val list = MutableLiveData<List<OrderList>>()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                showProgress.postValue(true)
                val startDate = Constants.dateFormat_MM_dd_yyyy.format(now())
                val endDate = Constants.dateFormat_MM_dd_yyyy.format(1.toDay().sinceNow)
                ordersRepository.fetchAllOrderList(employeeId = actionBy!!, storeId = storeId!!, startDate = startDate, endDate = endDate, Default.ALL_DATA, { response->
                    showProgress.postValue(false)
                    if(response.status == Default.SUCCESS_API) {
                        list.postValue(response.data)
                    } else errorMsg.postValue(if(response.message != null) response.message else "")
                }, { t->
                    Log.e("Last Order", "onFailure: ", t)
                    showProgress.postValue(false)
                })
            } catch(ex : Exception) {
                Utils.printAndWriteException(ex)
                showProgress.postValue(false)
            }
        }
        return list
    } //endregion

    //endregion

    //region Tender
    //region To fetch Tender
    fun getTenderList() : MutableLiveData<List<Tender>> {
        val list = MutableLiveData<List<Tender>>()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                ordersRepository.getTenderList(storeId!!, { response->
                    if(response.status == Default.SUCCESS_API) list.postValue(response.data)
                    else list.postValue(ArrayList())
                }, { t->
                    Log.e("Tender", "onFailure: ", t)
                })
            } catch(ex : Exception) {
                Utils.printAndWriteException(ex)
            }
        }
        return list
    } //endregion

    //region Tender Delete
    fun deleteTender(id : Int) : MutableLiveData<Boolean> {
        val isSuccess = MutableLiveData<Boolean>()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                showProgress.postValue(true)
                ordersRepository.deleteTender(id, storeId!!, { response->
                    showProgress.postValue(false)
                    isSuccess.postValue(response.status == Default.SUCCESS_API)
                    errorMsg.postValue(if(response.message != null) response.message else "")
                }, { t->
                    Log.e("Delete Tender", "onFailure: ", t)
                    showProgress.postValue(false)
                })
            } catch(ex : Exception) {
                Utils.printAndWriteException(ex)
                showProgress.postValue(false)
            }
        }
        return isSuccess
    } //endregion

    //region To Insert Tender
    fun insertTender(req : Tender) : MutableLiveData<Boolean> {
        val isSuccess = MutableLiveData<Boolean>()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                showProgress.postValue(true)
                req.storeId = storeId
                req.actionBy = actionBy
                ordersRepository.insertTender(req, { response->
                    showProgress.postValue(false)
                    isSuccess.postValue(response.status == Default.SUCCESS_API)
                    errorMsg.postValue(if(response.message != null) response.message else "")
                }, { t->
                    Log.e("Insert Tender", "onFailure: ", t)
                    showProgress.postValue(false)
                })
            } catch(ex : Exception) {
                Utils.printAndWriteException(ex)
                showProgress.postValue(false)
            }
        }
        return isSuccess
    } //endregion

    //region To fetch payment Mode Dropdown
    fun getPaymentModeDropDown() : MutableLiveData<List<CommonDropDown>> {
        val list = MutableLiveData<List<CommonDropDown>>()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                showProgress.postValue(true)
                ordersRepository.getPaymentModeDropDown({ response->
                    showProgress.postValue(false)
                    if(response.status == Default.SUCCESS_API) list.postValue(response.data)
                    else list.postValue(ArrayList())
                }, { t->
                    Log.e("Payment Mode Dropdown", "onFailure: ", t)
                    showProgress.postValue(false)
                })
            } catch(ex : Exception) {
                Utils.printAndWriteException(ex)
                showProgress.postValue(false)
            }
        }
        return list
    } //endregion

    //region To fetch card Type Dropdown
    fun getCardTypeDropDown() : MutableLiveData<List<CommonDropDown>> {
        val list = MutableLiveData<List<CommonDropDown>>()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                showProgress.postValue(true)
                ordersRepository.getCardTypeDropDown({ response->
                    showProgress.postValue(false)
                    if(response.status == Default.SUCCESS_API) list.postValue(response.data)
                    else list.postValue(ArrayList())
                }, { t->
                    Log.e("Card Type Dropdown", "onFailure: ", t)
                    showProgress.postValue(false)
                })
            } catch(ex : Exception) {
                Utils.printAndWriteException(ex)
                showProgress.postValue(false)
            }
        }
        return list
    } //endregion

    //region To fetch tender details
    fun getTenderDetails(tenderId : Int) : MutableLiveData<Tender?> {
        val tender = MutableLiveData<Tender?>()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                showProgress.postValue(true)
                ordersRepository.getTenderDetail(tenderId, { response->
                    showProgress.postValue(false)
                    if(response.status == Default.SUCCESS_API) tender.postValue(response.data)
                }, { t->
                    Log.e("Tender Detail", "onFailure: ", t)
                    showProgress.postValue(false)
                })
            } catch(ex : Exception) {
                Utils.printAndWriteException(ex)
                showProgress.postValue(false)
            }
        }
        return tender
    } //endregion

    //region To update Tender
    fun updateTender(req : Tender) : MutableLiveData<Boolean> {
        val isSuccess = MutableLiveData<Boolean>()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                showProgress.postValue(true)
                req.storeId = storeId!!
                req.actionBy = actionBy
                req.status = Default.ACTIVE
                ordersRepository.updateTender(req, { response->
                    showProgress.postValue(false)
                    isSuccess.postValue(response.status == Default.SUCCESS_API)
                    errorMsg.postValue(if(response.message != null) response.message else "")
                }, { t->
                    Log.e("Update Tender", "onFailure: ", t)
                    showProgress.postValue(false)
                })
            } catch(ex : Exception) {
                Utils.printAndWriteException(ex)
                showProgress.postValue(false)
            }
        }
        return isSuccess
    } //endregion

    //region To fetch card Type Dropdown
    fun getTenderDropDown() : MutableLiveData<List<CommonDropDown>> {
        val list = MutableLiveData<List<CommonDropDown>>()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                showProgress.postValue(true)
                ordersRepository.getTenderDropDown(storeId!!, { response->
                    showProgress.postValue(false)
                    if(response.status == Default.SUCCESS_API) list.postValue(response.data)
                    else list.postValue(ArrayList())
                }, { t->
                    Log.e("Tender Dropdown", "onFailure: ", t)
                    showProgress.postValue(false)
                })
            } catch(ex : Exception) {
                Utils.printAndWriteException(ex)
                showProgress.postValue(false)
            }
        }
        return list
    } //endregion

    //endregion

    //region Purchase Order
    //region To fetch Purchase Order
    fun getPurchaseOrderList(status : Int) : MutableLiveData<List<PurchaseOrder>> {
        val list = MutableLiveData<List<PurchaseOrder>>()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                ordersRepository.getPurchaseOrderList(storeId!!, status, { response->
                    if(response.status == Default.SUCCESS_API) list.postValue(response.data)
                    else list.postValue(ArrayList())
                }, { t->
                    Log.e("Purchase Order", "onFailure: ", t)
                })
            } catch(ex : Exception) {
                Utils.printAndWriteException(ex)
            }
        }
        return list
    } //endregion

    //region Purchase Order Delete
    fun deletePurchaseOrder(id : Int) : MutableLiveData<Boolean> {
        val isSuccess = MutableLiveData<Boolean>()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                showProgress.postValue(true)
                ordersRepository.deletePurchaseOrder(id, { response->
                    showProgress.postValue(false)
                    isSuccess.postValue(response.status == Default.SUCCESS_API)
                    errorMsg.postValue(if(response.message != null) response.message else "")
                }, { t->
                    Log.e("Delete Purchase Order", "onFailure: ", t)
                    showProgress.postValue(false)
                })
            } catch(ex : Exception) {
                Utils.printAndWriteException(ex)
                showProgress.postValue(false)
            }
        }
        return isSuccess
    } //endregion

    //region To fetch Auto Complete
    fun fetchAutoCompleteItems(msg : String) : MutableLiveData<List<FavouriteItems>> {
        val list = MutableLiveData<List<FavouriteItems>>()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                productsRepository.fetchAutoCompleteItems(msg, storeId!!, { response->
                    if(response.status == Default.SUCCESS_API) list.postValue(response.data)
                    else list.postValue(ArrayList())
                }, { t->
                    Log.e("Auto Complete", "onFailure: ", t)
                })
            } catch(ex : Exception) {
                Utils.printAndWriteException(ex)
                showProgress.postValue(false)
            }
        }
        return list
    } //endregion

    //region To Insert Purchase Order
    fun insertPurchaseOrder(req : PurchaseOrder) : MutableLiveData<Boolean> {
        val isSuccess = MutableLiveData<Boolean>()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                showProgress.postValue(true)
                req.storeId = storeId
                req.actionBy = actionBy
                req.status = Default.CONFIRM_ORDER
                ordersRepository.insertPurchaseOrder(req, { response->
                    showProgress.postValue(false)
                    isSuccess.postValue(response.status == Default.SUCCESS_API)
                    errorMsg.postValue(if(response.message != null) response.message else "")
                }, { t->
                    Log.e("Insert Purchase Order", "onFailure: ", t)
                    showProgress.postValue(false)
                })
            } catch(ex : Exception) {
                Utils.printAndWriteException(ex)
                showProgress.postValue(false)
            }
        }
        return isSuccess
    } //endregion

    //region To fetch Purchase Order details
    fun getPurchaseOrderDetail(id : Int) : MutableLiveData<PurchaseOrder?> {
        val purchaseList = MutableLiveData<PurchaseOrder?>()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                showProgress.postValue(true)
                ordersRepository.getPurchaseOrderDetail(id, { response->
                    showProgress.postValue(false)
                    if(response.status == Default.SUCCESS_API) purchaseList.postValue(response.data)
                }, { t->
                    Log.e("Purchase Order Detail", "onFailure: ", t)
                    showProgress.postValue(false)
                })
            } catch(ex : Exception) {
                Utils.printAndWriteException(ex)
                showProgress.postValue(false)
            }
        }
        return purchaseList
    } //endregion

    //region To update Purchase Order
    fun updatePurchaseOrder(req : PurchaseOrder) : MutableLiveData<Boolean> {
        val isSuccess = MutableLiveData<Boolean>()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                showProgress.postValue(true)
                req.storeId = storeId!!
                req.actionBy = actionBy
                ordersRepository.updatePurchaseOrder(req, { response->
                    showProgress.postValue(false)
                    isSuccess.postValue(response.status == Default.SUCCESS_API)
                    errorMsg.postValue(if(response.message != null) response.message else "")
                }, { t->
                    Log.e("Update Purchase Order", "onFailure: ", t)
                    showProgress.postValue(false)
                })
            } catch(ex : Exception) {
                Utils.printAndWriteException(ex)
                showProgress.postValue(false)
            }
        }
        return isSuccess
    } //endregion

    //region To Generate Invoice
    fun generateInvoice(orderId : Int) : MutableLiveData<Boolean> {
        val isSuccess = MutableLiveData<Boolean>()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                showProgress.postValue(true)
                ordersRepository.generateInvoice(orderId, { response->
                    showProgress.postValue(false)
                    isSuccess.postValue(response.status == Default.SUCCESS_API)
                    errorMsg.postValue(if(response.message != null) response.message else "")
                }, { t->
                    Log.e("Generate Invoice", "onFailure: ", t)
                    showProgress.postValue(false)
                })
            } catch(ex : Exception) {
                Utils.printAndWriteException(ex)
                showProgress.postValue(false)
            }
        }
        return isSuccess
    } //endregion


    //region To fetch Auto Complete Purchase Order
    fun fetchAutoCompletePurchaseOrder(search : String) : MutableLiveData<List<PurchaseOrder>> {
        val list = MutableLiveData<List<PurchaseOrder>>()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                ordersRepository.fetchAutoCompletePurchaseOrder(storeId!!, search, { response->
                    if(response.status == Default.SUCCESS_API) list.postValue(response.data)
                    else list.postValue(ArrayList())
                }, { t->
                    Log.e("Auto Complete Purchase Order", "onFailure: ", t)
                })
            } catch(ex : Exception) {
                Utils.printAndWriteException(ex)
                showProgress.postValue(false)
            }
        }
        return list
    } //endregion
    //endregion

    //region Invoice
    //region To fetch Invoice
    fun getInvoiceList() : MutableLiveData<List<Invoice>> {
        val list = MutableLiveData<List<Invoice>>()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                ordersRepository.getInvoiceList(storeId!!, { response->
                    if(response.status == Default.SUCCESS_API) list.postValue(response.data)
                    else list.postValue(ArrayList())
                }, { t->
                    Log.e("Invoice", "onFailure: ", t)
                })
            } catch(ex : Exception) {
                Utils.printAndWriteException(ex)
            }
        }
        return list
    } //endregion

    //region Invoice Delete
    fun deleteInvoice(id : Int) : MutableLiveData<Boolean> {
        val isSuccess = MutableLiveData<Boolean>()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                showProgress.postValue(true)
                ordersRepository.deleteInvoice(id, { response->
                    showProgress.postValue(false)
                    isSuccess.postValue(response.status == Default.SUCCESS_API)
                    errorMsg.postValue(if(response.message != null) response.message else "")
                }, { t->
                    Log.e("Delete Invoice", "onFailure: ", t)
                    showProgress.postValue(false)
                })
            } catch(ex : Exception) {
                Utils.printAndWriteException(ex)
                showProgress.postValue(false)
            }
        }
        return isSuccess
    } //endregion

    //region To Insert Invoice
    fun insertInvoice(req : Invoice) : MutableLiveData<Boolean> {
        val isSuccess = MutableLiveData<Boolean>()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                showProgress.postValue(true)
                req.storeId = storeId
                req.actionBy = actionBy
                req.status = Default.ORDER_PLACED
                req.payment.forEach { it.actionBy = actionBy }
                ordersRepository.insertInvoice(req, { response->
                    showProgress.postValue(false)
                    isSuccess.postValue(response.status == Default.SUCCESS_API)
                    errorMsg.postValue(if(response.message != null) response.message else "")
                }, { t->
                    Log.e("Insert Invoice", "onFailure: ", t)
                    showProgress.postValue(false)
                })
            } catch(ex : Exception) {
                Utils.printAndWriteException(ex)
                showProgress.postValue(false)
            }
        }
        return isSuccess
    } //endregion

    //region To fetch Invoice details
    fun getInvoiceDetail(id : Int) : MutableLiveData<Invoice?> {
        val purchaseOrderList = MutableLiveData<Invoice?>()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                showProgress.postValue(true)
                ordersRepository.getInvoiceDetail(id, { response->
                    showProgress.postValue(false)
                    if(response.status == Default.SUCCESS_API) purchaseOrderList.postValue(response.data)
                }, { t->
                    Log.e("Invoice Detail", "onFailure: ", t)
                    showProgress.postValue(false)
                })
            } catch(ex : Exception) {
                Utils.printAndWriteException(ex)
                showProgress.postValue(false)
            }
        }
        return purchaseOrderList
    } //endregion

    //region To update Invoice
    fun updateInvoice(req : Invoice) : MutableLiveData<Boolean> {
        val isSuccess = MutableLiveData<Boolean>()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                showProgress.postValue(true)
                req.storeId = storeId!!
                req.actionBy = actionBy
                req.payment.forEach { it.actionBy = actionBy }
                ordersRepository.updateInvoice(req, { response->
                    showProgress.postValue(false)
                    isSuccess.postValue(response.status == Default.SUCCESS_API)
                    errorMsg.postValue(if(response.message != null) response.message else "")
                }, { t->
                    Log.e("Update Invoice", "onFailure: ", t)
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

    //region Store
    //region To fetch Store
    fun getStoreList(status : Int) : MutableLiveData<List<Store>> {
        val list = MutableLiveData<List<Store>>()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                ordersRepository.getStoreList(status, { response->
                    if(response.status == Default.SUCCESS_API) list.postValue(response.data)
                    else {
                        list.postValue(ArrayList())
                        errorMsg.postValue(if(response.message != null) response.message else "")
                    }
                }, { t->
                    Log.e("Store", "onFailure: ", t)
                })
            } catch(ex : Exception) {
                Utils.printAndWriteException(ex)
            }
        }
        return list
    } //endregion

    //region To update store Status
    fun updateStoreStatus(req : StoreStatus) : MutableLiveData<Boolean> {
        val isSuccess = MutableLiveData<Boolean>()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                showProgress.postValue(true)
                req.actionBy = actionBy
                ordersRepository.updateStoreStatus(req, { response->
                    showProgress.postValue(false)
                    isSuccess.postValue(response.status == Default.SUCCESS_API)
                    errorMsg.postValue(if(response.message != null) response.message else "")
                }, { t->
                    Log.e("Update Store Status", "onFailure: ", t)
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
}