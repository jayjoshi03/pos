package com.varitas.gokulpos.tablet.activity

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.util.Pair
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import com.varitas.gokulpos.tablet.R
import com.varitas.gokulpos.tablet.databinding.ActivityReportsBinding
import com.varitas.gokulpos.tablet.utilities.Constants
import com.varitas.gokulpos.tablet.utilities.Default
import com.varitas.gokulpos.tablet.utilities.Utils
import com.varitas.gokulpos.tablet.viewmodel.OrdersViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.moallemi.tools.extension.date.now
import me.moallemi.tools.extension.date.toDay
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.File
import java.io.IOException


@AndroidEntryPoint class ReportActivity : BaseActivity() {

    private lateinit var binding : ActivityReportsBinding
    private var parentId = 0
    private var id = 0
    private var isFromOrder = false
    private var printWeb : WebView? = null
    private val viewModel : OrdersViewModel by viewModels()
    private var startDate : String = ""
    private var displayStartDate : String = ""
    private var displayEndDate : String = ""
    private var endDate : String = ""
    private var url : String = ""
    private lateinit var materialDateBuilder : MaterialDatePicker.Builder<Pair<Long, Long>>
    private lateinit var materialDatePicker : MaterialDatePicker<Pair<Long, Long>>

    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReportsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setSupportActionBar(binding.layoutToolbar.toolbarCommon)
        initData()
        //loadData()
        postInitViews()
        manageClicks()
    }

    //region To init data
    private fun initData() {
        parentId = if(intent.extras?.getInt(Default.PARENT_ID) != null) intent.extras?.getInt(Default.PARENT_ID)!! else 0
        id = if(intent.extras?.getInt(Default.ID) != null) intent.extras?.getInt(Default.ID)!! else 0
        isFromOrder = if(intent.extras?.getBoolean(Default.ORDER) != null) intent.extras?.getBoolean(Default.ORDER)!! else false
        startDate = Constants.dateFormat_MM_dd_yyyy.format(10.toDay().ago)
        endDate = Constants.dateFormat_MM_dd_yyyy.format(now())
        displayStartDate = Constants.dateFormat_MMM_dd_yyyy.format(10.toDay().ago)
        displayEndDate = Constants.dateFormat_MMM_dd_yyyy.format(now())
        binding.apply {
            val name = when(id) {
                Default.MANAGE_REPORT_DEPARTMENT -> resources.getString(R.string.lbl_DepartmentSummary)
                Default.MANAGE_REPORT_SALE -> resources.getString(R.string.lbl_SaleSummary)
                Default.MANAGE_REPORT_OPERATION_RECORD -> resources.getString(R.string.lbl_OperationLogSummary)
                Default.MANAGE_REPORT_CASH_SALE -> resources.getString(R.string.lbl_CashSalesSummary)
                Default.MANAGE_REPORT_SALE_EXCEL -> resources.getString(R.string.lbl_SalesSummaryExcel)
                else -> ""
            }
            layoutToolbar.textViewToolbarName.text = name
            layoutToolbar.imageViewAction.setImageDrawable(ContextCompat.getDrawable(this@ReportActivity, R.drawable.ic_home))
            layoutToolbar.imageViewBack.visibility = View.VISIBLE
        }
        materialDateBuilder = MaterialDatePicker.Builder.dateRangePicker()
        try {
            val constraintsBuilder = CalendarConstraints.Builder()
            materialDateBuilder.setCalendarConstraints(constraintsBuilder.build())
        } catch(ex : Exception) {
            Utils.printAndWriteException(ex)
        }
        materialDateBuilder.setSelection(Pair(now().time, 1.toDay().sinceNow.time))
        materialDatePicker = materialDateBuilder.build()
    }

    private fun postInitViews() {
        binding.apply {
            textViewDateRange.text = resources.getString(R.string.lbl_Date) + ": $displayStartDate - $displayEndDate"
            webViewMain.settings.javaScriptEnabled = true
            webViewMain.settings.loadWithOverviewMode = true
            webViewMain.settings.useWideViewPort = true
            webViewMain.settings.builtInZoomControls = false
            webViewMain.settings.displayZoomControls = false
            webViewMain.settings.setSupportZoom(false)
            webViewMain.settings.textZoom = 100
            webViewMain.webChromeClient = WebChromeClient()
            webViewMain.webViewClient = object : WebViewClient() {
                override fun onPageFinished(view : WebView, url : String) {
                    super.onPageFinished(view, url)
                    printWeb = webViewMain
                }
            }
        }
    }

    //region To manage click events
    private fun manageClicks() {
        binding.apply {
            layoutToolbar.imageViewAction.clickWithDebounce {
                moveToOrder(parentId)
            }
            layoutToolbar.imageViewBack.clickWithDebounce {
                moveToSubMenu(parentId, isFromOrder)
            }
            buttonPrint.clickWithDebounce {
                managePrintData(printWeb, true)
            }

            buttonDate.clickWithDebounce {
                try {
                    materialDatePicker.show(supportFragmentManager, "MATERIAL_DATE_PICKER")
                } catch(ex : Exception) {
                    Utils.printAndWriteException(ex)
                }
            }

            materialDatePicker.addOnPositiveButtonClickListener { selection->
                try {
                    startDate = Constants.dateFormat_MM_dd_yyyy.format(selection.first)
                    endDate = Constants.dateFormat_MM_dd_yyyy.format(selection.second)
                    displayStartDate = Constants.dateFormat_MMM_dd_yyyy.format(selection.first)
                    displayEndDate = Constants.dateFormat_MMM_dd_yyyy.format(selection.second)
                    textViewDateRange.text = resources.getString(R.string.lbl_Date) + ": $displayStartDate - $displayEndDate"
                    CoroutineScope(Dispatchers.Main).launch {
                        loadData()
                    }
                } catch(ex : Exception) {
                    Utils.printAndWriteException(ex)
                }
            }

            // Setting up the event for when cancelled is clicked
            materialDatePicker.addOnNegativeButtonClickListener {
                Toast.makeText(this@ReportActivity, "${materialDatePicker.headerText} is cancelled", Toast.LENGTH_LONG).show()
            }

            // Setting up the event for when back button is pressed
            materialDatePicker.addOnCancelListener {
                Toast.makeText(this@ReportActivity, "Date Picker Cancelled", Toast.LENGTH_LONG).show()
            }
        }
    }
    //endregion

    @Deprecated("Deprecated in Java") override fun onBackPressed() {
        moveToSubMenu(parentId, isFromOrder)
        onBackPressedDispatcher.onBackPressed()
    }

    //region To load data
    private fun loadData() {
        if(id == Default.MANAGE_REPORT_SALE_EXCEL) {
            CoroutineScope(Dispatchers.IO).launch {
                downloadExcelSheet()
            }
        } else {
            url = when(id) {
                Default.MANAGE_REPORT_DEPARTMENT -> "${Default.REPORT_URL}DepartmentSale?start=${startDate}&&end=${endDate}&&store=${viewModel.storeId}"
                Default.MANAGE_REPORT_SALE -> "${Default.REPORT_URL}SaleSummary?start=${startDate}&&end=${endDate}&&store=${viewModel.storeId}"
                Default.MANAGE_REPORT_OPERATION_RECORD -> "${Default.REPORT_URL}OperationRecordLog?start=${startDate}&&end=${endDate}&&store=${viewModel.storeId}"
                Default.MANAGE_REPORT_CASH_SALE -> "${Default.REPORT_URL}CashSaleSummary?start=${startDate}&&end=${endDate}&&store=${viewModel.storeId}"
                else -> ""
            }

            binding.apply {
                webViewMain.loadUrl(url)
                Log.e("REPORT URL", url)
            }
        }
    }
    //endregion

    //region To download excel sheet for sale
    private suspend fun downloadExcelSheet() {
        url = "${Default.REPORT_URL}SaleSummaryExcel?start=${startDate}&&end=${endDate}&&store=${viewModel.storeId}"
        val destinationPath = "${filesDir}/${System.currentTimeMillis()}.xlsx" // Provide the desired destination path for the downloaded Excel file
        val client = OkHttpClient()
        val request = Request.Builder()
            .url(url)
            .build()

        try {
            val response : Response = withContext(Dispatchers.IO) {
                client.newCall(request).execute()
            }

            if(response.isSuccessful) {
                val inputStream = response.body?.byteStream()
                if(inputStream != null) {
                    val outputFile = File(destinationPath)
                    outputFile.parentFile?.mkdirs()
                    outputFile.outputStream().use { output->
                        inputStream.copyTo(output)
                    }
                    showToast("Excel sheet downloaded to: $destinationPath", Toast.LENGTH_LONG)
                } else showToast("Failed to get input stream from response")
            } else showToast("HTTP request failed with code: ${response.code}")
        } catch(e : IOException) {
            Utils.printAndWriteException(e)
        }
    }

    private fun showToast(message : String, duration : Int = Toast.LENGTH_LONG) {
        Handler(Looper.getMainLooper()).post {
            Toast.makeText(this, message, duration).show()
        }
    }
    //endregion

    override fun onResume() {
        super.onResume()
        CoroutineScope(Dispatchers.Main).launch {
            loadData()
        }
    }
}