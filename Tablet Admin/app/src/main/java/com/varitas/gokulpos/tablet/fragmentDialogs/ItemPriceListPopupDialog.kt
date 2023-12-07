package com.varitas.gokulpos.tablet.fragmentDialogs

import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.varitas.gokulpos.tablet.R
import com.varitas.gokulpos.tablet.activity.AddProductActivity
import com.varitas.gokulpos.tablet.activity.EditProductActivity
import com.varitas.gokulpos.tablet.adapter.PriceAdapter
import com.varitas.gokulpos.tablet.databinding.FragmentDItemSpecificationBinding
import com.varitas.gokulpos.tablet.request.ItemPrice
import com.varitas.gokulpos.tablet.utilities.Default
import com.varitas.gokulpos.tablet.utilities.Enums

class ItemPriceListPopupDialog : BaseDialogFragment() {
    private var mActivity: Activity? = null
    private var onButtonClickListener: OnButtonClickListener? = null
    private var binding: FragmentDItemSpecificationBinding? = null
    private lateinit var priceAdapter: PriceAdapter
    private lateinit var priceList: ArrayList<ItemPrice>
    private var isEdit = false

    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        if (context is Activity) mActivity = activity
    }

    companion object {
        fun newInstance(): ItemPriceListPopupDialog {
            val f = ItemPriceListPopupDialog()
            val args = Bundle()
            f.arguments = args
            return f
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentDItemSpecificationBinding.inflate(LayoutInflater.from(context))
        return binding!!.root
    }

    override fun onStart() {
        super.onStart()
        val param = WindowManager.LayoutParams()
        param.copyFrom(dialog!!.window!!.attributes)
        param.width = resources.getInteger(R.integer.fragmentHeight)
        param.height = WindowManager.LayoutParams.WRAP_CONTENT
        dialog?.window?.attributes = param
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (arguments != null) {
            if (arguments!!.getSerializable(Default.PRICE) != null) {
                priceList = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) (arguments?.getSerializable(Default.PRICE))!! as ArrayList<ItemPrice>
                else arguments?.getSerializable(Default.PRICE)!! as ArrayList<ItemPrice>
            }
            isEdit = arguments?.getBoolean(Default.EDIT)!!
        }
        initData()
        postInitViews()
        loadData()
        manageClicks()
    }

    private fun initData() {
        priceAdapter = PriceAdapter {
            priceAdapter.apply {
                list.removeAt(it)
                priceList.removeAt(it)
                notifyItemRemoved(it)
                notifyItemRangeChanged(it, list.size)
            }
        }
    }

    private fun loadData() {
        priceAdapter.apply {
            setList(priceList)
        }
    }

    private fun postInitViews() {
        binding?.apply {
            layoutToolbar.root.visibility = View.GONE
            linearLayoutButtons.visibility = View.VISIBLE
            linearLayoutPrice.visibility = View.VISIBLE
            buttonSave.text = resources.getString(R.string.button_Save)
            recycleViewSpecificationList.apply {
                adapter = priceAdapter
                layoutManager = LinearLayoutManager(if (!isEdit) AddProductActivity.Instance else EditProductActivity.Instance)
            }
        }

    }

    private fun manageClicks() {
        binding!!.apply {
            buttonClose.clickWithDebounce {
                onButtonClickListener?.onButtonClickListener(Enums.ClickEvents.CANCEL)
            }

            buttonSave.clickWithDebounce {
                onButtonClickListener?.onButtonClickListener(Enums.ClickEvents.CANCEL, priceList)
            }
        }
    }

    interface OnButtonClickListener {
        fun onButtonClickListener(typeButton: Enums.ClickEvents, list: ArrayList<ItemPrice> = ArrayList())
    }

    fun setListener(listener: OnButtonClickListener) {
        this.onButtonClickListener = listener
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}