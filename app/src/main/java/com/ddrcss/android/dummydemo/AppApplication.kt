package com.ddrcss.android.dummydemo

import android.app.Application
import com.ddrcss.android.dummydemo.util.GlobalFactory

class AppApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        GlobalFactory.context = this
    }
}