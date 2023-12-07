package com.varitas.gokulpos.tablet.activity

import android.os.Bundle
import android.view.View
import android.widget.SearchView
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.varitas.gokulpos.tablet.R
import com.varitas.gokulpos.tablet.adapter.VendorAdapter
import com.varitas.gokulpos.tablet.databinding.ActivityVendorBinding
import com.varitas.gokulpos.tablet.fragmentDialogs.AddSalePersonPopupDialog
import com.varitas.gokulpos.tablet.fragmentDialogs.DeleteAlertPopupDialog
import com.varitas.gokulpos.tablet.fragmentDialogs.VendorPopupDialog
import com.varitas.gokulpos.tablet.model.Header
import com.varitas.gokulpos.tablet.response.Vendor
import com.varitas.gokulpos.tablet.utilities.Default
import com.varitas.gokulpos.tablet.utilities.Enums
import com.varitas.gokulpos.tablet.viewmodel.ProductFeatureViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint class VendorActivity : BaseActivity() {

    private lateinit var binding: ActivityVendorBinding
    val viewModel: ProductFeatureViewModel by viewModels()
    private lateinit var vendorAdapter: VendorAdapter
    private var parentId = 0
    private var isFromOrder = false

    companion object {
        lateinit var Instance: VendorActivity
    }

    init {
        Instance = this
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVendorBinding.inflate(layoutInflater)
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
        parentId = if (intent.extras?.getInt(Default.PARENT_ID) != null) intent.extras?.getInt(Default.PARENT_ID)!! else 0
        isFromOrder = if (intent.extras?.getBoolean(Default.ORDER) != null) intent.extras?.getBoolean(Default.ORDER)!! else false
        binding.apply {
            layoutToolbar.textViewToolbarName.text = resources.getString(R.string.Menu_Vendor)
            layoutToolbar.imageViewAction.setImageDrawable(ContextCompat.getDrawable(this@VendorActivity, R.drawable.ic_home))
            layoutToolbar.imageViewBack.visibility = View.VISIBLE
        }
        vendorAdapter = VendorAdapter { vendor, i, isFromDelete ->
            if (isFromDelete) {
                hideKeyBoard(this)
                val ft = supportFragmentManager.beginTransaction()
                val dialogFragment = DeleteAlertPopupDialog(resources.getString(R.string.DeletePermission, resources.getString(R.string.Menu_Vendor)),vendor.id!!, isVendor = true)
                val prevFragment: Fragment? = supportFragmentManager.findFragmentByTag(DeleteAlertPopupDialog::class.java.name)
                if (prevFragment != null) return@VendorAdapter
                dialogFragment.isCancelable = false
                dialogFragment.show(ft, DeleteAlertPopupDialog::class.java.name)
                dialogFragment.setListener(object : DeleteAlertPopupDialog.OnButtonClickListener {
                    override fun onButtonClickListener(typeButton: Enums.ClickEvents) {
                        when (typeButton) {
                            Enums.ClickEvents.DELETE -> {
                                vendorAdapter.apply {
                                    list.remove(vendor)
                                    filteredList.remove(vendor)
                                    notifyItemRemoved(i)
                                    notifyItemRangeChanged(i, list.size)
                                }
                            }
                            else -> {}
                        }
                    }
                })

            } else manageVendor(vendorId = vendor.id!!)
        }
    }

    private fun postInitView() {
        binding.apply {
            recycleViewProducts.apply {
                adapter = vendorAdapter
                layoutManager = LinearLayoutManager(this@VendorActivity)
            }
            layoutHeader.data = Header(resources.getString(R.string.lbl_Id), resources.getString(R.string.Menu_Vendor), resources.getString(R.string.lbl_Code), resources.getString(R.string.lbl_CompanyName), resources.getString(R.string.lbl_Action))
        }
    } //endregion

    //region To manage click events
    private fun manageClicks() {
        binding.apply {
            layoutToolbar.imageViewAction.clickWithDebounce {
                moveToOrder(parentId)
            }

            layoutToolbar.imageViewBack.clickWithDebounce {
                moveToSubMenu(parentId,isFromOrder)
            }

            searchProduct.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String): Boolean {
                    return false
                }

                override fun onQueryTextChange(newText: String): Boolean {
                    vendorAdapter.getFilter().filter(newText)
                    return true
                }
            })
            buttonAddVendor.clickWithDebounce {
                manageVendor()
            }
            buttonAddSalePerson.clickWithDebounce {
                addSalePerson()
            }
            swipeRefresh.apply {
                setOnRefreshListener {
                    swipeRefresh.isRefreshing = false
                    getVendors()
                    clearSearch()
                }
                setColorSchemeColors(ContextCompat.getColor(this@VendorActivity, R.color.pink))
            }
        }
    } //endregion

    @Deprecated("Deprecated in Java") override fun onBackPressed() {
        moveToSubMenu(parentId,isFromOrder)
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

        getVendors()
    }

    private fun getVendors() {
        viewModel.fetchVendors().observe(this) {
            CoroutineScope(Dispatchers.Main).launch {
                vendorAdapter.apply {
                    setList(it)
                }
            }
        }
    } //endregion

    //region To manage vendor
    private fun manageVendor(vendorId: Int = 0) {
        if (vendorId > 0) {
            viewModel.getVendorDetails(vendorId).observe(this) {
                val bundle = Bundle()
                bundle.putParcelable(Default.VENDOR, it!!)
                openVendorPopup(bundle)
            }
        } else openVendorPopup()
    }

    private fun openVendorPopup(bundle: Bundle? = null) {
        val ft = supportFragmentManager.beginTransaction()
        val dialogFragment = VendorPopupDialog.newInstance()
        val prevFragment: Fragment? = supportFragmentManager.findFragmentByTag(VendorPopupDialog::class.java.name)
        if (prevFragment != null) return
        if (bundle != null) dialogFragment.arguments = bundle
        dialogFragment.isCancelable = false
        dialogFragment.show(ft, VendorPopupDialog::class.java.name)
        dialogFragment.setListener(object : VendorPopupDialog.OnButtonClickListener {
            override fun onButtonClickListener(typeButton: Enums.ClickEvents, vendor: Vendor?, isUpdate: Boolean) {
                clearSearch()
                when (typeButton) {
                    Enums.ClickEvents.CANCEL -> {
                        if (dialogFragment.isVisible) dialogFragment.dismiss()
                    }

                    Enums.ClickEvents.SAVE -> {
                        if (dialogFragment.isVisible) dialogFragment.dismiss()
                        if (!isUpdate) getVendors()
                        else {
                            if (vendor != null) {
                                getVendors()
                            }
                        }
                    }

                    Enums.ClickEvents.ERROR -> {
                        if (dialogFragment.isVisible) dialogFragment.dismiss()
                        showSweetDialog(this@VendorActivity, resources.getString(R.string.lbl_SomethingWrong))
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

    //region To add sale person
    private fun addSalePerson() {
        val ft = supportFragmentManager.beginTransaction()
        val dialogFragment = AddSalePersonPopupDialog.newInstance()
        val prevFragment: Fragment? = supportFragmentManager.findFragmentByTag(AddSalePersonPopupDialog::class.java.name)
        if (prevFragment != null) return
        dialogFragment.isCancelable = false
        dialogFragment.show(ft, AddSalePersonPopupDialog::class.java.name)
        dialogFragment.setListener(object : AddSalePersonPopupDialog.OnButtonClickListener {
            override fun onButtonClickListener(typeButton: Enums.ClickEvents, vendor: Vendor?, isUpdate: Boolean) {
                clearSearch()
                if (dialogFragment.isVisible) dialogFragment.dismiss()
            }
        })
    }
    //endregion
}