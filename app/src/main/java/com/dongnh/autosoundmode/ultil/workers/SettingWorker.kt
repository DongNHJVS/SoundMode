package com.dongnh.autosoundmode.ultil.workers

import android.annotation.SuppressLint
import android.content.Context
import android.media.AudioManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.dongnh.autosoundmode.const.*
import com.dongnh.autosoundmode.db.dao.LogEntityDao
import com.dongnh.autosoundmode.db.entity.LogEntity
import com.dongnh.autosoundmode.ultil.helper.SharePreferenceHelper
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.koin.core.KoinComponent
import org.koin.core.get
import timber.log.Timber
import java.util.*


class SettingWorker(val context: Context, params: WorkerParameters) : Worker(context, params) , KoinComponent {

    companion object {
        val TAG = SettingWorker::class.java.simpleName
    }

    private val sharedPreferences: SharePreferenceHelper = get()
    private val logEntityDao: LogEntityDao = get()

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
        val month = c[Calendar.MONTH]
        val year = c[Calendar.YEAR]
        val hour = c[Calendar.HOUR_OF_DAY]
        val minute = c[Calendar.MINUTE]
        val dayOfWeek: Int = c.get(Calendar.DAY_OF_WEEK)

        val hourString =  if (hour < 10) {
            "0$hour"
        } else {
            hour.toString()
        }

        val minuteString =  if (minute < 10) {
            "0$minute"
        } else {
            minute.toString()
        }

        val currencyTime = "$hourString:$minuteString"

        // If run on working day
        if (isWorkday!!) {
            // If this is day off, stop setting
            if (dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY) {
                val logEntity = LogEntity(date = "$day/$month/$year", time = currencyTime, mode = NO_CHANGE, id = null)
                insertToDbLog(logEntity)
                return Result.success()
            }

            if (timeStart < currencyTime && currencyTime <= timeEnd) {
                // change to mode vibrate
                changeModeOfAudioManager(AudioManager.RINGER_MODE_VIBRATE,
                    "$day/$month/$year", currencyTime)
                return Result.success()
            } else if (currencyTime >= timeEnd) {
                // change to mode normal
                changeModeOfAudioManager(AudioManager.RINGER_MODE_NORMAL, "$day/$month/$year", currencyTime)
                return Result.success()
            }
        } else {
            // One day
            if (isNextday!!) {
                if (day == currentDay) {
                    if (currencyTime >= timeEnd) {
                        // change to mode normal
                        changeModeOfAudioManager(AudioManager.RINGER_MODE_NORMAL, "$day/$month/$year", currencyTime)
                        return Result.success()
                    }
                } else {
                    if (timeStart <= currencyTime) {
                        // change to mode vibrate
                        changeModeOfAudioManager(AudioManager.RINGER_MODE_VIBRATE, "$day/$month/$year", currencyTime)
                        return Result.success()
                    }
                }
            } else {
                if (timeStart < currencyTime && currencyTime <= timeEnd) {
                    // change to mode vibrate
                    changeModeOfAudioManager(AudioManager.RINGER_MODE_VIBRATE, "$day/$month/$year", currencyTime)
                    return Result.success()
                } else if (currencyTime >= timeEnd) {
                    // change to mode normal
                    changeModeOfAudioManager(AudioManager.RINGER_MODE_NORMAL, "$day/$month/$year", currencyTime)
                    return Result.success()
                }
            }
        }

        val logEntity = LogEntity(date = "$day/$month/$year", time = currencyTime, mode = NO_CHANGE, id = null)
        insertToDbLog(logEntity)
        // Need stop this
        return Result.success()
    }

    /**
     * Update mode
     */
    private fun changeModeOfAudioManager(mode: Int, date: String, time: String) {
        try {
            val auManager =
                context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
            if (auManager.ringerMode != mode) {
                auManager.ringerMode = mode

                var modeString = ""
                when (mode) {
                    AudioManager.RINGER_MODE_NORMAL -> {
                        modeString = NORMAL
                    }
                    AudioManager.RINGER_MODE_VIBRATE -> {
                        modeString = VIBRATE
                    }
                    AudioManager.RINGER_MODE_SILENT -> {
                        modeString = SILENT
                    }
                    else -> {
                        // Not know
                        modeString = NOT_KNOW
                    }
                }
                val logEntity = LogEntity(date = date, time = time, mode = modeString, id = null)
                insertToDbLog(logEntity)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @SuppressLint("CheckResult")
    private fun insertToDbLog(logEntity: LogEntity) {
        Observable.fromCallable { logEntityDao.insert(logEntity) }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    Timber.e("Insert ok")
                },
                {
                    Timber.e("Insert fail")
                    Timber.e(it)
                }
            )
    }

}