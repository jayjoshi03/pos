package com.varitas.gokulpos.fragmentDialogs

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.varitas.gokulpos.R
import com.varitas.gokulpos.activity.ProductListActivity
import com.varitas.gokulpos.adapter.MultiPackAdapter
import com.varitas.gokulpos.databinding.FragmentDMultipackBinding
import com.varitas.gokulpos.model.Header
import com.varitas.gokulpos.request.ItemPrice
import com.varitas.gokulpos.utilities.Default
import com.varitas.gokulpos.utilities.Enums
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MultiPackPopupDialog : BaseDialogFragment() {
    private var mActivity : Activity? = null
    private var onButtonClickListener : OnButtonClickListener? = null
    var title : String = ""
    private var binding : FragmentDMultipackBinding? = null
    private var productDetail : ArrayList<ItemPrice>? = null
    private lateinit var multiPackAdapter : MultiPackAdapter
    private var productId : Int = 0

    @Deprecated("Deprecated in Java")
    override fun onAttach(activity : Activity) {
        super.onAttach(activity)
        if(context is Activity) mActivity = activity
    }

    companion object {
        fun newInstance() : MultiPackPopupDialog {
            val f = MultiPackPopupDialog()
            val args = Bundle()
            f.arguments = args
            return f
        }
    }

    override fun onCreateView(inflater : LayoutInflater, container : ViewGroup?, savedInstanceState : Bundle?) : View {
        binding = FragmentDMultipackBinding.inflate(LayoutInflater.from(context))
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
            productId = arguments?.getInt(Default.PRODUCT_ID)!!
            try {
                (arguments!!.getSerializable(Default.PRODUCT) as ArrayList<ItemPrice>?).also { productDetail = it }
            } catch(ex : Exception) {
                ex.printStackTrace()
            }
        }
        initData()
        postInitViews()
        loadData()
        manageClicks()
    }

    private fun loadData() {

        ProductListActivity.Instance.viewModel.getPriceList(productId).observe(ProductListActivity.Instance) {
            CoroutineScope(Dispatchers.Main).launch {
                if(it != null) {
                    binding?.apply {
                        val prices = ArrayList<ItemPrice>()
                        multiPackAdapter.apply {
                            list.clear()
                            filteredList.clear()
                            for(data in it) {
                                for(price in data.price) prices.add(price)
                            }
                            setList(prices)
                        }
                    }
                }
            }
        }

    }

    private fun initData() {
        multiPackAdapter = MultiPackAdapter { multipack, i->
            ProductListActivity.Instance.managePrice(multipack, title, true)
        }
    }

    private fun postInitViews() {
        if(productDetail != null) {
            binding!!.layoutToolbar.textViewTitle.isSelected = true
            binding!!.layoutToolbar.textViewTitle.text = title
        } else binding!!.layoutToolbar.textViewTitle.text = title

        binding?.apply {
            recycleViewMultiPack.apply {
                adapter = multiPackAdapter
                layoutManager = LinearLayoutManager(ProductListActivity.Instance)
            }
        }
        binding!!.layoutHeader.data = Header(resources.getString(R.string.Menu_Specification), resources.getString(R.string.hint_UnitPrice))
    }

    private fun manageClicks() {
        binding!!.layoutToolbar.imageViewCancel.clickWithDebounce {
            onButtonClickListener?.onButtonClickListener(Enums.ClickEvents.CANCEL)
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