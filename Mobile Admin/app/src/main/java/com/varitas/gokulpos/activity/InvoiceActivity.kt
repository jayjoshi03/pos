package com.varitas.gokulpos.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.SearchView
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import cn.pedant.SweetAlert.SweetAlertDialog
import com.varitas.gokulpos.R
import com.varitas.gokulpos.adapter.InvoiceAdapter
import com.varitas.gokulpos.databinding.ActivityCommonBinding
import com.varitas.gokulpos.model.Header
import com.varitas.gokulpos.utilities.Default
import com.varitas.gokulpos.viewmodel.OrdersViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class InvoiceActivity : BaseActivity() {
    lateinit var binding : ActivityCommonBinding
    val viewModel : OrdersViewModel by viewModels()
    private lateinit var invoiceAdapter : InvoiceAdapter
    private var parentId = 0

    companion object {
        lateinit var Instance : InvoiceActivity
    }

    init {
        Instance = this
    }

    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCommonBinding.inflate(layoutInflater)
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
        binding.apply {
            layoutToolbar.textViewToolbarName.text = resources.getString(R.string.Menu_Invoice)
            layoutToolbar.imageViewAction.setImageDrawable(ContextCompat.getDrawable(this@InvoiceActivity, R.drawable.ic_home))
            layoutToolbar.imageViewBack.visibility = View.VISIBLE
        }

        invoiceAdapter = InvoiceAdapter { invoice, i, isFromDelete->
            if(isFromDelete) {
                hideKeyBoard(this)
                val sweetAlertDialog = SweetAlertDialog(this@InvoiceActivity, SweetAlertDialog.CUSTOM_IMAGE_TYPE)
                sweetAlertDialog.apply {
                    setCanceledOnTouchOutside(false)
                    setCancelable(false)
                    contentText = resources.getString(R.string.lbl_DeletePermission, resources.getString(R.string.Menu_Invoice))
                    confirmText = resources.getString(R.string.lbl_Yes)
                    cancelText = resources.getString(R.string.lbl_No)
                    confirmButtonBackgroundColor = ContextCompat.getColor(this@InvoiceActivity, R.color.base_color)
                    cancelButtonBackgroundColor = ContextCompat.getColor(this@InvoiceActivity, R.color.pink)
                    setConfirmClickListener { sDialog->
                        sDialog.dismissWithAnimation()
                        viewModel.deleteInvoice(invoice.id!!).observe(this@InvoiceActivity) {
                            CoroutineScope(Dispatchers.Main).launch {
                                if(it) {
                                    invoiceAdapter.apply {
                                        list.remove(invoice)
                                        filteredList.remove(invoice)
                                        notifyItemRemoved(i)
                                        notifyItemRangeChanged(i, list.size)
                                    }
                                }
                            }
                        }
                    }
                    setCancelClickListener { sDialog->
                        sDialog.dismissWithAnimation()
                    }
                    show()
                }
            } else {
                viewModel.getInvoiceDetail(invoice.id!!).observe(this) {
                    CoroutineScope(Dispatchers.Main).launch {
                        if(it != null) {
                            val intent = Intent(this@InvoiceActivity, AddInvoiceActivity::class.java)
                            intent.putExtra(Default.INVOICE, it)
                            intent.putExtra(Default.PARENT_ID, parentId)
                            openActivity(intent)
                        }
                    }
                }
            }
        }
    }

    private fun postInitView() {
        binding.apply {
            recycleViewProducts.apply {
                adapter = invoiceAdapter
                layoutManager = LinearLayoutManager(this@InvoiceActivity)
            }
            layoutHeader.data = Header(resources.getString(R.string.lbl_Id), resources.getString(R.string.Menu_Invoice), resources.getString(R.string.Menu_Vendor))
        }
    }
    //endregion

    //region To manage click
    private fun manageClicks() {
        binding.apply {
            layoutToolbar.imageViewAction.clickWithDebounce {
                openActivity(Intent(this@InvoiceActivity, DashboardActivity::class.java))
            }

            layoutToolbar.imageViewBack.clickWithDebounce {
                moveToProducts(parentId)
            }

            searchProduct.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query : String) : Boolean {
                    return false
                }

                override fun onQueryTextChange(newText : String) : Boolean {
                    invoiceAdapter.getFilter().filter(newText)
                    return true
                }
            })

            fabAdd.setOnClickListener {
                val intent = Intent(this@InvoiceActivity, AddInvoiceActivity::class.java)
                intent.putExtra(Default.PARENT_ID, parentId)
                openActivity(intent)
            }

            swipeRefresh.apply {
                setOnRefreshListener {
                    swipeRefresh.isRefreshing = false
                    getInvoice()
                    clearSearch()
                }
                setColorSchemeColors(ContextCompat.getColor(this@InvoiceActivity, R.color.pink))
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
        getInvoice()
    }

    private fun getInvoice() {
        binding.linearLayoutShimmer.visibility = View.VISIBLE
        viewModel.getInvoiceList().observe(this) {
            CoroutineScope(Dispatchers.Main).launch {
                binding.linearLayoutShimmer.visibility = View.GONE
                invoiceAdapter.apply {
                    setList(it)
                }
            }
        }
    }
    //endregion

    @Deprecated("Deprecated in Java") override fun onBackPressed() {
        moveToProducts(parentId)
        onBackPressedDispatcher.onBackPressed() //with this line
    }

    private fun clearSearch() {
        binding.apply {
            searchProduct.apply {
                setQuery("", false)
                clearFocus()
            }
        }
    }

    override fun onResume() {
        binding.shimmer.startShimmer()
        super.onResume()
    }

    override fun onPause() {
        binding.shimmer.stopShimmer()
        super.onPause()
    }
}