package com.varitas.gokulpos.tablet.activity

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.varitas.gokulpos.tablet.R
import com.varitas.gokulpos.tablet.adapter.AddPurchaseOrderAdapter
import com.varitas.gokulpos.tablet.adapter.AutoCompleteAdapter
import com.varitas.gokulpos.tablet.databinding.ActivityAddPurchaseorderBinding
import com.varitas.gokulpos.tablet.model.Header
import com.varitas.gokulpos.tablet.response.CommonDropDown
import com.varitas.gokulpos.tablet.response.FavouriteItems
import com.varitas.gokulpos.tablet.response.PurchaseOrder
import com.varitas.gokulpos.tablet.response.PurchaseOrderDetail
import com.varitas.gokulpos.tablet.utilities.Constants
import com.varitas.gokulpos.tablet.utilities.CustomSpinner
import com.varitas.gokulpos.tablet.utilities.Default
import com.varitas.gokulpos.tablet.utilities.Utils
import com.varitas.gokulpos.tablet.viewmodel.OrdersViewModel
import com.varitas.gokulpos.tablet.viewmodel.ProductFeatureViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.moallemi.tools.extension.date.now
import me.moallemi.tools.extension.date.toDay
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.TimeZone

@AndroidEntryPoint class AddPurchaseOrderActivity : BaseActivity() {
    private lateinit var binding : ActivityAddPurchaseorderBinding
    val viewModel : OrdersViewModel by viewModels()
    private val viewModelDropdown : ProductFeatureViewModel by viewModels()
    private lateinit var vendorList : ArrayList<CommonDropDown>
    private lateinit var vendorSpinner : ArrayAdapter<CommonDropDown>
    private lateinit var vendor : CommonDropDown
    private lateinit var suggestionAdapter : AutoCompleteAdapter
    private lateinit var purchaseOrderAddAdapter : AddPurchaseOrderAdapter
    private lateinit var itemDetails : FavouriteItems
    private lateinit var materialBillDateBuilder : MaterialDatePicker.Builder<Long>
    private lateinit var materialBillDatePicker : MaterialDatePicker<Long>
    private lateinit var materialDeliveryDateBuilder : MaterialDatePicker.Builder<Long>
    private lateinit var materialDeliveryDatePicker : MaterialDatePicker<Long>
    private var purchaseId = 0
    private var orderSubTotal = 0.00
    private var orderTotalTax = 0.00
    private var orderGrandTotal = 0.00
    private var statusId = 0
    private var parentId = 0
    private var isFromOrder = false


    companion object {
        lateinit var Instance : AddPurchaseOrderActivity
    }

    init {
        Instance = this
    }

    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddPurchaseorderBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setSupportActionBar(binding.layoutToolbar.toolbarCommon)
        initData()
        postInitView()
        loadData()
        manageClicks()
    }

    //region To init data
    private fun initData() {
        parentId = if(intent.extras?.getInt(Default.PARENT_ID) != null) intent.extras?.getInt(Default.PARENT_ID)!! else 0
        isFromOrder = if(intent.extras?.getBoolean(Default.ORDER) != null) intent.extras?.getBoolean(Default.ORDER)!! else false
        orderSubTotal = 0.00
        orderTotalTax = 0.00
        orderGrandTotal = 0.00
        binding.apply {
            layoutToolbar.imageViewAction.setImageDrawable(ContextCompat.getDrawable(this@AddPurchaseOrderActivity, R.drawable.ic_save))
            layoutToolbar.imageViewBack.visibility = View.VISIBLE
        }
        purchaseId = intent.extras?.getInt(Default.ID)!!
        purchaseOrderAddAdapter = AddPurchaseOrderAdapter {
            purchaseOrderAddAdapter.apply {
                list.removeAt(it)
                notifyItemRemoved(it)
                notifyItemRangeChanged(it, list.size)
                calculateTotal()
            }
        }
        calculateTotal()
        suggestionAdapter = AutoCompleteAdapter(this, android.R.layout.simple_spinner_dropdown_item, ArrayList())
        vendorList = ArrayList()
        vendorSpinner = ArrayAdapter(this, R.layout.spinner_items, vendorList)
        itemDetails = FavouriteItems()
        //Today Date to Before is Disable
        val constraintsBuilder = CalendarConstraints.Builder().setValidator(DateValidatorPointForward.now())
        materialBillDateBuilder = MaterialDatePicker.Builder.datePicker().setInputMode(MaterialDatePicker.INPUT_MODE_CALENDAR)
        materialBillDatePicker = materialBillDateBuilder.build()
        materialDeliveryDateBuilder = MaterialDatePicker.Builder.datePicker().setInputMode(MaterialDatePicker.INPUT_MODE_CALENDAR)
        materialDeliveryDatePicker = materialDeliveryDateBuilder.setCalendarConstraints(constraintsBuilder.build()).build()
    }

    private fun postInitView() {
        binding.apply {
            layoutToolbar.textViewToolbarName.text = resources.getString(R.string.PurchaseOrder, if(purchaseId > 0) resources.getString(R.string.lbl_Edit) else resources.getString(R.string.lbl_Add))
            recycleViewAddItemPurchase.apply {
                adapter = purchaseOrderAddAdapter
                layoutManager = LinearLayoutManager(this@AddPurchaseOrderActivity)
            }
            textInputItemSearch.setAdapter(suggestionAdapter)
            textInputItemSearch.threshold = 0
            header = Header(resources.getString(R.string.lbl_Id), resources.getString(R.string.hint_ItemName), resources.getString(R.string.lbl_Quantity), resources.getString(R.string.hint_Cost), resources.getString(R.string.Menu_Tax), resources.getString(R.string.lbl_Total), resources.getString(R.string.lbl_Action))
            val startDate = Constants.dateFormat_MMM_dd_yyyy.format(now())
            val endDate = Constants.dateFormat_MMM_dd_yyyy.format(1.toDay().sinceNow)
            textInputBillDate.setText(startDate)
            textInputDeliveryDate.setText(endDate)
        }
    } //endregion

    //region To load data
    private fun loadData() {
        viewModel.showProgress.observe(this) {
            manageProgress(it)
        }

        viewModel.errorMsg.observe(this) {
            showSweetDialog(this, it)
        }

        if(purchaseId > 0) {
            viewModel.getPurchaseOrderDetail(purchaseId).observe(this) {
                if(it != null) {
                    binding.apply {
                        textInputBillDate.setText(Constants.dateFormat_MMM_dd_yyyy.format(Utils.convertStringToDate(formatter = Constants.dateFormatT, parseDate = it.date)))
                        textInputDeliveryDate.setText(Constants.dateFormat_MMM_dd_yyyy.format(Utils.convertStringToDate(formatter = Constants.dateFormatT, parseDate = it.expectedDeliveryDate)))
                        textInputShipTo.setText(it.shipto!!)
                        if(it.details.size > 0) {
                            purchaseOrderAddAdapter.apply {
                                setList(it.details)
                                calculateTotal()
                            }
                        }
                    }
                    statusId = it.status!!
                    manageDropdown(it)
                }
            }
        } else manageDropdown()
    }
    //endregion

    //region To manage click events
    private fun manageClicks() {
        binding.apply {
            layoutToolbar.imageViewBack.clickWithDebounce {
                manageBack()
            }

            textInputItemSearch.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s : CharSequence?, start : Int, count : Int, after : Int) {}

                override fun onTextChanged(s : CharSequence?, start : Int, before : Int, count : Int) {
                    if(!s.isNullOrEmpty()) fetchSuggestions(s.toString())
                }

                override fun afterTextChanged(s : Editable?) {}
            })

            textInputItemSearch.setOnItemClickListener { parent, _, position, _->
                itemDetails = parent.getItemAtPosition(position) as FavouriteItems
                textInputItemName.setText(itemDetails.name)
                textInputQty.setText("1")
                textInputCost.setText("0.00")
                textInputTax.setText("0.00")
                textInputItemSearch.text.clear()
            }

            buttonAddItem.clickWithDebounce {
                val qty = if(textInputQty.text!!.isNotEmpty()) textInputQty.text.toString().trim().toLong() else 0
                val cost = if(textInputCost.text!!.isNotEmpty()) textInputCost.text.toString().trim().toDouble() else 0.00
                val tax = if(textInputTax.text!!.isNotEmpty()) textInputTax.text.toString().trim().toDouble() else 0.00
                val taxSum = cost.times(qty).times(tax).div(100)
                val total = cost.times(qty).plus(taxSum)
                if(textInputItemName.text!!.isNotEmpty()) {
                    purchaseOrderAddAdapter.apply {
                        addPurchaseOrderList(
                            PurchaseOrderDetail(
                                poid = 0,
                                itemId = itemDetails.id,
                                specificationId = itemDetails.specificationId,
                                quantity = qty,
                                uom = "",
                                lastCost = Utils.getTwoDecimalValue(cost).toDouble(),
                                sItemName = if(textInputItemName.text!!.isNotEmpty()) textInputItemName.text.toString().trim() else "",
                                tax = Utils.getTwoDecimalValue(taxSum).toDouble(),
                                total = Utils.getTwoDecimalValue(total).toDouble(),
                                receivedQuantity = 0,
                                status = Default.COMPLETE_ORDER
                            )
                        )
                        calculateTotal()
                    }
                    textInputItemName.text!!.clear()
                    textInputQty.text!!.clear()
                    textInputCost.text!!.clear()
                    textInputTax.text!!.clear()
                    itemDetails = FavouriteItems()
                }
            }

            spinnerVendor.apply {
                adapter = vendorSpinner
                setSpinnerEventsListener(object : CustomSpinner.OnSpinnerEventsListener {
                    override fun onSpinnerOpened(spinner : Spinner?) {

                    }

                    override fun onSpinnerClosed(spinner : Spinner?) {
                        vendor = spinner?.selectedItem as CommonDropDown
                    }
                })
            }


            materialBillDatePicker.addOnPositiveButtonClickListener {
                val utc = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
                utc.timeInMillis = it
                val format = SimpleDateFormat("MMM dd, yyyy")
                val formatted : String = format.format(utc.time)
                textInputBillDate.setText(formatted)
            }

            materialDeliveryDatePicker.addOnPositiveButtonClickListener {
                val utc = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
                utc.timeInMillis = it
                val format = SimpleDateFormat("MMM dd, yyyy")
                val formatted : String = format.format(utc.time)
                textInputDeliveryDate.setText(formatted)
            }

            textInputBillDate.clickWithDebounce { //setStartEndDate(startCalendar, binding.textViewBirthDate)
                materialBillDatePicker.show(supportFragmentManager, "MATERIAL_DATE_PICKER")
            }

            textInputDeliveryDate.clickWithDebounce { //setStartEndDate(startCalendar, binding.textViewBirthDate)
                materialDeliveryDatePicker.show(supportFragmentManager, "MATERIAL_DATE_PICKER")
            }

            layoutToolbar.imageViewAction.clickWithDebounce {
                val shipTo = textInputShipTo.text.toString().trim()
                if(vendor.value!! > 0 && purchaseOrderAddAdapter.list.size > 0) {
                    if(purchaseId == 0) {
                        val req = PurchaseOrder(
                            vendorId = vendor.value,
                            date = Constants.dateFormatTZ.format(Utils.convertStringToDate(formatter = Constants.dateFormat_MMM_dd_yyyy, parseDate = textInputBillDate.text.toString())),
                            expectedDeliveryDate = Constants.dateFormatTZ.format(Utils.convertStringToDate(formatter = Constants.dateFormat_MMM_dd_yyyy, parseDate = textInputDeliveryDate.text.toString())),
                            addressId = 0,
                            shipto = shipTo,
                            subTotal = orderSubTotal,
                            tax = orderTotalTax,
                            grandTotal = orderGrandTotal,
                            note = "",
                            details = purchaseOrderAddAdapter.list
                        )
                        viewModel.insertPurchaseOrder(req).observe(this@AddPurchaseOrderActivity) {
                            if(it) manageBack()
                        }
                    } else {
                        val req = PurchaseOrder(
                            id = purchaseId,
                            vendorId = vendor.value,
                            date = Constants.dateFormatTZ.format(Utils.convertStringToDate(formatter = Constants.dateFormat_MMM_dd_yyyy, parseDate = textInputBillDate.text.toString())),
                            expectedDeliveryDate = Constants.dateFormatTZ.format(Utils.convertStringToDate(formatter = Constants.dateFormat_MMM_dd_yyyy, parseDate = textInputDeliveryDate.text.toString().trim())),
                            addressId = 0,
                            shipto = shipTo,
                            subTotal = orderSubTotal,
                            tax = orderTotalTax,
                            grandTotal = orderGrandTotal,
                            note = "",
                            status = statusId,
                            details = purchaseOrderAddAdapter.list
                        )
                        viewModel.updatePurchaseOrder(req).observe(this@AddPurchaseOrderActivity) {
                            if(it) manageBack()
                        }
                    }
                } else if(vendor.value == 0) showSweetDialog(this@AddPurchaseOrderActivity, resources.getString(R.string.Validation, resources.getString(R.string.Menu_Vendor)))
                else showSweetDialog(this@AddPurchaseOrderActivity, resources.getString(R.string.lbl_ItemValidation))
            }
        }
    } //endregion

    @Deprecated("Deprecated in Java") override fun onBackPressed() {
        manageBack()
        onBackPressedDispatcher.onBackPressed() //with this line
    }

    //region To fetch suggestions
    private fun fetchSuggestions(str : String) {
        viewModel.fetchAutoCompleteItems(str).observe(this) {
            CoroutineScope(Dispatchers.Main).launch {
                suggestionAdapter.apply {
                    clear()
                    addAll(it)
                    notifyDataSetChanged()
                    binding.textInputItemSearch.showDropDown()
                }
            }
        }
    }
    //endregion

    //region Calculate Totals
    private fun calculateTotal() {
        purchaseOrderAddAdapter.apply {
            orderGrandTotal = list.sumOf { it.total!! }
            orderSubTotal = list.sumOf { it.lastCost!!.times(it.quantity!!) }
            orderTotalTax = list.sumOf { it.tax!! }
            binding.apply {
                subTotal = "${resources.getString(R.string.lbl_Subtotal)}: ${Utils.setAmountWithCurrency(this@AddPurchaseOrderActivity, orderSubTotal)}"
                totalTax = "${resources.getString(R.string.lbl_TotalTax)}: ${Utils.setAmountWithCurrency(this@AddPurchaseOrderActivity, orderTotalTax)}"
                totalQty = "${resources.getString(R.string.lbl_TotalQty)}: ${list.sumOf { it.quantity!! }}"
                grandTotal = "${resources.getString(R.string.lbl_GrandTotal)}: ${Utils.setAmountWithCurrency(this@AddPurchaseOrderActivity, orderGrandTotal)}"
            }
        }
    }//endregion

    //region Manage Dropdown
    private fun manageDropdown(data : PurchaseOrder? = null) {
        viewModelDropdown.fetchVendorDropDown().observe(this) {
            CoroutineScope(Dispatchers.Main).launch {
                vendorSpinner.apply {
                    setDropDownViewResource(R.layout.spinner_dropdown_item)
                    vendor = CommonDropDown(0, resources.getString(R.string.lbl_SelectVendor))
                    add(vendor)
                    addAll(it)
                    if(data != null) {
                        val index = vendorList.indexOfFirst { vendor-> vendor.value == data.vendorId }
                        if(index >= 0) {
                            binding.spinnerVendor.setSelection(index)
                            vendor = vendorList[index]
                        }
                    }
                    notifyDataSetChanged()
                }
            }
        }
    }//endregion

    //Manage Back Insert/Update
    private fun manageBack(){
        val intent = Intent(this@AddPurchaseOrderActivity, PurchaseOrderActivity::class.java)
        intent.putExtra(Default.PARENT_ID, parentId)
        intent.putExtra(Default.ORDER, isFromOrder)
        openActivity(intent)
    }//endregion
}