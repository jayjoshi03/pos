package com.varitas.gokulpos.activity

import android.content.Intent
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
import com.varitas.gokulpos.R
import com.varitas.gokulpos.databinding.ActivityReportsPdfBinding
import com.varitas.gokulpos.utilities.Constants
import com.varitas.gokulpos.utilities.Default
import com.varitas.gokulpos.utilities.Utils
import com.varitas.gokulpos.viewmodel.ReportsViewModel
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
import java.util.Calendar


@AndroidEntryPoint class ReportDetailsActivity : BaseActivity() {
    private lateinit var binding : ActivityReportsPdfBinding
    private lateinit var materialDateBuilder : MaterialDatePicker.Builder<Pair<Long, Long>>
    private lateinit var materialDatePicker : MaterialDatePicker<Pair<Long, Long>>
    private val viewModel : ReportsViewModel by viewModels()
    private var startDate : String = ""
    private var endDate : String = ""
    private var parentId = 0
    private var id = 0
    private var printWeb : WebView? = null
    private var url : String = ""

    companion object {
        lateinit var Instance : ReportDetailsActivity
    }

    init {
        Instance = this
    }

    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReportsPdfBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setSupportActionBar(binding.layoutToolbar.toolbarCommon)
        initData()
        postInitView()
        manageClicks()
    }

    //region To init data
    private fun initData() {
        parentId = if(intent.extras?.getInt(Default.PARENT_ID) != null) intent.extras?.getInt(Default.PARENT_ID)!! else 0
        id = if(intent.extras?.getInt(Default.ID) != null) intent.extras?.getInt(Default.ID)!! else 0
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
            layoutToolbar.imageViewAction.setImageDrawable(ContextCompat.getDrawable(this@ReportDetailsActivity, R.drawable.ic_home))
            layoutToolbar.imageViewBack.visibility = View.VISIBLE
        }

        startDate = Constants.dateFormat_yyyy_MM_dd.format(now())
        endDate = Constants.dateFormat_yyyy_MM_dd.format(1.toDay().sinceNow)

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

    private fun postInitView() {
        binding.apply {
            textViewDateRange.text = Constants.dateFormat_MMM_dd_yyyy.format(now())
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

        when(id) {
            Default.MANAGE_REPORT_SALE_EXCEL -> {
                binding.buttonPrint.visibility = View.INVISIBLE
            }
        }
    } //endregion

    //region To manage click events
    private fun manageClicks() {
        binding.apply {
            layoutToolbar.imageViewAction.clickWithDebounce {
                openActivity(Intent(this@ReportDetailsActivity, DashboardActivity::class.java))
            }
            layoutToolbar.imageViewBack.clickWithDebounce {
                moveToProducts(parentId)
            }

            buttonPrint.clickWithDebounce {
                managePrintData(printWeb, true)
            }

            //Material Date Range Picker
            imageViewDateFilter.clickWithDebounce {
                try {
                    materialDatePicker.show(supportFragmentManager, "MATERIAL_DATE_PICKER")
                } catch(ex : Exception) {
                    Utils.printAndWriteException(ex)
                }
            }
        }
        materialDatePicker.addOnPositiveButtonClickListener {
            try {
                binding.textViewDateRange.text = materialDatePicker.headerText
                if(it.first != null && it.second != null) { // start date
                    val start : Calendar = Calendar.getInstance()
                    start.timeInMillis = it.first // end date
                    startDate = Constants.dateFormat_yyyy_MM_dd.format(it.first)
                    val end : Calendar = Calendar.getInstance()
                    end.timeInMillis = it.second
                    endDate = Constants.dateFormat_yyyy_MM_dd.format(it.second)
                    while(start.before(end)) {
                        start.add(Calendar.DAY_OF_MONTH, 1)
                    }
                    CoroutineScope(Dispatchers.Main).launch {
                        loadData()
                    }
                }
            } catch(ex : Exception) {
                Utils.printAndWriteException(ex)
            }
        }

    } //endregion

    @Deprecated("Deprecated in Java") override fun onBackPressed() { //openActivity(Intent(this, ReportsActivity::class.java))
        moveToProducts(parentId)
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
    } //endregion

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