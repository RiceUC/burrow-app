package com.clarice.burrow

import android.app.Application
import android.content.Context

class BurrowApplication : Application() {
    companion object {
        lateinit var appContext: Context
            private set
    }

    override fun onCreate() {
        super.onCreate()
        appContext = applicationContext
    }
}
