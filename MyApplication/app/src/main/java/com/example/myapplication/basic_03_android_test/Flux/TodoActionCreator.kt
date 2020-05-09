package com.example.myapplication.basic_03_android_test.Flux

import com.example.myapplication.basic_03_android_test.TodoService.ITodoLogic
import com.example.myapplication.basic_03_android_test.TodoService.TodoLogic
import com.example.myapplication.basic_03_android_test.model.Todo
import com.example.myapplication.basic_03_android_test.todoList.ListDisplayType
import com.example.myapplication.basic_03_android_test.todoList.StartType
import com.example.myapplication.basic_03_android_test.todoRepository.ITodoRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TodoActionCreator @Inject constructor(
    val todoLogic: ITodoLogic,
    val todoRepository: ITodoRepository
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

    fun detailLoaded(todo: Todo) = ActionCreate {
        DetailedLoaded(todo)
    }

    val loadedTodoList = ActionCreate {
        TodoListLoaded(todoRepository.alltodos)
    }
}