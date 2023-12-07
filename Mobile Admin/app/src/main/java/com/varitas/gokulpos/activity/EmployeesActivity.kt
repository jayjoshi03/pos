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
import com.varitas.gokulpos.adapter.EmployeeAdapter
import com.varitas.gokulpos.databinding.ActivityOrderlistBinding
import com.varitas.gokulpos.model.Header
import com.varitas.gokulpos.utilities.Default
import com.varitas.gokulpos.viewmodel.EmployeeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint class EmployeesActivity : BaseActivity() {

    private lateinit var binding : ActivityOrderlistBinding
    private val viewModel : EmployeeViewModel by viewModels()
    private lateinit var employeeAdapter : EmployeeAdapter

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
            layoutToolbar.textViewToolbarName.text = resources.getString(R.string.lbl_EmployeeList)
            layoutToolbar.imageViewAction.setImageDrawable(ContextCompat.getDrawable(this@EmployeesActivity, R.drawable.ic_home))
            layoutToolbar.imageViewBack.visibility = View.VISIBLE
        }
        employeeAdapter = EmployeeAdapter { employee, i, isFromDelete->
            if(isFromDelete) {
                hideKeyBoard(this)
                val sweetAlertDialog = SweetAlertDialog(this@EmployeesActivity, SweetAlertDialog.CUSTOM_IMAGE_TYPE)
                sweetAlertDialog.apply {
                    setCanceledOnTouchOutside(false)
                    setCancelable(false)
                    contentText = resources.getString(R.string.lbl_DeletePermission, resources.getString(R.string.Menu_Employees))
                    confirmText = resources.getString(R.string.lbl_Yes)
                    cancelText = resources.getString(R.string.lbl_No)
                    confirmButtonBackgroundColor = ContextCompat.getColor(this@EmployeesActivity, R.color.base_color)
                    cancelButtonBackgroundColor = ContextCompat.getColor(this@EmployeesActivity, R.color.pink)
                    setConfirmClickListener { sDialog->
                        sDialog.dismissWithAnimation()
                        viewModel.deleteEmployee(employee.id!!).observe(this@EmployeesActivity) {
                            CoroutineScope(Dispatchers.Main).launch {
                                if(it) {
                                    employeeAdapter.apply {
                                        list.remove(employee)
                                        filteredList.remove(employee)
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
            } else manageEmployee(employee.id!!)
        }
    }

    private fun postInitView() {
        binding.apply {
            recycleViewOrders.apply {
                adapter = employeeAdapter
                layoutManager = LinearLayoutManager(this@EmployeesActivity)
            }
            layoutHeader.data = Header(resources.getString(R.string.lbl_Id),resources.getString(R.string.hint_Name), resources.getString(R.string.lbl_Role))
        }
    } //endregion

    //region To manage click events
    private fun manageClicks() {
        binding.apply {
            layoutToolbar.imageViewAction.clickWithDebounce {
                openActivity(Intent(this@EmployeesActivity, DashboardActivity::class.java))
            }
            layoutToolbar.imageViewBack.clickWithDebounce {
                openActivity(Intent(this@EmployeesActivity, DashboardActivity::class.java))
            }
            fabAdd.setOnClickListener {
                manageEmployee()
            }

            searchOrders.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query : String) : Boolean {
                    return false
                }

                override fun onQueryTextChange(newText : String) : Boolean {
                    employeeAdapter.getFilter().filter(newText)
                    return true
                }
            })
            swipeRefresh.apply {
                setOnRefreshListener {
                    swipeRefresh.isRefreshing = false
                    getEmployees()
                    clearSearch()
                }
                setColorSchemeColors(ContextCompat.getColor(this@EmployeesActivity, R.color.pink))
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

        getEmployees()
    }

    private fun getEmployees() {
        binding.linearLayoutShimmer.visibility = View.VISIBLE
        viewModel.fetchEmployees().observe(this) {
            CoroutineScope(Dispatchers.Main).launch {
                binding.linearLayoutShimmer.visibility = View.GONE
                employeeAdapter.apply {
                    setList(it)
                }
            }
        }
    }

    //region Get Customer Detail by id
    private fun manageEmployee(id : Int = 0) {
        val intent = Intent(this@EmployeesActivity, EmployeeDetailsActivity::class.java)
        intent.putExtra(Default.EMPLOYEE_ID, if(id > 0) id else 0)
        openActivity(intent)
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

    //endregion
}