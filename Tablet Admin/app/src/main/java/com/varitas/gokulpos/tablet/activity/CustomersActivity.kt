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
import com.varitas.gokulpos.tablet.adapter.CustomerDetailAdapter
import com.varitas.gokulpos.tablet.databinding.ActivityCommonBinding
import com.varitas.gokulpos.tablet.fragmentDialogs.AddCustomerPopupDialog
import com.varitas.gokulpos.tablet.fragmentDialogs.DeleteAlertPopupDialog
import com.varitas.gokulpos.tablet.model.Header
import com.varitas.gokulpos.tablet.response.Customers
import com.varitas.gokulpos.tablet.utilities.Default
import com.varitas.gokulpos.tablet.utilities.Enums
import com.varitas.gokulpos.tablet.viewmodel.CustomerViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint class CustomersActivity : BaseActivity() {

    private lateinit var binding : ActivityCommonBinding
    val viewModel : CustomerViewModel by viewModels()
    private lateinit var customerDetailAdapter : CustomerDetailAdapter

    companion object {
        lateinit var Instance : CustomersActivity
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
            layoutToolbar.textViewToolbarName.text = resources.getString(R.string.lbl_CustomerList)
            layoutToolbar.imageViewAction.setImageDrawable(ContextCompat.getDrawable(this@CustomersActivity, R.drawable.ic_home))
            layoutToolbar.imageViewBack.visibility = View.VISIBLE
        }
        customerDetailAdapter = CustomerDetailAdapter { customer, typeButton, pos->
            when(typeButton) {
                Enums.ClickEvents.EDIT -> {
                    manageCustomer(customer.id!!, pos)
                }

                Enums.ClickEvents.DELETE -> {
                    hideKeyBoard(this)
                    val ft = supportFragmentManager.beginTransaction()
                    val dialogFragment = DeleteAlertPopupDialog(resources.getString(R.string.DeletePermission, resources.getString(R.string.lbl_Customer)), customer.id!!, isCustomer = true)
                    val prevFragment: Fragment? = supportFragmentManager.findFragmentByTag(DeleteAlertPopupDialog::class.java.name)
                    if (prevFragment != null) return@CustomerDetailAdapter
                    dialogFragment.isCancelable = false
                    dialogFragment.show(ft, DeleteAlertPopupDialog::class.java.name)
                    dialogFragment.setListener(object : DeleteAlertPopupDialog.OnButtonClickListener {
                        override fun onButtonClickListener(typeButton : Enums.ClickEvents) {
                            when(typeButton) {
                                Enums.ClickEvents.DELETE -> {
                                    customerDetailAdapter.apply {
                                        list.remove(customer)
                                        filteredList.remove(customer)
                                        notifyItemRemoved(pos)
                                        notifyItemRangeChanged(pos, list.size)
                                    }
                                }

                                else -> {}
                            }
                        }
                    })
                }

                else -> {}
            }
        }
    }

    private fun postInitView() {
        binding.apply {
            layoutHeader.data = Header(resources.getString(R.string.lbl_Id), resources.getString(R.string.hint_Name), resources.getString(R.string.lbl_PhoneNumber), resources.getString(R.string.lbl_DrivingLicense), resources.getString(R.string.lbl_Action))
            recycleViewProducts.apply {
                adapter = customerDetailAdapter
                layoutManager = LinearLayoutManager(this@CustomersActivity)
            }
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
            searchProduct.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query : String) : Boolean {
                    return false
                }

                override fun onQueryTextChange(newText : String) : Boolean {
                    customerDetailAdapter.getFilter().filter(newText)
                    return true
                }
            })
            swipeRefresh.apply {
                setOnRefreshListener {
                    swipeRefresh.isRefreshing = false
                    getCustomers()
                }
                setColorSchemeColors(ContextCompat.getColor(this@CustomersActivity, R.color.pink))
            }
            fabAdd.clickWithDebounce {
                openCustomerPopup()
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

    private fun getCustomers() {
        viewModel.fetchCustomers().observe(this) {
            CoroutineScope(Dispatchers.Main).launch {
                customerDetailAdapter.apply {
                    setList(it)
                }
            }
        }
    }
    //endregion

    //region To manage Customer
    private fun manageCustomer(id : Int = 0, pos : Int = 0) {
        if(id > 0) {
            viewModel.getCustomerDetail(id).observe(this) {
                val bundle = Bundle()
                bundle.putSerializable(Default.CUSTOMER, it)
                openCustomerPopup(bundle, pos)
            }
        } else openCustomerPopup()
    }

    private fun openCustomerPopup(bundle : Bundle? = null, pos : Int = 0) {
        val ft = supportFragmentManager.beginTransaction()
        val dialogFragment = AddCustomerPopupDialog.newInstance()
        val prevFragment : Fragment? = supportFragmentManager.findFragmentByTag(AddCustomerPopupDialog::class.java.name)
        if(prevFragment != null) return
        if(bundle != null) dialogFragment.arguments = bundle
        dialogFragment.isCancelable = false
        dialogFragment.show(ft, AddCustomerPopupDialog::class.java.name)
        dialogFragment.setListener(object : AddCustomerPopupDialog.OnButtonClickListener {
            override fun onButtonClickListener(typeButton : Enums.ClickEvents, customer : Customers?) {
                clearSearch()
                when(typeButton) {
                    Enums.ClickEvents.CANCEL -> {
                        if(dialogFragment.isVisible) dialogFragment.dismiss()
                    }

                    Enums.ClickEvents.SAVE -> {
                        if(dialogFragment.isVisible) dialogFragment.dismiss()
                        getCustomers()
                    }

                    Enums.ClickEvents.UPDATE -> {
                        if(dialogFragment.isVisible) dialogFragment.dismiss()
                        if(customer != null) {
                            customerDetailAdapter.apply {
                                list[pos].name = customer.name
                                list[pos].phonenNo = customer.phonenNo
                                list[pos].drivingLicense = customer.drivingLicense
                                notifyItemChanged(pos)
                            }
                        }
                    }

                    Enums.ClickEvents.ERROR -> {
                        if(dialogFragment.isVisible) dialogFragment.dismiss()
                        showSweetDialog(this@CustomersActivity, resources.getString(R.string.lbl_SomethingWrong))
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