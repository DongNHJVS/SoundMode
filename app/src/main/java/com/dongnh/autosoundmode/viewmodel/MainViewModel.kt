package com.dongnh.autosoundmode.viewmodel

import android.annotation.SuppressLint
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dongnh.autosoundmode.adapter.LogItemAdapter
import com.dongnh.autosoundmode.db.dao.LogEntityDao
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

class MainViewModel(val logEntityDao: LogEntityDao) : ViewModel() {
    val timeStart: MutableLiveData<String> = MutableLiveData()
    val timeEnd: MutableLiveData<String> = MutableLiveData()
    val isWorkday: MutableLiveData<Boolean> = MutableLiveData()

    val isNormal: MutableLiveData<Boolean> = MutableLiveData()
    val isVibrate: MutableLiveData<Boolean> = MutableLiveData()
    val isSilent: MutableLiveData<Boolean> = MutableLiveData()

    val adapterLogEntity: LogItemAdapter = LogItemAdapter()

    init {
        timeStart.value = ""
        timeEnd.value = ""
        isWorkday.value = false

        isNormal.value = false
        isVibrate.value = false
        isSilent.value = false
    }

    @SuppressLint("CheckResult")
    fun loadDataFormDbToShow() {
        Observable.fromCallable { logEntityDao.loadAll }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    Timber.e("Load ok")
                    adapterLogEntity.setDataForAdapter(it)
                },
                {
                    Timber.e("Load Error")
                    Timber.e(it)
                }
            )
    }

    @SuppressLint("CheckResult")
    fun clearHistory() {
        Observable.fromCallable { logEntityDao.deleteAllLog() }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    Timber.e("Delete ok")
                    adapterLogEntity.setDataForAdapter(arrayListOf())
                },
                {
                    Timber.e("Delete Error")
                    Timber.e(it)
                }
            )
    }
}