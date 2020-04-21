package com.example.myapplication.basic_03_android_test.Flux

import com.example.myapplication.basic_03_android_test.TodoService.OpResult
import com.example.myapplication.basic_03_android_test.model.Todo
import com.example.myapplication.basic_03_android_test.todoList.ListDisplayType
import com.example.myapplication.basic_03_android_test.todoList.StartType

sealed class TodoAction : Action
class TodoListLoaded(val todoList : List<Todo>) : TodoAction()
class TodoAdded(val todo : Todo, val result : OpResult) : TodoAction()
class TodoUpdated(val todo : Todo, val result : OpResult) : TodoAction()
class TodoDeleted(val todo : Todo, val result : OpResult) : TodoAction()
class TodoListDisplayType(val displayType: ListDisplayType) : Action
class TodoEdited(val todo : Todo) : Action
class StartTypeSelected(val startType : StartType) : Action