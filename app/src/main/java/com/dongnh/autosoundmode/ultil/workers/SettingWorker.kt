package com.dongnh.autosoundmode.ultil.workers

import android.content.Context
import android.media.AudioManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.dongnh.autosoundmode.const.*
import com.dongnh.autosoundmode.ultil.helper.SharePreferenceHelper
import org.koin.core.KoinComponent
import org.koin.core.get
import java.util.*


class SettingWorker(val context: Context, params: WorkerParameters) : Worker(context, params) , KoinComponent{

    companion object {
        val TAG = SettingWorker::class.java.simpleName
    }

    private val sharedPreferences: SharePreferenceHelper = get()

    override fun doWork(): Result {

        // Get data from SharedPreferences
        val timeStart = sharedPreferences.getDataString(TIME_START)
        val timeEnd = sharedPreferences.getDataString(TIME_END)
        val isWorkday = sharedPreferences.getDataBoolean(IS_WORK_DAY)
        val isNextday = sharedPreferences.getDataBoolean(TO_NEXT_DAY)
        val currentDay = sharedPreferences.getDataInt(CURRENT_DATE)

        if (timeStart.isNullOrEmpty() || timeEnd.isNullOrEmpty()) {
            return Result.success()
        }

        // Get time current of app
        val c = Calendar.getInstance()
        val day = c[Calendar.DAY_OF_MONTH]
        val hour = c[Calendar.HOUR_OF_DAY]
        val minute = c[Calendar.MINUTE]
        val dayOfWeek: Int = c.get(Calendar.DAY_OF_WEEK)

        val hourString =  if (hour < 10) {
            "0$hour"
        } else {
            hour.toString()
        }

        val minuteString =  if (hour < 10) {
            "0$minute"
        } else {
            minute.toString()
        }

        val currencyTime = "$hourString:$minuteString"

        // If run on working day
        if (isWorkday!!) {
            // If this is day off, stop setting
            if (dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY) {
                return Result.success()
            }

            if (timeStart < currencyTime && currencyTime <= timeEnd) {
                // change to mode vibrate
                changeModeOfAudioManager(AudioManager.RINGER_MODE_VIBRATE)
            } else if (currencyTime >= timeEnd) {
                // change to mode normal
                changeModeOfAudioManager(AudioManager.RINGER_MODE_NORMAL)
            }
        } else {
            // One day
            if (isNextday!!) {
                if (day == currentDay) {
                    if (currencyTime >= timeEnd) {
                        // change to mode normal
                        changeModeOfAudioManager(AudioManager.RINGER_MODE_NORMAL)
                    }
                } else {
                    if (timeStart <= currencyTime) {
                        // change to mode vibrate
                        changeModeOfAudioManager(AudioManager.RINGER_MODE_VIBRATE)
                    }
                }
            } else {
                if (timeStart < currencyTime && currencyTime <= timeEnd) {
                    // change to mode vibrate
                    changeModeOfAudioManager(AudioManager.RINGER_MODE_VIBRATE)
                } else if (currencyTime >= timeEnd) {
                    // change to mode normal
                    changeModeOfAudioManager(AudioManager.RINGER_MODE_NORMAL)
                }
            }
        }

        // Need stop this
        return Result.success()
    }

    /**
     * Update mode
     */
    private fun changeModeOfAudioManager(mode: Int) {
        try {
            val auManager =
                context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
            auManager.ringerMode = mode
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}