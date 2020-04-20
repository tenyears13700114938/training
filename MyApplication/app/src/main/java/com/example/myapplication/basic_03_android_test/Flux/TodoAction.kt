package com.example.myapplication.basic_03_android_test.Flux

import androidx.lifecycle.LiveData
import com.example.myapplication.basic_03_android_test.TodoService.OpResult
import com.example.myapplication.basic_03_android_test.model.Todo

sealed class TodoAction : Action
class LoadTodoListAction(val todoList : List<Todo>) : TodoAction()
class AddTodoAction(val todo : Todo, val result : OpResult) : TodoAction()
class UpdateTodoAction(val todo : Todo, val result : OpResult) : TodoAction()
class DeleteTodoAction(val todo : Todo, val result : OpResult) : TodoAction()
class EditingTodoAction(val todo : Todo) : TodoAction()