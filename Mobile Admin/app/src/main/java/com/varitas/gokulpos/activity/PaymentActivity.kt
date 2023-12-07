package com.varitas.gokulpos.activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.varitas.gokulpos.R
import com.varitas.gokulpos.adapter.PaymentListAdapter
import com.varitas.gokulpos.databinding.ActivityPaymentBinding
import com.varitas.gokulpos.model.Header
import com.varitas.gokulpos.response.CommonDropDown
import com.varitas.gokulpos.response.Invoice
import com.varitas.gokulpos.response.PaymentInvoice
import com.varitas.gokulpos.utilities.Constants
import com.varitas.gokulpos.utilities.CustomSpinner
import com.varitas.gokulpos.utilities.Default
import com.varitas.gokulpos.utilities.Utils
import com.varitas.gokulpos.viewmodel.OrdersViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.moallemi.tools.extension.date.now

class PaymentActivity : BaseActivity() {
    lateinit var binding : ActivityPaymentBinding
    val viewModel : OrdersViewModel by viewModels()
    private var parentId = 0
    private lateinit var tenderList : ArrayList<CommonDropDown>
    private lateinit var tenderSpinner : ArrayAdapter<CommonDropDown>
    private lateinit var tender : CommonDropDown
    private lateinit var paymentListAdapter : PaymentListAdapter
    private var orderPayTotal = 0.00
    private var paymentList : Invoice? = null

    companion object {
        lateinit var Instance : PaymentActivity
    }

    init {
        Instance = this
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPaymentBinding.inflate(layoutInflater)
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
        orderPayTotal = 0.00
        if(intent.extras?.getParcelable<Invoice>(Default.INVOICE) != null) {
            paymentList = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) intent.extras?.getParcelable(Default.INVOICE, Invoice::class.java)!!
            else intent.extras?.getParcelable(Default.INVOICE)!!
        }
        parentId = if(intent.extras?.getInt(Default.PARENT_ID) != null) intent.extras?.getInt(Default.PARENT_ID)!! else 0
        tenderList = ArrayList()
        tenderSpinner = ArrayAdapter(this, R.layout.spinner_items, tenderList)
        paymentListAdapter = PaymentListAdapter {
            paymentListAdapter.apply {
                list.removeAt(it)
                notifyItemRemoved(it)
                notifyItemRangeChanged(it, list.size)
                calculateTotal()
            }
        }
        calculateTotal()
    }

    private fun postInitView() {
        binding.apply {
            layoutToolbar.textViewToolbarName.text = resources.getString(R.string.button_Payment)
            layoutToolbar.imageViewAction.setImageDrawable(ContextCompat.getDrawable(this@PaymentActivity, R.drawable.ic_save))
            layoutToolbar.imageViewBack.visibility = View.VISIBLE
            header = Header(resources.getString(R.string.lbl_SrNumber), resources.getString(R.string.lbl_Tender), resources.getString(R.string.hint_Amount))

            recycleViewPaymentList.apply {
                adapter = paymentListAdapter
                layoutManager = LinearLayoutManager(this@PaymentActivity)
            }
            lableGrandTotal = "${resources.getString(R.string.lbl_GrandTotal)}: "
            grandTotal = Utils.setAmountWithCurrency(this@PaymentActivity, paymentList!!.grandTotal!!)
        }
    }
    //endregion

    //region To manage click
    private fun manageClicks() {
        binding.apply {
            layoutToolbar.imageViewBack.clickWithDebounce {
                val intent = Intent(this@PaymentActivity, AddInvoiceActivity::class.java)
                intent.putExtra(Default.PARENT_ID, parentId)
                intent.putExtra(Default.INVOICE, paymentList)
                openActivity(intent)
            }

            buttonAddPay.clickWithDebounce {
                val amount = editTextAmount.text.toString().trim()
                if(!TextUtils.isEmpty(amount) && tender.value!! > 0) {
                    paymentListAdapter.apply {
                        addPayList(
                            PaymentInvoice(
                                invoiceId = 0,
                                tenderType = tender.value,
                                description = "",
                                amount = amount.toDouble(),
                                status = Default.PARTIAL,
                                sTender = tender.label,
                                paymentDate = Constants.dateFormatTZ.format(now())
                            )
                        )
                        calculateTotal()
                    }
                } else showSweetDialog(this@PaymentActivity, resources.getString(R.string.Validation, if(tender.value!! == 0) resources.getString(R.string.lbl_Tender) else resources.getString(R.string.hint_Amount)))
                editTextAmount.text!!.clear()
            }

            layoutToolbar.imageViewAction.clickWithDebounce {
                if(paymentList!!.id!! > 0) {
                    val req = Invoice(
                        id = paymentList!!.id!!,
                        poid = paymentList!!.poid,
                        vendorId = paymentList!!.vendorId,
                        date = paymentList!!.date,
                        dueDate = paymentList!!.dueDate,
                        subTotal = paymentList!!.subTotal,
                        tax = paymentList!!.tax,
                        grandTotal = paymentList!!.grandTotal,
                        discountAmount = paymentList!!.discountAmount,
                        details = paymentList!!.details,
                        status = paymentList!!.status,
                        paymentStatus = if(paymentListAdapter.list.size > 0) {
                            if(paymentList!!.grandTotal!! <= orderPayTotal) Default.PAID else Default.PARTIAL
                        } else Default.UNPAID,
                        payment = paymentListAdapter.list
                    )
                    viewModel.updateInvoice(req).observe(this@PaymentActivity) {
                        CoroutineScope(Dispatchers.Main).launch {
                            if(it) {
                                val intent = Intent(this@PaymentActivity, InvoiceActivity::class.java)
                                intent.putExtra(Default.PARENT_ID, parentId)
                                openActivity(intent)
                            }
                        }
                    }
                } else {
                    val req = Invoice(
                        poid = paymentList!!.poid,
                        vendorId = paymentList!!.vendorId,
                        date = paymentList!!.date,
                        dueDate = paymentList!!.dueDate,
                        subTotal = paymentList!!.subTotal,
                        tax = paymentList!!.tax,
                        grandTotal = paymentList!!.grandTotal,
                        discountAmount = paymentList!!.discountAmount,
                        details = paymentList!!.details,
                        paymentStatus = if(paymentList!!.grandTotal!! <= orderPayTotal) Default.PAID else Default.PARTIAL,
                        payment = paymentListAdapter.list
                    )
                    viewModel.insertInvoice(req).observe(this@PaymentActivity) {
                        CoroutineScope(Dispatchers.Main).launch {
                            if(it) {
                                val intent = Intent(this@PaymentActivity, InvoiceActivity::class.java)
                                intent.putExtra(Default.PARENT_ID, parentId)
                                openActivity(intent)
                            }
                        }
                    }
                }
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

        viewModel.getTenderDropDown().observe(this) {
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

        if(paymentList!!.payment.size > 0) {
            paymentListAdapter.apply {
                list.clear()
                setList(paymentList!!.payment)
                calculateTotal()
            }
        }
    }

    @Deprecated("Deprecated in Java") override fun onBackPressed() {
        val intent = Intent(this@PaymentActivity, AddInvoiceActivity::class.java)
        intent.putExtra(Default.PARENT_ID, parentId)
        intent.putExtra(Default.INVOICE, paymentList)
        openActivity(intent)
        onBackPressedDispatcher.onBackPressed() //with this line
    }

    //region Calculate Totals
    private fun calculateTotal() {
        paymentListAdapter.apply {
            orderPayTotal = list.sumOf { it.amount!! }
            binding.apply {
                lableTotalPay = "${resources.getString(R.string.lbl_TotalPay)}: "
                totalPay = Utils.setAmountWithCurrency(this@PaymentActivity, orderPayTotal)
            }
        }
    }//endregion

}