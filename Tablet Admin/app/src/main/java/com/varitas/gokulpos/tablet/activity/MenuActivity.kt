package com.varitas.gokulpos.tablet.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.varitas.gokulpos.tablet.R
import com.varitas.gokulpos.tablet.adapter.MenusAdapter
import com.varitas.gokulpos.tablet.databinding.ActivitySubmenusBinding
import com.varitas.gokulpos.tablet.utilities.Default
import com.varitas.gokulpos.tablet.viewmodel.DashViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@AndroidEntryPoint class MenuActivity : BaseActivity() {

    private lateinit var binding: ActivitySubmenusBinding
    private val viewModel: DashViewModel by viewModels()
    private lateinit var menusAdapter: MenusAdapter
    private var parentId = 0
    private var isFromOrder = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySubmenusBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setSupportActionBar(binding.layoutToolbar.toolbarCommon)
        initData()
        loadData()
        manageClicks()
    }

    //region To init data
    private fun initData() {
        parentId = if (intent.extras?.getInt(Default.PARENT_ID) != null) intent.extras?.getInt(Default.PARENT_ID)!! else 0
        isFromOrder = if (intent.extras?.getBoolean(Default.ORDER) != null) intent.extras?.getBoolean(Default.ORDER)!! else false
        binding.apply {
            layoutToolbar.textViewToolbarName.text = resources.getString(R.string.Menu)
            layoutToolbar.imageViewAction.setImageDrawable(ContextCompat.getDrawable(this@MenuActivity, R.drawable.ic_home))
            layoutToolbar.imageViewBack.visibility = View.VISIBLE
        }

        menusAdapter = MenusAdapter { menu ->
            when(menu.id){
                Default.MANAGE_DEPARTMENT -> {
                    val intent = Intent(this, DepartmentActivity::class.java)
                    intent.putExtra(Default.PARENT_ID, parentId)
                    intent.putExtra(Default.ORDER, isFromOrder)
                    openActivity(intent)
                }
                Default.MANAGE_BRAND -> {
                    val intent = Intent(this, BrandActivity::class.java)
                    intent.putExtra(Default.PARENT_ID, parentId)
                    intent.putExtra(Default.ORDER, isFromOrder)
                    openActivity(intent)
                }
                Default.MANAGE_CATEGORY -> {
                    val intent = Intent(this, CategoryActivity::class.java)
                    intent.putExtra(Default.PARENT_ID, parentId)
                    intent.putExtra(Default.ORDER, isFromOrder)
                    openActivity(intent)
                }
                Default.MANAGE_PRODUCT_ITEM -> {
                    val intent = Intent(this, ProductListActivity::class.java)
                    intent.putExtra(Default.PARENT_ID, parentId)
                    intent.putExtra(Default.ORDER, isFromOrder)
                    openActivity(intent)
                }
                Default.MANAGE_FACILITY ->{
                    val intent = Intent(this, FacilityActivity::class.java)
                    intent.putExtra(Default.PARENT_ID, parentId)
                    intent.putExtra(Default.ORDER, isFromOrder)
                    openActivity(intent)
                }
                Default.MANAGE_SPECIFICATION -> {
                    val intent = Intent(this, SpecificationActivity::class.java)
                    intent.putExtra(Default.PARENT_ID, parentId)
                    intent.putExtra(Default.ORDER, isFromOrder)
                    openActivity(intent)
                }
                Default.MANAGE_TAX -> {
                    val intent = Intent(this, TaxActivity::class.java)
                    intent.putExtra(Default.PARENT_ID, parentId)
                    intent.putExtra(Default.ORDER, isFromOrder)
                    openActivity(intent)
                }
                Default.MANAGE_UOM -> {
                    val intent = Intent(this, UOMActivity::class.java)
                    intent.putExtra(Default.PARENT_ID, parentId)
                    intent.putExtra(Default.ORDER, isFromOrder)
                    openActivity(intent)
                }
                Default.MANAGE_VENDOR -> {
                    val intent = Intent(this, VendorActivity::class.java)
                    intent.putExtra(Default.PARENT_ID, parentId)
                    intent.putExtra(Default.ORDER, isFromOrder)
                    openActivity(intent)
                }
                Default.MANAGE_PURCHASE_ORDER -> {
                    val intent = Intent(this, PurchaseOrderActivity::class.java)
                    intent.putExtra(Default.PARENT_ID, parentId)
                    intent.putExtra(Default.ORDER, isFromOrder)
                    openActivity(intent)
                }
                Default.MANAGE_PURCHASE_INVOICE -> {
                    val intent = Intent(this, InvoiceActivity::class.java)
                    intent.putExtra(Default.PARENT_ID, parentId)
                    intent.putExtra(Default.ORDER, isFromOrder)
                    openActivity(intent)
                }
                Default.MANAGE_REPORT_DEPARTMENT -> {
                    val intent = Intent(this, ReportActivity::class.java)
                    intent.putExtra(Default.PARENT_ID, parentId)
                    intent.putExtra(Default.ID, menu.id)
                    intent.putExtra(Default.ORDER, isFromOrder)
                    openActivity(intent)
                }
                Default.MANAGE_REPORT_SALE -> {
                    val intent = Intent(this, ReportActivity::class.java)
                    intent.putExtra(Default.PARENT_ID, parentId)
                    intent.putExtra(Default.ID, menu.id)
                    intent.putExtra(Default.ORDER, isFromOrder)
                    openActivity(intent)
                }
                Default.MANAGE_REPORT_OPERATION_RECORD -> {
                    val intent = Intent(this, ReportActivity::class.java)
                    intent.putExtra(Default.PARENT_ID, parentId)
                    intent.putExtra(Default.ID, menu.id)
                    intent.putExtra(Default.ORDER, isFromOrder)
                    openActivity(intent)
                }
                Default.MANAGE_REPORT_CASH_SALE -> {
                    val intent = Intent(this, ReportActivity::class.java)
                    intent.putExtra(Default.PARENT_ID, parentId)
                    intent.putExtra(Default.ID, menu.id)
                    intent.putExtra(Default.ORDER, isFromOrder)
                    openActivity(intent)
                }
                Default.MANAGE_REPORT_SALE_EXCEL -> {
                    val intent = Intent(this, ReportActivity::class.java)
                    intent.putExtra(Default.PARENT_ID, parentId)
                    intent.putExtra(Default.ID, menu.id)
                    intent.putExtra(Default.ORDER, isFromOrder)
                    openActivity(intent)
                }
            }
        }
    }

    //region To manage click events
    private fun manageClicks() {
        binding.apply {
            layoutToolbar.imageViewAction.clickWithDebounce {
                if(isFromOrder) moveToOrder(parentId)
                else openActivity(Intent(this@MenuActivity, DashboardActivity::class.java))
            }
            layoutToolbar.imageViewBack.clickWithDebounce {
                if(isFromOrder) moveToOrder(parentId)
                else openActivity(Intent(this@MenuActivity, DashboardActivity::class.java))
            }
            recycleViewReports.apply {
                adapter = menusAdapter
                layoutManager = GridLayoutManager(this@MenuActivity, 6)
            }
        }
    } //endregion

    @Deprecated("Deprecated in Java") override fun onBackPressed() {
        if(isFromOrder) moveToOrder(parentId)
        else openActivity(Intent(this, DashboardActivity::class.java))
        onBackPressedDispatcher.onBackPressed()
    }

    //region To load data
    private fun loadData() {
        viewModel.showProgress.observe(this) {
            manageProgress(it)
        }

        viewModel.errorMsg.observe(this) {
            showSweetDialog(this, it)
        }

        viewModel.fetchMenus(if(isFromOrder) Default.MANAGE_PRODUCT else parentId).observe(this) {
            CoroutineScope(Dispatchers.Main).launch {
                menusAdapter.setList(it)
            }
        }
    } //endregion
}