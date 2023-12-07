package com.varitas.gokulpos.tablet.activity

import android.app.Activity
import android.app.ActivityManager
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.print.PrintAttributes
import android.print.PrintJob
import android.print.PrintManager
import android.text.TextUtils
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.webkit.WebView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.varitas.gokulpos.tablet.R
import com.varitas.gokulpos.tablet.databinding.ActivityAddProductBinding
import com.varitas.gokulpos.tablet.databinding.ProgressBinding
import com.varitas.gokulpos.tablet.fragmentDialogs.AlertPopupDialog
import com.varitas.gokulpos.tablet.fragmentDialogs.ItemCountPopupDialog
import com.varitas.gokulpos.tablet.fragmentDialogs.ItemFacilityPopupDialog
import com.varitas.gokulpos.tablet.fragmentDialogs.ReceiptPopupDialog
import com.varitas.gokulpos.tablet.response.Facility
import com.varitas.gokulpos.tablet.utilities.Constants
import com.varitas.gokulpos.tablet.utilities.Default
import com.varitas.gokulpos.tablet.utilities.Enums
import com.varitas.gokulpos.tablet.utilities.Preference
import com.varitas.gokulpos.tablet.utilities.Utils
import com.varitas.gokulpos.tablet.viewmodel.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import kotlin.system.exitProcess


@AndroidEntryPoint open class BaseActivity : AppCompatActivity() {

    private var progressDialog: Dialog? = null
    private lateinit var binding: ProgressBinding
    private val viewModelAuth: LoginViewModel by viewModels()
    private var printJob: PrintJob? = null
    private var printBtnPressed = false


    companion object {
        private var instance: BaseActivity? = null

        fun getInstance(): BaseActivity? {
            if (instance == null) {
                synchronized(BaseActivity::class.java) {
                    if (instance == null) instance = BaseActivity()
                }
            }
        return instance
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MainApp.mainApp?.setContext(this)
        initUI()
    }

    private fun initUI() {
        initProgressDialog()
    }

    private fun initProgressDialog() {
        try {
            binding = ProgressBinding.inflate(layoutInflater)
            progressDialog = Dialog(this, R.style.CustomProgressBarTheme)
            progressDialog!!.setContentView(binding.root)
            Glide.with(this).asGif().load(R.raw.progress_load).into(binding.gifImageView)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    //region To open activity
    fun openActivity(intent: Intent) {
        try {
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            intent.flags = Intent.FLAG_ACTIVITY_NO_ANIMATION
            startActivity(intent)
            killProcessesAround(this@BaseActivity)
            finish()
        } catch (ex: Exception) {
            Utils.printAndWriteException(ex)
        }
    }

    @Throws(PackageManager.NameNotFoundException::class) private fun killProcessesAround(activity: Activity) {
        try {
            val am = activity.getSystemService(ACTIVITY_SERVICE) as ActivityManager
            val myProcessPrefix = activity.applicationInfo.processName
            val myProcessName = activity.packageManager.getActivityInfo(activity.componentName, 0).processName
            if (am != null) {
                for (process in am.runningAppProcesses) {
                    if (process.processName.startsWith(myProcessPrefix) && process.processName != myProcessName) android.os.Process.killProcess(process.pid)
                }
            }
        } catch (ex: Exception) {
            Utils.printAndWriteException(ex)
        }
    } //endregion

    //region To prevent from double clicks
    open fun View.clickWithDebounce(debounceTime: Long = Constants.clickDelay, action: () -> Unit) {
        this.setOnClickListener {
            it?.apply { isEnabled = false; postDelayed({ isEnabled = true }, debounceTime) }
            action()
        }
    } //endregion


    override fun onDestroy() {
        try {
            System.runFinalization()
            Runtime.getRuntime().gc()
            System.gc()
        } catch (ex: Exception) {
            Utils.printAndWriteException(ex)
        }
        super.onDestroy()
    }

    //region To show sweet alert dialog
    fun showSweetDialog(activity: Activity, msg: String) {
        try {
            if (!TextUtils.isEmpty(msg)) {
                hideKeyBoard(activity)
                val ft = supportFragmentManager.beginTransaction()
                val dialogFragment = AlertPopupDialog(msg)
                val prevFragment: Fragment? = supportFragmentManager.findFragmentByTag(AlertPopupDialog::class.java.name)
                if (prevFragment != null) return
                dialogFragment.show(supportFragmentManager, "alert_dialog")
                dialogFragment.isCancelable = false
                dialogFragment.show(ft, AlertPopupDialog::class.java.name)
            }
        } catch (e: Exception) {
            Utils.printAndWriteException(e)
        }
    } //endregion

    //region To hide keyboard
    fun hideKeyBoard(activity: Activity) {
        val view = activity.currentFocus
        if (view != null) {
            val inputManager = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputManager.hideSoftInputFromWindow(view.windowToken, 0)
        }
    } //endregion

    //region To manage progressbar
    fun manageProgress(showProgress: Boolean) {
        CoroutineScope(Dispatchers.Main).launch {
            if (showProgress) showProgressDialog()
            else dismissProgressDialog()
        }
    }

    private fun showProgressDialog() {
        try {
            if (progressDialog == null) initProgressDialog()
            if (progressDialog != null && !progressDialog!!.isShowing) {
                binding.gifImageView.visibility = View.VISIBLE
                if (!this.isFinishing && !this.isDestroyed) progressDialog?.show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun dismissProgressDialog() {
        try {
            if (progressDialog != null && progressDialog!!.isShowing) {
                binding.gifImageView.visibility = View.GONE
                progressDialog!!.dismiss()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    } //endregion

    //region Move to Product Screen
    fun moveToSubMenu(id : Int, isFromOrder : Boolean = false) {
        val intent = Intent(this, MenuActivity::class.java)
        intent.putExtra(Default.PARENT_ID, id)
        intent.putExtra(Default.ORDER, isFromOrder)
        openActivity(intent)
    }
    //endregion

    // region Move to Order Screen
    fun moveToOrder(id : Int) {
        val intent = Intent(this, OrderActivity::class.java)
        intent.putExtra(Default.PARENT_ID, id)
        openActivity(intent)
    }
    //endregion

    //region Tobacco & Alcohol date
    fun tobaccoDate(dateFormat : SimpleDateFormat) : String {
        return try {
            val cal = Calendar.getInstance()
            cal.add(Calendar.YEAR, -21)
            dateFormat.format(cal.time)
        } catch(ex : Exception) {
            ""
        }
    }
    //endregion

    //region To make logout from an application
    fun logOut() {
        viewModelAuth.doLogout().observe(this) { result ->
            when (result!!.status) {
                Default.SUCCESS_API -> {
                    CoroutineScope(Dispatchers.Main).launch {
                        Preference.getPref(this@BaseActivity).edit().clear().apply()
                        delay(1000)
                        finishAffinity()
                        exitProcess(0)
                    }
                }
            }
        }
    }
    //endregion

    fun calculateTextColorForBackground(backgroundColor : Int) : Int {
        val darkness =
            1 - (0.299 * Color.red(backgroundColor) +
                    0.587 * Color.green(backgroundColor) +
                    0.114 * Color.blue(backgroundColor)) / 255
        return if(darkness < 0.5) Color.BLACK else Color.WHITE
    }

    //region To view stocks
    fun viewStocks(facilities: ArrayList<Facility>, isEdit: Boolean = false) {
        val ft = supportFragmentManager.beginTransaction()
        val dialogFragment = ItemFacilityPopupDialog.newInstance()
        val prevFragment : Fragment? = supportFragmentManager.findFragmentByTag(ItemFacilityPopupDialog::class.java.name)
        if(prevFragment != null) return
        val bundle = Bundle()
        bundle.putSerializable(Default.FACILITY, facilities)
        bundle.putBoolean(Default.EDIT, isEdit)
        dialogFragment.arguments = bundle
        dialogFragment.show(ft, ItemFacilityPopupDialog::class.java.name)
        dialogFragment.setListener(object : ItemFacilityPopupDialog.OnButtonClickListener {

            override fun onButtonClickListener(typeButton : Enums.ClickEvents) {
                if(dialogFragment.isVisible) dialogFragment.dismiss()
            }
        })
    }
    //endregion

    //region To manage item's price
    fun manageItemPrice(binding: ActivityAddProductBinding) {
        binding.apply {
            layoutBasic.apply {
                textInputUnitPrice.setText(Utils.getTwoDecimalValue(0.00))
                textInputUnitCost.setText(Utils.getTwoDecimalValue(0.00))
                textInputMinPrice.setText(Utils.getTwoDecimalValue(0.00))
                textInputBuyDown.setText(Utils.getTwoDecimalValue(0.00))
                textInputMSRP.setText(Utils.getTwoDecimalValue(0.00))
                textInputSalePrice.setText(Utils.getTwoDecimalValue(0.00))
                textInputMargin.setText(Utils.getTwoDecimalValue(0.00))
                textInputMarkup.setText(Utils.getTwoDecimalValue(0.00))
            }
        }
    }

    fun manageDisableViews(binding: ActivityAddProductBinding, isEnable: Boolean) {
        binding.apply {
            layoutBasic.apply {
                textInputUnitPrice.isEnabled = isEnable
                textInputUnitCost.isEnabled = isEnable
                textInputMinPrice.isEnabled = isEnable
                textInputBuyDown.isEnabled = isEnable
                textInputMSRP.isEnabled = isEnable
                textInputSalePrice.isEnabled = isEnable
                checkBoxBuyAsCase.isEnabled = isEnable
                textInputUnitInCase.isEnabled = isEnable
                textInputCaseCost.isEnabled = isEnable
                textInputCasePrice.isEnabled = isEnable
                textInputReOrder.isEnabled = isEnable
                textInputMinWarnQty.isEnabled = isEnable
                buttonAdd.isEnabled = isEnable
                buttonAddSoldAlong.isEnabled = isEnable
                checkBoxNonDiscount.isEnabled = isEnable
                checkBoxWeightItem.isEnabled = isEnable
                checkBoxWICCheck.isEnabled = isEnable
                checkBoxNonRevenue.isEnabled = isEnable
                checkBoxDepositItem.isEnabled = isEnable
                checkBoxWebItem.isEnabled = isEnable
                checkBoxCountWithNoDisc.isEnabled = isEnable
                checkBoxNonCountable.isEnabled = isEnable
                checkBoxFoodStamp.isEnabled = isEnable
                checkBoxReturnItem.isEnabled = isEnable
                checkBoxNonPlu.isEnabled = isEnable
                buttonPrice.isEnabled = isEnable
                buttonPriceView.isEnabled = isEnable
                buttonEnter.isEnabled = isEnable
                buttonStockView.isEnabled = isEnable
            }
        }
    }
    //endregion

    fun manageItemCount(id : Int, menu : Enums.Menus) {
        val ft = supportFragmentManager.beginTransaction()
        val dialogFragment = ItemCountPopupDialog.newInstance()
        val prevFragment : Fragment? = supportFragmentManager.findFragmentByTag(ItemCountPopupDialog::class.java.name)
        if(prevFragment != null) return
        val bundle = Bundle()
        bundle.putSerializable(Default.ITEMCOUNT, menu)
        bundle.putInt(Default.ID, id)
        dialogFragment.arguments = bundle
        dialogFragment.show(ft, ItemCountPopupDialog::class.java.name)
        dialogFragment.setListener(object : ItemCountPopupDialog.OnButtonClickListener {
            override fun onButtonClickListener(typeButton : Enums.ClickEvents) {
                if(dialogFragment.isVisible) dialogFragment.dismiss()
            }
        })
    }

    //region To manage receipt
    fun manageReceipt(orderId: Int, isFromHold: Boolean = false, isFromVoid: Boolean = false, isFromOrder: Boolean = true) {
        val ft = supportFragmentManager.beginTransaction()
        val dialogFragment = ReceiptPopupDialog.newInstance()
        val prevFragment: Fragment? = supportFragmentManager.findFragmentByTag(ReceiptPopupDialog::class.java.name)
        if (prevFragment != null) return
        val bundle = Bundle()
        bundle.putInt(Default.ID, orderId)
        bundle.putBoolean(Default.IS_HOLD, isFromHold)
        bundle.putBoolean(Default.IS_VOID, isFromVoid)
        bundle.putBoolean(Default.ORDER, isFromOrder)
        dialogFragment.arguments = bundle
        dialogFragment.isCancelable = true
        dialogFragment.show(ft, ReceiptPopupDialog::class.java.name)
        dialogFragment.setListener(object : ReceiptPopupDialog.OnButtonClickListener {
            override fun onButtonClickListener(typeButton: Enums.ClickEvents) {
                if (dialogFragment.isVisible) dialogFragment.dismiss()
            }
        })
    }
    //endregion

    fun managePrintData(printWeb: WebView?, isForLandScape: Boolean = false) {
        try {
            if (printWeb != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    // Calling createWebPrintJob()
                    printTheWebPage(printWeb, isForLandScape)
                } else {
                    // Showing Toast message to user
                    Toast.makeText(this, "Not available for device below Android LOLLIPOP", Toast.LENGTH_SHORT).show()
                }
            } else {
                // Showing Toast message to user
                Toast.makeText(this, "WebPage not fully loaded", Toast.LENGTH_SHORT).show()
            }
        } catch (ex: Exception) {
            Utils.printAndWriteException(ex)
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP) private fun printTheWebPage(webView: WebView, isForLandScape: Boolean = false) {

        // set printBtnPressed true
        printBtnPressed = true

        // Creating  PrintManager instance
        val printManager = getSystemService(Context.PRINT_SERVICE) as PrintManager?

        // setting the name of job
        val jobName = getString(R.string.app_name) + " webpage" + System.currentTimeMillis()

        // Creating  PrintDocumentAdapter instance
        val printAdapter = webView.createPrintDocumentAdapter(jobName)
        assert(printManager != null)

        val printAttributes = PrintAttributes.Builder()
        if (isForLandScape) printAttributes.setMediaSize(PrintAttributes.MediaSize.UNKNOWN_LANDSCAPE) // Landscape orientation
        printJob = printManager!!.print(jobName, printAdapter, printAttributes.build())
    }

    override fun onResume() {
        super.onResume()
        if (printJob != null && printBtnPressed) {
            when {
                printJob!!.isCompleted -> Toast.makeText(this, "Completed", Toast.LENGTH_SHORT).show()
                printJob!!.isStarted -> Toast.makeText(this, "isStarted", Toast.LENGTH_SHORT).show()
                printJob!!.isBlocked -> Toast.makeText(this, "isBlocked", Toast.LENGTH_SHORT).show()
                printJob!!.isFailed -> Toast.makeText(this, "isFailed", Toast.LENGTH_SHORT).show()
                printJob!!.isCancelled -> Toast.makeText(this, "isCancelled", Toast.LENGTH_SHORT).show()
                printJob!!.isQueued -> Toast.makeText(this, "isQueued", Toast.LENGTH_SHORT).show()
            }
            // set printBtnPressed false
            printBtnPressed = false
        }
    }
}