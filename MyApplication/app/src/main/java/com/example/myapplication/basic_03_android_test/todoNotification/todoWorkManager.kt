package com.example.myapplication.basic_03_android_test.todoNotification

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.ReentrantLock

class todoWorkManager(val appontext : Context) {
    val mWorkManager : WorkManager

    init {
        mWorkManager = WorkManager.getInstance(appontext)
    }

    fun run(){
        mWorkManager.enqueueUniquePeriodicWork(
            WORKER_NAME,
            ExistingPeriodicWorkPolicy.REPLACE,
            PeriodicWorkRequestBuilder<todoCheckWorker>(15, TimeUnit.MINUTES)
                .setInitialDelay(1, TimeUnit.MINUTES)
                .build()
        )
    }

    companion object{
        private val WORKER_NAME = "com.example.myapplication.basic_03_android_test.todoNotification.TodoExpiredCheck"
        var mIns : todoWorkManager? = null
        val reentrantLock = ReentrantLock()
        fun getIns(appContext: Context) : todoWorkManager {
            reentrantLock.lock()
            if(mIns == null){
                mIns = todoWorkManager(appContext)
            }
            reentrantLock.unlock()
            return mIns!!
        }
    }
}