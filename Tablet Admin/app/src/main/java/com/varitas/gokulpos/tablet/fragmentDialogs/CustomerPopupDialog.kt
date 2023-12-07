package com.varitas.gokulpos.tablet.fragmentDialogs

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.varitas.gokulpos.tablet.R
import com.varitas.gokulpos.tablet.activity.OrderActivity
import com.varitas.gokulpos.tablet.adapter.CustomerDetailAdapter
import com.varitas.gokulpos.tablet.databinding.FragmentDCustomerBinding
import com.varitas.gokulpos.tablet.model.Header
import com.varitas.gokulpos.tablet.utilities.Enums
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CustomerPopupDialog : BaseDialogFragment() {

    private var mActivity: Activity? = null
    private var binding: FragmentDCustomerBinding? = null
    private var onButtonClickListener: OnButtonClickListener? = null
    private lateinit var customerDetailAdapter: CustomerDetailAdapter

    companion object {
        fun newInstance(): CustomerPopupDialog {
            val f = CustomerPopupDialog()
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
        dialog?.window?.setLayout(resources.getInteger(R.integer.fragmentWidth), resources.getInteger(R.integer.fragmentHeight))
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?,
                             ): View {
        binding = FragmentDCustomerBinding.inflate(LayoutInflater.from(context))
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        postInitViews()
        loadData()
        manageClicks()
    }

    private fun loadData() {
        OrderActivity.Instance.viewModelCustomer.showProgress.observe(this) {
            OrderActivity.Instance.manageProgress(it)
        }

        OrderActivity.Instance.viewModelCustomer.fetchCustomers().observe(this) {
            CoroutineScope(Dispatchers.Main).launch {
                customerDetailAdapter.apply {
                    setList(it)
                }
            }
        }
    }

    private fun postInitViews() {
        customerDetailAdapter = CustomerDetailAdapter(true) { customer, typeButton, _ ->
            when (typeButton) {
                Enums.ClickEvents.VIEW -> {
                    onButtonClickListener?.onButtonClickListener(Enums.ClickEvents.VIEW, customerName = customer.name!!, customerId = customer.id!!)
                }
                else -> {}
            }
        }

        binding!!.apply {
            layoutToolbar.textViewTitle.text = resources.getString(R.string.lbl_CustomerList)
            recycleViewCustomer.apply {
                adapter = customerDetailAdapter
                layoutManager = LinearLayoutManager(OrderActivity.Instance)
            }
            layoutHeader.data = Header(resources.getString(R.string.lbl_Id), resources.getString(R.string.hint_Name), resources.getString(R.string.lbl_PhoneNumber), resources.getString(R.string.lbl_DrivingLicense), resources.getString(R.string.lbl_Select))
        }
    }

    private fun manageClicks() {
        binding!!.apply {
            layoutToolbar.imageViewCancel.clickWithDebounce {
                onButtonClickListener?.onButtonClickListener(Enums.ClickEvents.CANCEL)
            }
            searchCustomer.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String): Boolean {
                    return false
                }

                override fun onQueryTextChange(newText: String): Boolean {
                    customerDetailAdapter.getFilter().filter(newText)
                    return true
                }
            })
        }
    }

    fun setListener(listener: OnButtonClickListener) {
        this.onButtonClickListener = listener
    }

    interface OnButtonClickListener {
        fun onButtonClickListener(typeButton: Enums.ClickEvents, customerName: String = "", customerId: Int = 0)
    }
}