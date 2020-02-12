package com.dongnh.autosoundmode.view

import android.annotation.SuppressLint
import android.app.Activity
import android.app.TimePickerDialog
import android.app.TimePickerDialog.OnTimeSetListener
import android.content.Context
import android.content.DialogInterface
import android.media.AudioManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.inputmethod.InputMethodManager
import android.widget.AutoCompleteTextView
import android.widget.TimePicker
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dongnh.autosoundmode.R
import com.dongnh.autosoundmode.const.*
import com.dongnh.autosoundmode.databinding.ActivityMainBinding
import com.dongnh.autosoundmode.ultil.helper.ManagerWorkerHelper
import com.dongnh.autosoundmode.ultil.helper.SharePreferenceHelper
import com.dongnh.autosoundmode.viewmodel.MainViewModel
import org.koin.android.ext.android.get
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {

    private var viewModelMain: MainViewModel = get()
    private var sharePreferenceHelper: SharePreferenceHelper = get()
    private var workerHelper: ManagerWorkerHelper = get()
    private lateinit var dataBinding: ActivityMainBinding

    // Show dialog
    private var alertBuild: AlertDialog.Builder? = null
    private var dialog: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Init databinding and viewmodel
        this@MainActivity.dataBinding =
            DataBindingUtil.setContentView(this@MainActivity, R.layout.activity_main)
        this@MainActivity.dataBinding.lifecycleOwner = this@MainActivity
        this@MainActivity.dataBinding.viewModel = this@MainActivity.viewModelMain

        // Hide keyboard
        setupUIHideKeyBoard(this@MainActivity.dataBinding.root, this@MainActivity)

        // Status color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = ContextCompat.getColor(this@MainActivity, R.color.colorPrimary)
        }

        setUpDefaultTime()
        setUpButtonClick()
        setUpEventCLickTime()
        setUpEventClickChange()
        updateModeForView()

        this@MainActivity.dataBinding.viewHistory.layoutManager = LinearLayoutManager(this@MainActivity, RecyclerView.VERTICAL, false)
    }

    override fun onResume() {
        super.onResume()
        // Call method reload database for show
        viewModelMain.loadDataFormDbToShow()
    }

    /**
     * Setup view time for layout
     */
    private fun setUpDefaultTime() {
        var timeStart = sharePreferenceHelper.getDataString(TIME_START)
        var timeEnd = sharePreferenceHelper.getDataString(TIME_END)

        if (timeStart.isNullOrEmpty()) {
            timeStart =
                SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())

        }

        if (timeEnd.isNullOrEmpty()) {
            timeEnd =
                SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
        }

        viewModelMain.timeStart.value = timeStart.toString()
        viewModelMain.timeEnd.value = timeEnd.toString()

        // Is workday checked
        viewModelMain.isWorkday.value = sharePreferenceHelper.getDataBoolean(IS_WORK_DAY)!!
    }

    /**
     * Button click event
     */
    private fun setUpButtonClick() {
        // Btn execute
        dataBinding.btnExecute.setOnClickListener {
            sharePreferenceHelper.setDataStringKeyValue(TIME_START, viewModelMain.timeStart.value!!)
            sharePreferenceHelper.setDataStringKeyValue(TIME_END, viewModelMain.timeEnd.value!!)
            sharePreferenceHelper.setDataBooleKeyValue(IS_WORK_DAY, viewModelMain.isWorkday.value!!)

            if (viewModelMain.isWorkday.value!!) {
                if (viewModelMain.timeStart.value!! > viewModelMain.timeEnd.value!!) {
                    initDialogMessage(getString(R.string.main_dialog_mess_error_time))
                    return@setOnClickListener
                }
            }

            initDialogOK()
        }

        // Btn exit app
        dataBinding.btnExit.setOnClickListener {
           this@MainActivity.onBackPressed()
        }

        // Btn cancel all worker
        dataBinding.btnCancel.setOnClickListener {
            workerHelper.cancelAllWorker()
            sharePreferenceHelper.setDataStringKeyValue(TIME_START, "")
            sharePreferenceHelper.setDataStringKeyValue(TIME_END, "")
            sharePreferenceHelper.setDataBooleKeyValue(IS_WORK_DAY, false)
            setUpDefaultTime()
            initDialogMessage(getString(R.string.main_dialog_mess_cancel_all))
        }

        // Clear all history
        dataBinding.btnClear.setOnClickListener {
            viewModelMain.clearHistory()
        }
    }

    /**
     * Setup event change mode when click
     */
    private fun setUpEventClickChange() {
        dataBinding.viewNormal.setOnClickListener {
            changeModeOfAudioManager(AudioManager.RINGER_MODE_NORMAL)
            updateModeForView()
        }

        dataBinding.viewVibrate.setOnClickListener {
            changeModeOfAudioManager(AudioManager.RINGER_MODE_VIBRATE)
            updateModeForView()
        }

        dataBinding.viewSilent.setOnClickListener {
            changeModeOfAudioManager(0)
            updateModeForView()
        }
    }

    /**
     * Setup event click on select time
     */
    @Suppress("DEPRECATION")
    private fun setUpEventCLickTime() {
        dataBinding.viewDateStart.setOnClickListener {
            // Get Current Time
            val c = Calendar.getInstance()
            val mHour = c[Calendar.HOUR_OF_DAY]
            val mMinute = c[Calendar.MINUTE]

            // Launch Time Picker Dialog
            @SuppressLint("SetTextI18n") val timePickerDialog = TimePickerDialog(
                this@MainActivity,
                android.R.style.Theme_Holo_Light_Dialog_NoActionBar,
                OnTimeSetListener { _: TimePicker?, hourOfDay: Int, minute: Int ->
                    var hour = hourOfDay.toString()
                    if (hourOfDay < 10) {
                        hour = "0$hour"
                    }
                    var minuteString = minute.toString()
                    if (minute < 10) {
                        minuteString = "0$minuteString"
                    }
                    dataBinding.viewDateStart.setText("$hour:$minuteString")
                },
                mHour,
                mMinute,
                false
            )
            timePickerDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
            timePickerDialog.show()
        }

        dataBinding.viewDateEnd.setOnClickListener {
            // Get Current Time
            val c = Calendar.getInstance()
            val mHour = c[Calendar.HOUR_OF_DAY]
            val mMinute = c[Calendar.MINUTE]

            // Launch Time Picker Dialog
            @SuppressLint("SetTextI18n") val timePickerDialog = TimePickerDialog(
                this@MainActivity,
                android.R.style.Theme_Holo_Light_Dialog_NoActionBar,
                OnTimeSetListener { _: TimePicker?, hourOfDay: Int, minute: Int ->
                    var hour = hourOfDay.toString()
                    if (hourOfDay < 10) {
                        hour = "0$hour"
                    }
                    var minuteString = minute.toString()
                    if (minute < 10) {
                        minuteString = "0$minuteString"
                    }
                    dataBinding.viewDateEnd.setText("$hour:$minuteString")
                },
                mHour,
                mMinute,
                false
            )
            timePickerDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
            timePickerDialog.show()
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

    /**
     * Create dialog
     */
    private fun initDialogOK() {
        alertBuild = AlertDialog.Builder(this@MainActivity, R.style.cust_dialog)
        // On pressing Settings button
        alertBuild!!.setPositiveButton(getString(R.string.common_dialog_btn_ok)) { _: DialogInterface, _: Int ->
            // Exit activity
            dialog?.dismiss()
            workerHelper.cancelAllWorker()
            workerHelper.addWorkerToManager()
            this@MainActivity.onBackPressed()
        }

        // Set default for flag next day
        sharePreferenceHelper.setDataBooleKeyValue(TO_NEXT_DAY, false)

        // Setting Dialog Message
        var message = getString(R.string.main_dialog_mess)
        if (viewModelMain.isWorkday.value!!) {
            message += " " + getString(R.string.main_dialog_mess_work_day)
        } else {
            message += " " + getString(R.string.main_dialog_mess_non_work_day)

            if (viewModelMain.timeStart.value!! > viewModelMain.timeEnd.value!!) {
                message += getString(R.string.main_dialog_mess_start) + " " + viewModelMain.timeStart + getString(R.string.main_dialog_mess_today) + " "
                message += getString(R.string.main_dialog_mess_end) + " " + viewModelMain.timeEnd + getString(R.string.main_dialog_mess_tomoro)

                // Next day is true
                sharePreferenceHelper.setDataBooleKeyValue(TO_NEXT_DAY, true)
                sharePreferenceHelper.setDataIntKeyValue(
                    CURRENT_DATE,
                    Calendar.getInstance().get(Calendar.DATE))
            }
        }

        alertBuild!!.setMessage(message)
        alertBuild!!.setCancelable(false)

        // Showing Alert Message
        alertBuild!!.setTitle("")

        dialog?.dismiss()
        dialog = alertBuild!!.create()
        dialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)

        if (!(this@MainActivity).isFinishing && !dialog!!.isShowing) {
            dialog!!.show()
        }
    }

    /**
     * Create dialog
     */
    private fun initDialogMessage(message: String) {
        alertBuild = AlertDialog.Builder(this@MainActivity, R.style.cust_dialog)
        // On pressing Settings button
        alertBuild?.setPositiveButton(getString(R.string.common_dialog_btn_ok)) { _: DialogInterface, _: Int ->
            dialog?.dismiss()
        }
        // Setting Dialog Message
        alertBuild?.setMessage(message)
        alertBuild?.setCancelable(false)

        // Showing Alert Message
        alertBuild?.setTitle("")
        dialog?.dismiss()
        dialog = alertBuild?.create()
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)

        if (!(this@MainActivity).isFinishing && !dialog?.isShowing!!) {
            dialog?.show()
        }
    }

    /**
     * Update mode
     */
    private fun changeModeOfAudioManager(mode: Int) {
        try {
            val auManager =
                this@MainActivity.getSystemService(Context.AUDIO_SERVICE) as AudioManager
            if (auManager.ringerMode != mode) {
                auManager.ringerMode = mode
            }

//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && mode == AudioManager.RINGER_MODE_SILENT) {
//                (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_NONE)
//            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Get mode
     */
    private fun getModeAudioManager() : Int {
        try {
            val auManager =
                this@MainActivity.getSystemService(Context.AUDIO_SERVICE) as AudioManager
            return auManager.ringerMode
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return -1
    }

    /**
     * Update mode for view in layout
     */
    private fun updateModeForView() {
        when (getModeAudioManager()) {
            AudioManager.RINGER_MODE_NORMAL -> {
                viewModelMain.isNormal.value = true
                viewModelMain.isVibrate.value = false
                viewModelMain.isSilent.value = false
            }
            AudioManager.RINGER_MODE_VIBRATE -> {
                viewModelMain.isNormal.value = false
                viewModelMain.isVibrate.value = true
                viewModelMain.isSilent.value = false
            }
            AudioManager.RINGER_MODE_SILENT -> {
                viewModelMain.isNormal.value = false
                viewModelMain.isVibrate.value = false
                viewModelMain.isSilent.value = true
            }
            else -> {
                // Not know
            }
        }
    }
}
