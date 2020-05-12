package com.example.myapplication

import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.work.Configuration
import com.example.myapplication.basic_03_android_test.nasaphotoRepository.photoDownloadWorkManager
import com.example.myapplication.basic_03_android_test.todoNotification.todoAlarmManager
import com.example.myapplication.util.dagger.AppComponent
import com.example.myapplication.util.dagger.DaggerAppComponent
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import javax.inject.Inject

open class MyApplication : Application(),HasAndroidInjector, Configuration.Provider {
    @Inject
    lateinit var todoAlarmManager: todoAlarmManager
    lateinit var appComponent: AppComponent
    override fun getWorkManagerConfiguration(): Configuration {
        return Configuration.Builder().build()
    }

    @Inject
    lateinit var dispatchingAndroidInjector : DispatchingAndroidInjector<Any>

    override fun androidInjector(): AndroidInjector<Any> {
        return dispatchingAndroidInjector
    }

    open fun createAppComponent() {
        appComponent = DaggerAppComponent.builder().myApp(this).build()
        appComponent.inject(this@MyApplication)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate() {
        super.onCreate()
        //appComponent = DaggerAppComponent.builder().myApp(this).build().apply {
         createAppComponent()
        //}
        //todo start workmanager
        todoAlarmManager.run()
        photoDownloadWorkManager.getIns(this).run()
    }

    override fun onTerminate() {
        super.onTerminate()
    }
}