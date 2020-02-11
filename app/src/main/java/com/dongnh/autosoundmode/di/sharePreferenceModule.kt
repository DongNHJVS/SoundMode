package com.dongnh.autosoundmode.di

import android.content.Context
import com.dongnh.autosoundmode.ultil.helper.SharePreferenceHelper
import org.koin.dsl.module

val sharePreferenceModule = module {
    single { createSharePreferenceHelper(get()) }
}

private fun createSharePreferenceHelper(context: Context): SharePreferenceHelper {
    return SharePreferenceHelper(context)
}