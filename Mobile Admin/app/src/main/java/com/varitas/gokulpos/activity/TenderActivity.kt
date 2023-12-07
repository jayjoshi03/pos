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
import com.varitas.gokulpos.adapter.TenderAdapter
import com.varitas.gokulpos.databinding.ActivityCommonBinding
import com.varitas.gokulpos.fragmentDialogs.TenderPopupDialog
import com.varitas.gokulpos.model.Header
import com.varitas.gokulpos.response.Tender
import com.varitas.gokulpos.utilities.Default
import com.varitas.gokulpos.utilities.Enums
import com.varitas.gokulpos.viewmodel.OrdersViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint class TenderActivity : BaseActivity() {

    private lateinit var binding : ActivityCommonBinding
    val viewModel : OrdersViewModel by viewModels()
    private lateinit var tenderAdapter : TenderAdapter

    companion object {
        lateinit var Instance : TenderActivity
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
            layoutToolbar.textViewToolbarName.text = resources.getString(R.string.lbl_Tender)
            layoutToolbar.imageViewAction.setImageDrawable(ContextCompat.getDrawable(this@TenderActivity, R.drawable.ic_home))
            layoutToolbar.imageViewBack.visibility = View.VISIBLE
        }
        tenderAdapter = TenderAdapter { tender, i, isFromDelete->
            if(isFromDelete) {
                hideKeyBoard(this)
                val sweetAlertDialog = SweetAlertDialog(this@TenderActivity, SweetAlertDialog.CUSTOM_IMAGE_TYPE)
                sweetAlertDialog.apply {
                    setCanceledOnTouchOutside(false)
                    setCancelable(false)
                    contentText = resources.getString(R.string.lbl_DeletePermission, resources.getString(R.string.lbl_Tender))
                    confirmText = resources.getString(R.string.lbl_Yes)
                    cancelText = resources.getString(R.string.lbl_No)
                    confirmButtonBackgroundColor = ContextCompat.getColor(this@TenderActivity, R.color.base_color)
                    cancelButtonBackgroundColor = ContextCompat.getColor(this@TenderActivity, R.color.pink)
                    setConfirmClickListener { sDialog->
                        sDialog.dismissWithAnimation()
                        viewModel.deleteTender(tender.id!!).observe(this@TenderActivity) {
                            CoroutineScope(Dispatchers.Main).launch {
                                if(it) {
                                    tenderAdapter.apply {
                                        list.remove(tender)
                                        filteredList.remove(tender)
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
            } else manageTender(tenderId = tender.id!!, pos = i)
        }
    }

    private fun postInitView() {
        binding.apply {
            recycleViewProducts.apply {
                adapter = tenderAdapter
                layoutManager = LinearLayoutManager(this@TenderActivity)
            }
            layoutHeader.data = Header(resources.getString(R.string.lbl_Id), resources.getString(R.string.lbl_TenderName), resources.getString(R.string.lbl_CardType))
        }
    } //endregion

    //region To manage click events
    private fun manageClicks() {
        binding.apply {
            layoutToolbar.imageViewAction.clickWithDebounce {
                openActivity(Intent(this@TenderActivity, DashboardActivity::class.java))
            }

            layoutToolbar.imageViewBack.clickWithDebounce {
                openActivity(Intent(this@TenderActivity, DashboardActivity::class.java))
            }

            searchProduct.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query : String) : Boolean {
                    return false
                }

                override fun onQueryTextChange(newText : String) : Boolean {
                    tenderAdapter.getFilter().filter(newText)
                    return true
                }
            })

            fabAdd.setOnClickListener {
                manageTender()
            }

            swipeRefresh.apply {
                setOnRefreshListener {
                    swipeRefresh.isRefreshing = false
                    getTender()
                    clearSearch()
                }
                setColorSchemeColors(ContextCompat.getColor(this@TenderActivity, R.color.pink))
            }
        }
    } //endregion

    @Deprecated("Deprecated in Java") override fun onBackPressed() {
        openActivity(Intent(this@TenderActivity, DashboardActivity::class.java))
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
        getTender()
    }

    private fun getTender() {
        binding.linearLayoutShimmer.visibility = View.VISIBLE
        viewModel.getTenderList().observe(this) {
            CoroutineScope(Dispatchers.Main).launch {
                binding.linearLayoutShimmer.visibility = View.GONE
                tenderAdapter.apply {
                    setList(it)
                }
            }
        }
    } //endregion

    //region To manage Tender
    private fun manageTender(tenderId : Int = 0, pos : Int = 0) {
        if(tenderId > 0) {
            viewModel.getTenderDetails(tenderId).observe(this) {
                val bundle = Bundle()
                bundle.putParcelable(Default.TENDER, it!!)
                openTenderPopup(bundle, pos)
            }
        } else openTenderPopup()
    }
    //endregion

    private fun openTenderPopup(bundle : Bundle? = null, pos : Int = 0) {
        val ft = supportFragmentManager.beginTransaction()
        val dialogFragment = TenderPopupDialog.newInstance()
        val prevFragment : Fragment? = supportFragmentManager.findFragmentByTag(TenderPopupDialog::class.java.name)
        if(prevFragment != null) return
        if(bundle != null) dialogFragment.arguments = bundle
        dialogFragment.isCancelable = false
        dialogFragment.show(ft, TenderPopupDialog::class.java.name)
        dialogFragment.setListener(object : TenderPopupDialog.OnButtonClickListener {
            override fun onButtonClickListener(typeButton : Enums.ClickEvents, tender : Tender?) {
                clearSearch()
                when(typeButton) {
                    Enums.ClickEvents.CANCEL -> {
                        if(dialogFragment.isVisible) dialogFragment.dismiss()
                    }

                    Enums.ClickEvents.SAVE -> {
                        if(dialogFragment.isVisible) dialogFragment.dismiss()
                        getTender()
                    }

                    Enums.ClickEvents.UPDATE -> {
                        if(dialogFragment.isVisible) dialogFragment.dismiss()
                        tenderAdapter.apply {
                            list[pos].name = tender!!.name
                            list[pos].sCardType = tender.sCardType
                            notifyItemChanged(pos)
                        }
                    }

                    Enums.ClickEvents.ERROR -> {
                        if(dialogFragment.isVisible) dialogFragment.dismiss()
                        showSweetDialog(this@TenderActivity, resources.getString(R.string.lbl_SomethingWrong))
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