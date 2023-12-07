package com.varitas.gokulpos.fragmentDialogs

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.varitas.gokulpos.activity.ProductListActivity
import com.varitas.gokulpos.adapter.StockAdapter
import com.varitas.gokulpos.databinding.FragmentDChangequantityBinding
import com.varitas.gokulpos.request.ItemStock
import com.varitas.gokulpos.response.ProductList
import com.varitas.gokulpos.utilities.Default
import com.varitas.gokulpos.utilities.Enums
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ChangeQuantityPopupDialog : BaseDialogFragment() {
    private var mActivity : Activity? = null
    private var onButtonClickListener : OnButtonClickListener? = null
    var title : String = ""
    private var binding : FragmentDChangequantityBinding? = null
    private var product : ProductList? = null
    private lateinit var stockAdapter : StockAdapter

    @Deprecated("Deprecated in Java")
    override fun onAttach(activity : Activity) {
        super.onAttach(activity)
        if(context is Activity) mActivity = activity
    }

    companion object {
        fun newInstance() : ChangeQuantityPopupDialog {
            val f = ChangeQuantityPopupDialog()
            val args = Bundle()
            f.arguments = args
            return f
        }
    }

    override fun onCreateView(inflater : LayoutInflater, container : ViewGroup?, savedInstanceState : Bundle?) : View {
        binding = FragmentDChangequantityBinding.inflate(LayoutInflater.from(context))
        return binding!!.root
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
    }

    override fun onViewCreated(view : View, savedInstanceState : Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(arguments != null) {
            title = arguments?.getString(Default.TITLE, "")!!
            product = arguments?.getParcelable(Default.PRODUCT) as ProductList?
        }
        initData()
        postInitViews()
        loadData()
        manageClicks()
    }

    private fun initData() {
        stockAdapter = StockAdapter { _, _, _->

        }
    }

    private fun loadData() {
        ProductListActivity.Instance.viewModel.showProgress.observe(this) {
            ProductListActivity.Instance.manageProgress(it)
        }

        ProductListActivity.Instance.viewModel.getItemStock(product!!.id!!).observe(ProductListActivity.Instance) {
            CoroutineScope(Dispatchers.Main).launch {
                if(it != null) {
                    binding?.apply {
                        stockAdapter.apply {
                            list.clear()
                            for(i in it.indices) {
                                if(it[i].stock != null && it[i].stock.size > 0) setList(it[i].stock)
                            }
                        }
                    }
                }
            }

        }

    }

    private fun postInitViews() {
        if(product != null) {
            binding!!.layoutToolbar.textViewTitle.isSelected = true
            binding!!.layoutToolbar.textViewTitle.text = product!!.name
        } else binding!!.layoutToolbar.textViewTitle.text = title

        binding?.apply {
            recycleViewFacility.apply {
                adapter = stockAdapter
                layoutManager = LinearLayoutManager(ProductListActivity.Instance)
            }
        }

    }

    private fun manageClicks() {
        binding!!.layoutToolbar.imageViewCancel.clickWithDebounce {
            onButtonClickListener?.onButtonClickListener(Enums.ClickEvents.CANCEL)
        }
        binding?.apply {
            buttonUpdate.setOnClickListener {
                val stock = ArrayList<ItemStock>()
                stockAdapter.apply {
                    for(i in list.indices) {
                        stock.add(
                            ItemStock(
                                id = list[i].id,
                                quantity = list[i].qty + list[i].quantity!!,
                                facilityId = list[i].facilityId
                            )
                        )
                    }
                }
                ProductListActivity.Instance.viewModel.updateItemStock(stock).observe(ProductListActivity.Instance) {
                    if(it) {
                        onButtonClickListener?.onButtonClickListener(Enums.ClickEvents.UPDATE)
                    }
                }
            }

            buttonReplace.setOnClickListener {
                val stock = ArrayList<ItemStock>()
                stockAdapter.apply {
                    for(i in list.indices) {
                        stock.add(
                            ItemStock(
                                id = list[i].id,
                                quantity = list[i].qty,
                                facilityId = list[i].facilityId
                            )
                        )
                    }
                }
                ProductListActivity.Instance.viewModel.updateItemStock(stock).observe(ProductListActivity.Instance) {
                    if(it) onButtonClickListener?.onButtonClickListener(Enums.ClickEvents.UPDATE)
                }
            }
        }


    }

    interface OnButtonClickListener {
        fun onButtonClickListener(typeButton : Enums.ClickEvents)
    }

    fun setListener(listener : OnButtonClickListener) {
        this.onButtonClickListener = listener
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}