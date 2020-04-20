package com.example.myapplication.basic_03_android_test.todoRepository

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import com.example.myapplication.basic_03_android_test.model.Todo

interface ITodoRepository {
    val alltodosLiveData : LiveData<List<Todo>>
    val alltodos : List<Todo>
    fun addToDo(todo: Todo)
    fun updateToDo(todo : Todo)
    fun deleteToDo(todo : Todo)
    //fun searchTodo(key : String) : DataSource.Factory<Int, Todo>
    fun getNotificationTodo(limitTime : Long) : List<Todo>
    fun getLiveNotificationTodo(limitTime : Long) : LiveData<List<Todo>>
}