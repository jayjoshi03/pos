package com.varitas.gokulpos.tablet.activity

import android.os.Bundle
import android.view.View
import android.widget.SearchView
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.varitas.gokulpos.tablet.R
import com.varitas.gokulpos.tablet.adapter.DepartmentAdapter
import com.varitas.gokulpos.tablet.databinding.ActivityCommonBinding
import com.varitas.gokulpos.tablet.fragmentDialogs.DeleteAlertPopupDialog
import com.varitas.gokulpos.tablet.fragmentDialogs.DepartmentPopupDialog
import com.varitas.gokulpos.tablet.model.Header
import com.varitas.gokulpos.tablet.response.Department
import com.varitas.gokulpos.tablet.utilities.Default
import com.varitas.gokulpos.tablet.utilities.Enums
import com.varitas.gokulpos.tablet.viewmodel.ProductFeatureViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint class DepartmentActivity : BaseActivity() {

    private lateinit var binding : ActivityCommonBinding
    val viewModel : ProductFeatureViewModel by viewModels()

    private lateinit var deptCateAdapter: DepartmentAdapter
    private var parentId = 0
    private var isFromOrder = false

    companion object {
        lateinit var Instance : DepartmentActivity
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
        parentId = if (intent.extras?.getInt(Default.PARENT_ID) != null) intent.extras?.getInt(Default.PARENT_ID)!! else 0
        isFromOrder = if (intent.extras?.getBoolean(Default.ORDER) != null) intent.extras?.getBoolean(Default.ORDER)!! else false
        binding.apply {
            layoutToolbar.textViewToolbarName.text = resources.getString(R.string.lbl_Department)
            layoutToolbar.imageViewAction.setImageDrawable(ContextCompat.getDrawable(this@DepartmentActivity, R.drawable.ic_home))
            layoutToolbar.imageViewBack.visibility = View.VISIBLE
        }
        deptCateAdapter = DepartmentAdapter { department, i, isFromDelete, isItemCount ->
            if (isItemCount) {
                //if(department.itemCount!! >= 0)
                manageItemCount(department.id!!, Enums.Menus.DEPARTMENT)
                clearSearch()
            } else {
                if(isFromDelete) {
                    hideKeyBoard(this)
                    val ft = supportFragmentManager.beginTransaction()
                    val dialogFragment = DeleteAlertPopupDialog(resources.getString(R.string.DeletePermission, resources.getString(R.string.Menu_Department)), department.id!!, isDepartment = true)
                    val prevFragment : Fragment? = supportFragmentManager.findFragmentByTag(DeleteAlertPopupDialog::class.java.name)
                    if(prevFragment != null) return@DepartmentAdapter
                    dialogFragment.isCancelable = false
                    dialogFragment.show(ft, DeleteAlertPopupDialog::class.java.name)
                    dialogFragment.setListener(object : DeleteAlertPopupDialog.OnButtonClickListener {
                        override fun onButtonClickListener(typeButton : Enums.ClickEvents) {
                            when(typeButton) {
                                Enums.ClickEvents.DELETE -> {
                                    deptCateAdapter.apply {
                                        list.remove(department)
                                        filteredList.remove(department)
                                        notifyItemRemoved(i)
                                        notifyItemRangeChanged(i, list.size)
                                    }
                                }

                                else -> {}
                            }
                        }
                    })
                } else manageDepartment(department.id!!)
            }
        }
    }

    private fun postInitView() {
        binding.apply {
            recycleViewProducts.apply {
                adapter = deptCateAdapter
                layoutManager = LinearLayoutManager(this@DepartmentActivity)
            }
            layoutHeader.root.visibility = View.GONE
            layoutHeaderItem.root.visibility = View.VISIBLE
            layoutHeaderItem.data = Header(resources.getString(R.string.lbl_Id), resources.getString(R.string.Menu_Department), resources.getString(R.string.lbl_Taxable), resources.getString(R.string.lbl_FoodStamp), resources.getString(R.string.lbl_Items), resources.getString(R.string.lbl_Action))
        }
    } //endregion

    //region To manage click events
    private fun manageClicks() {
        binding.apply {
            layoutToolbar.imageViewAction.clickWithDebounce {
                moveToOrder(parentId)
            }
            layoutToolbar.imageViewBack.clickWithDebounce {
                moveToSubMenu(parentId,isFromOrder)
            }
            searchProduct.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query : String) : Boolean {
                    return false
                }

                override fun onQueryTextChange(newText : String) : Boolean {
                    deptCateAdapter.getFilter().filter(newText)
                    return true
                }
            })
            fabAdd.setOnClickListener {
                manageDepartment()
            }
        }

        binding.swipeRefresh.apply {
            setOnRefreshListener {
                isRefreshing = false
                getDepartments()
                clearSearch()
            }
            setColorSchemeColors(ContextCompat.getColor(this@DepartmentActivity, R.color.pink))
        }
    } //endregion

    @Deprecated("Deprecated in Java") override fun onBackPressed() {
        moveToSubMenu(parentId,isFromOrder)
        onBackPressedDispatcher.onBackPressed()
    }

    //region To load data
    private fun loadData() {
        viewModel.showProgress.observe(this) {
            manageProgress(it)
        }

        viewModel.errorMsg.observe(this) {
            showSweetDialog(this, it)
        }
        getDepartments()
    }

    private fun getDepartments() {
        viewModel.fetchDepartment().observe(this) {
            CoroutineScope(Dispatchers.Main).launch {
                deptCateAdapter.apply {
                    setList(it)
                }
            }
        }
    } //endregion

    //region To manage department
    private fun manageDepartment(deptID : Int = 0) {
        if(deptID > 0) {
            viewModel.getDepartmentDetails(deptID).observe(this) {
                val bundle = Bundle()
                bundle.putParcelable(Default.DEPARTMENT, it!!)
                openDepartmentPopUp(bundle)
            }
        } else openDepartmentPopUp()
    }

    private fun openDepartmentPopUp(bundle : Bundle? = null) {
        val ft = supportFragmentManager.beginTransaction()
        val dialogFragment = DepartmentPopupDialog.newInstance()
        val prevFragment : Fragment? = supportFragmentManager.findFragmentByTag(DepartmentPopupDialog::class.java.name)
        if(prevFragment != null) return
        if(bundle != null) dialogFragment.arguments = bundle
        dialogFragment.isCancelable = false
        dialogFragment.show(ft, DepartmentPopupDialog::class.java.name)
        dialogFragment.setListener(object : DepartmentPopupDialog.OnButtonClickListener {
            override fun onButtonClickListener(typeButton : Enums.ClickEvents, department : Department?, isUpdate : Boolean) {
                clearSearch()
                when(typeButton) {
                    Enums.ClickEvents.CANCEL -> {
                        if(dialogFragment.isVisible) dialogFragment.dismiss()
                    }

                    Enums.ClickEvents.SAVE -> {
                        if(dialogFragment.isVisible) dialogFragment.dismiss()
                        getDepartments()
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