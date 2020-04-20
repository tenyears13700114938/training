package com.example.myapplication.basic_03_android_test.Flux

import com.example.myapplication.basic_03_android_test.TodoService.TodoLogic
import com.example.myapplication.basic_03_android_test.model.Todo
import com.example.myapplication.basic_03_android_test.todoRepository.ITodoRepository
import com.example.myapplication.basic_03_android_test.todoRepository.todoRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TodoActionCreator @Inject constructor(
    val todoLogic: TodoLogic,
    val todoRepository: todoRepository //todo change to IF
) {
    fun addTodo(todo: Todo) = ActionCreate {
        AddTodoAction(todo, todoLogic.addTodo(todo))
    }

    fun deleteTodo(todo: Todo) = ActionCreate {
        DeleteTodoAction(todo, todoLogic.deleteTodo(todo))
    }

    fun updateTodo(todo: Todo) = ActionCreate {
        UpdateTodoAction(todo, todoLogic.updateTodo(todo))
    }

    val LoadTodoList = ActionCreate {
        LoadTodoListAction(todoRepository.alltodos)
    }

    fun editingTodo(todo: Todo) = ActionCreate {
        EditingTodoAction(todo)
    }
}