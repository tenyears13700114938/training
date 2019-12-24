package com.example.myapplication.basic_03_android_test.todoNotification

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.util.concurrent.locks.ReentrantLock

class todoAlarmManager(val appContext: Context){
    private val pendingIntent : PendingIntent
    private val mIntent : Intent
    private val TAG = todoAlarmManager::class.java.simpleName
    init {
        mIntent = Intent( "com.example.myapplication.basic_03_android_test.todoNotification.TodoExpiredCheck")
        pendingIntent = PendingIntent.getBroadcast(appContext, 0, mIntent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun run(){
        if (PendingIntent.getBroadcast(
                appContext,
                0,
                mIntent,
                PendingIntent.FLAG_NO_CREATE
            ) != null
        ) {
            Log.d(TAG, "alarm has exist")
        } else {
            appContext.getSystemService(Context.ALARM_SERVICE).also { _alarmService ->
                if (_alarmService is AlarmManager) {
                    _alarmService.setRepeating(
                        AlarmManager.RTC_WAKEUP,
                        LocalDateTime.now().plusMinutes(2).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(),
                        15 * 60 * 1000,
                        pendingIntent
                    )
                }
            }
        }
    }

    companion object {
        private var mIns: todoAlarmManager? = null
        private val reentrantLock = ReentrantLock()
        fun getInstance(appContext: Context) : todoAlarmManager {
            reentrantLock.lock()
            if(mIns == null){
                mIns = todoAlarmManager((appContext))
            }
            reentrantLock.unlock()
            return mIns!!
        }
    }


}