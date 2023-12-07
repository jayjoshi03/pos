package com.varitas.gokulpos.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import cn.pedant.SweetAlert.SweetAlertDialog
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarEntry
import com.varitas.gokulpos.R
import com.varitas.gokulpos.adapter.MenusAdapter
import com.varitas.gokulpos.adapter.TopProductsAdapter
import com.varitas.gokulpos.databinding.ActivityDashBinding
import com.varitas.gokulpos.response.DropDown
import com.varitas.gokulpos.utilities.CustomSpinner
import com.varitas.gokulpos.utilities.Default
import com.varitas.gokulpos.utilities.Enums
import com.varitas.gokulpos.utilities.Preference
import com.varitas.gokulpos.viewmodel.DashViewModel
import com.varitas.gokulpos.viewmodel.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.system.exitProcess


@AndroidEntryPoint class DashboardActivity : BaseActivity() {

    private lateinit var binding : ActivityDashBinding

    // array list for storing entries.
    private lateinit var barEntriesArrayList : ArrayList<BarEntry>
    private val viewModel : DashViewModel by viewModels()
    private val viewModelAuth : LoginViewModel by viewModels()

    private lateinit var topProductAdapter : TopProductsAdapter
    private lateinit var menusAdapter : MenusAdapter

    private lateinit var tendersSpinner : ArrayAdapter<DropDown>
    private lateinit var tenderList : ArrayList<DropDown>
    private var parentId = 0

    companion object {
        lateinit var Instance : DashboardActivity
    }

    init {
        Instance = this
    }

    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashBinding.inflate(layoutInflater)
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
        binding.layoutToolbar.textViewToolbarName.text = resources.getString(R.string.lbl_Dashboard)
        barEntriesArrayList = ArrayList()
        topProductAdapter = TopProductsAdapter()
        tenderList = ArrayList()
        parentId = 0
        tendersSpinner = ArrayAdapter(this, R.layout.spinner_items, tenderList)

        menusAdapter = MenusAdapter { menu->
            parentId = menu.id!!
            when(menu.id) {
                Default.MANAGE_PRODUCT -> moveToProducts(parentId)
                Default.MANAGE_CUSTOMER -> openActivity(Intent(this, CustomersActivity::class.java))
                Default.MANAGE_EMPLOYEE -> openActivity(Intent(this, EmployeesActivity::class.java))
                Default.MANAGE_GROUP -> openActivity(Intent(this, GroupActivity::class.java))
                Default.MANAGE_ORDER -> {
                    val intent = Intent(this@DashboardActivity, OrderListActivity::class.java)
                    intent.putExtra(Default.USER, Enums.Menus.DASHBOARD)
                    intent.putExtra(Default.ID, 0)
                    openActivity(intent)
                }
                Default.MANAGE_SALES_PROMO -> {showSweetDialog(this, resources.getString(R.string.lbl_ComingSoon))}
                Default.MANAGE_ROLE -> {showSweetDialog(this, resources.getString(R.string.lbl_ComingSoon))}
                Default.MANAGE_TENDER -> openActivity(Intent(this, TenderActivity::class.java))
                Default.MANAGE_INVENTORY ->  moveToProducts(parentId)
                Default.MANAGE_STORE -> openActivity(Intent(this, StoreActivity::class.java))
                Default.MANAGE_REPORT -> moveToProducts(parentId)
                else -> {}
            }
        }
    } //endregion

    //region To init values
    private fun postInitView() {
        tendersSpinner.apply {
            setDropDownViewResource(R.layout.spinner_dropdown_item)
        }

        binding.apply {
            recycleViewProducts.apply {
                adapter = topProductAdapter
                layoutManager = LinearLayoutManager(this@DashboardActivity)
            }
            recycleViewMenus.apply {
                adapter = menusAdapter
                layoutManager = GridLayoutManager(this@DashboardActivity, 3)
            }
        }
    } // endregion

    //region To manage click events
    private fun manageClicks() {
        binding.apply {
            layoutToolbar.imageViewAction.clickWithDebounce {
                val sweetAlertDialog = SweetAlertDialog(this@DashboardActivity, SweetAlertDialog.CUSTOM_IMAGE_TYPE)
                sweetAlertDialog.setCanceledOnTouchOutside(false)
                sweetAlertDialog.setCancelable(false)
                sweetAlertDialog.contentText = resources.getString(R.string.lbl_SureLogOut)
                sweetAlertDialog.confirmText = resources.getString(R.string.lbl_Yes)
                sweetAlertDialog.cancelText = resources.getString(R.string.lbl_No)
                sweetAlertDialog.confirmButtonBackgroundColor = ContextCompat.getColor(this@DashboardActivity, R.color.base_color)
                sweetAlertDialog.cancelButtonBackgroundColor = ContextCompat.getColor(this@DashboardActivity, R.color.pink)
                sweetAlertDialog.setConfirmClickListener { sDialog->
                    sDialog.dismissWithAnimation()
                    viewModelAuth.doLogout().observe(this@DashboardActivity) {
                        CoroutineScope(Dispatchers.Main).launch {
                            if(it!!.status == Default.SUCCESS_API) {
                                Preference.getPref(this@DashboardActivity).edit().clear().apply()
                                openActivity(Intent(this@DashboardActivity, LoginActivity::class.java))
                            }
                        }
                    }
                }
                sweetAlertDialog.setCancelClickListener { sDialog->
                    sDialog.dismissWithAnimation()
                }
                sweetAlertDialog.show()
            }

            imageViewProducts.visibility = View.GONE

            spinnerTender.apply {
                adapter = tendersSpinner
                setSpinnerEventsListener(object : CustomSpinner.OnSpinnerEventsListener {
                    override fun onSpinnerOpened(spinner : Spinner?) {

                    }

                    override fun onSpinnerClosed(spinner : Spinner?) {
                        val selectedValue = spinner?.selectedItem as DropDown?
                        if(selectedValue != null) fetchTopSellingProducts(selectedValue.id!!)
                    }
                })
            }
        }
    } //endregion

    @Deprecated("Deprecated in Java") override fun onBackPressed() {
        onBackPressedDispatcher.onBackPressed()
        finishAffinity()
        exitProcess(0)
    }

    //region To load data
    private fun loadData() {
        viewModel.showProgress.observe(this) {
            manageProgress(it)
        }

        viewModelAuth.showProgress.observe(this) {
            manageProgress(it)
        }

        viewModel.fetchMenus().observe(this) {
            CoroutineScope(Dispatchers.Main).launch {
                menusAdapter.setList(it)
            }
        }

        viewModel.fetchTenders().observe(this) {
            CoroutineScope(Dispatchers.Main).launch {
                tendersSpinner.apply {
                    addAll(it)
                    notifyDataSetChanged()
                    if(it.isNotEmpty()) fetchTopSellingProducts(it[0].id!!)
                }
            }
        }
    }

    private fun setBarChart(barData : BarData) {
        binding.apply {
            barChart.apply {
                data = barData // adding color to our bar data set.
                invalidate()
                setTouchEnabled(true)
                isDoubleTapToZoomEnabled = false
                setDrawBorders(false)
                setDrawGridBackground(false)
                description.isEnabled = false
                axisLeft.apply {
                    setDrawGridLines(false)
                    setDrawLabels(false)
                    setDrawAxisLine(false)
                }
                xAxis.apply {
                    setDrawGridLines(false)
                    setDrawLabels(false)
                    setDrawAxisLine(false)
                }
                axisRight.apply {
                    setDrawGridLines(false)
                    setDrawLabels(false)
                    setDrawAxisLine(false)
                }
            }
        }
    } //endregion

    //region To fetch top selling products
    private fun fetchTopSellingProducts(method : Int) { //TODO : Need to add method id after getting confirmation
        viewModel.fetchTopProducts(11).observe(this@DashboardActivity) {
            topProductAdapter.apply {
                list.clear()
                setList(it)
            }
        }

        viewModel.setBarChart(12).observe(this) {
            CoroutineScope(Dispatchers.Main).launch {
                setBarChart(it)
            }
        }
    } //endregion
}