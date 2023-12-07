package com.varitas.gokulpos.tablet.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.SearchView
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.varitas.gokulpos.tablet.R
import com.varitas.gokulpos.tablet.adapter.InvoiceAdapter
import com.varitas.gokulpos.tablet.databinding.ActivityCommonBinding
import com.varitas.gokulpos.tablet.fragmentDialogs.DeleteAlertPopupDialog
import com.varitas.gokulpos.tablet.model.Header
import com.varitas.gokulpos.tablet.utilities.Default
import com.varitas.gokulpos.tablet.utilities.Enums
import com.varitas.gokulpos.tablet.viewmodel.OrdersViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint class InvoiceActivity : BaseActivity() {

    private lateinit var binding : ActivityCommonBinding
    val viewModel : OrdersViewModel by viewModels()
    private lateinit var invoiceAdapter : InvoiceAdapter
    private var parentId = 0
    private var isFromOrder = false

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
        isFromOrder = if(intent.extras?.getBoolean(Default.ORDER) != null) intent.extras?.getBoolean(Default.ORDER)!! else false
        binding.apply {
            layoutToolbar.textViewToolbarName.text = resources.getString(R.string.Menu_Invoice)
            layoutToolbar.imageViewAction.setImageDrawable(ContextCompat.getDrawable(this@InvoiceActivity, R.drawable.ic_home))
            layoutToolbar.imageViewBack.visibility = View.VISIBLE
        }
        invoiceAdapter = InvoiceAdapter { invoice, pos, type->
            when(type) {
                Enums.ClickEvents.DELETE -> {
                    hideKeyBoard(this)
                    val ft = supportFragmentManager.beginTransaction()
                    val dialogFragment = DeleteAlertPopupDialog(resources.getString(R.string.DeletePermission, resources.getString(R.string.Menu_Invoice)), invoice.id!!, isInvoice = true)
                    val prevFragment : Fragment? = supportFragmentManager.findFragmentByTag(DeleteAlertPopupDialog::class.java.name)
                    if(prevFragment != null) return@InvoiceAdapter
                    dialogFragment.isCancelable = false
                    dialogFragment.show(ft, DeleteAlertPopupDialog::class.java.name)
                    dialogFragment.setListener(object : DeleteAlertPopupDialog.OnButtonClickListener {
                        override fun onButtonClickListener(typeButton : Enums.ClickEvents) {
                            when(typeButton) {
                                Enums.ClickEvents.DELETE -> {
                                    invoiceAdapter.apply {
                                        list.remove(invoice)
                                        filteredList.remove(invoice)
                                        notifyItemRemoved(pos)
                                        notifyItemRangeChanged(pos, list.size)
                                    }
                                }

                                else -> {}
                            }
                        }
                    })
                }

                Enums.ClickEvents.EDIT -> manageInvoice(invoice.id!!)

                else -> {}
            }

        }
    }

    private fun postInitView() {
        binding.apply {
            recycleViewProducts.apply {
                adapter = invoiceAdapter
                layoutManager = LinearLayoutManager(this@InvoiceActivity)
            }
            layoutHeader.data = Header(resources.getString(R.string.lbl_Id), resources.getString(R.string.lbl_InvoiceNo), resources.getString(R.string.Menu_Vendor), resources.getString(R.string.lbl_Status), resources.getString(R.string.lbl_Action))
        }
    } //endregion

    //region To manage click events
    private fun manageClicks() {
        binding.apply {
            layoutToolbar.imageViewAction.clickWithDebounce {
                moveToOrder(parentId)
            }

            layoutToolbar.imageViewBack.clickWithDebounce {
                moveToSubMenu(parentId, isFromOrder)
            }

            fabAdd.clickWithDebounce {
                manageInvoice()
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
            swipeRefresh.apply {
                setOnRefreshListener {
                    swipeRefresh.isRefreshing = false
                    getInvoice()
                    clearSearch()
                }
                setColorSchemeColors(ContextCompat.getColor(this@InvoiceActivity, R.color.pink))
            }
        }
    } //endregion

    @Deprecated("Deprecated in Java") override fun onBackPressed() {
        moveToSubMenu(parentId, isFromOrder)
        onBackPressedDispatcher.onBackPressed() //with this line
    }

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
        viewModel.getInvoiceList(Default.All_DATA).observe(this) {
            CoroutineScope(Dispatchers.Main).launch {
                invoiceAdapter.apply {
                    setList(it)
                }
            }
        }
    } //endregion

    //region To manage Invoice
    private fun manageInvoice(id : Int = 0) {
        val intent = Intent(this@InvoiceActivity, AddInvoiceActivity::class.java)
        intent.putExtra(Default.ID, if(id > 0) id else 0)
        intent.putExtra(Default.PARENT_ID, parentId)
        intent.putExtra(Default.ORDER, isFromOrder)
        openActivity(intent)
    }
    //endregion

    private fun clearSearch() {
        binding.apply {
            searchProduct.apply {
                setQuery("", false)
                clearFocus()
            }
        }
    }
}