package com.varitas.gokulpos.activity

import android.app.Activity
import android.app.ActivityManager
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.print.PrintAttributes
import android.print.PrintJob
import android.print.PrintManager
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.webkit.WebView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import cn.pedant.SweetAlert.SweetAlertDialog
import com.bumptech.glide.Glide
import com.varitas.gokulpos.R
import com.varitas.gokulpos.databinding.ProgressBinding
import com.varitas.gokulpos.fragmentDialogs.ItemCountPopupDialog
import com.varitas.gokulpos.utilities.Constants
import com.varitas.gokulpos.utilities.Default
import com.varitas.gokulpos.utilities.Enums
import com.varitas.gokulpos.utilities.Utils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@AndroidEntryPoint open class BaseActivity : AppCompatActivity() {

    private var instance : BaseActivity? = null
    private var progressDialog : Dialog? = null
    private lateinit var binding : ProgressBinding
    private var printJob : PrintJob? = null
    private var printBtnPressed = false

    fun getInstance() : BaseActivity? {
        if(instance == null) {
            synchronized(BaseActivity::class.java) {
                if(instance == null) instance = BaseActivity()
            }
        }
        return instance
    }

    override fun onCreate(savedInstanceState : Bundle?) {
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
        } catch(e : Exception) {
            e.printStackTrace()
        }
    }

    //region To open activity
    fun openActivity(intent : Intent) {
        try {
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            intent.flags = Intent.FLAG_ACTIVITY_NO_ANIMATION
            startActivity(intent)
            killProcessesAround(this@BaseActivity)
            finish()
        } catch(ex : Exception) {
            Utils.printAndWriteException(ex)
        }
    }

    @Throws(PackageManager.NameNotFoundException::class) private fun killProcessesAround(activity : Activity) {
        try {
            val am = activity.getSystemService(ACTIVITY_SERVICE) as ActivityManager
            val myProcessPrefix = activity.applicationInfo.processName
            val myProcessName = activity.packageManager.getActivityInfo(activity.componentName, 0).processName
            if(am != null) {
                for(process in am.runningAppProcesses) {
                    if(process.processName.startsWith(myProcessPrefix) && process.processName != myProcessName) android.os.Process.killProcess(process.pid)
                }
            }
        } catch(ex : Exception) {
            Utils.printAndWriteException(ex)
        }
    } //endregion

    //region To prevent from double clicks
    open fun View.clickWithDebounce(debounceTime : Long = Constants.clickDelay, action : () -> Unit) {
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
        } catch(ex : Exception) {
            Utils.printAndWriteException(ex)
        }
        super.onDestroy()
    }

    //region To show sweet alert dialog
    fun showSweetDialog(activity : Activity, msg : String) {
        try {
            hideKeyBoard(activity)
            val sweetAlertDialog = SweetAlertDialog(activity, SweetAlertDialog.CUSTOM_IMAGE_TYPE)
            sweetAlertDialog.apply {
                setCanceledOnTouchOutside(false)
                setCancelable(false)
                contentText = msg
                confirmText = resources.getString(R.string.lbl_Okay)
                confirmButtonBackgroundColor = ContextCompat.getColor(this@BaseActivity, R.color.base_color)
                cancelButtonBackgroundColor = ContextCompat.getColor(this@BaseActivity, R.color.pink)
                setConfirmClickListener { sDialog->
                    sDialog.dismissWithAnimation()
                }
                show()
            }
        } catch(e : Exception) {
            Utils.printAndWriteException(e)
        }
    } //endregion

    //region To hide keyboard
    fun hideKeyBoard(activity : Activity) {
        val view = activity.currentFocus
        if(view != null) {
            val inputManager = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputManager.hideSoftInputFromWindow(view.windowToken, 0)
        }
    } //endregion

    //region To manage progressbar
    fun manageProgress(showProgress : Boolean) {
        CoroutineScope(Dispatchers.Main).launch {
            if(showProgress) showProgressDialog()
            else dismissProgressDialog()
        }
    }

    private fun showProgressDialog() {
        try {
            if(progressDialog == null) initProgressDialog()
            if(progressDialog != null && !progressDialog!!.isShowing) {
                binding.gifImageView.visibility = View.VISIBLE
                if(!this.isFinishing && !this.isDestroyed) progressDialog?.show()
            }
        } catch(e : Exception) {
            e.printStackTrace()
        }
    }

    private fun dismissProgressDialog() {
        try {
            if(progressDialog != null && progressDialog!!.isShowing) {
                binding.gifImageView.visibility = View.GONE
                progressDialog!!.dismiss()
            }
        } catch(e : Exception) {
            e.printStackTrace()
        }
    } //endregion

    //region Move to Product Screen
    fun moveToProducts(id : Int) {
        val intent = Intent(this, MenuActivity::class.java)
        intent.putExtra(Default.PARENT_ID, id)
        openActivity(intent)
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

    fun managePrintData(printWeb : WebView?, isForLandScape : Boolean = false) {
        try {
            if(printWeb != null) {
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
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
        } catch(ex : Exception) {
            Utils.printAndWriteException(ex)
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP) private fun printTheWebPage(webView : WebView, isForLandScape : Boolean = false) {

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
        if(isForLandScape) printAttributes.setMediaSize(PrintAttributes.MediaSize.UNKNOWN_LANDSCAPE) // Landscape orientation
        printJob = printManager!!.print(jobName, printAdapter, printAttributes.build())
    }

    override fun onResume() {
        super.onResume()
        if(printJob != null && printBtnPressed) {
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