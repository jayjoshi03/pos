package com.varitas.gokulpos.activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import cn.pedant.SweetAlert.SweetAlertDialog
import com.google.android.material.datepicker.MaterialDatePicker
import com.varitas.gokulpos.R
import com.varitas.gokulpos.adapter.AddInvoiceItemAdapter
import com.varitas.gokulpos.adapter.AutoCompletePurchaseAdapter
import com.varitas.gokulpos.databinding.ActivityAddinvoiceBinding
import com.varitas.gokulpos.model.Header
import com.varitas.gokulpos.response.DetailsInvoice
import com.varitas.gokulpos.response.Invoice
import com.varitas.gokulpos.response.PaymentInvoice
import com.varitas.gokulpos.response.PurchaseOrder
import com.varitas.gokulpos.utilities.Constants
import com.varitas.gokulpos.utilities.Default
import com.varitas.gokulpos.utilities.Utils
import com.varitas.gokulpos.viewmodel.OrdersViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.moallemi.tools.extension.date.now
import me.moallemi.tools.extension.date.toDay
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.TimeZone

class AddInvoiceActivity : BaseActivity() {
    lateinit var binding : ActivityAddinvoiceBinding
    val viewModel : OrdersViewModel by viewModels()
    private lateinit var suggestPurchaseOrderAdapter : AutoCompletePurchaseAdapter
    private var parentId = 0
    private lateinit var purchaseOrderDetails : PurchaseOrder
    private lateinit var invoiceAddItemAdapter : AddInvoiceItemAdapter
    private var invoiceDetail : Invoice? = null
    private var orderSubTotal = 0.00
    private var orderTotalTax = 0.00
    private var orderGrandTotal = 0.00
    private lateinit var materialBillDateBuilder : MaterialDatePicker.Builder<Long>
    private lateinit var materialBillDatePicker : MaterialDatePicker<Long>
    private lateinit var materialDueDateBuilder : MaterialDatePicker.Builder<Long>
    private lateinit var materialDueDatePicker : MaterialDatePicker<Long>
    private var statusId = 0
    private var poId = 0
    private var vendorId = 0

    companion object {
        lateinit var Instance : AddInvoiceActivity
    }

    init {
        Instance = this
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddinvoiceBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setSupportActionBar(binding.layoutToolbar.toolbarCommon)
        initData()
        postInitView()
        loadData()
        manageClicks()
    }

    //region To init data
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun initData() {
        if(intent.extras?.getParcelable<Invoice>(Default.INVOICE) != null) {
            invoiceDetail = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) intent.extras?.getParcelable(Default.INVOICE, Invoice::class.java)!!
            else intent.extras?.getParcelable(Default.INVOICE)!!
        }
        parentId = if(intent.extras?.getInt(Default.PARENT_ID) != null) intent.extras?.getInt(Default.PARENT_ID)!! else 0
        orderSubTotal = 0.00
        orderTotalTax = 0.00
        orderGrandTotal = 0.00
        binding.apply {
            layoutToolbar.textViewToolbarName.text = resources.getString(R.string.Menu_Invoice)
            layoutToolbar.imageViewAction.setImageDrawable(ContextCompat.getDrawable(this@AddInvoiceActivity, R.drawable.ic_save))
            layoutToolbar.imageViewBack.visibility = View.VISIBLE
        }
        suggestPurchaseOrderAdapter = AutoCompletePurchaseAdapter(this, android.R.layout.simple_spinner_dropdown_item, ArrayList())
        purchaseOrderDetails = PurchaseOrder()
        materialBillDateBuilder = MaterialDatePicker.Builder.datePicker().setInputMode(MaterialDatePicker.INPUT_MODE_CALENDAR)
        materialBillDatePicker = materialBillDateBuilder.build()
        materialDueDateBuilder = MaterialDatePicker.Builder.datePicker().setInputMode(MaterialDatePicker.INPUT_MODE_CALENDAR)
        materialDueDatePicker = materialBillDateBuilder.build()
        invoiceAddItemAdapter = AddInvoiceItemAdapter {
            invoiceAddItemAdapter.apply {
//                list.removeAt(it)
//                notifyItemRemoved(it)
//                notifyItemRangeChanged(it, list.size)
//                calculateTotal()
            }
        }
        calculateTotal()
    }

    private fun postInitView() {
        binding.apply {
            recycleViewAddItem.apply {
                adapter = invoiceAddItemAdapter
                layoutManager = LinearLayoutManager(this@AddInvoiceActivity)
            }
            editTextOrderSearch.setAdapter(suggestPurchaseOrderAdapter)
            editTextOrderSearch.threshold = 0
            header = Header(resources.getString(R.string.lbl_SrNumber), resources.getString(R.string.hint_ItemName), resources.getString(R.string.lbl_Qty))
            val selectDate = Constants.dateFormat_MMM_dd_yyyy.format(now())
            val selectDueDate = Constants.dateFormat_MMM_dd_yyyy.format(1.toDay().sinceNow)
            editTextBillDate.setText(selectDate)
            editTextDueDate.setText(selectDueDate)
            id = if(invoiceDetail != null) invoiceDetail!!.id else 0
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

        binding.apply {
            if(invoiceDetail != null) {
                editTextBillDate.setText(Constants.dateFormat_MMM_dd_yyyy.format(Utils.convertStringToDate(formatter = Constants.dateFormatT, parseDate = invoiceDetail!!.date)))
                editTextDueDate.setText(Constants.dateFormat_MMM_dd_yyyy.format(Utils.convertStringToDate(formatter = Constants.dateFormatT, parseDate = invoiceDetail!!.dueDate)))
                editTextInvoiceId.setText(if(invoiceDetail!!.invoiceNo!!.isNotEmpty()) invoiceDetail!!.invoiceNo else "")
                editTextVendorName.setText(if(invoiceDetail!!.sVendor!!.isNotEmpty()) invoiceDetail!!.sVendor else "")
                if(invoiceDetail!!.details.size > 0) {
                    invoiceAddItemAdapter.apply {
                        list.clear()
                        invoiceDetail!!.details.forEach { detail-> if(detail.tax == null) detail.tax = 0.00 }
                        setList(invoiceDetail!!.details)
                        calculateTotal()
                    }
                }
                statusId = invoiceDetail!!.status!!
                poId = invoiceDetail!!.poid!!
                vendorId = invoiceDetail!!.vendorId!!
            }
        }
    }
    //endregion

    //region To manage click
    private fun manageClicks() {
        binding.apply {
            layoutToolbar.imageViewBack.clickWithDebounce {
                val intent = Intent(this@AddInvoiceActivity, InvoiceActivity::class.java)
                intent.putExtra(Default.PARENT_ID, parentId)
                openActivity(intent)
            }

            editTextOrderSearch.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s : CharSequence?, start : Int, count : Int, after : Int) {}

                override fun onTextChanged(s : CharSequence?, start : Int, before : Int, count : Int) {
                    if(!s.isNullOrEmpty()) fetchSuggestionsPOrder(s.toString())
                }

                override fun afterTextChanged(s : Editable?) {}
            })

            editTextOrderSearch.setOnItemClickListener { parent, _, position, _->
                purchaseOrderDetails = parent.getItemAtPosition(position) as PurchaseOrder
                editTextPurchaseNo.setText(if(purchaseOrderDetails.purchaseOrderNo!!.isNotEmpty()) purchaseOrderDetails.purchaseOrderNo else "")
                editTextVendorName.setText(if(purchaseOrderDetails.sVendor!!.isNotEmpty()) purchaseOrderDetails.sVendor else "")
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
                editTextOrderSearch.text.clear()
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

            materialDueDatePicker.addOnPositiveButtonClickListener {
                val utc = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
                utc.timeInMillis = it
                val format = SimpleDateFormat("MMM dd, yyyy")
                val formatted : String = format.format(utc.time)
                editTextDueDate.setText(formatted)
            }

            editTextDueDate.clickWithDebounce { //setStartEndDate(startCalendar, binding.textViewBirthDate)
                materialDueDatePicker.show(supportFragmentManager, "MATERIAL_DATE_PICKER")
            }

            layoutToolbar.imageViewAction.clickWithDebounce {
                if(invoiceAddItemAdapter.list.size > 0) {
                    if(invoiceDetail != null) {
                        if(invoiceDetail!!.payment.size > 0) updateInvoice(invoiceDetail!!.payment)
                        else {
                            hideKeyBoard(this@AddInvoiceActivity)
                            val sweetAlertDialog = SweetAlertDialog(this@AddInvoiceActivity, SweetAlertDialog.CUSTOM_IMAGE_TYPE)
                            sweetAlertDialog.apply {
                                setCanceledOnTouchOutside(false)
                                setCancelable(false)
                                contentText = resources.getString(R.string.lbl_PaymentPermission)
                                confirmText = resources.getString(R.string.lbl_Yes)
                                cancelText = resources.getString(R.string.lbl_No)
                                confirmButtonBackgroundColor = ContextCompat.getColor(this@AddInvoiceActivity, R.color.base_color)
                                cancelButtonBackgroundColor = ContextCompat.getColor(this@AddInvoiceActivity, R.color.pink)
                                setConfirmClickListener { sDialog->
                                    sDialog.dismissWithAnimation()
                                    updateInvoice(ArrayList())
                                }
                                setCancelClickListener { sDialog->
                                    sDialog.dismissWithAnimation()
                                }
                                show()
                            }
                        }
                    } else {
                        hideKeyBoard(this@AddInvoiceActivity)
                        val sweetAlertDialog = SweetAlertDialog(this@AddInvoiceActivity, SweetAlertDialog.CUSTOM_IMAGE_TYPE)
                        sweetAlertDialog.apply {
                            setCanceledOnTouchOutside(false)
                            setCancelable(false)
                            contentText = resources.getString(R.string.lbl_PaymentPermission)
                            confirmText = resources.getString(R.string.lbl_Yes)
                            cancelText = resources.getString(R.string.lbl_No)
                            confirmButtonBackgroundColor = ContextCompat.getColor(this@AddInvoiceActivity, R.color.base_color)
                            cancelButtonBackgroundColor = ContextCompat.getColor(this@AddInvoiceActivity, R.color.pink)
                            setConfirmClickListener { sDialog->
                                sDialog.dismissWithAnimation()
                                val req = Invoice(
                                    poid = purchaseOrderDetails.id!!,
                                    vendorId = purchaseOrderDetails.vendorId!!,
                                    date = Constants.dateFormatTZ.format(Utils.convertStringToDate(formatter = Constants.dateFormat_MMM_dd_yyyy, parseDate = editTextBillDate.text.toString())),
                                    dueDate = Constants.dateFormatTZ.format(Utils.convertStringToDate(formatter = Constants.dateFormat_MMM_dd_yyyy, parseDate = editTextDueDate.text.toString())),
                                    subTotal = orderSubTotal,
                                    tax = orderTotalTax,
                                    grandTotal = orderGrandTotal,
                                    discountAmount = 0.00,
                                    details = invoiceAddItemAdapter.list,
                                    paymentStatus = Default.UNPAID,
                                    payment = ArrayList()
                                )
                                viewModel.insertInvoice(req).observe(this@AddInvoiceActivity) {
                                    CoroutineScope(Dispatchers.Main).launch {
                                        if(it) {
                                            val intent = Intent(this@AddInvoiceActivity, InvoiceActivity::class.java)
                                            intent.putExtra(Default.PARENT_ID, parentId)
                                            openActivity(intent)
                                        }
                                    }
                                }
                            }
                            setCancelClickListener { sDialog->
                                sDialog.dismissWithAnimation()
                            }
                            show()
                        }
                    }
                } else showSweetDialog(this@AddInvoiceActivity, resources.getString(R.string.lbl_POValidation))
            }
            buttonPayment.clickWithDebounce {
                var req = Invoice()
                if(orderGrandTotal > 0.00) {
                    if(invoiceDetail != null) {
                        req = Invoice(
                            id = invoiceDetail!!.id!!,
                            invoiceNo = invoiceDetail!!.invoiceNo,
                            poid = invoiceDetail!!.poid!!,
                            vendorId = invoiceDetail!!.vendorId!!,
                            sVendor = invoiceDetail!!.sVendor,
                            date = Constants.dateFormatTZ.format(Utils.convertStringToDate(formatter = Constants.dateFormat_MMM_dd_yyyy, parseDate = editTextBillDate.text.toString())),
                            dueDate = Constants.dateFormatTZ.format(Utils.convertStringToDate(formatter = Constants.dateFormat_MMM_dd_yyyy, parseDate = editTextDueDate.text.toString())),
                            subTotal = invoiceDetail!!.subTotal,
                            tax = invoiceDetail!!.tax,
                            grandTotal = invoiceDetail!!.grandTotal,
                            discountAmount = invoiceDetail!!.discountAmount,
                            status = invoiceDetail!!.status,
                            details = invoiceAddItemAdapter.list,
                            paymentStatus = invoiceDetail!!.paymentStatus,
                            payment = invoiceDetail!!.payment
                        )
                    } else {
                        req = Invoice(
                            id = 0,
                            poid = purchaseOrderDetails.id!!,
                            vendorId = purchaseOrderDetails.vendorId!!,
                            date = Constants.dateFormatTZ.format(Utils.convertStringToDate(formatter = Constants.dateFormat_MMM_dd_yyyy, parseDate = editTextBillDate.text.toString())),
                            dueDate = Constants.dateFormatTZ.format(Utils.convertStringToDate(formatter = Constants.dateFormat_MMM_dd_yyyy, parseDate = editTextDueDate.text.toString())),
                            subTotal = orderSubTotal,
                            tax = orderTotalTax,
                            grandTotal = orderGrandTotal,
                            discountAmount = 0.00,
                            details = invoiceAddItemAdapter.list,
                            paymentStatus = Default.UNPAID,
                            payment = ArrayList()
                        )
                    }
                    val intent = Intent(this@AddInvoiceActivity, PaymentActivity::class.java)
                    intent.putExtra(Default.PARENT_ID, parentId)
                    intent.putExtra(Default.INVOICE, req)
                    openActivity(intent)
                } else showSweetDialog(this@AddInvoiceActivity, resources.getString(R.string.lbl_ValidPayment))
            }
        }
    }
    //endregion

    //update Invoice
    private fun updateInvoice(paymentArray : ArrayList<PaymentInvoice>) {
        val req = Invoice(
            id = invoiceDetail!!.id,
            poid = poId,
            vendorId = vendorId,
            date = Constants.dateFormatTZ.format(Utils.convertStringToDate(formatter = Constants.dateFormat_MMM_dd_yyyy, parseDate = binding.editTextBillDate.text.toString())),
            dueDate = Constants.dateFormatTZ.format(Utils.convertStringToDate(formatter = Constants.dateFormat_MMM_dd_yyyy, parseDate = binding.editTextDueDate.text.toString())),
            subTotal = orderSubTotal,
            tax = orderTotalTax,
            grandTotal = orderGrandTotal,
            discountAmount = 0.00,
            status = statusId,
            details = invoiceAddItemAdapter.list,
            paymentStatus = invoiceDetail!!.paymentStatus,
            payment = paymentArray
        )
        viewModel.updateInvoice(req).observe(this@AddInvoiceActivity) {
            CoroutineScope(Dispatchers.Main).launch {
                if(it) {
                    val intent = Intent(this@AddInvoiceActivity, InvoiceActivity::class.java)
                    intent.putExtra(Default.PARENT_ID, parentId)
                    openActivity(intent)
                }
            }
        }
    }

    //Get Auto complete search purchase order
    private fun fetchSuggestionsPOrder(str : String) {
        viewModel.fetchAutoCompletePurchaseOrder(str).observe(this) {
            CoroutineScope(Dispatchers.Main).launch {
                suggestPurchaseOrderAdapter.apply {
                    clear()
                    addAll(it)
                    notifyDataSetChanged()
                    binding.editTextOrderSearch.showDropDown()
                }
            }
        }
    }

    //region Calculate Totals
    private fun calculateTotal() {
        invoiceAddItemAdapter.apply {
            orderGrandTotal = list.sumOf { it.totalAmount!! }
            orderSubTotal = list.sumOf { it.amount!!.times(it.quantity!!) }
            orderTotalTax = list.sumOf { it.tax!! }
            binding.apply {
                lableSubTotal = "${resources.getString(R.string.lbl_Subtotal)}: "
                subTotal = Utils.setAmountWithCurrency(this@AddInvoiceActivity, orderSubTotal)
                lableTax = "${resources.getString(R.string.lbl_TotalTax)}: "
                totalTax = Utils.setAmountWithCurrency(this@AddInvoiceActivity, orderTotalTax)
                lableQty = "${resources.getString(R.string.hint_TotalQty)}: "
                val qty = list.sumOf { it.quantity!! }
                totalQty = if(qty.toString().length > 1) qty.toString() else "0$qty"
                lableGrandTotal = "${resources.getString(R.string.lbl_GrandTotal)}: "
                grandTotal = Utils.setAmountWithCurrency(this@AddInvoiceActivity, orderGrandTotal)
            }
        }
    }//endregion

    @Deprecated("Deprecated in Java") override fun onBackPressed() {
        val intent = Intent(this@AddInvoiceActivity, InvoiceActivity::class.java)
        intent.putExtra(Default.PARENT_ID, parentId)
        openActivity(intent)
        onBackPressedDispatcher.onBackPressed() //with this line
    }
}
