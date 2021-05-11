package com.camo.kripto

import android.app.Application
import timber.log.Timber

class Kripto: Application() {
    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
    }
}