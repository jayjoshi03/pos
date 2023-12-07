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
import com.varitas.gokulpos.tablet.adapter.RoleAdapter
import com.varitas.gokulpos.tablet.databinding.ActivityCommonBinding
import com.varitas.gokulpos.tablet.fragmentDialogs.RolePopupDialog
import com.varitas.gokulpos.tablet.model.Header
import com.varitas.gokulpos.tablet.response.Role
import com.varitas.gokulpos.tablet.utilities.Default
import com.varitas.gokulpos.tablet.utilities.Enums
import com.varitas.gokulpos.tablet.viewmodel.OrdersViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint class RoleActivity : BaseActivity() {

    private lateinit var binding : ActivityCommonBinding
    val viewModel : OrdersViewModel by viewModels()
    private lateinit var roleAdapter : RoleAdapter

    companion object {
        lateinit var Instance : RoleActivity
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
            layoutToolbar.textViewToolbarName.text = resources.getString(R.string.lbl_Role)
            layoutToolbar.imageViewAction.setImageDrawable(ContextCompat.getDrawable(this@RoleActivity, R.drawable.ic_home))
            layoutToolbar.imageViewBack.visibility = View.VISIBLE
        }
        roleAdapter = RoleAdapter { role, i, enum->
            when(enum) {
                Enums.ClickEvents.EDIT -> manageRole(role.id!!, i)
                else -> {}
            }
        }
    }

    private fun postInitView() {
        binding.apply {
            recycleViewProducts.apply {
                adapter = roleAdapter
                layoutManager = LinearLayoutManager(this@RoleActivity)
            }
            layoutHeader.data = Header(resources.getString(R.string.lbl_Id), resources.getString(R.string.hint_Name), title5 = resources.getString(R.string.lbl_Action))
        }
    } //endregion

    //region To manage click events
    private fun manageClicks() {
        binding.apply {
            layoutToolbar.imageViewAction.clickWithDebounce {
                openActivity(Intent(this@RoleActivity, DashboardActivity::class.java))
            }

            layoutToolbar.imageViewBack.clickWithDebounce {
                openActivity(Intent(this@RoleActivity, DashboardActivity::class.java))
            }
            fabAdd.clickWithDebounce {
                manageRole()
            }
            searchProduct.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query : String) : Boolean {
                    return false
                }

                override fun onQueryTextChange(newText : String) : Boolean {
                    roleAdapter.getFilter().filter(newText)
                    return true
                }
            })
            swipeRefresh.apply {
                setOnRefreshListener {
                    swipeRefresh.isRefreshing = false
                    getRole()
                    clearSearch()
                }
                setColorSchemeColors(ContextCompat.getColor(this@RoleActivity, R.color.pink))
            }
        }
    } //endregion

    @Deprecated("Deprecated in Java") override fun onBackPressed() {
        openActivity(Intent(this@RoleActivity, DashboardActivity::class.java))
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

        getRole()
    }

    private fun getRole() {
        viewModel.fetchRoles().observe(this) {
            CoroutineScope(Dispatchers.Main).launch {
                roleAdapter.apply {
                    setList(it)
                }
            }
        }
    } //endregion

    //region To manage UOM
    private fun manageRole(id : Int = 0, pos : Int = 0) {
        if(id > 0) {
            viewModel.getRoleDetails(id).observe(this) {
                val bundle = Bundle()
                bundle.putParcelable(Default.ROLE, it!!)
                openRolePopup(bundle, pos)
            }
        } else openRolePopup()
    }

    private fun openRolePopup(bundle : Bundle? = null, pos : Int = 0) {
        val ft = supportFragmentManager.beginTransaction()
        val dialogFragment = RolePopupDialog.newInstance()
        val prevFragment : Fragment? = supportFragmentManager.findFragmentByTag(RolePopupDialog::class.java.name)
        if(prevFragment != null) return
        if(bundle != null) dialogFragment.arguments = bundle
        dialogFragment.isCancelable = false
        dialogFragment.show(ft, RolePopupDialog::class.java.name)
        dialogFragment.setListener(object : RolePopupDialog.OnButtonClickListener {
            override fun onButtonClickListener(typeButton : Enums.ClickEvents, role : Role?, isUpdate : Boolean) {
                clearSearch()
                when(typeButton) {
                    Enums.ClickEvents.CANCEL -> {
                        if(dialogFragment.isVisible) dialogFragment.dismiss()
                    }

                    Enums.ClickEvents.SAVE -> {
                        if(dialogFragment.isVisible) dialogFragment.dismiss()
                        if(!isUpdate) getRole()
                        else {
                            if(role != null) {
                                roleAdapter.apply {
                                    list[pos].name = role.name
                                    notifyItemChanged(pos)
                                }
                            }
                        }
                    }

                    Enums.ClickEvents.ERROR -> {
                        if(dialogFragment.isVisible) dialogFragment.dismiss()
                        showSweetDialog(this@RoleActivity, resources.getString(R.string.lbl_SomethingWrong))
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