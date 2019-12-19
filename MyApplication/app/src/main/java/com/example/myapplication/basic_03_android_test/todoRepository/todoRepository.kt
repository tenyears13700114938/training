package com.example.myapplication.basic_03_android_test.todoRepository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import com.example.myapplication.basic_03_android_test.model.Todo
import java.io.File

class todoRepository(val context : Context) {
    val alltodos : LiveData<List<Todo>> by lazy {
        todoDatabase.getInstance(context).todoDao().getAll()
    }

    fun addToDo(todo: Todo) {
        todoDatabase.getInstance(context).todoDao().insertTodo(todo)
    }

    fun updateToDo(todo : Todo){
        todoDatabase.getInstance(context).todoDao().updateTodo(todo)
    }

    fun deleteToDo(todo : Todo){
        todo.imageUrl?.also {
            File(it).delete()
        }
        todoDatabase.getInstance(context).todoDao().delete(todo)
    }

    fun searchTodo(key : String) : DataSource.Factory<Int, Todo>{
        return todoDatabase.getInstance(context).todoDao().searchByTitleOrDescription(key)
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
