package com.example.myapplication.basic_03_android_test.todoRepository

import androidx.arch.core.util.Function
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import com.example.myapplication.basic_03_android_test.model.Todo

open class FaketodoDao : todoDao {
    override fun getAllLiveData(): LiveData<List<Todo>> {
        return MutableLiveData()
    }

    override fun getAllTodos(): List<Todo> {
        return listOf()
    }

    override fun deleteAll() {
    }

    override fun insertTodo(todo: Todo) {
    }

    override fun updateTodo(todo: Todo) {
     }

    override fun delete(todo: Todo) {
     }

    override fun searchByTitleOrDescription(key: String): DataSource.Factory<Int, Todo>? {
        return null
    }

    override fun getNotificationTodo(millisecs: Long): List<Todo> {
        return listOf()
    }

    override fun getLiveNotificationTodo(millisecs: Long): LiveData<List<Todo>> {
        return MutableLiveData()
    }
}