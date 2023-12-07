package com.varitas.gokulpos.tablet.fragmentDialogs

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import com.varitas.gokulpos.tablet.R
import com.varitas.gokulpos.tablet.activity.OrderActivity
import com.varitas.gokulpos.tablet.activity.ReceiptActivity
import com.varitas.gokulpos.tablet.databinding.FragmentDReceiptBinding
import com.varitas.gokulpos.tablet.utilities.Default
import com.varitas.gokulpos.tablet.utilities.Enums


class ReceiptPopupDialog : BaseDialogFragment() {

    private var mActivity: Activity? = null
    private var binding: FragmentDReceiptBinding? = null
    private var onButtonClickListener: OnButtonClickListener? = null
    private var printWeb: WebView? = null
    private var orderId: Int = 0
    private var isFromHold: Boolean = false
    private var isFromVoid: Boolean = false
    private var isFromOrder: Boolean = false

    companion object {
        fun newInstance(): ReceiptPopupDialog {
            val f = ReceiptPopupDialog()
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
        dialog?.window?.setLayout(WindowManager.LayoutParams.WRAP_CONTENT, resources.getInteger(R.integer.receiptHeight))
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?,
                             ): View {
        binding = FragmentDReceiptBinding.inflate(LayoutInflater.from(context))
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (arguments != null) {
            orderId = arguments?.getInt(Default.ID)!!
            isFromHold = arguments?.getBoolean(Default.IS_HOLD)!!
            isFromVoid = arguments?.getBoolean(Default.IS_VOID)!!
            isFromOrder = arguments?.getBoolean(Default.ORDER)!!
        }
        loadData()
        manageClicks()
    }

    //region To load data
    private fun loadData() {

        val url = when {
            isFromHold -> Default.PRINT_URL + "HoldReceipt?orderid=${orderId}"
            isFromVoid -> Default.PRINT_URL + "VoidReceipt?orderid=${orderId}"
            else -> Default.PRINT_URL + "CustomerReceipt?orderid=${orderId}"
        }

        binding!!.apply {
            webViewMain.settings.javaScriptEnabled = true
            webViewMain.webChromeClient = WebChromeClient()
            webViewMain.webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: WebView, url: String) {
                    super.onPageFinished(view, url)
                    printWeb = binding!!.webViewMain
                }
            }
            webViewMain.loadUrl(url)
            Log.e("RECEIPT URL", url)
        }
    }
    //endregion

    //region To manage clicks
    private fun manageClicks() {
        binding!!.buttonPrint.setOnClickListener {
            if (isFromOrder) OrderActivity.Instance.managePrintData(printWeb)
            else ReceiptActivity.Instance.managePrintData(printWeb)
        }
    }
    //endregion

    fun setListener(listener: OnButtonClickListener) {
        this.onButtonClickListener = listener
    }

    interface OnButtonClickListener {
        fun onButtonClickListener(typeButton: Enums.ClickEvents)
    }
}