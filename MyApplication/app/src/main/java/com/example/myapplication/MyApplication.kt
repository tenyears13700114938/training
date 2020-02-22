package com.example.myapplication

import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.work.Configuration
import com.example.myapplication.basic_03_android_test.nasaphotoRepository.photoDownloadWorkManager
import com.example.myapplication.basic_03_android_test.todoNotification.todoAlarmManager
import com.example.myapplication.util.dagger.DaggerAppComponent
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import javax.inject.Inject

class MyApplication : Application(),HasAndroidInjector, Configuration.Provider {
    override fun getWorkManagerConfiguration(): Configuration {
        return Configuration.Builder().build()
    }

    @Inject
    lateinit var dispatchingAndroidInjector : DispatchingAndroidInjector<Any>

    override fun androidInjector(): AndroidInjector<Any> {
        return dispatchingAndroidInjector
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate() {
        super.onCreate()
        DaggerAppComponent.builder().myApp(this).build().inject(this)
        //todo start workmanager
        todoAlarmManager.getInstance(this).run()
        photoDownloadWorkManager.getIns(this).run()
    }

    override fun onTerminate() {
        super.onTerminate()
    }
}