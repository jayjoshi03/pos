package com.varitas.gokulpos.tablet.activity

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.varitas.gokulpos.tablet.R
import com.varitas.gokulpos.tablet.adapter.AddInvoiceItemAdapter
import com.varitas.gokulpos.tablet.adapter.AutoPurchaseOrderAdapter
import com.varitas.gokulpos.tablet.adapter.PaymentListAdapter
import com.varitas.gokulpos.tablet.databinding.ActivityAddInvoiceBinding
import com.varitas.gokulpos.tablet.fragmentDialogs.DeleteAlertPopupDialog
import com.varitas.gokulpos.tablet.model.Header
import com.varitas.gokulpos.tablet.response.CommonDropDown
import com.varitas.gokulpos.tablet.response.DetailsInvoice
import com.varitas.gokulpos.tablet.response.Invoice
import com.varitas.gokulpos.tablet.response.PaymentInvoice
import com.varitas.gokulpos.tablet.response.PurchaseOrder
import com.varitas.gokulpos.tablet.utilities.Constants
import com.varitas.gokulpos.tablet.utilities.CustomSpinner
import com.varitas.gokulpos.tablet.utilities.Default
import com.varitas.gokulpos.tablet.utilities.Enums
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

@AndroidEntryPoint class AddInvoiceActivity : BaseActivity() {
    private lateinit var binding : ActivityAddInvoiceBinding
    val viewModel : OrdersViewModel by viewModels()
    private val viewModelDropdown : ProductFeatureViewModel by viewModels()
    private lateinit var suggestPurchaseOrderAdapter : AutoPurchaseOrderAdapter
    private lateinit var purchaseOrderDetails : PurchaseOrder
    private lateinit var materialBillDateBuilder : MaterialDatePicker.Builder<Long>
    private lateinit var materialBillDatePicker : MaterialDatePicker<Long>
    private lateinit var materialDeliveryDateBuilder : MaterialDatePicker.Builder<Long>
    private lateinit var materialDeliveryDatePicker : MaterialDatePicker<Long>
    private var invoiceId = 0
    private var orderSubTotal = 0.00
    private var orderTotalTax = 0.00
    private var orderGrandTotal = 0.00
    private var parentId = 0
    private lateinit var tenderList : ArrayList<CommonDropDown>
    private lateinit var tenderSpinner : ArrayAdapter<CommonDropDown>
    private lateinit var tender : CommonDropDown
    private lateinit var paymentListAdapter : PaymentListAdapter
    private var orderPayTotal = 0.00
    private lateinit var invoiceAddItemAdapter : AddInvoiceItemAdapter
    private var statusId = 0
    private var poId = 0
    private var vendorId = 0
    private var paymentId = 0
    private var isFromOrder = false


    companion object {
        lateinit var Instance : AddInvoiceActivity
    }

    init {
        Instance = this
    }

    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddInvoiceBinding.inflate(layoutInflater)
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
        invoiceId = intent.extras?.getInt(Default.ID)!!
        orderSubTotal = 0.00
        orderTotalTax = 0.00
        orderGrandTotal = 0.00
        orderPayTotal = 0.00
        binding.apply {
            layoutToolbar.imageViewAction.setImageDrawable(ContextCompat.getDrawable(this@AddInvoiceActivity, R.drawable.ic_save))
            layoutToolbar.imageViewBack.visibility = View.VISIBLE
        }
        tenderList = ArrayList()
        tenderSpinner = ArrayAdapter(this, R.layout.spinner_items, tenderList)
        suggestPurchaseOrderAdapter = AutoPurchaseOrderAdapter(this, android.R.layout.simple_spinner_dropdown_item, ArrayList())
        purchaseOrderDetails = PurchaseOrder()

        //Today Date to Before is Disable
        val constraintsBuilder = CalendarConstraints.Builder().setValidator(DateValidatorPointForward.now())
        materialBillDateBuilder = MaterialDatePicker.Builder.datePicker().setInputMode(MaterialDatePicker.INPUT_MODE_CALENDAR)
        materialBillDatePicker = materialBillDateBuilder.build()
        materialDeliveryDateBuilder = MaterialDatePicker.Builder.datePicker().setInputMode(MaterialDatePicker.INPUT_MODE_CALENDAR)
        materialDeliveryDatePicker = materialDeliveryDateBuilder.setCalendarConstraints(constraintsBuilder.build()).build()
        paymentListAdapter = PaymentListAdapter {
            paymentListAdapter.apply {
                list.removeAt(it)
                notifyItemRemoved(it)
                notifyItemRangeChanged(it, list.size)
                calculatePayTotal()
            }
        }
        calculatePayTotal()
        invoiceAddItemAdapter = AddInvoiceItemAdapter()
        calculateTotal()
    }

    private fun postInitView() {
        binding.apply {
            layoutToolbar.textViewToolbarName.text = resources.getString(R.string.Invoice, if(invoiceId > 0) resources.getString(R.string.lbl_Edit) else resources.getString(R.string.lbl_Add))
            header = Header(resources.getString(R.string.lbl_SrNumber), resources.getString(R.string.hint_ItemName), resources.getString(R.string.lbl_Quantity), resources.getString(R.string.hint_Cost), resources.getString(R.string.Menu_Tax), resources.getString(R.string.lbl_Total), resources.getString(R.string.lbl_Action))
            payment = Header(resources.getString(R.string.lbl_SrNumber), resources.getString(R.string.Menu_Tender), resources.getString(R.string.lbl_Amount), resources.getString(R.string.lbl_Action))
            val startDate = Constants.dateFormat_MMM_dd_yyyy.format(now())
            val endDate = Constants.dateFormat_MMM_dd_yyyy.format(1.toDay().sinceNow)
            textInputBillDate.setText(startDate)
            textInputDueDate.setText(endDate)
            textInputOrderSearch.setAdapter(suggestPurchaseOrderAdapter)
            textInputOrderSearch.threshold = 0
            recycleViewPaymentList.apply {
                adapter = paymentListAdapter
                layoutManager = LinearLayoutManager(this@AddInvoiceActivity)
            }
            recycleViewAddItemPurchase.apply {
                adapter = invoiceAddItemAdapter
                layoutManager = LinearLayoutManager(this@AddInvoiceActivity)
            }
            id = invoiceId
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

        viewModelDropdown.getTenderDropDown().observe(this) {
            CoroutineScope(Dispatchers.Main).launch {
                tenderSpinner.apply {
                    setDropDownViewResource(R.layout.spinner_dropdown_item)
                    tender = CommonDropDown(label = resources.getString(R.string.lbl_SelectTender), value = 0)
                    add(tender)
                    addAll(it)
                    notifyDataSetChanged()
                }
            }
        }

        if(invoiceId > 0) {
            viewModel.getInvoiceDetail(invoiceId).observe(this) {
                CoroutineScope(Dispatchers.Main).launch {
                    if(it != null) {
                        binding.apply {
                            statusId = it.status!!
                            poId = it.poid!!
                            vendorId = it.vendorId!!
                            paymentId = it.paymentStatus!!
                            textInputBillDate.setText(Constants.dateFormat_MMM_dd_yyyy.format(Utils.convertStringToDate(formatter = Constants.dateFormatT, parseDate = it.date)))
                            textInputDueDate.setText(Constants.dateFormat_MMM_dd_yyyy.format(Utils.convertStringToDate(formatter = Constants.dateFormatT, parseDate = it.dueDate)))
                            textInputInvoiceId.setText(if(it.invoiceNo!!.isNotEmpty()) it.invoiceNo else "")
                            textInputVendorName.setText(if(it.sVendor!!.isNotEmpty()) it.sVendor else "")
                            if(it.details.size > 0) {
                                invoiceAddItemAdapter.apply {
                                    list.clear()
                                    it.details.forEach { detail-> if(detail.tax == null) detail.tax = 0.00 }
                                    setList(it.details)
                                    calculateTotal()
                                }
                            }
                            if(it.payment.size > 0) {
                                paymentListAdapter.apply {
                                    list.clear()
                                    setList(it.payment)
                                    calculatePayTotal()
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    //endregion

    //region To manage click events
    private fun manageClicks() {
        binding.apply {
            layoutToolbar.imageViewBack.clickWithDebounce {
                manageBack()
            }

            buttonAddPay.clickWithDebounce {
                val amount = textInputAmount.text.toString().trim()
                if(!TextUtils.isEmpty(amount) && tender.value!! > 0) {
                    paymentListAdapter.apply {
                        addPayList(
                            PaymentInvoice(
                                invoiceId = 0,
                                tenderType = tender.value,
                                description = "",
                                amount = amount.toDouble(),
                                status = 11,
                                sTender = tender.label,
                                paymentDate = Constants.dateFormatTZ.format(now())
                            )
                        )
                        calculatePayTotal()
                    }
                } else showSweetDialog(this@AddInvoiceActivity, resources.getString(R.string.Validation, if(tender.value!! == 0) resources.getString(R.string.Menu_Tender) else resources.getString(R.string.lbl_Amount)))
                textInputAmount.text!!.clear()
            }

            textInputOrderSearch.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s : CharSequence?, start : Int, count : Int, after : Int) {}

                override fun onTextChanged(s : CharSequence?, start : Int, before : Int, count : Int) {
                    if(!s.isNullOrEmpty()) fetchSuggestionsPOrder(s.toString())
                }

                override fun afterTextChanged(s : Editable?) {}
            })

            textInputOrderSearch.setOnItemClickListener { parent, _, position, _->
                purchaseOrderDetails = parent.getItemAtPosition(position) as PurchaseOrder
                textInputPurchaseId.setText(if(purchaseOrderDetails.purchaseOrderNo!!.isNotEmpty()) purchaseOrderDetails.purchaseOrderNo else "")
                textInputVendorName.setText(if(purchaseOrderDetails.sVendor!!.isNotEmpty()) purchaseOrderDetails.sVendor else "")
                if(purchaseOrderDetails.details.size > 0) {
                    val invoiceList = ArrayList<DetailsInvoice>()
                    for(data in purchaseOrderDetails.details) {
                        invoiceList.add(DetailsInvoice(poDetailId = data.id, itemId = data.itemId, quantity = data.quantity, specificationId = data.specificationId, amount = data.lastCost, totalAmount = data.total, uom = "", itemName = data.sItemName, tax = data.tax))
                    }
                    invoiceAddItemAdapter.apply {
                        list.clear()
                        notifyDataSetChanged()
                        if(invoiceList.size > 0) setList(invoiceList)
                        calculateTotal()
                    }
                }
                textInputOrderSearch.text.clear()
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
                textInputDueDate.setText(formatted)
            }

            textInputBillDate.clickWithDebounce { //setStartEndDate(startCalendar, binding.textViewBirthDate)
                materialBillDatePicker.show(supportFragmentManager, "MATERIAL_DATE_PICKER")
            }

            textInputDueDate.clickWithDebounce { //setStartEndDate(startCalendar, binding.textViewBirthDate)
                materialDeliveryDatePicker.show(supportFragmentManager, "MATERIAL_DATE_PICKER")
            }

            spinnerTender.apply {
                adapter = tenderSpinner
                setSpinnerEventsListener(object : CustomSpinner.OnSpinnerEventsListener {
                    override fun onSpinnerOpened(spinner : Spinner?) {

                    }

                    override fun onSpinnerClosed(spinner : Spinner?) {
                        tender = spinner?.selectedItem as CommonDropDown
                    }
                })
            }

            layoutToolbar.imageViewAction.clickWithDebounce {
                if(paymentListAdapter.list.size > 0) {
                    if(invoiceId > 0) updateInvoice(paymentListAdapter.list)
                    else insertInvoice(paymentListAdapter.list)
                } else {
                    hideKeyBoard(this@AddInvoiceActivity)
                    val ft = supportFragmentManager.beginTransaction()
                    val dialogFragment = DeleteAlertPopupDialog(resources.getString(R.string.lbl_PaymentPermission), 0, isInsertInvoice = true)
                    val prevFragment : Fragment? = supportFragmentManager.findFragmentByTag(DeleteAlertPopupDialog::class.java.name)
                    if(prevFragment != null) return@clickWithDebounce
                    dialogFragment.isCancelable = false
                    dialogFragment.show(ft, DeleteAlertPopupDialog::class.java.name)
                    dialogFragment.setListener(object : DeleteAlertPopupDialog.OnButtonClickListener {
                        override fun onButtonClickListener(typeButton : Enums.ClickEvents) {
                            when(typeButton) {
                                Enums.ClickEvents.DELETE -> {
                                    if(invoiceId > 0) updateInvoice(ArrayList())
                                    else insertInvoice(ArrayList())
                                }

                                else -> {}
                            }
                        }
                    })
                }
            }
        }
    } //endregion

    //update Invoice
    private fun updateInvoice(paymentArray : ArrayList<PaymentInvoice>) {
        val req = Invoice(
            id = invoiceId,
            poid = poId,
            vendorId = vendorId,
            date = Constants.dateFormatTZ.format(Utils.convertStringToDate(formatter = Constants.dateFormat_MMM_dd_yyyy, parseDate = binding.textInputBillDate.text.toString())),
            dueDate = Constants.dateFormatTZ.format(Utils.convertStringToDate(formatter = Constants.dateFormat_MMM_dd_yyyy, parseDate = binding.textInputDueDate.text.toString())),
            subTotal = orderSubTotal,
            tax = orderTotalTax,
            grandTotal = orderGrandTotal,
            discountAmount = 0.00,
            status = statusId,
            details = invoiceAddItemAdapter.list,
            paymentStatus = if(paymentListAdapter.list.size > 0) {
                if(orderGrandTotal <= orderPayTotal) Default.PAID else Default.PARTIAL
            } else Default.UNPAID,
            payment = paymentArray
        )
        viewModel.updateInvoice(req).observe(this@AddInvoiceActivity) {
            CoroutineScope(Dispatchers.Main).launch {
                if(it) manageBack()

            }
        }
    }

    private fun insertInvoice(paymentArray : ArrayList<PaymentInvoice>) {
        val req = Invoice(
            poid = purchaseOrderDetails.id!!,
            vendorId = purchaseOrderDetails.vendorId!!,
            date = Constants.dateFormatTZ.format(Utils.convertStringToDate(formatter = Constants.dateFormat_MMM_dd_yyyy, parseDate = binding.textInputBillDate.text.toString())),
            dueDate = Constants.dateFormatTZ.format(Utils.convertStringToDate(formatter = Constants.dateFormat_MMM_dd_yyyy, parseDate = binding.textInputDueDate.text.toString())),
            subTotal = orderSubTotal,
            tax = orderTotalTax,
            grandTotal = orderGrandTotal,
            discountAmount = 0.00,
            details = invoiceAddItemAdapter.list,
            paymentStatus = if(orderGrandTotal <= orderPayTotal) Default.PAID else Default.PARTIAL,
            status = Default.ORDER_PLACED,
            payment = paymentArray
        )
        viewModel.insertInvoice(req).observe(this@AddInvoiceActivity) {
            CoroutineScope(Dispatchers.Main).launch {
                if(it) manageBack()
            }
        }
    }

    @Deprecated("Deprecated in Java") override fun onBackPressed() {
        manageBack()
        onBackPressedDispatcher.onBackPressed() //with this line
    }

    //Get Auto complete search purchase order
    private fun fetchSuggestionsPOrder(str : String) {
        viewModel.fetchAutoPurchaseOrderList(str).observe(this) {
            CoroutineScope(Dispatchers.Main).launch {
                suggestPurchaseOrderAdapter.apply {
                    clear()
                    addAll(it)
                    notifyDataSetChanged()
                    binding.textInputOrderSearch.showDropDown()
                }
            }
        }
    }

    //region Calculate Totals
    private fun calculateTotal() {
        invoiceAddItemAdapter.apply {
            orderGrandTotal = list.sumOf { it.totalAmount!! }
            orderSubTotal = list.sumOf { it.amount!! }
            orderTotalTax = list.sumOf { it.tax!! }
            binding.apply {
                subTotal = Utils.setAmountWithCurrency(this@AddInvoiceActivity, orderSubTotal)
                totalTax = Utils.setAmountWithCurrency(this@AddInvoiceActivity, orderTotalTax)
                val qty = list.sumOf { it.quantity!! }
                totalQty = if(qty.toString().length > 1) qty.toString() else "0$qty"
                grandTotal = Utils.setAmountWithCurrency(this@AddInvoiceActivity, orderGrandTotal)
            }
        }
    }//endregion

    //region Calculate Totals
    private fun calculatePayTotal() {
        paymentListAdapter.apply {
            orderPayTotal = list.sumOf { it.amount!! }
            binding.apply {
                totalPay = Utils.setAmountWithCurrency(this@AddInvoiceActivity, orderPayTotal)
            }
        }
    }//endregion

    //Manage Back and Insert/Update
    private fun manageBack() {
        val intent = Intent(this@AddInvoiceActivity, InvoiceActivity::class.java)
        intent.putExtra(Default.PARENT_ID, parentId)
        intent.putExtra(Default.ORDER, isFromOrder)
        openActivity(intent)
    }//endregion
}