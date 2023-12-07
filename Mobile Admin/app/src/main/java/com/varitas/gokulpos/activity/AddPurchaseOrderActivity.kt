package com.varitas.gokulpos.activity

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
import com.varitas.gokulpos.R
import com.varitas.gokulpos.adapter.AddPurchaseItemAdapter
import com.varitas.gokulpos.adapter.AutoCompleteAdapter
import com.varitas.gokulpos.databinding.ActivityAddpurchaseorderBinding
import com.varitas.gokulpos.model.Header
import com.varitas.gokulpos.response.CommonDropDown
import com.varitas.gokulpos.response.DetailsAt
import com.varitas.gokulpos.response.FavouriteItems
import com.varitas.gokulpos.response.PurchaseOrder
import com.varitas.gokulpos.utilities.Constants
import com.varitas.gokulpos.utilities.CustomSpinner
import com.varitas.gokulpos.utilities.Default
import com.varitas.gokulpos.utilities.Utils
import com.varitas.gokulpos.viewmodel.OrdersViewModel
import com.varitas.gokulpos.viewmodel.ProductFeatureViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.moallemi.tools.extension.date.now
import me.moallemi.tools.extension.date.toDay
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.TimeZone

class AddPurchaseOrderActivity : BaseActivity() {
    lateinit var binding : ActivityAddpurchaseorderBinding
    val viewModel : OrdersViewModel by viewModels()
    private val viewModelDropdown : ProductFeatureViewModel by viewModels()
    private lateinit var materialBillDateBuilder : MaterialDatePicker.Builder<Long>
    private lateinit var materialBillDatePicker : MaterialDatePicker<Long>
    private lateinit var materialDeliveryDateBuilder : MaterialDatePicker.Builder<Long>
    private lateinit var materialDeliveryDatePicker : MaterialDatePicker<Long>
    private lateinit var suggestionAdapter : AutoCompleteAdapter
    private lateinit var itemDetails : FavouriteItems
    private lateinit var vendorList : ArrayList<CommonDropDown>
    private lateinit var vendorSpinner : ArrayAdapter<CommonDropDown>
    private lateinit var vendor : CommonDropDown
    private lateinit var purchaseAddItemAdapter : AddPurchaseItemAdapter
    private var orderSubTotal = 0.00
    private var orderTotalTax = 0.00
    private var orderGrandTotal = 0.00
    private var purchaseId = 0
    private var statusId = 0
    private var parentId = 0

    companion object {
        lateinit var Instance : AddPurchaseOrderActivity
    }

    init {
        Instance = this
    }

    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddpurchaseorderBinding.inflate(layoutInflater)
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
        orderSubTotal = 0.00
        orderTotalTax = 0.00
        orderGrandTotal = 0.00
        binding.apply {
            layoutToolbar.textViewToolbarName.text = resources.getString(R.string.Menu_PurchaseOrder)
            layoutToolbar.imageViewAction.setImageDrawable(ContextCompat.getDrawable(this@AddPurchaseOrderActivity, R.drawable.ic_save))
            layoutToolbar.imageViewBack.visibility = View.VISIBLE
        }
        //Today Date to Before is Disable
        val constraintsBuilder = CalendarConstraints.Builder().setValidator(DateValidatorPointForward.now())

        purchaseId = intent.extras?.getInt(Default.ID)!!

        materialBillDateBuilder = MaterialDatePicker.Builder.datePicker().setInputMode(MaterialDatePicker.INPUT_MODE_CALENDAR)
        materialBillDatePicker = materialBillDateBuilder.build()
        materialDeliveryDateBuilder = MaterialDatePicker.Builder.datePicker().setInputMode(MaterialDatePicker.INPUT_MODE_CALENDAR)
        materialDeliveryDatePicker = materialBillDateBuilder.setCalendarConstraints(constraintsBuilder.build()).build()
        suggestionAdapter = AutoCompleteAdapter(this, android.R.layout.simple_spinner_dropdown_item, ArrayList())
        vendorList = ArrayList()
        vendorSpinner = ArrayAdapter(this, R.layout.spinner_items, vendorList)
        purchaseAddItemAdapter = AddPurchaseItemAdapter {
            purchaseAddItemAdapter.apply {
                list.removeAt(it)
                notifyItemRemoved(it)
                notifyItemRangeChanged(it, list.size)
                calculateTotal()
            }
        }
        calculateTotal()
        itemDetails = FavouriteItems()
    }

    private fun postInitView() {
        binding.apply {
            recycleViewAddItem.apply {
                adapter = purchaseAddItemAdapter
                layoutManager = LinearLayoutManager(this@AddPurchaseOrderActivity)
            }
            header = Header(resources.getString(R.string.lbl_SrNumber), resources.getString(R.string.hint_ItemName), resources.getString(R.string.lbl_Qty))
            editTextItemSearch.setAdapter(suggestionAdapter)
            editTextItemSearch.threshold = 0
            val selectDate = Constants.dateFormat_MMM_dd_yyyy.format(now())
            val selectDDate = Constants.dateFormat_MMM_dd_yyyy.format(1.toDay().sinceNow)
            editTextBillDate.setText(selectDate)
            editTextDeliveryDate.setText(selectDDate)
            id = purchaseId
        }
    }
    //endregion

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
                        editTextBillDate.setText(Constants.dateFormat_MMM_dd_yyyy.format(Utils.convertStringToDate(formatter = Constants.dateFormatT, parseDate = it.date)))
                        editTextDeliveryDate.setText(Constants.dateFormat_MMM_dd_yyyy.format(Utils.convertStringToDate(formatter = Constants.dateFormatT, parseDate = it.expectedDeliveryDate)))
                        textViewPurchaseNo.setText(if(it.purchaseOrderNo!!.isNotEmpty()) it.purchaseOrderNo else "")
                        textViewShipTo.setText(if(it.shipto!!.isNotEmpty()) it.shipto else "")
                        if(it.details.size > 0) {
                            purchaseAddItemAdapter.apply {
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

    //region To manage click
    private fun manageClicks() {
        binding.apply {
            layoutToolbar.imageViewBack.clickWithDebounce {
                val intent = Intent(this@AddPurchaseOrderActivity, PurchaseOrderActivity::class.java)
                intent.putExtra(Default.PARENT_ID, parentId)
                openActivity(intent)
            }

            materialBillDatePicker.addOnPositiveButtonClickListener {
                val utc = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
                utc.timeInMillis = it
                val format = SimpleDateFormat("MMM dd, yyyy")
                val formatted : String = format.format(utc.time)
                editTextBillDate.setText(formatted)
            }

            editTextBillDate.clickWithDebounce { //setStartEndDate(startCalendar, binding.textViewBirthDate)
                materialBillDatePicker.show(supportFragmentManager, "MATERIAL_DATE_PICKER")
            }

            materialDeliveryDatePicker.addOnPositiveButtonClickListener {
                val utc = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
                utc.timeInMillis = it
                val format = SimpleDateFormat("MMM dd, yyyy")
                val formatted : String = format.format(utc.time)
                editTextDeliveryDate.setText(formatted)
            }

            editTextDeliveryDate.clickWithDebounce { //setStartEndDate(startCalendar, binding.textViewBirthDate)
                materialDeliveryDatePicker.show(supportFragmentManager, "MATERIAL_DATE_PICKER")
            }


            editTextItemSearch.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s : CharSequence?, start : Int, count : Int, after : Int) {}

                override fun onTextChanged(s : CharSequence?, start : Int, before : Int, count : Int) {
                    if(!s.isNullOrEmpty()) fetchSuggestions(s.toString())
                }

                override fun afterTextChanged(s : Editable?) {}
            })

            editTextItemSearch.setOnItemClickListener { parent, _, position, _->
                itemDetails = parent.getItemAtPosition(position) as FavouriteItems
                editTextItemName.setText(itemDetails.itemName)
                editTextQty.setText("1")
                editTextCost.setText("0.00")
                editTextTax.setText("0.00")
                editTextItemSearch.text.clear()
            }

            buttonAddItem.clickWithDebounce {
                val qty = if(editTextQty.text!!.isNotEmpty()) editTextQty.text.toString().trim().toLong() else 0
                val cost = if(editTextCost.text!!.isNotEmpty()) editTextCost.text.toString().trim().toDouble() else 0.00
                val tax = if(editTextTax.text!!.isNotEmpty()) editTextTax.text.toString().trim().toDouble() else 0.00
                val taxSum = cost.times(tax).div(100).times(qty)
                val total = cost.times(qty).plus(tax.times(qty))
                if(editTextItemName.text!!.isNotEmpty()) {
                    purchaseAddItemAdapter.apply {
                        addPurchaseItemList(
                            DetailsAt(
                                poid = 0,
                                itemId = itemDetails.id,
                                specificationId = itemDetails.specificationId,
                                quantity = qty,
                                uom = "",
                                lastCost = cost,
                                sItemName = if(editTextItemName.text!!.isNotEmpty()) editTextItemName.text.toString().trim() else "",
                                tax = taxSum,
                                total = total,
                                receivedQuantity = 0,
                                status = Default.CONFIRM_ORDER
                            )
                        )
                        calculateTotal()
                    }
                    editTextItemName.text!!.clear()
                    editTextQty.text!!.clear()
                    editTextCost.text!!.clear()
                    editTextTax.text!!.clear()
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

            layoutToolbar.imageViewAction.clickWithDebounce {
                val shipTo = textViewShipTo.text.toString().trim()
                if(vendor.value!! > 0 && purchaseAddItemAdapter.list.size > 0) {
                    if(purchaseId == 0) {
                        val req = PurchaseOrder(
                            vendorId = vendor.value,
                            date = Constants.dateFormatTZ.format(Utils.convertStringToDate(formatter = Constants.dateFormat_MMM_dd_yyyy, parseDate = editTextBillDate.text.toString())),
                            expectedDeliveryDate = Constants.dateFormatTZ.format(Utils.convertStringToDate(formatter = Constants.dateFormat_MMM_dd_yyyy, parseDate = editTextDeliveryDate.text.toString())),
                            addressId = 0,
                            shipto = shipTo,
                            subTotal = orderSubTotal,
                            tax = orderTotalTax,
                            grandTotal = orderGrandTotal,
                            note = "",
                            details = purchaseAddItemAdapter.list
                        )
                        viewModel.insertPurchaseOrder(req).observe(this@AddPurchaseOrderActivity) {
                            if(it) {
                                val intent = Intent(this@AddPurchaseOrderActivity, PurchaseOrderActivity::class.java)
                                intent.putExtra(Default.PARENT_ID, parentId)
                                openActivity(intent)
                            }
                        }
                    } else {
                        if(statusId == 5) {
                            val req = PurchaseOrder(
                                id = purchaseId,
                                vendorId = vendor.value,
                                date = Constants.dateFormatTZ.format(Utils.convertStringToDate(formatter = Constants.dateFormat_MMM_dd_yyyy, parseDate = editTextBillDate.text.toString())),
                                expectedDeliveryDate = Constants.dateFormatTZ.format(Utils.convertStringToDate(formatter = Constants.dateFormat_MMM_dd_yyyy, parseDate = editTextDeliveryDate.text.toString().trim())),
                                addressId = 0,
                                shipto = shipTo,
                                subTotal = orderSubTotal,
                                tax = orderTotalTax,
                                grandTotal = orderGrandTotal,
                                note = "",
                                status = statusId,
                                details = purchaseAddItemAdapter.list
                            )
                            viewModel.updatePurchaseOrder(req).observe(this@AddPurchaseOrderActivity) {
                                if(it) {
                                    val intent = Intent(this@AddPurchaseOrderActivity, PurchaseOrderActivity::class.java)
                                    intent.putExtra(Default.PARENT_ID, parentId)
                                    openActivity(intent)
                                }
                            }
                        } else showSweetDialog(this@AddPurchaseOrderActivity, resources.getString(R.string.lbl_PurchaseUpdateValidation))
                    }
                } else if(vendor.value == 0) showSweetDialog(this@AddPurchaseOrderActivity, resources.getString(R.string.Validation, resources.getString(R.string.Menu_Vendor)))
                else showSweetDialog(this@AddPurchaseOrderActivity, resources.getString(R.string.lbl_ItemValidation))
            }
        }
    }
    //endregion


    @Deprecated("Deprecated in Java") override fun onBackPressed() {
        val intent = Intent(this@AddPurchaseOrderActivity, PurchaseOrderActivity::class.java)
        intent.putExtra(Default.PARENT_ID, parentId)
        openActivity(intent)
        onBackPressedDispatcher.onBackPressed() //with this line
    }

    private fun fetchSuggestions(str : String) {
        viewModel.fetchAutoCompleteItems(str).observe(this) {
            CoroutineScope(Dispatchers.Main).launch {
                suggestionAdapter.apply {
                    clear()
                    addAll(it)
                    notifyDataSetChanged()
                    binding.editTextItemSearch.showDropDown()
                }
            }
        }
    }

    //region Calculate Totals
    private fun calculateTotal() {
        purchaseAddItemAdapter.apply {
            orderGrandTotal = list.sumOf { it.total!! }
            orderSubTotal = list.sumOf { it.lastCost!!.times(it.quantity!!) }
            orderTotalTax = list.sumOf { it.tax!!}
            binding.apply {
                lableSubTotal = "${resources.getString(R.string.lbl_Subtotal)}: "
                subTotal = Utils.setAmountWithCurrency(this@AddPurchaseOrderActivity, orderSubTotal)
                lableTax = "${resources.getString(R.string.lbl_TotalTax)}: "
                totalTax = Utils.setAmountWithCurrency(this@AddPurchaseOrderActivity, orderTotalTax)
                lableQty = "${resources.getString(R.string.hint_TotalQty)}: "
                val qty = list.sumOf { it.quantity!! }
                totalQty = if(qty.toString().length > 1) qty.toString() else "0$qty"
                lableGrandTotal = "${resources.getString(R.string.lbl_GrandTotal)}: "
                grandTotal = Utils.setAmountWithCurrency(this@AddPurchaseOrderActivity, orderGrandTotal)
            }
        }
    }//endregion

    //region Manage Dropdown
    private fun manageDropdown(data : PurchaseOrder? = null) {
        viewModelDropdown.getVendorDropDown().observe(this) {
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
}
