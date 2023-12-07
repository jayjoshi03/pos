package com.varitas.gokulpos.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.SearchView
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.varitas.gokulpos.R
import com.varitas.gokulpos.adapter.DeviceAdapter
import com.varitas.gokulpos.databinding.ActivityDevicelistBinding
import com.varitas.gokulpos.viewmodel.DeviceViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint class DeviceListActivity : BaseActivity() {

    private lateinit var binding: ActivityDevicelistBinding
    private val viewModel: DeviceViewModel by viewModels()
    private lateinit var deviceAdapter: DeviceAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDevicelistBinding.inflate(layoutInflater)
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
            layoutToolbar.textViewToolbarName.text = resources.getString(R.string.lbl_Devices)
            layoutToolbar.imageViewAction.setImageDrawable(ContextCompat.getDrawable(this@DeviceListActivity, R.drawable.ic_home))
            layoutToolbar.imageViewBack.visibility = View.VISIBLE
        }

        deviceAdapter = DeviceAdapter { _, _ ->

        }
    }

    private fun postInitView() {
        binding.apply {
            recycleViewDevices.apply {
                adapter = deviceAdapter
                layoutManager = LinearLayoutManager(this@DeviceListActivity)
            }
        }
    } //endregion

    //region To manage click events
    private fun manageClicks() {
        binding.apply {
            layoutToolbar.imageViewAction.clickWithDebounce {
                openActivity(Intent(this@DeviceListActivity, DashboardActivity::class.java))
            }
            layoutToolbar.imageViewBack.clickWithDebounce {
                openActivity(Intent(this@DeviceListActivity, DashboardActivity::class.java))
            }
            searchDevices.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String): Boolean {
                    return false
                }

                override fun onQueryTextChange(newText: String): Boolean {
                    deviceAdapter.getFilter().filter(newText)
                    return true
                }
            })
            
            swipeRefresh.apply {
                setOnRefreshListener {
                    swipeRefresh.isRefreshing = false
                    getDevices()
                }
                setColorSchemeColors(ContextCompat.getColor(this@DeviceListActivity, R.color.pink))
            }
        }
    } //endregion

    //region To load data
    private fun loadData() {
        viewModel.showProgress.observe(this) {
            manageProgress(it)
        }

        getDevices()
    }

    private fun getDevices() {
        viewModel.fetchDevices().observe(this) {
            CoroutineScope(Dispatchers.Main).launch {
                deviceAdapter.apply {
                    lists.clear()
                    filteredList.clear()
                    setList(it)
                }
            }
        }
    }

    //endregion

    @Deprecated("Deprecated in Java") override fun onBackPressed() {
        openActivity(Intent(this, DashboardActivity::class.java))
        onBackPressedDispatcher.onBackPressed()
    }
}