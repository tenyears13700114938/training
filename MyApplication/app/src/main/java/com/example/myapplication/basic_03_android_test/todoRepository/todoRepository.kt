package com.example.myapplication.basic_03_android_test.todoRepository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import com.example.myapplication.basic_03_android_test.model.Todo
import dagger.Binds
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class todoRepository @Inject constructor(val context : Context, val todoDao: todoDao) : ITodoRepository {
    override val alltodosLiveData : LiveData<List<Todo>> by lazy {
        todoDao.getAllLiveData()
    }

    override val alltodos: List<Todo>
        get() = todoDao.getAllTodos()

    override fun addToDo(todo: Todo) {
        todoDao.insertTodo(todo)
    }

    override fun updateToDo(todo : Todo){
        todoDao.updateTodo(todo)
    }

    override fun deleteToDo(todo : Todo){
        todo.imageUrl?.also {
            File(it).delete()
        }
        todoDao.delete(todo)
    }

    fun searchTodo(key : String) : DataSource.Factory<Int, Todo>?{
        return todoDao.searchByTitleOrDescription(key)
    }

    override fun getNotificationTodo(limitTime : Long) : List<Todo>{
        return todoDao.getNotificationTodo(limitTime)
    }

    override fun getLiveNotificationTodo(limitTime : Long) : LiveData<List<Todo>>{
        return todoDao.getLiveNotificationTodo(limitTime)
    }

    /*companion object {
        private var todoIns : todoRepository? = null
        fun getInstance(context: Context) : todoRepository {
            synchronized(todoRepository::class.java) {
                if (todoIns == null) {
                    todoIns = todoRepository(context)
                }
                return todoIns as todoRepository
            }
        }
    }*/
}