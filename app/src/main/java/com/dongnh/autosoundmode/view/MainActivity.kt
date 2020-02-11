package com.dongnh.autosoundmode.view

import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.AutoCompleteTextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.dongnh.autosoundmode.R
import com.dongnh.autosoundmode.databinding.ActivityMainBinding
import com.dongnh.autosoundmode.viewmodel.MainViewModel
import org.koin.android.ext.android.get

class MainActivity : AppCompatActivity() {

    private var viewModelMain: MainViewModel = get()
    private lateinit var dataBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Init databinding and viewmodel
        this@MainActivity.dataBinding = DataBindingUtil.setContentView(this@MainActivity, R.layout.activity_main)
        this@MainActivity.dataBinding.viewModel = this@MainActivity.viewModelMain
        this@MainActivity.dataBinding.lifecycleOwner = this@MainActivity

        // Hide keyboard
        setupUIHideKeyBoard(this@MainActivity.dataBinding.root, this@MainActivity)

        // Status color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = ContextCompat.getColor(this@MainActivity, R.color.colorPrimary)
        }
    }

    /**
     * Setup hide keyboard
     */
    private fun setupUIHideKeyBoard(view: View, activity: Activity) {
        // Set up touch listener for non-text box views to hide keyboard.
        if (view !is AutoCompleteTextView) {
            view.setOnTouchListener { _, _ ->
                hideSoftKeyboard(activity)
                false
            }
        }

        //If a layout container, iterate over children and seed recursion.
        if (view is ViewGroup) {
            for (i in 0 until view.childCount) {
                val innerView = view.getChildAt(i)
                setupUIHideKeyBoard(innerView, activity)
            }
        }
    }

    // Hide keyboard
    private fun hideSoftKeyboard(activity: Activity) {
        try {
            val inputMethodManager = activity.getSystemService(
                Activity.INPUT_METHOD_SERVICE
            ) as InputMethodManager
            if (activity.currentFocus != null) {
                inputMethodManager.hideSoftInputFromWindow(
                    activity.currentFocus!!.windowToken, 0
                )
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
