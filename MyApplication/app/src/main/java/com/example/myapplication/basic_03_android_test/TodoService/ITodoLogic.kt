package com.example.myapplication.basic_03_android_test.TodoService

import com.example.myapplication.basic_03_android_test.Flux.TodoAdded
import com.example.myapplication.basic_03_android_test.Flux.TodoDeleted
import com.example.myapplication.basic_03_android_test.Flux.TodoUpdated
import com.example.myapplication.basic_03_android_test.model.Todo

interface ITodoLogic {
    fun isTodoEditing(todo: Todo): Boolean
    fun isTodoAdding(todo: Todo): Boolean
    fun popEditMap(todo: Todo)

    suspend fun updateTodo(todo: Todo): TodoUpdated

    suspend fun deleteTodo(todo: Todo): TodoDeleted

    open suspend fun addTodo(todo: Todo): TodoAdded
    fun removeTodo(todo: Todo)
}