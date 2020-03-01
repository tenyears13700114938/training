package com.example.myapplication.basic_03_android_test.todoNotification

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.myapplication.MyApplication
import com.example.myapplication.basic_03_android_test.model.Todo
import com.example.myapplication.basic_03_android_test.todoList.StartType
import com.example.myapplication.basic_03_android_test.todoList.TodoListActivity
import com.example.myapplication.basic_03_android_test.todoRepository.todoRepository
import com.example.myapplication.util.dagger.AppComponent
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.Callable
import javax.inject.Inject

class todoAlarmReceiver : BroadcastReceiver() {
    private val TAG = todoAlarmReceiver::class.java.simpleName
    @Inject
    lateinit var todoRepository: todoRepository
    @Inject
    lateinit var todoAlarmManager: todoAlarmManager
    @Inject
    lateinit var todoNotificationManager: todoNotificationManager

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onReceive(context: Context, intent: Intent) {
        Log.d(TAG, "debugAlarm doWork......")
        (context.applicationContext as? MyApplication)?.appComponent?.inject(this)

        Observable.fromCallable(object : Callable<List<Todo>> {
            override fun call(): List<Todo> {
                return todoRepository.getNotificationTodo(System.currentTimeMillis())
            }
        }).subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe() {
                if (it.size > 0) {
                    val intent = Intent(context, TodoListActivity::class.java).also {
                        it.putExtra(
                            TodoListActivity.EXTRA_PARAMETER_START_TYPE,
                            StartType.search.type
                        )
                    }
                    todoNotificationManager.notifyTodoToDeal(
                        PendingIntent.getActivity(
                            context,
                            100,
                            intent,
                            PendingIntent.FLAG_ONE_SHOT
                        )
                    )
                }
                //next Check Alarm
                todoAlarmManager.run()
            }
    }
}
