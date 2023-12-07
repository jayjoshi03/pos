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
import com.varitas.gokulpos.tablet.adapter.OrderItemAdapter
import com.varitas.gokulpos.tablet.databinding.FragmentDItemsBinding
import com.varitas.gokulpos.tablet.response.CompletedOrder
import com.varitas.gokulpos.tablet.response.OrderItemDetails
import com.varitas.gokulpos.tablet.response.OrderList
import com.varitas.gokulpos.tablet.utilities.Default
import com.varitas.gokulpos.tablet.utilities.Enums

class ItemsPopupDialog : BaseDialogFragment() {

    private var mActivity: Activity? = null
    private var binding: FragmentDItemsBinding? = null
    private var onButtonClickListener: OnButtonClickListener? = null
    private var details: OrderList? = null
    private var orderDetails: CompletedOrder? = null
    private lateinit var orderItemAdapter: OrderItemAdapter
    private var isComplete = false

    companion object {
        fun newInstance(): ItemsPopupDialog {
            val f = ItemsPopupDialog()
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
        dialog?.window?.setLayout(resources.getInteger(R.integer.fragmentWidth), WindowManager.LayoutParams.WRAP_CONTENT)
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?,
                             ): View {
        binding = FragmentDItemsBinding.inflate(LayoutInflater.from(context))
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (arguments != null) {
            isComplete = arguments!!.getBoolean(Default.COMPLETE)
            if(isComplete){
                if (arguments != null) {
                    val orderParcelable = arguments?.getParcelable<CompletedOrder>(Default.ORDER)
                    if (orderParcelable != null) orderDetails = orderParcelable
                }
            } else {
                if (arguments != null) {
                    val orderParcelable = arguments?.getParcelable<OrderList>(Default.ORDER)
                    if (orderParcelable != null) details = orderParcelable
                }
            }

        }
        postInitViews()
        loadData()
        manageClicks()
    }

    private fun postInitViews() {

        orderItemAdapter = OrderItemAdapter(isCompleted = isComplete)

        binding!!.apply {
            isCompleted = isComplete
            layoutToolbar.textViewTitle.text = resources.getText(R.string.lbl_Items)
            recycleViewItems.apply {
                adapter = orderItemAdapter
                layoutManager = LinearLayoutManager(OrderActivity.Instance)
            }
        }
    }

    private fun loadData() {
        orderItemAdapter.apply {
            orderItemAdapter.apply {
                setList(if(isComplete) orderDetails!!.orderDetailsApiModels else details!!.orderDetailsApiModels)
            }
        }
    }

    private fun manageClicks() {
        binding!!.apply {
            layoutToolbar.imageViewCancel.clickWithDebounce {
                onButtonClickListener?.onButtonClickListener(Enums.ClickEvents.CANCEL, ArrayList())
            }
            buttonSave.clickWithDebounce {
                orderItemAdapter.apply {
                    val selectedItems = list.filter { it.isSelected }

                    if (selectedItems.isNotEmpty()) {
                        onButtonClickListener?.onButtonClickListener(Enums.ClickEvents.LOAD, selectedItems)
                    } else {
                        // Handle the case when no items are selected
                        return@clickWithDebounce
                    }
                }
            }
        }
    }

    fun setListener(listener: OnButtonClickListener) {
        this.onButtonClickListener = listener
    }

    interface OnButtonClickListener {
        fun onButtonClickListener(typeButton: Enums.ClickEvents, selectedItems: List<OrderItemDetails>)
    }
}