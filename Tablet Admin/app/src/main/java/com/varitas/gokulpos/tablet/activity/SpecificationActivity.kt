package com.varitas.gokulpos.tablet.activity

import android.os.Bundle
import android.view.View
import android.widget.SearchView
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.varitas.gokulpos.tablet.R
import com.varitas.gokulpos.tablet.adapter.SpecificationAdapter
import com.varitas.gokulpos.tablet.databinding.ActivityCommonBinding
import com.varitas.gokulpos.tablet.fragmentDialogs.DeleteAlertPopupDialog
import com.varitas.gokulpos.tablet.fragmentDialogs.SpecificationPopupDialog
import com.varitas.gokulpos.tablet.model.Header
import com.varitas.gokulpos.tablet.response.Specification
import com.varitas.gokulpos.tablet.utilities.Default
import com.varitas.gokulpos.tablet.utilities.Enums
import com.varitas.gokulpos.tablet.viewmodel.ProductFeatureViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SpecificationActivity : BaseActivity() {

    lateinit var binding : ActivityCommonBinding
    val viewModel : ProductFeatureViewModel by viewModels()
    private lateinit var specificationAdapter: SpecificationAdapter
    private var parentId = 0
    private var isFromOrder = false

    companion object {
        lateinit var Instance : SpecificationActivity
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
            layoutToolbar.textViewToolbarName.text = resources.getString(R.string.Menu_Specification)
            layoutToolbar.imageViewAction.setImageDrawable(ContextCompat.getDrawable(this@SpecificationActivity, R.drawable.ic_home))
            layoutToolbar.imageViewBack.visibility = View.VISIBLE
        }
        specificationAdapter = SpecificationAdapter { specification, i, isFromDelete->
            if(isFromDelete) {
                hideKeyBoard(this)
                val ft = supportFragmentManager.beginTransaction()
                val dialogFragment = DeleteAlertPopupDialog(resources.getString(R.string.DeletePermission, resources.getString(R.string.Menu_Specification)),specification.id!!, isSpecification= true)
                val prevFragment: Fragment? = supportFragmentManager.findFragmentByTag(DeleteAlertPopupDialog::class.java.name)
                if (prevFragment != null) return@SpecificationAdapter
                dialogFragment.isCancelable = false
                dialogFragment.show(ft, DeleteAlertPopupDialog::class.java.name)
                dialogFragment.setListener(object : DeleteAlertPopupDialog.OnButtonClickListener {
                    override fun onButtonClickListener(typeButton: Enums.ClickEvents) {
                        when (typeButton) {
                            Enums.ClickEvents.DELETE -> {
                                specificationAdapter.apply {
                                    list.remove(specification)
                                    filteredList.remove(specification)
                                    notifyItemRemoved(i)
                                    notifyItemRangeChanged(i, list.size)
                                }
                            }
                            else -> {}
                        }
                    }
                })
            } else manageSpecification(specificId = specification.id!!, pos = i)
        }
    }

    private fun postInitView() {
        binding.apply {
            recycleViewProducts.apply {
                adapter = specificationAdapter
                layoutManager = LinearLayoutManager(this@SpecificationActivity)
            }
            layoutHeader.data = Header(resources.getString(R.string.lbl_Id), resources.getString(R.string.Menu_Specification), resources.getString(R.string.hint_Value), resources.getString(R.string.Menu_UOM),resources.getString(R.string.lbl_Action))
        }
    } //endregion

    //region To manage click event
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
                    specificationAdapter.getFilter().filter(newText)
                    return true
                }
            })

            fabAdd.setOnClickListener {
                manageSpecification()
            }

            swipeRefresh.apply {
                setOnRefreshListener {
                    swipeRefresh.isRefreshing = false
                    getSpecification()
                    clearSearch()
                }
                setColorSchemeColors(ContextCompat.getColor(this@SpecificationActivity, R.color.pink))
            }
        }
    }
    //endregion

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
        getSpecification()
    }

    private fun getSpecification() {
        viewModel.fetchSpecification().observe(this) {
            CoroutineScope(Dispatchers.Main).launch {
                specificationAdapter.apply {
                    setList(it)
                }
            }
        }
    }
    //endregion

    //region To manage specification
    private fun manageSpecification(specificId : Int = 0, pos : Int = 0) {
        if(specificId > 0) {
            viewModel.getSpecificationDetails(specificId).observe(this) {
                val bundle = Bundle()
                bundle.putParcelable(Default.SPECIFICATION, it!!)
                openSpecificationPopup(bundle, pos)
            }
        } else openSpecificationPopup(pos = pos)
    }

    private fun openSpecificationPopup(bundle : Bundle? = null, pos : Int = 0) {
        val ft = supportFragmentManager.beginTransaction()
        val dialogFragment = SpecificationPopupDialog.newInstance()
        val prevFragment : Fragment? = supportFragmentManager.findFragmentByTag(SpecificationPopupDialog::class.java.name)
        if(prevFragment != null) return
        if(bundle != null) dialogFragment.arguments = bundle
        dialogFragment.isCancelable = false
        dialogFragment.show(ft, SpecificationPopupDialog::class.java.name)
        dialogFragment.setListener(object : SpecificationPopupDialog.OnButtonClickListener {
            override fun onButtonClickListener(typeButton : Enums.ClickEvents, specification : Specification?, isUpdate : Boolean) {
                clearSearch()
                when(typeButton) {
                    Enums.ClickEvents.CANCEL -> {
                        if(dialogFragment.isVisible) dialogFragment.dismiss()
                    }

                    Enums.ClickEvents.SAVE -> {
                        if(dialogFragment.isVisible) dialogFragment.dismiss()
                        if(!isUpdate) getSpecification()
                        else {
                            if(specification != null) {
                                specificationAdapter.apply {
                                    list[pos].type = specification.type
                                    list[pos].name = specification.name
                                    list[pos].uom = specification.uom
                                    notifyItemChanged(pos)
                                }
                            }
                        }

                    }

                    Enums.ClickEvents.ERROR -> {
                        if(dialogFragment.isVisible) dialogFragment.dismiss()
                        showSweetDialog(this@SpecificationActivity, resources.getString(R.string.lbl_SomethingWrong))
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