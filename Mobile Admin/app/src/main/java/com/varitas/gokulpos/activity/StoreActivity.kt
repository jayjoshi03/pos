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
import com.varitas.gokulpos.adapter.StoreAdapter
import com.varitas.gokulpos.databinding.ActivityCommonBinding
import com.varitas.gokulpos.model.Header
import com.varitas.gokulpos.response.StoreStatus
import com.varitas.gokulpos.utilities.Default
import com.varitas.gokulpos.viewmodel.OrdersViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class StoreActivity : BaseActivity() {
    lateinit var binding : ActivityCommonBinding
    val viewModel : OrdersViewModel by viewModels()
    private lateinit var storeAdapter : StoreAdapter

    companion object {
        lateinit var Instance : StoreActivity
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
            layoutToolbar.textViewToolbarName.text = resources.getString(R.string.Menu_Store)
            layoutToolbar.imageViewAction.setImageDrawable(ContextCompat.getDrawable(this@StoreActivity, R.drawable.ic_home))
            layoutToolbar.imageViewBack.visibility = View.VISIBLE
        }

        storeAdapter = StoreAdapter { store, i->
            hideKeyBoard(this)
            val sweetAlertDialog = SweetAlertDialog(this@StoreActivity, SweetAlertDialog.CUSTOM_IMAGE_TYPE)
            sweetAlertDialog.apply {
                setCanceledOnTouchOutside(false)
                setCancelable(false)
                contentText = resources.getString(R.string.lbl_DeletePermission, resources.getString(R.string.Menu_Store))
                confirmText = resources.getString(R.string.lbl_Approval)
                cancelText = resources.getString(R.string.lbl_Reject)
                confirmButtonBackgroundColor = ContextCompat.getColor(this@StoreActivity, R.color.base_color)
                cancelButtonBackgroundColor = ContextCompat.getColor(this@StoreActivity, R.color.pink)
                setConfirmClickListener { sDialog->
                    sDialog.dismissWithAnimation()
                    val req = StoreStatus(
                        storeId = store.id,
                        statusId = Default.ACTIVE
                    )
                    viewModel.updateStoreStatus(req).observe(this@StoreActivity){
                    }
                }
                setCancelClickListener { sDialog->
                    sDialog.dismissWithAnimation()
                    val req = StoreStatus(
                        storeId = store.id,
                        statusId = Default.REJECT
                    )
                    viewModel.updateStoreStatus(req).observe(this@StoreActivity){
                    }
                }
                show()
            }
        }
    }

    private fun postInitView() {
        binding.apply {
            recycleViewProducts.apply {
                adapter = storeAdapter
                layoutManager = LinearLayoutManager(this@StoreActivity)
            }
            layoutHeader.data = Header(resources.getString(R.string.lbl_Id), resources.getString(R.string.Menu_Store))
            fabAdd.visibility = View.INVISIBLE
        }
    }
    //endregion

    //region To manage click
    private fun manageClicks() {
        binding.apply {
            layoutToolbar.imageViewAction.clickWithDebounce {
                openActivity(Intent(this@StoreActivity, DashboardActivity::class.java))
            }

            layoutToolbar.imageViewBack.clickWithDebounce {
                openActivity(Intent(this@StoreActivity, DashboardActivity::class.java))
            }

            searchProduct.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query : String) : Boolean {
                    return false
                }

                override fun onQueryTextChange(newText : String) : Boolean {
                    storeAdapter.getFilter().filter(newText)
                    return true
                }
            })

            swipeRefresh.apply {
                setOnRefreshListener {
                    swipeRefresh.isRefreshing = false
                    getStore()
                    clearSearch()
                }
                setColorSchemeColors(ContextCompat.getColor(this@StoreActivity, R.color.pink))
            }
        }
    }
    //endregion

    @Deprecated("Deprecated in Java") override fun onBackPressed() {
        openActivity(Intent(this, DashboardActivity::class.java))
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
        getStore()
    }

    private fun getStore() {
        binding.linearLayoutShimmer.visibility = View.VISIBLE
        viewModel.getStoreList(Default.PENDING_STORE).observe(this) {
            CoroutineScope(Dispatchers.Main).launch {
                binding.linearLayoutShimmer.visibility = View.GONE
                storeAdapter.apply {
                    setList(it)
                }
            }
        }
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