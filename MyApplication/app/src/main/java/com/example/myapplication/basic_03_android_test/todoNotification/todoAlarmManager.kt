package com.example.myapplication.basic_03_android_test.todoNotification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.ReentrantLock
import android.os.SystemClock
import java.time.*
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class todoAlarmManager @Inject constructor(val appContext: Context){
    private val pendingIntent : PendingIntent
    private val mIntent : Intent
    private val TAG = todoAlarmManager::class.java.simpleName
    //private val alramReceiver : todoAlarmReceiver
    init {
        //mIntent = Intent( "com.example.myapplication.basic_03_android_test.todoNotification.TodoExpiredCheck")
        mIntent = Intent(appContext, todoAlarmReceiver::class.java)
        pendingIntent = PendingIntent.getBroadcast(appContext, 0, mIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        //alramReceiver = todoAlarmReceiver()
        //appContext.registerReceiver(alramReceiver, IntentFilter("com.example.myapplication.basic_03_android_test.todoNotification.TodoExpiredCheck"))
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun run(){
        appContext.getSystemService(Context.ALARM_SERVICE).also { _alarmService ->
            if (_alarmService is AlarmManager) {
               /* val alarmMillis = LocalDateTime.now().plusMinutes(2).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
                Log.d(TAG, "debugAlarm set alarm time to:" + LocalDateTime.ofInstant(Instant.ofEpochMilli(alarmMillis), ZoneId.systemDefault()).toString())*/
                //val alarmMillis = System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(2)

                var targetDate = if(LocalTime.now().hour >= 12) LocalDate.now().plusDays(1)  else LocalDate.now()
                var targeTime =  LocalTime.of(if(LocalTime.now().hour >= 12) (23 - LocalTime.now().hour + 11) else 12, 0)
                //val alarmMillis = LocalDateTime.of(targetDate, targeTime).toInstant(ZoneId.systemDefault().rules.getOffset(Instant.now())).toEpochMilli()
                val alarmMillis = LocalDateTime.of(targetDate, targeTime).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

                Log.d(TAG, "debugAlarm set alarm time to:" + Date(alarmMillis).toString())
                _alarmService.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    alarmMillis,
                    //15 * 60 * 1000,
                    pendingIntent
                )
            }
         }
    }

    /*companion object {
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
    }*/
}