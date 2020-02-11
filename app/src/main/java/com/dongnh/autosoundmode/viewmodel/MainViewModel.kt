package com.dongnh.autosoundmode.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {
    val timeStart: MutableLiveData<String> = MutableLiveData()
    val timeEnd: MutableLiveData<String> = MutableLiveData()
    val isWorkday: MutableLiveData<Boolean> = MutableLiveData()

    val isNormal: MutableLiveData<Boolean> = MutableLiveData()
    val isVibrate: MutableLiveData<Boolean> = MutableLiveData()
    val isSilent: MutableLiveData<Boolean> = MutableLiveData()

    init {
        timeStart.value = ""
        timeEnd.value = ""
        isWorkday.value = false

        isNormal.value = false
        isVibrate.value = false
        isSilent.value = false
    }
}