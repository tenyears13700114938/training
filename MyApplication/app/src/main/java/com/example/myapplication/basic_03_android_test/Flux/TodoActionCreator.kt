package com.example.myapplication.basic_03_android_test.Flux

import com.example.myapplication.basic_03_android_test.TodoService.TodoLogic
import com.example.myapplication.basic_03_android_test.model.Todo
import com.example.myapplication.basic_03_android_test.todoList.ListDisplayType
import com.example.myapplication.basic_03_android_test.todoList.StartType
import com.example.myapplication.basic_03_android_test.todoRepository.todoRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TodoActionCreator @Inject constructor(
    val todoLogic: TodoLogic,
    val todoRepository: todoRepository //todo change to IF
) {
    fun addTodo(todo: Todo) = ActionCreate {
        todoLogic.addTodo(todo)
    }

    fun deleteTodo(todo: Todo) = ActionCreate {
        todoLogic.deleteTodo(todo)
    }

    fun updateTodo(todo: Todo) = ActionCreate {
        todoLogic.updateTodo(todo)
    }

    fun todoListDisplayType(diplayType: ListDisplayType) = ActionCreate {
        TodoListDisplayType(diplayType)
    }

    fun editedTodo(todo: Todo) = ActionCreate {
        TodoEdited(todo)
    }

    fun setStartType(startType: StartType) = ActionCreate {
        StartTypeSelected(startType)
    }

    val loadedTodoList = ActionCreate {
        TodoListLoaded(todoRepository.alltodos)
    }
}