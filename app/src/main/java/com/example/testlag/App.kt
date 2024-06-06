package com.example.testlag

import android.app.Application
import com.yandex.mapkit.MapKitFactory

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        MapKitFactory.setApiKey("3b1b2150-7688-4bac-b1b4-8869260d927f")
    }
}