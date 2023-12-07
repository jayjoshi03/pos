package com.varitas.gokulpos.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.varitas.gokulpos.R
import com.varitas.gokulpos.adapter.MenusAdapter
import com.varitas.gokulpos.databinding.ActivityReportsBinding
import com.varitas.gokulpos.utilities.Default
import com.varitas.gokulpos.viewmodel.DashViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@AndroidEntryPoint class MenuActivity : BaseActivity() {
    private lateinit var binding : ActivityReportsBinding
    private val viewModel : DashViewModel by viewModels()
    private lateinit var menusAdapter : MenusAdapter
    private var parentId = 0

    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReportsBinding.inflate(layoutInflater)
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
        binding.apply {
            layoutToolbar.textViewToolbarName.text = resources.getString(R.string.Menu)
            parentId = if(intent.extras?.getInt(Default.PARENT_ID) != null) intent.extras?.getInt(Default.PARENT_ID)!! else 0
//            isFromProduct = if(intent.extras?.getBoolean(Default.PRODUCT) != null) intent.extras?.getBoolean(Default.PRODUCT)!! else false
            layoutToolbar.imageViewAction.setImageDrawable(ContextCompat.getDrawable(this@MenuActivity, R.drawable.ic_home))
            layoutToolbar.imageViewBack.visibility = View.VISIBLE
        }

        menusAdapter = MenusAdapter { menu->
            when(menu.id) {
                Default.MANAGE_DEPARTMENT -> {
                    val intent = Intent(this, DepartmentActivity::class.java)
                    intent.putExtra(Default.PARENT_ID, parentId)
                    openActivity(intent)
                }

                Default.MANAGE_BRAND -> {
                    val intent = Intent(this, BrandActivity::class.java)
                    intent.putExtra(Default.PARENT_ID, parentId)
                    openActivity(intent)
                }

                Default.MANAGE_CATEGORY -> {
                    val intent = Intent(this, CategoryActivity::class.java)
                    intent.putExtra(Default.PARENT_ID, parentId)
                    openActivity(intent)
                }

                Default.MANAGE_PRODUCT_ITEM -> {
                    val intent = Intent(this, ProductListActivity::class.java)
                    intent.putExtra(Default.PARENT_ID, parentId)
                    openActivity(intent)
                }

                Default.MANAGE_FACILITY -> {
                    val intent = Intent(this, FacilityActivity::class.java)
                    intent.putExtra(Default.PARENT_ID, parentId)
                    openActivity(intent)
                }

                Default.MANAGE_SPECIFICATION -> {
                    val intent = Intent(this, SpecificationActivity::class.java)
                    intent.putExtra(Default.PARENT_ID, parentId)
                    openActivity(intent)
                }

                Default.MANAGE_TAX -> {
                    val intent = Intent(this, TaxActivity::class.java)
                    intent.putExtra(Default.PARENT_ID, parentId)
                    openActivity(intent)
                }

                Default.MANAGE_UOM -> {
                    val intent = Intent(this, UOMActivity::class.java)
                    intent.putExtra(Default.PARENT_ID, parentId)
                    openActivity(intent)
                }

                Default.MANAGE_VENDOR -> {
                    val intent = Intent(this, VendorActivity::class.java)
                    intent.putExtra(Default.PARENT_ID, parentId)
                    openActivity(intent)
                }

                Default.MANAGE_PURCHASE_ORDER -> {
                    val intent = Intent(this, PurchaseOrderActivity::class.java)
                    intent.putExtra(Default.PARENT_ID, parentId)
                    openActivity(intent)
                }

                Default.MANAGE_INVOICE -> {
                    val intent = Intent(this, InvoiceActivity::class.java)
                    intent.putExtra(Default.PARENT_ID, parentId)
                    openActivity(intent)
                }

                Default.MANAGE_REPORT_DEPARTMENT -> {
                    val intent = Intent(this, ReportDetailsActivity::class.java)
                    intent.putExtra(Default.PARENT_ID, parentId)
                    intent.putExtra(Default.ID, menu.id)
                    openActivity(intent)
                }

                Default.MANAGE_REPORT_SALE -> {
                    val intent = Intent(this, ReportDetailsActivity::class.java)
                    intent.putExtra(Default.PARENT_ID, parentId)
                    intent.putExtra(Default.ID, menu.id)
                    openActivity(intent)
                }

                Default.MANAGE_REPORT_OPERATION_RECORD -> {
                    val intent = Intent(this, ReportDetailsActivity::class.java)
                    intent.putExtra(Default.PARENT_ID, parentId)
                    intent.putExtra(Default.ID, menu.id)
                    openActivity(intent)
                }

                Default.MANAGE_REPORT_CASH_SALE -> {
                    val intent = Intent(this, ReportDetailsActivity::class.java)
                    intent.putExtra(Default.PARENT_ID, parentId)
                    intent.putExtra(Default.ID, menu.id)
                    openActivity(intent)
                }

                Default.MANAGE_REPORT_SALE_EXCEL -> {
                    val intent = Intent(this, ReportDetailsActivity::class.java)
                    intent.putExtra(Default.PARENT_ID, parentId)
                    intent.putExtra(Default.ID, menu.id)
                    openActivity(intent)
                }
            }
        }
    }

    private fun postInitView() { // Setting we View Client


    } //endregion

    //region To manage click events
    private fun manageClicks() {
        binding.apply {
            layoutToolbar.imageViewAction.clickWithDebounce {
                openActivity(Intent(this@MenuActivity, DashboardActivity::class.java))
            }
            layoutToolbar.imageViewBack.clickWithDebounce {
                openActivity(Intent(this@MenuActivity, DashboardActivity::class.java))
            }
            recycleViewReports.apply {
                adapter = menusAdapter
                layoutManager = GridLayoutManager(this@MenuActivity, 3)
            }
        }
    } //endregion

    @Deprecated("Deprecated in Java") override fun onBackPressed() {
        openActivity(Intent(this, DashboardActivity::class.java))
        onBackPressedDispatcher.onBackPressed()
    }

    //region To load data
    private fun loadData() {
        viewModel.showProgress.observe(this) {
            manageProgress(it)
        }

        viewModel.fetchMenus(parentId).observe(this) {
            CoroutineScope(Dispatchers.Main).launch {
                menusAdapter.setList(it)
            }
        }
    } //endregion
}