package com.dongnh.autosoundmode.di

import com.dongnh.autosoundmode.ultil.helper.ManagerWorkerHelper
import org.koin.dsl.module

val workModule = module {
    single { createWorkerManager() }
}

private fun createWorkerManager() : ManagerWorkerHelper {
    return ManagerWorkerHelper()
}