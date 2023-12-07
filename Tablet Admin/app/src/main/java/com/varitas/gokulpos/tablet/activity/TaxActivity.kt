package com.varitas.gokulpos.tablet.activity

import android.os.Bundle
import android.view.View
import android.widget.SearchView
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.varitas.gokulpos.tablet.R
import com.varitas.gokulpos.tablet.adapter.TaxAdapter
import com.varitas.gokulpos.tablet.databinding.ActivityCommonBinding
import com.varitas.gokulpos.tablet.fragmentDialogs.DeleteAlertPopupDialog
import com.varitas.gokulpos.tablet.fragmentDialogs.TaxPopupDialog
import com.varitas.gokulpos.tablet.model.Header
import com.varitas.gokulpos.tablet.response.Tax
import com.varitas.gokulpos.tablet.utilities.Default
import com.varitas.gokulpos.tablet.utilities.Enums
import com.varitas.gokulpos.tablet.viewmodel.ProductFeatureViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint class TaxActivity : BaseActivity() {

    private lateinit var binding: ActivityCommonBinding
    val viewModel: ProductFeatureViewModel by viewModels()
    private lateinit var taxAdapter: TaxAdapter
    private var parentId = 0
    private var isFromOrder = false

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
        parentId = if (intent.extras?.getInt(Default.PARENT_ID) != null) intent.extras?.getInt(Default.PARENT_ID)!! else 0
        isFromOrder = if (intent.extras?.getBoolean(Default.ORDER) != null) intent.extras?.getBoolean(Default.ORDER)!! else false
        binding.apply {
            layoutToolbar.textViewToolbarName.text = resources.getString(R.string.Menu_Tax)
            layoutToolbar.imageViewAction.setImageDrawable(ContextCompat.getDrawable(this@TaxActivity, R.drawable.ic_home))
            layoutToolbar.imageViewBack.visibility = View.VISIBLE
        }
        taxAdapter = TaxAdapter { tax, i, isFromDelete ->
            if (isFromDelete) {
                hideKeyBoard(this)
                val ft = supportFragmentManager.beginTransaction()
                val dialogFragment = DeleteAlertPopupDialog(resources.getString(R.string.DeletePermission, resources.getString(R.string.Menu_Tax)),tax.id!!, isTax = true)
                val prevFragment: Fragment? = supportFragmentManager.findFragmentByTag(DeleteAlertPopupDialog::class.java.name)
                if (prevFragment != null) return@TaxAdapter
                dialogFragment.isCancelable = false
                dialogFragment.show(ft, DeleteAlertPopupDialog::class.java.name)
                dialogFragment.setListener(object : DeleteAlertPopupDialog.OnButtonClickListener {
                    override fun onButtonClickListener(typeButton: Enums.ClickEvents) {
                        when (typeButton) {
                            Enums.ClickEvents.DELETE -> {
                                taxAdapter.apply {
                                    list.remove(tax)
                                    filteredList.remove(tax)
                                    notifyItemRemoved(i)
                                    notifyItemRangeChanged(i, list.size)
                                }
                            }
                            else -> {}
                        }
                    }
                })
            } else manageTax(id = tax.id!!, pos = i)
        }
    }

    private fun postInitView() {
        binding.apply {
            recycleViewProducts.apply {
                adapter = taxAdapter
                layoutManager = LinearLayoutManager(this@TaxActivity)
            }
            layoutHeader.data = Header(resources.getString(R.string.lbl_Id), resources.getString(R.string.Menu_Tax), resources.getString(R.string.lbl_Rate), title4 = resources.getString(R.string.lbl_Action))
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

        getTaxes()
    }

    private fun getTaxes() {
        viewModel.fetchTaxes().observe(this) {
            CoroutineScope(Dispatchers.Main).launch {
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
}