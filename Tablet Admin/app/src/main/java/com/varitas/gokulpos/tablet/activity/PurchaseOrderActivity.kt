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
import com.varitas.gokulpos.tablet.adapter.PurchaseOrderAdapter
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

@AndroidEntryPoint class PurchaseOrderActivity : BaseActivity() {

    private lateinit var binding : ActivityCommonBinding
    val viewModel : OrdersViewModel by viewModels()
    private lateinit var purchaseOrderAdapter : PurchaseOrderAdapter
    private var parentId = 0
    private var isFromOrder = false

    companion object {
        lateinit var Instance : PurchaseOrderActivity
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
            layoutToolbar.textViewToolbarName.text = resources.getString(R.string.Menu_PurchaseOrder)
            layoutToolbar.imageViewAction.setImageDrawable(ContextCompat.getDrawable(this@PurchaseOrderActivity, R.drawable.ic_home))
            layoutToolbar.imageViewBack.visibility = View.VISIBLE
        }
        purchaseOrderAdapter = PurchaseOrderAdapter { it, pos, type->
            when(type) {
                Enums.ClickEvents.DELETE -> {
                    hideKeyBoard(this)
                    val ft = supportFragmentManager.beginTransaction()
                    val dialogFragment = DeleteAlertPopupDialog(resources.getString(R.string.DeletePermission, resources.getString(R.string.Menu_PurchaseOrder)), it.id!!, isPO = true)
                    val prevFragment : Fragment? = supportFragmentManager.findFragmentByTag(DeleteAlertPopupDialog::class.java.name)
                    if(prevFragment != null) return@PurchaseOrderAdapter
                    dialogFragment.isCancelable = false
                    dialogFragment.show(ft, DeleteAlertPopupDialog::class.java.name)
                    dialogFragment.setListener(object : DeleteAlertPopupDialog.OnButtonClickListener {
                        override fun onButtonClickListener(typeButton : Enums.ClickEvents) {
                            when(typeButton) {
                                Enums.ClickEvents.DELETE -> {
                                    purchaseOrderAdapter.apply {
                                        list.remove(it)
                                        filteredList.remove(it)
                                        notifyItemRemoved(pos)
                                        notifyItemRangeChanged(pos, list.size)
                                    }
                                }

                                else -> {}
                            }
                        }
                    })
                }

                Enums.ClickEvents.EDIT -> {
                    managePurchaseOrder(it.id!!)
                }

                Enums.ClickEvents.INVOICE -> {
                    viewModel.generateInvoice(it.id!!).observe(this@PurchaseOrderActivity) { check->
                        if(check) {
                            purchaseOrderAdapter.apply {
                                it.isMake = true
                                notifyItemChanged(pos)
                            }
                        }
                    }
                }

                else -> {}
            }

        }
    }

    private fun postInitView() {
        binding.apply {
            recycleViewProducts.apply {
                adapter = purchaseOrderAdapter
                layoutManager = LinearLayoutManager(this@PurchaseOrderActivity)
            }
            layoutHeader.root.visibility = View.GONE
            layoutHeaderItem.root.visibility = View.VISIBLE
            layoutHeaderItem.data = Header(resources.getString(R.string.lbl_Id), resources.getString(R.string.lbl_PurchaseOrderNo), resources.getString(R.string.lbl_ExpiredDate), resources.getString(R.string.lbl_Status), resources.getString(R.string.lbl_Total), resources.getString(R.string.lbl_Action))
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
                managePurchaseOrder()
            }

            searchProduct.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query : String) : Boolean {
                    return false
                }

                override fun onQueryTextChange(newText : String) : Boolean {
                    purchaseOrderAdapter.getFilter().filter(newText)
                    return true
                }
            })
            swipeRefresh.apply {
                setOnRefreshListener {
                    swipeRefresh.isRefreshing = false
                    getPurchaseOrders()
                    clearSearch()
                }
                setColorSchemeColors(ContextCompat.getColor(this@PurchaseOrderActivity, R.color.pink))
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

        getPurchaseOrders()
    }

    private fun getPurchaseOrders() {
        viewModel.fetchPurchaseOrders().observe(this) {
            CoroutineScope(Dispatchers.Main).launch {
                purchaseOrderAdapter.apply {
                    setList(it)
                }
            }
        }
    } //endregion

    //region To manage Purchase Order
    private fun managePurchaseOrder(id : Int = 0) {
        val intent = Intent(this@PurchaseOrderActivity, AddPurchaseOrderActivity::class.java)
        intent.putExtra(Default.PARENT_ID, parentId)
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