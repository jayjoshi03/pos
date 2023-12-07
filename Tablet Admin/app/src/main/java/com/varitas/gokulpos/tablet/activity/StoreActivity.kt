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
import com.varitas.gokulpos.tablet.adapter.StoreAdapter
import com.varitas.gokulpos.tablet.databinding.ActivityCommonBinding
import com.varitas.gokulpos.tablet.fragmentDialogs.DeleteAlertPopupDialog
import com.varitas.gokulpos.tablet.model.Header
import com.varitas.gokulpos.tablet.model.StoreUpdate
import com.varitas.gokulpos.tablet.utilities.Default
import com.varitas.gokulpos.tablet.utilities.Enums
import com.varitas.gokulpos.tablet.viewmodel.OrdersViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class StoreActivity : BaseActivity() {
    lateinit var binding: ActivityCommonBinding
    val viewModel: OrdersViewModel by viewModels()
    private lateinit var storeAdapter: StoreAdapter

    companion object {
        lateinit var Instance: StoreActivity
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
        binding.apply {
            layoutToolbar.textViewToolbarName.text = resources.getString(R.string.Menu_Store)
            layoutToolbar.imageViewAction.setImageDrawable(ContextCompat.getDrawable(this@StoreActivity, R.drawable.ic_home))
            layoutToolbar.imageViewBack.visibility = View.VISIBLE
            fabAdd.visibility = View.GONE
        }

        storeAdapter = StoreAdapter { store, i, enum ->
            hideKeyBoard(this)
            val ft = supportFragmentManager.beginTransaction()
            val str = resources.getString(R.string.StorePermission, if (enum == Enums.ClickEvents.DELETE) resources.getString(R.string.lbl_Reject).lowercase() else resources.getString(R.string.lbl_Approve).lowercase(), store.name!!.uppercase())
            val dialogFragment = DeleteAlertPopupDialog(str, 0, changeStoreStatus = true)
            val prevFragment: Fragment? = supportFragmentManager.findFragmentByTag(DeleteAlertPopupDialog::class.java.name)
            if (prevFragment != null) return@StoreAdapter
            dialogFragment.isCancelable = false
            dialogFragment.show(ft, DeleteAlertPopupDialog::class.java.name)
            dialogFragment.setListener(object : DeleteAlertPopupDialog.OnButtonClickListener {
                override fun onButtonClickListener(typeButton: Enums.ClickEvents) {
                    when (typeButton) {
                        Enums.ClickEvents.DELETE -> {
                            viewModel.updateStoreStatus(StoreUpdate(storeId = store.id, statusId = if (enum == Enums.ClickEvents.DELETE) Default.REJECTED else Default.ACTIVE)).observe(this@StoreActivity){
                                if(it){
                                    storeAdapter.apply {
                                        list.remove(store)
                                        filteredList.remove(store)
                                        notifyItemRemoved(i)
                                        notifyItemRangeChanged(i, list.size)
                                    }
                                }
                            }
                        }

                        else -> {}
                    }
                }
            })
        }
    }

    private fun postInitView() {
        binding.apply {
            recycleViewProducts.apply {
                adapter = storeAdapter
                layoutManager = LinearLayoutManager(this@StoreActivity)
            }
            layoutHeader.data = Header(resources.getString(R.string.lbl_Id), resources.getString(R.string.lbl_StoreName), resources.getString(R.string.lbl_EmailId), title3 = resources.getString(R.string.lbl_BusinessName), title4 = resources.getString(R.string.lbl_Action))
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
                override fun onQueryTextSubmit(query: String): Boolean {
                    return false
                }

                override fun onQueryTextChange(newText: String): Boolean {
                    storeAdapter.getFilter().filter(newText)
                    return true
                }
            })

            swipeRefresh.apply {
                setOnRefreshListener {
                    swipeRefresh.isRefreshing = false
                    getPendingStoreList()
                    clearSearch()
                }
                setColorSchemeColors(ContextCompat.getColor(this@StoreActivity, R.color.pink))
            }
        }
    }
    //endregion

    @Deprecated("Deprecated in Java") override fun onBackPressed() {
        openActivity(Intent(this@StoreActivity, DashboardActivity::class.java))
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

        getPendingStoreList()
    }

    private fun getPendingStoreList() {
        viewModel.getStoreList(Default.PENDING_APPROVAL).observe(this) {
            CoroutineScope(Dispatchers.Main).launch {
                storeAdapter.apply {
                    setList(it)
                }
            }
        }
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