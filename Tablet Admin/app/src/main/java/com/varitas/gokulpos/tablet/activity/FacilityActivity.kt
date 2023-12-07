package com.varitas.gokulpos.tablet.activity

import android.os.Bundle
import android.view.View
import android.widget.SearchView
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.varitas.gokulpos.tablet.R
import com.varitas.gokulpos.tablet.adapter.FacilityAdapter
import com.varitas.gokulpos.tablet.databinding.ActivityCommonBinding
import com.varitas.gokulpos.tablet.fragmentDialogs.DeleteAlertPopupDialog
import com.varitas.gokulpos.tablet.fragmentDialogs.FacilityPopupDialog
import com.varitas.gokulpos.tablet.model.Header
import com.varitas.gokulpos.tablet.response.Facility
import com.varitas.gokulpos.tablet.utilities.Default
import com.varitas.gokulpos.tablet.utilities.Enums
import com.varitas.gokulpos.tablet.viewmodel.ProductFeatureViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FacilityActivity : BaseActivity() {

    lateinit var binding : ActivityCommonBinding
    val viewModel : ProductFeatureViewModel by viewModels()
    private lateinit var facilityAdapter: FacilityAdapter
    private var parentId = 0
    private var isFromOrder = false

    companion object {
        lateinit var Instance : FacilityActivity
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
            layoutToolbar.textViewToolbarName.text = resources.getString(R.string.Menu_Facility)
            layoutToolbar.imageViewAction.setImageDrawable(ContextCompat.getDrawable(this@FacilityActivity, R.drawable.ic_home))
            layoutToolbar.imageViewBack.visibility = View.VISIBLE
        }

        facilityAdapter = FacilityAdapter { facility, i, isFromDelete->
            if(isFromDelete) {
                hideKeyBoard(this)
                val ft = supportFragmentManager.beginTransaction()
                val dialogFragment = DeleteAlertPopupDialog(resources.getString(R.string.DeletePermission, resources.getString(R.string.Menu_Facility)),facility.id!!, isFacility = true)
                val prevFragment: Fragment? = supportFragmentManager.findFragmentByTag(DeleteAlertPopupDialog::class.java.name)
                if (prevFragment != null) return@FacilityAdapter
                dialogFragment.isCancelable = false
                dialogFragment.show(ft, DeleteAlertPopupDialog::class.java.name)
                dialogFragment.setListener(object : DeleteAlertPopupDialog.OnButtonClickListener {
                    override fun onButtonClickListener(typeButton: Enums.ClickEvents) {
                        when (typeButton) {
                            Enums.ClickEvents.DELETE -> {
                                facilityAdapter.apply {
                                    list.remove(facility)
                                    filteredList.remove(facility)
                                    notifyItemRemoved(i)
                                    notifyItemRangeChanged(i, list.size)
                                }
                            }
                            else -> {}
                        }
                    }
                })
            } else manageFacility(facilityID = facility.id!!)
        }
    }

    private fun postInitView() {
        binding.apply {
            recycleViewProducts.apply {
                adapter = facilityAdapter
                layoutManager = LinearLayoutManager(this@FacilityActivity)
            }
            layoutHeader.data = Header(resources.getString(R.string.lbl_Id), resources.getString(R.string.Menu_Facility), resources.getString(R.string.hint_ContactName), resources.getString(R.string.hint_ContactNumber),resources.getString(R.string.lbl_Action))
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
                    facilityAdapter.getFilter().filter(newText)
                    return true
                }
            })

            fabAdd.setOnClickListener {
                manageFacility()
            }

            swipeRefresh.apply {
                setOnRefreshListener {
                    swipeRefresh.isRefreshing = false
                    getFacility()
                    clearSearch()
                }
                setColorSchemeColors(ContextCompat.getColor(this@FacilityActivity, R.color.pink))
            }
        }
    }//endregion

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
        getFacility()
    }

    private fun getFacility() {
        viewModel.fetchFacility().observe(this) {
            CoroutineScope(Dispatchers.Main).launch {
                facilityAdapter.apply {
                    setList(it)
                }
            }
        }
    }
    //endregion

    //region To manage specification
    private fun manageFacility(facilityID : Int = 0) {
        if(facilityID > 0) {
            viewModel.getFacilityDetails(facilityID).observe(this) {
                val bundle = Bundle()
                bundle.putParcelable(Default.FACILITY, it!!)
                openFacilityPopup(bundle)
            }
        } else openFacilityPopup()
    }

    private fun openFacilityPopup(bundle : Bundle? = null) {
        val ft = supportFragmentManager.beginTransaction()
        val dialogFragment = FacilityPopupDialog.newInstance()
        val prevFragment : Fragment? = supportFragmentManager.findFragmentByTag(FacilityPopupDialog::class.java.name)
        if(prevFragment != null) return
        if(bundle != null) dialogFragment.arguments = bundle
        dialogFragment.isCancelable = false
        dialogFragment.show(ft, FacilityPopupDialog::class.java.name)
        dialogFragment.setListener(object : FacilityPopupDialog.OnButtonClickListener {
            override fun onButtonClickListener(typeButton : Enums.ClickEvents, facility : Facility?, isUpdate : Boolean) {
                clearSearch()
                when(typeButton) {
                    Enums.ClickEvents.CANCEL -> {
                        if(dialogFragment.isVisible) dialogFragment.dismiss()
                    }

                    Enums.ClickEvents.SAVE -> {
                        if(dialogFragment.isVisible) dialogFragment.dismiss()
                        getFacility()
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