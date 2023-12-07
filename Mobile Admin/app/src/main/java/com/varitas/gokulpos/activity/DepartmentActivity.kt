package com.varitas.gokulpos.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.SearchView
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import cn.pedant.SweetAlert.SweetAlertDialog
import com.varitas.gokulpos.R
import com.varitas.gokulpos.adapter.DepartmentAdapter
import com.varitas.gokulpos.databinding.ActivityCommonBinding
import com.varitas.gokulpos.fragmentDialogs.DepartmentPopupDialog
import com.varitas.gokulpos.model.Header
import com.varitas.gokulpos.response.Department
import com.varitas.gokulpos.utilities.Default
import com.varitas.gokulpos.utilities.Enums
import com.varitas.gokulpos.viewmodel.ProductFeatureViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint class DepartmentActivity : BaseActivity() {

    private lateinit var binding : ActivityCommonBinding
    val viewModel : ProductFeatureViewModel by viewModels()
    private var parentId = 0
    private lateinit var deptCateAdapter : DepartmentAdapter

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
        parentId = if(intent.extras?.getInt(Default.PARENT_ID) != null) intent.extras?.getInt(Default.PARENT_ID)!! else 0

        binding.apply {
            layoutToolbar.textViewToolbarName.text = resources.getString(R.string.lbl_Department)
            layoutToolbar.imageViewAction.setImageDrawable(ContextCompat.getDrawable(this@DepartmentActivity, R.drawable.ic_home))
            layoutToolbar.imageViewBack.visibility = View.VISIBLE
        }

        deptCateAdapter = DepartmentAdapter { department, i, isFromDelete, isItemCount->
            if(isItemCount) {
                //if(department.itemCount!! >= 0)
                manageItemCount(department.id!!, Enums.Menus.DEPARTMENT)
                clearSearch()
            } else {
                if(isFromDelete) {
                    hideKeyBoard(this)
                    val sweetAlertDialog = SweetAlertDialog(this@DepartmentActivity, SweetAlertDialog.CUSTOM_IMAGE_TYPE)
                    sweetAlertDialog.apply {
                        setCanceledOnTouchOutside(false)
                        setCancelable(false)
                        contentText = resources.getString(R.string.lbl_DeletePermission, resources.getString(R.string.Menu_Department))
                        confirmText = resources.getString(R.string.lbl_Yes)
                        cancelText = resources.getString(R.string.lbl_No)
                        confirmButtonBackgroundColor = ContextCompat.getColor(this@DepartmentActivity, R.color.base_color)
                        cancelButtonBackgroundColor = ContextCompat.getColor(this@DepartmentActivity, R.color.pink)
                        setConfirmClickListener { sDialog->
                            sDialog.dismissWithAnimation()
                            viewModel.deleteDepartment(department.id!!).observe(this@DepartmentActivity) {
                                CoroutineScope(Dispatchers.Main).launch {
                                    if(it) {
                                        deptCateAdapter.apply {
                                            list.remove(department)
                                            filteredList.remove(department)
                                            notifyItemRemoved(i)
                                            notifyItemRangeChanged(i, list.size)
                                        }
                                    }
                                }
                            }
                        }
                        setCancelClickListener { sDialog->
                            sDialog.dismissWithAnimation()
                        }
                        show()
                    }
                } else manageDepartment(department.id!!, i)
            }
        }
    }

    private fun postInitView() {
        binding.apply {
            recycleViewProducts.apply {
                adapter = deptCateAdapter
                layoutManager = LinearLayoutManager(this@DepartmentActivity)
            }
            layoutHeader.data = Header(resources.getString(R.string.lbl_Id), resources.getString(R.string.Menu_Department), resources.getString(R.string.lbl_Items))
        }
    } //endregion

    //region To manage click events
    private fun manageClicks() {
        binding.apply {
            layoutToolbar.imageViewAction.clickWithDebounce {
                openActivity(Intent(this@DepartmentActivity, DashboardActivity::class.java))
            }
            layoutToolbar.imageViewBack.clickWithDebounce {
                moveToProducts(parentId)
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
        moveToProducts(parentId)
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
        binding.linearLayoutShimmer.visibility = View.VISIBLE
        viewModel.fetchDepartment().observe(this) {
            CoroutineScope(Dispatchers.Main).launch {
                binding.linearLayoutShimmer.visibility = View.GONE
                deptCateAdapter.apply {
                    setList(it)
                }
            }
        }
    } //endregion

    //region To manage department
    private fun manageDepartment(deptID : Int = 0, pos : Int = 0) {
        if(deptID > 0) {
            viewModel.getDepartmentDetails(deptID).observe(this) {
                val bundle = Bundle()
                bundle.putParcelable(Default.DEPARTMENT, it!!)
                openDepartmentPopUp(bundle, pos)
            }
        } else openDepartmentPopUp()
    }

    private fun openDepartmentPopUp(bundle : Bundle? = null, pos : Int = 0) {
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
                        if(!isUpdate) getDepartments()
                        else {
                            if(department != null) {
                                deptCateAdapter.apply {
                                    list[pos].name = department.name
                                    notifyItemChanged(pos)
                                }
                            }
                        }
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

    override fun onResume() {
        binding.shimmer.startShimmer()
        super.onResume()
    }

    override fun onPause() {
        binding.shimmer.stopShimmer()
        super.onPause()
    }
}