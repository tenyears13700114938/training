package com.example.myapplication.basic_03_android_test.todoRepository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import com.example.myapplication.basic_03_android_test.model.Todo

class FakeTodoRepository : ITodoRepository {
    override val alltodos: LiveData<List<Todo>> = MutableLiveData<List<Todo>>()
    var todoList = mutableListOf<Todo>()

    init {
        (alltodos as MutableLiveData).value = todoList
    }

    override fun addToDo(todo: Todo) {
        todoList.add(todo)
        (alltodos as MutableLiveData).value = todoList
    }

    override fun updateToDo(todo: Todo) {
        todoList.indexOfFirst { it.id == todo.id }.takeIf { it != -1 }?.let {
            todoList[it] = todo
        }
        (alltodos as MutableLiveData).value = todoList
    }

    override fun deleteToDo(todo: Todo) {
        todoList.remove(todo)
        (alltodos as MutableLiveData).value = todoList
    }

    override fun getNotificationTodo(limitTime: Long): List<Todo> {
        val result = mutableListOf<Todo>()
        todoList.asSequence().filter { (it.targetTime ?: 0) <= limitTime }.toCollection(result)
        return result
    }

    override fun getLiveNotificationTodo(limitTime: Long): LiveData<List<Todo>> {
        return MutableLiveData<List<Todo>>().also {
            it.postValue(getNotificationTodo(limitTime))
        }
    }
}