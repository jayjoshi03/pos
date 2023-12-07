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
import com.varitas.gokulpos.tablet.adapter.EmployeeAdapter
import com.varitas.gokulpos.tablet.databinding.ActivityCommonBinding
import com.varitas.gokulpos.tablet.fragmentDialogs.DeleteAlertPopupDialog
import com.varitas.gokulpos.tablet.fragmentDialogs.EmployeePopupDialog
import com.varitas.gokulpos.tablet.model.Header
import com.varitas.gokulpos.tablet.response.Employee
import com.varitas.gokulpos.tablet.utilities.Default
import com.varitas.gokulpos.tablet.utilities.Enums
import com.varitas.gokulpos.tablet.viewmodel.EmployeeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint class EmployeesActivity : BaseActivity() {

    private lateinit var binding : ActivityCommonBinding
    val viewModel : EmployeeViewModel by viewModels()
    private lateinit var employeeAdapter : EmployeeAdapter

    companion object {
        lateinit var Instance : EmployeesActivity
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
        binding.apply {
            layoutToolbar.textViewToolbarName.text = resources.getString(R.string.lbl_EmployeeList)
            layoutToolbar.imageViewAction.setImageDrawable(ContextCompat.getDrawable(this@EmployeesActivity, R.drawable.ic_home))
            layoutToolbar.imageViewBack.visibility = View.VISIBLE
        }
        employeeAdapter = EmployeeAdapter { emp, action, pos->
            when(action) {
                Enums.ClickEvents.DELETE -> {
                    hideKeyBoard(this)
                    val ft = supportFragmentManager.beginTransaction()
                    val dialogFragment = DeleteAlertPopupDialog(resources.getString(R.string.DeletePermission, resources.getString(R.string.lbl_Employee)), emp.id!!, isEmployee = true)
                    val prevFragment: Fragment? = supportFragmentManager.findFragmentByTag(DeleteAlertPopupDialog::class.java.name)
                    if (prevFragment != null) return@EmployeeAdapter
                    dialogFragment.isCancelable = false
                    dialogFragment.show(ft, DeleteAlertPopupDialog::class.java.name)
                    dialogFragment.setListener(object : DeleteAlertPopupDialog.OnButtonClickListener {
                        override fun onButtonClickListener(typeButton : Enums.ClickEvents) {
                            when(typeButton) {
                                Enums.ClickEvents.DELETE -> {
                                    employeeAdapter.apply {
                                        list.remove(emp)
                                        filteredList.remove(emp)
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
                    manageEmployee(emp.id!!, pos)
                }

                else -> {
                }
            }
        }
    }

    private fun postInitView() {
        binding.apply {
            layoutHeader.data = Header(resources.getString(R.string.lbl_Id), resources.getString(R.string.hint_Name), resources.getString(R.string.lbl_PhoneNumber), resources.getString(R.string.lbl_Designation), resources.getString(R.string.lbl_Action))
            recycleViewProducts.apply {
                adapter = employeeAdapter
                layoutManager = LinearLayoutManager(this@EmployeesActivity)
            }
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
            fabAdd.clickWithDebounce {
                manageEmployee()
            }
            searchProduct.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
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
        viewModel.fetchEmployees().observe(this) {
            CoroutineScope(Dispatchers.Main).launch {
                employeeAdapter.apply {
                    setList(it)
                }
            }
        }
    }
//endregion

    //region To manage Employee
    private fun manageEmployee(id : Int = 0, pos : Int = 0) {
        if(id > 0) {
            viewModel.fetchEmployeeDetails(id).observe(this) {
                val bundle = Bundle()
                bundle.putSerializable(Default.EMPLOYEE, it)
                openEmployeePopup(bundle, pos)
            }
        } else openEmployeePopup()
    }

    private fun openEmployeePopup(bundle : Bundle? = null, pos : Int = 0) {
        val ft = supportFragmentManager.beginTransaction()
        val dialogFragment = EmployeePopupDialog.newInstance()
        val prevFragment : Fragment? = supportFragmentManager.findFragmentByTag(EmployeePopupDialog::class.java.name)
        if(prevFragment != null) return
        if(bundle != null) dialogFragment.arguments = bundle
        dialogFragment.isCancelable = false
        dialogFragment.show(ft, EmployeePopupDialog::class.java.name)
        dialogFragment.setListener(object : EmployeePopupDialog.OnButtonClickListener {
            override fun onButtonClickListener(typeButton : Enums.ClickEvents, employee : Employee?) {
                clearSearch()
                when(typeButton) {
                    Enums.ClickEvents.CANCEL -> {
                        if(dialogFragment.isVisible) dialogFragment.dismiss()
                    }

                    Enums.ClickEvents.SAVE -> {
                        if(dialogFragment.isVisible) dialogFragment.dismiss()
                        getEmployees()
                    }

                    Enums.ClickEvents.UPDATE -> {
                        if(dialogFragment.isVisible) dialogFragment.dismiss()
                        if(employee != null) {
                            employeeAdapter.apply {
                                list[pos].name = employee.name
                                list[pos].mobileNo = employee.mobileNo
                                list[pos].roleName = employee.roleName
                                notifyItemChanged(pos)
                            }
                        }
                    }

                    Enums.ClickEvents.ERROR -> {
                        if(dialogFragment.isVisible) dialogFragment.dismiss()
                        showSweetDialog(this@EmployeesActivity, resources.getString(R.string.lbl_SomethingWrong))
                    }

                    else -> {
                    }
                }
            }
        })
    } //endregion

    private fun clearSearch() {
        binding.apply {
            searchProduct.apply {
                setQuery("", false)
                clearFocus()
            }
        }
    }
}