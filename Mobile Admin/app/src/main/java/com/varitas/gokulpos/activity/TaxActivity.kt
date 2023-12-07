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
import com.varitas.gokulpos.adapter.TaxAdapter
import com.varitas.gokulpos.databinding.ActivityCommonBinding
import com.varitas.gokulpos.fragmentDialogs.TaxPopupDialog
import com.varitas.gokulpos.model.Header
import com.varitas.gokulpos.response.Tax
import com.varitas.gokulpos.utilities.Default
import com.varitas.gokulpos.utilities.Enums
import com.varitas.gokulpos.viewmodel.ProductFeatureViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint class TaxActivity : BaseActivity() {

    private lateinit var binding: ActivityCommonBinding
    val viewModel: ProductFeatureViewModel by viewModels()
    private lateinit var taxAdapter: TaxAdapter
    private var parentId = 0

    companion object {
        lateinit var Instance: TaxActivity
    }

    init {
        Instance = this
    }

    override fun onCreate(savedInstanceState: Bundle?) {
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
            layoutToolbar.textViewToolbarName.text = resources.getString(R.string.Menu_Tax)
            layoutToolbar.imageViewAction.setImageDrawable(ContextCompat.getDrawable(this@TaxActivity, R.drawable.ic_home))
            layoutToolbar.imageViewBack.visibility = View.VISIBLE
        }
        taxAdapter = TaxAdapter { tax, i, isFromDelete ->
            if (isFromDelete) {
                hideKeyBoard(this)
                val sweetAlertDialog = SweetAlertDialog(this@TaxActivity, SweetAlertDialog.CUSTOM_IMAGE_TYPE)
                sweetAlertDialog.apply {
                    setCanceledOnTouchOutside(false)
                    setCancelable(false)
                    contentText = resources.getString(R.string.lbl_DeletePermission, resources.getString(R.string.Menu_Tax))
                    confirmText = resources.getString(R.string.lbl_Yes)
                    cancelText = resources.getString(R.string.lbl_No)
                    confirmButtonBackgroundColor = ContextCompat.getColor(this@TaxActivity, R.color.base_color)
                    cancelButtonBackgroundColor = ContextCompat.getColor(this@TaxActivity, R.color.pink)
                    setConfirmClickListener { sDialog ->
                        sDialog.dismissWithAnimation()
                        viewModel.deleteTax(tax.id!!).observe(this@TaxActivity) {
                            CoroutineScope(Dispatchers.Main).launch {
                                if (it) {
                                    taxAdapter.apply {
                                        list.remove(tax)
                                        filteredList.remove(tax)
                                        notifyItemRemoved(i)
                                        notifyItemRangeChanged(i, list.size)
                                    }
                                }
                            }
                        }
                    }
                    setCancelClickListener { sDialog ->
                        sDialog.dismissWithAnimation()
                    }
                    show()
                }
            } else manageTax(id = tax.id!!, pos = i)
        }
    }

    private fun postInitView() {
        binding.apply {
            recycleViewProducts.apply {
                adapter = taxAdapter
                layoutManager = LinearLayoutManager(this@TaxActivity)
            }
            layoutHeader.data = Header(resources.getString(R.string.lbl_Id), resources.getString(R.string.Menu_Tax))
        }
    } //endregion

    //region To manage click events
    private fun manageClicks() {
        binding.apply {
            layoutToolbar.imageViewAction.clickWithDebounce {
                openActivity(Intent(this@TaxActivity, DashboardActivity::class.java))
            }

            layoutToolbar.imageViewBack.clickWithDebounce {
                moveToProducts(parentId)
            }

            searchProduct.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String): Boolean {
                    return false
                }

                override fun onQueryTextChange(newText: String): Boolean {
                    taxAdapter.getFilter().filter(newText)
                    return true
                }
            })
            fabAdd.setOnClickListener {
                manageTax()
            }
            swipeRefresh.apply {
                setOnRefreshListener {
                    swipeRefresh.isRefreshing = false
                    getTaxes()
                    clearSearch()
                }
                setColorSchemeColors(ContextCompat.getColor(this@TaxActivity, R.color.pink))
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
        getTaxes()
    }

    private fun getTaxes() {
        binding.linearLayoutShimmer.visibility = View.VISIBLE
        viewModel.fetchTaxes().observe(this) {
            CoroutineScope(Dispatchers.Main).launch {
                binding.linearLayoutShimmer.visibility = View.GONE
                taxAdapter.apply {
                    setList(it)
                }
            }
        }
    } //endregion

    //region To manage brand
    private fun manageTax(id: Int = 0, pos: Int = 0) {
        if (id > 0) {
            viewModel.getTaxDetails(id).observe(this) {
                val bundle = Bundle()
                bundle.putParcelable(Default.TAX, it!!)
                openTaxPopup(bundle, pos)
            }
        } else openTaxPopup()
    }

    private fun openTaxPopup(bundle: Bundle? = null, pos: Int = 0) {
        val ft = supportFragmentManager.beginTransaction()
        val dialogFragment = TaxPopupDialog.newInstance()
        val prevFragment: Fragment? = supportFragmentManager.findFragmentByTag(TaxPopupDialog::class.java.name)
        if (prevFragment != null) return
        if (bundle != null) dialogFragment.arguments = bundle
        dialogFragment.isCancelable = false
        dialogFragment.show(ft, TaxPopupDialog::class.java.name)
        dialogFragment.setListener(object : TaxPopupDialog.OnButtonClickListener {
            override fun onButtonClickListener(typeButton: Enums.ClickEvents, tax: Tax?, isUpdate: Boolean) {
                clearSearch()
                when (typeButton) {
                    Enums.ClickEvents.CANCEL -> {
                        if (dialogFragment.isVisible) dialogFragment.dismiss()
                    }

                    Enums.ClickEvents.SAVE -> {
                        if (dialogFragment.isVisible) dialogFragment.dismiss()
                        if (!isUpdate) getTaxes()
                        else {
                            if (tax != null) {
                                taxAdapter.apply {
                                    list[pos].className = tax.className
                                    list[pos].rateValue = tax.rateValue
                                    notifyItemChanged(pos)
                                }
                            }
                        }
                    }

                    Enums.ClickEvents.ERROR -> {
                        if (dialogFragment.isVisible) dialogFragment.dismiss()
                        showSweetDialog(this@TaxActivity, resources.getString(R.string.lbl_SomethingWrong))
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