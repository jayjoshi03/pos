package com.varitas.gokulpos.tablet.fragmentDialogs

import android.app.Activity
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.content.ContextCompat
import com.varitas.gokulpos.tablet.R
import com.varitas.gokulpos.tablet.activity.OrderActivity
import com.varitas.gokulpos.tablet.databinding.FragmentDBalanceBinding
import com.varitas.gokulpos.tablet.response.OpenBatchRequest
import com.varitas.gokulpos.tablet.utilities.Enums
import android.view.inputmethod.EditorInfo

class OpenBalancePopupDialog : BaseDialogFragment() {

    private var mActivity: Activity? = null
    private var binding: FragmentDBalanceBinding? = null
    private var onButtonClickListener: OnButtonClickListener? = null

    companion object {
        fun newInstance(): OpenBalancePopupDialog {
            val f = OpenBalancePopupDialog()
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
        binding = FragmentDBalanceBinding.inflate(LayoutInflater.from(context))
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        postInitViews()
        loadData()
        manageClicks()
    }

    private fun loadData() {
        OrderActivity.Instance.viewModel.showProgress.observe(this) {
            OrderActivity.Instance.manageProgress(it)
        }
    }

    private fun postInitViews() {
        binding!!.apply {
            textInputBalance.requestFocus()
            layoutToolbar.textViewTitle.text = resources.getString(R.string.lbl_OpenBalance)
        }
    }

    private fun manageClicks() {
        binding!!.apply {
            layoutToolbar.imageViewCancel.setImageDrawable(ContextCompat.getDrawable(OrderActivity.Instance, R.drawable.ic_save))
            layoutToolbar.imageViewCancel.clickWithDebounce {
                val balance = binding!!.textInputBalance.text.toString().trim()
                if(!TextUtils.isEmpty(balance)){
                    val req = OpenBatchRequest(openingBalance = balance.toDouble())
                    OrderActivity.Instance.viewModel.openBatch(req).observe(OrderActivity.Instance){
                        if(it != null)
                            onButtonClickListener?.onButtonClickListener(Enums.ClickEvents.SAVE, it.id!!)
                    }
                }
            }

            textInputBalance.setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    // Enter key or action button pressed
                    val balance = binding?.textInputBalance?.text.toString().trim()
                    if (!TextUtils.isEmpty(balance)) {
                        val req = OpenBatchRequest(openingBalance = balance.toDouble())
                        OrderActivity.Instance.viewModel.openBatch(req).observe(OrderActivity.Instance) {
                            if (it != null)
                                onButtonClickListener?.onButtonClickListener(Enums.ClickEvents.SAVE, it.id!!)
                        }
                    }
                    true // Consume the action event
                } else {
                    false // Allow other action events to be processed
                }
            }
        }
    }



    fun setListener(listener: OnButtonClickListener) {
        this.onButtonClickListener = listener
    }

    interface OnButtonClickListener {
        fun onButtonClickListener(typeButton: Enums.ClickEvents, id: Int = 0)
    }
}