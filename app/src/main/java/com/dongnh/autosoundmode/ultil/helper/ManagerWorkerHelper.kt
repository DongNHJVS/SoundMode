package com.dongnh.autosoundmode.ultil.helper

import androidx.work.*
import com.dongnh.autosoundmode.ultil.workers.SettingWorker
import java.util.concurrent.TimeUnit

@Suppress("DEPRECATION")
class ManagerWorkerHelper() {

    private val periodicBuilder = PeriodicWorkRequest.Builder(SettingWorker::class.java, 15, TimeUnit.MINUTES) // 15 minute
    private val myWork = periodicBuilder.addTag(SettingWorker.TAG).build()

    init {
        val constraints: Constraints = Constraints.Builder()
            .setRequiresCharging(false)
            .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
            .setRequiresDeviceIdle(false)
            .setRequiresStorageNotLow(false)
            .setRequiresBatteryNotLow(false)
            .build()
        periodicBuilder.setConstraints(constraints)
    }

    fun cancelAllWorker() {
        WorkManager.getInstance().cancelAllWorkByTag(SettingWorker.TAG)
        WorkManager.getInstance().cancelWorkById(myWork.id)
        WorkManager.getInstance().cancelUniqueWork(SettingWorker.TAG)
    }

    fun addWorkerToManager() {
        WorkManager.getInstance().enqueueUniquePeriodicWork(SettingWorker.TAG, ExistingPeriodicWorkPolicy.KEEP, myWork)
    }
}