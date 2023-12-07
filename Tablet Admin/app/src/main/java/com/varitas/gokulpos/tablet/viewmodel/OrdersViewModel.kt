package com.varitas.gokulpos.tablet.viewmodel

import android.text.TextUtils
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.varitas.gokulpos.tablet.R
import com.varitas.gokulpos.tablet.activity.AppContext.getString
import com.varitas.gokulpos.tablet.activity.OrderActivity
import com.varitas.gokulpos.tablet.model.Store
import com.varitas.gokulpos.tablet.model.StoreUpdate
import com.varitas.gokulpos.tablet.repositories.OrdersRepository
import com.varitas.gokulpos.tablet.repositories.ProductsRepository
import com.varitas.gokulpos.tablet.request.DrawerInsertRequest
import com.varitas.gokulpos.tablet.request.OrderInsertRequest
import com.varitas.gokulpos.tablet.request.UpdateOrderRequest
import com.varitas.gokulpos.tablet.response.AddItemToDiscount
import com.varitas.gokulpos.tablet.response.AllOrders
import com.varitas.gokulpos.tablet.response.CommonDropDown
import com.varitas.gokulpos.tablet.response.CompletedOrder
import com.varitas.gokulpos.tablet.response.Currency
import com.varitas.gokulpos.tablet.response.DataDetails
import com.varitas.gokulpos.tablet.response.DiscountMapList
import com.varitas.gokulpos.tablet.response.Discounts
import com.varitas.gokulpos.tablet.response.DrawerTransaction
import com.varitas.gokulpos.tablet.response.FavouriteItems
import com.varitas.gokulpos.tablet.response.FavouriteMenu
import com.varitas.gokulpos.tablet.response.Invoice
import com.varitas.gokulpos.tablet.response.ItemCartDetail
import com.varitas.gokulpos.tablet.response.ItemCount
import com.varitas.gokulpos.tablet.response.OpenBatch
import com.varitas.gokulpos.tablet.response.OpenBatchRequest
import com.varitas.gokulpos.tablet.response.OrderAnalytics
import com.varitas.gokulpos.tablet.response.OrderDetails
import com.varitas.gokulpos.tablet.response.OrderList
import com.varitas.gokulpos.tablet.response.OrderPlaced
import com.varitas.gokulpos.tablet.response.PurchaseOrder
import com.varitas.gokulpos.tablet.response.Role
import com.varitas.gokulpos.tablet.response.SalesPromotion
import com.varitas.gokulpos.tablet.response.Tender
import com.varitas.gokulpos.tablet.response.ValidatePromotion
import com.varitas.gokulpos.tablet.utilities.Constants
import com.varitas.gokulpos.tablet.utilities.Default
import com.varitas.gokulpos.tablet.utilities.Enums
import com.varitas.gokulpos.tablet.utilities.Utils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import me.moallemi.tools.extension.date.now
import me.moallemi.tools.extension.date.toDay
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel class OrdersViewModel @Inject constructor(private val ordersRepository : OrdersRepository, private val productsRepository : ProductsRepository) : ViewModel() {

    val showProgress : MutableLiveData<Boolean> = MutableLiveData()
    val amountList : MutableLiveData<ArrayList<Double>> = MutableLiveData()
    val errorMsg = MutableLiveData<String>()
    val storeDetails = Utils.fetchLoginResponse()
    val storeId = storeDetails.singleResult!!.storeId
    val actionBy = storeDetails.singleResult!!.userId
    var displaySubTotal : MutableLiveData<Double> = MutableLiveData()
    var subTotalAmount = 0.00
    var displayTotalTax : MutableLiveData<Double> = MutableLiveData()
    var taxAmount = 0.00
    var displaySavings : MutableLiveData<Double> = MutableLiveData()
    var displayQuantity : MutableLiveData<Int> = MutableLiveData()
    var displayFoodStamp : MutableLiveData<Double> = MutableLiveData()
    var displayNetPayable : MutableLiveData<Double> = MutableLiveData()
    var displayGrossTotal : MutableLiveData<Double> = MutableLiveData()
    var grossAmount = 0.00
    var displayChangeDue : MutableLiveData<Double> = MutableLiveData()
    var orderHold : MutableLiveData<Boolean> = MutableLiveData()
    var isShortcut : MutableLiveData<Boolean> = MutableLiveData()

    //region To fetch Order Analytics
    fun fetchAnalysis(startDate : String, endDate : String) : MutableLiveData<OrderAnalytics?> {
        val list = MutableLiveData<OrderAnalytics?>()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                showProgress.postValue(true)
                ordersRepository.fetchOrderAnalytics(storeId!!, startDate, endDate, { response->
                    showProgress.postValue(false)
                    if(response.status == Default.SUCCESS_API) list.postValue(response.data)
                }, { t->
                    Log.e("Orders", "onFailure: ", t)
                    showProgress.postValue(false)
                })
            } catch(ex : Exception) {
                Utils.printAndWriteException(ex)
                showProgress.postValue(false)
            }
        }
        return list
    } //endregion

    //region To get order details
    fun fetchOrderDetails(orderId : Int) : MutableLiveData<OrderDetails?> {
        val orderDetails = MutableLiveData<OrderDetails?>()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                showProgress.postValue(true)
                ordersRepository.fetchOrderDetails(orderId, { response->
                    showProgress.postValue(false)
                    if(response.status == Default.SUCCESS_API) orderDetails.postValue(response.data)
                }, { t->
                    Log.e("Orders", "onFailure: ", t)
                    showProgress.postValue(false)
                })
            } catch(ex : Exception) {
                Utils.printAndWriteException(ex)
                showProgress.postValue(false)
            }
        }
        return orderDetails
    } //endregion

    //region To fetch Auto Complete
    fun fetchAutoCompleteItems(msg : String) : MutableLiveData<List<FavouriteItems>> {
        val list = MutableLiveData<List<FavouriteItems>>()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                productsRepository.fetchAutoCompleteItems(msg, storeId!!, { response ->
                    if (response.status == Default.SUCCESS_API) list.postValue(response.data)
                    else list.postValue(ArrayList())
                }, { t ->
                    Log.e("Auto Complete", "onFailure: ", t)
                })
            } catch(ex : Exception) {
                Utils.printAndWriteException(ex)
                showProgress.postValue(false)
            }
        }
        return list
    } //endregion

    suspend fun resetTotals() {
        displaySubTotal.postValue(0.00)
        displayTotalTax.postValue(0.00)
        displaySavings.postValue(0.00)
        displayFoodStamp.postValue(0.00)
        displayNetPayable.postValue(0.00)
        displayGrossTotal.postValue(0.00)
        displayChangeDue.postValue(0.00)
        displayQuantity.postValue(0)
        orderHold.postValue(false)
        coroutineScope {
            val resultDeferred = async { manageAmounts() }
            resultDeferred.await()
        }
        OrderActivity.Instance.binding.autoCompleteTextView.requestFocus()
        OrderActivity.Instance.hideKeyBoard(OrderActivity.Instance)
        OrderActivity.Instance.orderDiscount = DiscountMapList(id = 0)
    }

    //region To fetch Auto Complete
    fun fetchCartItem(id : Int) : MutableLiveData<ItemCartDetail> {
        val list = MutableLiveData<ItemCartDetail>()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                productsRepository.fetchCartItemDetail(id, { response->
                    if(response.status == Default.SUCCESS_API) {
                        if(response.data != null) {
                            response.data!!.apply {
                                if(itemSpecification.size > 0) {
                                    price = itemSpecification[0].unitPrice!!
                                    originalPrice = itemSpecification[0].unitPrice!!
                                    taxRate = 0.00
                                    for(data in taxList) taxRate += data.rateValue!!
                                    tax = (price.times(taxRate)) / 100
                                    quantity = 1
                                    priceTotal = (price.times(quantity))
                                    taxTotal = (tax.times(quantity))
                                    specificationId = itemSpecification[0].id!!
                                }
                            }
                        }
                        list.postValue(response.data)
                    } else errorMsg.postValue(response.message)
                }, { t->
                    Log.e("Fetch Cart Item", "onFailure: ", t)
                })
            } catch(ex : Exception) {
                Utils.printAndWriteException(ex)
                showProgress.postValue(false)
            }
        }
        return list
    }
    //endregion

    //region Manage Cart
    suspend fun updateCart(due: Double = 0.00) = viewModelScope.async(Dispatchers.IO) {
        try {
            var calTax = 0.00
            var calSubTotal = 0.00
            var quantity = 0
            var ebt = 0.00
            var savings = 0.00
            val openDiscount = OrderActivity.Instance.orderDiscount

            OrderActivity.Instance.cartAdapter.apply {
                val ind = list.indexOfFirst { it.appliedOnOrder }
                if (ind >= 0) {
                    list.forEach {
                        it.discountId = 0
                        it.discountAmount = 0.00
                        it.appliedOnOrder = true
                        it.price = it.originalPrice
                        it.priceTotal = it.price.times(it.quantity)
                        it.tax = it.price.times(it.taxRate) / 100
                        it.taxTotal = it.tax.times(it.quantity)
                    }
                }
            }

            OrderActivity.Instance.cartAdapter.list.forEach { item ->
                if (!item.appliedOnOrder) {
                    if (item.nonDiscountable != null) {
                        if (!item.nonDiscountable!!) {
                            if (item.discounts.isNotEmpty()) {
                                val discount = item.discounts[0]
                                var isApplicable = false
                                if (discount.isLimitedTime!!) {
                                    if (!TextUtils.isEmpty(discount.startDate) && !TextUtils.isEmpty(discount.endDate)) {
                                        val today = Constants.dateFormatT.format(now())

                                        if (TextUtils.isEmpty(discount.startTime)) discount.startTime = "00:00:00"
                                        if (TextUtils.isEmpty(discount.endTime)) discount.endTime = "00:00:00"

                                        val firstDate = Constants.dateFormatT.parse(today)
                                        val startDate = Utils.convertStringToDate(formatter = Constants.dateFormatT, parseDate = discount.startDate + "T" + discount.startTime)
                                        val endDate = Utils.convertStringToDate(formatter = Constants.dateFormatT, parseDate = discount.endDate + "T" + discount.endTime)

                                        if (firstDate != null) {
                                            if (firstDate.time >= startDate.time && firstDate.time <= endDate.time) {
                                                val calendar = Calendar.getInstance()
                                                val currentDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
                                                val dayOfWeekNames = arrayOf("Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday")
                                                val currentDayName = dayOfWeekNames[currentDayOfWeek - 1]
                                                val index = discount.mWeekDaysList.indexOfFirst { it.name!!.lowercase() == currentDayName.lowercase() }
                                                isApplicable = index >= 0
                                            }
                                        }
                                    }
                                } else isApplicable = true

                                if (isApplicable) {
                                    item.discountId = discount.discountId!!
                                    item.discountAmount = discount.discountAmount!!
                                    if (discount.isBOGO!!) {
                                        val totalPrice: Double
                                        if (discount.itemGet!! > 0 && discount.itemBuy!! > 0) {
                                            val qty = discount.itemBuy!! + discount.itemGet!!
                                            var promoQty = ((item.quantity) * discount.itemGet!!) / discount.itemBuy!!

                                            val bogoQty = if (qty >= item.quantity) item.quantity
                                            else item.quantity - promoQty

                                            if (qty >= bogoQty) {
                                                promoQty = if (item.quantity >= qty) countBOGOQty(item.quantity, discount)
                                                else item.quantity - discount.itemBuy!!
                                                totalPrice = ((item.originalPrice * (item.quantity - promoQty))) / item.quantity
                                            } else totalPrice = ((item.originalPrice * ((item.quantity) - promoQty)) + (item.originalPrice)) / item.quantity
                                        } else totalPrice = item.originalPrice
                                        item.price = totalPrice
                                    } else {
                                        when (discount.discountType) {
                                            Default.PERCENTAGE -> item.price -= (item.price.times(discount.discountAmount!!)) / 100
                                            Default.EXACT -> item.price = discount.discountAmount!!
                                            Default.AMOUNT_OFF -> item.price -= discount.discountAmount!!
                                        }
                                    }
                                    item.priceTotal = (item.price.times(item.quantity))
                                    item.tax = (item.price.times(item.taxRate)) / 100
                                    item.taxTotal = (item.tax.times(item.quantity))
                                }
                            }
                        }
                    }
                } else {
                    if (openDiscount.id!! > 0) {
                        item.discountId = openDiscount.discountId!!
                        item.discountAmount = openDiscount.discountAmount!!
                    }
                }
                savings += (item.originalPrice - item.price) * item.quantity
                calTax += item.taxTotal
                quantity += item.quantity
                calSubTotal += item.priceTotal
                if (item.acceptFoodStamp!!) ebt += item.priceTotal
            }

            taxAmount = calTax
            subTotalAmount = calSubTotal
            grossAmount = calTax + subTotalAmount

            if (openDiscount.id!! > 0) {
                when (openDiscount.discountType) {
                    Default.PERCENTAGE -> {
                        savings = (grossAmount * openDiscount.discountAmount!!) / 100
                        OrderActivity.Instance.cartAdapter.apply {
                            list.forEach {
                                if (it.appliedOnOrder) it.discountAmount = savings
                            }
                        }
                        //savings = grossAmount.times(openDiscount.discountAmount!!).div(100)
                        grossAmount -= savings
                    }
                    Default.AMOUNT_OFF -> {
                        savings = openDiscount.discountAmount!!
                        grossAmount -= savings
                    }
                    else -> {}
                }
            }

            viewModelScope.launch(Dispatchers.Main) {
                OrderActivity.Instance.cartAdapter.notifyDataSetChanged()

                displaySubTotal.postValue(subTotalAmount)
                displaySavings.postValue(savings)
                displayTotalTax.postValue(taxAmount)
                displayGrossTotal.postValue(grossAmount)
                displayQuantity.postValue(quantity)
                displayFoodStamp.postValue(ebt)
                displayNetPayable.postValue(grossAmount)
                displayChangeDue.postValue(due)

                orderHold.postValue(grossAmount != 0.00)

                OrderActivity.Instance.binding.autoCompleteTextView.requestFocus()
            }

            coroutineScope {
                val resultDeferred = async { manageAmounts() }
                resultDeferred.await()
            }

        } catch(e : Exception) {
            Utils.printAndWriteException(e)
        }
        return@async
    }.await()

    private fun countBOGOQty(quantity : Int, discount : Discounts) : Int {
        val qty = quantity - (quantity % (discount.itemBuy!! + discount.itemGet!!))
        return if(qty > 0) if(quantity % qty == 0) quantity - (discount.itemBuy!! * (quantity / (discount.itemBuy!! + discount.itemGet!!)))
        else countLimitedQuantity(quantity, discount, qty) else discount.itemBuy!!
    }

    private fun countLimitedQuantity(quantity : Int, discount : Discounts, qty : Int) : Int {
        var promoQty = 0
        val bogoQty = quantity - (quantity % qty)
        if(bogoQty % qty == 0) promoQty = bogoQty - (discount.itemBuy!! * (bogoQty / (discount.itemBuy!! + discount.itemGet!!)))
        if(quantity > qty) {
            if((quantity - qty) - discount.itemBuy!! >= 0) promoQty += (quantity - qty) - discount.itemBuy!!
        }
        return promoQty
    }
    //endregion

    //region To fetch Auto Complete
    fun fetchSoldAlongCartItem(id : Int) : MutableLiveData<List<ItemCartDetail>> {
        val list = MutableLiveData<List<ItemCartDetail>>()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                productsRepository.fetchSoldAlongItemDetail(id, { response->
                    if(response.status == Default.SUCCESS_API) {
                        if(response.data != null) {
                            if(response.data!!.isNotEmpty()) {
                                for(data in response.data!!) {
                                    data.apply {
                                        if(itemSpecification.size > 0) {
                                            price = itemSpecification[0].unitPrice!!
                                            originalPrice = itemSpecification[0].unitPrice!!
                                            var taxRate = 0.00
                                            for(details in taxList) taxRate += details.rateValue!!
                                            tax = (price.times(taxRate)) / 100
                                            quantity = 1
                                            priceTotal = (price.times(quantity))
                                            taxTotal = (tax.times(quantity))
                                            specificationId = itemSpecification[0].id!!
                                        }
                                    }
                                }
                            }
                        }
                        list.postValue(response.data)
                    } else errorMsg.postValue(response.message)
                }, { t->
                    Log.e("Fetch Sold Along Cart Item", "onFailure: ", t)
                })
            } catch(ex : Exception) {
                Utils.printAndWriteException(ex)
                showProgress.postValue(false)
            }
        }
        return list
    }
    //endregion

    //region To open batch
    fun openBatch(req : OpenBatchRequest) : MutableLiveData<OpenBatch> {
        val data = MutableLiveData<OpenBatch>()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                showProgress.postValue(true)
                req.storeId = storeId!!
                req.userId = actionBy
                ordersRepository.openBatch(req, { response->
                    showProgress.postValue(false)
                    if(response.status == Default.SUCCESS_API) {
                        data.postValue(response.data)
                    } else data.postValue(null)
                }, { t->
                    Log.e("Open Batch", "onFailure: ", t)
                    showProgress.postValue(false)
                })
            } catch(ex : Exception) {
                Utils.printAndWriteException(ex)
                showProgress.postValue(false)
            }
        }
        return data
    } //endregion

    //region To fetch Favourite Items
    private suspend fun fetchFavouriteItems(deferred: CompletableDeferred<List<FavouriteItems>>) {
        try {
            resetTotals()
            showProgress.postValue(true)
            productsRepository.fetchFavouriteItems(storeId!!, { response ->
                showProgress.postValue(false)
                if (response.status == Default.SUCCESS_API) {
                    if (response.data != null)
                        deferred.complete(response.data!!)
                } else {
                    deferred.complete(emptyList())
                }
            }, { t ->
                Log.e("Favourite Items", "onFailure: ", t)
                showProgress.postValue(false)
                deferred.complete(emptyList())
            })
        } catch (ex: Exception) {
            Utils.printAndWriteException(ex)
            showProgress.postValue(false)
            deferred.complete(emptyList())
        }
    }

    private suspend fun fetchFavouriteGroupItems(deferred: CompletableDeferred<List<ItemCount>>, groupId: Int) {
        try {
            showProgress.postValue(true)
            productsRepository.fetchFavouriteGroupItems(groupId, { response ->
                showProgress.postValue(false)
                if (response.status == Default.SUCCESS_API) {
                    if (response.data != null)
                        deferred.complete(response.data!!)
                } else {
                    deferred.complete(emptyList())
                }
            }, { t ->
                Log.e("Favourite Items", "onFailure: ", t)
                showProgress.postValue(false)
                deferred.complete(emptyList())
            })
        } catch (ex: Exception) {
            Utils.printAndWriteException(ex)
            showProgress.postValue(false)
            deferred.complete(emptyList())
        }
    }

    private suspend fun fetchFavouriteMenuItems(deferred: CompletableDeferred<List<DataDetails>>, type: Int, id: Int) {
        try {
            showProgress.postValue(true)
            productsRepository.fetchFavouriteMenuItems(storeId!!, type, id, { response ->
                showProgress.postValue(false)
                if (response.status == Default.SUCCESS_API) {
                    if (response.data != null)
                        deferred.complete(response.data!!)
                } else {
                    deferred.complete(emptyList())
                }
            }, { t ->
                Log.e("Favourite Menu Items", "onFailure: ", t)
                showProgress.postValue(false)
                deferred.complete(emptyList())
            })
        } catch (ex: Exception) {
            Utils.printAndWriteException(ex)
            showProgress.postValue(false)
            deferred.complete(emptyList())
        }
    }
    //endregion

    //region To fetch Currency
    private suspend fun fetchCurrency(deferred: CompletableDeferred<List<Currency>>) {
        try {
            ordersRepository.fetchCurrency(storeId!!, { response ->
                if (response.status == Default.SUCCESS_API) {
                    if (response.data != null)
                        deferred.complete(response.data!!)
                } else {
                    deferred.complete(emptyList())
                }
            }, { t ->
                Log.e("Currency", "onFailure: ", t)
                deferred.complete(emptyList())
            })
        } catch (ex: Exception) {
            Utils.printAndWriteException(ex)
            deferred.complete(emptyList())
        }
    }
    //endregion

    //region To fetch shortcut items

    suspend fun fetchShortcutItems(type: Int, id: Int = 0): List<DataDetails> = coroutineScope {
        val itemsDeferred = CompletableDeferred<List<DataDetails>>()

        launch(Dispatchers.IO) {
            fetchFavouriteMenuItems(itemsDeferred, type, id)
        }

        return@coroutineScope itemsDeferred.await()
    }

    suspend fun fetchShortcutGroupItems(groupId: Int): List<ItemCount> = coroutineScope {
        val itemsDeferred = CompletableDeferred<List<ItemCount>>()

        launch(Dispatchers.IO) {
            fetchFavouriteGroupItems(itemsDeferred, groupId)
        }

        return@coroutineScope itemsDeferred.await()
    }
    //endregion

    //region To manage order
    fun manageOrder(req: OrderInsertRequest): MutableLiveData<OrderPlaced> {
        val data = MutableLiveData<OrderPlaced>()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                showProgress.postValue(true)
                req.storeId = storeId!!
                req.createdBy = actionBy!!
                ordersRepository.placeOrder(req, { response ->
                    showProgress.postValue(false)
                    if (response.status == Default.SUCCESS_API)
                        data.postValue(response.data)
                    else {
                        data.postValue(null)
                        errorMsg.postValue(if (response.message != null) response.message else "")
                    }
                }, { t ->
                    Log.e("Order Place", "onFailure: ", t)
                    showProgress.postValue(false)
                })
            } catch(ex : Exception) {
                Utils.printAndWriteException(ex)
                showProgress.postValue(false)
            }
        }
        return data
    } //endregion

    //region To update order
    fun updateOrder(req : UpdateOrderRequest) : MutableLiveData<OrderPlaced> {
        val data = MutableLiveData<OrderPlaced>()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                showProgress.postValue(true)
                req.storeId = storeId!!
                req.createdBy = actionBy!!
                ordersRepository.updateOrder(req, { response->
                    showProgress.postValue(false)
                    if(response.status == Default.SUCCESS_API)
                        data.postValue(response.data)
                    else {
                        data.postValue(null)
                        errorMsg.postValue(if(response.message != null) response.message else "")
                    }
                }, { t->
                    Log.e("Order Place Update", "onFailure: ", t)
                    showProgress.postValue(false)
                })
            } catch(ex : Exception) {
                Utils.printAndWriteException(ex)
                showProgress.postValue(false)
            }
        }
        return data
    } //endregion

    //region To manage amounts
    private suspend fun manageAmounts() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                if (grossAmount >= 0.00) {
                    ordersRepository.manageAmount(grossAmount, { response ->
                        if (response.size > 0) amountList.postValue(response)
                        else amountList.postValue(arrayListOf(grossAmount + 5.00, grossAmount + 10.00, grossAmount + 20.00, grossAmount + 50.00, grossAmount + 100.00))
                    }, { t ->
                        Log.e("Amounts", "onFailure: ", t)
                    })
                } else amountList.postValue(arrayListOf(grossAmount))

            } catch(ex : Exception) {
                Utils.printAndWriteException(ex)
            }
        }
    } //endregion

    //region To fetch Hold Order
    fun fetchHoldOrders() : MutableLiveData<List<OrderList>> {
        val list = MutableLiveData<List<OrderList>>()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val startDate = Constants.dateFormat_MM_dd_yyyy.format(now())
                val endDate = Constants.dateFormat_MM_dd_yyyy.format(1.toDay().sinceNow)
                ordersRepository.fetchHoldOrders(storeId!!, actionBy!!, startDate, endDate, { response->
                    if(response.status == Default.SUCCESS_API) list.postValue(response.data)
                    else list.postValue(ArrayList())
                }, { t->
                    Log.e("Hold", "onFailure: ", t)
                })
            } catch(ex : Exception) {
                Utils.printAndWriteException(ex)
            }
        }
        return list
    } //endregion

    //region To fetch Complete Order
    fun fetchCompleteOrders() : MutableLiveData<List<CompletedOrder>> {
        val list = MutableLiveData<List<CompletedOrder>>()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                showProgress.postValue(true)
                val startDate = Constants.dateFormat_MM_dd_yyyy.format(now())
                val endDate = Constants.dateFormat_MM_dd_yyyy.format(1.toDay().sinceNow)
                ordersRepository.fetchCompleteOrders(storeId!!, Default.COMPLETE_ORDER, startDate, endDate, { response->
                    showProgress.postValue(false)
                    if(response.status == Default.SUCCESS_API) list.postValue(response.data)
                    else list.postValue(ArrayList())
                }, { t->
                    Log.e("Hold", "onFailure: ", t)
                    showProgress.postValue(false)
                })
            } catch(ex : Exception) {
                Utils.printAndWriteException(ex)
                showProgress.postValue(false)
            }
        }
        return list
    } //endregion

    //region To save an order as a draft
    fun draftOrder(orderId : Int) : MutableLiveData<Boolean> {
        val data = MutableLiveData<Boolean>()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                showProgress.postValue(true)
                ordersRepository.saveDraft(orderId, { response->
                    showProgress.postValue(false)
                    data.postValue(response.status == Default.SUCCESS_API)
                    errorMsg.postValue(if(response.message != null) response.message else "")
                }, { t->
                    Log.e("Order Draft", "onFailure: ", t)
                    showProgress.postValue(false)
                })
            } catch(ex : Exception) {
                Utils.printAndWriteException(ex)
                showProgress.postValue(false)
            }
        }
        return data
    } //endregion

    //region To save an order as a draft
    fun deleteHoldOrder(orderId : Int) : MutableLiveData<Boolean> {
        val data = MutableLiveData<Boolean>()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                showProgress.postValue(true)
                ordersRepository.deleteHold(orderId, { response->
                    showProgress.postValue(false)
                    data.postValue(response.status == Default.SUCCESS_API)
                    errorMsg.postValue(if(response.message != null) response.message else "")
                }, { t->
                    Log.e("Order Delete", "onFailure: ", t)
                    showProgress.postValue(false)
                })
            } catch(ex : Exception) {
                Utils.printAndWriteException(ex)
                showProgress.postValue(false)
            }
        }
        return data
    } //endregion

    //region To close batch
    fun closeBatch(id : Int) : MutableLiveData<Boolean> {
        val data = MutableLiveData<Boolean>()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                showProgress.postValue(true)
                ordersRepository.closeBatch(id, { response->
                    showProgress.postValue(false)
                    data.postValue(response.status == Default.SUCCESS_API)
                    errorMsg.postValue(if(response.message != null) response.message else "")
                }, { t->
                    Log.e("Batch Close", "onFailure: ", t)
                    showProgress.postValue(false)
                })
            } catch(ex : Exception) {
                Utils.printAndWriteException(ex)
            }
        }
        return data
    } //endregion

    //region To open batch
    fun fetchDrawerTransaction() : MutableLiveData<List<DrawerTransaction>> {
        val data = MutableLiveData<List<DrawerTransaction>>()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val startDate = Constants.dateFormat_yyyy_MM_dd.format(now())
                val endDate = Constants.dateFormat_yyyy_MM_dd.format(1.toDay().sinceNow)
                ordersRepository.fetchDrawerTransactions(storeId!!, startDate, endDate, { response->
                    if(response.status == Default.SUCCESS_API) {
                        data.postValue(response.data)
                    } else errorMsg.postValue(if(response.message != null) response.message else "")
                }, { t->
                    Log.e("Drawer Transaction", "onFailure: ", t)
                })
            } catch(ex : Exception) {
                Utils.printAndWriteException(ex)
            }
        }
        return data
    } //endregion

    //region To insert Drawer
    fun insertDrawer(req : DrawerInsertRequest) : MutableLiveData<Boolean> {
        val isSuccess = MutableLiveData<Boolean>()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                showProgress.postValue(true)
                req.storeId = storeId
                req.actionBy = actionBy
                ordersRepository.insertDrawerTransaction(req, { response->
                    showProgress.postValue(false)
                    isSuccess.postValue(response.status == Default.SUCCESS_API)
                }, { t->
                    Log.e("Insert Drawer Transaction", "onFailure: ", t)
                    showProgress.postValue(false)
                })
            } catch(ex : Exception) {
                Utils.printAndWriteException(ex)
                showProgress.postValue(false)
            }
        }
        return isSuccess
    } //endregion

    //region To fetch Purchase Order
    fun fetchPurchaseOrders() : MutableLiveData<List<PurchaseOrder>> {
        val data = MutableLiveData<List<PurchaseOrder>>()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                ordersRepository.fetchPurchaseOrders(storeId!!, Default.All_DATA, { response->
                    if(response.status == Default.SUCCESS_API) {
                        data.postValue(response.data)
                    } else errorMsg.postValue(if(response.message != null) response.message else "")
                }, { t->
                    Log.e("Purchase Orders", "onFailure: ", t)
                })
            } catch(ex : Exception) {
                Utils.printAndWriteException(ex)
            }
        }
        return data
    } //endregion

    //region Tender

    //region To fetch Tender
    fun getTenderList() : MutableLiveData<List<Tender>> {
        val list = MutableLiveData<List<Tender>>()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                ordersRepository.getTenderList(storeId!!, { response->
                    if(response.status == Default.SUCCESS_API) list.postValue(response.data)
                    else errorMsg.postValue(if(response.message != null) response.message else "")
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

    //endregion

    //region To fetch sales promotion
    fun fetchSalesPromotion() : MutableLiveData<List<SalesPromotion>> {
        val data = MutableLiveData<List<SalesPromotion>>()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                showProgress.postValue(true)
                ordersRepository.fetchSalesPromotion(storeId!!, Default.ACTIVE, { response->
                    showProgress.postValue(false)
                    if(response.status == Default.SUCCESS_API) {
                        data.postValue(if(response.data != null) response.data else ArrayList())
                    } else data.postValue(ArrayList())
                    errorMsg.postValue(if(response.message != null) response.message else "")
                }, { t->
                    showProgress.postValue(false)
                    Log.e("Sales Promotion", "onFailure: ", t)
                })
            } catch(ex : Exception) {
                Utils.printAndWriteException(ex)
                showProgress.postValue(false)
            }
        }
        return data
    } //endregion

    //region delete promotion
    fun deletePromotion(id : Int) : MutableLiveData<Boolean> {
        val data = MutableLiveData<Boolean>()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                showProgress.postValue(true)
                ordersRepository.deletePromotion(id, { response->
                    showProgress.postValue(false)
                    data.postValue(response.status == Default.SUCCESS_API)
                    errorMsg.postValue(if(response.message != null) response.message else "")
                }, { t->
                    Log.e("Order Delete", "onFailure: ", t)
                    showProgress.postValue(false)
                })
            } catch(ex : Exception) {
                Utils.printAndWriteException(ex)
                showProgress.postValue(false)
            }
        }
        return data
    } //endregion

    //region delete purchase order
    fun deletePurchaseOrder(id : Int) : MutableLiveData<Boolean> {
        val data = MutableLiveData<Boolean>()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                showProgress.postValue(true)
                ordersRepository.deletePurchaseOrder(id, { response->
                    showProgress.postValue(false)
                    data.postValue(response.status == Default.SUCCESS_API)
                    errorMsg.postValue(if(response.message != null) response.message else "")
                }, { t->
                    Log.e("Order Delete", "onFailure: ", t)
                    showProgress.postValue(false)
                })
            } catch(ex : Exception) {
                Utils.printAndWriteException(ex)
                showProgress.postValue(false)
            }
        }
        return data
    } //endregion

    //region To fetch Promotion Type Dropdown
    fun getPromotionTypeDropDown() : MutableLiveData<List<CommonDropDown>> {
        val list = MutableLiveData<List<CommonDropDown>>()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                showProgress.postValue(true)
                ordersRepository.getPromotionTypeDropDown({ response->
                    showProgress.postValue(false)
                    if(response.status == Default.SUCCESS_API) list.postValue(response.data)
                    else list.postValue(ArrayList())
                }, { t->
                    Log.e("Promotion Type Dropdown", "onFailure: ", t)
                    showProgress.postValue(false)
                })
            } catch(ex : Exception) {
                Utils.printAndWriteException(ex)
                showProgress.postValue(false)
            }
        }
        return list
    } //endregion

    //region To fetch Common Group Dropdown
    fun getCommonGroupDropDown(type : Int) : MutableLiveData<List<CommonDropDown>> {
        val list = MutableLiveData<List<CommonDropDown>>()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                showProgress.postValue(true)
                ordersRepository.getCommonGroupDropDown(type, storeId!!, { response->
                    showProgress.postValue(false)
                    if(response.status == Default.SUCCESS_API) list.postValue(response.data)
                    else list.postValue(ArrayList())
                }, { t->
                    Log.e("Common Group Dropdown", "onFailure: ", t)
                    showProgress.postValue(false)
                })
            } catch(ex : Exception) {
                Utils.printAndWriteException(ex)
                showProgress.postValue(false)
            }
        }
        return list
    } //endregion

    //region To Insert Sales Promotion
    fun insertSalesPromotion(req : SalesPromotion) : MutableLiveData<Boolean> {
        val isSuccess = MutableLiveData<Boolean>()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                showProgress.postValue(true)
                req.storeId = storeId
                req.actionBy = actionBy
                ordersRepository.insertSalesPromotion(req, { response->
                    showProgress.postValue(false)
                    isSuccess.postValue(response.status == Default.SUCCESS_API)
                    errorMsg.postValue(if(response.message != null) response.message else "")
                }, { t->
                    Log.e("Insert Sales Promotion", "onFailure: ", t)
                    showProgress.postValue(false)
                })
            } catch(ex : Exception) {
                Utils.printAndWriteException(ex)
                showProgress.postValue(false)
            }
        }
        return isSuccess
    } //endregion

    //region To Insert Item to Promotion
    fun insertItemToPromotion(req : AddItemToDiscount) : MutableLiveData<Boolean> {
        val isSuccess = MutableLiveData<Boolean>()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                showProgress.postValue(true)
                ordersRepository.insertItemToPromotion(req, { response->
                    showProgress.postValue(false)
                    isSuccess.postValue(response.status == Default.SUCCESS_API)
                    errorMsg.postValue(if(response.message != null) response.message else "")
                }, { t->
                    Log.e("Insert Item to Promotion", "onFailure: ", t)
                    showProgress.postValue(false)
                })
            } catch(ex : Exception) {
                Utils.printAndWriteException(ex)
                showProgress.postValue(false)
            }
        }
        return isSuccess
    } //endregion

    //region To update Tender
    fun updateSalesPromotion(req : SalesPromotion) : MutableLiveData<Boolean> {
        val isSuccess = MutableLiveData<Boolean>()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                showProgress.postValue(true)
                req.actionBy = actionBy
                ordersRepository.updateSalesPromotion(req, { response->
                    showProgress.postValue(false)
                    isSuccess.postValue(response.status == Default.SUCCESS_API)
                    errorMsg.postValue(if(response.message != null) response.message else "")
                }, { t->
                    Log.e("Update Sales Promotion", "onFailure: ", t)
                    showProgress.postValue(false)
                })
            } catch(ex : Exception) {
                Utils.printAndWriteException(ex)
                showProgress.postValue(false)
            }
        }
        return isSuccess
    } //endregion

    //region To fetch tender details
    fun getSalesPromotionDetail(discountId : Int) : MutableLiveData<SalesPromotion?> {
        val discount = MutableLiveData<SalesPromotion?>()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                showProgress.postValue(true)
                ordersRepository.getSalesPromotionDetail(discountId, { response->
                    showProgress.postValue(false)
                    if(response.status == Default.SUCCESS_API) discount.postValue(response.data)
                    else errorMsg.postValue(if(response.message != null) response.message else "")
                }, { t->
                    Log.e("Sales Promotion Detail", "onFailure: ", t)
                    showProgress.postValue(false)
                })
            } catch(ex : Exception) {
                Utils.printAndWriteException(ex)
                showProgress.postValue(false)
            }
        }
        return discount
    } //endregion

    //region To fetch roles
    fun fetchRoles() : MutableLiveData<List<Role>> {
        val data = MutableLiveData<List<Role>>()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                ordersRepository.fetchRoles(storeId!!, { response->
                    if(response.status == Default.SUCCESS_API) {
                        data.postValue(response.data)
                    } else errorMsg.postValue(if(response.message != null) response.message else "")

                }, { t->
                    Log.e("Sales Promotion", "onFailure: ", t)
                })
            } catch(ex : Exception) {
                Utils.printAndWriteException(ex)
            }
        }
        return data
    } //endregion

    //region To Insert Role
    fun insertRole(req : Role) : MutableLiveData<Boolean> {
        val isSuccess = MutableLiveData<Boolean>()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                showProgress.postValue(true)
                req.storeId = storeId
                req.actionBy = actionBy
                ordersRepository.insertRole(req, { response->
                    showProgress.postValue(false)
                    isSuccess.postValue(response.status == Default.SUCCESS_API)
                    errorMsg.postValue(if(response.message != null) response.message else "")
                }, { t->
                    Log.e("Insert Role", "onFailure: ", t)
                    showProgress.postValue(false)
                })
            } catch(ex : Exception) {
                Utils.printAndWriteException(ex)
                showProgress.postValue(false)
            }
        }
        return isSuccess
    } //endregion

    //region To fetch tender details
    fun getRoleDetails(roleId : Int) : MutableLiveData<Role?> {
        val role = MutableLiveData<Role?>()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                showProgress.postValue(true)
                ordersRepository.getRoleDetail(roleId, { response->
                    showProgress.postValue(false)
                    if(response.status == Default.SUCCESS_API) role.postValue(response.data)
                }, { t->
                    Log.e("Role Detail", "onFailure: ", t)
                    showProgress.postValue(false)
                })
            } catch(ex : Exception) {
                Utils.printAndWriteException(ex)
                showProgress.postValue(false)
            }
        }
        return role
    } //endregion

    //region To update Tender
    fun updateRole(req : Role) : MutableLiveData<Boolean> {
        val isSuccess = MutableLiveData<Boolean>()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                showProgress.postValue(true)
                req.actionBy = actionBy
                ordersRepository.updateRole(req, { response->
                    showProgress.postValue(false)
                    isSuccess.postValue(response.status == Default.SUCCESS_API)
                    errorMsg.postValue(if(response.message != null) response.message else "")
                }, { t->
                    Log.e("Update Role", "onFailure: ", t)
                    showProgress.postValue(false)
                })
            } catch(ex : Exception) {
                Utils.printAndWriteException(ex)
                showProgress.postValue(false)
            }
        }
        return isSuccess
    } //endregion

    //region To validate promotion
    fun validatePromotion(req : ValidatePromotion) : MutableLiveData<Boolean> {
        val isSuccess = MutableLiveData<Boolean>()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                showProgress.postValue(true)
                req.storeId = storeId
                req.actionBy = actionBy
                ordersRepository.validatePromotion(req, { response->
                    showProgress.postValue(false)
                    isSuccess.postValue(response.status == Default.SUCCESS_API)
                    errorMsg.postValue(if(response.message != null) response.message else "")
                }, { t->
                    Log.e("Validate Promotion", "onFailure: ", t)
                    showProgress.postValue(false)
                })
            } catch(ex : Exception) {
                Utils.printAndWriteException(ex)
                showProgress.postValue(false)
            }
        }
        return isSuccess
    } //endregion

    //region To Insert Purchase Order
    fun insertPurchaseOrder(req : PurchaseOrder) : MutableLiveData<Boolean> {
        val isSuccess = MutableLiveData<Boolean>()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                showProgress.postValue(true)
                req.storeId = storeId
                req.actionBy = actionBy
                req.status = Default.COMPLETE_ORDER
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

    //region To all order by employee
    fun fetchAllOrderList(): MutableLiveData<List<AllOrders>> {
        val list = MutableLiveData<List<AllOrders>>()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                showProgress.postValue(true)
                val startDate = Constants.dateFormat_MM_dd_yyyy.format(now())
                val endDate = Constants.dateFormat_MM_dd_yyyy.format(1.toDay().sinceNow)
                ordersRepository.fetchAllOrderList(actionBy!!, storeId!!, startDate, endDate, { response->
                    showProgress.postValue(false)
                    if(response.status == Default.SUCCESS_API) {
                        list.postValue(response.data)
                    } else errorMsg.postValue(if (response.message != null) response.message else "")
                }, { t ->
                    Log.e("All Order", "onFailure: ", t)
                    showProgress.postValue(false)
                })
            } catch (ex: Exception) {
                Utils.printAndWriteException(ex)
                showProgress.postValue(false)
            }
        }
        return list
    } //endregion

    //region To last order by employee
    fun fetchLastOrder(): MutableLiveData<AllOrders> {
        val list = MutableLiveData<AllOrders>()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                showProgress.postValue(true)
                ordersRepository.fetchLastOrder(actionBy!!, { response ->
                    showProgress.postValue(false)
                    if (response.status == Default.SUCCESS_API) {
                        list.postValue(response.data)
                    } else errorMsg.postValue(if (response.message != null) response.message else "")
                }, { t ->
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

    //region To fetch Pending Stores
    fun getStoreList(status : Int) : MutableLiveData<List<Store>> {
        val list = MutableLiveData<List<Store>>()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                ordersRepository.getStoreList(status, { response->
                    if(response.status == Default.SUCCESS_API) list.postValue(response.data)
                    else errorMsg.postValue(if(response.message != null) response.message else "")
                }, { t->
                    Log.e("Tender", "onFailure: ", t)
                })
            } catch(ex : Exception) {
                Utils.printAndWriteException(ex)
            }
        }
        return list
    } //endregion

    //region To update Store Status
    fun updateStoreStatus(req : StoreUpdate) : MutableLiveData<Boolean> {
        val isSuccess = MutableLiveData<Boolean>()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                showProgress.postValue(true)
                req.actionBy = actionBy
                ordersRepository.updateStoreStatus(req, { response ->
                    showProgress.postValue(false)
                    isSuccess.postValue(response.status == Default.SUCCESS_API)
                    errorMsg.postValue(if (response.message != null) response.message else "")
                }, { t ->
                    Log.e("Update Store Status", "onFailure: ", t)
                    showProgress.postValue(false)
                })
            } catch (ex: Exception) {
                Utils.printAndWriteException(ex)
                showProgress.postValue(false)
            }
        }
        return isSuccess
    } //endregion

    //region To fetch sales promotion
    fun fetchOpenDiscount() : MutableLiveData<List<SalesPromotion>> {
        val data = MutableLiveData<List<SalesPromotion>>()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                showProgress.postValue(true)
                ordersRepository.fetchOpenDiscount(storeId!!, { response->
                    showProgress.postValue(false)
                    if(response.status == Default.SUCCESS_API) {
                        data.postValue(if(response.data != null) response.data else ArrayList())
                    } else data.postValue(ArrayList())
                    errorMsg.postValue(if(response.message != null) response.message else "")
                }, { t->
                    showProgress.postValue(false)
                    Log.e("Open Discount", "onFailure: ", t)
                })
            } catch(ex : Exception) {
                Utils.printAndWriteException(ex)
                showProgress.postValue(false)
            }
        }
        return data
    } //endregion

    val menus: MutableLiveData<List<FavouriteMenu>> = MutableLiveData()
    fun favouriteMenus() {
        val favMenuList = listOf(
            FavouriteMenu(getString(R.string.lbl_Items), Enums.Menus.ITEM, true),
            FavouriteMenu(getString(R.string.Menu_Brand), Enums.Menus.BRAND, false),
            FavouriteMenu(getString(R.string.lbl_Dept), Enums.Menus.DEPARTMENT, false),
            FavouriteMenu(getString(R.string.Menu_Category), Enums.Menus.CATEGORY, false),
            FavouriteMenu(getString(R.string.Menu_Group), Enums.Menus.GROUP, false)
                                )
        menus.value = favMenuList
    }
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

    //region Invoice
    //region To fetch Invoice
    fun getInvoiceList(status : Int) : MutableLiveData<List<Invoice>> {
        val list = MutableLiveData<List<Invoice>>()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                ordersRepository.getInvoiceList(storeId!!, status, { response->
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

    //region To fetch Auto Complete Purchase Order
    fun fetchAutoPurchaseOrderList(search : String) : MutableLiveData<List<PurchaseOrder>> {
        val list = MutableLiveData<List<PurchaseOrder>>()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                ordersRepository.getAutoPurchaseOrderList(storeId!!, search, { response->
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

    //region To Insert Invoice
    fun insertInvoice(req : Invoice) : MutableLiveData<Boolean> {
        val isSuccess = MutableLiveData<Boolean>()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                showProgress.postValue(true)
                req.storeId = storeId
                req.actionBy = actionBy
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
}