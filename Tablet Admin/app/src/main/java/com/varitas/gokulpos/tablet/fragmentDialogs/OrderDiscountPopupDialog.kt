package com.varitas.gokulpos.tablet.fragmentDialogs

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.varitas.gokulpos.tablet.R
import com.varitas.gokulpos.tablet.activity.OrderActivity
import com.varitas.gokulpos.tablet.adapter.OrderDiscountAdapter
import com.varitas.gokulpos.tablet.databinding.FragmentDItemSpecificationBinding
import com.varitas.gokulpos.tablet.response.DiscountMapList
import com.varitas.gokulpos.tablet.utilities.Enums
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class OrderDiscountPopupDialog : BaseDialogFragment() {

    private var mActivity: Activity? = null
    private var binding: FragmentDItemSpecificationBinding? = null
    private var onButtonClickListener: OnButtonClickListener? = null
    private lateinit var orderDiscountAdapter: OrderDiscountAdapter

    companion object {
        fun newInstance(): OrderDiscountPopupDialog {
            val f = OrderDiscountPopupDialog()
            val args = Bundle()
            f.arguments = args
            return f
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        if (context is Activity) mActivity = activity
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(1500, WindowManager.LayoutParams.WRAP_CONTENT)
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?,
                             ): View {
        binding = FragmentDItemSpecificationBinding.inflate(LayoutInflater.from(context))
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        postInitViews()
        loadData()
        manageClicks()
    }

    //region To load data
    private fun loadData() {
        OrderActivity.Instance.viewModel.showProgress.observe(this) {
            OrderActivity.Instance.manageProgress(it)
        }

        OrderActivity.Instance.viewModel.fetchOpenDiscount().observe(this) {
            CoroutineScope(Dispatchers.Main).launch {
                if (it != null) {
                    val discountList = ArrayList<DiscountMapList>()
                    for (j in it) {
                        j.discountMapList.forEach { disc -> disc.discountName = j.name!! }
                        discountList.addAll(j.discountMapList)
                    }
                    orderDiscountAdapter.apply {
                        setList(discountList)
                    }
                }
            }
        }
    }
    //endregion

    //region To post init views
    private fun postInitViews() {
        orderDiscountAdapter = OrderDiscountAdapter()
//        {
//            onButtonClickListener?.onButtonClickListener(Enums.ClickEvents.LOAD, it)
//        }

        binding!!.apply {
            layoutToolbar.root.visibility = View.GONE
            linearLayoutDiscount.visibility = View.VISIBLE
            linearLayoutButtons.visibility = View.VISIBLE
            buttonSave.text = resources.getString(R.string.button_Confirm)
            recycleViewSpecificationList.apply {
                adapter = orderDiscountAdapter
                layoutManager = LinearLayoutManager(OrderActivity.Instance)
            }
        }
    }
    //endregion

    //region To manage clicks
    private fun manageClicks() {
        binding!!.apply {
            buttonClose.clickWithDebounce {
                onButtonClickListener?.onButtonClickListener(Enums.ClickEvents.CANCEL)
            }

            buttonSave.clickWithDebounce {
                orderDiscountAdapter.apply {
                    val index = list.indexOfFirst { it.isSelected }
                    if (index >= 0) onButtonClickListener?.onButtonClickListener(Enums.ClickEvents.LOAD, list[index])
                    else OrderActivity.Instance.showSweetDialog(OrderActivity.Instance, resources.getString(R.string.CartItemOperation, resources.getString(R.string.lbl_AtLeastOne)))
                }
            }
        }
    }
    //endregion

    fun setListener(listener: OnButtonClickListener) {
        this.onButtonClickListener = listener
    }

    interface OnButtonClickListener {
        fun onButtonClickListener(typeButton: Enums.ClickEvents, discount: DiscountMapList? = null)
    }
}