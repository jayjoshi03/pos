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
import com.varitas.gokulpos.tablet.adapter.SalesPromotionAdapter
import com.varitas.gokulpos.tablet.databinding.ActivityCommonBinding
import com.varitas.gokulpos.tablet.fragmentDialogs.DeleteAlertPopupDialog
import com.varitas.gokulpos.tablet.model.Header
import com.varitas.gokulpos.tablet.utilities.Default
import com.varitas.gokulpos.tablet.utilities.Enums
import com.varitas.gokulpos.tablet.viewmodel.OrdersViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint class SalesPromotionActivity : BaseActivity() {

    private lateinit var binding : ActivityCommonBinding
    val viewModel : OrdersViewModel by viewModels()
    private lateinit var salesPromotionAdapter : SalesPromotionAdapter

    companion object {
        lateinit var Instance : SalesPromotionActivity
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
            layoutToolbar.textViewToolbarName.text = resources.getString(R.string.Menu_Promotion)
            layoutToolbar.imageViewAction.setImageDrawable(ContextCompat.getDrawable(this@SalesPromotionActivity, R.drawable.ic_home))
            layoutToolbar.imageViewBack.visibility = View.VISIBLE
        }
        salesPromotionAdapter = SalesPromotionAdapter { it, pos, type->
            when(type) {
                Enums.ClickEvents.DELETE -> {
                    hideKeyBoard(this)
                    val ft = supportFragmentManager.beginTransaction()
                    val dialogFragment = DeleteAlertPopupDialog(resources.getString(R.string.DeletePermission, resources.getString(R.string.Menu_Promotion)), it.id!!, isPromotion = true)
                    val prevFragment : Fragment? = supportFragmentManager.findFragmentByTag(DeleteAlertPopupDialog::class.java.name)
                    if(prevFragment != null) return@SalesPromotionAdapter
                    dialogFragment.isCancelable = false
                    dialogFragment.show(ft, DeleteAlertPopupDialog::class.java.name)
                    dialogFragment.setListener(object : DeleteAlertPopupDialog.OnButtonClickListener {
                        override fun onButtonClickListener(typeButton : Enums.ClickEvents) {
                            when(typeButton) {
                                Enums.ClickEvents.DELETE -> {
                                    salesPromotionAdapter.apply {
                                        list.remove(it)
                                        filteredList.remove(it)
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
                    val intent = Intent(this@SalesPromotionActivity, PromotionAddActivity::class.java)
                    intent.putExtra(Default.PROMOTION, if(it.id!! > 0) it.id!! else 0)
                    openActivity(intent)
                }

                else -> {}
            }
        }
    }

    private fun postInitView() {
        binding.apply {
            recycleViewProducts.apply {
                adapter = salesPromotionAdapter
                layoutManager = LinearLayoutManager(this@SalesPromotionActivity)
            }
            layoutHeader.data = Header(id =resources.getString(R.string.lbl_Id), title = resources.getString(R.string.hint_Name), title2 = resources.getString(R.string.lbl_StartDate), title3 = resources.getString(R.string.lbl_EndDate), title4 = resources.getString(R.string.lbl_Action))
        }
    } //endregion

    //region To manage click events
    private fun manageClicks() {
        binding.apply {
            layoutToolbar.imageViewAction.clickWithDebounce {
                openActivity(Intent(this@SalesPromotionActivity, DashboardActivity::class.java))
            }

            layoutToolbar.imageViewBack.clickWithDebounce {
                openActivity(Intent(this@SalesPromotionActivity, DashboardActivity::class.java))
            }

            fabAdd.clickWithDebounce {
                val intent = Intent(this@SalesPromotionActivity, PromotionAddActivity::class.java)
                intent.putExtra(Default.PROMOTION, 0)
                openActivity(intent)
            }

            searchProduct.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query : String) : Boolean {
                    return false
                }

                override fun onQueryTextChange(newText : String) : Boolean {
                    salesPromotionAdapter.getFilter().filter(newText)
                    return true
                }
            })
            swipeRefresh.apply {
                setOnRefreshListener {
                    swipeRefresh.isRefreshing = false
                    getPromotion()
                    clearSearch()
                }
                setColorSchemeColors(ContextCompat.getColor(this@SalesPromotionActivity, R.color.pink))
            }
        }
    } //endregion

    @Deprecated("Deprecated in Java") override fun onBackPressed() {
        openActivity(Intent(this@SalesPromotionActivity, DashboardActivity::class.java))
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
        getPromotion()
    }

    private fun getPromotion() {
        viewModel.fetchSalesPromotion().observe(this) {
            CoroutineScope(Dispatchers.Main).launch {
                salesPromotionAdapter.apply {
                    list.clear()
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