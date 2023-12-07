package com.varitas.gokulpos.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.varitas.gokulpos.R
import com.varitas.gokulpos.adapter.NotificationAdapter
import com.varitas.gokulpos.databinding.ActivityNotificationBinding
import com.varitas.gokulpos.request.NotificationRequest
import com.varitas.gokulpos.viewmodel.ProductViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@AndroidEntryPoint class NotificationActivity : BaseActivity() {

    private lateinit var binding : ActivityNotificationBinding
    private val viewModel : ProductViewModel by viewModels()
    private lateinit var notificationAdapter : NotificationAdapter

    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityNotificationBinding.inflate(layoutInflater)
        val view=binding.root
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
            layoutToolbar.textViewToolbarName.text=resources.getString(R.string.Menu_Notification)
            layoutToolbar.imageViewAction.setImageDrawable(ContextCompat.getDrawable(this@NotificationActivity, R.drawable.ic_home))
            layoutToolbar.imageViewBack.visibility=View.VISIBLE
        }

        notificationAdapter=NotificationAdapter { notification, pos ->
            val req=NotificationRequest(id=notification.id, isActive=notification.isActive)
            viewModel.updateNotifications(req).observe(this) {
                if (!it){
                    showSweetDialog(this, resources.getString(R.string.lbl_SomethingWrong))
                    notificationAdapter.list[pos].isActive = !notification.isActive
                    notificationAdapter.notifyItemChanged(pos)
                }
            }
        }
    }

    private fun postInitView() { // Setting we View Client
        binding.apply {
            recycleViewNotification.apply {
                adapter=notificationAdapter
                layoutManager=LinearLayoutManager(this@NotificationActivity)
            }
        }

    } //endregion

    //region To manage click events
    private fun manageClicks() {
        binding.apply {
            layoutToolbar.imageViewAction.clickWithDebounce {
                openActivity(Intent(this@NotificationActivity, DashboardActivity::class.java))
            }
            layoutToolbar.imageViewBack.clickWithDebounce {
                openActivity(Intent(this@NotificationActivity, DashboardActivity::class.java))
            }
        }
    } //endregion

    @Deprecated("Deprecated in Java") override fun onBackPressed() {
        openActivity(Intent(this, DashboardActivity::class.java))
        onBackPressedDispatcher.onBackPressed()
    }

    //region To load data
    private fun loadData() {
        viewModel.getNotifications().observe(this) {
            CoroutineScope(Dispatchers.Main).launch {
                notificationAdapter.apply {
                    setList(it)
                }
            }
        }
        viewModel.showProgress.observe(this) {
            manageProgress(it)
        }
    } //endregion
}