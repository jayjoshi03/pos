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
import com.varitas.gokulpos.tablet.adapter.GroupAdapter
import com.varitas.gokulpos.tablet.databinding.ActivityCommonBinding
import com.varitas.gokulpos.tablet.fragmentDialogs.DeleteAlertPopupDialog
import com.varitas.gokulpos.tablet.model.Header
import com.varitas.gokulpos.tablet.utilities.Default
import com.varitas.gokulpos.tablet.utilities.Enums
import com.varitas.gokulpos.tablet.viewmodel.OrdersViewModel
import com.varitas.gokulpos.tablet.viewmodel.ProductFeatureViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint class GroupActivity : BaseActivity() {

    private lateinit var binding : ActivityCommonBinding
    val viewModel: ProductFeatureViewModel by viewModels()
    val orderViewModel: OrdersViewModel by viewModels()
    private lateinit var groupAdapter: GroupAdapter

    companion object {
        lateinit var Instance : GroupActivity
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
            layoutToolbar.textViewToolbarName.text = resources.getString(R.string.Menu_Group)
            layoutToolbar.imageViewAction.setImageDrawable(ContextCompat.getDrawable(this@GroupActivity, R.drawable.ic_home))
            layoutToolbar.imageViewBack.visibility = View.VISIBLE
        }
        groupAdapter = GroupAdapter { group, i, type ->
            when (type) {
                Enums.ClickEvents.DELETE -> {
                    hideKeyBoard(this)
                    val ft = supportFragmentManager.beginTransaction()
                    val dialogFragment = DeleteAlertPopupDialog(resources.getString(R.string.DeletePermission, resources.getString(R.string.Menu_Group)), group.id!!, isGroup = true)
                    val prevFragment: Fragment? = supportFragmentManager.findFragmentByTag(DeleteAlertPopupDialog::class.java.name)
                    if (prevFragment != null) return@GroupAdapter
                    dialogFragment.isCancelable = false
                    dialogFragment.show(ft, DeleteAlertPopupDialog::class.java.name)
                    dialogFragment.setListener(object : DeleteAlertPopupDialog.OnButtonClickListener {
                        override fun onButtonClickListener(typeButton: Enums.ClickEvents) {
                            when (typeButton) {
                                Enums.ClickEvents.DELETE -> {
                                    groupAdapter.apply {
                                        list.remove(group)
                                        filteredList.remove(group)
                                        notifyItemRemoved(i)
                                        notifyItemRangeChanged(i, list.size)
                                    }
                                }

                                else -> {}
                            }
                        }
                    })
                }
                Enums.ClickEvents.EDIT -> {
                    val intent = Intent(this@GroupActivity, GroupDetailActivity::class.java)
                    intent.putExtra(Default.ID, group.id!!)
                    openActivity(intent)
                }
                Enums.ClickEvents.VIEW -> {
                    manageItemCount(group.id!!, Enums.Menus.GROUP)
                    clearSearch()
                }
                else -> {}
            }
        }
    }

    private fun postInitView() {
        binding.apply {
            recycleViewProducts.apply {
                adapter = groupAdapter
                layoutManager = LinearLayoutManager(this@GroupActivity)
            }
            layoutHeader.data = Header(resources.getString(R.string.lbl_Id), resources.getString(R.string.Menu_Group), title2 = resources.getString(R.string.lbl_Type), title3 = resources.getString(R.string.lbl_Items), title4 = resources.getString(R.string.lbl_Action))
        }
    } //endregion

    //region To manage click events
    private fun manageClicks() {
        binding.apply {
            layoutToolbar.imageViewAction.clickWithDebounce {
                openActivity(Intent(this@GroupActivity, DashboardActivity::class.java))
            }

            layoutToolbar.imageViewBack.clickWithDebounce {
                openActivity(Intent(this@GroupActivity, DashboardActivity::class.java))
            }

            searchProduct.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query : String) : Boolean {
                    return false
                }

                override fun onQueryTextChange(newText : String) : Boolean {
                    groupAdapter.getFilter().filter(newText)
                    return true
                }
            })
            fabAdd.setOnClickListener {
                val intent = Intent(this@GroupActivity, GroupDetailActivity::class.java)
                intent.putExtra(Default.ID, 0)
                openActivity(intent)
            }
            swipeRefresh.apply {
                setOnRefreshListener {
                    swipeRefresh.isRefreshing = false
                    getGroup()
                    clearSearch()
                }
                setColorSchemeColors(ContextCompat.getColor(this@GroupActivity, R.color.pink))
            }
        }
    } //endregion

    @Deprecated("Deprecated in Java") override fun onBackPressed() {
        openActivity(Intent(this@GroupActivity, DashboardActivity::class.java))
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
        getGroup()
    }

    private fun getGroup() {
        viewModel.fetchGroups().observe(this) {
            CoroutineScope(Dispatchers.Main).launch {
                groupAdapter.apply {
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