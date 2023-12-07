package com.varitas.gokulpos.tablet.activity

import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.varitas.gokulpos.tablet.R
import com.varitas.gokulpos.tablet.adapter.DrawerAdapter
import com.varitas.gokulpos.tablet.databinding.ActivityDrawerBinding
import com.varitas.gokulpos.tablet.model.Header
import com.varitas.gokulpos.tablet.request.DrawerInsertRequest
import com.varitas.gokulpos.tablet.utilities.Default
import com.varitas.gokulpos.tablet.viewmodel.OrdersViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint class DrawerActivity : BaseActivity() {

    private lateinit var binding: ActivityDrawerBinding
    val viewModel: OrdersViewModel by viewModels()
    private lateinit var drawerAdapter: DrawerAdapter
    private var typeId = 0
    private var parentId = 0

    companion object {
        lateinit var Instance: DrawerActivity
    }

    init {
        Instance = this
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDrawerBinding.inflate(layoutInflater)
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
        typeId = Default.ADJUSTMENT
        binding.apply {
            layoutToolbar.textViewToolbarName.text = resources.getString(R.string.Menu_PaidOut_Drop)
            layoutToolbar.imageViewAction.setImageDrawable(ContextCompat.getDrawable(this@DrawerActivity, R.drawable.ic_home))
            layoutToolbar.imageViewBack.visibility = View.VISIBLE
            radioAdjustment.isChecked = true
        }
        drawerAdapter = DrawerAdapter()
    }

    private fun postInitView() {
        binding.apply {
            recycleViewDrawer.apply {
                adapter = drawerAdapter
                layoutManager = LinearLayoutManager(this@DrawerActivity)
            }
            layoutHeader.data = Header(resources.getString(R.string.lbl_Id), resources.getString(R.string.lbl_Date), resources.getString(R.string.lbl_Amount), resources.getString(R.string.lbl_Reason), resources.getString(R.string.lbl_Type))
        }
    } //endregion

    //region To manage click events
    private fun manageClicks() {
        binding.apply {
            layoutToolbar.imageViewAction.clickWithDebounce {
                moveToOrder(parentId)
            }

            layoutToolbar.imageViewBack.clickWithDebounce {
                moveToOrder(parentId)
            }

            groupPaidOut.setOnCheckedChangeListener { _, checkedId ->
                when (checkedId) {
                    R.id.radioAdjustment -> typeId = Default.ADJUSTMENT
                    R.id.radioLoan -> typeId = Default.LOAN
                    R.id.radioDrop -> typeId = Default.DROP
                    R.id.radioPaidOut -> typeId = Default.PAID_OUT
                }
            }

            buttonSave.clickWithDebounce {
                if (!TextUtils.isEmpty(textInputAmount.text.toString().trim())) {
                    val amount = textInputAmount.text.toString().trim().toDouble()
                    val reason = textInputReason.text.toString().trim()
                    val req = DrawerInsertRequest(typeId, amount, reason)
                    viewModel.insertDrawer(req).observe(this@DrawerActivity) {
                        if (it) {
                            CoroutineScope(Dispatchers.Main).launch {
                                textInputAmount.text!!.clear()
                                textInputReason.text!!.clear()
                                getDrawerTransactions()
                            }
                        }
                    }
                }
            }
        }
    } //endregion

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

        getDrawerTransactions()
    }

    private fun getDrawerTransactions() {
        viewModel.fetchDrawerTransaction().observe(this) {
            CoroutineScope(Dispatchers.Main).launch {
                drawerAdapter.apply {
                    setList(it)
                }
            }
        }


    } //endregion
}