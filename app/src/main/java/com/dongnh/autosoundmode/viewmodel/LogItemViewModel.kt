package com.dongnh.autosoundmode.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dongnh.autosoundmode.db.entity.LogEntity

class LogItemViewModel : ViewModel() {
    val time: MutableLiveData<String> = MutableLiveData()
    val date: MutableLiveData<String> = MutableLiveData()
    val mode: MutableLiveData<String> = MutableLiveData()

    init {
        time.value = ""
        date.value = ""
        mode.value = ""
    }

    fun binding(logEntity: LogEntity) {
        time.value = logEntity.time
        date.value = logEntity.date
        mode.value = logEntity.mode
    }
}
