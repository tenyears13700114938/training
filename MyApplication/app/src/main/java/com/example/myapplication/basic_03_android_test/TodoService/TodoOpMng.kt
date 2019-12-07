package com.example.myapplication.basic_03_android_test.TodoService

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v4.os.ResultReceiver
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.myapplication.basic_03_android_test.model.Todo
import com.example.myapplication.basic_03_android_test.tooBroadcastReceiver.todoBroadcastReceiver
import com.example.myapplication.util.copyTodo
import java.util.concurrent.locks.ReentrantLock

enum class OpResult(val result: Int) {
    DELETE_OK(0),
    ADD_OK(1),
    UPDATE_OK(2),
    DB_ADD_FAILED(-1),
    DB_DELETE_FAILED(-2),
    DB_UPDATE_FAILED(-3),
    TODO_ALREADY_DOING(-4),
    REQUEST_ACCEPT(-5)
}

class TodoOpMng(val appContext: Context) : ResultReceiver(Handler()) {
    val RESULT_EXTRA_PARAM_TODO = "RESULT_EXTRA_PARAM_TODO"
    private val editMapLock = ReentrantLock()
    private val addListLock = ReentrantLock()

    override fun onReceiveResult(resultCode: Int, resultData: Bundle?) {
        when (resultCode) {
            OpResult.ADD_OK.result, OpResult.DB_ADD_FAILED.result -> {
                resultData?.getSerializable(RESULT_EXTRA_PARAM_TODO).also {
                    if (it is Todo) {
                        removeTodo(it)
                    }
                }
            }
            OpResult.UPDATE_OK.result, OpResult.DELETE_OK.result, OpResult.DB_UPDATE_FAILED.result, OpResult.DB_DELETE_FAILED.result -> {
                resultData?.getSerializable(EXTRA_PARAM_TODO_INFO).also {_todo ->
                    if (_todo is Todo) {
                        popEditMap(_todo)
                        Intent(todoBroadcastReceiver.TODO_RESULT_INTENT_FILTER).also {
                            it.putExtra(todoBroadcastReceiver.TODO_RESULT_EXTRA_PARAM, resultCode)
                            it.putExtra(todoBroadcastReceiver.TODO_INFO_EXTRA_PARAM,_todo)
                            LocalBroadcastManager.getInstance(appContext).sendBroadcast(it)
                        }
                    }
                }
            }
            else -> {
            }
        }
    }

    fun isTodoEditing(todo: Todo): Boolean {
        var isEditing = false
        try {
            editMapLock.lock()
            isEditing = mEditTodoMap.containsKey(todo.id)
        } finally {
            editMapLock.unlock()
        }
        return isEditing
    }

    fun isTodoAdding(todo: Todo): Boolean {
        var isAdding = false
        try {
            addListLock.lock()
            isAdding = mAddTodoList.contains(todo)
        } finally {
            addListLock.unlock()
        }
        return isAdding
    }

    fun popEditMap(todo: Todo) {
        try {
            editMapLock.lock()
            mEditTodoMap.remove(todo.id)
        } finally {
            editMapLock.unlock()
        }
    }

    fun updateTodo(todo: Todo): OpResult {
        var result = OpResult.REQUEST_ACCEPT
        try {
            editMapLock.lock()
            if (isTodoEditing(todo)) {
                result = OpResult.TODO_ALREADY_DOING
            } else {
                val copy = Todo()
                copyTodo(todo, copy)
                mEditTodoMap[copy.id] = copy
                TodoOpIntentService.startActionUpdateTodo(appContext, copy, this)
            }
        } finally {
            editMapLock.unlock()
        }
        return result
    }

    fun deleteTodo(todo: Todo): OpResult {
        var result = OpResult.REQUEST_ACCEPT
        try {
            editMapLock.lock()
            if (isTodoEditing(todo)) {
                result = OpResult.TODO_ALREADY_DOING
            } else {
                val copy = Todo()
                copyTodo(todo, copy)
                mEditTodoMap[copy.id] = todo
                TodoOpIntentService.startActionDeleteTodo(appContext, copy, this)
            }
        } finally {
            editMapLock.unlock()
        }
        return result
    }

    fun addTodo(todo: Todo): OpResult {
        var result = OpResult.REQUEST_ACCEPT
        try {
            addListLock.lock()
            val copy = Todo()
            copyTodo(todo,copy)
            if (isTodoAdding(copy)) {
                result = OpResult.TODO_ALREADY_DOING
            } else {
                mAddTodoList.add(copy)
                TodoOpIntentService.starAddTodo(appContext, copy, this)
            }
        } finally {
            addListLock.unlock()
        }
        return result
    }

    fun removeTodo(todo: Todo) {
        try {
            addListLock.lock()
            mAddTodoList.remove(todo)
        } finally {
            addListLock.unlock()
        }
    }


    val mEditTodoMap = hashMapOf<Long, Todo>()
    val mAddTodoList = mutableListOf<Todo>()

    companion object {
        private var mIns: TodoOpMng? = null
        fun getIns(appContext: Context): TodoOpMng {
            if (mIns == null) {
                mIns = TodoOpMng(appContext)
            }

            return mIns!!
        }
    }
}