package com.example.myapplication.basic_03_android_test.todoNotification

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.myapplication.basic_03_android_test.todoList.StartType
import com.example.myapplication.basic_03_android_test.todoList.TodoListActivity
import com.example.myapplication.basic_03_android_test.todoRepository.todoRepository

class todoCheckWorker(ctx: Context, params: WorkerParameters) : Worker(ctx, params) {
    companion object{
        val TAG = todoCheckWorker::class.java.simpleName
    }
    @RequiresApi(Build.VERSION_CODES.O)
    override fun doWork(): Result {
        //search todo soon expire db or expired
        Log.d(TAG, "doWork......")

        todoRepository.getInstance(applicationContext)
            .getNotificationTodo(System.currentTimeMillis())
            .let {
                if (it.size > 0) {
                    val intent = Intent(applicationContext, TodoListActivity::class.java).also {
                        it.putExtra(TodoListActivity.EXTRA_PARAMETER_START_TYPE, StartType.search.type)
                    }
                    todoNotificationManager.getInstance(applicationContext).notifyTodoToDeal(
                        PendingIntent.getActivity(
                            applicationContext,
                            100,
                            intent,
                            PendingIntent.FLAG_ONE_SHOT
                        )
                    )
                }
            }

        return Result.success()
    }
}