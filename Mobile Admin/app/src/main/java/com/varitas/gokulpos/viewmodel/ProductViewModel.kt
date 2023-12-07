package com.varitas.gokulpos.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.varitas.gokulpos.repositories.ProductsRepository
import com.varitas.gokulpos.request.ChangePriceRequest
import com.varitas.gokulpos.request.ChangeQtyRequest
import com.varitas.gokulpos.request.ItemPrice
import com.varitas.gokulpos.request.ItemPriceList
import com.varitas.gokulpos.request.ItemStock
import com.varitas.gokulpos.request.NotificationRequest
import com.varitas.gokulpos.request.ProductInsertRequest
import com.varitas.gokulpos.request.UpdateProductDetail
import com.varitas.gokulpos.response.CommonDropDown
import com.varitas.gokulpos.response.DropDown
import com.varitas.gokulpos.response.ItemStockSpecification
import com.varitas.gokulpos.response.Notifications
import com.varitas.gokulpos.response.ProductDetail
import com.varitas.gokulpos.response.ProductList
import com.varitas.gokulpos.response.ProductResponse
import com.varitas.gokulpos.response.ScanBarcode
import com.varitas.gokulpos.utilities.Default
import com.varitas.gokulpos.utilities.Enums
import com.varitas.gokulpos.utilities.Utils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel class ProductViewModel @Inject constructor(private val productsRepository : ProductsRepository) : ViewModel() {

    val showProgress = MutableLiveData<Boolean>()
    val errorMsg = MutableLiveData<String>()
    private val storeDetails = Utils.fetchLoginResponse()
    private val storeId = storeDetails.singleResult!!.storeId
    private val actionBy = storeDetails.singleResult!!.userId

    //region To fetch drop downs
    fun fetchDropDown(isDepartments : Boolean = false, isItemType : Boolean = false, isPack : Boolean = false, isSize : Boolean = false) : MutableLiveData<List<DropDown>> {
        val list = MutableLiveData<List<DropDown>>()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                showProgress.postValue(true)
                when {
                    isDepartments -> {
                        productsRepository.fetchDepartments(storeId!!, { response->
                            showProgress.postValue(false)
                            if(response.status == Default.SUCCESS_API) {
                                if(response.data != null)
                                    list.postValue(response.data!!.sortedBy { it.value })
                            } else list.postValue(ArrayList())
                        }, { t->
                            Log.e("Department", "onFailure: ", t)
                            showProgress.postValue(false)
                        })
                    }

                    isItemType -> {
                        productsRepository.fetchItemTypes(storeId!!, { response->
                            showProgress.postValue(false)
                            if(response.status == Default.SUCCESS_API) {
                                if(response.data != null)
                                    list.postValue(response.data!!.sortedBy { it.value })
                            } else list.postValue(ArrayList())
                        }, { t->
                            Log.e("Item Type", "onFailure: ", t)
                            showProgress.postValue(false)
                        })
                    }

                    isPack -> {
                        productsRepository.fetchAttributes(Enums.Attributes.PACK, { response->
                            showProgress.postValue(false)
                            if(response.status == Default.SUCCESS_API) {
                                if(response.data != null)
                                    list.postValue(response.data!!.sortedBy { it.value })
                            } else list.postValue(ArrayList())
                        }, { t->
                            Log.e("Pack", "onFailure: ", t)
                            showProgress.postValue(false)
                        })
                    }

                    isSize -> {
                        productsRepository.fetchAttributes(Enums.Attributes.SIZE, { response->
                            showProgress.postValue(false)
                            if(response.status == Default.SUCCESS_API) {
                                if(response.data != null)
                                    list.postValue(response.data!!.sortedBy { it.value })
                            } else list.postValue(ArrayList())
                        }, { t->
                            Log.e("Size", "onFailure: ", t)
                            showProgress.postValue(false)
                        })
                    }
                }
            } catch(ex : Exception) {
                Utils.printAndWriteException(ex)
                showProgress.postValue(false)
            }
        }
        return list
    } //endregion

    //region PRODUCTS

    //region To fetch products
    fun fetchProducts() : MutableLiveData<List<ProductList>> {
        val products = MutableLiveData<List<ProductList>>()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                productsRepository.fetchProducts(storeId!!, { response->
                    if(response.status == Default.SUCCESS_API) products.postValue(response.data)
                    else products.postValue(ArrayList())
                }, { t->
                    Log.e("Products", "onFailure: ", t)
                })
            } catch(ex : Exception) {
                Utils.printAndWriteException(ex)
            }
        }
        return products
    } //endregion

    //region To update price
    fun updatePrice(req : ChangePriceRequest) : MutableLiveData<Boolean> {
        val isSuccess = MutableLiveData<Boolean>()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                showProgress.postValue(true)
                req.storeId = storeId!!
                req.actionBy = actionBy!!
                productsRepository.updatePrice(req, { response->
                    showProgress.postValue(false)
                    if(response.status == Default.SUCCESS_API) isSuccess.postValue(true)
                    else isSuccess.postValue(false)
                }, { t->
                    Log.e("Price", "onFailure: ", t)
                    showProgress.postValue(false)
                })
            } catch(ex : Exception) {
                Utils.printAndWriteException(ex)
                showProgress.postValue(false)
            }
        }
        return isSuccess
    } //endregion

    //region To update quantity
    fun updateQuantity(req : ChangeQtyRequest) : MutableLiveData<Boolean> {
        val isSuccess = MutableLiveData<Boolean>()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                showProgress.postValue(true)
                req.storageId = storeId!!
                req.actionBy = actionBy!!
                req.stockStatus = 1
                productsRepository.updateQuantity(req, { response->
                    showProgress.postValue(false)
                    if(response.status == Default.SUCCESS_API) isSuccess.postValue(true)
                    else isSuccess.postValue(false)
                }, { t->
                    Log.e("Quantity", "onFailure: ", t)
                    showProgress.postValue(false)
                })
            } catch(ex : Exception) {
                Utils.printAndWriteException(ex)
                showProgress.postValue(false)
            }
        }
        return isSuccess
    } //endregion

    //region To insert/update product
    fun updateProductDetail(req : UpdateProductDetail) : MutableLiveData<Boolean> {
        val isSuccess = MutableLiveData<Boolean>()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                showProgress.postValue(true)
                req.storeId = storeId
                req.actionBy = actionBy
                req.addPriceMapModel!!.storeId = storeId!!
                req.addPriceMapModel!!.actionBy = actionBy!!
                req.barcodeMapModel.forEach {
                    it.storeId = storeId
                    it.actionBy = actionBy
                }
                productsRepository.updateProductDetails(req, { response->
                    showProgress.postValue(false)
                    if(response.status == Default.SUCCESS_API) isSuccess.postValue(true)
                    else isSuccess.postValue(false)
                }, { t->
                    Log.e("Product Detail", "onFailure: ", t)
                    showProgress.postValue(false)
                })
            } catch(ex : Exception) {
                Utils.printAndWriteException(ex)
                showProgress.postValue(false)
            }
        }
        return isSuccess
    } //endregion

    //region To get product details
    fun getProductDetails(id : Int) : MutableLiveData<ProductDetail?> {
        val details = MutableLiveData<ProductDetail?>()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                showProgress.postValue(true)
                productsRepository.fetchProductDetails(id, { response->
                    showProgress.postValue(false)
                    if(response.status == Default.SUCCESS_API) details.postValue(response.data)
                }, { t->
                    Log.e("Orders", "onFailure: ", t)
                    showProgress.postValue(false)
                })
            } catch(ex : Exception) {
                Utils.printAndWriteException(ex)
                showProgress.postValue(false)
            }
        }
        return details
    } //endregion

    //region To get barcode details
    fun getBarcodeDetails(upc : String) : MutableLiveData<ScanBarcode?> {
        val details = MutableLiveData<ScanBarcode?>()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                showProgress.postValue(true)
                productsRepository.fetchBarcodeDetails(upc, { response->
                    showProgress.postValue(false)
                    if(response.status == Default.SUCCESS_API) details.postValue(response.data)
                    else errorMsg.postValue(if(response.message != null) response.message else "")
                }, { t->
                    Log.e("Barcode", "onFailure: ", t)
                    showProgress.postValue(false)
                })
            } catch(ex : Exception) {
                Utils.printAndWriteException(ex)
                showProgress.postValue(false)
            }
        }
        return details
    } //endregion

    //region To insert Product
    fun insertProduct(req : ProductInsertRequest) : MutableLiveData<Boolean> {
        val isSuccess = MutableLiveData<Boolean>()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                showProgress.postValue(true)
                req.storeId = storeId!!
                req.actionBy = actionBy
                productsRepository.insertProduct(req, { response->
                    showProgress.postValue(false)
                    isSuccess.postValue(response.status == Default.SUCCESS_API)
                    errorMsg.postValue(if(response.message != null) response.message else "")
                }, { t->
                    Log.e("Insert Product", "onFailure: ", t)
                    showProgress.postValue(false)
                })
            } catch(ex : Exception) {
                Utils.printAndWriteException(ex)
                showProgress.postValue(false)
            }
        }
        return isSuccess
    } //endregion

    //region To get Item Price
    fun getItemPrice(itemId : Int) : MutableLiveData<ItemPrice> {
        val itemPrice = MutableLiveData<ItemPrice>()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                showProgress.postValue(true)
                productsRepository.getItemPrice(itemId, { response->
                    showProgress.postValue(false)
                    if(response.status == Default.SUCCESS_API) itemPrice.postValue(response.data)
                }, { t->
                    Log.e("Get Item Price", "onFailure: ", t)
                    showProgress.postValue(false)
                })
            } catch(ex : Exception) {
                Utils.printAndWriteException(ex)
                showProgress.postValue(false)
            }
        }
        return itemPrice
    } //endregion

    //region To update item price
    fun updateItemPrice(req : ItemPrice) : MutableLiveData<Boolean> {
        val isSuccess = MutableLiveData<Boolean>()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                showProgress.postValue(true)
                productsRepository.updateItemPrice(req, { response->
                    showProgress.postValue(false)
                    isSuccess.postValue(response.status == Default.SUCCESS_API)
                    //errorMsg.postValue(if(response.message != null) response.message else "")
                }, { t->
                    Log.e("Update Item Price", "onFailure: ", t)
                    showProgress.postValue(false)
                })
            } catch(ex : Exception) {
                Utils.printAndWriteException(ex)
                showProgress.postValue(false)
            }
        }
        return isSuccess
    } //endregion

    //region To get Item Qty
    fun getItemStock(itemId : Int) : MutableLiveData<List<ItemStockSpecification>> {
        val itemStock = MutableLiveData<List<ItemStockSpecification>>()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                showProgress.postValue(true)
                productsRepository.getItemStock(itemId, { response->
                    showProgress.postValue(false)
                    if(response.status == Default.SUCCESS_API) itemStock.postValue(response.data)
                }, { t->
                    Log.e("Get Item Stock", "onFailure: ", t)
                    showProgress.postValue(false)
                })
            } catch(ex : Exception) {
                Utils.printAndWriteException(ex)
                showProgress.postValue(false)
            }
        }
        return itemStock
    } //endregion

    //region To get Item Detail by Id
    fun getItemDetail(itemId : Int) : MutableLiveData<ProductResponse> {
        val itemDetail = MutableLiveData<ProductResponse>()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                showProgress.postValue(true)
                productsRepository.getItemDetails(itemId, { response->
                    showProgress.postValue(false)
                    if(response.status == Default.SUCCESS_API) itemDetail.postValue(response.data)
                    else errorMsg.postValue(response.message)
                }, { t->
                    Log.e("Get Item Detail", "onFailure: ", t)
                    showProgress.postValue(false)
                })
            } catch(ex : Exception) {
                Utils.printAndWriteException(ex)
                showProgress.postValue(false)
            }
        }
        return itemDetail
    } //endregion

    //region To update item qty
    fun updateItemStock(req : ArrayList<ItemStock>) : MutableLiveData<Boolean> {
        val isSuccess = MutableLiveData<Boolean>()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                showProgress.postValue(true)
                productsRepository.updateItemStock(req, { response->
                    showProgress.postValue(false)
                    isSuccess.postValue(response.status == Default.SUCCESS_API)
                    //errorMsg.postValue(if(response.message != null) response.message else "")
                }, { t->
                    Log.e("Update Item Stock", "onFailure: ", t)
                    showProgress.postValue(false)
                })
            } catch(ex : Exception) {
                Utils.printAndWriteException(ex)
                showProgress.postValue(false)
            }
        }
        return isSuccess
    } //endregion

    //region To update Product
    fun updateProduct(req : ProductInsertRequest) : MutableLiveData<Boolean> {
        val isSuccess = MutableLiveData<Boolean>()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                showProgress.postValue(true)
                req.storeId = storeId!!
                req.actionBy = actionBy
                productsRepository.updateProduct(req, { response->
                    showProgress.postValue(false)
                    isSuccess.postValue(response.status == Default.SUCCESS_API)
                    //errorMsg.postValue(if(response.message != null) response.message else "")
                }, { t->
                    Log.e("Update Product", "onFailure: ", t)
                    showProgress.postValue(false)
                })
            } catch(ex : Exception) {
                Utils.printAndWriteException(ex)
                showProgress.postValue(false)
            }
        }
        return isSuccess
    } //endregion

    //region To get Price List Qty
    fun getPriceList(itemId : Int) : MutableLiveData<List<ItemPriceList>> {
        val itemStock = MutableLiveData<List<ItemPriceList>>()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                showProgress.postValue(true)
                productsRepository.getPriceList(itemId, { response->
                    showProgress.postValue(false)
                    if(response.status == Default.SUCCESS_API) itemStock.postValue(response.data)
                }, { t->
                    Log.e("Get Item Price List", "onFailure: ", t)
                    showProgress.postValue(false)
                })
            } catch(ex : Exception) {
                Utils.printAndWriteException(ex)
                showProgress.postValue(false)
            }
        }
        return itemStock
    } //endregion


    //region To get size pack Dropdown
    fun getSizePackDropDown(typeId : Int) : MutableLiveData<ArrayList<CommonDropDown>> {
        val sizePack = MutableLiveData<ArrayList<CommonDropDown>>()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                showProgress.postValue(true)
                productsRepository.getSizePackDropDown(storeId!!, typeId, { response->
                    showProgress.postValue(false)
                    if(response.status == Default.SUCCESS_API) sizePack.postValue(response.data as ArrayList<CommonDropDown>)
                    else sizePack.postValue(ArrayList())
                }, { t->
                    Log.e("BRAND DROPDOWN", "onFailure: ", t)
                    showProgress.postValue(false)
                })
            } catch(ex : Exception) {
                Utils.printAndWriteException(ex)
                showProgress.postValue(false)
            }
        }
        return sizePack
    } //endregion

    //endregion

    //region To get notifications
    fun getNotifications() : MutableLiveData<List<Notifications>> {
        val notifications = MutableLiveData<List<Notifications>>()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                showProgress.postValue(true)
                productsRepository.fetchNotifications(storeId!!, { response->
                    showProgress.postValue(false)
                    if(response.status == Default.SUCCESS_API) notifications.postValue(response.data)
                    else errorMsg.postValue(if(response.message != null) response.message else "")
                }, { t->
                    Log.e("Notifications", "onFailure: ", t)
                    showProgress.postValue(false)
                })
            } catch(ex : Exception) {
                Utils.printAndWriteException(ex)
                showProgress.postValue(false)
            }
        }
        return notifications

    } //endregion

    //region To update notification
    fun updateNotifications(req : NotificationRequest) : MutableLiveData<Boolean> {
        val isSuccess = MutableLiveData<Boolean>()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                showProgress.postValue(true)
                req.empId = actionBy!!
                productsRepository.updateNotifications(req, { response->
                    showProgress.postValue(false)
                    if(response.status == Default.SUCCESS_API) isSuccess.postValue(true)
                    else isSuccess.postValue(false)
                }, { t->
                    Log.e("Notification", "onFailure: ", t)
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