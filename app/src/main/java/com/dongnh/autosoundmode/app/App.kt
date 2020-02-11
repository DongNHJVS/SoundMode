package com.dongnh.autosoundmode.app

import androidx.multidex.MultiDex
import androidx.multidex.MultiDexApplication
import com.dongnh.autosoundmode.di.sharePreferenceModule
import com.dongnh.autosoundmode.di.viewModelModule
import com.dongnh.autosoundmode.di.workModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import timber.log.Timber

class App : MultiDexApplication() {
    override fun onCreate() {
        super.onCreate()

        MultiDex.install(this@App)
        setupTimber()
        setupKoin()
    }

    private fun setupTimber() {
        Timber.plant(Timber.DebugTree())
    }

    private fun setupKoin() {
        startKoin {
            androidContext(this@App)
            modules(
                listOf(
                    viewModelModule,
                    sharePreferenceModule,
                    workModule
                )
            )
        }
    }
}
