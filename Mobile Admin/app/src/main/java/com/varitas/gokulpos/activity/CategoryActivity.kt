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
import com.varitas.gokulpos.adapter.CategoryAdapter
import com.varitas.gokulpos.databinding.ActivityCommonBinding
import com.varitas.gokulpos.fragmentDialogs.CategoryPopupDialog
import com.varitas.gokulpos.model.Header
import com.varitas.gokulpos.response.CategoryDetails
import com.varitas.gokulpos.utilities.Default
import com.varitas.gokulpos.utilities.Enums
import com.varitas.gokulpos.viewmodel.ProductFeatureViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint class CategoryActivity : BaseActivity() {

    private lateinit var binding : ActivityCommonBinding
    val viewModel : ProductFeatureViewModel by viewModels()
    private lateinit var categoryAdapter : CategoryAdapter
    private var parentId = 0

    companion object {
        lateinit var Instance : CategoryActivity
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
            layoutToolbar.textViewToolbarName.text = resources.getString(R.string.lbl_Category)
            layoutToolbar.imageViewAction.setImageDrawable(ContextCompat.getDrawable(this@CategoryActivity, R.drawable.ic_home))
            layoutToolbar.imageViewBack.visibility = View.VISIBLE
        }
        categoryAdapter = CategoryAdapter { category, i, isFromDelete, isItemCount->
            if(isItemCount) {
                //if(category.itemCount!! >= 0)
                manageItemCount(category.id!!, Enums.Menus.CATEGORY)
                clearSearch()
            } else {
                if(isFromDelete) {
                    hideKeyBoard(this)
                    val sweetAlertDialog = SweetAlertDialog(this@CategoryActivity, SweetAlertDialog.CUSTOM_IMAGE_TYPE)
                    sweetAlertDialog.apply {
                        setCanceledOnTouchOutside(false)
                        setCancelable(false)
                        contentText = resources.getString(R.string.lbl_DeletePermission, resources.getString(R.string.Menu_Category))
                        confirmText = resources.getString(R.string.lbl_Yes)
                        cancelText = resources.getString(R.string.lbl_No)
                        confirmButtonBackgroundColor = ContextCompat.getColor(this@CategoryActivity, R.color.base_color)
                        cancelButtonBackgroundColor = ContextCompat.getColor(this@CategoryActivity, R.color.pink)
                        setConfirmClickListener { sDialog->
                            sDialog.dismissWithAnimation()
                            viewModel.deleteCategory(category.id!!).observe(this@CategoryActivity) {
                                CoroutineScope(Dispatchers.Main).launch {
                                    if(it) {
                                        categoryAdapter.apply {
                                            list.remove(category)
                                            filteredList.remove(category)
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
                } else manageCategory(catId = category.id!!, pos = i)
            }
        }
    }

    private fun postInitView() {
        binding.apply {
            recycleViewProducts.apply {
                adapter = categoryAdapter
                layoutManager = LinearLayoutManager(this@CategoryActivity)
            }
            layoutHeader.data = Header(resources.getString(R.string.lbl_Id), resources.getString(R.string.Menu_Category), resources.getString(R.string.lbl_Items))
        }
    } //endregion

    //region To manage click events
    private fun manageClicks() {
        binding.apply {
            layoutToolbar.imageViewAction.clickWithDebounce {
                openActivity(Intent(this@CategoryActivity, DashboardActivity::class.java))
            }

            layoutToolbar.imageViewBack.clickWithDebounce {
                moveToProducts(parentId)
            }

            searchProduct.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query : String) : Boolean {
                    return false
                }

                override fun onQueryTextChange(newText : String) : Boolean {
                    categoryAdapter.getFilter().filter(newText)
                    return true
                }
            })
            fabAdd.setOnClickListener {
                manageCategory()
            }
            swipeRefresh.apply {
                setOnRefreshListener {
                    swipeRefresh.isRefreshing = false
                    getCategories()
                    clearSearch()
                }
                setColorSchemeColors(ContextCompat.getColor(this@CategoryActivity, R.color.pink))
            }
        }
    } //endregion

    @Deprecated("Deprecated in Java") override fun onBackPressed() {
        moveToProducts(parentId)
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

        getCategories()
    }

    private fun getCategories() {
        binding.linearLayoutShimmer.visibility = View.VISIBLE
        viewModel.fetchCategories().observe(this) {
            CoroutineScope(Dispatchers.Main).launch {
                binding.linearLayoutShimmer.visibility = View.GONE
                categoryAdapter.apply {
                    setList(it)
                }
            }
        }
    } //endregion

    //region To manage category
    private fun manageCategory(catId : Int = 0, pos : Int = 0) {
        if(catId > 0) {
            viewModel.getCategoryDetails(catId).observe(this) {
                val bundle = Bundle()
                bundle.putParcelable(Default.CATEGORY, it!!)
                openCategoryPopup(bundle, pos)
            }
        } else openCategoryPopup()
    }

    private fun openCategoryPopup(bundle : Bundle? = null, pos : Int = 0) {
        val ft = supportFragmentManager.beginTransaction()
        val dialogFragment = CategoryPopupDialog.newInstance()
        val prevFragment : Fragment? = supportFragmentManager.findFragmentByTag(CategoryPopupDialog::class.java.name)
        if(prevFragment != null) return
        if(bundle != null) dialogFragment.arguments = bundle
        dialogFragment.isCancelable = false
        dialogFragment.show(ft, CategoryPopupDialog::class.java.name)
        dialogFragment.setListener(object : CategoryPopupDialog.OnButtonClickListener {
            override fun onButtonClickListener(typeButton : Enums.ClickEvents, category : CategoryDetails?, isUpdate : Boolean) {
                clearSearch()
                when(typeButton) {
                    Enums.ClickEvents.CANCEL -> {
                        if(dialogFragment.isVisible) dialogFragment.dismiss()
                    }

                    Enums.ClickEvents.SAVE -> {
                        if(dialogFragment.isVisible) dialogFragment.dismiss()
                        if(!isUpdate) getCategories()
                        else {
                            if(category != null) {
                                categoryAdapter.apply {
                                    list[pos].name = category.name
                                    notifyItemChanged(pos)
                                }
                            }
                        }
                    }

                    Enums.ClickEvents.ERROR -> {
                        if(dialogFragment.isVisible) dialogFragment.dismiss()
                        showSweetDialog(this@CategoryActivity, resources.getString(R.string.lbl_SomethingWrong))
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