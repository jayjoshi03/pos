package com.varitas.gokulpos.tablet.fragmentDialogs

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.recyclerview.widget.LinearLayoutManager
import com.varitas.gokulpos.tablet.R
import com.varitas.gokulpos.tablet.activity.OrderActivity
import com.varitas.gokulpos.tablet.adapter.TaxGroupAdapter
import com.varitas.gokulpos.tablet.databinding.FragmentDTaxGroupBinding
import com.varitas.gokulpos.tablet.model.Header
import com.varitas.gokulpos.tablet.response.CommonDropDown
import com.varitas.gokulpos.tablet.response.TaxList
import com.varitas.gokulpos.tablet.utilities.CustomSpinner
import com.varitas.gokulpos.tablet.utilities.Default
import com.varitas.gokulpos.tablet.utilities.Enums
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TaxGroupPopupDialog : BaseDialogFragment() {

    private var mActivity: Activity? = null
    private var binding: FragmentDTaxGroupBinding? = null
    private var onButtonClickListener: OnButtonClickListener? = null
    private lateinit var taxGroupList: ArrayList<CommonDropDown>
    private lateinit var taxGroupSpinner: ArrayAdapter<CommonDropDown>
    private lateinit var taxGroup: CommonDropDown
    private lateinit var taxGroupAdapter: TaxGroupAdapter
    private var groupId = 0
    private var itemId = 0
    private var specificationId = 0
    private var price = 0.00

    companion object {
        fun newInstance(): TaxGroupPopupDialog {
            val f = TaxGroupPopupDialog()
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
        dialog?.window?.setLayout(1000, WindowManager.LayoutParams.WRAP_CONTENT)
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?,
                             ): View {
        binding = FragmentDTaxGroupBinding.inflate(LayoutInflater.from(context))
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (arguments != null) {
            groupId = arguments?.getInt(Default.ID)!!
            itemId = arguments?.getInt(Default.PRODUCT_ID)!!
            specificationId = arguments?.getInt(Default.SPECIFICATION)!!
            price = arguments?.getDouble(Default.PRICE)!!
        }
        initData()
        postInitViews()
        loadData()
        manageClicks()
    }

    private fun initData() {
        taxGroupList = ArrayList()
        taxGroupSpinner = ArrayAdapter(OrderActivity.Instance, R.layout.spinner_items, taxGroupList)
        taxGroupAdapter = TaxGroupAdapter(price)
    }

    private fun loadData() {
        OrderActivity.Instance.featureViewModel.showProgress.observe(this) {
            OrderActivity.Instance.manageProgress(it)
        }

        OrderActivity.Instance.featureViewModel.fetchGroupsById(Default.TYPE_TAX).observe(OrderActivity.Instance) { tax ->
            CoroutineScope(Dispatchers.Main).launch {
                taxGroupSpinner.apply {
                    setDropDownViewResource(R.layout.spinner_dropdown_item)
                    taxGroup = CommonDropDown(label = resources.getString(R.string.lbl_SelectTax), value = 0)
                    add(taxGroup)
                    addAll(tax)
                    val ind = taxGroupList.indexOfFirst { taxGroup -> taxGroup.value == groupId }
                    if (ind >= 0) {
                        binding!!.spinnerTaxGroup.setSelection(ind)
                        taxGroup = taxGroupList[ind]
                    }
                    notifyDataSetChanged()
                }
            }
        }

        if (groupId > 0) getTaxDetails(groupId)
    }

    private fun getTaxDetails(id: Int) {
        OrderActivity.Instance.featureViewModel.getGroupDetails(id, itemId, specificationId, price).observe(OrderActivity.Instance) {
            CoroutineScope(Dispatchers.Main).launch {
                taxGroupAdapter.apply {
                    setCustomList(it.tax)
                }
            }
        }
    }

    private fun postInitViews() {
        binding!!.apply {
            layoutToolbar.textViewTitle.text = resources.getString(R.string.Menu_Group)
            data = Header(resources.getString(R.string.lbl_Id), resources.getString(R.string.hint_Name), resources.getString(R.string.lbl_Rate), resources.getString(R.string.lbl_Amount))
            recycleViewTax.apply {
                adapter = taxGroupAdapter
                layoutManager = LinearLayoutManager(OrderActivity.Instance)
            }
        }
    }

    private fun manageClicks() {
        binding!!.apply {
            layoutToolbar.imageViewCancel.clickWithDebounce {
                onButtonClickListener?.onButtonClickListener(Enums.ClickEvents.CANCEL)
            }

            buttonSave.clickWithDebounce {
                val data = ArrayList<TaxList>()
                taxGroupAdapter.apply {
                    for (i in list.indices) data.add(TaxList(id = list[i].id, className = list[i].taxName, rateValue = list[i].taxRate))
                }
                onButtonClickListener?.onButtonClickListener(Enums.ClickEvents.SAVE, taxGroup.value!!, data)
            }

            buttonRemoveTax.clickWithDebounce {
                onButtonClickListener?.onButtonClickListener(Enums.ClickEvents.REMOVE, 0, ArrayList())
            }

            spinnerTaxGroup.apply {
                adapter = taxGroupSpinner
                var previousSelectedItem: CommonDropDown? = null
                setSpinnerEventsListener(object : CustomSpinner.OnSpinnerEventsListener {
                    override fun onSpinnerOpened(spinner: Spinner?) {
                        previousSelectedItem = spinner?.selectedItem as? CommonDropDown
                    }

                    override fun onSpinnerClosed(spinner: Spinner?) {
                        taxGroup = spinner?.selectedItem as CommonDropDown
                        if (taxGroup != previousSelectedItem) {
                            if (taxGroup.value!! > 0) getTaxDetails(taxGroup.value!!)
                        }
                        previousSelectedItem = null
                    }
                })
            }
        }
    }

    fun setListener(listener: OnButtonClickListener) {
        this.onButtonClickListener = listener
    }

    interface OnButtonClickListener {
        fun onButtonClickListener(typeButton: Enums.ClickEvents, id: Int = 0, taxDetails: ArrayList<TaxList> = ArrayList())
    }
}