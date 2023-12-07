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
import com.varitas.gokulpos.adapter.PurchaseOrderAdapter
import com.varitas.gokulpos.databinding.ActivityCommonBinding
import com.varitas.gokulpos.model.Header
import com.varitas.gokulpos.utilities.Default
import com.varitas.gokulpos.viewmodel.OrdersViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PurchaseOrderActivity : BaseActivity() {
    lateinit var binding : ActivityCommonBinding
    val viewModel : OrdersViewModel by viewModels()
    private lateinit var purchaseOrderAdapter : PurchaseOrderAdapter
    private var parentId = 0

    companion object {
        lateinit var Instance : PurchaseOrderActivity
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

        purchaseOrderAdapter = PurchaseOrderAdapter { purchase, i, isFromDelete, isMakeInvoice->
            if(isMakeInvoice) {
                viewModel.generateInvoice(purchase.id!!).observe(this) {
                    if(it) {
                        purchaseOrderAdapter.apply {
                            purchase.isMake = true
                            notifyItemChanged(i)
                        }
                    }
                }
            } else {
                if(isFromDelete) {
                    if(purchase.status != Default.CANCEL) {
                        hideKeyBoard(this)
                        val sweetAlertDialog = SweetAlertDialog(this@PurchaseOrderActivity, SweetAlertDialog.CUSTOM_IMAGE_TYPE)
                        sweetAlertDialog.apply {
                            setCanceledOnTouchOutside(false)
                            setCancelable(false)
                            contentText = resources.getString(R.string.lbl_CancelPermission, resources.getString(R.string.Menu_PurchaseOrder))
                            confirmText = resources.getString(R.string.lbl_Yes)
                            cancelText = resources.getString(R.string.lbl_No)
                            confirmButtonBackgroundColor = ContextCompat.getColor(this@PurchaseOrderActivity, R.color.base_color)
                            cancelButtonBackgroundColor = ContextCompat.getColor(this@PurchaseOrderActivity, R.color.pink)
                            setConfirmClickListener { sDialog->
                                sDialog.dismissWithAnimation()
                                viewModel.deletePurchaseOrder(purchase.id!!).observe(this@PurchaseOrderActivity) {
                                    CoroutineScope(Dispatchers.Main).launch {

                                        if(it) {
                                            purchaseOrderAdapter.apply {
                                                list[i].status = Default.CANCEL
                                                notifyItemChanged(i)
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
                    } else showSweetDialog(this@PurchaseOrderActivity, resources.getString(R.string.CancelOrder, resources.getString(R.string.lbl_Already)))
                } else managePurchaseOrder(purchase.id!!)
            }
        }
    }

    private fun postInitView() {
        binding.apply {
            layoutToolbar.textViewToolbarName.text = resources.getString(R.string.Menu_PurchaseOrder)
            layoutToolbar.imageViewAction.setImageDrawable(ContextCompat.getDrawable(this@PurchaseOrderActivity, R.drawable.ic_home))
            layoutToolbar.imageViewBack.visibility = View.VISIBLE
            linearLayoutPurchase.visibility = View.VISIBLE
            recycleViewProducts.apply {
                adapter = purchaseOrderAdapter
                layoutManager = LinearLayoutManager(this@PurchaseOrderActivity)
            }
            layoutHeader.data = Header(resources.getString(R.string.lbl_Id), resources.getString(R.string.Menu_PurchaseOrder))
        }
    }
    //endregion

    //region To manage click
    private fun manageClicks() {
        binding.apply {
            layoutToolbar.imageViewAction.clickWithDebounce {
                openActivity(Intent(this@PurchaseOrderActivity, DashboardActivity::class.java))
            }

            layoutToolbar.imageViewBack.clickWithDebounce {
                moveToProducts(parentId)
            }

            searchProduct.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query : String) : Boolean {
                    return false
                }

                override fun onQueryTextChange(newText : String) : Boolean {
                    purchaseOrderAdapter.getFilter().filter(newText)
                    return true
                }
            })

            fabAdd.clickWithDebounce {
                val intent = Intent(this@PurchaseOrderActivity, AddPurchaseOrderActivity::class.java)
                intent.putExtra(Default.ID, 0)
                intent.putExtra(Default.PARENT_ID, parentId)
                openActivity(intent)
            }

            swipeRefresh.apply {
                setOnRefreshListener {
                    swipeRefresh.isRefreshing = false
                    getPurchaseOrder()
                    clearSearch()
                }
                setColorSchemeColors(ContextCompat.getColor(this@PurchaseOrderActivity, R.color.pink))
            }
        }
    }
    //endregion

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
        getPurchaseOrder()
    }

    private fun getPurchaseOrder() {
        binding.linearLayoutShimmer.visibility = View.VISIBLE
        viewModel.getPurchaseOrderList(Default.ALL_DATA).observe(this) {
            CoroutineScope(Dispatchers.Main).launch {
                binding.linearLayoutShimmer.visibility = View.GONE
                purchaseOrderAdapter.apply {
                    list.clear()
                    setList(it)
                }
            }
        }
    }
    //endregion

    //region To manage Purchase Order
    private fun managePurchaseOrder(id : Int = 0) {
        val intent = Intent(this@PurchaseOrderActivity, AddPurchaseOrderActivity::class.java)
        intent.putExtra(Default.ID, if(id > 0) id else 0)
        intent.putExtra(Default.PARENT_ID, parentId)
        openActivity(intent)
    }
    //endregion

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