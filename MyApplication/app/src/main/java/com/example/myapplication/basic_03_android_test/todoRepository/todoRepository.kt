package com.example.myapplication.basic_03_android_test.todoRepository

import android.content.Context
import androidx.lifecycle.LiveData
import com.example.myapplication.basic_03_android_test.model.Todo

class todoRepository(val context : Context) {
    val alltodos : LiveData<List<Todo>> by lazy {
        todoDatabase.getInstance(context).todoDao().getAll()
    }

    suspend fun addToDo(todo: Todo) {
        todoDatabase.getInstance(context).todoDao().insertTodo(todo)
    }

    suspend fun updateToDo(todo : Todo){
        todoDatabase.getInstance(context).todoDao().updateTodo(todo)
    }

    suspend fun deleteToDo(todo : Todo){
        todoDatabase.getInstance(context).todoDao().delete(todo)
    }

    companion object {
        private var todoIns : todoRepository? = null
        fun getInstance(context: Context) : todoRepository {
            synchronized(todoRepository::class.java) {
                if (todoIns == null) {
                    todoIns = todoRepository(context)
                }
                return todoIns as todoRepository
            }
        }
    }
}
