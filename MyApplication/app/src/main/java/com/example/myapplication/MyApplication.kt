package com.example.myapplication

import android.app.Application
import com.example.myapplication.util.dagger.DaggerAppComponent
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import javax.inject.Inject

class MyApplication : Application(),HasAndroidInjector {
    @Inject
    lateinit var dispatchingAndroidInjector : DispatchingAndroidInjector<Any>

    override fun androidInjector(): AndroidInjector<Any> {
        return dispatchingAndroidInjector
    }

    override fun onCreate() {
        super.onCreate()
        DaggerAppComponent.builder().myApp(this).build().inject(this)
        //todo start workmanager
    }

    override fun onTerminate() {
        super.onTerminate()
    }
}