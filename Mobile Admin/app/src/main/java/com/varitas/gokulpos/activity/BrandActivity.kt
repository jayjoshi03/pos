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
import com.varitas.gokulpos.adapter.BrandAdapter
import com.varitas.gokulpos.databinding.ActivityCommonBinding
import com.varitas.gokulpos.fragmentDialogs.BrandPopupDialog
import com.varitas.gokulpos.model.Header
import com.varitas.gokulpos.response.Brand
import com.varitas.gokulpos.utilities.Default
import com.varitas.gokulpos.utilities.Enums
import com.varitas.gokulpos.viewmodel.ProductFeatureViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint class BrandActivity : BaseActivity() {

    private lateinit var binding : ActivityCommonBinding
    val viewModel : ProductFeatureViewModel by viewModels()
    private lateinit var brandAdapter : BrandAdapter
    private var parentId = 0

    companion object {
        lateinit var Instance : BrandActivity
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
        parentId = if(intent.extras?.getInt(Default.PARENT_ID) != null) intent.extras?.getInt(Default.PARENT_ID)!! else 0
        binding.apply {
            layoutToolbar.textViewToolbarName.text = resources.getString(R.string.Menu_Brand)
            layoutToolbar.imageViewAction.setImageDrawable(ContextCompat.getDrawable(this@BrandActivity, R.drawable.ic_home))
            layoutToolbar.imageViewBack.visibility = View.VISIBLE
        }
        brandAdapter = BrandAdapter { brand, i, isFromDelete, isItemCount->
            if(isItemCount) {
                //if(brand.itemCount!! >= 0)
                manageItemCount(brand.id!!, Enums.Menus.BRAND)
                clearSearch()
            } else {
                if(isFromDelete) {
                    hideKeyBoard(this)
                    val sweetAlertDialog = SweetAlertDialog(this@BrandActivity, SweetAlertDialog.CUSTOM_IMAGE_TYPE)
                    sweetAlertDialog.apply {
                        setCanceledOnTouchOutside(false)
                        setCancelable(false)
                        contentText = resources.getString(R.string.lbl_DeletePermission, resources.getString(R.string.Menu_Brand))
                        confirmText = resources.getString(R.string.lbl_Yes)
                        cancelText = resources.getString(R.string.lbl_No)
                        confirmButtonBackgroundColor = ContextCompat.getColor(this@BrandActivity, R.color.base_color)
                        cancelButtonBackgroundColor = ContextCompat.getColor(this@BrandActivity, R.color.pink)
                        setConfirmClickListener { sDialog->
                            sDialog.dismissWithAnimation()
                            viewModel.deleteBrand(brand.id!!).observe(this@BrandActivity) {
                                CoroutineScope(Dispatchers.Main).launch {
                                    if(it) {
                                        brandAdapter.apply {
                                            list.remove(brand)
                                            filteredList.remove(brand)
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
                } else manageBrand(brandId = brand.id!!, pos = i)
            }
        }
    }

    private fun postInitView() {
        binding.apply {
            recycleViewProducts.apply {
                adapter = brandAdapter
                layoutManager = LinearLayoutManager(this@BrandActivity)
            }
            layoutHeader.data = Header(resources.getString(R.string.lbl_Id), resources.getString(R.string.Menu_Brand), resources.getString(R.string.lbl_Items))
        }
    } //endregion

    //region To manage click events
    private fun manageClicks() {
        binding.apply {
            layoutToolbar.imageViewAction.clickWithDebounce {
                openActivity(Intent(this@BrandActivity, DashboardActivity::class.java))
            }

            layoutToolbar.imageViewBack.clickWithDebounce {
                moveToProducts(parentId)
            }

            searchProduct.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query : String) : Boolean {
                    return false
                }

                override fun onQueryTextChange(newText : String) : Boolean {
                    brandAdapter.getFilter().filter(newText)
                    return true
                }
            })
            fabAdd.setOnClickListener {
                manageBrand()
            }
            swipeRefresh.apply {
                setOnRefreshListener {
                    swipeRefresh.isRefreshing = false
                    getBrands()
                    clearSearch()
                }
                setColorSchemeColors(ContextCompat.getColor(this@BrandActivity, R.color.pink))
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

        getBrands()
    }

    private fun getBrands() {
        binding.linearLayoutShimmer.visibility = View.VISIBLE
        viewModel.fetchBrands().observe(this) {
            CoroutineScope(Dispatchers.Main).launch {
                binding.linearLayoutShimmer.visibility = View.GONE
                brandAdapter.apply {
                    setList(it)
                }
            }
        }
    } //endregion

    //region To manage brand
    private fun manageBrand(brandId : Int = 0, pos : Int = 0) {
        if(brandId > 0) {
            viewModel.getBrandDetails(brandId).observe(this) {
                val bundle = Bundle()
                bundle.putParcelable(Default.BRAND, it!!)
                openBrandPopup(bundle, pos)
            }
        } else openBrandPopup()
    }

    private fun openBrandPopup(bundle : Bundle? = null, pos : Int = 0) {
        val ft = supportFragmentManager.beginTransaction()
        val dialogFragment = BrandPopupDialog.newInstance()
        val prevFragment : Fragment? = supportFragmentManager.findFragmentByTag(BrandPopupDialog::class.java.name)
        if(prevFragment != null) return
        if(bundle != null) dialogFragment.arguments = bundle
        dialogFragment.isCancelable = false
        dialogFragment.show(ft, BrandPopupDialog::class.java.name)
        dialogFragment.setListener(object : BrandPopupDialog.OnButtonClickListener {
            override fun onButtonClickListener(typeButton : Enums.ClickEvents, brand : Brand?, isUpdate : Boolean) {
                clearSearch()
                when(typeButton) {
                    Enums.ClickEvents.CANCEL -> {
                        if(dialogFragment.isVisible) dialogFragment.dismiss()
                    }

                    Enums.ClickEvents.SAVE -> {
                        if(dialogFragment.isVisible) dialogFragment.dismiss()
                        if(!isUpdate) getBrands()
                        else {
                            if(brand != null) {
                                brandAdapter.apply {
                                    list[pos].name = brand.name
                                    notifyItemChanged(pos)
                                }
                            }
                        }
                    }

                    Enums.ClickEvents.ERROR -> {
                        if(dialogFragment.isVisible) dialogFragment.dismiss()
                        showSweetDialog(this@BrandActivity, resources.getString(R.string.lbl_SomethingWrong))
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

    override fun onResume() {
        binding.shimmer.startShimmer()
        super.onResume()
    }

    override fun onPause() {
        binding.shimmer.stopShimmer()
        super.onPause()
    }
}