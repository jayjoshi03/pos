package com.varitas.gokulpos.tablet.fragmentDialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import com.varitas.gokulpos.tablet.activity.BrandActivity
import com.varitas.gokulpos.tablet.activity.CategoryActivity
import com.varitas.gokulpos.tablet.activity.CustomersActivity
import com.varitas.gokulpos.tablet.activity.DepartmentActivity
import com.varitas.gokulpos.tablet.activity.EmployeesActivity
import com.varitas.gokulpos.tablet.activity.FacilityActivity
import com.varitas.gokulpos.tablet.activity.GroupActivity
import com.varitas.gokulpos.tablet.activity.InvoiceActivity
import com.varitas.gokulpos.tablet.activity.OrderActivity
import com.varitas.gokulpos.tablet.activity.PurchaseOrderActivity
import com.varitas.gokulpos.tablet.activity.SalesPromotionActivity
import com.varitas.gokulpos.tablet.activity.SpecificationActivity
import com.varitas.gokulpos.tablet.activity.TaxActivity
import com.varitas.gokulpos.tablet.activity.TenderActivity
import com.varitas.gokulpos.tablet.activity.UOMActivity
import com.varitas.gokulpos.tablet.activity.VendorActivity
import com.varitas.gokulpos.tablet.databinding.DeleteAlertBinding
import com.varitas.gokulpos.tablet.utilities.Enums
import com.varitas.gokulpos.tablet.utilities.Utils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class DeleteAlertPopupDialog(private val message: String, private val id: Int, private val isDepartment: Boolean = false, private val isBrand: Boolean = false,
                             private val isCategory: Boolean = false, private val isFacility: Boolean = false, private val isGroup: Boolean = false, private val isSpecification: Boolean = false,
                             private val isTax: Boolean = false, private val isVendor: Boolean = false, private val isUOM: Boolean = false, private val isBatchClose: Boolean = false,
                             private val isPromotion: Boolean = false, private val isPO: Boolean = false, private val isTender: Boolean = false, private val isCustomer: Boolean = false,
                             private val isEmployee: Boolean = false, private val isBarItem: Boolean = false,private val changeItemType: Boolean = false, private val changeStoreStatus : Boolean = false,
                             private val appliedDiscount : Boolean = false, private val isInvoice:Boolean = false, private val isInsertInvoice:Boolean = false) : BaseDialogFragment() {

    private var _binding: DeleteAlertBinding? = null
    private var onButtonClickListener: OnButtonClickListener? = null
    private val binding get() = _binding!!

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?,
                             ): View {
        _binding = DeleteAlertBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        val param = WindowManager.LayoutParams()
        param.copyFrom(dialog!!.window!!.attributes)
        param.width = 1500
        param.height = WindowManager.LayoutParams.WRAP_CONTENT
        dialog?.window?.attributes = param
    }

    override fun onViewCreated(view : View, savedInstanceState : Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.messageTextView.text = message
        binding.buttonYes.setOnClickListener {
            dismiss()
            when {
                isDepartment -> {
                    DepartmentActivity.Instance.viewModel.deleteDepartment(id).observe(DepartmentActivity.Instance) {
                        CoroutineScope(Dispatchers.Main).launch {
                            if (it) onButtonClickListener?.onButtonClickListener(Enums.ClickEvents.DELETE)
                        }
                    }
                }

                isBrand -> {
                    BrandActivity.Instance.viewModel.deleteBrand(id).observe(BrandActivity.Instance) {
                        CoroutineScope(Dispatchers.Main).launch {
                            if (it) onButtonClickListener?.onButtonClickListener(Enums.ClickEvents.DELETE)
                        }
                    }
                }

                isCategory -> {
                    CategoryActivity.Instance.viewModel.deleteCategory(id).observe(CategoryActivity.Instance) {
                        CoroutineScope(Dispatchers.Main).launch {
                            if (it) onButtonClickListener?.onButtonClickListener(Enums.ClickEvents.DELETE)
                        }
                    }
                }

                isFacility -> {
                    FacilityActivity.Instance.viewModel.deleteFacility(id).observe(FacilityActivity.Instance) {
                        CoroutineScope(Dispatchers.Main).launch {
                            if (it) onButtonClickListener?.onButtonClickListener(Enums.ClickEvents.DELETE)
                        }
                    }
                }

                isGroup -> {
                    GroupActivity.Instance.viewModel.deleteGroup(id).observe(GroupActivity.Instance) {
                        CoroutineScope(Dispatchers.Main).launch {
                            if (it) onButtonClickListener?.onButtonClickListener(Enums.ClickEvents.DELETE)
                        }
                    }
                }

                isSpecification -> {
                    SpecificationActivity.Instance.viewModel.deleteSpecification(id).observe(SpecificationActivity.Instance) {
                        CoroutineScope(Dispatchers.Main).launch {
                            if (it) onButtonClickListener?.onButtonClickListener(Enums.ClickEvents.DELETE)
                        }
                    }
                }

                isTax -> {
                    TaxActivity.Instance.viewModel.deleteTax(id).observe(TaxActivity.Instance) {
                        CoroutineScope(Dispatchers.Main).launch {
                            if (it) onButtonClickListener?.onButtonClickListener(Enums.ClickEvents.DELETE)
                        }
                    }
                }

                isVendor -> {
                    VendorActivity.Instance.viewModel.deleteVendor(id).observe(VendorActivity.Instance) {
                        CoroutineScope(Dispatchers.Main).launch {
                            if (it) onButtonClickListener?.onButtonClickListener(Enums.ClickEvents.DELETE)
                        }
                    }
                }

                isUOM -> {
                    UOMActivity.Instance.viewModel.deleteUOM(id).observe(UOMActivity.Instance) {
                        CoroutineScope(Dispatchers.Main).launch {
                            if (it) onButtonClickListener?.onButtonClickListener(Enums.ClickEvents.DELETE)
                        }
                    }
                }

                isBatchClose -> {
                    OrderActivity.Instance.viewModel.closeBatch(id).observe(OrderActivity.Instance) {
                        CoroutineScope(Dispatchers.Main).launch {
                            if(it) {
                                Utils.manageLoginResponse(0)
                                delay(1000)
                                onButtonClickListener?.onButtonClickListener(Enums.ClickEvents.CLOSE)
                            }
                        }
                    }
                }

                isPromotion -> {
                    SalesPromotionActivity.Instance.viewModel.deletePromotion(id).observe(SalesPromotionActivity.Instance) {
                        if (it) onButtonClickListener?.onButtonClickListener(Enums.ClickEvents.DELETE)
                    }
                }

                isPO -> {
                    PurchaseOrderActivity.Instance.viewModel.deletePurchaseOrder(id).observe(PurchaseOrderActivity.Instance) {
                        if (it) onButtonClickListener?.onButtonClickListener(Enums.ClickEvents.DELETE)
                    }
                }
                isTender -> {
                    TenderActivity.Instance.viewModel.deleteTender(id).observe(TenderActivity.Instance) {
                        CoroutineScope(Dispatchers.Main).launch {
                            if (it) onButtonClickListener?.onButtonClickListener(Enums.ClickEvents.DELETE)
                        }
                    }
                }

                isCustomer -> {
                    CustomersActivity.Instance.viewModel.deleteCustomer(id).observe(CustomersActivity.Instance) {
                        CoroutineScope(Dispatchers.Main).launch {
                            if (it) onButtonClickListener?.onButtonClickListener(Enums.ClickEvents.DELETE)
                        }
                    }
                }

                isEmployee -> {
                    EmployeesActivity.Instance.viewModel.deleteEmployee(id).observe(EmployeesActivity.Instance) {
                        CoroutineScope(Dispatchers.Main).launch {
                            if (it) onButtonClickListener?.onButtonClickListener(Enums.ClickEvents.DELETE)
                        }
                    }
                }

                isBarItem -> onButtonClickListener?.onButtonClickListener(Enums.ClickEvents.DELETE)

                changeItemType -> onButtonClickListener?.onButtonClickListener(Enums.ClickEvents.DELETE)

                changeStoreStatus -> onButtonClickListener?.onButtonClickListener(Enums.ClickEvents.DELETE)

                appliedDiscount -> onButtonClickListener?.onButtonClickListener(Enums.ClickEvents.SAVE)

                isInvoice -> {
                    InvoiceActivity.Instance.viewModel.deleteInvoice(id).observe(InvoiceActivity.Instance){
                        CoroutineScope(Dispatchers.Main).launch {
                            if (it) onButtonClickListener?.onButtonClickListener(Enums.ClickEvents.DELETE)
                        }
                    }
                }

                isInsertInvoice -> onButtonClickListener?.onButtonClickListener(Enums.ClickEvents.DELETE)

            }

        }
        binding.buttonNo.setOnClickListener {
            dismiss()
            when {
                changeItemType -> onButtonClickListener?.onButtonClickListener(Enums.ClickEvents.CANCEL)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun setListener(listener : OnButtonClickListener) {
        this.onButtonClickListener = listener
    }

    interface OnButtonClickListener {
        fun onButtonClickListener(typeButton : Enums.ClickEvents)
    }
}