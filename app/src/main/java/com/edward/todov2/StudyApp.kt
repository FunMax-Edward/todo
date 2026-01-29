package com.edward.todov2

import android.app.Application
import com.edward.todov2.di.AppContainer

class StudyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        AppContainer.init(this)
    }
}
