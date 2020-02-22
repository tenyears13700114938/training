package com.example.myapplication.basic_03_android_test.nasaphotoRepository

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.ReentrantLock

class photoDownloadWorkManager(appContext : Context) {
    private val WORKER_NAME = "com.example.myapplication.basic_03_android_test.nasaphotoRepository.photoDownloadWorkManager"
    private val mWorkManager : WorkManager = WorkManager.getInstance(appContext)

    fun run(){
        mWorkManager.enqueueUniquePeriodicWork(
            WORKER_NAME,
            ExistingPeriodicWorkPolicy.REPLACE,
            PeriodicWorkRequestBuilder<photoDownloadWorker>(24, TimeUnit.HOURS)
                .setInitialDelay(1, TimeUnit.MINUTES)
                .build()
        )
    }

    companion object{
        var mIns : photoDownloadWorkManager? = null
        private val reentrantLock = ReentrantLock()
        fun getIns(appContext: Context) : photoDownloadWorkManager {
            reentrantLock.lock()
            if(mIns == null){
                mIns =
                    photoDownloadWorkManager(
                        appContext
                    )
            }
            reentrantLock.unlock()
            return mIns!!
        }
    }
}