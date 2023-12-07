package com.varitas.gokulpos.tablet.activity

import android.os.Bundle
import android.view.View
import android.widget.SearchView
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.varitas.gokulpos.tablet.R
import com.varitas.gokulpos.tablet.adapter.ReceiptAdapter
import com.varitas.gokulpos.tablet.databinding.ActivityReceiptBinding
import com.varitas.gokulpos.tablet.model.Header
import com.varitas.gokulpos.tablet.utilities.Default
import com.varitas.gokulpos.tablet.viewmodel.OrdersViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ReceiptActivity : BaseActivity() {

    lateinit var binding: ActivityReceiptBinding
    val viewModel: OrdersViewModel by viewModels()
    private var parentId = 0
    private var isFromOrder = false
    private lateinit var receiptAdapter: ReceiptAdapter

    companion object {
        lateinit var Instance: ReceiptActivity
    }

    init {
        Instance = this
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReceiptBinding.inflate(layoutInflater)
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
            layoutToolbar.textViewToolbarName.text = resources.getString(R.string.button_Receipt)
            layoutToolbar.imageViewAction.setImageDrawable(ContextCompat.getDrawable(this@ReceiptActivity, R.drawable.ic_home))
            layoutToolbar.imageViewBack.visibility = View.VISIBLE
        }

    }

    private fun postInitView() {
        receiptAdapter = ReceiptAdapter {
            manageReceipt(it.id!!, isFromHold = it.status == Default.HOLD_ORDER, isFromVoid = it.status == Default.VOID_ORDER, isFromOrder = false)
        }


        binding.apply {
            recycleViewProducts.apply {
                adapter = receiptAdapter
                layoutManager = LinearLayoutManager(this@ReceiptActivity)
            }
            header = Header(id = resources.getString(R.string.lbl_Id), title = resources.getString(R.string.lbl_OrderNumber), title2 = resources.getString(R.string.hint_Tax), title3 = resources.getString(R.string.lbl_Discount), title4 = resources.getString(R.string.lbl_Subtotal), title5 = resources.getString(R.string.lbl_Total), title6 = resources.getString(R.string.lbl_Status))
            fabAdd.visibility = View.GONE
        }
    } //endregion

    //region To manage click event
    private fun manageClicks() {
        binding.apply {
            layoutToolbar.imageViewAction.clickWithDebounce {
                moveToOrder(parentId)
            }

            layoutToolbar.imageViewBack.clickWithDebounce {
                moveToOrder(parentId)
            }

            searchProduct.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String): Boolean {
                    return false
                }

                override fun onQueryTextChange(newText: String): Boolean {
                    receiptAdapter.getFilter().filter(newText)
                    return true
                }
            })

            swipeRefresh.apply {
                setOnRefreshListener {
                    swipeRefresh.isRefreshing = false
                    getFacility()
                    clearSearch()
                }
                setColorSchemeColors(ContextCompat.getColor(this@ReceiptActivity, R.color.pink))
            }
        }
    }//endregion

    @Deprecated("Deprecated in Java") override fun onBackPressed() {
        moveToOrder(parentId)
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

        getFacility()
    }

    private fun getFacility() {
        viewModel.fetchAllOrderList().observe(this) {
            CoroutineScope(Dispatchers.Main).launch {
                receiptAdapter.apply {
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
}