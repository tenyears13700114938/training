package com.example.myapplication.basic_03_android_test.TodoService

import android.app.IntentService
import android.content.Intent
import android.content.Context
import android.os.Bundle
import android.support.v4.os.ResultReceiver
import android.util.Log
import com.example.myapplication.MyApplication
import com.example.myapplication.basic_03_android_test.model.Todo
import com.example.myapplication.basic_03_android_test.todoRepository.todoRepository
import kotlinx.coroutines.*
import javax.inject.Inject

private const val ACTION_ADD_TODO =
    "com.example.myapplication.basic_03_android_test.TodoService.action.ADD_TODO"
private const val ACTION_DELETE_TODO =
    "com.example.myapplication.basic_03_android_test.TodoService.action.DELETE_TODO"
private const val ACTION_UPDATE_TODO =
    "com.example.myapplication.basic_03_android_test.TodoService.action.UPDATE_TODO"

const val EXTRA_PARAM_TODO_INFO =
    "com.example.myapplication.basic_03_android_test.TodoService.extra.PARAM_TODO_INFO"
private const val EXTRA_PARAM_RECEIVER =
    "com.example.myapplication.basic_03_android_test.TodoService.extra.PARAM_RECEIVER"

/**
 * An [IntentService] subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 */
class TodoOpIntentService() : IntentService("TodoOpIntentService") {
    private val TAG = TodoOpIntentService::class.java.simpleName
    @Inject
    lateinit var todoRepository: todoRepository

    override fun onCreate() {
        super.onCreate()
        (application as? MyApplication)?.appComponent?.inject(this)
    }

    override fun onHandleIntent(intent: Intent?) {
        Log.d(TAG, "onHandleIntent todo.." + intent?.action)
        when (intent?.action) {
            ACTION_ADD_TODO -> {
                val addTodo = intent.getSerializableExtra(EXTRA_PARAM_TODO_INFO) as Todo
                val receiver = intent.getParcelableExtra<ResultReceiver>(EXTRA_PARAM_RECEIVER)
                //GlobalScope.launch {
                    CoroutineScope(Dispatchers.IO).launch {
                        handleAddTodo(addTodo)
                        Bundle().also {
                            it.putSerializable(EXTRA_PARAM_TODO_INFO, addTodo)
                            receiver.send(OpResult.ADD_OK.result, it)
                        }
                    }
                //}
            }
            ACTION_UPDATE_TODO -> {
                val updateTodo = intent.getSerializableExtra(EXTRA_PARAM_TODO_INFO) as Todo
                val receiver = intent.getParcelableExtra<ResultReceiver>(EXTRA_PARAM_RECEIVER)
                CoroutineScope(Dispatchers.IO).launch {
                    handleUpdateTodo(updateTodo)
                    Bundle().also {
                        it.putSerializable(EXTRA_PARAM_TODO_INFO, updateTodo)
                        receiver.send(OpResult.UPDATE_OK.result, it)
                    }
                }
            }
            ACTION_DELETE_TODO -> {
                val deleteTodo = intent.getSerializableExtra(EXTRA_PARAM_TODO_INFO) as Todo
                val receiver = intent.getParcelableExtra<ResultReceiver>(EXTRA_PARAM_RECEIVER)
                CoroutineScope(Dispatchers.IO).launch {
                    handleDeleteTodo(deleteTodo)
                    Bundle().also {
                        it.putSerializable(EXTRA_PARAM_TODO_INFO, deleteTodo)
                        receiver.send(OpResult.DELETE_OK.result, it)
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    /**
     * Handle action Add todo in the provided background thread with the provided
     * parameters.
     */
    suspend fun handleAddTodo(todo: Todo?) {
        todo?.also {
            Log.d(TAG, "add todo.....")
            todoRepository.addToDo(todo)
        }
    }

    /**
     * Handle action update todo in the provided background thread with the provided
     * parameters.
     */
    suspend fun handleUpdateTodo(todo : Todo?) {
        todo?.also {
            todoRepository.updateToDo(it)
        }
    }

    /**
     * Handle action delete todo in the provided background thread with the provided
     * parameters.
     */
    suspend fun handleDeleteTodo(todo : Todo?) {
        todo?.also {
            Log.d(TAG, "delete todo.....")
            todoRepository.deleteToDo(it)
        }
    }

    companion object {
        /**
         * Starts this service to perform action Foo with the given parameters. If
         * the service is already performing a task this action will be queued.
         *
         * @see IntentService
         */
        @JvmStatic
        fun starAddTodo(context: Context, todo : Todo, receiver : ResultReceiver) {
            val intent = Intent(context, TodoOpIntentService::class.java).apply {
                action = ACTION_ADD_TODO
                putExtra(EXTRA_PARAM_TODO_INFO, todo)
                putExtra(EXTRA_PARAM_RECEIVER, receiver)
            }

            context.startService(intent)
        }

        /**
         * Starts this service to perform action Baz with the given parameters. If
         * the service is already performing a task this action will be queued.
         *
         * @see IntentService
         */
        @JvmStatic
        fun startActionDeleteTodo(context: Context, todo : Todo, receiver: ResultReceiver) {
            val intent = Intent(context, TodoOpIntentService::class.java).apply {
                action = ACTION_DELETE_TODO
                putExtra(EXTRA_PARAM_TODO_INFO, todo)
                putExtra(EXTRA_PARAM_RECEIVER, receiver)
            }
            context.startService(intent)
        }

        @JvmStatic
        fun startActionUpdateTodo(context: Context, todo : Todo, receiver: ResultReceiver) {
            val intent = Intent(context, TodoOpIntentService::class.java).apply {
                action = ACTION_UPDATE_TODO
                putExtra(EXTRA_PARAM_TODO_INFO, todo)
                putExtra(EXTRA_PARAM_RECEIVER, receiver)
            }
            context.startService(intent)
        }
    }
}
