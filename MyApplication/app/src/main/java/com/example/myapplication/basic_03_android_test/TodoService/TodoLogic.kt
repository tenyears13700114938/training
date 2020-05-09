package com.example.myapplication.basic_03_android_test.TodoService

import android.content.Context
import android.util.Log
import com.example.myapplication.basic_03_android_test.Flux.TodoAdded
import com.example.myapplication.basic_03_android_test.Flux.TodoDeleted
import com.example.myapplication.basic_03_android_test.Flux.TodoUpdated
import com.example.myapplication.basic_03_android_test.model.Todo
import com.example.myapplication.basic_03_android_test.todoRepository.ITodoRepository
import com.example.myapplication.basic_03_android_test.todoRepository.todoRepository
import dagger.Binds
import javax.inject.Inject
import javax.inject.Singleton

enum class OpResult(val result: Int) {
    DELETE_OK(0),
    ADD_OK(1),
    UPDATE_OK(2),
    DB_ADD_FAILED(-1),
    DB_DELETE_FAILED(-2),
    DB_UPDATE_FAILED(-3),
    TODO_ALREADY_DOING(-4)
}

@Singleton
class TodoLogic @Inject constructor(
    val appContext: Context,
    val todoRepository: ITodoRepository
)/* : ResultReceiver(Handler())*/ : ITodoLogic {
    val RESULT_EXTRA_PARAM_TODO = "RESULT_EXTRA_PARAM_TODO"
    private val TAG = TodoLogic::class.java.simpleName

    // sample code of service return value.
    /*override fun onReceiveResult(resultCode: Int, resultData: Bundle?) {
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
    }*/

    override fun isTodoEditing(todo: Todo): Boolean {
        synchronized(mEditTodoMap) {
            return mEditTodoMap.containsKey(todo.id)
        }
    }

    override fun isTodoAdding(todo: Todo): Boolean {
        synchronized(mAddTodoList) {
            return mAddTodoList.contains(todo)
        }
    }

    override fun popEditMap(todo: Todo) {
        synchronized(mEditTodoMap) {
            mEditTodoMap.remove(todo.id)
        }
    }

    override suspend fun updateTodo(todo: Todo): TodoUpdated {
        var result = OpResult.UPDATE_OK
        val copy = todo.copy()
        synchronized(mEditTodoMap) {
            if (isTodoEditing(todo)) {
                result = OpResult.TODO_ALREADY_DOING
            } else {
                mEditTodoMap[copy.id] = copy
                //TodoOpIntentService.startActionUpdateTodo(appContext, copy, this)
            }
        }
        if (result != OpResult.TODO_ALREADY_DOING) {
            todoRepository.updateToDo(copy)
            popEditMap(copy)
        }
        return TodoUpdated(copy, result)
    }

    override suspend fun deleteTodo(todo: Todo): TodoDeleted {
        var result = OpResult.DELETE_OK
        val copy = todo.copy()
        synchronized(mEditTodoMap) {
            if (isTodoEditing(todo)) {
                result = OpResult.TODO_ALREADY_DOING
            } else {
                Log.d(TAG, "delete todo....")
                mEditTodoMap[copy.id] = todo
                //TodoOpIntentService.startActionDeleteTodo(appContext, copy, this)
            }
        }
        if (result != OpResult.TODO_ALREADY_DOING) {
            todoRepository.deleteToDo(copy)
            popEditMap(copy)
        }

        return TodoDeleted(copy, result)
    }

    override suspend fun addTodo(todo: Todo): TodoAdded {
        var result = OpResult.ADD_OK
        val copy = todo.copy()
        synchronized(mAddTodoList) {
            if (isTodoAdding(copy)) {
                result = OpResult.TODO_ALREADY_DOING
            } else {
                Log.d(TAG, "add todo....")
                mAddTodoList.add(copy)
                //TodoOpIntentService.starAddTodo(appContext, copy, this)
            }
        }
        if (result != OpResult.TODO_ALREADY_DOING) {
            todoRepository.addToDo(copy)
            removeTodo(copy)
        }
        return TodoAdded(copy, result)
    }

    override fun removeTodo(todo: Todo) {
        synchronized(mAddTodoList) {
            mAddTodoList.remove(todo)
        }
    }


    val mEditTodoMap = hashMapOf<Long, Todo>()
    val mAddTodoList = mutableListOf<Todo>()
}