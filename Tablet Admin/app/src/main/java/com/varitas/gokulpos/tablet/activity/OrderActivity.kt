package com.varitas.gokulpos.tablet.activity

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.inputmethod.EditorInfo
import android.widget.LinearLayout
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.varitas.gokulpos.tablet.R
import com.varitas.gokulpos.tablet.adapter.AutoCompleteAdapter
import com.varitas.gokulpos.tablet.adapter.CartAdapter
import com.varitas.gokulpos.tablet.adapter.CurrencyAdapter
import com.varitas.gokulpos.tablet.adapter.FavouriteItemsAdapter
import com.varitas.gokulpos.tablet.adapter.FavouriteMenuAdapter
import com.varitas.gokulpos.tablet.databinding.ActivityOrderBinding
import com.varitas.gokulpos.tablet.fragmentDialogs.AgeCheckPopupDialog
import com.varitas.gokulpos.tablet.fragmentDialogs.CheckOutPopupDialog
import com.varitas.gokulpos.tablet.fragmentDialogs.CustomPaymentPopupDialog
import com.varitas.gokulpos.tablet.fragmentDialogs.CustomerPopupDialog
import com.varitas.gokulpos.tablet.fragmentDialogs.DeleteAlertPopupDialog
import com.varitas.gokulpos.tablet.fragmentDialogs.DeletePopupDialog
import com.varitas.gokulpos.tablet.fragmentDialogs.DuePopupDialog
import com.varitas.gokulpos.tablet.fragmentDialogs.HoldVoidPopupDialog
import com.varitas.gokulpos.tablet.fragmentDialogs.ItemAttributePopupDialog
import com.varitas.gokulpos.tablet.fragmentDialogs.ItemSpecificationPopupDialog
import com.varitas.gokulpos.tablet.fragmentDialogs.LogoutAlertPopupDialog
import com.varitas.gokulpos.tablet.fragmentDialogs.OpenBalancePopupDialog
import com.varitas.gokulpos.tablet.fragmentDialogs.OrderDiscountPopupDialog
import com.varitas.gokulpos.tablet.fragmentDialogs.PromptPopupDialog
import com.varitas.gokulpos.tablet.fragmentDialogs.QuantityPopupDialog
import com.varitas.gokulpos.tablet.fragmentDialogs.QuickAddPopupDialog
import com.varitas.gokulpos.tablet.fragmentDialogs.ResumePopupDialog
import com.varitas.gokulpos.tablet.fragmentDialogs.ReturnPopupDialog
import com.varitas.gokulpos.tablet.fragmentDialogs.SetupMenuPopupDialog
import com.varitas.gokulpos.tablet.fragmentDialogs.TaxGroupPopupDialog
import com.varitas.gokulpos.tablet.request.CustomerAge
import com.varitas.gokulpos.tablet.request.OrderDetailRequest
import com.varitas.gokulpos.tablet.request.OrderInsertRequest
import com.varitas.gokulpos.tablet.request.OrderPaymentRequest
import com.varitas.gokulpos.tablet.request.UpdateOrderRequest
import com.varitas.gokulpos.tablet.response.CompletedOrder
import com.varitas.gokulpos.tablet.response.DataDetails
import com.varitas.gokulpos.tablet.response.DiscountMapList
import com.varitas.gokulpos.tablet.response.FavouriteItems
import com.varitas.gokulpos.tablet.response.ItemCartDetail
import com.varitas.gokulpos.tablet.response.OrderItemDetails
import com.varitas.gokulpos.tablet.response.OrderList
import com.varitas.gokulpos.tablet.response.OrderPlaced
import com.varitas.gokulpos.tablet.response.PriceList
import com.varitas.gokulpos.tablet.response.SoldAlongData
import com.varitas.gokulpos.tablet.response.TaxList
import com.varitas.gokulpos.tablet.utilities.Constants
import com.varitas.gokulpos.tablet.utilities.Default
import com.varitas.gokulpos.tablet.utilities.Enums
import com.varitas.gokulpos.tablet.utilities.PlateData
import com.varitas.gokulpos.tablet.utilities.Preference
import com.varitas.gokulpos.tablet.utilities.PreferenceData
import com.varitas.gokulpos.tablet.utilities.SharedPreferencesKeys
import com.varitas.gokulpos.tablet.utilities.Utils
import com.varitas.gokulpos.tablet.viewmodel.CustomerViewModel
import com.varitas.gokulpos.tablet.viewmodel.LoginViewModel
import com.varitas.gokulpos.tablet.viewmodel.OrdersViewModel
import com.varitas.gokulpos.tablet.viewmodel.ProductFeatureViewModel
import com.varitas.gokulpos.tablet.viewmodel.ProductViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale
import kotlin.system.exitProcess

@AndroidEntryPoint class OrderActivity : BaseActivity() {

    lateinit var binding: ActivityOrderBinding
    val viewModel: OrdersViewModel by viewModels()
    val featureViewModel: ProductFeatureViewModel by viewModels()
    val productViewModel: ProductViewModel by viewModels()
    val viewModelCustomer: CustomerViewModel by viewModels()
    private val viewModelAuth: LoginViewModel by viewModels()
    private lateinit var favouriteItemsAdapter: FavouriteItemsAdapter
    private lateinit var currencyAdapter: CurrencyAdapter
    private lateinit var favouriteMenuAdapter: FavouriteMenuAdapter
    lateinit var cartAdapter: CartAdapter
    private lateinit var suggestionAdapter : AutoCompleteAdapter
    private lateinit var role: String
    private var batchId: Int = 0
    private var isButtonClicked = false
    private var customerId = 0
    private var parentId = 0
    lateinit var orderDiscount: DiscountMapList

    companion object {
        lateinit var Instance: OrderActivity
    }

    init {
        Instance = this
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        initData()
        postInitView()
        loadData()
        manageClicks()
    }

    //region To init data
    private fun initData() {
        role = if (viewModel.storeDetails.singleResult != null) {
            viewModel.storeDetails.singleResult!!.roleName!!
        } else ""
        viewModel.isShortcut.postValue(PreferenceData.getPrefBoolean(SharedPreferencesKeys.SHORTCUT, false))
        batchId = if (viewModel.storeDetails.singleResult != null) {
            viewModel.storeDetails.singleResult!!.batchId!!
        } else 0
        customerId = 0
        orderDiscount = DiscountMapList(id = 0)
        parentId = if (intent.extras?.getInt(Default.PARENT_ID) != null) intent.extras?.getInt(Default.PARENT_ID)!! else 0
        suggestionAdapter = AutoCompleteAdapter(this, android.R.layout.simple_spinner_dropdown_item, ArrayList())
        favouriteItemsAdapter = FavouriteItemsAdapter {
            when (it.isGroup) {
                true -> {
                    if (it.id!! > 0) setupMenuItems(it.type, it.id!!)
                    else setupMenuItems(it.type, 0)
                }
                false -> {
                    if (it.id!! > 0) fetchItem(it.id!!)
                    else setupMenuItems(it.type, 0)
                }
            }
        }
        currencyAdapter = CurrencyAdapter {
            placeOrder(paymentAmount = it)
        }
        cartAdapter = CartAdapter {
            manageQuantity(it.quantity, it)
        }

        favouriteMenuAdapter = FavouriteMenuAdapter {
            favouriteMenuAdapter.apply {
                list.forEach { detail ->
                    detail.isSelected = detail.type == it
                }
                notifyDataSetChanged()
            }

            when (it) {
                Enums.Menus.BRAND -> setupMenuItems(Default.MENU_BRAND)
                Enums.Menus.DEPARTMENT -> setupMenuItems(Default.MENU_DEPARTMENT)
                Enums.Menus.CATEGORY -> setupMenuItems(Default.MENU_CATEGORY)
                Enums.Menus.GROUP -> setupMenuItems(Default.MENU_GROUP)
                Enums.Menus.ITEM -> setupMenuItems(Default.MENU_ITEM)
                else -> {}
            }
        }
    }

    private fun postInitView() {
        binding.apply {
            textViewTA.text = String.format(Locale.getDefault(), Constants.StringFormat, "T/A: ", tobaccoDate(Constants.sdfDateFormatMMMddYY))
            textViewUser.text = String.format(Locale.getDefault(), Constants.StringFormat, "User: ", role)
            textViewBatchId.text = String.format(Locale.getDefault(), Constants.StringFormat, "Batch: ", if (batchId.toString().length > 1) batchId.toString() else "0${batchId}")
            recycleViewFavouriteItems.apply {
                adapter = favouriteItemsAdapter
                layoutManager = GridLayoutManager(this@OrderActivity, 4)
            }
            recycleViewCart.apply {
                adapter = cartAdapter
                layoutManager = LinearLayoutManager(this@OrderActivity)
            }

            recycleViewMenus.apply {
                adapter = favouriteMenuAdapter
                layoutManager = LinearLayoutManager(this@OrderActivity, LinearLayoutManager.HORIZONTAL, false)
            }

            recycleViewCurrency.apply {
                adapter = currencyAdapter
                layoutManager = LinearLayoutManager(this@OrderActivity, LinearLayoutManager.HORIZONTAL, false)
            }
            autoCompleteTextView.setAdapter(suggestionAdapter)
            autoCompleteTextView.threshold = 0
        }

    } //endregion

    //region To manage click events
    private fun manageClicks() {
        binding.apply {
            buttonDash.clickWithDebounce {
                cartAdapter.apply {
                    if (list.size == 0)
                        openActivity(Intent(this@OrderActivity, DashboardActivity::class.java))
                    else showSweetDialog(this@OrderActivity, resources.getString(R.string.lbl_CartIsNotEmpty))
                }
            }

            buttonProduct.clickWithDebounce {
                cartAdapter.apply {
                    if (list.size == 0)
                        moveToSubMenu(parentId, true)
                    else showSweetDialog(this@OrderActivity, resources.getString(R.string.lbl_CartIsNotEmpty))
                }
            }

            buttonSetUp.clickWithDebounce {
                manageMenus()
            }

            buttonDiscount.clickWithDebounce {
                cartAdapter.apply {
                    if (list.isNotEmpty()) manageDiscount()
                    else showSweetDialog(this@OrderActivity, resources.getString(R.string.lbl_CartIsEmpty))
                }
            }

            buttonReceipt.clickWithDebounce {
                cartAdapter.apply {
                    if (list.size == 0)
                        openActivity(Intent(this@OrderActivity, ReceiptActivity::class.java))
                    else showSweetDialog(this@OrderActivity, resources.getString(R.string.lbl_CartIsNotEmpty))
                }
            }

            buttonExit.clickWithDebounce {
                cartAdapter.apply {
                    if (list.size == 0) {
                        hideKeyBoard(this@OrderActivity)
                        val ft = supportFragmentManager.beginTransaction()
                        val dialogFragment = LogoutAlertPopupDialog(resources.getString(R.string.lbl_SureLogOut))
                        val prevFragment: Fragment? = supportFragmentManager.findFragmentByTag(DeleteAlertPopupDialog::class.java.name)
                        if (prevFragment != null) return@clickWithDebounce
                        dialogFragment.isCancelable = false
                        dialogFragment.show(ft, LogoutAlertPopupDialog::class.java.name)
                        dialogFragment.setListener(object : LogoutAlertPopupDialog.OnButtonClickListener {
                            override fun onButtonClickListener(typeButton: Enums.ClickEvents) {
                                when (typeButton) {
                                    Enums.ClickEvents.LOGOUT -> {
                                        viewModelAuth.doLogout().observe(this@OrderActivity) {
                                            CoroutineScope(Dispatchers.Main).launch {
                                                if (it!!.status == Default.SUCCESS_API) {
                                                    Preference.getPref(this@OrderActivity).edit().clear().apply()
                                                    openActivity(Intent(this@OrderActivity, LoginActivity::class.java))
                                                }
                                            }
                                        }
                                    }
                                    else -> {}
                                }
                            }
                        })
                    } else showSweetDialog(this@OrderActivity, resources.getString(R.string.lbl_CartIsNotEmpty))
                }
            }

            autoCompleteTextView.setOnItemClickListener { parent, _, position, _ ->
                val item = (parent.getItemAtPosition(position) as FavouriteItems)
                fetchItem(item.id!!)
            }

            autoCompleteTextView.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (!s.isNullOrEmpty()) fetchSuggestions(s.toString())
                }

                override fun afterTextChanged(s: Editable?) {}
            })

            buttonCustomer.clickWithDebounce {
                manageCustomer()
            }

            autoCompleteTextView.setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    val data = autoCompleteTextView.text.toString().trim()
                    if (!TextUtils.isEmpty(data)) {
                        if (data.length > 3 && suggestionAdapter.count == 0) {
                            autoCompleteTextView.text.clear()
                            manageQuickAddItem()
                        }
                    }
                    true
                } else false

            }

            buttonVoid.clickWithDebounce {
                cartAdapter.apply {
                    if (list.isNotEmpty()) manageHoldVoid(true)
                }
            }

            buttonHoldResume.clickWithDebounce {
                cartAdapter.apply {
                    if (list.isNotEmpty()) manageHoldVoid()
                    else manageResumeOrders()
                }
            }

            buttonCloseOut.clickWithDebounce {
                cartAdapter.apply {
                    if (list.isEmpty()) manageCloseBatch()
                    else showSweetDialog(this@OrderActivity, resources.getString(R.string.lbl_CartIsNotEmpty))
                }
            }

            buttonExact.clickWithDebounce {
                placeOrder(paymentAmount = buttonExact.text.toString().substring(1).toDouble())
            }

            buttonCustomCheckOut.clickWithDebounce{
                cartAdapter.apply {
                    if (list.isNotEmpty()) manageCustomPayment()
                }
            }

            buttonDelete.clickWithDebounce {
                cartAdapter.apply {
                    if(list.isNotEmpty()){
                        val data = list.filter { it.isSelected } as ArrayList<ItemCartDetail>
                        when(data.size){
                            0 -> showSweetDialog(this@OrderActivity, resources.getString(R.string.CartItemOperation, resources.getString(R.string.lbl_AtLeastOne)))
                            1 -> manageCartReduce()
                            else -> showSweetDialog(this@OrderActivity, resources.getString(R.string.CartItemOperation, resources.getString(R.string.lbl_OnlyOne)))
                        }
                    }
                }
            }

            buttonNext.clickWithDebounce {
                placeOrder(paymentAmount = buttonNext.text.toString().substring(1).toDouble())
            }

            buttonShortCut.clickWithDebounce {
                isButtonClicked = !isButtonClicked
                viewModel.isShortcut.postValue(isButtonClicked)
            }

            buttonTax.clickWithDebounce {
                cartAdapter.apply {
                    if (list.isNotEmpty()) {
                        val ind = list.indexOfFirst { it.isSelected }
                        if (ind >= 0) manageTaxGroups(ind)
                        else showSweetDialog(this@OrderActivity, resources.getString(R.string.CartItemOperation, resources.getString(R.string.lbl_OnlyOne)))
                    }
                }
            }

            buttonReturn.clickWithDebounce {
                manageReturnOrders()
            }

            textViewTA.clickWithDebounce {
                manageAgeCheck()
            }

            buttonCheckOut.clickWithDebounce {
                cartAdapter.apply {
                    if (list.isNotEmpty()) {
                        manageCheckOut()
                    } else showSweetDialog(this@OrderActivity, resources.getString(R.string.lbl_CartIsEmpty))
                }
            }
        }
    } //endregion

    @Deprecated("Deprecated in Java", ReplaceWith("logOut()")) override fun onBackPressed() {
        logOut()
    }

    private fun loadData() {
        viewModelAuth.showProgress.observe(this) {
            manageProgress(it)
        }



        viewModel.errorMsg.observe(this) {
            showSweetDialog(this@OrderActivity, it)
        }

        viewModel.showProgress.observe(this) {
            manageProgress(it)
        }

        binding.apply {
            viewModel.displaySubTotal.observe(this@OrderActivity) {
                textViewSubTotal.text = Utils.setAmountWithCurrency(this@OrderActivity, it)
            }

            viewModel.orderHold.observe(this@OrderActivity) {
                buttonHoldResume.text = resources.getText(if (it) R.string.button_Hold else R.string.button_Resume)
            }

            viewModel.displaySavings.observe(this@OrderActivity) {
                textViewAddOn.text = Utils.setAmountWithCurrency(this@OrderActivity, it)
            }

            viewModel.displayTotalTax.observe(this@OrderActivity) {
                textViewTotalTax.text = Utils.setAmountWithCurrency(this@OrderActivity, it)
            }

            viewModel.displayFoodStamp.observe(this@OrderActivity) {
                textViewFoodStamp.text = Utils.setAmountWithCurrency(this@OrderActivity, it)
            }

            viewModel.displayNetPayable.observe(this@OrderActivity) {
                textViewNetPayable.text = Utils.setAmountWithCurrency(this@OrderActivity, it)
            }

            viewModel.displayGrossTotal.observe(this@OrderActivity) {
                textViewGrossTotal.text = Utils.setAmountWithCurrency(this@OrderActivity, it)
                buttonExact.text = Utils.setAmountWithCurrency(this@OrderActivity, it)
                buttonNext.text = Utils.setAmountWithCurrency(this@OrderActivity, kotlin.math.ceil(it))
            }

            viewModel.displayChangeDue.observe(this@OrderActivity) {
                textViewDue.text = Utils.setAmountWithCurrency(this@OrderActivity, it)
            }

            viewModel.displayQuantity.observe(this@OrderActivity) {
                textViewQuantity.text = if (it.toString().length > 1) it.toString() else "0${it}"
            }

            viewModel.isShortcut.observe(this@OrderActivity) {
                val param = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, if (it) 1.0f else 0.6f)
                cardViewCart.layoutParams = param
                PreferenceData.putPrefBoolean(key = SharedPreferencesKeys.SHORTCUT, value = it)
                isButtonClicked = it
            }
        }

        viewModel.favouriteMenus()

        CoroutineScope(Dispatchers.IO).launch {
            viewModel.resetTotals()
        }

        viewModel.menus.observe(this) { favMenuList ->
            CoroutineScope(Dispatchers.Main).launch {
                favouriteMenuAdapter.apply {
                    clearData()
                    setList(favMenuList)
                }
                setupMenuItems(Default.MENU_ITEM)
            }
        }

        viewModel.amountList.observe(this) {
            CoroutineScope(Dispatchers.Main).launch {
                currencyAdapter.apply {
                    clearData()
                    setList(it)
                }
            }
        }

    }

    //region To fetch suggestions
    /**
     * @param str Inserted parameter to be searched
     * @author Safiyuddin Surti
     */
    private fun fetchSuggestions(str: String) {
        viewModel.fetchAutoCompleteItems(str).observe(this) {
            CoroutineScope(Dispatchers.Main).launch {
                suggestionAdapter.apply {
                    clear()
                    addAll(it)
                    notifyDataSetChanged()
                    binding.autoCompleteTextView.showDropDown()
                }
            }
        }
    }
    //endregion

    //region To manage customers
    private fun manageCustomer() {
        val ft = supportFragmentManager.beginTransaction()
        val dialogFragment = CustomerPopupDialog.newInstance()
        val prevFragment: Fragment? = supportFragmentManager.findFragmentByTag(CustomerPopupDialog::class.java.name)
        if (prevFragment != null) return
        dialogFragment.isCancelable = false
        dialogFragment.show(ft, CustomerPopupDialog::class.java.name)
        dialogFragment.setListener(object : CustomerPopupDialog.OnButtonClickListener {
            override fun onButtonClickListener(typeButton: Enums.ClickEvents, customerName: String, customerId: Int) {
                when (typeButton) {
                    Enums.ClickEvents.CANCEL -> {
                        if (dialogFragment.isVisible) dialogFragment.dismiss()
                    }
                    Enums.ClickEvents.VIEW -> {
                        if (dialogFragment.isVisible) dialogFragment.dismiss()
                        if (!TextUtils.isEmpty(customerName)) {
                            binding.textViewCustomerName.text = String.format(Locale.getDefault(), Constants.StringFormat, "Customer: ", customerName)
                        }
                        this@OrderActivity.customerId = customerId
                    }

                    else -> {}
                }
            }
        })
    }
    //endregion

    override fun onResume() {
        super.onResume()
        if (batchId == 0) openBatch()
    }

    //region To insert opening balance
    private fun openBatch() {
        val ft = supportFragmentManager.beginTransaction()
        val dialogFragment = OpenBalancePopupDialog.newInstance()
        val prevFragment: Fragment? = supportFragmentManager.findFragmentByTag(OpenBalancePopupDialog::class.java.name)
        if (prevFragment != null) return
        dialogFragment.isCancelable = false
        dialogFragment.show(ft, OpenBalancePopupDialog::class.java.name)
        dialogFragment.setListener(object : OpenBalancePopupDialog.OnButtonClickListener {
            override fun onButtonClickListener(typeButton: Enums.ClickEvents, id: Int) {
                when (typeButton) {
                    Enums.ClickEvents.SAVE -> {
                        if (dialogFragment.isVisible) dialogFragment.dismiss()
                        if (id > 0) {
                            batchId = id
                            binding.textViewBatchId.text = String.format(Locale.getDefault(), Constants.StringFormat, "Batch: ", if (batchId.toString().length > 1) batchId.toString() else "0${batchId}")
                            Utils.manageLoginResponse(id)
                        }
                    }
                    else -> {}
                }
            }
        })
    }
    //endregion

    //region To fetch cart item
    /**
     * @param id Item id
     * @author Safiyuddin Surti
     */
    private fun fetchItem(id: Int) {
        viewModel.fetchCartItem(id).observe(this) {
            CoroutineScope(Dispatchers.Main).launch {
                binding.autoCompleteTextView.setText("")
                hideKeyBoard(this@OrderActivity)
                when (it.type) {
                    Default.MULTI_PACK -> if (it.itemSpecification.size > 0) manageSpecification(it)
                    Default.LOT_MATRIX -> {
                        if (it.attributes.size > 0) manageLotMatrix(it)
                    }
                    else -> {
                        cartAdapter.apply {
                            if (list.size == 0) list.clear()
                            val ind = list.indexOfFirst { item -> item.sku!! == it.sku && item.id == it.id }
                            itemRingUp(ind, it)
                        }
                    }
                }
            }
        }
    }

    /**
     * @param cartDetail Detail of an available item in the cart
     * @author Safiyuddin Surti
     */
    private fun fetchSoldAlong(cartDetail: ItemCartDetail) {
        cartDetail.apply {
            if (itemSpecification.size > 0) {
                val list = ArrayList<SoldAlongData>()
                for (detail in itemSpecification) list.addAll(detail.soldAlong)
                if (list.size > 0) fetchSoldAlongItemDetails(cartDetail.id!!, list)
            }
        }
    }

    /**
     * @param primaryId Sold along Item id
     * @param soldAlongData Sold along Item list
     * @author Safiyuddin Surti
     */
    private fun fetchSoldAlongItemDetails(primaryId: Int, soldAlongData: ArrayList<SoldAlongData>) {
        viewModel.fetchSoldAlongCartItem(primaryId).observe(this) {
            if (it.isNotEmpty()) {
                for (details in it) {
                    cartAdapter.apply {
                        if (list.size == 0) list.clear()
                        val ind = list.indexOfFirst { item -> item.sku!! == details.sku && item.id == details.id && item.specificationId == details.specificationId }
                        val index = soldAlongData.indexOfFirst { along -> along.soldAlongItemId == details.id }
                        if (index >= 0) details.quantity = soldAlongData[index].soldAlongQty!!
                        itemRingUp(ind, details)
                    }
                }
            }
        }
    }
    //endregion

    //region To place an order
    /**
     * @param paymentAmount Total paid amount
     * @param isFromVoid Order from void
     * @param isFromHold Order from hold
     * @param remark Order remark
     * @param ageVerified Age verified or not
     * @param licenseData License Data
     * @param payment Multiple Payment List
     * @param discountId Order Discount Id
     * @param discountAmount Order Discount Amount
     * @author Safiyuddin Surti
     */
    private fun performOrder(paymentAmount: Double = 0.00, isFromVoid: Boolean = false, isFromHold: Boolean = false, remark: String = "", ageVerified: Boolean = false, licenseData: PlateData? = null, payment: ArrayList<OrderPaymentRequest> = ArrayList(), discountId: Int = 0, discountAmount: Double = 0.00) {
        val paymentStatus = paymentStatus(isFromVoid, isFromHold)
        val orderStatus = orderStatus(isFromVoid, isFromHold)

        val orderReq = OrderInsertRequest(
            customerId = customerId,
            paymentStatus = paymentStatus,
            status = orderStatus,
            tax = viewModel.taxAmount,
            totalIncTax = viewModel.grossAmount,
            subTotal = viewModel.subTotalAmount,
            remark = remark,
            discountAmount = discountAmount,
            discountId = discountId,
            couponCode = "",
            extraCharges = 0.00,
            isAgeVerificationRequired = ageVerified,
            orderDetailsApiModels = orderItemDetails(),
            customerAgeModel = if (ageVerified && licenseData != null && customerId == 0) orderCustomer(licenseData) else CustomerAge(),
            orderPaymentModel = orderPayment(paymentAmount, paymentStatus, payment))

        viewModel.manageOrder(orderReq).observe(this) {
            CoroutineScope(Dispatchers.Main).launch {
                if (it != null) {
                    cartAdapter.apply {
                        clearData()
                    }
                    if (!isFromVoid && !isFromHold) it.due = paymentAmount - viewModel.grossAmount
                    viewModel.updateCart(it.due)
                    orderDiscount = DiscountMapList(id = 0)
                    resetCustomer()
                    if (isFromHold) manageReceipt(it.orderId!!, isFromHold = true)
                    else if (isFromVoid) manageReceipt(it.orderId!!, isFromVoid = true)
                    else manageChangeDue(it)
                }
            }
        }
    }

    private fun orderItemDetails(): ArrayList<OrderDetailRequest> {
        val orderDetailsReq = ArrayList<OrderDetailRequest>()
        cartAdapter.apply {
            for (i in list.indices) {
                orderDetailsReq.add(
                    OrderDetailRequest(
                        itemId = list[i].id,
                        quantity = list[i].quantity,
                        unitPrice = list[i].price,
                        taxGroupId = list[i].taxGroupId,
                        itemSpecificationId = list[i].specificationId,
                        discountAmount = list[i].discountAmount,
                        discountId = list[i].discountId,
                        totalPrice = list[i].priceTotal,
                        returnOrderItemId = list[i].returnOrderItemId,
                        taxAmount = list[i].taxTotal)
                                   )
            }
        }
        return orderDetailsReq
    }

    /**
     * @param licenseData Customer License data for an order
     * @author Safiyuddin Surti
     */
    private fun orderCustomer(licenseData: PlateData): CustomerAge {
        return CustomerAge(
            customerName = licenseData.name,
            customerDateOfBirth = Constants.dateFormat_yyyy_MM_dd.format(Utils.convertStringToDate(formatter = Constants.sdfDateFormatMMMddYY, parseDate = licenseData.dobString)),
            customerLicense = licenseData.licenseNumber
                          )
    }

    /**
     * @param paymentAmount Total paid amount
     * @param paymentStatus Payment status
     * @param payment Multiple Payment List
     * @author Safiyuddin Surti
     */
    private fun orderPayment(paymentAmount: Double, paymentStatus: Int, payment: ArrayList<OrderPaymentRequest>): ArrayList<OrderPaymentRequest> {
        var paymentList = ArrayList<OrderPaymentRequest>()
        if (payment.isEmpty()) {
            paymentList.add(OrderPaymentRequest(
                orderId = 0,
                amount = paymentAmount,
                tenderType = Default.TENDER_CASH,
                paymentStatus = paymentStatus,
                details = "",
                storeId = viewModel.storeId,
                actionBy = viewModel.actionBy))
        } else {
            paymentList = ArrayList(payment)
            paymentList.forEach {
                it.paymentStatus = paymentStatus
                it.storeId = viewModel.storeId
                it.actionBy = viewModel.actionBy
            }
        }
        return paymentList
    }

    /**
     * @param isFromHold Is it from hold
     * @param isFromVoid Is it from void
     * @author Safiyuddin Surti
     */
    private fun paymentStatus(isFromVoid: Boolean, isFromHold: Boolean): Int {
        return when {
            isFromVoid || isFromHold -> Default.UNPAID
            else -> Default.PAID
        }
    }

    /**
     * @param isFromHold Is it from hold
     * @param isFromVoid Is it from void
     * @author Safiyuddin Surti
     */
    private fun orderStatus(isFromVoid: Boolean, isFromHold: Boolean): Int {
        return when {
            isFromVoid -> Default.VOID_ORDER
            isFromHold -> Default.HOLD_ORDER
            else -> Default.COMPLETE_ORDER
        }
    }

    /**
     * @param paymentAmount Total paid amount
     * @param isFromVoid Order from void
     * @param isFromHold Order from hold
     * @param ageVerified Age verified or not
     * @param licenseData License Data
     * @param payment Multiple Payment List
     * @param discountId Order Discount Id
     * @param discountAmount Order Discount Amount
     * @author Safiyuddin Surti
     */
    private fun performUpdateOrder(paymentAmount: Double = 0.00, isFromVoid: Boolean = false, isFromHold: Boolean = false, orderId: Int = 0, ageVerified: Boolean = false, licenseData: PlateData? = null, payment: ArrayList<OrderPaymentRequest> = ArrayList(), discountId: Int = 0, discountAmount: Double = 0.00) {
        val paymentStatus = paymentStatus(isFromVoid, isFromHold)
        val orderStatus = orderStatus(isFromVoid, isFromHold)

        val orderReq = UpdateOrderRequest(
            id = orderId,
            customerId = customerId,
            paymentStatus = paymentStatus,
            status = orderStatus,
            subTotal = viewModel.subTotalAmount,
            tax = viewModel.taxAmount,
            totalIncTax = viewModel.grossAmount,
            discountAmount = discountAmount,
            discountId = discountId,
            extraCharges = 0.00,
            isAgeVerificationRequired = ageVerified,
            orderDetailsApiModels = orderItemDetails(),
            customerAgeModel = if (ageVerified && licenseData != null && customerId == 0) orderCustomer(licenseData) else CustomerAge(),
            orderPaymentModel = orderPayment(paymentAmount, paymentStatus, payment))

        viewModel.updateOrder(orderReq).observe(this) {
            CoroutineScope(Dispatchers.Main).launch {
                if (it != null) {
                    cartAdapter.apply {
                        clearData()
                    }
                    if (!isFromVoid && !isFromHold) it.due = paymentAmount - viewModel.grossAmount
                    viewModel.updateCart(it.due)
                    orderDiscount = DiscountMapList(id = 0)
                    resetCustomer()

                    if (isFromHold)
                        manageReceipt(it.orderId!!, isFromHold = true)
                    else if (isFromVoid)
                        manageReceipt(it.orderId!!, isFromVoid = true)
                    else
                        manageChangeDue(it)
                }
            }
        }
    }

    /**
     * @param detail Placed Order details
     * @author Safiyuddin Surti
     */
    private fun manageChangeDue(detail: OrderPlaced) {
        val ft = supportFragmentManager.beginTransaction()
        val dialogFragment = DuePopupDialog.newInstance()
        val prevFragment: Fragment? = supportFragmentManager.findFragmentByTag(DuePopupDialog::class.java.name)
        if (prevFragment != null) return
        val bundle = Bundle()
        bundle.putParcelable(Default.ORDER, detail)
        dialogFragment.arguments = bundle
        dialogFragment.isCancelable = false
        dialogFragment.show(ft, DuePopupDialog::class.java.name)
        dialogFragment.setListener(object : DuePopupDialog.OnButtonClickListener {
            override fun onButtonClickListener(typeButton: Enums.ClickEvents) {
                if (dialogFragment.isVisible) dialogFragment.dismiss()
                manageReceipt(detail.orderId!!)
            }
        })
    }

    /**
     * @param isVoid Is from void
     * @author Safiyuddin Surti
     */
    private fun manageHoldVoid(isVoid: Boolean = false) {
        val ft = supportFragmentManager.beginTransaction()
        val dialogFragment = HoldVoidPopupDialog.newInstance()
        val prevFragment: Fragment? = supportFragmentManager.findFragmentByTag(HoldVoidPopupDialog::class.java.name)
        if (prevFragment != null) return
        val bundle = Bundle()
        bundle.putBoolean(Default.VOID, isVoid)
        dialogFragment.arguments = bundle
        dialogFragment.isCancelable = false
        dialogFragment.show(ft, HoldVoidPopupDialog::class.java.name)
        dialogFragment.setListener(object : HoldVoidPopupDialog.OnButtonClickListener {
            override fun onButtonClickListener(typeButton: Enums.ClickEvents, remark: String) {
                if (dialogFragment.isVisible) dialogFragment.dismiss()
                when (typeButton) {
                    Enums.ClickEvents.SAVE -> placeOrder(isFromVoid = isVoid, isFromHold = !isVoid, remark = remark)
                    else -> {}
                }
            }
        })
    }

    private fun manageCustomPayment() {
        val ft = supportFragmentManager.beginTransaction()
        val dialogFragment = CustomPaymentPopupDialog.newInstance()
        val prevFragment: Fragment? = supportFragmentManager.findFragmentByTag(CustomPaymentPopupDialog::class.java.name)
        if (prevFragment != null) return
        dialogFragment.isCancelable = false
        dialogFragment.show(ft, CustomPaymentPopupDialog::class.java.name)
        dialogFragment.setListener(object : CustomPaymentPopupDialog.OnButtonClickListener {
            override fun onButtonClickListener(typeButton: Enums.ClickEvents, price: Double) {
                if (dialogFragment.isVisible) dialogFragment.dismiss()
                when (typeButton) {
                    Enums.ClickEvents.SAVE -> placeOrder(paymentAmount = price)
                    else -> {}
                }
            }
        })
    }

    private fun manageCheckOut() {
        val ft = supportFragmentManager.beginTransaction()
        val dialogFragment = CheckOutPopupDialog.newInstance()
        val prevFragment: Fragment? = supportFragmentManager.findFragmentByTag(CheckOutPopupDialog::class.java.name)
        if (prevFragment != null) return
        dialogFragment.isCancelable = false
        dialogFragment.show(ft, CheckOutPopupDialog::class.java.name)
        dialogFragment.setListener(object : CheckOutPopupDialog.OnButtonClickListener {
            override fun onButtonClickListener(typeButton: Enums.ClickEvents, payment: ArrayList<OrderPaymentRequest>, price: Double) {
                if (dialogFragment.isVisible) dialogFragment.dismiss()
                when (typeButton) {
                    Enums.ClickEvents.SAVE -> placeOrder(paymentAmount = price, payment = payment)
                    else -> {}
                }
            }
        })
    }

    /**
     * @param paymentAmount Total paid amount
     * @param isFromHold Order from hold
     * @param isFromVoid Order from void
     * @param remark Order remark
     * @param payment Multiple payment list
     */
    private fun placeOrder(paymentAmount: Double = 0.00, isFromVoid: Boolean = false, isFromHold: Boolean = false, remark: String = "", payment: ArrayList<OrderPaymentRequest> = ArrayList()) {
        cartAdapter.apply {
            if (list.isNotEmpty()) {
                var discountId = 0
                var discountAmount = 0.00
                var index = list.indexOfFirst { it.appliedOnOrder }
                if (index >= 0) {
                    discountId = list[index].discountId
                    discountAmount = list[index].discountAmount
                    list.forEach {
                        it.discountId = 0
                        it.discountAmount = 0.00
                    }
                }

                index = list.indexOfFirst { if (it.isAgeVerification != null) it.isAgeVerification!! else false }
                if (index >= 0 && !isFromVoid && !isFromHold)
                    manageAgeCheck(paymentAmount = paymentAmount, ageVerified = true, payment = payment, discountId = discountId, discountAmount = discountAmount)
                else {
                    val ind = list.indexOfFirst { it.orderId > 0 }
                    if (ind >= 0) performUpdateOrder(paymentAmount = paymentAmount, orderId = list[ind].orderId, isFromVoid = isFromVoid, isFromHold = isFromHold, payment = payment, discountId = discountId, discountAmount = discountAmount)
                    else performOrder(paymentAmount = paymentAmount, isFromVoid = isFromVoid, isFromHold = isFromHold, remark = remark, payment = payment, discountId = discountId, discountAmount = discountAmount)
                }
            }
        }
    }
    //endregion

    //region Manage Quantity
    /**
     * @param qty Cart Item Quantity
     * @param cartDetail Cart Item detail
     * @param isPrompt Is for quantity prompt
     * @author Safiyuddin Surti
     */
    private fun manageQuantity(qty: Int, cartDetail: ItemCartDetail? = null, isPrompt: Boolean = false) {
        val ft = supportFragmentManager.beginTransaction()
        val dialogFragment = QuantityPopupDialog.newInstance()
        val prevFragment: Fragment? = supportFragmentManager.findFragmentByTag(QuantityPopupDialog::class.java.name)
        if (prevFragment != null) return
        val bundle = Bundle()
        bundle.putInt(Default.QUANTITY, qty)
        bundle.putBoolean(Default.PROMPT_QUANTITY, isPrompt)
        dialogFragment.arguments = bundle
        dialogFragment.isCancelable = false
        dialogFragment.show(ft, QuantityPopupDialog::class.java.name)
        dialogFragment.setListener(object : QuantityPopupDialog.OnButtonClickListener {
            override fun onButtonClickListener(typeButton: Enums.ClickEvents, qty: Int) {
                when (typeButton) {
                    Enums.ClickEvents.REPLACE -> {
                        cartAdapter.apply {
                            if (cartDetail != null) {
                                val ind = list.indexOfFirst { it.id == cartDetail.id && it.specificationId == cartDetail.specificationId }
                                if (ind >= 0) {
                                    if (getStockDetails(list[ind], qty)) manageItemQuantity(ind, qty)
                                    else showSweetDialog(this@OrderActivity, resources.getString(R.string.lbl_OutOfStock))
                                }
                            }
                        }
                    }
                    Enums.ClickEvents.UPDATE -> {
                        cartAdapter.apply {
                            if (cartDetail != null) {
                                val ind = list.indexOfFirst { it.id == cartDetail.id && it.specificationId == cartDetail.specificationId }
                                if (ind >= 0) {
                                    if (getStockDetails(list[ind], list[ind].quantity + qty)) manageItemQuantity(ind, list[ind].quantity + qty)
                                    else showSweetDialog(this@OrderActivity, resources.getString(R.string.lbl_OutOfStock))
                                }
                            }
                        }
                    }
                    Enums.ClickEvents.SAVE -> {
                        if(cartDetail != null){
                            CoroutineScope(Dispatchers.Main).launch {
                                cartDetail.apply {
                                    if (getStockDetails(this, qty)) {
                                        quantity = qty
                                        isSelected = false
                                        if (promptForPrice!!)
                                            managePrice(this)
                                        else {
                                            priceTotal = (price.times(quantity))
                                            taxTotal = (tax.times(quantity))
                                            cartAdapter.setList(this)
                                            if (isPrompt) fetchSoldAlong(this)
                                            viewModel.updateCart()
                                        }
                                    } else showSweetDialog(this@OrderActivity, resources.getString(R.string.lbl_OutOfStock))
                                }
                            }
                        }
                    }
                    else -> {}
                }
                if (dialogFragment.isVisible) dialogFragment.dismiss()
            }
        })
    }

    private fun manageCartReduce() {
        var quantity = 0
        var index = -1
        cartAdapter.apply {
            index = list.indexOfFirst { it.isSelected }
            if(index >= 0) quantity = list[index].quantity
        }

        val ft = supportFragmentManager.beginTransaction()
        val dialogFragment = DeletePopupDialog.newInstance()
        val prevFragment: Fragment? = supportFragmentManager.findFragmentByTag(DeletePopupDialog::class.java.name)
        if (prevFragment != null) return
        val bundle = Bundle()
        bundle.putInt(Default.QUANTITY, quantity)
        dialogFragment.arguments = bundle
        dialogFragment.isCancelable = false
        dialogFragment.show(ft, DeletePopupDialog::class.java.name)
        dialogFragment.setListener(object : DeletePopupDialog.OnButtonClickListener {
            override fun onButtonClickListener(typeButton: Enums.ClickEvents, qty: Int) {
                if (dialogFragment.isVisible) dialogFragment.dismiss()
                when (typeButton) {
                    Enums.ClickEvents.REDUCE -> manageItemQuantity(index, quantity - qty)
                    Enums.ClickEvents.DELETE -> {
                        cartAdapter.apply {
                            if(index >= 0){
                                list.removeAt(index)
                                notifyItemRemoved(index)
                                CoroutineScope(Dispatchers.IO).launch {
                                    viewModel.updateCart()
                                    if (list.size == 0) viewModel.resetTotals()
                                }
                                if (list.size == 0) resetCustomer()
                            }
                        }
                    }
                    else -> {}
                }
            }
        })
    }

    /**
     * @param ind Cart item index
     * @param qty Cart item quantity
     * @author Safiyuddin Surti
     */
    private fun manageItemQuantity(ind: Int, qty: Int) {
        cartAdapter.apply {
            list[ind].quantity = qty
            list[ind].isSelected = false
            list[ind].priceTotal = (list[ind].price.times(list[ind].quantity))
            list[ind].taxTotal = (list[ind].tax.times(list[ind].quantity))
            notifyItemChanged(ind)
        }
        CoroutineScope(Dispatchers.IO).launch {
            viewModel.updateCart()
        }
    }
    //endregion

    //region To manage price
    /**
     * @param cartDetail Cart item detail
     * @author Safiyuddin Surti
     */
    private fun managePrice(cartDetail: ItemCartDetail){
        val ft = supportFragmentManager.beginTransaction()
        val dialogFragment = PromptPopupDialog.newInstance()
        val prevFragment: Fragment? = supportFragmentManager.findFragmentByTag(PromptPopupDialog::class.java.name)
        if (prevFragment != null) return
        dialogFragment.isCancelable = false
        dialogFragment.show(ft, PromptPopupDialog::class.java.name)
        dialogFragment.setListener(object : PromptPopupDialog.OnButtonClickListener {
            override fun onButtonClickListener(typeButton: Enums.ClickEvents, amount: Double) {
                if (dialogFragment.isVisible) dialogFragment.dismiss()
                when (typeButton) {
                    Enums.ClickEvents.SAVE -> {
                        cartDetail.apply {
                            featureViewModel.getGroupDetails(groupId = taxGroupId!!, itemId = id!!, specification = specificationId, price = amount).observe(this@OrderActivity) {
                                CoroutineScope(Dispatchers.Main).launch {
                                    if (it != null) {
                                        taxList.clear()
                                        taxRate = 0.00
                                        for (tax in it.tax) {
                                            taxList.add(TaxList(tax.id, tax.taxName, tax.taxRate))
                                            taxRate += tax.taxRate!!
                                        }
                                        price = amount
                                        originalPrice = amount
                                        priceTotal = (price.times(quantity))
                                        tax = (price.times(taxRate)) / 100
                                        taxTotal = (tax.times(quantity))
                                       // itemTax = if (tax > 0.00) "Y" else "N"
                                        cartAdapter.setList(this@apply)
                                        fetchSoldAlong(this@apply)
                                        CoroutineScope(Dispatchers.IO).launch {
                                            viewModel.updateCart()
                                        }
                                    }
                                }
                            }
                        }
                    }
                    else -> {}
                }
            }
        })
    }
    //endregion

    //region To manage hold orders
    private fun manageResumeOrders(){
        val ft = supportFragmentManager.beginTransaction()
        val dialogFragment = ResumePopupDialog.newInstance()
        val prevFragment: Fragment? = supportFragmentManager.findFragmentByTag(ResumePopupDialog::class.java.name)
        if (prevFragment != null) return
        dialogFragment.isCancelable = false
        dialogFragment.show(ft, ResumePopupDialog::class.java.name)
        dialogFragment.setListener(object : ResumePopupDialog.OnButtonClickListener {
            override fun onButtonClickListener(typeButton: Enums.ClickEvents, order: OrderList?) {
                when (typeButton) {
                    Enums.ClickEvents.LOAD -> {
                        if (order != null) {
                            if (order.orderDetailsApiModels.size > 0)
                                setupCartDetails(orderDetails = order.orderDetailsApiModels, orderId = order.id!!, customerName = if (!TextUtils.isEmpty(order.customerName)) order.customerName!! else "", id = order.customerId!!)
                        }
                        if (dialogFragment.isVisible) dialogFragment.dismiss()
                    }
                    Enums.ClickEvents.PRINT -> manageReceipt(order!!.id!!, true)
                    else -> if (dialogFragment.isVisible) dialogFragment.dismiss()
                }
            }
        })
    }
    //endregion

    //region To manage close batch
    private fun manageCloseBatch() {
        val ft = supportFragmentManager.beginTransaction()
        val dialogFragment = DeleteAlertPopupDialog(resources.getString(R.string.lbl_CloseBatch), batchId, isBatchClose = true)
        val prevFragment: Fragment? = supportFragmentManager.findFragmentByTag(DeleteAlertPopupDialog::class.java.name)
        if (prevFragment != null) return
        dialogFragment.isCancelable = false
        dialogFragment.show(ft, DeleteAlertPopupDialog::class.java.name)
        dialogFragment.setListener(object : DeleteAlertPopupDialog.OnButtonClickListener {
            override fun onButtonClickListener(typeButton: Enums.ClickEvents) {
                if (dialogFragment.isVisible) dialogFragment.dismiss()
                when (typeButton) {
                    Enums.ClickEvents.CLOSE -> {
                        batchId = 0
                        binding.textViewBatchId.text = String.format(Locale.getDefault(), Constants.StringFormat, "Batch: ", if (batchId.toString().length > 1) batchId.toString() else "0${batchId}")
                        finishAffinity()
                        exitProcess(0)

                    }
                    else -> {}
                }
            }
        })
    }
    //endregion

    //region To manage tax group
    /**
     * @param ind Index
     * @author Safiyuddin Surti
     */
    private fun manageTaxGroups(ind: Int) {
        val ft = supportFragmentManager.beginTransaction()
        val dialogFragment = TaxGroupPopupDialog.newInstance()
        val prevFragment: Fragment? = supportFragmentManager.findFragmentByTag(TaxGroupPopupDialog::class.java.name)
        if (prevFragment != null) return
        val bundle = Bundle()
        cartAdapter.apply {
            bundle.putInt(Default.ID, if (list[ind].taxGroupId != null) list[ind].taxGroupId!! else 0)
            bundle.putInt(Default.PRODUCT_ID, list[ind].id!!)
            bundle.putDouble(Default.PRICE, list[ind].price)
            bundle.putInt(Default.SPECIFICATION, list[ind].specificationId)
            dialogFragment.arguments = bundle
        }
        dialogFragment.isCancelable = false
        dialogFragment.show(ft, TaxGroupPopupDialog::class.java.name)
        dialogFragment.setListener(object : TaxGroupPopupDialog.OnButtonClickListener {
            override fun onButtonClickListener(typeButton: Enums.ClickEvents, id: Int, taxDetails: ArrayList<TaxList>) {
                if (dialogFragment.isVisible) dialogFragment.dismiss()
                when (typeButton) {
                    Enums.ClickEvents.SAVE -> {
                        cartAdapter.apply {
                            if (list[ind].taxGroupId != id) {
                                list[ind].apply {
                                    taxList.clear()
                                    taxList.addAll(taxDetails)
                                    taxGroupId = id
                                    taxRate = 0.00
                                    for (data in taxList) taxRate += data.rateValue!!
                                    tax = (price.times(taxRate)) / 100
                                    taxTotal = (tax.times(quantity))
                                    isSelected = false
                                    itemTax = "Y"
                                }

                                CoroutineScope(Dispatchers.IO).launch {
                                    viewModel.updateCart()
                                }

                            } else {
                                list[ind].apply {
                                    isSelected = false
                                }
                            }
                            notifyItemChanged(ind)
                        }
                    }
                    Enums.ClickEvents.REMOVE -> {
                        cartAdapter.apply {
                            if (list[ind].taxGroupId != id) {
                                list[ind].apply {
                                    taxList.clear()
                                    taxRate = 0.00
                                    taxGroupId = id
                                    for (data in taxList) taxRate += data.rateValue!!
                                    tax = (price.times(taxRate)) / 100
                                    taxTotal = (tax.times(quantity))
                                    isSelected = false
                                    itemTax = "N"
                                }

                                CoroutineScope(Dispatchers.IO).launch {
                                    viewModel.updateCart()
                                }

                            } else {
                                list[ind].apply {
                                    isSelected = false
                                }
                            }
                            notifyItemChanged(ind)
                        }
                    }
                    else -> {}
                }
            }
        })
    }
    //endregion

    //region To manage hold orders
    private fun manageReturnOrders() {
        val ft = supportFragmentManager.beginTransaction()
        val dialogFragment = ReturnPopupDialog.newInstance()
        val prevFragment: Fragment? = supportFragmentManager.findFragmentByTag(ReturnPopupDialog::class.java.name)
        if (prevFragment != null) return
        dialogFragment.isCancelable = false
        dialogFragment.show(ft, ReturnPopupDialog::class.java.name)
        dialogFragment.setListener(object : ReturnPopupDialog.OnButtonClickListener {
            override fun onButtonClickListener(typeButton: Enums.ClickEvents, order: CompletedOrder?) {
                when (typeButton) {
                    Enums.ClickEvents.LOAD -> {
                        if (order != null) {
                            if (order.orderDetailsApiModels.size > 0)
                                setupCartDetails(orderDetails = order.orderDetailsApiModels, orderId = 0, customerName = if (!TextUtils.isEmpty(order.customerName)) order.customerName!! else "", id = order.customerId!!, isFromReturn = true)
                        }
                        if (dialogFragment.isVisible) dialogFragment.dismiss()
                    }
                    Enums.ClickEvents.PRINT -> manageReceipt(order!!.id!!)
                    else -> if (dialogFragment.isVisible) dialogFragment.dismiss()
                }
            }
        })
    }
    //endregion

    //region To reset customer
    private fun resetCustomer() {
        customerId = 0
        binding.textViewCustomerName.text = String.format(Locale.getDefault(), Constants.StringFormat, "Customer: ", "")
    }
    //endregion

    //region To setup Cart Details
    /**
     * @param orderDetails Order Details to resume/return
     * @param orderId Order id
     * @param customerName Order customer name
     * @param id Order Customer id
     * @param isFromReturn Order from return
     * @author Safiyuddin Surti
     */
    private fun setupCartDetails(orderDetails: ArrayList<OrderItemDetails>, orderId: Int, customerName: String, id: Int, isFromReturn: Boolean = false) {
        binding.textViewCustomerName.text = String.format(Locale.getDefault(), Constants.StringFormat, "Customer: ", customerName)
        this@OrderActivity.customerId = id
        cartAdapter.apply {
            val details = ArrayList<ItemCartDetail>()
            for (data in orderDetails) {
                val spec = ArrayList<PriceList>()
                if (data.specification != null) {
                    spec.add(PriceList(
                        id = data.specification!!.id,
                        specification = data.specification!!.specification,
                        unitCost = data.specification!!.unitCost,
                        unitPrice = data.specification!!.unitPrice,
                        minPrice = data.specification!!.minPrice,
                        buyDown = data.specification!!.buyDown,
                        msrp = data.specification!!.msrp,
                        salesPrice = data.specification!!.salesPrice,
                        margin = data.specification!!.margin,
                        markup = data.specification!!.markup,
                        quantity = data.specification!!.quantity,
                        soldAlong = data.specification!!.soldAlong,
                        tax = data.specification!!.tax
                                      ))
                }

                var taxRate = 0.00
                for (i in data.taxList) taxRate += i.rateValue!!

                val taxAmt = (data.unitPrice!!.times(taxRate)) / 100

                val qty = if (!isFromReturn) data.quantity!! else -data.quantity!!

                details.add(0, ItemCartDetail(
                    id = data.itemId,
                    itemGuid = data.ordreItemGuid,
                    name = data.itemName,
                    sku = data.sku,
                    taxGroupId = data.taxGroupId,
                    itemTax = if (data.isTax!!) "Y" else "N",
                    itemSpecification = spec,
                    taxList = data.taxList,
                    quantity = qty,
                    price = data.unitPrice!!,
                    originalPrice = data.unitPrice!!,
                    priceTotal = (data.unitPrice!!.times(qty)),
                    tax = taxAmt,
                    taxTotal = taxAmt.times(qty),
                    specificationId = data.itemSpecification!!, acceptFoodStamp = data.acceptFoodStamp!!, returnOrderItemId = data.id!!
                                             ))
            }

            for (element in details) element.orderId = orderId
            setResumeList(details)
        }
        CoroutineScope(Dispatchers.IO).launch {
            viewModel.updateCart()
        }
    }
    //endregion

    //region To manage Specification
    /**
     * @param cartDetail Cart Item Details
     * @author Safiyuddin Surti
     */
    private fun manageSpecification(cartDetail: ItemCartDetail) {
        val ft = supportFragmentManager.beginTransaction()
        val dialogFragment = ItemSpecificationPopupDialog.newInstance()
        val prevFragment: Fragment? = supportFragmentManager.findFragmentByTag(ItemSpecificationPopupDialog::class.java.name)
        if (prevFragment != null) return
        val bundle = Bundle()
        bundle.putSerializable(Default.SPECIFICATION, cartDetail.itemSpecification)
        dialogFragment.arguments = bundle
        dialogFragment.isCancelable = false
        dialogFragment.show(ft, ItemSpecificationPopupDialog::class.java.name)
        dialogFragment.setListener(object : ItemSpecificationPopupDialog.OnButtonClickListener {
            override fun onButtonClickListener(typeButton: Enums.ClickEvents, specification: PriceList?) {
                if (dialogFragment.isVisible) dialogFragment.dismiss()
                when (typeButton) {
                    Enums.ClickEvents.LOAD -> {
                        try {
                            cartDetail.apply {
                                price = specification!!.unitPrice!!
                                originalPrice = specification.unitPrice!!
                                taxRate = 0.00
                                for (data in taxList) taxRate += data.rateValue!!
                                tax = (price.times(taxRate)) / 100
                                // quantity = soldAlongQty
                                priceTotal = (price.times(quantity))
                                taxTotal = (tax.times(quantity))
                                specificationId = specification.id!!
                                name += " ,${specification.specification}"
                               // itemTax = if (tax > 0.00) "Y" else "N"
                            }
                            cartAdapter.apply {
                                if (list.size == 0) list.clear()
                                val ind = list.indexOfFirst { item -> item.sku!! == cartDetail.sku && item.id == cartDetail.id && item.specificationId == cartDetail.specificationId }
                                itemRingUp(ind, cartDetail)
                            }
                        } catch (ex: Exception) {
                            Utils.printAndWriteException(ex)
                        }
                    }
                    else -> {}
                }
            }
        })
    }
    //endregion

    //region To ring up an Item to cart
    /**
     * @param ind Available index in the cart
     * @param cartDetail Item detail
     * @author Safiyuddin Surti
     */
    private fun itemRingUp(ind: Int, cartDetail: ItemCartDetail) {
        cartAdapter.apply {
            if (ind >= 0) {
                if (getStockDetails(cartDetail, list[ind].quantity + 1)) manageItemQuantity(ind, list[ind].quantity + 1)
                else showSweetDialog(this@OrderActivity, resources.getString(R.string.lbl_OutOfStock))
            } else {
                val isQtyAvailable = getStockDetails(cartDetail, cartDetail.quantity)
                when {
                    cartDetail.promptForQuantity!! || cartDetail.quantity == 0 -> manageQuantity(0, cartDetail, true)
                    cartDetail.promptForPrice!! -> {
                        if (isQtyAvailable) managePrice(cartDetail)
                        else showSweetDialog(this@OrderActivity, resources.getString(R.string.lbl_OutOfStock))
                    }
                    else -> {
                        if (isQtyAvailable) {
                            setList(cartDetail)
                            fetchSoldAlong(cartDetail)
                            CoroutineScope(Dispatchers.IO).launch {
                                viewModel.updateCart()
                            }
                        } else showSweetDialog(this@OrderActivity, resources.getString(R.string.lbl_OutOfStock))
                    }
                }
            }
        }
    }
    //endregion

    //region To check if Item is in stock or not
    /**
     * @param cartDetail Cart item detail
     * @param qty Quantity of an item
     * @return Quantity available or not
     */
    private fun getStockDetails(cartDetail: ItemCartDetail, qty: Int): Boolean {
        var isAvailable = false
        val data = cartDetail.itemSpecification.find { it.id == cartDetail.specificationId }
        if (data != null) isAvailable = data.quantity!! >= qty
        return isAvailable
    }
    //endregion

    //region To manage menus
    private fun manageMenus() {
        val ft = supportFragmentManager.beginTransaction()
        val dialogFragment = SetupMenuPopupDialog.newInstance()
        val prevFragment: Fragment? = supportFragmentManager.findFragmentByTag(SetupMenuPopupDialog::class.java.name)
        if (prevFragment != null) return
        val menuList = ArrayList<String>()
        menuList.add(resources.getString(R.string.Menu_PaidOut_Drop))
        menuList.add(resources.getString(R.string.Menu_QuickAdd))
        menuList.add(resources.getString(R.string.button_PrintLast))
        val bundle = Bundle()
        bundle.putSerializable(Default.MENU, menuList)
        dialogFragment.arguments = bundle
        dialogFragment.isCancelable = false
        dialogFragment.show(ft, SetupMenuPopupDialog::class.java.name)
        dialogFragment.setListener(object : SetupMenuPopupDialog.OnButtonClickListener {
            override fun onButtonClickListener(specification: String?) {
                if (dialogFragment.isVisible) dialogFragment.dismiss()
                cartAdapter.apply {
                    when (specification) {
                        resources.getString(R.string.Menu_PaidOut_Drop) -> {
                            if (list.size == 0) {
                                val intent = Intent(this@OrderActivity, DrawerActivity::class.java)
                                intent.putExtra(Default.PARENT_ID, parentId)
                                openActivity(intent)
                            }
                            else showSweetDialog(this@OrderActivity, resources.getString(R.string.lbl_CartIsNotEmpty))
                        }
                        resources.getString(R.string.Menu_QuickAdd) -> manageQuickAddItem()
                        resources.getString(R.string.button_PrintLast) -> manageLastPrint()
                    }
                }
            }
        })
    }
    //endregion


    //region To  manage quick add item
    private fun manageQuickAddItem() {
        val ft = supportFragmentManager.beginTransaction()
        val dialogFragment = QuickAddPopupDialog.newInstance()
        val prevFragment: Fragment? = supportFragmentManager.findFragmentByTag(QuickAddPopupDialog::class.java.name)
        if (prevFragment != null) return
        dialogFragment.isCancelable = false
        dialogFragment.show(ft, QuickAddPopupDialog::class.java.name)
        dialogFragment.setListener(object : QuickAddPopupDialog.OnButtonClickListener {
            override fun onButtonClickListener(typeButton: Enums.ClickEvents) {
                if (dialogFragment.isVisible) dialogFragment.dismiss()
            }
        })
    }
    //endregion

    //region To manage age check validation
    /**
     * @param paymentAmount Total paid amount
     * @param ageVerified Age verified or not
     * @param payment Multiple payment list
     * @param discountId Order discount id
     * @param discountAmount Order discount amount
     */
    private fun manageAgeCheck(paymentAmount: Double = 0.00, ageVerified: Boolean = false, payment: ArrayList<OrderPaymentRequest> = ArrayList(), discountId: Int = 0, discountAmount: Double = 0.00) {
        val ft = supportFragmentManager.beginTransaction()
        val dialogFragment = AgeCheckPopupDialog.newInstance()
        val prevFragment: Fragment? = supportFragmentManager.findFragmentByTag(AgeCheckPopupDialog::class.java.name)
        if (prevFragment != null) return
        dialogFragment.isCancelable = false
        dialogFragment.show(ft, AgeCheckPopupDialog::class.java.name)
        dialogFragment.setListener(object : AgeCheckPopupDialog.OnButtonClickListener {
            override fun onButtonClickListener(typeButton: Enums.ClickEvents, data: PlateData?) {
                if (dialogFragment.isVisible) dialogFragment.dismiss()
                when (typeButton) {
                    Enums.ClickEvents.SAVE -> {
                        if (ageVerified) {
                            cartAdapter.apply {
                                val ind = list.indexOfFirst { it.orderId > 0 }
                                if (ind >= 0) performUpdateOrder(paymentAmount = paymentAmount, orderId = list[ind].orderId, ageVerified = true, licenseData = data ?: PlateData(), payment = payment, discountId = discountId, discountAmount = discountAmount)
                                else performOrder(paymentAmount = paymentAmount, ageVerified = true, licenseData = data ?: PlateData(), payment = payment, discountId = discountId, discountAmount = discountAmount)
                            }
                        }
                    }
                    else -> {}
                }
            }
        })
    }
    //endregion

    //region To manage Lot Matrix
    /**
     * @param cartDetail Item details
     * @author Safiyuddin Surti
     */
    private fun manageLotMatrix(cartDetail: ItemCartDetail) {
        val ft = supportFragmentManager.beginTransaction()
        val dialogFragment = ItemAttributePopupDialog.newInstance()
        val prevFragment: Fragment? = supportFragmentManager.findFragmentByTag(ItemAttributePopupDialog::class.java.name)
        if (prevFragment != null) return
        val bundle = Bundle()
        bundle.putSerializable(Default.ATTRIBUTE, cartDetail.attributes)
        dialogFragment.arguments = bundle
        dialogFragment.isCancelable = false
        dialogFragment.show(ft, ItemAttributePopupDialog::class.java.name)
        dialogFragment.setListener(object : ItemAttributePopupDialog.OnButtonClickListener {
            override fun onButtonClickListener(typeButton: Enums.ClickEvents, specId: Int?) {
                if (dialogFragment.isVisible) dialogFragment.dismiss()
                when (typeButton) {
                    Enums.ClickEvents.LOAD -> {
                        try {
                            cartDetail.apply {
                                val index = itemSpecification.indexOfFirst { it.id == specId }
                                if (index >= 0) {
                                    price = itemSpecification[index].unitPrice!!
                                    originalPrice = itemSpecification[index].unitPrice!!
                                    specificationId = itemSpecification[index].id!!
                                    name += " ,${itemSpecification[index].specification}"
                                }

                                taxRate = 0.00
                                for (data in taxList) taxRate += data.rateValue!!
                                tax = (price.times(taxRate)) / 100
                                priceTotal = (price.times(quantity))
                                taxTotal = (tax.times(quantity))
                               // itemTax = if (tax > 0.00) "Y" else "N"
                            }
                            cartAdapter.apply {
                                if (list.size == 0) list.clear()
                                val ind = list.indexOfFirst { item -> item.sku!! == cartDetail.sku && item.id == cartDetail.id && item.specificationId == cartDetail.specificationId }
                                itemRingUp(ind, cartDetail)
                            }
                        } catch (ex: Exception) {
                            Utils.printAndWriteException(ex)
                        }
                    }
                    else -> {}
                }
            }
        })
    }
    //endregion

    //region To manage last print
    private fun manageLastPrint() {
        viewModel.fetchLastOrder().observe(this) {
            CoroutineScope(Dispatchers.Main).launch {
                if (it != null) manageReceipt(it.id!!, isFromHold = it.status == Default.HOLD_ORDER, isFromVoid = it.status == Default.VOID_ORDER)
            }
        }
    }
    //endregion

    //region To manage discount
    private fun manageDiscount() {
        val ft = supportFragmentManager.beginTransaction()
        val dialogFragment = OrderDiscountPopupDialog.newInstance()
        val prevFragment: Fragment? = supportFragmentManager.findFragmentByTag(OrderDiscountPopupDialog::class.java.name)
        if (prevFragment != null) return
        dialogFragment.isCancelable = false
        dialogFragment.show(ft, OrderDiscountPopupDialog::class.java.name)
        dialogFragment.setListener(object : OrderDiscountPopupDialog.OnButtonClickListener {
            override fun onButtonClickListener(typeButton: Enums.ClickEvents, discount: DiscountMapList?) {
                if (dialogFragment.isVisible) dialogFragment.dismiss()
                when (typeButton) {
                    Enums.ClickEvents.LOAD -> {
                        if (discount != null) {
                            cartAdapter.apply {
                                val index = list.indexOfFirst { it.discountId > 0 && !it.appliedOnOrder }
                                if (index >= 0) manageAppliedDiscount(index, discount)
                                else {
                                    orderDiscount = discount
                                    list.forEach {
                                        it.appliedOnOrder = true
                                    }
                                    CoroutineScope(Dispatchers.IO).launch {
                                        viewModel.updateCart()
                                    }
                                }
                            }
                        }
                    }
                    else -> {}
                }
            }
        })
    }

    /**
     * @param index Manage index to apply discount on order
     * @param discount Discount list
     * @author Safiyuddin Surti
     */
    private fun manageAppliedDiscount(index: Int, discount: DiscountMapList?) {
        val ft = supportFragmentManager.beginTransaction()
        val dialogFragment = DeleteAlertPopupDialog(resources.getString(R.string.lbl_DiscountApplied), 0, appliedDiscount = true)
        val prevFragment: Fragment? = supportFragmentManager.findFragmentByTag(DeleteAlertPopupDialog::class.java.name)
        if (prevFragment != null) return
        dialogFragment.isCancelable = false
        dialogFragment.show(ft, DeleteAlertPopupDialog::class.java.name)
        dialogFragment.setListener(object : DeleteAlertPopupDialog.OnButtonClickListener {
            override fun onButtonClickListener(typeButton: Enums.ClickEvents) {
                if (dialogFragment.isVisible) dialogFragment.dismiss()
                when (typeButton) {
                    Enums.ClickEvents.SAVE -> {
                        orderDiscount = discount!!
                        cartAdapter.apply {
                            list[index].appliedOnOrder = true
                        }
                        CoroutineScope(Dispatchers.IO).launch {
                            viewModel.updateCart()
                        }
                    }
                    else -> {}
                }
            }
        })
    }
    //endregion

    //region To manage favourite item
    /**
     * @param type Menu type
     * @param id Item id
     * @author Safiyuddin Surti
     */
    private fun setupMenuItems(type: Int, id: Int = 0) {
        lifecycleScope.launch {
            try {
                val result = viewModel.fetchShortcutItems(type, id)
                val data = if (id > 0) listOf(DataDetails(id = 0, name = resources.getString(R.string.lbl_Back), isGroup = false, type = type)) + result
                else result.onEach { it.isGroup = type != Default.MENU_ITEM; it.type = type }
                withContext(Dispatchers.Main) {
                    favouriteItemsAdapter.apply {
                        clearData()
                        setList(data)
                    }
                }
            } catch (ex: Exception) {
                Utils.printAndWriteException(ex)
            }
        }
    }
    //endregion
}