package com.varitas.gokulpos.tablet.fragmentDialogs

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import com.varitas.gokulpos.tablet.R
import com.varitas.gokulpos.tablet.activity.BaseActivity
import com.varitas.gokulpos.tablet.utilities.Constants
import com.varitas.gokulpos.tablet.utilities.Utils

open class BaseDialogFragment : DialogFragment() {

    private var myActivity : AppCompatActivity? = null

    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        val style = STYLE_NO_FRAME
        val theme = R.style.DialogTheme
        setStyle(style, theme)
    }

    @Deprecated("Deprecated in Java") override fun onAttach(activity : Activity) {
        super.onAttach(activity)
        if(context is Activity) myActivity = context as BaseActivity
    }

    companion object {
        fun newInstance() : BaseDialogFragment {
            val f = BaseDialogFragment()
            val args = Bundle()
            f.arguments = args
            return f
        }
    }

    fun View.clickWithDebounce(debounceTime : Long = Constants.clickDelay, action : () -> Unit) {
        this.setOnClickListener { // if (!isBatchClose) {
            it?.apply { isEnabled = false; postDelayed({ isEnabled = true }, debounceTime) }
            action()
        }
    }

    open fun hideKeyboardInFragment(view : View) {
        try {
            val windowToken = dialog!!.window!!.decorView.rootView
            val imm = dialog!!.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(windowToken.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
        } catch(ex : Exception) {
            Utils.printAndWriteException(ex)
        }
    }
}