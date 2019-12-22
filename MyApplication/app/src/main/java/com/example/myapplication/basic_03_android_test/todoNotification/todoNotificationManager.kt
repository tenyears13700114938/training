package com.example.myapplication.basic_03_android_test.todoNotification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.myapplication.R
import java.util.concurrent.locks.ReentrantLock

class todoNotificationManager(val mContext: Context) {
    lateinit var mNotificationManager : NotificationManagerCompat
    lateinit var mNotificationChannel: NotificationChannel
    val TODO_NOTIFICATION_ID = 100

    fun notifyTodoToDeal(pendingIntent: PendingIntent?) {
        NotificationCompat.Builder(mContext, TODO_CHANNEL_ID)
            .also {
                it.setContentTitle("todos need to be dealWith..")
                it.setContentText("in your todo list, exit expired or soon expire todo")
                it.setSmallIcon(R.drawable.ic_priority_high_black_24dp)
                it.setAutoCancel(true)
                pendingIntent?.also {_pendingIntent ->
                    it.setContentIntent(_pendingIntent)
                }
                mNotificationManager.notify(TODO_NOTIFICATION_ID, it.build())
            }
    }

    fun cancelTodoToDeal(){
        mNotificationManager.cancel(TODO_NOTIFICATION_ID)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun todoNotificationChannel(): NotificationChannel {
        return NotificationChannel(
            TODO_CHANNEL_ID,
            "TODO_Notification",
            NotificationManager.IMPORTANCE_HIGH
        ).also {
            it.enableLights(true)
            it.lightColor = mContext.getColor(R.color.colorPrimary)
            it.enableVibration(true)
            it.description = "todo needs to deal with..."
            mNotificationManager.createNotificationChannel(it)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun createTodoNotificationChannel(){
        mNotificationChannel = todoNotificationChannel()

    }

    companion object {
        private val TODO_CHANNEL_ID = "TODO_CHANNEL_ID"
        private var mIns: todoNotificationManager? = null
        private val insLock = ReentrantLock()
        @RequiresApi(Build.VERSION_CODES.O)
        fun getInstance(appContext: Context): todoNotificationManager {
            insLock.lock()
            if (mIns == null) {
                mIns = todoNotificationManager(appContext)
                mIns?.also {
                    it.mNotificationManager = NotificationManagerCompat.from(appContext)
                    it.createTodoNotificationChannel()
                }
            }
            insLock.unlock()
            return mIns as todoNotificationManager
        }
    }
}