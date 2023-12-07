package com.varitas.gokulpos.activity

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.print.PrintAttributes
import android.print.PrintJob
import android.print.PrintManager
import android.util.Log
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.varitas.gokulpos.R
import com.varitas.gokulpos.databinding.ActivityOrderdetailsBinding
import com.varitas.gokulpos.utilities.Default
import com.varitas.gokulpos.utilities.Utils
import dagger.hilt.android.AndroidEntryPoint
import java.io.Serializable

@AndroidEntryPoint class OrderDetailsActivity : BaseActivity() {

    private lateinit var binding : ActivityOrderdetailsBinding
    private var printWeb : WebView? = null
    private var orderId = 0
    private var id : Int = 0
    private var user : Serializable? = null
    private var isFromHold : Boolean = false
    private var isFromVoid : Boolean = false

    // object of print job
    private var printJob : PrintJob? = null

    // a boolean to check the status of printing
    private var printBtnPressed = false

    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderdetailsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        initData()
        postInitViews()
        loadData()
        manageClicks()
    }

    private fun postInitViews() {

    }

    private fun initData() {
        binding.apply {
            layoutToolbar.textViewToolbarName.text = resources.getString(R.string.lbl_OrderDetails)
            layoutToolbar.imageViewAction.setImageDrawable(ContextCompat.getDrawable(this@OrderDetailsActivity, R.drawable.ic_home))
            layoutToolbar.imageViewBack.visibility = View.VISIBLE
        }
        orderId = intent.extras?.getInt(Default.ORDER_ID)!!
        id = intent.extras?.getInt(Default.ID)!!
        user = intent.extras?.getSerializable(Default.USER)
        isFromHold = intent.extras?.getBoolean(Default.IS_HOLD)!!
        isFromVoid = intent.extras?.getBoolean(Default.IS_VOID)!!
    }

    //region To manage click events
    private fun manageClicks() {
        binding.apply {
            layoutToolbar.imageViewAction.clickWithDebounce {
                openActivity(Intent(this@OrderDetailsActivity, DashboardActivity::class.java))
            }
            layoutToolbar.imageViewBack.clickWithDebounce {
                manageBackPress()
            }

            buttonPrint.setOnClickListener {
                try {
                    if(printWeb != null) {
                        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            // Calling createWebPrintJob()
                            printTheWebPage(printWeb!!)
                        } else {
                            // Showing Toast message to user
                            Toast.makeText(this@OrderDetailsActivity, "Not available for device below Android LOLLIPOP", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        // Showing Toast message to user
                        Toast.makeText(this@OrderDetailsActivity, "WebPage not fully loaded", Toast.LENGTH_SHORT).show()
                    }
                } catch(ex : Exception) {
                    Utils.printAndWriteException(ex)
                }
            }
        }
    } //endregion

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP) private fun printTheWebPage(webView : WebView) {

        // set printBtnPressed true
        printBtnPressed = true

        // Creating  PrintManager instance
        val printManager = this.getSystemService(Context.PRINT_SERVICE) as PrintManager?

        // setting the name of job
        val jobName = getString(R.string.app_name) + " webpage" + webView.url

        // Creating  PrintDocumentAdapter instance
        val printAdapter = webView.createPrintDocumentAdapter(jobName)
        assert(printManager != null)
        printJob = printManager!!.print(
            jobName, printAdapter,
            PrintAttributes.Builder().build()
        )
    }

    //region To load data
    private fun loadData() {
        val url = when {
            isFromHold -> Default.PRINT_URL + "HoldReceipt?orderid=${orderId}"
            isFromVoid -> Default.PRINT_URL + "VoidReceipt?orderid=${orderId}"
            else -> Default.PRINT_URL + "CustomerReceipt?orderid=${orderId}"
        }
        binding.apply {
            webViewMain.settings.javaScriptEnabled = true
            webViewMain.webChromeClient = WebChromeClient()
            webViewMain.webViewClient = object : WebViewClient() {
                override fun onPageFinished(view : WebView, url : String) {
                    super.onPageFinished(view, url)
                    printWeb = binding.webViewMain
                }
            }
            webViewMain.loadUrl(url)
            Log.e("RECEIPT URL", url)
        }
    } //endregion

    override fun onResume() {
        super.onResume()
        if(printJob != null && printBtnPressed) {
            if(printJob!!.isCompleted) {
                // Showing Toast Message
                Toast.makeText(this@OrderDetailsActivity, "Completed", Toast.LENGTH_SHORT).show()
            } else if(printJob!!.isStarted) {
                // Showing Toast Message
                Toast.makeText(this@OrderDetailsActivity, "isStarted", Toast.LENGTH_SHORT).show()

            } else if(printJob!!.isBlocked) {
                // Showing Toast Message
                Toast.makeText(this@OrderDetailsActivity, "isBlocked", Toast.LENGTH_SHORT).show()

            } else if(printJob!!.isCancelled) {
                // Showing Toast Message
                Toast.makeText(this@OrderDetailsActivity, "isCancelled", Toast.LENGTH_SHORT).show()
            } else if(printJob!!.isFailed) {
                // Showing Toast Message
                Toast.makeText(this@OrderDetailsActivity, "isFailed", Toast.LENGTH_SHORT).show()

            } else if(printJob!!.isQueued) {
                // Showing Toast Message
                Toast.makeText(this@OrderDetailsActivity, "isQueued", Toast.LENGTH_SHORT).show()
            }
            // set printBtnPressed false
            printBtnPressed = false
        }
    }

    @Deprecated("Deprecated in Java") override fun onBackPressed() {
        manageBackPress()
        onBackPressedDispatcher.onBackPressed()
    }

    private fun manageBackPress() {
        val intent = Intent(this@OrderDetailsActivity, OrderListActivity::class.java)
        intent.putExtra(Default.USER, user)
        intent.putExtra(Default.ID, id)
        openActivity(intent)
    }

}