package com.example.myapplication.basic_03_android_test.todoRepository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.myapplication.basic_03_android_test.model.Todo

open class FakeTodoRepository(override val alltodos: MutableList<Todo>) : ITodoRepository {
    override val alltodosLiveData: LiveData<List<Todo>> = MutableLiveData<List<Todo>>()
    init {
        (alltodosLiveData as MutableLiveData).value = this.alltodos
    }

    override fun addToDo(todo: Todo) {
        this.alltodos.add(todo)
        (alltodosLiveData as MutableLiveData).value = this.alltodos
    }

    override fun updateToDo(todo: Todo) {
        this.alltodos.indexOfFirst { it.id == todo.id }.takeIf { it != -1 }?.let {
            this.alltodos[it] = todo
        }
        (alltodosLiveData as MutableLiveData).value = this.alltodos
    }

    override fun deleteToDo(todo: Todo) {
        this.alltodos.remove(todo)
        (alltodosLiveData as MutableLiveData).value = this.alltodos
    }

    override fun getNotificationTodo(limitTime: Long): List<Todo> {
        val result = mutableListOf<Todo>()
        this.alltodos.asSequence().filter { (it.targetTime ?: 0) <= limitTime }.toCollection(result)
        return result
    }

    override fun getLiveNotificationTodo(limitTime: Long): LiveData<List<Todo>> {
        return MutableLiveData<List<Todo>>().also {
            it.postValue(getNotificationTodo(limitTime))
        }
    }
}