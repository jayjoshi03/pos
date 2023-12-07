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
import com.varitas.gokulpos.adapter.FacilityAdapter
import com.varitas.gokulpos.databinding.ActivityCommonBinding
import com.varitas.gokulpos.fragmentDialogs.FacilityPopupDialog
import com.varitas.gokulpos.model.Header
import com.varitas.gokulpos.response.Facility
import com.varitas.gokulpos.utilities.Default
import com.varitas.gokulpos.utilities.Enums
import com.varitas.gokulpos.viewmodel.ProductFeatureViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FacilityActivity : BaseActivity() {

    lateinit var binding : ActivityCommonBinding
    val viewModel : ProductFeatureViewModel by viewModels()
    private lateinit var facilityAdapter : FacilityAdapter
    private var parentId = 0

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
        parentId = if(intent.extras?.getInt(Default.PARENT_ID) != null) intent.extras?.getInt(Default.PARENT_ID)!! else 0
        binding.apply {
            layoutToolbar.textViewToolbarName.text = resources.getString(R.string.Menu_Facility)
            layoutToolbar.imageViewAction.setImageDrawable(ContextCompat.getDrawable(this@FacilityActivity, R.drawable.ic_home))
            layoutToolbar.imageViewBack.visibility = View.VISIBLE
        }

        facilityAdapter = FacilityAdapter { facility, i, isFromDelete->
            if(isFromDelete) {
                hideKeyBoard(this)
                val sweetAlertDialog = SweetAlertDialog(this@FacilityActivity, SweetAlertDialog.CUSTOM_IMAGE_TYPE)
                sweetAlertDialog.apply {
                    setCanceledOnTouchOutside(false)
                    setCancelable(false)
                    contentText = resources.getString(R.string.lbl_DeletePermission, resources.getString(R.string.Menu_Facility))
                    confirmText = resources.getString(R.string.lbl_Yes)
                    cancelText = resources.getString(R.string.lbl_No)
                    confirmButtonBackgroundColor = ContextCompat.getColor(this@FacilityActivity, R.color.base_color)
                    cancelButtonBackgroundColor = ContextCompat.getColor(this@FacilityActivity, R.color.pink)
                    setConfirmClickListener { sDialog->
                        sDialog.dismissWithAnimation()
                        viewModel.deleteFacility(facility.id!!).observe(this@FacilityActivity) {
                            CoroutineScope(Dispatchers.Main).launch {
                                if(it) {
                                    facilityAdapter.apply {
                                        list.remove(facility)
                                        filteredList.remove(facility)
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
            } else manageFacility(facilityID = facility.id!!, pos = i)
        }

    }

    private fun postInitView() {
        binding.apply {
            recycleViewProducts.apply {
                adapter = facilityAdapter
                layoutManager = LinearLayoutManager(this@FacilityActivity)
            }
            layoutHeader.data = Header(resources.getString(R.string.lbl_Id), resources.getString(R.string.Menu_Facility))
        }
    } //endregion

    //region To manage click event
    private fun manageClicks() {
        binding.apply {
            layoutToolbar.imageViewAction.clickWithDebounce {
                openActivity(Intent(this@FacilityActivity, DashboardActivity::class.java))
            }

            layoutToolbar.imageViewBack.clickWithDebounce {
                moveToProducts(parentId)
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
        getFacility()
    }

    private fun getFacility() {
        binding.linearLayoutShimmer.visibility = View.VISIBLE
        viewModel.fetchFacility().observe(this) {
            CoroutineScope(Dispatchers.Main).launch {
                binding.linearLayoutShimmer.visibility = View.GONE
                facilityAdapter.apply {
                    setList(it)
                }
            }
        }
    }
    //endregion

    //region To manage specification
    private fun manageFacility(facilityID : Int = 0, pos : Int = 0) {
        if(facilityID > 0) {
            viewModel.getFacilityDetails(facilityID).observe(this) {
                val bundle = Bundle()
                bundle.putParcelable(Default.FACILITY, it!!)
                openFacilityPopup(bundle, pos)
            }
        } else openFacilityPopup(pos = pos)
    }

    private fun openFacilityPopup(bundle : Bundle? = null, pos : Int = 0) {
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
                        if(!isUpdate) getFacility()
                        else {
                            if(facility != null) {
                                facilityAdapter.apply {
                                    list[pos].name = facility.name
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