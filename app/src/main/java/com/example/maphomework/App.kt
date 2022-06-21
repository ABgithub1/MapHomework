package com.example.maphomework

import android.app.Application
import com.example.maphomework.koin.serviceModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@App)
            modules(
                serviceModule
            )
        }

    }

}