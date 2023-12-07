package com.varitas.gokulpos.activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.SearchView
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.varitas.gokulpos.R
import com.varitas.gokulpos.adapter.OrdersAdapter
import com.varitas.gokulpos.databinding.ActivityOrderlistBinding
import com.varitas.gokulpos.model.Header
import com.varitas.gokulpos.response.OrderList
import com.varitas.gokulpos.utilities.Constants
import com.varitas.gokulpos.utilities.Default
import com.varitas.gokulpos.utilities.Enums
import com.varitas.gokulpos.viewmodel.OrdersViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.moallemi.tools.extension.date.toDay
import java.io.Serializable
import java.util.Calendar
import java.util.Date


@AndroidEntryPoint class OrderListActivity : BaseActivity() {

    private lateinit var binding : ActivityOrderlistBinding
    private val viewModel : OrdersViewModel by viewModels()
    private lateinit var ordersAdapter : OrdersAdapter
    private var previousScrolled : Boolean = false
    private var currentPage : Int = 1
    private var id : Int = 0
    private var user : Serializable? = null
    private var userName : String = ""

    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderlistBinding.inflate(layoutInflater)
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
        user = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.extras?.getSerializable(Default.USER, Serializable::class.java)
        } else intent.extras?.getSerializable(Default.USER)
        id = intent.extras?.getInt(Default.ID)!!
        userName = intent.extras?.getString(Default.NAME, "")!!

        binding.apply {
            if(userName.isNotEmpty()) {
                layoutToolbar.textViewToolbarName.text = userName
            } else layoutToolbar.textViewToolbarName.text = resources.getString(R.string.lbl_OrderList)
            layoutToolbar.imageViewAction.setImageDrawable(ContextCompat.getDrawable(this@OrderListActivity, R.drawable.ic_home))
            layoutToolbar.imageViewBack.visibility = View.VISIBLE
        }

        previousScrolled = false
        currentPage = 1

        ordersAdapter = OrdersAdapter { orders, _->
            val intent = Intent(this@OrderListActivity, OrderDetailsActivity::class.java)
            intent.putExtra(Default.ORDER_ID, orders.id)
            intent.putExtra(Default.USER, user)
            intent.putExtra(Default.ID, id)
            intent.putExtra(Default.IS_HOLD, orders.status == Default.HOLD_ORDER)
            intent.putExtra(Default.IS_VOID, orders.status == Default.VOID_ORDER)
            openActivity(intent)
        }
    }

    private fun postInitView() {
        binding.apply {
            recycleViewOrders.apply {
                adapter = ordersAdapter
                layoutManager = LinearLayoutManager(this@OrderListActivity)
            }
            layoutHeader.data = Header(resources.getString(R.string.lbl_Id), resources.getString(R.string.lbl_OrderNo), resources.getString(R.string.lbl_Status))
            fabAdd.visibility = View.GONE
            fabTop.visibility = View.VISIBLE
        }
    } //endregion

    //region To manage click events
    private fun manageClicks() {
        binding.apply {
            layoutToolbar.imageViewAction.clickWithDebounce {
                openActivity(Intent(this@OrderListActivity, DashboardActivity::class.java))
            }
            layoutToolbar.imageViewBack.clickWithDebounce {
                manageBackPress()
            }
            searchOrders.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query : String) : Boolean {
                    return false
                }

                override fun onQueryTextChange(newText : String) : Boolean {
                    ordersAdapter.getFilter().filter(newText)
                    return true
                }
            })
            imageViewSearch.clickWithDebounce {
                if(ordersAdapter.filteredList.size == 0) Log.e("", "API")
                else return@clickWithDebounce
            }
            fabTop.clickWithDebounce {
                recycleViewOrders.layoutManager!!.smoothScrollToPosition(recycleViewOrders, null, 0)
            }

            swipeRefresh.apply {
                setOnRefreshListener {
                    swipeRefresh.isRefreshing = false
                    getOrderDetails()
                    clearSearch()
                }
                setColorSchemeColors(ContextCompat.getColor(this@OrderListActivity, R.color.pink))
            }

        }
    } //endregion

    //region To load data
    private fun loadData() {
        viewModel.showProgress.observe(this) {
            manageProgress(it)
        }
        getOrderDetails()
    } //endregion

    private fun getOrderDetails() {
        when(user) {
            Enums.Menus.DASHBOARD -> getOrders()
            Enums.Menus.CUSTOMERS -> getCustomerOrder()
            Enums.Menus.EMPLOYEES -> getEmployeeOrder()
        }
    }

    private fun getCustomerOrder() {
        binding.linearLayoutShimmer.visibility = View.VISIBLE
        viewModel.getCustomerOrder(id, Constants.dateFormat_yyyy_MM_dd.format(previousDate()), Constants.dateFormat_yyyy_MM_dd.format(1.toDay().sinceNow)).observe(this) {
            if(it != null) manageOrders(it) else manageOrders(ArrayList())
        }
    }

    private fun getEmployeeOrder() {
        binding.linearLayoutShimmer.visibility = View.VISIBLE
        viewModel.getEmployeeOrder(id, Constants.dateFormat_yyyy_MM_dd.format(previousDate()), Constants.dateFormat_yyyy_MM_dd.format(1.toDay().sinceNow)).observe(this) {
            if(it != null) manageOrders(it) else manageOrders(ArrayList())
        }
    }

    private fun getOrders() {
        binding.linearLayoutShimmer.visibility = View.VISIBLE
        viewModel.fetchAllOrderList().observe(this) {
            if(it != null) manageOrders(it) else manageOrders(ArrayList())
        }
    }

    private fun manageOrders(data : List<OrderList>) {
        CoroutineScope(Dispatchers.Main).launch {
            binding.linearLayoutShimmer.visibility = View.GONE
            ordersAdapter.apply {
                setList(data)
            }
        }
    }

    private fun previousDate() : Date {
        val today = Date()
        val cal = Calendar.getInstance()
        cal.time = today
        cal.add(Calendar.DAY_OF_YEAR, -1)
        return cal.time
    }

    private fun clearSearch() {
        binding.apply {
            searchOrders.apply {
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

    @Deprecated("Deprecated in Java") override fun onBackPressed() {
        manageBackPress()
        onBackPressedDispatcher.onBackPressed()
    }

    private fun manageBackPress() {
        when(user) {
            Enums.Menus.DASHBOARD -> openActivity(Intent(this, DashboardActivity::class.java))
            Enums.Menus.CUSTOMERS -> {
                val intent = Intent(this, CustomerDetailsActivity::class.java)
                intent.putExtra(Default.CUSTOMER_ID, id)
                openActivity(intent)
            }

            Enums.Menus.EMPLOYEES -> {
                val intent = Intent(this, EmployeeDetailsActivity::class.java)
                intent.putExtra(Default.EMPLOYEE_ID, id)
                openActivity(intent)
            }
        }
    }
}