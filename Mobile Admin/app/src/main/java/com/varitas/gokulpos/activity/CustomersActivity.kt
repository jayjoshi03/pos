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
import com.varitas.gokulpos.adapter.CustomerAdapter
import com.varitas.gokulpos.databinding.ActivityOrderlistBinding
import com.varitas.gokulpos.model.Header
import com.varitas.gokulpos.utilities.Default
import com.varitas.gokulpos.viewmodel.CustomerViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint class CustomersActivity : BaseActivity() {

    private lateinit var binding : ActivityOrderlistBinding
    private val viewModel : CustomerViewModel by viewModels()
    private lateinit var customerAdapter : CustomerAdapter

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
        binding.apply {
            layoutToolbar.textViewToolbarName.text = resources.getString(R.string.lbl_CustomerList)
            layoutToolbar.imageViewAction.setImageDrawable(ContextCompat.getDrawable(this@CustomersActivity, R.drawable.ic_home))
            layoutToolbar.imageViewBack.visibility = View.VISIBLE
        }
        customerAdapter = CustomerAdapter { customers, i, isFromDelete->
            if(isFromDelete) {
                hideKeyBoard(this)
                val sweetAlertDialog = SweetAlertDialog(this@CustomersActivity, SweetAlertDialog.CUSTOM_IMAGE_TYPE)
                sweetAlertDialog.apply {
                    setCanceledOnTouchOutside(false)
                    setCancelable(false)
                    contentText = resources.getString(R.string.lbl_DeletePermission, resources.getString(R.string.Menu_Customers))
                    confirmText = resources.getString(R.string.lbl_Yes)
                    cancelText = resources.getString(R.string.lbl_No)
                    confirmButtonBackgroundColor = ContextCompat.getColor(this@CustomersActivity, R.color.base_color)
                    cancelButtonBackgroundColor = ContextCompat.getColor(this@CustomersActivity, R.color.pink)
                    setConfirmClickListener { sDialog->
                        sDialog.dismissWithAnimation()
                        viewModel.deleteCustomer(customers.id!!).observe(this@CustomersActivity) {
                            CoroutineScope(Dispatchers.Main).launch {
                                if(it) {
                                    customerAdapter.apply {
                                        list.remove(customers)
                                        filteredList.remove(customers)
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
            } else manageCustomer(id = customers.id!!)
        }
    }

    private fun postInitView() {
        binding.apply {
            recycleViewOrders.apply {
                adapter = customerAdapter
                layoutManager = LinearLayoutManager(this@CustomersActivity)
            }
            layoutHeader.data = Header(resources.getString(R.string.lbl_Id),resources.getString(R.string.hint_Name), resources.getString(R.string.hint_MobileNo))
        }
    } //endregion

    //region To manage click events
    private fun manageClicks() {
        binding.apply {
            layoutToolbar.imageViewAction.clickWithDebounce {
                openActivity(Intent(this@CustomersActivity, DashboardActivity::class.java))
            }
            layoutToolbar.imageViewBack.clickWithDebounce {
                openActivity(Intent(this@CustomersActivity, DashboardActivity::class.java))
            }

            fabAdd.setOnClickListener {
                manageCustomer()
            }

            searchOrders.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query : String) : Boolean {
                    return false
                }

                override fun onQueryTextChange(newText : String) : Boolean {
                    customerAdapter.getFilter().filter(newText)
                    return true
                }
            })
            swipeRefresh.apply {
                setOnRefreshListener {
                    swipeRefresh.isRefreshing = false
                    getCustomers()
                    clearSearch()
                }
                setColorSchemeColors(ContextCompat.getColor(this@CustomersActivity, R.color.pink))
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

        viewModel.errorMsg.observe(this) {
            showSweetDialog(this, it)
        }

        getCustomers()
    }

    //region Customer List
    private fun getCustomers() {
        binding.linearLayoutShimmer.visibility = View.VISIBLE
        viewModel.fetchCustomers().observe(this) {
            CoroutineScope(Dispatchers.Main).launch {
                binding.linearLayoutShimmer.visibility = View.GONE
                customerAdapter.apply {
                    setList(it)
                }
            }
        }
    }
    //endregion

    private fun clearSearch() {
        binding.apply {
            searchOrders.apply {
                setQuery("", false)
                clearFocus()
            }
        }
    }

    //region Get Customer Detail by id
    private fun manageCustomer(id : Int = 0) {
        val intent = Intent(this@CustomersActivity, CustomerDetailsActivity::class.java)
        intent.putExtra(Default.CUSTOMER_ID, if(id > 0) id else 0)
        openActivity(intent)
    }
    //endregion
}