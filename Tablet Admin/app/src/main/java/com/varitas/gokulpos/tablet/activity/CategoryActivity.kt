package com.varitas.gokulpos.tablet.activity

import android.os.Bundle
import android.view.View
import android.widget.SearchView
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.varitas.gokulpos.tablet.R
import com.varitas.gokulpos.tablet.adapter.CategoryAdapter
import com.varitas.gokulpos.tablet.databinding.ActivityCommonBinding
import com.varitas.gokulpos.tablet.fragmentDialogs.CategoryPopupDialog
import com.varitas.gokulpos.tablet.fragmentDialogs.DeleteAlertPopupDialog
import com.varitas.gokulpos.tablet.model.CategoryDetails
import com.varitas.gokulpos.tablet.model.CategoryInsertUpdate
import com.varitas.gokulpos.tablet.model.Header
import com.varitas.gokulpos.tablet.utilities.Default
import com.varitas.gokulpos.tablet.utilities.Enums
import com.varitas.gokulpos.tablet.viewmodel.ProductFeatureViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint class CategoryActivity : BaseActivity() {

    private lateinit var binding : ActivityCommonBinding
    val viewModel : ProductFeatureViewModel by viewModels()
    private lateinit var categoryAdapter : CategoryAdapter
    private var parentId = 0
    private var isFromOrder = false

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
        parentId = if (intent.extras?.getInt(Default.PARENT_ID) != null) intent.extras?.getInt(Default.PARENT_ID)!! else 0
        isFromOrder = if (intent.extras?.getBoolean(Default.ORDER) != null) intent.extras?.getBoolean(Default.ORDER)!! else false
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
                    val ft = supportFragmentManager.beginTransaction()
                    val dialogFragment = DeleteAlertPopupDialog(resources.getString(R.string.DeletePermission, resources.getString(R.string.Menu_Category)), category.id!!, isCategory = true)
                    val prevFragment : Fragment? = supportFragmentManager.findFragmentByTag(DeleteAlertPopupDialog::class.java.name)
                    if(prevFragment != null) return@CategoryAdapter
                    dialogFragment.isCancelable = false
                    dialogFragment.show(ft, DeleteAlertPopupDialog::class.java.name)
                    dialogFragment.setListener(object : DeleteAlertPopupDialog.OnButtonClickListener {
                        override fun onButtonClickListener(typeButton : Enums.ClickEvents) {
                            when(typeButton) {
                                Enums.ClickEvents.DELETE -> {
                                    categoryAdapter.apply {
                                        list.remove(category)
                                        filteredList.remove(category)
                                        notifyItemRemoved(i)
                                        notifyItemRangeChanged(i, list.size)
                                    }
                                }

                                else -> {}
                            }
                        }
                    })
                } else manageCategory(catId = category.id!!)
            }
        }
    }

    private fun postInitView() {
        binding.apply {
            recycleViewProducts.apply {
                adapter = categoryAdapter
                layoutManager = LinearLayoutManager(this@CategoryActivity)
            }
            layoutHeader.root.visibility = View.GONE
            layoutHeaderItem.root.visibility = View.VISIBLE
            layoutHeaderItem.data = Header(resources.getString(R.string.lbl_Id), resources.getString(R.string.Menu_Category), resources.getString(R.string.Menu_ParentCategory), resources.getString(R.string.lbl_Department), resources.getString(R.string.lbl_Items), resources.getString(R.string.lbl_Action))
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
        moveToSubMenu(parentId,isFromOrder)
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
        viewModel.fetchCategories().observe(this) {
            CoroutineScope(Dispatchers.Main).launch {
                categoryAdapter.apply {
                    setList(it)
                }
            }
        }
    } //endregion

    //region To manage category
    private fun manageCategory(catId : Int = 0) {
        if(catId > 0) {
            viewModel.getCategoryDetails(catId).observe(this) {
                val bundle = Bundle()
                bundle.putParcelable(Default.CATEGORY, it!!)
                openCategoryPopup(bundle)
            }
        } else openCategoryPopup()
    }

    private fun openCategoryPopup(bundle : Bundle? = null) {
        val ft = supportFragmentManager.beginTransaction()
        val dialogFragment = CategoryPopupDialog.newInstance()
        val prevFragment : Fragment? = supportFragmentManager.findFragmentByTag(CategoryPopupDialog::class.java.name)
        if(prevFragment != null) return
        if(bundle != null) dialogFragment.arguments = bundle
        dialogFragment.isCancelable = false
        dialogFragment.show(ft, CategoryPopupDialog::class.java.name)
        dialogFragment.setListener(object : CategoryPopupDialog.OnButtonClickListener {
            override fun onButtonClickListener(typeButton: Enums.ClickEvents, category: CategoryInsertUpdate?, isUpdate: Boolean) {
                clearSearch()
                when(typeButton) {
                    Enums.ClickEvents.CANCEL -> {
                        if(dialogFragment.isVisible) dialogFragment.dismiss()
                    }

                    Enums.ClickEvents.SAVE -> {
                        if(dialogFragment.isVisible) dialogFragment.dismiss()
                        getCategories()
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
}