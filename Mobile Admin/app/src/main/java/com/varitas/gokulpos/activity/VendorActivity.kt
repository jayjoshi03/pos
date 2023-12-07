package com.varitas.gokulpos.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.SearchView
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import cn.pedant.SweetAlert.SweetAlertDialog
import com.varitas.gokulpos.R
import com.varitas.gokulpos.adapter.VendorAdapter
import com.varitas.gokulpos.databinding.ActivityVendorBinding
import com.varitas.gokulpos.fragmentDialogs.SalesPersonCountPopupDialog
import com.varitas.gokulpos.fragmentDialogs.SalesPersonPopupDialog
import com.varitas.gokulpos.fragmentDialogs.VendorPopupDialog
import com.varitas.gokulpos.model.Header
import com.varitas.gokulpos.response.Vendor
import com.varitas.gokulpos.utilities.Default
import com.varitas.gokulpos.utilities.Enums
import com.varitas.gokulpos.viewmodel.ProductFeatureViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint class VendorActivity : BaseActivity() {

    private lateinit var binding : ActivityVendorBinding
    val viewModel : ProductFeatureViewModel by viewModels()
    private lateinit var vendorAdapter : VendorAdapter
    private var parentId = 0

    companion object {
        lateinit var Instance : VendorActivity
    }

    init {
        Instance = this
    }

    override fun onCreate(savedInstanceState : Bundle?) {
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
        parentId = if(intent.extras?.getInt(Default.PARENT_ID) != null) intent.extras?.getInt(Default.PARENT_ID)!! else 0
        binding.apply {
            layoutToolbar.textViewToolbarName.text = resources.getString(R.string.Menu_Vendor)
            layoutToolbar.imageViewAction.setImageDrawable(ContextCompat.getDrawable(this@VendorActivity, R.drawable.ic_home))
            layoutToolbar.imageViewBack.visibility = View.VISIBLE
        }
        vendorAdapter = VendorAdapter { vendor, i, isFromDelete, isSalesPerson->
            if(isSalesPerson) {
                manageSalePerson(vendor.id!!, vendor.name!!)
                clearSearch()
            } else {
                if(isFromDelete) {
                    hideKeyBoard(this)
                    val sweetAlertDialog = SweetAlertDialog(this@VendorActivity, SweetAlertDialog.CUSTOM_IMAGE_TYPE)
                    sweetAlertDialog.apply {
                        setCanceledOnTouchOutside(false)
                        setCancelable(false)
                        contentText = resources.getString(R.string.lbl_DeletePermission, resources.getString(R.string.Menu_Vendor))
                        confirmText = resources.getString(R.string.lbl_Yes)
                        cancelText = resources.getString(R.string.lbl_No)
                        confirmButtonBackgroundColor = ContextCompat.getColor(this@VendorActivity, R.color.base_color)
                        cancelButtonBackgroundColor = ContextCompat.getColor(this@VendorActivity, R.color.pink)
                        setConfirmClickListener { sDialog->
                            sDialog.dismissWithAnimation()
                            viewModel.deleteVendor(vendor.id!!).observe(this@VendorActivity) {
                                CoroutineScope(Dispatchers.Main).launch {
                                    if(it) {
                                        vendorAdapter.apply {
                                            list.remove(vendor)
                                            filteredList.remove(vendor)
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
                } else manageVendor(vendorId = vendor.id!!, pos = i)
            }
        }
    }

    private fun postInitView() {
        binding.apply {
            recycleViewProducts.apply {
                adapter = vendorAdapter
                layoutManager = LinearLayoutManager(this@VendorActivity)
            }
            layoutHeader.data = Header(resources.getString(R.string.lbl_Id), resources.getString(R.string.Menu_Vendor), resources.getString(R.string.button_AddSalesPerson))
        }
    } //endregion

    //region To manage click events
    private fun manageClicks() {
        binding.apply {
            layoutToolbar.imageViewAction.clickWithDebounce {
                openActivity(Intent(this@VendorActivity, DashboardActivity::class.java))
            }

            layoutToolbar.imageViewBack.clickWithDebounce {
                moveToProducts(parentId)
            }

            searchProduct.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query : String) : Boolean {
                    return false
                }

                override fun onQueryTextChange(newText : String) : Boolean {
                    vendorAdapter.getFilter().filter(newText)
                    return true
                }
            })
            buttonAdd.setOnClickListener {
                manageVendor()
            }
            swipeRefresh.apply {
                setOnRefreshListener {
                    swipeRefresh.isRefreshing = false
                    getVendors()
                    clearSearch()
                }
                setColorSchemeColors(ContextCompat.getColor(this@VendorActivity, R.color.pink))
            }
            buttonAddSalesPerson.clickWithDebounce {
                openSalesPersonPopup()
            }
        }
    } //endregion

    @Deprecated("Deprecated in Java") override fun onBackPressed() {
        moveToProducts(parentId)
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
        binding.linearLayoutShimmer.visibility = View.VISIBLE
        viewModel.fetchVendors().observe(this) {
            CoroutineScope(Dispatchers.Main).launch {
                binding.linearLayoutShimmer.visibility = View.GONE
                vendorAdapter.apply {
                    setList(it)
                }
            }
        }
    } //endregion

    //region To manage vendor
    private fun manageVendor(vendorId : Int = 0, pos : Int = 0) {
        if(vendorId > 0) {
            viewModel.getVendorDetails(vendorId).observe(this) {
                val bundle = Bundle()
                bundle.putParcelable(Default.VENDOR, it!!)
                openVendorPopup(bundle, pos)
            }
        } else openVendorPopup()
    }

    private fun openVendorPopup(bundle : Bundle? = null, pos : Int = 0) {
        val ft = supportFragmentManager.beginTransaction()
        val dialogFragment = VendorPopupDialog.newInstance()
        val prevFragment : Fragment? = supportFragmentManager.findFragmentByTag(VendorPopupDialog::class.java.name)
        if(prevFragment != null) return
        if(bundle != null) dialogFragment.arguments = bundle
        dialogFragment.isCancelable = false
        dialogFragment.show(ft, VendorPopupDialog::class.java.name)
        dialogFragment.setListener(object : VendorPopupDialog.OnButtonClickListener {
            override fun onButtonClickListener(typeButton : Enums.ClickEvents, vendor : Vendor?, isUpdate : Boolean) {
                clearSearch()
                when(typeButton) {
                    Enums.ClickEvents.CANCEL -> {
                        if(dialogFragment.isVisible) dialogFragment.dismiss()
                    }

                    Enums.ClickEvents.SAVE -> {
                        if(dialogFragment.isVisible) dialogFragment.dismiss()
                        if(!isUpdate) getVendors()
                        else {
                            if(vendor != null) {
                                vendorAdapter.apply {
                                    list[pos].name = vendor.name
                                    notifyItemChanged(pos)
                                }
                            }
                        }
                    }

                    Enums.ClickEvents.ERROR -> {
                        if(dialogFragment.isVisible) dialogFragment.dismiss()
                        showSweetDialog(this@VendorActivity, resources.getString(R.string.lbl_SomethingWrong))
                    }

                    else -> {
                    }
                }
            }
        })
    } //endregion

    //region To Add Sales Person Popup Dialog
    private fun openSalesPersonPopup() {
        val ft = supportFragmentManager.beginTransaction()
        val dialogFragment = SalesPersonPopupDialog.newInstance()
        val prevFragment : Fragment? = supportFragmentManager.findFragmentByTag(SalesPersonPopupDialog::class.java.name)
        if(prevFragment != null) return
        dialogFragment.isCancelable = false
        dialogFragment.show(ft, SalesPersonPopupDialog::class.java.name)
        dialogFragment.setListener(object : SalesPersonPopupDialog.OnButtonClickListener {
            override fun onButtonClickListener(typeButton : Enums.ClickEvents) {
                clearSearch()
                when(typeButton) {
                    Enums.ClickEvents.CANCEL -> {
                        if(dialogFragment.isVisible) dialogFragment.dismiss()
                    }

                    Enums.ClickEvents.SAVE -> {
                        if(dialogFragment.isVisible) dialogFragment.dismiss()
                        getVendors()
                    }

                    Enums.ClickEvents.ERROR -> {
                        if(dialogFragment.isVisible) dialogFragment.dismiss()
                        showSweetDialog(this@VendorActivity, resources.getString(R.string.lbl_SomethingWrong))
                    }

                    else -> {}
                }
            }
        })
    } //endregion

    private fun manageSalePerson(id : Int, name : String) {
        val ft = supportFragmentManager.beginTransaction()
        val dialogFragment = SalesPersonCountPopupDialog.newInstance()
        val prevFragment : Fragment? = supportFragmentManager.findFragmentByTag(SalesPersonCountPopupDialog::class.java.name)
        if(prevFragment != null) return
        val bundle = Bundle()
        bundle.putString(Default.USER, name)
        bundle.putInt(Default.ID, id)
        dialogFragment.arguments = bundle
        dialogFragment.show(ft, SalesPersonCountPopupDialog::class.java.name)
        dialogFragment.setListener(object : SalesPersonCountPopupDialog.OnButtonClickListener {
            override fun onButtonClickListener(typeButton : Enums.ClickEvents) {
                if(dialogFragment.isVisible) dialogFragment.dismiss()
            }
        })
    }

    private fun clearSearch() {
        binding.apply {
            searchProduct.apply {
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
}