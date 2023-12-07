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
import com.varitas.gokulpos.adapter.UOMAdapter
import com.varitas.gokulpos.databinding.ActivityCommonBinding
import com.varitas.gokulpos.fragmentDialogs.UOMPopupDialog
import com.varitas.gokulpos.model.Header
import com.varitas.gokulpos.response.UOM
import com.varitas.gokulpos.utilities.Default
import com.varitas.gokulpos.utilities.Enums
import com.varitas.gokulpos.viewmodel.ProductFeatureViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UOMActivity : BaseActivity() {
    lateinit var binding : ActivityCommonBinding
    val viewModel : ProductFeatureViewModel by viewModels()
    private lateinit var uomAdapter : UOMAdapter
    private var parentId = 0

    companion object {
        lateinit var Instance : UOMActivity
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
            layoutToolbar.textViewToolbarName.text = resources.getString(R.string.Menu_UOM)
            layoutToolbar.imageViewAction.setImageDrawable(ContextCompat.getDrawable(this@UOMActivity, R.drawable.ic_home))
            layoutToolbar.imageViewBack.visibility = View.VISIBLE
        }

        uomAdapter = UOMAdapter { uom, i, isFromDelete->
            if(isFromDelete) {
                hideKeyBoard(this)
                val sweetAlertDialog = SweetAlertDialog(this@UOMActivity, SweetAlertDialog.CUSTOM_IMAGE_TYPE)
                sweetAlertDialog.apply {
                    setCanceledOnTouchOutside(false)
                    setCancelable(false)
                    contentText = resources.getString(R.string.lbl_DeletePermission, resources.getString(R.string.Menu_UOM))
                    confirmText = resources.getString(R.string.lbl_Yes)
                    cancelText = resources.getString(R.string.lbl_No)
                    confirmButtonBackgroundColor = ContextCompat.getColor(this@UOMActivity, R.color.base_color)
                    cancelButtonBackgroundColor = ContextCompat.getColor(this@UOMActivity, R.color.pink)
                    setConfirmClickListener { sDialog->
                        sDialog.dismissWithAnimation()
                        viewModel.deleteUOM(uom.id!!).observe(this@UOMActivity) {
                            CoroutineScope(Dispatchers.Main).launch {
                                if(it) {
                                    uomAdapter.apply {
                                        list.remove(uom)
                                        filteredList.remove(uom)
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
            } else manageUOM(id = uom.id!!, pos = i)
        }
    }

    private fun postInitView() {
        binding.apply {
            recycleViewProducts.apply {
                adapter = uomAdapter
                layoutManager = LinearLayoutManager(this@UOMActivity)
            }
            layoutHeader.data = Header(resources.getString(R.string.lbl_Id), resources.getString(R.string.Menu_UOM))
        }
    }
    //endregion

    //region To manage click
    private fun manageClicks() {
        binding.apply {
            layoutToolbar.imageViewAction.clickWithDebounce {
                openActivity(Intent(this@UOMActivity, DashboardActivity::class.java))
            }

            layoutToolbar.imageViewBack.clickWithDebounce {
                moveToProducts(parentId)
            }

            searchProduct.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query : String) : Boolean {
                    return false
                }

                override fun onQueryTextChange(newText : String) : Boolean {
                    uomAdapter.getFilter().filter(newText)
                    return true
                }
            })

            fabAdd.setOnClickListener {
                manageUOM()
            }

            swipeRefresh.apply {
                setOnRefreshListener {
                    swipeRefresh.isRefreshing = false
                    getUOM()
                    clearSearch()
                }
                setColorSchemeColors(ContextCompat.getColor(this@UOMActivity, R.color.pink))
            }
        }
    }
    //endregion

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
        getUOM()
    }

    private fun getUOM() {
        binding.linearLayoutShimmer.visibility = View.VISIBLE
        viewModel.fetchUOM().observe(this) {
            CoroutineScope(Dispatchers.Main).launch {
                binding.linearLayoutShimmer.visibility = View.GONE
                uomAdapter.apply {
                    setList(it)
                }
            }
        }
    }
    //endregion

    //region To manage UOM
    private fun manageUOM(id : Int = 0, pos : Int = 0) {
        if(id > 0) {
            viewModel.getUOMDetails(id).observe(this) {
                val bundle = Bundle()
                bundle.putParcelable(Default.UOM, it!!)
                openTaxPopup(bundle, pos)
            }
        } else openTaxPopup()
    }

    private fun openTaxPopup(bundle : Bundle? = null, pos : Int = 0) {
        val ft = supportFragmentManager.beginTransaction()
        val dialogFragment = UOMPopupDialog.newInstance()
        val prevFragment : Fragment? = supportFragmentManager.findFragmentByTag(UOMPopupDialog::class.java.name)
        if(prevFragment != null) return
        if(bundle != null) dialogFragment.arguments = bundle
        dialogFragment.isCancelable = false
        dialogFragment.show(ft, UOMPopupDialog::class.java.name)
        dialogFragment.setListener(object : UOMPopupDialog.OnButtonClickListener {
            override fun onButtonClickListener(typeButton : Enums.ClickEvents, uom : UOM?, isUpdate : Boolean) {
                clearSearch()
                when(typeButton) {
                    Enums.ClickEvents.CANCEL -> {
                        if(dialogFragment.isVisible) dialogFragment.dismiss()
                    }

                    Enums.ClickEvents.SAVE -> {
                        if(dialogFragment.isVisible) dialogFragment.dismiss()
                        if(!isUpdate) getUOM()
                        else {
                            if(uom != null) {
                                uomAdapter.apply {
                                    list[pos].uom = uom.uom
                                    notifyItemChanged(pos)
                                }
                            }
                        }
                    }

                    Enums.ClickEvents.ERROR -> {
                        if(dialogFragment.isVisible) dialogFragment.dismiss()
                        showSweetDialog(this@UOMActivity, resources.getString(R.string.lbl_SomethingWrong))
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