package com.varitas.gokulpos.fragmentDialogs

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.varitas.gokulpos.R
import com.varitas.gokulpos.activity.VendorActivity
import com.varitas.gokulpos.adapter.SalesPersonCountAdapter
import com.varitas.gokulpos.databinding.FragmentDSalespersonCountBinding
import com.varitas.gokulpos.model.Header
import com.varitas.gokulpos.utilities.Default
import com.varitas.gokulpos.utilities.Enums
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SalesPersonCountPopupDialog : BaseDialogFragment() {

    private var mActivity : Activity? = null
    private var binding : FragmentDSalespersonCountBinding? = null
    private var onButtonClickListener : OnButtonClickListener? = null
    private var id : Int? = null
    private var user = ""
    private lateinit var salesCountAdapter : SalesPersonCountAdapter


    companion object {
        fun newInstance() : SalesPersonCountPopupDialog {
            val f = SalesPersonCountPopupDialog()
            val args = Bundle()
            f.arguments = args
            return f
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onAttach(activity : Activity) {
        super.onAttach(activity)
        if(context is Activity) mActivity = activity
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
    }

    override fun onCreateView(
        inflater : LayoutInflater, container : ViewGroup?,
        savedInstanceState : Bundle?,
    ) : View {
        binding = FragmentDSalespersonCountBinding.inflate(LayoutInflater.from(context))
        return binding!!.root
    }

    override fun onViewCreated(view : View, savedInstanceState : Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(arguments != null) {
            id = arguments?.getInt(Default.ID)
            user = arguments?.getString(Default.USER).toString()
        }
        salesCountAdapter = SalesPersonCountAdapter()
        postInitViews()
        loadData()
        manageClicks()
    }

    private fun postInitViews() {
        binding!!.apply {
            layoutToolbar.textViewTitle.text = user
            recycleViewSpecificationList.apply {
                adapter = salesCountAdapter
                layoutManager = LinearLayoutManager(context)
            }
            header = Header(resources.getString(R.string.lbl_Id), resources.getString(R.string.hint_Name), resources.getString(R.string.hint_ContactNo))
        }
    }

    private fun loadData() {
        VendorActivity.Instance.viewModel.getSalesPersonCount(id!!).observe(VendorActivity.Instance) {
            if(it.isNotEmpty()) {
                CoroutineScope(Dispatchers.Main).launch {
                    salesCountAdapter.apply {
                        setList(it)
                    }
                }
            } else onButtonClickListener?.onButtonClickListener(Enums.ClickEvents.CANCEL)
        }
    }

    private fun manageClicks() {
        binding!!.apply {
            layoutToolbar.imageViewCancel.clickWithDebounce {
                onButtonClickListener?.onButtonClickListener(Enums.ClickEvents.CANCEL)
            }
        }
    }

    fun setListener(listener : OnButtonClickListener) {
        this.onButtonClickListener = listener
    }

    interface OnButtonClickListener {
        fun onButtonClickListener(typeButton : Enums.ClickEvents)
    }
}